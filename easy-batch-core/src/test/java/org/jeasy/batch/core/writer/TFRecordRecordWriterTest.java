package org.jeasy.batch.core.writer;

import org.jeasy.batch.core.record.Batch;
import org.jeasy.batch.core.record.Header;
import org.jeasy.batch.core.record.Record;
import org.jeasy.batch.core.record.GenericRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/** Test for {@link TFRecordRecordWriter}. */
@RunWith(MockitoJUnitRunner.class)
public class TFRecordRecordWriterTest {

    private final Path path = Paths.get("target/test.tfrecord");

    @Mock
    private Header header;

    private Record<byte[]> record1, record2;
    private TFRecordRecordWriter writer;

    @Before
    public void setUp() throws Exception {
        Files.deleteIfExists(path);
        record1 = new GenericRecord<>(header, "foo".getBytes());
        record2 = new GenericRecord<>(header, "bar".getBytes());
        writer = new TFRecordRecordWriter(path);
        writer.open();
    }

    @Test
    public void testWrite() throws Exception {
        writer.writeRecords(new Batch<>(record1, record2));
        writer.close();
        assertThat(Files.exists(path)).isTrue();
        assertThat(Files.size(path)).isGreaterThan(0);
    }

    @After
    public void tearDown() throws Exception {
        Files.deleteIfExists(path);
    }
}
