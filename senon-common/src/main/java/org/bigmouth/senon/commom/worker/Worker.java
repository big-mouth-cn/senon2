package org.bigmouth.senon.commom.worker;

import org.bigmouth.senon.commom.registry.Service;

public class Worker extends Service {

    public static final String URI_JOB_RECEIVER = "/job_receiver";

    public String getUrl(String uri) {
        return new StringBuilder().append(getHostNameForHttpProtocol()).append("/").append(uri).toString();
    }
}
