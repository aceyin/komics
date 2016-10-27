package komics.core.spring

import komics.core.Application
import org.springframework.core.io.ClassPathResource
import org.springframework.util.Log4jConfigurer
import java.io.File

/**
 * Created by ace on 2016/10/24.
 */

internal object LogInitializer {

    private val defaultConf = "conf/log4j.properties"

    fun initLog4J() {
        val conf = Application.conf.str("log4j.configuration.file")
        var path = ""
        if (conf.isNullOrEmpty()) {
            path = getFromClasspath()
        } else {
            val file = File(conf)
            if (file.exists() && file.isFile) path = file.path
        }
        if (path != "")
            Log4jConfigurer.initLogging(path)
    }

    private fun getFromClasspath(): String {
        val cp = ClassPathResource(defaultConf)
        if (cp.exists()) {
            return cp.file.path
        }
        return ""
    }
}