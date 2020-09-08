package com.bytecode.asm;


import com.processor.CustomAnno;
import com.processor.GetterAnno;
import com.processor.SetterAnno;

import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@CustomAnno
@SetterAnno
public class AsmDemo {

    private String name;

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

//
//        ClassReader classReader = new ClassReader("meituan/bytecode/asm/Base");
//        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
//        //处理
//        ClassVisitor classVisitor = new MyClassVisitor(classWriter);
//        classReader.accept(classVisitor, ClassReader.SKIP_DEBUG);
//        byte[] data = classWriter.toByteArray();
//        //输出
//        File f = new File("operation-server/target/classes/meituan/bytecode/asm/Base.class");
//        FileOutputStream fout = new FileOutputStream(f);
//        fout.write(data);
////        fout.close();
//        System.out.println("now generator cc success!!!!!");
        AsmDemo asmObj = new AsmDemo();
        asmObj.name = "nihao";
        System.out.println(asmObj.getName());
//        Method getName = AsmDemo.class.getMethod("getName");
//        System.out.println(getName.invoke(asmObj));
////        System.out.println(asmObj.getName());

    }
}
