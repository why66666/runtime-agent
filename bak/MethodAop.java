package com.weaver.check.agent.runtime;

public class MethodAop {
    public static long methodBefore(){
        return System.currentTimeMillis();
    }
    public static void methodAfter(String classname,String methodname,long stime){
        long etime = System.currentTimeMillis();
        long runtime = etime-stime;
        if(runtime>10){
            System.out.println(System.getProperty("user.dir")+"+++++++++"+runtime);
        }
    }
}
