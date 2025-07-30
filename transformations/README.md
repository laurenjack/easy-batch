# Transformations

This module provides a batch job to convert impression JSONL files into TFRecord files.

```
java -jar transformations.jar <inputPath> <outputPath> <startDate> <endDate>
```

Dates must be in `YYYY-MM-DD` format. Output files mirror the partition layout of the input tree with `.tfrecord` suffix.
