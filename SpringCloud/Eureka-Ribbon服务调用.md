在上一篇中已经完成了应用向注册中心Eureka中注册的功能。

这篇将讲解Eureka使用Ribbon实现服务调用。

定义了两个服务 **shop-service-learn** 和 **user-service-learn** 

#### 消费者 shop-service-learn

```java
@Configuration
public class ApplicationConfig {
    @Bean
    @LoadBalanced   // 负载均衡
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
```

```java
@RestController
public class ShopController {
    
    private String USER_SERVICE_URL = "http://user-service-learn";

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("getUser")
    public String getUser() {
        return restTemplate.getForObject(USER_SERVICE_URL + "/getUser", String.class);
    }
}
```

#### 提供者 user-service-learn

```java
@RestController
public class UserController {

    @Value("${eureka.instance.instance-id}")
    private String port;

    @GetMapping("getUser")
    public String getUser() {
        System.out.println(port);
        return "user:"+port;
    }
}
```

配置

```yaml
server:
  port: 0        #自动分配端口
spring:
  application:
    name: user-service-learn
eureka:
  client:
    fetch-registry: true
    register-with-eureka: true
    service-url:
      defaultZone: http://localhost:8700/eureka
  instance:
    instance-id: user-service-learn${random.long}   # 这是每个Eureka客户端的ID。
    prefer-ip-address: true                         # 显示真实的IP地址
```



代码见：https://github.com/shenhuaxin/spring-cloud-learn.git

Branch: eureka-ribbon-service-invoke

