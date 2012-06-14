package be.jorambarrez.activiti.benchmark.execution;

import be.jorambarrez.activiti.benchmark.output.BenchmarkResult;
import be.jorambarrez.activiti.benchmark.util.Utils;
import org.activiti.engine.ProcessEngine;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class ThreadPooledBenchmarkExecution extends BasicBenchmarkExecution {

    protected int nrOfWorkerThreads;

    public ThreadPooledBenchmarkExecution(ProcessEngine processEngine, String[] processes) {
        super(processEngine, processes);
    }

    protected abstract ExecutorService getExecutorService();

    @Override
    public BenchmarkResult sequentialExecution(String[] processes, int nrOfProcessExecutions, boolean history) {

        countProcessesBeforeBenchmark();
        BenchmarkResult result = new BenchmarkResult(nrOfWorkerThreads);
        ExecutionTime totalTime;
        
        for (String process : processes) {
            final String currentProcess = process;
            totalTime = new ExecutionTime();
            
            System.out.println(new Date() + " : [SEQ]Starting " + nrOfProcessExecutions + " of process " + currentProcess);
            ExecutorService executorService = getExecutorService();

            for (int i = 0; i < nrOfProcessExecutions; i++) {
                executorService.execute(new ExecuteProcessRunnable(process, processEngine, totalTime));
            }

            try {
                executorService.shutdown();
                executorService.awaitTermination(1, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            result.addProcessMeasurement(process, nrOfProcessExecutions, totalTime.getExecutionTime());
        }

        if (history) {
            countProcessesAfterBenchmark();
            verifyCounts(nrOfProcessExecutions * processes.length);
        }

        cleanAndDeploy();
        return result;
    }

    @Override
    public BenchmarkResult randomExecution(String[] processes, int totalNrOfExecutions, boolean history) {
        countProcessesBeforeBenchmark();
        BenchmarkResult result = new BenchmarkResult(nrOfWorkerThreads);
        final String[] randomizedProcesses = Utils.randomArray(processes, totalNrOfExecutions);

        ExecutorService executorService = getExecutorService();
        System.out.println(new Date() + ": [RND]Starting " + totalNrOfExecutions + " random processes");

        long start = System.currentTimeMillis();
        for (int i = 0; i < randomizedProcesses.length; i++) {

            final String randomProcess = randomizedProcesses[i];
            executorService.execute(new Runnable() {

                public void run() {
                    runtimeService.startProcessInstanceByKey(randomProcess);
                }
            });

        }

        try {
            executorService.shutdown();
            executorService.awaitTermination(2, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        result.addProcessMeasurement(Utils.toString(processes), totalNrOfExecutions, end - start);

        if (history) {
            countProcessesAfterBenchmark();
            verifyCounts(totalNrOfExecutions);
        }

        cleanAndDeploy();
        return result;
    }

}
