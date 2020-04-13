package com.weaver.check.agent.runtime;

/**
 * @author w
 * @date 2020-03-28 21:19
 */
public class RecordSetSqlAop {
    public static long methodBefore(){
        if(ThreadLocalData.doNotAOP.get()||!RuntimeCheck.isStart()){
            return -1;
        }
        return System.currentTimeMillis();
    }

    public static void methodAfter(String className, String methodName, long startTime,Object[] args) {
        if(startTime==-1||!RuntimeCheck.isStart()||ThreadLocalData.doNotAOP.get()){
            return;
        }
        String sql = String.valueOf(args[0]);
        long runtime = System.currentTimeMillis()-startTime;
        if(runtime>10){
            if(sql!=null&&!sql.isEmpty()){
                RunTimeData runTimeData = new RunTimeData(ThreadLocalData.starttime.get(),startTime,runtime,ThreadLocalData.url.get(),sql,ThreadLocalData.treadid.get());
                ThreadLocalData.runtimes.get().add(runTimeData);
            }else{
                RunTimeData runTimeData = new RunTimeData(ThreadLocalData.starttime.get(),startTime,runtime,ThreadLocalData.url.get(),className+"."+methodName,ThreadLocalData.treadid.get());
                ThreadLocalData.runtimes.get().add(runTimeData);
            }
        }
    }
}
