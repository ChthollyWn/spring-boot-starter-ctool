# CTool工具集合

![Logo](logo%20(3).png)

CTool是一个综合性工具集合。目的在于通过封装和集成多种工具和库，简化Spring应用的开发、管理和维护过程。

（仅供作者学习之用）

## 目录

- [安装](#安装)
- [使用](#使用)
- [功能](#功能)

## 安装

直接在pom.xml文件中引入依赖即可
```xml
<dependency>
  <groupId>xyz.chthollywn.ctool</groupId>
  <artifactId>spring-boot-starter-ctool</artifactId>
  <version>1.0.0</version>
</dependency>
```

## 使用

在安装完依赖后，请先继承并装配CToolAutoConfigure配置类，以方便spring容器管理相关的bean
```java
@Configuration
public class DemoConfigure extends CToolAutoConfiguration {
    // 其他需要装配的bean
    // ...
}
```

## 功能

- 注解式日志工具
  - 自动配置打印接口进出参数
  - 方法层级的注解式计时器
  - 自定义日志输出策略 TODO
- TODO

作者很菜，其他功能待开发。。。