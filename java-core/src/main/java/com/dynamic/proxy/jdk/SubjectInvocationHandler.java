package com.dynamic.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author shenhuaxin
 * @date 2020/12/29
 */
public class SubjectInvocationHandler implements InvocationHandler {

    private Subject subject;

    public SubjectInvocationHandler(Subject subject) {
        this.subject = subject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("start invoker");
        Object invoke = method.invoke(subject, args);
        System.out.println("end invoker");
        return invoke;
    }

    public Subject getProxy() {
        return (Subject) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), subject.getClass().getInterfaces(), this);
    }


    public static void main(String[] args) {
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        Subject subject = new RealSubject();
        SubjectInvocationHandler subjectInvocationHandler = new SubjectInvocationHandler(subject);
        Subject proxy = subjectInvocationHandler.getProxy();
        proxy.test();
    }
}
