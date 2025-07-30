package transform;

/**
* Main entry-point for the impressions → TFRecord batch transformation.
*
* <p>Expected arguments, in order:
* <ol>
*   <li><b>inputPath</b>  – absolute path to the root of the raw JSONL tree</li>
*   <li><b>outputPath</b> – absolute path where TFRecords will be written</li>
*   <li><b>startDate</b>  – inclusive start date, format {@code YYYY-MM-DD}</li>
*   <li><b>endDate</b>    – inclusive end date,   format {@code YYYY-MM-DD}</li>
* </ol>
* */
public class Main {


   public static void main(String[] args) {
       if (args.length != 4) {
           System.err.printf(
               "Expected 4 arguments but got %d%n%n" +
               "Usage: java -jar transformations.jar <inputPath> <outputPath> <startDate> <endDate>%n",
               args.length
           );
           System.exit(1);
           return;
       }


       String inputPath  = args[0];
       String outputPath = args[1];
       String startDate  = args[2];
       String endDate    = args[3];


       try {
           ImpressionsToTFRecord.run(inputPath, outputPath, startDate, endDate);
       } catch (Exception e) {
           e.printStackTrace(System.err);
           System.exit(2);
       }
   }
}
