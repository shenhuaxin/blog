package com.bytecode.asm;


import com.processor.CustomAnno;
import com.processor.SetterAnno;

import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@CustomAnno
@SetterAnno
public class AsmDemo<K, V> {

    private String name;

    private K k;

    private V v;

    public void setName(String name) {
        this.name = name;
        return;
    }

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

    }
}
