package com.bytecode.asm;


import com.processor.GetterAnno;
import com.processor.SetterAnno;

import javax.xml.bind.annotation.XmlElement;

@SetterAnno
@GetterAnno
public class AsmObj {


    private String name;

    @XmlElement
    private String age = "10";

    public String getName() {
        return name;
    }

//    @GetterAnno
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
