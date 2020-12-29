package com.reflect;



import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

public class ReflectTest {


    public static void main(String[] args) {
        getDeclaringClass();
    }

    /**
     * 获取泛型类的泛型参数。
     * 注： 获取的不是实际使用的String、Integer等， 而是E、S、T等。
     *      如果需要获取实际的类型，如String。 可使用如Jackson、FastJson的逻辑，使用抽象类，在实际使用时使用子类。这样可以优雅获取实际类型。
     *      如： JSON.parseObject("", new TypeReference<String>(){});
     */
    private static void getTypeParameters() {
        List<String> list = new ArrayList<>();
        TypeVariable<? extends Class<? extends List>>[] typeParameters = list.getClass().getTypeParameters();
        for (TypeVariable<? extends Class<? extends List>> typeParameter : typeParameters) {
            System.out.println(typeParameter.getTypeName());
        }
    }

    /**
     * 获取方法的声明类
     */
    public static void getDeclaringClass() {
        Method[] methods = ReflectTest.class.getMethods();

        for (Method method : methods) {
            Class<?> declaringClass = method.getDeclaringClass();
            System.out.println("methodName:  " + method.getName() + "   declaringClassName:  " + declaringClass.getSimpleName());
        }
    }

    @Override
    public String toString() {
        return "ReflectTest{}";
    }
}
