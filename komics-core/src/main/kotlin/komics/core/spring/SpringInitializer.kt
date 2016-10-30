package komics.core.spring

import komics.core.Application
import org.springframework.beans.BeanUtils
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.core.env.SimpleCommandLinePropertySource

/**
 * Created by ace on 2016/10/24.
 */

internal object SpringInitializer {
    private val DEFAULT_CONTEXT_CLASS = "org.springframework.context.annotation.AnnotationConfigApplicationContext"

    /**
     * 手动初始化Spring context环境。
     * 主要过程:
     * - 从 application.yml 文件中读取配置
     * - 将所有配置文件保存到 system environment
     * - 解析 configurationClasses
     * - 解析 packageScan
     * - spring会自动添加 SpringAutoConfig 类作为 Configuration class
     */
    fun createAndInitializeContext(): AnnotationConfigApplicationContext {
        val context = createApplicationContext()
        with(context) {
            val pkgScan = Application.conf.strs("spring.packageScan")
            if (pkgScan.isNotEmpty()) scan(*pkgScan.toTypedArray())
            environment.propertySources.addFirst(SimpleCommandLinePropertySource(*Application.args))
            // put the configuration into spring context
            environment.systemProperties.putAll(Application.conf.PROPS)
        }

        return context
    }

    /**
     * 生成 AnnotationConfigApplicationContext 实例
     */
    private fun createApplicationContext(): AnnotationConfigApplicationContext {
        try {
            val clazz = Class.forName(DEFAULT_CONTEXT_CLASS)
            return BeanUtils.instantiate(clazz) as AnnotationConfigApplicationContext
        } catch (ex: ClassNotFoundException) {
            throw IllegalStateException(
                    "Unable to create a default ApplicationContext, please specify an ApplicationContextClass", ex)
        }
    }
}