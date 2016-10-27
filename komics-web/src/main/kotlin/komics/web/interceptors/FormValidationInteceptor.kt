package komics.web.interceptors

import komics.web.Form
import org.springframework.stereotype.Component
import org.wso2.msf4j.Interceptor
import org.wso2.msf4j.Request
import org.wso2.msf4j.Response
import org.wso2.msf4j.ServiceMethodInfo
import javax.validation.Valid

/**
 * 表单验证拦截器
 */

@Component
open class FormValidationInteceptor : Interceptor {

    override fun postCall(request: Request?, status: Int, serviceMethodInfo: ServiceMethodInfo?) {
    }

    /**
     * 执行表单验证。
     */
    override fun preCall(request: Request, response: Response, methodInfo: ServiceMethodInfo): Boolean {
        val params = methodInfo.method.parameters ?: return true
        params.forEachIndexed loop@ { i, param ->
            val type = param.type
            var paramIsForm = type.interfaces.filter { i -> i == Form::class.java }.isNotEmpty()
            if (!paramIsForm) return@loop

            val needValidate = param.declaredAnnotations.filter { param.javaClass == Valid::class.java }.isNotEmpty()
            if (!needValidate) return@loop
            // do validate
            doValidate(request, response, methodInfo, i)
        }
        return true
    }

    /**
     * 注：因为 MSF4J 框架暂时不支持 JSR-349，所以这里只是一个workaround。
     *     如果框架更新之后，需要改用MSF4J官方的表单验证方式。
     */
    private fun doValidate(request: Request, response: Response, methodInfo: ServiceMethodInfo, i: Int) {
//        val destination = microservicesRegistry.getMetadata().getDestinationMethod(request.uri, request.httpMethod, request.contentType,
//                request.acceptTypes)
//        val resourceModel = destination.getDestination()
//
//        val httpMethodInfoBuilder = HttpMethodInfoBuilder().
//                httpResourceModel(resourceModel).
//                httpRequest(request).httpResponder(response).
//                requestInfo(destination.getGroupNameValues())
//
//        val httpMethodInfo = httpMethodInfoBuilder.build()
    }

}