package komics.core.spring

import komics.core.ConfKeys
import komics.core.Config
import komics.core.DataFormatException
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
class DatasourceInitializer(val cfg: Config) : BeanDefinitionRegistryPostProcessor {

    private val LOGGER = LoggerFactory.getLogger(DatasourceInitializer::class.java)
    private val datasourceBeans = mutableMapOf<String, Map<*, *>>()
    private val jdbcTemplateClassName = "org.springframework.jdbc.core.JdbcTemplate"

    /**
     * 从 application.yml 中读取 datasource 配置，将对应的datasource注册到spring
     * TODO enable the init and destroy method binding
     */
    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        LOGGER.debug("Invoking DatasourceInitializer.postProcessBeanDefinitionRegistry ")
        val ds = this.cfg.ORIGIN[ConfKeys.datasource.name] ?: return

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
                    this.datasourceBeans.put(datasourceBeanName, it)

                    // register jdbc template and set the dependency
                    val jdbcTplName = "${datasourceBeanName}JdbcTemplate"
                    registerBeanDefinition(registry, jdbcTplName, jdbcTemplateClassName)
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
                beanFactory.getBean("${datasourceName}JdbcTemplate", bean)
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