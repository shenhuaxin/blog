package com.bytecode.asm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeforeAsmClass {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BeforeAsmClass beforeAsmClass = new BeforeAsmClass();
        Method method = beforeAsmClass.getClass().getMethod("setName", String.class);
        method.invoke(beforeAsmClass, "shenhuaxin");
        Method getName = beforeAsmClass.getClass().getMethod("getName");
        System.out.println(getName.invoke(beforeAsmClass));
    }
}
