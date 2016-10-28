package komics.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.hibernate.validator.internal.engine.path.PathImpl
import javax.validation.Validation

/**
 * Created by ace on 2016/10/21.
 */

interface Form {

    private companion object {
        val mapper = ObjectMapper()
        val validator = Validation.buildDefaultValidatorFactory().validator
    }

    /**
     * 校验表单.
     * TODO implement it
     */
    fun validate(): ValidateResult {
        val constraints = validator.validate(this)
        if (constraints.isEmpty()) return ValidateResult(true)

        val msg = mutableMapOf<String, String>()
        constraints.iterator().forEach {
            val path = it.propertyPath as PathImpl
            msg.put(path.asString(), it.message)
        }
        return ValidateResult(false, msg)
    }

    /**
     * 返回一个JSON字符串
     */
    fun json(): String {
        return Form.mapper.writeValueAsString(this)
    }
}

/**
 * 表单校验结果
 */
data class ValidateResult(val success: Boolean, val errors: Map<String, String> = emptyMap())