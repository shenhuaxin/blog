## JPA

#### Springboot如何实现自动扫描Repository


1. 在spring-boot-autoconfigure中找到JpaRepositoriesAutoConfiguration。
2. 在JpaRepositoriesAutoConfiguration使用@Import引入了JpaRepositoriesRegistrar.class。
3. 在JpaRepositoriesRegistrar的父类AbstractRepositoryConfigurationSourceSupport中使用了registerBeanDefinitions()。
4. 在registerBeanDefinitions方法中委托给RepositoryConfigurationDelegate类的registerRepositoriesIn方法去注册Bean。
5. 在registerRepositoriesIn方法中。使用下列代码加载JPA的Repository。
```java
Collection<RepositoryConfiguration<RepositoryConfigurationSource>> configurations = extension
		.getRepositoryConfigurations(configurationSource, resourceLoader, inMultiStoreMode);
```
6. 在RepositoryConfigurationExtensionSupport的getRepositoryConfigurations方法中使用configSource.getCandidates继续加载Repository。
```java
for (BeanDefinition candidate : configSource.getCandidates(loader))
```
7. 在RepositoryConfigurationSourceSupport中getCandidates方法中对扫描到的所有的Class文件进行匹配。使用RepositoryComponentProvider进行过滤。
在RepositoryComponentProvider构造函数中，默认添加了两个IncludeFilter和一个ExcludeFilter。
```java
public RepositoryComponentProvider(Iterable<? extends TypeFilter> includeFilters, BeanDefinitionRegistry registry) {
	super(false);
	Assert.notNull(includeFilters, "Include filters must not be null!");
	Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");
	this.registry = registry;
    // 没有指定IncludeFilter， 则默认添加两个IncludeFilter。
	if (includeFilters.iterator().hasNext()) {
		for (TypeFilter filter : includeFilters) {
			addIncludeFilter(filter);
		}
	} else {
        //注册继承了Repository的类， 和被RepositoryDefinition注解修饰的类。
		super.addIncludeFilter(new InterfaceTypeFilter(Repository.class));
		super.addIncludeFilter(new AnnotationTypeFilter(RepositoryDefinition.class, true, true));
	}
    // 默认添加一个ExcludeFilter。 不注册被NoRepositoryBean注解修饰的类。
	addExcludeFilter(new AnnotationTypeFilter(NoRepositoryBean.class));
}
```
8. 在第7步，我们看到了JpaRepositoriesAutoConfiguration自动注册Bean的匹配条件。在第8步，将看到JpaRepositoriesAutoConfiguration将符合条件的Bean
注册到IOC容器中。
```java
public List<BeanComponentDefinition> registerRepositoriesIn(BeanDefinitionRegistry registry,
			RepositoryConfigurationExtension extension) {
    // ....省略
	watch.start();
    // 这里将开始扫描符合条件的Bean。 
	Collection<RepositoryConfiguration<RepositoryConfigurationSource>> configurations = extension
			.getRepositoryConfigurations(configurationSource, resourceLoader, inMultiStoreMode);

	Map<String, RepositoryConfiguration<?>> configurationsByRepositoryName = new HashMap<>(configurations.size());

	for (RepositoryConfiguration<? extends RepositoryConfigurationSource> configuration : configurations) {

		configurationsByRepositoryName.put(configuration.getRepositoryInterface(), configuration);

		BeanDefinitionBuilder definitionBuilder = builder.build(configuration);

		extension.postProcess(definitionBuilder, configurationSource);

		if (isXml) {
			extension.postProcess(definitionBuilder, (XmlRepositoryConfigurationSource) configurationSource);
		} else {
			extension.postProcess(definitionBuilder, (AnnotationRepositoryConfigurationSource) configurationSource);
		}

		AbstractBeanDefinition beanDefinition = definitionBuilder.getBeanDefinition();
		beanDefinition.setResourceDescription(configuration.getResourceDescription());

		String beanName = configurationSource.generateBeanName(beanDefinition);

		if (LOG.isTraceEnabled()) {
			LOG.trace(REPOSITORY_REGISTRATION, extension.getModuleName(), beanName, configuration.getRepositoryInterface(),
					configuration.getRepositoryFactoryBeanClassName());
		}

		beanDefinition.setAttribute(FACTORY_BEAN_OBJECT_TYPE, configuration.getRepositoryInterface());
		// 在这里， 将扫描到的Bean注册到IOC容器中。
		registry.registerBeanDefinition(beanName, beanDefinition);
		definitions.add(new BeanComponentDefinition(beanDefinition, beanName));
	}

	potentiallyLazifyRepositories(configurationsByRepositoryName, registry, configurationSource.getBootstrapMode());
	// ... 省略
	return definitions;
}

```