# komics
## a simple web framework
- programming language: kotlin
- framework integrated:
    - MSF4J
    - JdbcTemplate
    - Spring

- 支持多个数据源
- 自动初始化NamedParameterJdbcTemplate和TransactionManager

## TODO
- 热加载配置文件
- 默认的以及自定义的 exception handler
- form validation
- web层基本的功能封装，如 Response 的封装等
- 自定义拦截器配置
- 集成Shiro，自动化实现权限校验
- DB 支持 Join query，一次查询多个对象
- One to One, One to Many, Many to Many的支持
- HTTP 日志