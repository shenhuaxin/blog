package com.bytecode.asm;

import java.lang.reflect.InvocationTargetException;

public class BeforeAsmClass {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BeforeAsmClass beforeAsmClass = new BeforeAsmClass();
        beforeAsmClass.originMethod(true);
    }



    public void originMethod(boolean flag) {
        if (flag) {
            System.out.println("do something");
            return;
        }
        return;
    }
}
