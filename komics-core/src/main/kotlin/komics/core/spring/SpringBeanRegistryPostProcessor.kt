package komics.core.spring

import com.avaje.ebean.EbeanServerFactory
import com.avaje.ebean.config.ServerConfig
import komics.core.Config
import komics.core.ConfigException
import komics.core.DataFormatException
import komics.core.FrameworkConfKeys
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
 * Spring 的 BeanFactoryPostProcessor, 用来动态创建 在 application.yml 中配置的 datasource 等 spring beans
 */
class SpringBeanRegistryPostProcessor(cfg: Config) : BeanDefinitionRegistryPostProcessor {

    private val LOGGER = LoggerFactory.getLogger(SpringBeanRegistryPostProcessor::class.java)
    private val config: Config = cfg
    private val beanProps = mutableMapOf<String, Map<*, *>>()

    /**
     * 配置bean的属性
     */
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        configBeanProperties(beanFactory)
        initialEbeanServer(beanFactory)
    }

    /**
     * 注册 application.yml 中定义的 bean, 如 datasource 等
     */
    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        LOGGER.debug("Invoking SpringBeanRegistryPostProcessor.postProcessBeanDefinitionRegistry ")
        // TODO bind init and close method
        initializeDataSources(registry)
    }

    private fun configBeanProperties(beanFactory: ConfigurableListableBeanFactory) {
        beanProps.entries.forEach { it ->
            val bean = beanFactory.getBean(it.key)
            if (bean == null) {
                LOGGER.warn("No bean '${it.key}' found, please check if it is configured properly !")
            } else {
                try {
                    BeanUtils.populate(bean, it.value)
                    LOGGER.debug("Set properties for bean ${it.key} success")
                } catch (e: Exception) {
                    LOGGER.error("Error while set properties for bean ${it.key}", e)
                }
            }
        }
    }

    /**
     * 初始化ebean配置
     */
    private fun initialEbeanServer(beanFactory: ConfigurableListableBeanFactory) {
        val ebean = this.config.ORIGIN[FrameworkConfKeys.ebean.name] ?: return
        if (ebean !is ArrayList<*>) throw ConfigException("Ebean configuration should be a list")

        ebean.forEach { it ->
            if (it !is HashMap<*, *>)
                throw ConfigException("Each 'ebean' configuration item should be a map")

            val servername = if (it["name"] is String) it["name"] as String else "ebean-server-default"
            val dsname = it["datasource"]
            if (dsname !is String) throw ConfigException("Datasource not properly configured for ebean '$servername'")

            val ds = beanFactory.getBean(dsname) ?: throw ConfigException("No bean found '$dsname'")

            val def = if (it["default"] is String) (it["default"] as String).toBoolean() else false

            EbeanServerFactory.create(ServerConfig().apply {
                name = servername
                dataSource = ds as DataSource
                isDefaultServer = def
            })
        }
    }

    private fun initializeDataSources(registry: BeanDefinitionRegistry) {
        val ds = this.config.ORIGIN[FrameworkConfKeys.datasource.name] ?: return

        if (ds is ArrayList<*>) {
            ds.forEach { it ->
                if (it !is HashMap<*, *>) throw ConfigException("Datasource items should be a map")

                val name = it.remove("name")
                val clazz = it.remove("class")
                var beanName = if (name is String) name else "defaultDataSource"

                // register bean
                if (clazz !is String) throw DataFormatException("Class parameter is invalid datasource $beanName")

                registerBeanDefinition(registry, beanName, clazz)
                this.beanProps.put(beanName, it)
            }
        }
    }

    private fun registerBeanDefinition(registry: BeanDefinitionRegistry, beanName: String, className: String) {
        val beanClass = Class.forName(className) ?: throw RuntimeException("Error while register bean $beanName for class $className")

        registry.registerBeanDefinition(beanName, RootBeanDefinition(beanClass).apply {
            targetType = beanClass
            role = BeanDefinition.ROLE_APPLICATION
        })
    }

}