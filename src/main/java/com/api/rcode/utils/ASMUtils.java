package com.api.rcode.utils;

import com.alibaba.fastjson.JSON;
import com.api.rcode.controller.HelloController2;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.SpringAsmInfo;
import org.springframework.asm.Type;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Executable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：河神
 * @date ：Created in 2022/2/11 3:16 下午
 */
public class ASMUtils {

    private final static Map<Class<?>,Map<Executable, String[]>> cacheMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        Map<Executable, String[]> map = getParameterMap(HelloController2.class);
        map.forEach((k,v)->{
            System.out.println(k.getName());
            System.out.println(JSON.toJSONString(v));
        });
    }


    public static Map<Executable, String[]> getParameterMap(Class<?> clazz)  {
        Map<Executable, String[]> executableMap = cacheMap.get(clazz);
        if (executableMap!=null){
            return executableMap;
        }

        InputStream is = clazz.getResourceAsStream(ClassUtils.getClassFileName(clazz));
        if (is==null){
            return null;
        }
        try {
            ClassReader classReader = new ClassReader(is);
            Map<Executable, String[]> map = new ConcurrentHashMap<>(32);
            classReader.accept(new ParamVisitor(clazz,map),0);
            cacheMap.put(clazz,map);
            return map;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public static  class  ParamVisitor extends ClassVisitor {

        private static final String STATIC_CLASS_INIT = "<clinit>";

        private final Class<?> clazz ;
        private final Map<Executable, String[]> executableMap;

        public ParamVisitor(Class<?> clazz,Map<Executable, String[]> executableMap) {
            super(Opcodes.ASM10_EXPERIMENTAL);
            this.clazz = clazz;
            this.executableMap = executableMap;
        }

        @Override
        @Nullable
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            // exclude synthetic + bridged && static class initialization
            if (!isSyntheticOrBridged(access) && !STATIC_CLASS_INIT.equals(name)) {
                return new LocalVariableTableVisitor(this.clazz, this.executableMap, name, desc, isStatic(access));
            }
            return null;
        }

        private static boolean isSyntheticOrBridged(int access) {
            return (((access & Opcodes.ACC_SYNTHETIC) | (access & Opcodes.ACC_BRIDGE)) > 0);
        }

        private static boolean isStatic(int access) {
            return ((access & Opcodes.ACC_STATIC) > 0);
        }
    }


    private static class LocalVariableTableVisitor extends MethodVisitor {

        private static final String CONSTRUCTOR = "<init>";

        private final Class<?> clazz;

        private final Map<Executable, String[]> executableMap;

        private final String name;

        private final Type[] args;

        private final String[] parameterNames;

        private final boolean isStatic;

        private boolean hasLvtInfo = false;

        /*
         * The nth entry contains the slot index of the LVT table entry holding the
         * argument name for the nth parameter.
         */
        private final int[] lvtSlotIndex;

        public LocalVariableTableVisitor(Class<?> clazz, Map<Executable, String[]> map, String name, String desc, boolean isStatic) {
            super(SpringAsmInfo.ASM_VERSION);
            this.clazz = clazz;
            this.executableMap = map;
            this.name = name;
            this.args = Type.getArgumentTypes(desc);
            this.parameterNames = new String[this.args.length];
            this.isStatic = isStatic;
            this.lvtSlotIndex = computeLvtSlotIndices(isStatic, this.args);
        }

        @Override
        public void visitLocalVariable(String name, String description, String signature, Label start, Label end, int index) {
            this.hasLvtInfo = true;
            for (int i = 0; i < this.lvtSlotIndex.length; i++) {
                if (this.lvtSlotIndex[i] == index) {
                    this.parameterNames[i] = name;
                }
            }
        }

        @Override
        public void visitEnd() {
            if (this.hasLvtInfo || (this.isStatic && this.parameterNames.length == 0)) {
                // visitLocalVariable will never be called for static no args methods
                // which doesn't use any local variables.
                // This means that hasLvtInfo could be false for that kind of methods
                // even if the class has local variable info.
                this.executableMap.put(resolveExecutable(), this.parameterNames);
            }
        }

        private Executable resolveExecutable() {
            ClassLoader loader = this.clazz.getClassLoader();
            Class<?>[] argTypes = new Class<?>[this.args.length];
            for (int i = 0; i < this.args.length; i++) {
                argTypes[i] = ClassUtils.resolveClassName(this.args[i].getClassName(), loader);
            }
            try {
                if (CONSTRUCTOR.equals(this.name)) {
                    return this.clazz.getDeclaredConstructor(argTypes);
                }
                return this.clazz.getDeclaredMethod(this.name, argTypes);
            }
            catch (NoSuchMethodException ex) {
                throw new IllegalStateException("Method [" + this.name +
                        "] was discovered in the .class file but cannot be resolved in the class object", ex);
            }
        }

        private static int[] computeLvtSlotIndices(boolean isStatic, Type[] paramTypes) {
            int[] lvtIndex = new int[paramTypes.length];
            int nextIndex = (isStatic ? 0 : 1);
            for (int i = 0; i < paramTypes.length; i++) {
                lvtIndex[i] = nextIndex;
                if (isWideType(paramTypes[i])) {
                    nextIndex += 2;
                }
                else {
                    nextIndex++;
                }
            }
            return lvtIndex;
        }

        private static boolean isWideType(Type aType) {
            // float is not a wide type
            return (aType == Type.LONG_TYPE || aType == Type.DOUBLE_TYPE);
        }
    }

}
