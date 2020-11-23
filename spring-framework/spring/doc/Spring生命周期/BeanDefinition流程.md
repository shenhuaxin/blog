### 1. 加载XML配置文件

#### 1.1 保存配置文件路径
```java
public ClassPathXmlApplicationContext(
			String[] configLocations, boolean refresh, @Nullable ApplicationContext parent)
			throws BeansException {
		super(parent);
        // 保存配置文件
		setConfigLocations(configLocations);
		if (refresh) {
            // 刷新IOC容器
			refresh();
		}
	}

public void setConfigLocations(@Nullable String... locations) {
		if (locations != null) {
			Assert.noNullElements(locations, "Config locations must not be null");
			this.configLocations = new String[locations.length];
			for (int i = 0; i < locations.length; i++) {
				this.configLocations[i] = resolvePath(locations[i]).trim();
			}
		}
		else {
			this.configLocations = null;
		}
	}
```

#### 1.2 加载配置文件并解析
```java
    @Override
	public void refresh() throws BeansException, IllegalStateException {
		synchronized (this.startupShutdownMonitor) {
			// Prepare this context for refreshing.
			prepareRefresh();

			// 解析配置文件、注册Bean，刷新BeanFactory, 这一步将XML解析做完，并获得了BeanFactory.
			ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
            ......
        }
    }

```

### 2.加载注解配置类

#### 2.1 BeanDefinitionRegistryPostProcessor的调用时机
```java
public void refresh() throws BeansException, IllegalStateException {
    synchronized (this.startupShutdownMonitor) {
        // ......
        try {
            postProcessBeanFactory(beanFactory);
            // 5. 执行BeanFactoryPostProcessor
            invokeBeanFactoryPostProcessors(beanFactory);
            registerBeanPostProcessors(beanFactory);
            // ......
        }
        // catch finally .....
    }
}
```





### 参考资料
1. https://juejin.cn/book/6857911863016390663/section/6867689993351987207