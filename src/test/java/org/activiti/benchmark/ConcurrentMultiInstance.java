/**
 * Copyright 2005-2015 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.activiti.benchmark;

import org.activiti.benchmark.execution.BenchmarkExecution;
import org.activiti.benchmark.execution.FixedThreadPoolBenchmarkExecution;
import org.activiti.benchmark.execution.ProcessEngineHolder;
import org.activiti.benchmark.output.BenchmarkResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Yvo Swillens
 */
public class ConcurrentMultiInstance {

    private static final String PROCESS_DEF_NAME = "process-multi-instance-01";

    @Before
    public void deployProcess() {

        ProcessEngineHolder.getInstance().getRepositoryService().createDeployment()
                .addClasspathResource(PROCESS_DEF_NAME + ".bpmn20.xml").deploy();
    }

    @Test
    public void testConcurrentMultiInstance_4() {

        int nrOfWorkerThreads = 4;
        int nrOfProcessExecutions = 1000;

        testConcurrentMultiInstance(nrOfWorkerThreads, nrOfProcessExecutions);
    }

    private void testConcurrentMultiInstance(int nrOfWorkerThreads, int nrOfProcessExecutions) {

        String[] PROCESSES = {PROCESS_DEF_NAME};
        boolean HISTORY_ENABLED = true;

        BenchmarkExecution fixedPoolBenchMark = new FixedThreadPoolBenchmarkExecution(nrOfWorkerThreads, PROCESSES);
        BenchmarkResult result = fixedPoolBenchMark.sequentialExecution(PROCESSES, nrOfProcessExecutions, HISTORY_ENABLED);

        System.out.println("Result for: "+PROCESS_DEF_NAME);
        System.out.println("Number of threads: "+result.getNrOfThreads());
        System.out.println("Number of executions: "+result.getNrOfExecutions(PROCESS_DEF_NAME));
        System.out.println("Average per process instance: "+result.getAverage(PROCESS_DEF_NAME));
        System.out.println("Throughput per hour: "+result.getThroughputPerHour(PROCESS_DEF_NAME));
        System.out.println("Total time: "+result.getTotalTime(PROCESS_DEF_NAME));

    }


    @After
    public void deleteProcess() {
        for (org.activiti.engine.repository.Deployment deployment : ProcessEngineHolder.getInstance().getRepositoryService().createDeploymentQuery().list()) {
            ProcessEngineHolder.getInstance().getRepositoryService().deleteDeployment(deployment.getId(), true);
        }
    }
}
