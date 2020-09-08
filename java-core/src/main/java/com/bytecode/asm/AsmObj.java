package com.bytecode.asm;


import com.processor.GetterAnno;
import com.processor.SetterAnno;

@SetterAnno
public class AsmObj {


    private String name;

    private String age;

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }


    public static void main(String[] args) {
        AsmObj asmObj = new AsmObj();
        System.out.println(asmObj.getName());
    }
}
