package transform;

import org.jeasy.batch.core.job.Job;
import org.jeasy.batch.core.job.JobBuilder;
import org.jeasy.batch.core.job.JobExecutor;
import org.jeasy.batch.flatfile.FlatFileRecordReader;
import org.jeasy.batch.extensions.jackson.JacksonRecordMapper;
import org.jeasy.batch.core.writer.TFRecordRecordWriter;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

/** Utility to transform impressions jsonl files to tfrecord format. */
public class ImpressionsToTFRecord {

    public static void run(String inputRoot, String outputRoot, String start, String end) throws Exception {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Path inDir = Paths.get(inputRoot, "dt=" + date.toString());
            if (!Files.isDirectory(inDir)) {
                continue;
            }
            Path outDir = Paths.get(outputRoot, "dt=" + date.toString());
            Files.createDirectories(outDir);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(inDir, "*.jsonl")) {
                for (Path file : stream) {
                    Path out = outDir.resolve(file.getFileName().toString().replace(".jsonl", ".tfrecord"));
                    runJob(file, out);
                }
            }
        }
    }

    private static void runJob(Path inputFile, Path outputFile) throws Exception {
        Job job = new JobBuilder<ImpressionRow, byte[]>()
                .reader(new FlatFileRecordReader(inputFile))
                .mapper(new JacksonRecordMapper<>(ImpressionRow.class))
                .processor(new ImpressionProcessor())
                .writer(new TFRecordRecordWriter(outputFile))
                .build();

        JobExecutor executor = new JobExecutor();
        executor.execute(job);
        executor.shutdown();
    }
}
