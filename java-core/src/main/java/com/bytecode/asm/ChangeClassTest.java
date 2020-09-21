package com.bytecode.asm;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.objectweb.asm.Opcodes.*;

public class ChangeClassTest {


    public static void main(String[] args) throws IOException {
//
        System.out.println(Type.getType(String.class).getInternalName());
//        addField();
//        addSetNameMethodByCoreApi();
//        addGetNameMethodByTreeApi();
    }

    public static void addSetNameMethodByCoreApi() throws IOException {
        ClassReader classReader = new ClassReader(BeforeAsmClass.class.getName());
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        ClassVisitor classVisitor = new ClassVisitor(ASM8, classWriter) {
            @Override
            public void visitEnd() {
                MethodVisitor setName = cv.visitMethod(ACC_PUBLIC, "setName", "(Ljava/lang/String;)V", null, null);
                setName.visitCode();
                setName.visitVarInsn(ALOAD, 0);
                setName.visitVarInsn(Type.getType(BeforeAsmClass.class).getOpcode(ILOAD), 1);
                setName.visitFieldInsn(PUTFIELD, "com/bytecode/asm/BeforeAsmClass", "name", "Ljava/lang/String;");
                setName.visitInsn(RETURN);
                setName.visitMaxs(0, 0);
                setName.visitEnd();
            }
        };
        classReader.accept(classVisitor, ClassReader.SKIP_DEBUG);
        write(BeforeAsmClass.class, classWriter.toByteArray());
    }

    /**
     * public getA()Ljava/lang/String;
     * ALOAD 0
     * GETFIELD com/bytecode/asm/BeforeAsmClass.a : Ljava/lang/String;
     * ARETURN
     *
     * @throws IOException
     */
    public static void addGetNameMethodByTreeApi() throws IOException {
        ClassReader classReader = new ClassReader(BeforeAsmClass.class.getName());
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.SKIP_DEBUG);
        MethodNode methodNode = new MethodNode(ACC_PUBLIC, "getName", "()Ljava/lang/String;", null, null);
        methodNode.visitVarInsn(ALOAD, 0);
        methodNode.visitFieldInsn(GETFIELD, "com/bytecode/asm/BeforeAsmClass", "name", "Ljava/lang/String;");
        methodNode.visitInsn(ARETURN);
        methodNode.visitMaxs(0, 0);
        methodNode.visitEnd();
        classNode.methods.add(methodNode);
        classNode.accept(classWriter);
        write(BeforeAsmClass.class, classWriter.toByteArray());
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
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new ClassVisitor(ASM8, classWriter) {
            @Override
            public void visitEnd() {
                super.visitEnd();
                FieldVisitor name = cv.visitField(Opcodes.ACC_PUBLIC, "name", "Ljava/lang/String;", null, null);
                if (name != null) {
                    name.visitEnd();
                }
            }
        };
        classReader.accept(cv, ClassReader.SKIP_DEBUG);
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
