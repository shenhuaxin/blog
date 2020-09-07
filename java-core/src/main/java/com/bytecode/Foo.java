package com.bytecode;

import java.io.IOException;
import java.lang.invoke.*;

public class Foo {
    public void print(String s) {
        System.out.println("hello, " + s);
    }

    public static void main(String[] args) throws Throwable {

        int i = 0;
        i = ++i + i++ + i++ + i++;
        System.out.println("i=" + i);
        Foo foo = new Foo();

        MethodType methodType = MethodType.methodType(void.class, String.class);
        MethodHandle methodHandle = MethodHandles.lookup().findVirtual(Foo.class, "print", methodType);
        methodHandle.invokeExact(foo, "world");
    }

}
