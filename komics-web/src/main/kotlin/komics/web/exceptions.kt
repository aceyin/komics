package komics.web

/**
 * 表单验证失败异常
 */
class FormValidationException(val form: Form, val result: ValidateResult) : RuntimeException()