#### 负载均衡策略
![IRule](https://user-images.githubusercontent.com/23735480/83317767-b5921a80-a261-11ea-8146-482525d91cc3.png)

默认的ILoadBalancer是ZoneAwareLoadBalancer。
默认的IRule是ZoneAvoidanceRule。

在spring.factories中引入了

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration
```

在RibbonAutoConfiguration中引入了SpringClientFactory。

```java
@Bean
public SpringClientFactory springClientFactory() {
	SpringClientFactory factory = new SpringClientFactory();
	factory.setConfigurations(this.configurations);
	return factory;
}
```
在SpringClientFactory中引入了RibbonClientConfiguration。
```java
public SpringClientFactory() {
	super(RibbonClientConfiguration.class, NAMESPACE, "ribbon.client.name");
}
```
在RibbonClientConfiguration中引入了ZoneAwareLoadBalancer和ZoneAvoidanceRule。
```java
@Bean
@ConditionalOnMissingBean
public ILoadBalancer ribbonLoadBalancer(IClientConfig config,
		ServerList<Server> serverList, ServerListFilter<Server> serverListFilter,
		IRule rule, IPing ping, ServerListUpdater serverListUpdater) {
	if (this.propertiesFactory.isSet(ILoadBalancer.class, name)) {
		return this.propertiesFactory.get(ILoadBalancer.class, config, name);
	}
	return new ZoneAwareLoadBalancer<>(config, rule, ping, serverList,
			serverListFilter, serverListUpdater);
}

@Bean
@ConditionalOnMissingBean
public IRule ribbonRule(IClientConfig config) {
	if (this.propertiesFactory.isSet(IRule.class, name)) {
		return this.propertiesFactory.get(IRule.class, config, name);
	}
	ZoneAvoidanceRule rule = new ZoneAvoidanceRule();
	rule.initWithNiwsConfig(config);
	return rule;
}
```
具体均衡策略解释请见： https://juejin.im/entry/58f4a0ee8d6d810057b9d72a



#### 源码分析

通过Debug的方式查看源码：在代码 `restTemplate.getForObject(USER_SERVICE_URL + "/getUser", String.class);` 上打上断点， 然后进行调试。

1. 通过Debug可以看到`RestTemplate#doExecute` 中有一行 `response = request.execute();`， 在这里进行了Request的调用。

2. 继续执行 -> ` AbstractClientHttpRequest#execute` 中的`ClientHttpResponse result = executeInternal(this.headers);`

3. 继续执行 -> `AbstractBufferingClientHttpRequest#executeInternal(org.springframework.http.HttpHeaders)`的 `ClientHttpResponse result = executeInternal(headers, bytes);` 。 
4. 继续执行 -> `InterceptingClientHttpRequest#executeInternal`的 `return requestExecution.execute(this, bufferedOutput);`

5. 继续执行 -> `InterceptingClientHttpRequest.InterceptingRequestExecution#execute` 的 `return nextInterceptor.intercept(request, body, this);`

6. 继续执行 -> `LoadBalancerInterceptor#intercept` 的 `return this.loadBalancer.execute(serviceName,this.requestFactory.createRequest(request, body, execution));`
7.  继续执行 -> `RibbonLoadBalancerClient#execute(String,LoadBalancerRequest<T>,Object)` 的 `Server server = getServer(loadBalancer, hint);`

到了第七步， 我们终于看到了LoadBalance选择出来的主机地址。

**ZoneAwareLoadBalancer**

```java
public Server chooseServer(Object key) {
        if (!ENABLED.get() || getLoadBalancerStats().getAvailableZones().size() <= 1) {
            logger.debug("Zone aware logic disabled or there is only one zone");
            // 未开启区域感知负载均衡 或者 只有一个可用分区， 调用父类的chooseServer方法。
            return super.chooseServer(key);
        }
        Server server = null;
        try {
            LoadBalancerStats lbStats = getLoadBalancerStats();
            Map<String, ZoneSnapshot> zoneSnapshot = ZoneAvoidanceRule.createSnapshot(lbStats);
            logger.debug("Zone snapshots: {}", zoneSnapshot);
            if (triggeringLoad == null) {
                triggeringLoad = DynamicPropertyFactory.getInstance().getDoubleProperty(
                        "ZoneAwareNIWSDiscoveryLoadBalancer." + this.getName() + ".triggeringLoadPerServerThreshold", 0.2d);
            }

            if (triggeringBlackoutPercentage == null) {
                triggeringBlackoutPercentage = DynamicPropertyFactory.getInstance().getDoubleProperty(
                        "ZoneAwareNIWSDiscoveryLoadBalancer." + this.getName() + ".avoidZoneWithBlackoutPercetage", 0.99999d);
            }
            Set<String> availableZones = ZoneAvoidanceRule.getAvailableZones(zoneSnapshot, triggeringLoad.get(), triggeringBlackoutPercentage.get());
            logger.debug("Available zones: {}", availableZones);
            if (availableZones != null &&  availableZones.size() < zoneSnapshot.keySet().size()) {
                String zone = ZoneAvoidanceRule.randomChooseZone(zoneSnapshot, availableZones);
                logger.debug("Zone chosen: {}", zone);
                if (zone != null) {
                    BaseLoadBalancer zoneLoadBalancer = getLoadBalancer(zone);
                    server = zoneLoadBalancer.chooseServer(key);
                }
            }
        } catch (Exception e) {
            logger.error("Error choosing server using zone aware logic for load balancer={}", name, e);
        }
        if (server != null) {
            return server;
        } else {
            logger.debug("Zone avoidance logic is not invoked.");
            return super.chooseServer(key);
        }
    }
```

**BaseLoadBalancer**   

查看父类中chooserServer的实现。可以看到

```java
public Server chooseServer(Object key) {
        if (counter == null) {
            counter = createCounter();
        }
        counter.increment();
        if (rule == null) {
            return null;
        } else {
            try {
                return rule.choose(key);
            } catch (Exception e) {
                logger.warn("LoadBalancer [{}]:  Error choosing server for key {}", name, key, e);
                return null;
            }
        }
    }
```

**RandomRule**   可以看到RandomRule是如何选择Server的。

```java
public Server choose(ILoadBalancer lb, Object key) {
        if (lb == null) {
            return null;
        }
        Server server = null;
        while (server == null) {
            if (Thread.interrupted()) {
                return null;
            }
            List<Server> upList = lb.getReachableServers();
            List<Server> allList = lb.getAllServers();

            int serverCount = allList.size();
            if (serverCount == 0) {
                /*
                 * No servers. End regardless of pass, because subsequent passes
                 * only get more restrictive.
                 */
                return null;
            }
            int index = chooseRandomInt(serverCount);
            server = upList.get(index);
            if (server == null) {
                /*
                 * The only time this should happen is if the server list were
                 * somehow trimmed. This is a transient condition. Retry after
                 * yielding.
                 */
                Thread.yield();
                continue;
            }
            if (server.isAlive()) {
                return (server);
            }
            // Shouldn't actually happen.. but must be transient or a bug.
            server = null;
            Thread.yield();
        }
        return server;
    }
```



#### ServerListFilter

在 ``DynamicServerListLoadBalancer`` 类中还可以看到一个成员变量。

```java
volatile ServerListFilter<T> filter;
```

```java
/**
 * This interface allows for filtering the configured or dynamically obtained
 * List of candidate servers with desirable characteristics.
 */
public interface ServerListFilter<T extends Server> {

    public List<T> getFilteredListOfServers(List<T> servers);

}
```



![ServerListFilter](https://user-images.githubusercontent.com/23735480/83321304-4fb58b00-a281-11ea-8f8b-af511fc47047.png)

通过介绍可以， 该接口可以过滤出符合条件的Server集合。那么这个filter是怎么使用的呢。

```java
  public void updateListOfServers() {
        List<T> servers = new ArrayList<T>();
        if (serverListImpl != null) {
            servers = serverListImpl.getUpdatedListOfServers();
            LOGGER.debug("List of Servers for {} obtained from Discovery client: {}",
                    getIdentifier(), servers);

            if (filter != null) {
                servers = filter.getFilteredListOfServers(servers);  // 过滤出符合条件的servers, 并赋值。
                LOGGER.debug("Filtered List of Servers for {} obtained from Discovery client: {}",
                        getIdentifier(), servers);
            }
        }
        updateAllServerList(servers);
    }
```

在通过查找可以看出 **updateListOfServers** 在两个地方使用到了。

```java
protected final ServerListUpdater.UpdateAction updateAction = new ServerListUpdater.UpdateAction() {
        @Override
        public void doUpdate() {
            updateListOfServers();   // 第一处， 定时任务，定时更新。
        }
    };
```

```java
void restOfInit(IClientConfig clientConfig) {
        boolean primeConnection = this.isEnablePrimingConnections();
        // turn this off to avoid duplicated asynchronous priming done in BaseLoadBalancer.setServerList()
        this.setEnablePrimingConnections(false);
        enableAndInitLearnNewServersFeature();

        updateListOfServers();     // 第二处， 初始化时更新。
        if (primeConnection && this.getPrimeConnections() != null) {
            this.getPrimeConnections()
                    .primeConnections(getReachableServers());
        }
        this.setEnablePrimingConnections(primeConnection);
        LOGGER.info("DynamicServerListLoadBalancer for client {} initialized: {}", clientConfig.getClientName(), this.toString());
    }
```

我们再来看一下 **updateAction** 的实现。

```java
public void enableAndInitLearnNewServersFeature() {
        LOGGER.info("Using serverListUpdater {}", serverListUpdater.getClass().getSimpleName());
        serverListUpdater.start(updateAction);     // 这里对action进行了Start
}
```

继续追踪 **serverListUpdater** 的实现, 可以看到 **ServerListUpdater** 有两个实现类。都在Start方法中使用了线程池和定时任务不断更新Server的信息。



**updateListOfServers** 的两处的调用也分别对应了`ServerListFilter`接口上的注释的解释。

```
This interface allows for filtering the configured or dynamically obtained List of candidate servers with desirable characteristics.
```





通过对负载均衡代码的阅读， 可以得知整个负载均衡需要三个接口配合，才能完成对Server的选择。

`ILoadBalancer` :    RestTemplate中的拦截器调用`ILoadBalancer` 接口。

`IRule`  ：                 封装在BaseLoadBalancer接口中， 提供从可选择的ServerList中选择一个Server。（如何选择根据`choose`方法的实现）

`ServerListFilter`： 根据Config或者动态地选择符合条件的ServerList。定时更新客户端的ServerList。