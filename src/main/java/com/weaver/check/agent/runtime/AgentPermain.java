package com.weaver.check.agent.runtime;


import com.weaver.check.agent.runtime.asm.ClassReader;
import com.weaver.check.agent.runtime.asm.ClassWriter;
import com.weaver.check.agent.runtime.asm.Opcodes;
import com.weaver.check.agent.runtime.utils.PropUtil;

import java.io.*;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author w
 * @date 2020-03-28 21:19
 */
public class AgentPermain {
    //消费者线程池
    public static ExecutorService executor = Executors.newFixedThreadPool(5);
    public static void premain(String agentArgs, Instrumentation inst) throws IOException, ClassNotFoundException {
        RuntimeCheck.setFromProp(PropUtil.initProp());
        inst.addTransformer(new RunTimeTrans());
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        //创建消费者线程包存数据
        for (int i = 0; i < 5; i++) {
            executor.submit(new RunTimeConsumer(RunTimeData.queue));
        }

        System.out.println("agentmain!!!");
        Class<?>[] allClass = inst.getAllLoadedClasses();
        for (Class claz : allClass) {
            String className = claz.getSimpleName();
            if ((claz.getName().contains("weaver") || claz.getName().contains("_jsp")) && !claz.getName().contains("com.weaver.check.agent.runtime") && !claz.getName().contains("weaver.permainfile") && !claz.getName().contains("weaver.security") && !claz.getName().contains("weaver.conn.aop") && !claz.getName().contains("weaver.aop")) {
                System.out.println("runtime-agent:redefine class====>" + claz.getName());
                ClassReader reader = null;
                try {
                    InputStream sbs = claz.getResourceAsStream(className + ".class");
                    reader = new ClassReader(sbs);
                } catch (Exception e) {
                    continue;
                }
                // 构建一个ClassWriter对象，并设置让系统自动计算栈和本地变量大小
                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                //加入自己的适配器
                ClassAdapter runtimeClassAdapter = new ClassAdapter(Opcodes.ASM5, classWriter);
                reader.accept(runtimeClassAdapter, ClassReader.EXPAND_FRAMES);
                byte[] code = classWriter.toByteArray();

                /*String filepath = "D:\\Weaver\\monitor\\1\\" + className + ".class";
                File file = new File(filepath);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(code, 0, code.length);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                try {
                    inst.redefineClasses(new ClassDefinition(claz, code));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (UnmodifiableClassException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
