package komics.data.jdbc.handler

import komics.model.Entity
import komics.data.jdbc.Sql
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import kotlin.reflect.KClass

/**
 * Created by ace on 2016/10/17.
 */
internal class CountHandler(val template: NamedParameterJdbcTemplate) {

    /**
     * 统计一个entity的数据条数
     * @param clazz 需要被统计的Entity类
     */
    fun <E : Entity> count(clazz: KClass<E>): Int {
        val sql = Sql.get(clazz, Sql.Predefine.count)
        if (sql.isNullOrEmpty()) return -1

        val num = template.queryForObject(sql, emptyMap<String, Any>(), Int::class.java)
        return num
    }

    /**
     * 统计一个entity的数据条数
     * @param sqlId 数据统计SQL语句
     * @param param 统计参数
     */
    fun count(sqlId: String, param: Map<String, Any>): Int {
        val sql = Sql.get(sqlId)
        if (sql.isNullOrEmpty()) return -1
        val num = template.queryForObject(sql, param, Int::class.java)
        return num
    }

    /**
     * 统计一个entity的数据条数
     * @param sqlId 数据统计SQL语句
     * @param param 统计参数
     */
    fun <E : Entity> count(sqlId: String, entity: E): Int {
        val sql = Sql.get(sqlId)
        if (sql.isNullOrEmpty()) return -1
        val num = template.queryForObject(sql, BeanPropertySqlParameterSource(entity), Int::class.java)
        return num
    }
}