## Maven

1. 添加依赖

```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
</dependency>
```

2. 配置 annotationProcessor

每个 maven 模块都需要配置：

```xml
<properties>
    <therapi-runtime-javadoc.version>0.15.0</therapi-runtime-javadoc.version>
    
    <maven-compiler-plugin.version>3.13.0</maven-compiler-plugin.version>
</properties>

<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <version>${maven-compiler-plugin.version}</version>
  <configuration>
    <annotationProcessorPaths>
      <!-- https://springdoc.org/#javadoc-support -->
      <path>
        <groupId>com.github.therapi</groupId>
        <artifactId>therapi-runtime-javadoc-scribe</artifactId>
        <version>${therapi-runtime-javadoc.version}</version>
      </path>
    </annotationProcessorPaths>
  </configuration>
</plugin>
```

3. 配置 spring boot 插件，生成 build.properties

```xml
<plugin>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-maven-plugin</artifactId>
  <executions>
    <execution>
      <goals>
        <goal>repackage</goal>
        <goal>build-info</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

4. 自动装配

```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SPRINGDOC_ENABLED, matchIfMissing = true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class OpenApiConfiguration {
	@Bean
	public OpenAPI customOpenAPI(@Nullable BuildProperties build, Environment env) {
		if (build == null) {
			build = new BuildProperties(new Properties());
		}

		OpenAPI openAPI = new OpenAPI();

		Info info = new Info().title(StringUtils.defaultIfBlank(build.getName(), env.getProperty("spring.application.name")))
			.version(build.getVersion())
			.license(new License().name("Apache 2.0"));
		openAPI.info(info);
		return openAPI;
	}
} 
```

## Gradle

1. 添加依赖

```xml
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0'
```

2. 配置 annotationProcessor

每个 gradle 模块都需要配置：

```xml
    //https://github.com/dnault/therapi-runtime-javadoc?tab=readme-ov-file#gradle
    annotationProcessor 'com.github.therapi:therapi-runtime-javadoc-scribe:0.15.0'
    runtimeOnly "com.github.therapi:therapi-runtime-javadoc:0.15.0"
```

3. 配置 spring boot 插件，生成 build.properties

```xml
springBoot {
    buildInfo()
}
```

4. 自动装配

参考上面。