package com.weaver.check.agent.runtime;


import com.weaver.check.agent.runtime.asm.commons.AdviceAdapter;
import com.weaver.check.agent.runtime.asm.*;
import com.weaver.check.agent.runtime.asm.commons.AnalyzerAdapter;
import com.weaver.check.agent.runtime.asm.commons.LocalVariablesSorter;

import static com.weaver.check.agent.runtime.asm.Opcodes.*;

/**
 * @author w
 * @date 2020-03-28 21:19
 */
public class ClassAdapter extends ClassVisitor {
    private String className;
    private String methodName;
    private boolean isInterface;

    public ClassAdapter(int api, ClassVisitor cv) {
        super(api, cv);
    }


    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        cv.visit(version, access, name, signature, superName, interfaces);
        className = name;
        isInterface = (access & ACC_INTERFACE) != 0;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        methodName = name;
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (!isInterface && mv != null && !name.equals("<init>") && !name.equals("<clinit>") ) {
            if (className.contains("SecurityFilter") && name.equals("doFilterInternal")) {
                FilterAdapter rt = new FilterAdapter(Opcodes.ASM5, mv, access, name, desc);
                rt.aa = new AnalyzerAdapter(className, access, name, desc, rt);
                rt.lvs = new LocalVariablesSorter(access, desc, rt.aa);
                return rt.lvs;
            } else if (name.equals("executeSql")) {
                RecordSetAdapter rt = new RecordSetAdapter(Opcodes.ASM5, mv, access, name, desc);
                rt.aa = new AnalyzerAdapter(className, access, name, desc, rt);
                rt.lvs = new LocalVariablesSorter(access, desc, rt.aa);
                return rt.lvs;
            } else {
                RunTimeAdapter rt = new RunTimeAdapter(Opcodes.ASM5, mv, access, name, desc);
                rt.aa = new AnalyzerAdapter(className, access, name, desc, rt);
                rt.lvs = new LocalVariablesSorter(access, desc, rt.aa);
                return rt.lvs;
            }
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        /*if (!isInterface) {
            FieldVisitor fv = cv.visitField(ACC_PUBLIC + ACC_STATIC, "monitorAopRTStFiled",
                    "J", null, null);
            if (fv != null) {
                fv.visitEnd();
            }
        }*/
        cv.visitEnd();
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {

        return super.visitField(access, name, desc, signature, value);
    }

    class RunTimeAdapter extends AdviceAdapter {
        public LocalVariablesSorter lvs;
        public AnalyzerAdapter aa;
        private int time;
        private int maxStack;
        public RunTimeAdapter(int api, MethodVisitor mv, int access, String name, String desc) {
            super(api, mv, access, name, desc);
            this.mv = mv;
            methodName = name;
        }

        @Override
        protected void onMethodEnter() {
            mv.visitMethodInsn(INVOKESTATIC, "com/weaver/check/agent/runtime/RunTimeAop", "methodBefore", "()J", false);
            time = newLocal(Type.LONG_TYPE);
            mv.visitVarInsn(LSTORE, time);
            maxStack = 4;
            //mv.visitFieldInsn(PUTSTATIC, className, "monitorAopRTStFiled", "J");
        }

        @Override
        protected void onMethodExit(int opcode) {
            mv.visitLdcInsn(className);
            mv.visitLdcInsn(methodName);
            mv.visitVarInsn(LLOAD, time);
            //mv.visitFieldInsn(GETSTATIC, className, "monitorAopRTStFiled", "J");
            mv.visitMethodInsn(INVOKESTATIC, "com/weaver/check/agent/runtime/RunTimeAop", "methodAfter", "(Ljava/lang/String;Ljava/lang/String;J)V", false);
            maxStack = Math.max(aa.stack.size() + 4, maxStack);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            mv.visitMaxs(Math.max(this.maxStack, maxStack), maxLocals);
        }
    }

    class FilterAdapter extends AdviceAdapter {
        public LocalVariablesSorter lvs;
        public AnalyzerAdapter aa;
        private int time;
        private int maxStack;
        public FilterAdapter(int api, MethodVisitor mv, int access, String name, String desc) {
            super(api, mv, access, name, desc);
            this.mv = mv;
            methodName = name;
        }

        @Override
        protected void onMethodEnter() {
            loadArg(0);
            mv.visitMethodInsn(INVOKESTATIC, "com/weaver/check/agent/runtime/FilterAop", "methodBefore", "(Ljava/lang/Object;)J", false);
            time = newLocal(Type.LONG_TYPE);
            mv.visitVarInsn(LSTORE, time);
            maxStack = 4;
            //mv.visitFieldInsn(PUTSTATIC, className, "monitorAopRTStFiled", "J");
        }

        @Override
        protected void onMethodExit(int opcode) {
            mv.visitLdcInsn(className);
            mv.visitLdcInsn(methodName);
            //mv.visitFieldInsn(GETSTATIC, className, "monitorAopRTStFiled", "J");
            mv.visitVarInsn(LLOAD, time);
            mv.visitMethodInsn(INVOKESTATIC, "com/weaver/check/agent/runtime/FilterAop", "methodAfter", "(Ljava/lang/String;Ljava/lang/String;J)V", false);
            maxStack = Math.max(aa.stack.size() + 4, maxStack);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            mv.visitMaxs(Math.max(this.maxStack, maxStack), maxLocals);
        }

    }

    class RecordSetAdapter extends AdviceAdapter {
        public LocalVariablesSorter lvs;
        public AnalyzerAdapter aa;
        private int time;
        private int maxStack;
        public RecordSetAdapter(int api, MethodVisitor mv, int access, String name, String desc) {
            super(api, mv, access, name, desc);
            this.mv = mv;
            methodName = name;
        }

        @Override
        protected void onMethodEnter() {
            mv.visitMethodInsn(INVOKESTATIC, "com/weaver/check/agent/runtime/RunTimeAop", "methodBefore", "()J", false);
            //mv.visitFieldInsn(PUTSTATIC, className, "monitorAopRTStFiled", "J");
            time = newLocal(Type.LONG_TYPE);
            mv.visitVarInsn(LSTORE, time);
            maxStack = 4;
        }

        @Override
        protected void onMethodExit(int opcode) {
            mv.visitCode();
            mv.visitLdcInsn(className);
            mv.visitLdcInsn(methodName);
            //mv.visitFieldInsn(GETSTATIC, className, "monitorAopRTStFiled", "J");
            mv.visitVarInsn(LLOAD, time);
            loadArgArray();
            mv.visitMethodInsn(INVOKESTATIC, "com/weaver/check/agent/runtime/RecordSetSqlAop", "methodAfter", "(Ljava/lang/String;Ljava/lang/String;J[Ljava/lang/Object;)V", false);
            maxStack = Math.max(aa.stack.size() + 4, maxStack);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            mv.visitMaxs(Math.max(this.maxStack, maxStack), maxLocals);
        }
    }
}