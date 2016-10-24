package komics.web.support

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Created by ace on 2016/10/23.
 */

@Component
@Aspect
open class Pointcuts {

    private val LOGGER: Logger = LoggerFactory.getLogger(Pointcuts::class.java)

    @Pointcut("execution(* *.*(..))")
    protected fun loggingOperation() {
    }

    @Pointcut("@annotation(javax.ws.rs.Path) && execution(* *.*(..))")
    protected fun pathPoint() {

    }

    @Before("pathPoint()")
    fun before(point: JoinPoint) {

        println("${point.signature.declaringTypeName}_${point.target.javaClass.name}_${point.signature.name}***********************")
    }

//    @Around("within(komics.web.RestTest)")
//    fun validateForm(pjp: ProceedingJoinPoint) {
//        if (LOGGER.isDebugEnabled) LOGGER.debug("Validating form ...")
//        val args = pjp.args ?: pjp.proceed()
//        if (args is Array<*>) {
//            args.forEach {
//                if (it is Form) {
//                    val result = it.validate()
//                    if (result.success) pjp.proceed(args)
//                    else throw InvalidFormException(it, result)
//                }
//            }
//        } else pjp.proceed()
//    }
}