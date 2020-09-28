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

#### CORE-API
Core-Api
```
visit
[visitSource]
[visitOuterClass] 
(visitAnnotation | visitAttribute)*
(visitInnerClass | visitField | visitMethod)* 
visitEnd
```






参考资料：
1. https://blog.csdn.net/ryo1060732496/article/details/103655505
2. https://asm.ow2.io/asm4-guide.pdf