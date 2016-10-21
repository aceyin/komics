package komics.data.jdbc.handler

import komics.model.Entity
import komics.data.jdbc.Sql
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import kotlin.reflect.KClass

/**
 * Created by ace on 2016/10/17.
 */
internal class DeleteHandler(val template: NamedParameterJdbcTemplate) {
    private val LOGGER = LoggerFactory.getLogger(DeleteHandler::class.java)

    /**
     * 根据给定的id从数据库中删除一个对象。
     * @param id 需要被删除的对象的id
     * @param clazz 需要被删除的对象类型
     */
    fun <E : Entity> deleteById(clazz: KClass<E>, id: String): Boolean {
        val sql = Sql.get(clazz, Sql.Predefine.deleteById)
        val n = template.update(sql, mapOf(Entity::id.name to id))
        return n == 1
    }

    /**
     * 根据给定的id，批量删除entity
     * @param clazz 要被删除的entity类
     * @param ids 被删除的entity的id
     */
    fun <E : Entity> deleteByIds(clazz: KClass<E>, ids: List<String>): Boolean {
        if (ids.isEmpty()) return false
        val sql = Sql.get(clazz, Sql.Predefine.deleteById)
        if (sql.isNullOrEmpty()) {
            LOGGER.warn("No SQL found for entity class '$clazz' and predefined sql '${Sql.Predefine.deleteById}'")
            return false
        }

        val params = Array(ids.size) {
            mapOf(Entity::id.name to ids.get(it))
        }

        val n = template.batchUpdate(sql, params)
        return n.sum() == ids.size
    }

    /**
     * 根据指定的sql删除一个entity
     * @param sqlId 查询条件
     * @param entity sql语句的参数
     */
    fun <E : Entity> delete(sqlId: String, entity: E): Boolean {
        val sql = Sql.get(sqlId)
        if (sql.isNullOrEmpty()) {
            LOGGER.warn("No SQL found with id '$sqlId'")
            return false
        }

        val n = template.update(sql, BeanPropertySqlParameterSource(entity))
        return n > 0
    }

    /**
     * 删除所有entity数据
     * @param clazz 需要被删除的数据的类型
     */
    fun <E : Entity> delete(clazz: KClass<E>): Boolean {
        val sql = Sql.get(clazz, Sql.Predefine.deleteAll)
        if (sql.isNullOrEmpty()) return false
        val n = template.update(sql, emptyMap<String, Any>())
        return n > 0
    }

    /**
     * 根据给定条件删除指定的entities
     * @param sqlId 删除数据的SQL语句
     * @param param 要删除的参数。
     *        如果要使用 in 语法(delete from y where z in (:z)
     *        那么需要保证param里面的参数"z"的类型为 List 类型
     */
    fun delete(sqlId: String, param: Map<String, Any>): Boolean {
        val sql = Sql.get(sqlId)
        if (sql.isNullOrEmpty()) return false

        val n = template.update(sql, param)
        return n > 0
    }

    /**
     * 根据给定的SQL批量删除entity
     * @param sqlId 删除SQL语句
     * @param entities 需要被删除的entity
     */
    fun <E : Entity> batchDelete(sqlId: String, entities: List<E>): Boolean {
        if (entities.isEmpty()) return false
        val sql = Sql.get(sqlId)
        if (sql.isNullOrEmpty()) return false

        val params = Array<BeanPropertySqlParameterSource>(entities.size) {
            BeanPropertySqlParameterSource(entities[it])
        }
        val n = template.batchUpdate(sql, params)
        return n.sum() == entities.size
    }


    /**
     * 根据给定的SQL批量删除entity
     * @param sqlId 删除SQL语句
     * @param entities 需要被删除的entity
     */
    fun <E : Entity> batchDelete(sqlId: String, vararg entities: E): Boolean {
        return batchDelete(sqlId, entities.asList())
    }

    /**
     * 根据给定的SQL批量删除entity
     * @param sqlId 删除SQL语句
     * @param entities 需要被删除的entity
     */
    fun batchDelete(sqlId: String, params: Array<out Map<String, Any>>): Boolean {
        val sql = Sql.get(sqlId)
        if (sql.isNullOrEmpty()) return false

        val n = template.batchUpdate(sql, params)
        return n.sum() == params.size
    }
}