package komics.web.interceptors

import org.springframework.stereotype.Component
import org.wso2.msf4j.Interceptor
import org.wso2.msf4j.Request
import org.wso2.msf4j.Response
import org.wso2.msf4j.ServiceMethodInfo

/**
 * 表单验证拦截器
 */

@Component
open class FormValidationInterceptor : Interceptor {

    override fun postCall(request: Request?, status: Int, serviceMethodInfo: ServiceMethodInfo?) {
    }

    /**
     * 执行表单验证。
     * TODO: 因为 MSF4J 目前不支持 JSR-349，而且在Interceptor里面也没办法简单的实现，所以表单的自动验证暂时不支持。
     */
    override fun preCall(request: Request, response: Response, methodInfo: ServiceMethodInfo): Boolean {
//        val params = methodInfo.method.parameters ?: return true
//        params.forEachIndexed loop@ { i, param ->
//            val type = param.type
//            var paramIsForm = type.interfaces.filter { i -> i == Form::class.java }.isNotEmpty()
//            if (!paramIsForm) return@loop
//
//            val needValidate = param.annotations.filter { ann -> ann.annotationClass == Valid::class }.isNotEmpty()
//            if (!needValidate) return@loop
//            // do validate
//            doValidate(request, response, type, i)
//        }
        return true
    }

    /**
     * 注：因为 MSF4J 框架暂时不支持 JSR-349，所以这里只是一个workaround。
     *     如果框架更新之后，需要改用MSF4J官方的表单验证方式。
     */
    private fun doValidate(request: Request, response: Response, type: Class<*>, i: Int) {
    }

}