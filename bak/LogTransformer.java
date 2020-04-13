package com.weaver.check.agent.runtime;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import com.weaver.check.agent.runtime.asm.ClassReader;
import com.weaver.check.agent.runtime.asm.ClassWriter;

public class LogTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            InputStream sbs = new ByteArrayInputStream(classfileBuffer);
            ClassReader cr = new ClassReader(sbs);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            System.out.println("redefine class::"+className);
            TimeCountAdpter timeCountAdpter = new TimeCountAdpter(cw);
            System.out.println("************************"+System.getProperty("user.dir")
                    + className.replaceAll("/","_")+".class");
            cr.accept(timeCountAdpter, ClassReader.EXPAND_FRAMES);
            byte[] code = cw.toByteArray();
            FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir")
                    + className.replaceAll("/","_")+".class");
            fos.write(code);
            fos.close();
            return code;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}