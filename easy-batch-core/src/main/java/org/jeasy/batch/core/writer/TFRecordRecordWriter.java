/*
 * The MIT License
 *
 *  Copyright (c) 2021, Mahmoud Ben Hassine
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package org.jeasy.batch.core.writer;

import org.jeasy.batch.core.record.Batch;
import org.jeasy.batch.core.record.Record;
import org.tensorflow.example.TFRecordWriter;

import java.nio.file.Path;

/**
 * A writer that writes byte array records into TFRecord files.
 */
public class TFRecordRecordWriter implements RecordWriter<byte[]> {

    private final Path path;
    private TFRecordWriter writer;

    /**
     * Create a new {@link TFRecordRecordWriter}.
     *
     * @param path output file path
     */
    public TFRecordRecordWriter(Path path) {
        this.path = path;
    }

    @Override
    public void open() throws Exception {
        writer = new TFRecordWriter(path.toString());
    }

    @Override
    public void writeRecords(Batch<byte[]> batch) throws Exception {
        for (Record<byte[]> record : batch) {
            writer.write(record.getPayload());
        }
    }

    @Override
    public void close() throws Exception {
        if (writer != null) {
            writer.close();
        }
    }
}
