package com.weaver.check.agent.runtime;


import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author w
 * @date 2020-03-28 21:19
 */
public class RunTimeAop {
    static SimpleDateFormat tf = new SimpleDateFormat("yyyyMMdd HHmmss");
    static SimpleDateFormat lf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static ExecutorService executor = Executors.newFixedThreadPool(5);
    public static long methodBefore(){
        if(ThreadLocalData.doNotAOP.get()||!RuntimeCheck.isStart()){
            return -1;
        }
        return System.currentTimeMillis();
    }

    public static void methodAfter(String className, String methodName,long startTime) {
        if(startTime==-1||ThreadLocalData.doNotAOP.get()||!RuntimeCheck.isStart()){
            return;
        }
        long runtime = System.currentTimeMillis()-startTime;
        if(runtime>10){
            RunTimeData runTimeData = new RunTimeData(ThreadLocalData.starttime.get(),startTime,runtime,ThreadLocalData.url.get(),className+"."+methodName,ThreadLocalData.treadid.get());
            ThreadLocalData.runtimes.get().add(runTimeData);
        }
    }
}
