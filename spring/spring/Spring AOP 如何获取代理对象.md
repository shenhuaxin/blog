先看代码

```java
/**
 * 这个一个通知类
 */
@Aspect
@Component
public class AdviceDemo {
    
    @Pointcut("execution(* org.learn.spring.aop.advice.Main.call())")
    public void pointcut(){
    }

    @Before(value = "pointcut()")
    public void beforecall(){
        System.out.println("before call");
    }
}
```

设置代理

```
    <aop:aspectj-autoproxy />   // proxy-target-class = "false" 表示默认使用JDK代理，无接口使用CGLIB代理。
```

```java
// 一个接口， 使用它来测试JDK代理和CgLib代理。
public interface MainI {
    void call();
}
```

#### 第一种方式

我们先不实现MainI 接口， SpringAOP将使用CGLIB为Main创建代理对象。

```java
@Component
public class Main {
    public static void main(String[] args) {
        System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring.xml");
        Main bean = context.getBean(Main.class);
        bean.call();
    }
    
    public void call() {
        System.out.println("call");
    }

}
```

输出结果：

```
before call
call
```

#### 第二种方式

我们实现MainI接口，SpringAOP将使用JDK动态代理创建代理对象。

```java
@Component
public class Main implements MainI{

    public static void main(String[] args) {
        System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring.xml");
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }
        Main bean = context.getBean(Main.class);
        bean.call();
        
    }

    @Override
    public void call() {
        System.out.println("call");
    }
}
```

输出

```java
main
Exception in thread "main" org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'org.learn.spring.aop.advice.Main' available
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.getBean(DefaultListableBeanFactory.java:351)
```

我们可以看到BeanDefinition中有main这个bean。 报错却显示没有 org.learn.spring.aop.advice.Main 这个类的Bean可用。

我们Debug查看源代码。

org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean()

```java
@Override
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
		throws BeanCreationException {
    ..... // 省略部分代码
	try {
		// Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
		Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
		if (bean != null) {
			return bean;
		}
	}
    ..... // 省略部分代码
}
```

```java
@Nullable
protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
	Object bean = null;
	if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
		// Make sure bean class is actually resolved at this point.
		if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
			Class<?> targetType = determineTargetType(beanName, mbd);
			if (targetType != null) {
				bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName); // BeanPostProcessor处理
				if (bean != null) {
					bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
				}
			}
		}
		mbd.beforeInstantiationResolved = (bean != null);
	}
	return bean;
}
```

```java
@Nullable
protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
	for (BeanPostProcessor bp : getBeanPostProcessors()) {
		if (bp instanceof InstantiationAwareBeanPostProcessor) {
			InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
			Object result = ibp.postProcessBeforeInstantiation(beanClass, beanName);
			if (result != null) {
				return result;
			}
		}
	}
	return null;
}
```

org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessBeforeInstantiation

```java
@Override
public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
	Object cacheKey = getCacheKey(beanClass, beanName);

	if (!StringUtils.hasLength(beanName) || !this.targetSourcedBeans.contains(beanName)) {
		if (this.advisedBeans.containsKey(cacheKey)) {
			return null;
		}
		if (isInfrastructureClass(beanClass) || shouldSkip(beanClass, beanName)) {
			this.advisedBeans.put(cacheKey, Boolean.FALSE);
			return null;
		}
	}
	// Create proxy here if we have a custom TargetSource.
	// Suppresses unnecessary default instantiation of the target bean:
	// The TargetSource will handle target instances in a custom fashion.
	TargetSource targetSource = getCustomTargetSource(beanClass, beanName);
	if (targetSource != null) {
		if (StringUtils.hasLength(beanName)) {
			this.targetSourcedBeans.add(beanName);
		}
		Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(beanClass, beanName, targetSource);
        // 在这里创建了代理对象并返回。我们使用的是JDK动态代理。JDK动态代理创建的对象会实现接口，所以我们需要通过接口（MainI）去获取Bean。
		Object proxy = createProxy(beanClass, beanName, specificInterceptors, targetSource);
		this.proxyTypes.put(cacheKey, proxy.getClass());
		return proxy;
	}
	return null;
}
```

#### 更改Main类

```java
@Component
public class Main implements MainI{

    public static void main(String[] args) {
        System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring.xml");
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }
        Main bean = context.getBean(MainI.class);
        bean.call();
        System.out.println(bean.getClass());
    }

    @Override
    public void call() {
        System.out.println("call");
    }
}
```

输出

```
before call
call
class com.sun.proxy.$Proxy23
```

根据 输出的类信息 **class com.sun.proxy.$Proxy23** 找到源代码。

```java
public final class $Proxy23 extends Proxy implements MainI, SpringProxy, Advised, DecoratingProxy {
```

这是JDK生成的代理类对象。因此我们清楚了为什么无法通过 **context.getBean(Main.class);** 获取到Bean。