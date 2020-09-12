package com.bytecode.annotation.processor;

import com.processor.Data;

/**
 * @author shenhuaxin
 * @date 2020/9/10
 */
@Data
public class DataProcessorTest {

    private String name;


    public static void main(String[] args) {
        System.out.println(new DataProcessorTest().hashCode());
    }
}
