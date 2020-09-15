package com.bytecode.asm;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.FileOutputStream;
import java.io.IOException;

import static org.objectweb.asm.Opcodes.*;

public class ChangeClassTest {


    public static void main(String[] args) throws IOException {

        addField();
        addMethodByCoreApi();
    }


    public static void addMethodByTreeApi() throws IOException {
        ClassReader classReader = new ClassReader(BeforeAsmClass.class.getName());
        ClassWriter classWriter = new ClassWriter(0);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.SKIP_CODE);
        MethodNode methodNode = new MethodNode(ACC_PUBLIC, "name", "(Ljava/lang.String;)V", null, null);
        classNode.methods.add(methodNode);

        classNode.accept(classWriter);
        write(BeforeAsmClass.class, classWriter.toByteArray());
    }

    public static void addMethodByCoreApi() throws IOException {
        ClassReader classReader = new ClassReader(BeforeAsmClass.class.getName());
        ClassWriter classWriter = new ClassWriter(0);
        ClassVisitor classVisitor = new ClassVisitor(ASM8, classWriter) {
            @Override
            public void visitEnd() {
                super.visitEnd();
                MethodVisitor method = cv.visitMethod(ACC_PUBLIC, "name", "(Ljava/lang.String;)V", null, null);
                if (method != null) {
                    method.visitEnd();
                }

            }
        };
        classReader.accept(classVisitor, ClassReader.SKIP_CODE);
        write(BeforeAsmClass.class, classWriter.toByteArray());
    }

    public static void addField() throws IOException {
        ClassReader classReader = new ClassReader(BeforeAsmClass.class.getName());
        ClassWriter classWriter = new ClassWriter(0);
        ClassVisitor cv = new ClassVisitor(ASM5, classWriter) {
            @Override
            public void visitEnd() {
                super.visitEnd();
                FieldVisitor name = cv.visitField(Opcodes.ACC_PUBLIC, "name", "Ljava/lang/String;", null, null);
                if (name != null) {
                    name.visitEnd();
                }
            }
        };
        classReader.accept(cv, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG);
        write(BeforeAsmClass.class, classWriter.toByteArray());
    }

    public static <T> void write(Class<T> clazz, byte[] bytes) {
        String path = ClassLoader.getSystemResource(clazz.getName().replace('.', '/') + ".class").getPath();

        try (FileOutputStream fileOutputStream = new FileOutputStream(path);) {
            fileOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
