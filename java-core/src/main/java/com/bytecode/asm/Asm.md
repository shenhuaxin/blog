### ASM入门

#### 模型
ASM提供了两种生成和转换类的模型。
1. 基于事件的方式的core api。 访问类的某个部分，会产生一个事件。
2. 基于对象的表达方式的tree api。将类解析为一棵树。

可以使用解析XMl的两种方式进行对比：
1. SAX解析XML, 使用事件驱动，一边按照内容顺序解析XML,解析到了特定的内容则产生事件，调用对应的函数处理事件。SAX是流式的、单向的，
解析过的部分不会再次读取。Core-Api 类似于这种模式。
2. DOM解析XML, 则会将整个XML作为类似树结构的方式读入内存。之后可以反复读取。Tree-Api类似于这种模式。

#### JVM Class文件结构概览

##### 编译后类结构
![编译类结构](https://raw.githubusercontent.com/shenhuaxin/blog/master/file/pic/%E7%BC%96%E8%AF%91%E5%90%8EClass%E7%BB%93%E6%9E%84.png)

##### INTERNAL NAME
将类的全限定名中的 . 号 替换为 / 即为类的Internal names。  
即java.lang.String的Internal name为java/lang/String

##### Type descriptors
Java Type | Type descriptor
---- | ---
boolean | Z
char |  C
byte | B
short | S
int | I
float | F
long | J
double | D
Object | Ljava/lang/Object; (引用类型要以 ; 号结束)
int[] | [I
Object[][] | [[Ljava/lang/Object;

##### Method descriptors
源代码中方法描述 | Class文件中方法描述
---- | ----
void m(int i, float f) | (IF)V
int m(Object o) | (Ljava/lang/Object;)I
int[] m(int i, String s) | (ILjava/lang/String;)[I
Object m(int[] i) | ([I)Ljava/lang/Object;

##### Local Variable: 方法本地变量
除了静态方法，第0个都是this对象。
##### Execute Frame： 方法执行栈帧
入栈出栈操作。

#### CORE-API
Core-Api中有三大核心类： 
1. ClassReader : 用于读取和解析二进制的class文件，每当有事件发生，调用ClassVisitor、MethodVisitor相应的方法。
2. ClassVisitor : 是抽象类，重写该类中的方法，在事件发生时，触发自定义的逻辑。有些visit过程还可以触发子过程，
例如，MethodVisitor、AnnotationVisitor。（如编译后类结果的图所示）
3. ClassWriter : ClassWriter是ClassVisitor的一个实现类，它之前的每个ClassVisitor都可以对原始的字节码进行修改，
ClassWriter最后使用toByteArray方法将最终修改的字节码以字节数组的方式返回。

三者的联系如下：  
ClassReader:  event producer  
ClassVisitor: event filter  
ClassWriter:  event writer  

##### ClassReader
###### 创建ClassReader

创建ClassReader主要的3个方法

1. public ClassReader(final byte[] classFile);
2. public ClassReader(final InputStream inputStream) throws IOException;
3. public ClassReader(final String className) throws IOException;

方法2和方法3最终都是使用了方法1。

###### 调用accept方法，读取class文件，触发相应的函数。

1. public void accept(final ClassVisitor classVisitor, final int parsingOptions)；

2. public void accept(final ClassVisitor classVisitor, final Attribute[] attributePrototypes, final int parsingOptions);

第一个参数为classVisitor, 作为事件的消费者。

第二个参数为attributePrototype, 不在attributePrototype中的属性不会被解析。

第三个参数为parsingOptions,  有4个值：SKIP_CODE, SKIP_DEBUG, SKIP_FRAMES or EXPAND_FRAMES.


##### ClassVisitor
###### 创建ClassVisitor
1. public ClassVisitor(final int api);
2. public ClassVisitor(final int api, final ClassVisitor classVisitor);

第一个参数为ASM的版本， 有Opcodes.ASM5, Opcodes.ASM6，Opcodes.ASM7, Opcodes.ASM8等。  
第二个参数为ClassVisitor, 在ClassVisitor中使用另一个ClassVisitor。

###### 如何使用ClassVisitor访问class字节码
使用ClassReader.accept 方法。

###### ClassVisitor的方法调用顺序： 
```
visit
[visitSource]
[visitOuterClass] 
(visitAnnotation | visitAttribute)*
(visitInnerClass | visitField | visitMethod)* 
visitEnd
```

###### MethodVisitor的方法调用顺序
```
 ( visitParameter )* 
 [ visitAnnotationDefault ] 
 ( visitAnnotation | visitAnnotableParameterCount | visitParameterAnnotation visitTypeAnnotation | visitAttribute )* 
 [ visitCode ( visitFrame | visit<i>X</i>Insn | visitLabel | visitInsnAnnotation | visitTryCatchBlock | visitTryCatchAnnotation | visitLocalVariable | visitLocalVariableAnnotation | visitLineNumber )* visitMaxs ]
```

##### ClassWriter
###### 创建ClassWriter
1. public ClassWriter(final int flags);
2. public ClassWriter(final ClassReader classReader, final int flags);

第一个参数为classReader，用于读取目标class文件字节码。  
第二个参数为选项，0为不做任何事，COMPUTE_MAXS为自动计算最大栈深度， COMPUTE_FRAMES为计算方法的stack map frame


###### 生成类
classWriter.toByteArray();  
返回由classWriter构建的字节码内容。


##### 示例

###### 新增字段
```java
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
```

###### 添加Setter方法
```java
public static void addSetNameMethodByCoreApi() throws IOException {
    ClassReader classReader = new ClassReader(BeforeAsmClass.class.getName());
    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    ClassVisitor classVisitor = new ClassVisitor(ASM8, classWriter) {
        @Override
        public void visitEnd() {
            MethodVisitor setName = cv.visitMethod(ACC_PUBLIC, "setName", "(Ljava/lang/String;)V", null, null);
            setName.visitCode();
            setName.visitVarInsn(ALOAD, 0);
            setName.visitVarInsn(Type.getType(String.class).getOpcode(ILOAD), 1);
            setName.visitFieldInsn(PUTFIELD, "com/bytecode/asm/BeforeAsmClass", "name", "Ljava/lang/String;");
            setName.visitInsn(RETURN);
            setName.visitMaxs(0, 0);
            setName.visitEnd();
        }
    };
    classReader.accept(classVisitor, ClassReader.SKIP_DEBUG);
    write(BeforeAsmClass.class, classWriter.toByteArray());
}
```

###### 修改函数内容
在函数的入口和出口都加上一个System.out.println。
```java
public static void changeOriginMethod() throws IOException {
 ClassReader classReader = new ClassReader(BeforeAsmClass.class.getName());
    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    ClassVisitor classVisitor = new ClassVisitor(ASM8, classWriter) {
        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {

            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            if ("originMethod".equals(name)) {
                return new MethodVisitor(ASM8, mv) {
                    @Override
                    public void visitCode() {
                        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                        mv.visitLdcInsn("enter " + name);
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                        super.visitCode();
                    }
                    @Override
                    public void visitInsn(int opcode) {
                        switch (opcode) {
                            case RETURN:
                            case IRETURN:
                            case FRETURN:
                            case DRETURN:
                            case LRETURN:
                            case ARETURN:
                            case ATHROW:
                                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                                mv.visitLdcInsn("exit " + name);
                                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                                break;
                        }
                        super.visitInsn(opcode);
                    }
                };
            }
            return mv;
        }
    };
    classReader.accept(classVisitor, ClassReader.SKIP_DEBUG);
    write(BeforeAsmClass.class, classWriter.toByteArray());
}
```

#### TREE-API
##### ClassNode
###### 创建ClassNode
public ClassNode(int api);   参数为ASM版本。
public ClassNode();    默认为ASM8

ClassNode为ClassVisitor的子类。

###### 使用ClassNode
和使用classVisitor一样。
classReader.accept(classNode, ClassReader.SKIP_DEBUG);

之后便可从classNode中获取读入的类的树结构。


###### 示例
```java
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
```


#### 工具类
##### Opcodes
1. 包含了JAVA主版本号。
2. 包含了访问标识符。
3. 字节码指令。

##### Type
1. 获取类的Type。 public static Type getType(final Class<?> clazz);  
2. 获取方法的Type。 public static Type getType(final Method method);  
3. 获取Type对应的字节码指令。 public int getOpcode(final int opcode); 可选填的参数如下：
```
 ILOAD, ISTORE, IALOAD,
 IASTORE, IADD, ISUB, IMUL, IDIV, IREM, INEG, ISHL, ISHR, IUSHR, IAND, IOR, IXOR and
 IRETURN.
```
其中IALOAD, IASTORE 用于数组。有了这个函数，就不需要关注指令操作的值的类型。实现了指令类型自适应。

#### 参考资料：

1. https://blog.csdn.net/ryo1060732496/article/details/103655505
2. https://asm.ow2.io/asm4-guide.pdf