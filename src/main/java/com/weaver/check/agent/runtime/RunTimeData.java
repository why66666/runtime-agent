package com.weaver.check.agent.runtime;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author w
 * @date 2020-03-28 21:19
 */
public class RunTimeData {
    static BlockingQueue<RunTimeData> queue = new LinkedBlockingDeque<RunTimeData>(1600); // 缓冲队列
    public static String serverPost = "";
    private long reqtime;
    private long starttime;
    private long runtime;
    private String url;
    private String spanname;
    private long threadid;

    public RunTimeData(long reqtime, long starttime, long runtime, String url, String spanname, long threadid) {
        this.reqtime = reqtime;
        this.starttime = starttime;
        this.runtime = runtime;
        this.url = url;
        this.spanname = spanname;
        this.threadid = threadid;
    }

    public long getReqtime() {
        return reqtime;
    }

    public void setReqtime(long reqtime) {
        this.reqtime = reqtime;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getRuntime() {
        return runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSpanname() {
        return spanname;
    }

    public void setSpanname(String spanname) {
        this.spanname = spanname;
    }

    public long getThreadid() {
        return threadid;
    }

    public void setThreadid(long threadid) {
        this.threadid = threadid;
    }

    @Override
    public String toString() {
        return "RunTimeData{" +
                "reqtime=" + reqtime +
                ", starttime=" + starttime +
                ", runtime=" + runtime +
                ", url='" + url + '\'' +
                ", spanname='" + spanname + '\'' +
                ", threadid=" + threadid +
                '}';
    }
}
