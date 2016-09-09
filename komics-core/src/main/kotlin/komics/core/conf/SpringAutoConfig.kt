package komics.core.conf

import com.alibaba.druid.pool.DruidDataSource
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor
import komics.util.StrUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import redis.clients.jedis.JedisPoolConfig
import javax.sql.DataSource

@Configuration
open class SpringAutoConfig {
    private val LOGGER = LoggerFactory.getLogger(SpringAutoConfig::class.java)

    @Value("\${database.datasource.url}")
    private val url: String? = null
    @Value("\${database.datasource.username}")
    private val username: String? = null
    @Value("\${database.datasource.password}")
    private val password: String? = null
    @Value("\${database.datasource.minIdle}")
    private val minIdle: Int = 0
    @Value("\${database.datasource.initialSize}")
    private val initialSize: Int = 0
    @Value("\${database.datasource.maxActive}")
    private val maxActive: Int = 0
    @Value("\${database.datasource.maxWait}")
    private val maxWait: Int = 0
    @Value("\${database.datasource.testWhileIdle}")
    private val testWhileIdle: Boolean = false
    @Value("\${database.datasource.timeBetweenEvictionRunsMillis}")
    private val timeBetweenEvictionRunsMillis: Int = 0
    @Value("\${database.datasource.minEvictableIdleTimeMillis}")
    private val minEvictableIdleTimeMillis: Int = 0
    @Value("\${database.datasource.testOnBorrow}")
    private val testOnBorrow: Boolean = false
    @Value("\${database.datasource.testOnReturn}")
    private val testOnReturn: Boolean = false
    @Value("\${database.datasource.poolPreparedStatements}")
    private val poolPreparedStatements: Boolean = false
    @Value("\${database.datasource.maxPoolPreparedStatementPerConnectionSize}")
    private val maxPoolPreparedStatementPerConnectionSize: Int = 0
    @Value("\${database.datasource.filters}")
    private val filters: String? = null
    @Value("\${database.datasource.validationQuery}")
    private val validationQuery: String? = null


    /**
     * 根据配置文件决定是否开启数据源配置。
     * 注: 本框架为一个最佳实践组合, 因此只支持目前评价比较好的一些开源框架。
     * 例如数据源只支持阿里巴巴的 DruidDataSource。
     */
    @Bean(initMethod = "init", destroyMethod = "close")
    open fun dataSource(): DataSource? {
        if (StrUtil.notNull(url, username)) {
            LOGGER.info("Found datasource configuration, creating the datasource")
            val dataSource = DruidDataSource()
            dataSource.url = url
            dataSource.username = username
            dataSource.password = password
            dataSource.minIdle = if (minIdle <= 0) 1 else minIdle
            dataSource.initialSize = if (initialSize <= 0) 1 else initialSize
            dataSource.maxActive = if (maxActive <= 0) 1 else maxActive
            dataSource.maxWait = (if (maxWait <= 0) 60000 else maxWait).toLong()
            dataSource.isTestWhileIdle = testWhileIdle
            dataSource.timeBetweenEvictionRunsMillis = (if (timeBetweenEvictionRunsMillis <= 0) 60000 else timeBetweenEvictionRunsMillis).toLong()
            dataSource.minEvictableIdleTimeMillis = (if (minEvictableIdleTimeMillis <= 0) 300000 else minEvictableIdleTimeMillis).toLong()
            dataSource.isTestOnBorrow = testOnBorrow
            dataSource.isTestOnReturn = testOnReturn
            dataSource.isPoolPreparedStatements = poolPreparedStatements
            dataSource.maxPoolPreparedStatementPerConnectionSize = if (maxPoolPreparedStatementPerConnectionSize <= 0) 20 else maxPoolPreparedStatementPerConnectionSize
            dataSource.validationQuery = validationQuery ?: "SELECT 'x'"
            return dataSource
        }
        return null
    }

    /**
     * 根据配置决定是否开启事务。
     * 默认开启事务模式。
     * 如果数据源有配置,则同时配置事务。
     */
    @Bean
    open fun dataSourceProxy(dataSource: DataSource?): TransactionAwareDataSourceProxy? {
        if (dataSource != null) {
            LOGGER.info("Creating datasource proxy ...")
            val proxy = TransactionAwareDataSourceProxy(dataSource)
            return proxy
        }
        return null
    }

    /**
     * 根据配置文件决定是否开启事务支持。
     * 默认支持事务。
     */
    @Bean
    open fun transactionManager(dataSource: DataSource?): DataSourceTransactionManager? {
        if (dataSource != null) {
            LOGGER.info("Create transaction manager ...")
            val transactionManager = DataSourceTransactionManager(dataSource)
            return transactionManager
        }
        return null
    }

    @Bean
    open fun jedisPoolConfig(): JedisPoolConfig? {
        //TODO 根据配置决定是否开启jedis配置
        return null
    }

    @Bean
    open fun databaseMonitor(): DruidStatInterceptor? {
        //TODO 根据配置文件决定是否开启数据源监控
        return null
    }
}