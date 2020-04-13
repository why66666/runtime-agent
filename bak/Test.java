package com.weaver.check.agent.runtime;

public class Test {
    public void test(){
        long time = MethodAop.methodBefore();
    }
    public void test1(long time){
        MethodAop.methodAfter("1","2",time);
    }
}
