package komics.web

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.sql.SQLException
import java.util.concurrent.atomic.AtomicInteger
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

/**
 * 异常处理类
 * TODO: 增加计数器，为每一个class，每一个异常类型进行统计
 */

@Component
open class DefaultExceptionMapper<E : Exception> : ExceptionMapper<E> {

    companion object {
        // 基于异常类的统计
        val exceptionStatistics = mutableMapOf<String, AtomicInteger>()
        // 基于抛异常的代码的统计
        val codeStatistics = mutableMapOf<String, AtomicInteger>()
        private val LOGGER: Logger = LoggerFactory.getLogger(ExceptionMapper::class.java)
    }

    override fun toResponse(exception: E): Response {
        this.doStatistics(exception)
        return handleException(exception)
    }

    //TODO: 将异常展示出去(打印到日志文件，或者通过其他方式保存出去)
    private fun doStatistics(exception: E) {
        // statistics on the exception level
        val name = exception.javaClass.name
        val count = exceptionStatistics[name] ?: AtomicInteger(0)
        count.incrementAndGet()
        exceptionStatistics.put(name, count)
        // statistics on the code level
        val stacks = exception.stackTrace
        if (stacks.size > 0) {
            val stack = stacks[0]
            val info = "${stack.className}.${stack.methodName}:${stack.lineNumber}[${stack.fileName}]"
            val num = codeStatistics[info] ?: AtomicInteger(0)
            num.incrementAndGet()
            codeStatistics.put(info, num)
        }
    }

    protected fun handleException(exception: E): Response {
        when (exception) {
            is FormValidationException -> return handleInvalidDataException(exception)
            is SQLException -> return handleSQLException(exception)
            is NullPointerException -> return handleNullPointException(exception)
            else -> return handleOtherException(exception)
        }
    }

    private fun handleInvalidDataException(exception: FormValidationException): Response {
        val form = exception.form
        val result = exception.result

        LOGGER.warn("Form: ${form.json()}, message: $result")

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(result).build()
    }

    private fun handleSQLException(exception: SQLException): Response {
        LOGGER.warn("SQL State:${exception.sqlState}")
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build()
    }

    private fun handleNullPointException(exception: NullPointerException): Response {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build()
    }

    private fun handleOtherException(exception: Throwable): Response {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build()
    }
}
