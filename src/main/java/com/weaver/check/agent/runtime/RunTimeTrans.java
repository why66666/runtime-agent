package com.weaver.check.agent.runtime;


import com.weaver.check.agent.runtime.asm.ClassWriter;
import com.weaver.check.agent.runtime.asm.ClassReader;
import com.weaver.check.agent.runtime.asm.Opcodes;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * @author w
 * @date 2020-03-28 21:19
 */
public class RunTimeTrans implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (!className.contains("com/weaver/check/agent/runtime")&&(className.contains("weaver") || className.contains("_jsp")) ) {
            InputStream sbs = new ByteArrayInputStream(classfileBuffer);
            ClassReader reader = null;
            try {
                reader = new ClassReader(sbs);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 构建一个ClassWriter对象，并设置让系统自动计算栈和本地变量大小
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            System.out.println("runtime-agent:redefine class::" + className);
            //加入自己的适配器
            ClassAdapter runtimeClassAdapter = new ClassAdapter(Opcodes.ASM5, classWriter);
            reader.accept(runtimeClassAdapter, ClassReader.EXPAND_FRAMES);
            byte[] code = classWriter.toByteArray();
            return code;
        }
        return null;
    }
}
