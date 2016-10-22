package komics.web

/**
 * Created by ace on 2016/10/21.
 */

interface Form {
    /**
     * 校验表单.
     * TODO implement it
     */
    fun validate(): ValidateResult {
        return ValidateResult(true, mutableListOf<ValidateMessage>())
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