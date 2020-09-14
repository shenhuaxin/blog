package com.bytecode.asm;

import org.objectweb.asm.*;

import java.io.FileOutputStream;
import java.io.IOException;

import static org.objectweb.asm.Opcodes.ASM5;

public class ChangeClassTest {


    public static void main(String[] args) throws IOException {

        addField();
    }


    public static void addField() throws IOException {
        ClassReader classReader = new ClassReader(BeforeAsmClass.class.getName());
        ClassWriter classWriter = new ClassWriter(0);
        ClassVisitor cv = new ClassVisitor(ASM5, classWriter) {
            @Override
            public void visitEnd() {
                super.visitEnd();
                FieldVisitor name = cv.visitField(Opcodes.ACC_PUBLIC, "name", "Ljava/lang/String;", null, "shenhuaxin");
                if (name != null) {
                    name.visitEnd();
                }
            }
        };
        classReader.accept(cv, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG);

        String path = ClassLoader.getSystemResource(BeforeAsmClass.class.getName().replace('.', '/')+".class").getPath();

        byte[] bytes = classWriter.toByteArray();

        FileOutputStream fileOutputStream = new FileOutputStream(path);
        fileOutputStream.write(bytes);
    }


    public static void addMethod() throws IOException {
        ClassReader classReader = new ClassReader(BeforeAsmClass.class.getName());
        ClassWriter classWriter = new ClassWriter(0);
        ClassVisitor classVisitor = new ClassVisitor(ASM5, classWriter) {
            @Override
            public void visitEnd() {
                super.visitEnd();
                FieldVisitor name = classWriter.visitField(Opcodes.ACC_PUBLIC, "name", "Ljava/lang/String", null, null);
                if (name != null) {
                    name.visitEnd();
                }
            }
        };
    }
}
