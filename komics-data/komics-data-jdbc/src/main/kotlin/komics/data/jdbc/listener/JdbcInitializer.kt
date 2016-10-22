package komics.data.listener

import komics.AppInitListener
import komics.ConfKeys
import komics.core.Application
import komics.data.jdbc.Sql
import komics.prototype.DeclarativeTransactionConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by ace on 2016/10/16.
 */
class JdbcInitializer : AppInitListener {
    val LOGGER: Logger = LoggerFactory.getLogger(JdbcInitializer::class.java)

    override fun postInitialized(application: Application) {
        LOGGER.info("Running JdbcInitializer.postInitialized")
        val datasourceConf = Application.Config.ORIGIN.get(ConfKeys.datasource.name)
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