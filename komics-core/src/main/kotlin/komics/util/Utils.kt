package komics.util

import komics.data.Entity
import javax.persistence.Table
import kotlin.reflect.KClass

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

/**
 * Entity util
 */
object EntityUtil {
    /**
     * build a sql with an entity class as the table name
     */
    fun <T : Entity> table(c: KClass<T>): String {
        val cname = c.simpleName ?: throw RuntimeException("Name of entity class '$c' cannot be null")
        val t = c.annotations.find { it.annotationClass == Table::class } as? Table

        t?.let {
            if (t.name.isNullOrEmpty()) return cname
            return t.name
        }
        return cname
    }
}