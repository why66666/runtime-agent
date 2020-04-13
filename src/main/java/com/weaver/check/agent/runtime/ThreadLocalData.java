package com.weaver.check.agent.runtime;

import java.util.ArrayList;
import java.util.List;

/**
 * @author w
 * @date 2020-03-28 21:19
 */
public class ThreadLocalData {

    static ThreadLocal<List<RunTimeData>> runtimes = new ThreadLocal<List<RunTimeData>>() {
        @Override
        protected List<RunTimeData> initialValue() {
            return new ArrayList<>();
        }
    };
    static ThreadLocal<String> url = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return "";
        }
    };
    static ThreadLocal<Boolean> doNotAOP = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };
    static ThreadLocal<Long> starttime = new ThreadLocal<Long>() {
        @Override
        protected Long initialValue() {
            return new Long(0);
        }
    };
    static ThreadLocal<Long> treadid = new ThreadLocal<Long>() {
        @Override
        protected Long initialValue() {
            return new Long(-1);
        }
    };

    public static void initialAll(){
        runtimes.set(new ArrayList<>());
        url.set("");
        doNotAOP.set(false);
        starttime.set(new Long(0));
    }
}
