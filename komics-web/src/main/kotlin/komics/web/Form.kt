package komics.web

import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Created by ace on 2016/10/21.
 */

interface Form {

    private companion object {
        val mapper = ObjectMapper()
    }

    /**
     * 校验表单.
     * TODO implement it
     */
    fun validate(): ValidateResult {
        return ValidateResult(false, mutableListOf<ValidateMessage>())
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
data class ValidateResult(val success: Boolean, val errors: MutableList<ValidateMessage>)

/**
 * 表单校验消息。
 * 每个校验失败的表单元素对应一条消息。
 */
data class ValidateMessage(val name: String, val message: String)
