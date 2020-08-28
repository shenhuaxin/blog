### Class类

#### 基本介绍

官方文档是这样介绍Class类。

```java
Instances of the class Class represent classes and interfaces in a running Java application. An enum is a kind of class and an annotation is a kind of interface. Every array also belongs to a class that is reflected as a Class object that is shared by all arrays with the same element type and number of dimensions. The primitive Java types (boolean, byte, char, short, int, long, float, and double), and the keyword void are also represented as Class objects.
```

Class类实例代表了运行中Java应用的类和接口。枚举是类的一种，注解是接口的一种。每个数组都属于一个类，类型和维度相同的数组属于相同的类。

8种基本类型和特殊的void类型都可以作为一个类对象。



#### 获取Class对象的几种方法

**通过对象获取**

```java
Object obj = new Object();
Class clazz = obj.getClass();
```

**通过类获取**

```java
Class clazz = Object.class;
```

**通过类的全限定名获取**

```java
Class clazz = Class.forName("java.lang.Object");
```



#### Class 方法



##### asSubclass(Class<T> clazz)

```
Class<? extends Object> aClass = String.class.asSubclass(Object.class);
```

用户判断某个类是否为参数Class的子类。用于窄化Class。

```java
class A {}
class B extends A {}

public static void main(String[] args) {
    Class a1 = B.class;
    Class<? extends B> aClass = a1.asSubclass(B.class); //经常使用父类引用子类对象。想把父类强转为子类，会有警告，而这样使用，无警告。
}
```



##### cast(Object obj)

将对象转换为对应类型。

```java
Father father = new Son();
Son son = Son.class.cast(father);
```



