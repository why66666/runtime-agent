package com.weaver.check.agent.runtime;


import com.weaver.check.agent.runtime.asm.*;
import com.weaver.check.agent.runtime.asm.commons.AnalyzerAdapter;
import com.weaver.check.agent.runtime.asm.commons.LocalVariablesSorter;

public class WeaverClassAdapter extends ClassVisitor {


    private String className;
    private String methodName;


    public WeaverClassAdapter(int api, ClassVisitor cv) {
        super(api, cv);
    }


    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        className = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        methodName = name;
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (!name.equals("<init>")) {
            RunTimeAdapter rt = new RunTimeAdapter(api,mv);
            rt.aa = new AnalyzerAdapter(className, access, name, signature, rt);
            rt.lvs = new LocalVariablesSorter(access, signature, rt.aa);
            return rt.lvs;
        }
        return mv;
    }


    class RunTimeAdapter extends MethodVisitor implements Opcodes {

        private int time;
        public LocalVariablesSorter lvs;
        public AnalyzerAdapter aa;


        public RunTimeAdapter(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitCode() {
            mv.visitCode();
            mv.visitMethodInsn(INVOKESTATIC, "com/weaver/check/agent/runtime/MethodAop", "methodBefore", "()J", false);
            time=lvs.newLocal(Type.LONG_TYPE);
            mv.visitVarInsn(LSTORE, time);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 3);
            mv.visitEnd();
        }


        @Override
        public void visitInsn(int opcode) {
            if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
                mv.visitCode();
                mv.visitLdcInsn(className);
                mv.visitLdcInsn(methodName);
                mv.visitVarInsn(LLOAD, time);
                mv.visitMethodInsn(INVOKESTATIC, "com/weaver/check/agent/runtime/MethodAop", "methodAfter", "(Ljava/lang/String;Ljava/lang/String;J)V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(4, 3);
                mv.visitEnd();
            }
            mv.visitInsn(opcode);
        }

    }


}