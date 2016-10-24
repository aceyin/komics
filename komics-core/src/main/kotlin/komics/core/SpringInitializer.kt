package komics.core

import org.springframework.beans.BeanUtils
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.core.env.SimpleCommandLinePropertySource

/**
 * Created by ace on 2016/10/24.
 */

internal class SpringInitializer() {
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

        val configClasses = Application.conf.strs("spring.configurationClasses")
        val pkgScan = Application.conf.strs("spring.packageScan")

        val context = createApplicationContext()
        configApplicationContext(context, configClasses, pkgScan)

        with(context) {
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


    /**
     * 配置 Spring context
     * @param context
     * @param confClass
     * @param pkgscan
     */
    private fun configApplicationContext(context: AnnotationConfigApplicationContext, confClass: List<String>, pkgscan: List<String>) {
        val classes = Array(confClass.size) { Class.forName(confClass[it]) }
        if (classes.size > 0) context.register(*classes)
        context.scan(*pkgscan.toTypedArray())
    }

}