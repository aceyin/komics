package komics.data.listener

import komics.ConfKeys
import komics.core.Application
import komics.exception.DataFormatException
import org.apache.commons.beanutils.BeanUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.beans.factory.support.RootBeanDefinition
import java.util.*
import javax.sql.DataSource

/**
 * Spring 的 BeanFactoryPostProcessor
 * 用来动态创建 在 application.yml 中配置的 datasource
 */
internal class DatasourceInitializer : BeanDefinitionRegistryPostProcessor {

    private val LOGGER = LoggerFactory.getLogger(DatasourceInitializer::class.java)
    private val datasourceBeans = mutableMapOf<String, Map<*, *>>()
    private val jdbcTemplateClassName = "org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate"
    private val transactionManagerClassName = "org.springframework.jdbc.datasource.DataSourceTransactionManager"
    private val jdbcTemplateBeanNameSuffix = "JdbcTemplate"
    private val transactionManagerBeanNameSuffix = "TransManager"

    /**
     * 从 application.yml 中读取 datasource 配置，将对应的datasource注册到spring
     * TODO enable the init and destroy method binding
     */
    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        LOGGER.info("Calling DatasourceInitializer.postProcessBeanDefinitionRegistry ")
        val ds = Application.Config.ORIGIN[ConfKeys.datasource.name] ?: return

        if (ds is ArrayList<*>) {
            ds.forEach { it ->
                if (it is HashMap<*, *>) {
                    val name = it.remove("name")
                    val clazz = it.remove("class")
                    var datasourceBeanName = if (name is String) name else "defaultDataSource"

                    // register bean
                    if (clazz !is String) throw DataFormatException("Class parameter is invalid datasource $datasourceBeanName")

                    // register datasource
                    registerBeanDefinition(registry, datasourceBeanName, clazz)
                    LOGGER.info("Register datasource bean $name -> $clazz")
                    this.datasourceBeans.put(datasourceBeanName, it)

                    // register jdbc template and set the dependency
                    val jdbcTemplateBeanName = "${datasourceBeanName}_$jdbcTemplateBeanNameSuffix"
                    registerBeanDefinition(registry, jdbcTemplateBeanName, jdbcTemplateClassName)
                    LOGGER.info("Register JdbcTemplate bean $jdbcTemplateBeanName -> $jdbcTemplateClassName")

                    // register transaction manager
                    val transactionManagerBeanName = "${datasourceBeanName}_$transactionManagerBeanNameSuffix"
                    registerBeanDefinition(registry, transactionManagerBeanName, transactionManagerClassName)
                    LOGGER.info("Register TransactionManager bean $transactionManagerBeanName -> $transactionManagerClassName")
                }
            }
        }
    }

    /**
     * 创建bean实例
     */
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        datasourceBeans.entries.forEach { b ->
            val datasourceName = b.key
            val bean = beanFactory.getBean(datasourceName) ?: LOGGER.warn("No datasource bean found with name '$datasourceName'")
            if (bean is DataSource) {
                BeanUtils.populate(bean, b.value)
                // 初始化JdbcTemplate实例，传入datasource构造函数
                val jdbcTemplateBeanName = "${datasourceName}_$jdbcTemplateBeanNameSuffix"
                beanFactory.getBean("$jdbcTemplateBeanName", bean)
                LOGGER.info("Initialize JdbcTemplate bean $jdbcTemplateBeanName with datasource $datasourceName")
                // 初始化transactionManager实例，传入datasource构造函数
                val transactionManagerBeanName = "${datasourceName}_$transactionManagerBeanNameSuffix"
                beanFactory.getBean(transactionManagerBeanName, bean)
                LOGGER.info("Initialize TransactionManager bean $transactionManagerBeanName with datasource $datasourceName")
            }
        }
    }

    private fun registerBeanDefinition(registry: BeanDefinitionRegistry, beanName: String, className: String): RootBeanDefinition {
        val beanClass = Class.forName(className) ?: throw RuntimeException("Error while register bean $beanName for class $className")

        registry.registerBeanDefinition(beanName, RootBeanDefinition(beanClass).apply {
            targetType = beanClass
            role = BeanDefinition.ROLE_APPLICATION
        })
        return registry.getBeanDefinition(beanName) as RootBeanDefinition
    }

}