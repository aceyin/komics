package komics.data.jdbc.listener

import komics.ApplicationListener
import komics.ConfKeys
import komics.core.Application
import komics.data.jdbc.Sql
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Closeable
import javax.sql.DataSource

class JdbcInitializer : ApplicationListener {
    val LOGGER: Logger = LoggerFactory.getLogger(JdbcInitializer::class.java)

    /**
     * 对数据库资源进行释放
     */
    override fun preShutdown(application: Application) {
        val datasource = Application.context.getBeansOfType(DataSource::class.java)
        datasource?.entries?.forEach {
            val bean = it.value
            if (bean is Closeable) {
                LOGGER.info("Closing datasource '${it.key}' ...")
                bean.close()
            }
        }
    }


    /**
     * 根据配置文件中是否有 datasource 的配置，来初始化数据库相关的功能。
     * 如果 application.yml 中有配置一个或者多个 datasource 那么：
     * 1. 将这些 datasource 初始化为 spring 的bean
     * 2. 以这些 datasource 为依赖，初始化一个或多个 NamedParameterJdbcTemplate bean
     * 3. 以这些 datasource 为依赖，初始化一个或多个 TransactionManager bean
     * 4. 调用 DeclarativeTransactionConfig 以便支持spring的申明式事务
     */
    override fun postInitialized(application: Application) {
        LOGGER.info("Initializing JDBC related configuration from ${JdbcInitializer::class.java.name}")
        val datasourceConf = Application.conf.ORIGIN.get(ConfKeys.datasource.name)
        if (datasourceConf != null) {
            // 如果发现配置文件中有datasource相关的配置，则初始化datasource和jdbctemplate
            Application.context.addBeanFactoryPostProcessor(DatasourceInitializer())
            // 添加申明式事务配置类
            Application.context.register(DeclarativeTransactionConfig::class.java)
            // 读取SQL配置
            Sql.Config.load(Sql.Config.SQL_FILE)
        }
    }
}