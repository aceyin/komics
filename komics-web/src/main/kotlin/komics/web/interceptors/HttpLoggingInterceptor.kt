package komics.web.interceptors

import komics.core.Application
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.wso2.msf4j.Interceptor
import org.wso2.msf4j.Request
import org.wso2.msf4j.Response
import org.wso2.msf4j.ServiceMethodInfo

/**
 * 用来打印 HTTP 请求日志，供调试系统使用。
 */

@Component
open class HttpLoggingInterceptor : Interceptor {
    private val LOGGER: Logger = LoggerFactory.getLogger("============== HTTP DEBUG MESSAGE ==============")

    override fun postCall(request: Request, status: Int, serviceMethodInfo: ServiceMethodInfo) {
    }

    /**
     * 打印 HTTP 日志
     */
    override fun preCall(request: Request, responder: Response, methodInfo: ServiceMethodInfo): Boolean {
        val logEnabled = Application.conf.bool("http.logging.enabled") ?: false
        if (logEnabled) {
            if (LOGGER.isInfoEnabled) {
                val buffer = StringBuilder("\r\n")
                with(buffer) {
                    append("Request: ${request.uri} (${request.httpMethod})\r\n")
                    append("Thread: ${Thread.currentThread().id}\r\n")
                    append("Handler: ${methodInfo.methodName}.${methodInfo.method.name}\r\n")
                    append("Headers:\r\n")
                    request.headers.entries.forEach { append(" - ${it.key}: ${it.value}\r\n") }
                }
                LOGGER.info(buffer.toString())
            } else println("Please set logger level to 'INFO' to print HTTP logging")
        }
        return true
    }
}