package com.bytecode.annotation.processor;

import com.processor.Log;

public class LogProcessorTest {


    @Log(value = "log Test start")
    public void logTest() {
        System.out.println(1+1);
    }


    public static void main(String[] args) {
        new LogProcessorTest().logTest();
    }


}
