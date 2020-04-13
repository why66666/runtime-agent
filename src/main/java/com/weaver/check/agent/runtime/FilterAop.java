package com.weaver.check.agent.runtime;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author w
 * @date 2020-03-28 21:19
 * SecurityFilter.doFilter 注入
 */
public class FilterAop {

    public static long methodBefore(Object arg) {
        if(!RuntimeCheck.isStart()){
            return -1;
        }
        long starttime = System.currentTimeMillis();
        String url = ThreadLocalData.url.get();
        if (url.isEmpty()) {
            HttpServletRequest request = (HttpServletRequest) arg;
            String u = request.getRequestURI();
            //通过url过滤
            if (!u.endsWith(".js") && !u.endsWith(".css") && !u.endsWith(".jpg") && !u.endsWith(".png") && !u.endsWith(".ttf") && !u.endsWith(".woff") && !u.endsWith(".html") && !u.endsWith(".map")) {
                System.out.println("url->>" + u + "  DoRuntimeAOP");
                ThreadLocalData.url.set(u);
                ThreadLocalData.doNotAOP.set(false);
                ThreadLocalData.starttime.set(starttime);
                ThreadLocalData.treadid.set(Thread.currentThread().getId());
            } else {
                ThreadLocalData.doNotAOP.set(true);
            }
        }
        return starttime;
    }

    public static void methodAfter(String className, String methodName, long startTime) {
        if(startTime==-1||!RuntimeCheck.isStart()){
            return;
        }
        startTime = startTime == 0 ? System.currentTimeMillis() : startTime;
        long runtime = System.currentTimeMillis() - startTime;
        List<RunTimeData> runtimes = ThreadLocalData.runtimes.get();
        String url = ThreadLocalData.url.get();
        if (runtime < 2000) {
            return;
        }
        RunTimeData runTimeData = new RunTimeData(ThreadLocalData.starttime.get(),startTime, runtime, url, className + "." + methodName,ThreadLocalData.treadid.get());
        ThreadLocalData.runtimes.get().add(runTimeData);
        //将所有数据存入缓存队列
        while (runtimes.size() > 0) {
            RunTimeData addData = runtimes.get(0);
            runtimes.remove(0);
            System.out.println("RunTimeData put ：" + addData + "into queue...");
            //如果队列已满,立即返回false
            if (!RunTimeData.queue.offer(addData)) {
                System.out.println("queue full, put failed");
            }
        }
        //线程数据初始化
        ThreadLocalData.initialAll();

    }
}