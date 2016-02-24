package org.activiti.benchmark.output;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BenchmarkCSVOutput {

    private String baseFolderName;
    private String benchmarkName;
    private String engineVersion;
    private Map<Integer, BenchmarkResult> results = new HashMap<>();
    private static DateFormat df = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final Object [] FILE_HEADER = {"processName","noThreads","totalTime","average", "throughputSec", "throughputHour","noExecutions","engineVersion"};


    public BenchmarkCSVOutput() {
        this("activiti", df.format(new Date()));
    }

    public BenchmarkCSVOutput(String benchmarkName, String startDateTime) {
        this.benchmarkName = benchmarkName;
        this.baseFolderName = "../benchmark_reports/" + startDateTime;

        File folder = new File(baseFolderName);
        folder.mkdirs();
    }

    public void start(String engineVersion) {
        this.engineVersion = engineVersion;
    }

    public void generateChartOfPreviousAddedBenchmarkResults(boolean randomizedBenchmarkResults) {
    }

    public void addBenchmarkResult(int numberOfThreads, BenchmarkResult benchmarkResult) {
        results.put(numberOfThreads, benchmarkResult);
    }

    public void writeOut() {

        FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;

        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);

        try {

            //initialize FileWriter object
            String outputFile = baseFolderName + "/" + benchmarkName + ".csv";
            fileWriter = new FileWriter(outputFile);

            //initialize CSVPrinter object
            csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);

            //Create CSV file header
            csvFilePrinter.printRecord(FILE_HEADER);

            for (Map.Entry<Integer, BenchmarkResult> entry : results.entrySet()) {

                Integer noThreads = entry.getKey();
                BenchmarkResult benchmarkResult = entry.getValue();

                for (String processName : benchmarkResult.getProcesses()) {
                    List resultRecord = new ArrayList();

                    resultRecord.add(processName);
                    resultRecord.add(noThreads);
                    resultRecord.add(benchmarkResult.getTotalTime(processName));
                    resultRecord.add(benchmarkResult.getAverage(processName));
                    resultRecord.add(benchmarkResult.getThroughputPerSecond(processName));
                    resultRecord.add(benchmarkResult.getThroughputPerHour(processName));
                    resultRecord.add(benchmarkResult.getNrOfExecutions(processName));
                    resultRecord.add(engineVersion);

                    csvFilePrinter.printRecord(resultRecord);
                }
            }

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
                csvFilePrinter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
                e.printStackTrace();
            }
        }

        // Empty the previous results
        results.clear();
    }

}
