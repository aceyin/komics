package komics.data.jdbc

import komics.data.Entity
import komics.data.jdbc.sql.Sqls
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import javax.sql.DataSource
import kotlin.reflect.KClass

/**
 * Created by ace on 2016/9/29.
 */
class Db(val datasource: DataSource) {

    private val template = NamedParameterJdbcTemplate(datasource)
    private val LOGGER = LoggerFactory.getLogger(Db::class.java)

    /**
     * 向数据库中插入一个对象。
     * @param entity 被插入的对象
     */
    fun <E : Entity> insert(entity: E): Boolean {
        val sql = Sqls.get(entity.javaClass.kotlin, Sqls.Predefine.insert)

        val param = BeanPropertySqlParameterSource(entity)
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("Execting sql: $sql with parameter $param")
        }
        val e = template.update(sql, param)
        return e > 0
    }

    /**
     * 在同一个事务中，批量插入数据到数据库。
     * 如果entities中的数据为不同的entity实例，则会插入到不同的数据库表。
     * @param entities 需要被批量插入的数据
     */
    fun <E : Entity> batchInsert(entities: List<E>): Boolean {
        if (entities.size == 0) return true
        val example = entities[0]
        val sql = Sqls.get(example.javaClass.kotlin, Sqls.Predefine.insert)

        var params = Array<BeanPropertySqlParameterSource>(entities.size) {
            BeanPropertySqlParameterSource(entities[it])
        }

        val n = template.batchUpdate(sql, params)
        return n.sum() == entities.size
    }

    /**
     * 根据给定的ID，将entity对象中所有不为空和默认值的数据更新到数据库
     * @param id 对象id
     * @param entity 需要被更新的数据。
     */
    fun <E : Entity> updateById(id: String, entity: E): Boolean {
        if (id.isNullOrBlank()) return false
        val sql = Sqls.get(entity.javaClass.kotlin, Sqls.Predefine.updateById)
        val n = template.update(sql, BeanPropertySqlParameterSource(entity))
        return n == 1
    }

    /**
     * 根据给定的条件，更新entity中的数据到数据库。
     * @param columns 需要更新的列，如果不指定则更新 entity 对象中所有值不为空和默认值的列
     * @param cnd 更新数据的条件
     * @param entity 需要更新的数据
     */
    fun <E : Entity> update(columns: List<String> = emptyList(), cnd: Cnd, entity: E): Boolean {
        TODO("to be implemented")
    }


    /**
     * 根据给定的SQL和参数，更新数据库的数据。
     * @param sql 数据更新语句
     * @param param sql语句的参数。如果sql语句中不需要参数，则该参数可以不填
     * @param entity 需要被更新的数据
     */
    fun <E : Entity> update(sql: String, entity: Entity, param: Map<String, Any> = emptyMap()): Boolean {
        TODO("to be implemented")
    }

    /**
     * 批量更新数据。
     * 注：entity只能为同一种类型。
     * @param sql 数据库更新语句
     * @param param sql参数的数据
     * @param entities 需要被更新的数据
     */
    fun <E : Entity> batchUpdate(sql: String, entities: List<E>, param: Map<String, Any> = emptyMap()): Boolean {
        TODO("to be implemented")
    }

    /**
     * 根据给定的id从数据库中删除一个对象。
     * @param id 需要被删除的对象的id
     * @param clazz 需要被删除的对象类型
     */
    fun <E : Entity> deleteById(id: String, clazz: KClass<E>): Boolean {
        TODO("to be implemented")
    }

    /**
     * 根据给定条件删除数据
     * @param cnd 查询条件
     * @param clazz 被删除的对象
     */
    fun <E : Entity> delete(cnd: Cnd, clazz: KClass<E>): Boolean {
        TODO("to be implemented")
    }

    /**
     * 根据id查询一个对象。
     * @param id 对象id
     * @param clazz 需要被查询的对象类型
     */
    fun <E : Entity> queryById(id: String, clazz: KClass<E>): E? {
        TODO("to be implemented")
    }

    /**
     * 根据给定条件查询对象。
     * @param cnd 查询条件
     * @param clazz 被查询的对象类型
     */
    fun <E : Entity> query(cnd: Cnd, clazz: KClass<E>): List<E>? {
        TODO("to be implemented")
    }
}