#### Java 注解处理器AbstractProcessor在gradle中未生效

引入包使用 annotationProcessor。

asm-demo 这个是注解处理器的jar包

使用annotationProcessor引入jar包即可。（以前是使用APT（annotation-processor-tool）进行引入）

annotationProcessor group: 'org.blog', name:'asm-demo', version: '1.0'

```java
dependencies {
    compile group: 'org.projectlombok', name: 'lombok', version: '1.18.12'
    compile group: 'org.junit.jupiter', name: 'junit-jupiter-api', version: '5.6.2'
    compileOnly group: 'org.blog', name:'asm-demo', version: '1.0'
    compile files("$jdkHome/lib/tools.jar")
    annotationProcessor group: 'org.blog', name:'asm-demo', version: '1.0'
}
```


注：
= _ = ||    第一次使用gradle，就遇上了这个， 找了好久才找到解决方案。