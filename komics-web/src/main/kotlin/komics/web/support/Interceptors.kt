package komics.web.support

import komics.core.Application
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.wso2.msf4j.Interceptor
import org.wso2.msf4j.Request
import org.wso2.msf4j.Response
import org.wso2.msf4j.ServiceMethodInfo

/**
 * Created by ace on 2016/10/23.
 */

@Component
open class HttpLoggingInterceptor : Interceptor {
    private val LOGGER: Logger = LoggerFactory.getLogger(HttpLoggingInterceptor::class.java)

    override fun postCall(request: Request, status: Int, serviceMethodInfo: ServiceMethodInfo) {
    }

    /**
     * 打印 HTTP 日志
     */
    override fun preCall(request: Request, responder: Response, serviceMethodInfo: ServiceMethodInfo): Boolean {
        val logEnabled = Application.conf.bool("http.logging.enabled") ?: false
        if (logEnabled) {
            if (LOGGER.isInfoEnabled) {
                LOGGER.info("Incoming HTTP Request: ${request.uri}, Content-Type: ${request.contentType}")
                LOGGER.info("HTTP Request Processor: ${serviceMethodInfo.methodName}")
                LOGGER.info("HTTP Request Headers:")
                request.headers.entries.forEach { LOGGER.info(" >${it.key}: ${it.value}") }

                LOGGER.info("HTTP Message Body:")
                request.properties.forEach {
                    LOGGER.info(" >${it.key}=${it.value}")
                }
            } else println("Please set logger level to 'INFO' to print HTTP logging")
        }
        return true
    }
}