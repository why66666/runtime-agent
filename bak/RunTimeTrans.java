package com.weaver.check.agent.runtime;


import com.weaver.check.agent.runtime.asm.ClassReader;
import com.weaver.check.agent.runtime.asm.ClassWriter;
import com.weaver.check.agent.runtime.asm.Opcodes;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class RunTimeTrans implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if(className.contains("weaver") && !className.contains("dscanAgent")) {
            try {
                InputStream sbs = new ByteArrayInputStream(classfileBuffer);
                ClassReader reader = new ClassReader(sbs);
                // 构建一个ClassWriter对象，并设置让系统自动计算栈和本地变量大小
                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                System.out.println("redefine class::"+className);
                //加入自己的适配器
                WeaverClassAdapter runtimeClassAdapter = new WeaverClassAdapter(Opcodes.ASM5,classWriter);
                System.out.println("************************"+System.getProperty("user.dir")
                        + className.replaceAll("/","_")+".class");
                reader.accept(runtimeClassAdapter, ClassReader.EXPAND_FRAMES);
                byte[] code = classWriter.toByteArray();
                FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir")
                        + className.replaceAll("/","_")+".class");
                fos.write(code);
                fos.close();
                return code;
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
