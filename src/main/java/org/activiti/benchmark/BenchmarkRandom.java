package org.activiti.benchmark;

import org.activiti.benchmark.execution.BasicBenchmarkExecution;
import org.activiti.benchmark.execution.BenchmarkExecution;
import org.activiti.benchmark.execution.FixedThreadPoolBenchmarkExecution;
import org.activiti.benchmark.execution.ProcessEngineHolder;
import org.activiti.benchmark.output.BenchmarkOuput;
import org.activiti.benchmark.output.BenchmarkResult;
import org.activiti.benchmark.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Main class that contains the logic to execute the benchmark.
 * 
 * @author jbarrez
 * @author Yvo Swillens
 */
public class BenchmarkRandom extends Benchmark {

	protected static void executeBenchmarks(int nrOfProcessExecutions, int maxNrOfThreadsInThreadPool, String benchmarkName) {

		// Deploy test processes
		Utils.cleanAndRedeployTestProcesses();

		// Single thread benchmark
		System.out.println(new Date() + " - benchmarking with one thread.");
		BenchmarkExecution singleThreadBenchmark = new BasicBenchmarkExecution(PROCESSES);
        fixedPoolRandomResults.add(singleThreadBenchmark.randomExecution(PROCESSES, nrOfProcessExecutions, HISTORY_ENABLED));

		// Multiple threads - fixed pool benchmark
		for (int nrOfWorkerThreads = 2; nrOfWorkerThreads <= maxNrOfThreadsInThreadPool; nrOfWorkerThreads++) {

			System.out.println(new Date() + " - benchmarking with fixed threadpool of " + nrOfWorkerThreads + " threads.");
			BenchmarkExecution fixedPoolBenchMark = new FixedThreadPoolBenchmarkExecution(nrOfWorkerThreads, PROCESSES);
            fixedPoolRandomResults.add(fixedPoolBenchMark.randomExecution(PROCESSES, nrOfProcessExecutions, HISTORY_ENABLED));
        }

		writeHtmlReport(benchmarkName);
	}

	protected static void writeHtmlReport(String benchmarkName) {
		BenchmarkOuput output = new BenchmarkOuput(benchmarkName);
		output.start("Activiti " + ProcessEngineHolder.getInstance().VERSION + " basic benchmark results");

		for (int i = 1; i <= maxNrOfThreadsInThreadPool; i++) {
			output.addBenchmarkResult("Fixed thread pool (" + i + "threads), randomized", fixedPoolRandomResults.get(i - 1));
		}
		output.generateChartOfPreviousAddedBenchmarkResults(true);

		output.writeOut();
	}

}
