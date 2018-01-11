package org.bigmouth.senon.worker;

import org.bigmouth.senon.commom.job.JobInfoRequest;
import org.bigmouth.senon.commom.registry.SchedulerRegistry;
import org.bigmouth.senon.commom.scheduler.Scheduler;
import org.bigmouth.senon.commom.selector.Selector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class WorkerManager {

    private final ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

    private static final String workingFolder = System.getProperty("java.io.tmpdir") + "/senon_working";

    @Autowired
    private SchedulerRegistry schedulerRegistry;
    @Autowired
    private Selector selector;

    public void addJob(JobInfoRequest job) {
        List<Scheduler> services = schedulerRegistry.getServices();
        Scheduler select = selector.select(services);
        if (null == select) {
            throw new IllegalStateException("Could found any scheduler service!");
        }
        String responseUrl = select.getUrl(Scheduler.URI_JOB_RESPONSE);
        String sendlogUrl = select.getUrl(Scheduler.URI_SEND_LOG);

        exec.submit(new WorkingThread(workingFolder, responseUrl, sendlogUrl, job));
    }

    public String getRunningJobLog(Long historyId) {
        File logFile = new File(workingFolder + File.separator + historyId.toString() + File.separator + "job.log");
        if (!logFile.exists()) {
            return "Log not found!!";
        }
        InputStream in = null;
        try {
            in = new FileInputStream(logFile);
            byte[] data = new byte[in.available()];
            in.read(data);
            in.close();
            return new String(data);
        } catch (Exception e) {
            return "Reading log error!!";
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                return "Reading log error!!";
            }
        }
    }
}
