package com.dynamic.proxy.jdk;

/**
 * @author shenhuaxin
 * @date 2020/12/29
 */
public class RealSubject implements Subject{

    @Override
    public void test() {
        System.out.println("realSubject.test ====");
    }
}
