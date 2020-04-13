package com.weaver.check.agent.runtime;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * @author w
 * @date 2020-03-28 21:19
 */
public class RuntimeCheck {
    private static Boolean runtimecheck = true;
    private static Boolean runtimecheckWritelog = true;
    private static Boolean runtimecheckonline = false;
    private static String serverpost = "";
    public static void startRumtime(){
        if(AgentPermain.executor.isShutdown()){
            AgentPermain.executor = Executors.newFixedThreadPool(5);
            //创建消费者线程包存数据
            for (int i = 0; i < 5; i++) {
                AgentPermain.executor.submit(new RunTimeConsumer(RunTimeData.queue));
            }
        }
        runtimecheck = true;
        startWriteLog();
    }
    public static Boolean isStart(){
        return runtimecheck;
    }
    public static void stopRumtime(){
        try {
            AgentPermain.executor.shutdownNow();
        } catch (Exception e) {
            System.out.println("-------Runtime Tread close------");
        }
        runtimecheck = false;
    }

    public static void startWriteLog(){
        runtimecheckWritelog = true;
    }
    public static Boolean isStartWriteLog(){
        return runtimecheckWritelog;
    }
    public static void stopWriteLog(){
        runtimecheckWritelog = false;
    }
    public static void StartOnline(){
        runtimecheckonline = true;
    }
    public static Boolean isStartOnline(){
        return runtimecheckonline;
    }
    public static void stopOnline(){
        runtimecheckonline = false;
    }

    public static String getServerpost() {
        return serverpost;
    }

    public static void setFromProp(Map<String, Object> prop) {
        runtimecheck = Boolean.parseBoolean(prop.get("runtime.check").toString());
        runtimecheckWritelog = Boolean.parseBoolean(prop.get("runtime.writelog").toString());
        runtimecheckonline = Boolean.parseBoolean(prop.get("runtime.online").toString());
        serverpost = prop.get("runtime.online.post").toString();
    }
}

