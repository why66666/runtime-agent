package com.weaver.check.agent.runtime;

import com.weaver.check.agent.runtime.utils.HttpUtil;
import com.weaver.check.agent.runtime.utils.JsonUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author w
 * @date 2020-03-28 21:19
 */
public class RunTimeConsumer implements Runnable {
    private BlockingQueue<RunTimeData> queue;// 缓冲队列

    public RunTimeConsumer(BlockingQueue<RunTimeData> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if(RuntimeCheck.isStartWriteLog()){
                    RunTimeData data = null;
                    data = queue.poll(2, TimeUnit.SECONDS);
                    if (null != data) {
                        String path = System.getProperty("user.dir") + "/../ecology/log/runtime_log/" + data.getUrl().replaceAll("/", "_") + "/";
                        String filename = data.getThreadid()+"_"+RunTimeAop.tf.format(data.getReqtime()) + ".log";
                        //创建文件
                        File file = new File(path + filename);
                        if (!file.exists()) {
                            File file1 = new File(path);
                            if (!file1.exists()) {
                                file1.mkdirs();
                            }
                            try {
                                file.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //写日志
                        FileWriter writer = null;
                        try {
                            writer= new FileWriter(path + filename, true);
                            writer.write(RunTimeAop.lf.format(data.getStarttime())+"\t"+data.getRuntime()+"ms\t"+data.getSpanname()+"\r\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            try {
                                if (writer != null) {
                                    writer.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if(RuntimeCheck.isStartOnline()){
                    RunTimeData data = null;
                    data = queue.poll(2, TimeUnit.SECONDS);
                    RunTimeData finalData = data;
                    new Thread(){
                        @Override
                        public void run() {
                            HttpUtil.sendPostOnJson(RuntimeCheck.getServerpost(),JsonUtil.toJson(finalData));
                        }
                    }.start();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } finally {
            //System.out.println("exiting RunTimeConsumer...");
        }
    }
}
