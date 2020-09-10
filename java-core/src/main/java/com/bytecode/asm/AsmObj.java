package com.bytecode.asm;


import com.processor.PrintAnno;

import java.util.Objects;

@PrintAnno
public class AsmObj {


    private String name;

    private String age = "10";

    private AsmDemo asmDemo;

    public String getName() {
        return name;
    }

    public String getAge(String name, Long age1) {
        this.name = name;
        String name1 = name;
        Long age22 = age1;
        return age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AsmObj asmObj = (AsmObj) o;
        return Objects.equals(name, asmObj.name) &&
                Objects.equals(age, asmObj.age) &&
                Objects.equals(asmDemo, asmObj.asmDemo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, asmDemo);
    }

    public static void main(String[] args) {
        AsmObj asmObj = new AsmObj();
        System.out.println(asmObj.getName());
    }
}
