package komics.util

object StrUtil {
    /**
     * 判断给定的字符串是否包含空。
     * @param values
     * @return
     */
    fun hasNull(vararg values: String?): Boolean {
        if (values == null) return true
        values.forEach { if (it.isNullOrEmpty()) return true }
        return false
    }

    /**
     * 判断给定的字符串都不为空。
     * @param values
     * @return
     */
    fun notNull(vararg values: String?): Boolean {
        if (values == null) return false
        values.forEach { if (it.isNullOrEmpty()) return false }
        return true
    }
}