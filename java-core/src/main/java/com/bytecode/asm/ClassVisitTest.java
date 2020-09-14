package com.bytecode.asm;


import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.List;

import static org.objectweb.asm.Opcodes.ASM5;

public class ClassVisitTest {
    public static void main(String[] args) throws IOException {

        System.out.println("core api -----------");
        coreApi();
        System.out.println("tree api -----------");
        treeApi();
    }


    public static void coreApi() throws IOException {
        String className = BeforeAsmClass.class.getName(); // MyMain.class 文件的字节数组
        ClassReader cr = new ClassReader(className);
        ClassWriter cw = new ClassWriter(0);
        ClassVisitor cv = new ClassVisitor(ASM5, cw) {

            @Override
            public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
                System.out.println("field: " + name + " " + desc + " " + signature + " " + value);
                return super.visitField(access, name, desc, signature, value);
            }

            @Override
            public void visitSource(String source, String debug) {
                super.visitSource(source, debug);
                System.out.println(source);
                System.out.println(debug);
            }

            @Override
            public void visitInnerClass(String name, String outerName, String innerName, int access) {
                System.out.println(name);
                System.out.println(outerName);
                System.out.println(innerName);

                super.visitInnerClass(name, outerName, innerName, access);
            }



            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                System.out.println("method: " + name);
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
        };
        cr.accept(cv, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG);
    }

    public static void treeApi() throws IOException {

        ClassReader cr = new ClassReader(BeforeAsmClass.class.getName());
        ClassNode cn = new ClassNode();
        cr.accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_CODE);

        List<FieldNode> fields = cn.fields;
        for (int i = 0; i < fields.size(); i++) {
            FieldNode fieldNode = fields.get(i);
            System.out.println("field: " + fieldNode.name);
        }
        List<MethodNode> methods = cn.methods;
        for (int i = 0; i < methods.size(); ++i) {
            MethodNode method = methods.get(i);
            System.out.println("method: " + method.name);
        }
    }
}
