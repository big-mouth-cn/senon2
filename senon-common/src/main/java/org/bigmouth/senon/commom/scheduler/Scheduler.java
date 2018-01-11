package org.bigmouth.senon.commom.scheduler;

import org.bigmouth.senon.commom.registry.Service;

public class Scheduler extends Service {

    public static final String URI_TRIGGER_JOB = "/trigger_job";
    public static final String URI_MANUAL_RUN_JOB = "/manual_run_job";
    public static final String URI_RESUME_RUN_JOB = "/resume_run_job";
    public static final String URI_GET_LOG = "/get_log";
    public static final String URI_JOB_RESPONSE = "/job_response";
    public static final String URI_SEND_LOG = "/send_log";

    public String getUrl(String uri) {
        return new StringBuilder().append("http://").append(getHost()).append(":").append(getPort()).append("/").append(uri).toString();
    }
}
