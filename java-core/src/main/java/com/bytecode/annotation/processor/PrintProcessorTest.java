package com.bytecode.annotation.processor;

import com.bytecode.asm.AsmObj;
import com.processor.PrintAnno;

/**
 * @author shenhuaxin
 * @date 2020/9/9
 */
@PrintAnno
public class PrintProcessorTest {


    private String name;

    private String age = "10";

    public String getName() {
        return name;
    }

    public String getAge(String name, Long age1) {
        this.name = name;
        String name1 = name;
        Long age22 = age1;
        return age;
    }


    public static void main(String[] args) {
        AsmObj asmObj = new AsmObj();
        System.out.println(asmObj.getName());
    }


}
