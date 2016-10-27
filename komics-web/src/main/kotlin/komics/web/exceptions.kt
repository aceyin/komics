package komics.web

/**
 * 表单验证失败异常
 */
class InvalidDataException(val form: Form, result: ValidateResult) : RuntimeException()
