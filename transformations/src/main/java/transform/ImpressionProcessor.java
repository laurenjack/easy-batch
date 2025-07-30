package transform;

import org.jeasy.batch.core.processor.RecordProcessor;
import org.jeasy.batch.core.record.GenericRecord;
import org.jeasy.batch.core.record.Record;
import org.tensorflow.example.BytesList;
import org.tensorflow.example.Example;
import org.tensorflow.example.Feature;
import org.tensorflow.example.Features;
import org.tensorflow.example.Int64List;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Process {@link ImpressionRow} into serialized TF Example bytes. */
public class ImpressionProcessor implements RecordProcessor<ImpressionRow, byte[]> {

    @Override
    public Record<byte[]> processRecord(Record<ImpressionRow> record) {
        ImpressionRow row = record.getPayload();

        Map<String, Integer> brandCount = new HashMap<>();
        List<Action> allActions = new ArrayList<>();
        if (row.recent_clicks != null) {
            allActions.addAll(row.recent_clicks);
        }
        if (row.recent_add_carts != null) {
            allActions.addAll(row.recent_add_carts);
        }
        for (Action a : allActions) {
            String brand = a.item != null ? a.item.brand_code : null;
            if (brand != null) {
                brandCount.merge(brand, 1, Integer::sum);
            }
        }
        Collections.sort(allActions, Comparator.comparing(a -> Instant.parse(a.occurred_at)).reversed());
        List<Long> actionItemIds = allActions.stream()
                .map(a -> (long) a.item.item_id)
                .collect(Collectors.toList());

        List<Long> itemIds = new ArrayList<>();
        List<Long> orders = new ArrayList<>();
        List<Long> brandCounts = new ArrayList<>();
        for (Impression imp : row.impression) {
            itemIds.add((long) imp.item.item_id);
            orders.add(imp.is_order ? 1L : 0L);
            String brand = imp.item.brand_code;
            brandCounts.add((long) brandCount.getOrDefault(brand, 0));
        }

        Example example = Example.newBuilder()
                .setFeatures(Features.newBuilder()
                        .putFeature("ranking_id", stringFeature(row.ranking_id))
                        .putFeature("customer_id", int64Feature(row.customer_id))
                        .putFeature("item_id", int64ListFeature(itemIds))
                        .putFeature("is_order", int64ListFeature(orders))
                        .putFeature("brand_interaction_count", int64ListFeature(brandCounts))
                        .putFeature("actions", int64ListFeature(actionItemIds))
                        .build())
                .build();

        return new GenericRecord<>(record.getHeader(), example.toByteArray());
    }

    private static Feature int64Feature(long value) {
        return Feature.newBuilder()
                .setInt64List(Int64List.newBuilder().addValue(value).build())
                .build();
    }

    private static Feature int64ListFeature(List<Long> values) {
        Int64List.Builder builder = Int64List.newBuilder();
        for (Long v : values) {
            builder.addValue(v);
        }
        return Feature.newBuilder().setInt64List(builder.build()).build();
    }

    private static Feature stringFeature(String s) {
        return Feature.newBuilder()
                .setBytesList(BytesList.newBuilder().addValue(com.google.protobuf.ByteString.copyFromUtf8(s)).build())
                .build();
    }
}
