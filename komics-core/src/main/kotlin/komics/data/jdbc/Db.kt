package komics.data.jdbc

import jodd.bean.BeanUtil
import komics.data.Entity
import komics.data.EntityMeta
import komics.data.jdbc.sql.Sql
import org.slf4j.LoggerFactory
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.dao.IncorrectResultSizeDataAccessException
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import javax.sql.DataSource
import kotlin.reflect.KClass

/**
 * TODO 使用spring注入 jdbctemplate/datasource
 * TODO 翻页
 * TODO one-to-one, one-to-many
 */
class Db(val datasource: DataSource) {

    private val template = NamedParameterJdbcTemplate(datasource)
    private val LOGGER = LoggerFactory.getLogger(Db::class.java)

    /**
     * 向数据库中插入一个对象。
     * @param entity 被插入的对象
     */
    fun <E : Entity> insert(entity: E): Boolean {
        val sql = Sql.get(entity.javaClass.kotlin, Sql.Predefine.insert)

        val param = BeanPropertySqlParameterSource(entity)
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("Execting sql: $sql with parameter $param")
        }
        val e = template.update(sql, param)
        return e > 0
    }

    /**
     * 按照给定的SQL语句和参数插入数据。
     * @param sqlId 需要执行的SQL预计
     * @param param sql参数
     */
    fun insert(sqlId: String, param: Map<String, Any>): Boolean {
        val sql = Sql.get(sqlId)
        if (sql.isNullOrEmpty()) return false
        val n = template.update(sql, param)
        return n > 0
    }

    /**
     * 在同一个事务中，批量插入数据到数据库。
     * 如果entities中的数据为不同的entity实例，则会插入到不同的数据库表。
     * @param entities 需要被批量插入的数据
     */
    fun <E : Entity> batchInsert(entities: List<E>): Boolean {
        if (entities.isEmpty()) return true
        val example = entities[0]
        val clazz = example.javaClass.kotlin

        val sql = Sql.get(clazz, Sql.Predefine.insert)
        if (sql.isNullOrEmpty()) {
            LOGGER.warn("No SQL found for entity class '$clazz' and predefined sql '${Sql.Predefine.insert}'")
            return false
        }

        val params = Array(entities.size) {
            BeanPropertySqlParameterSource(entities[it])
        }

        val n = template.batchUpdate(sql, params)
        return n.sum() == entities.size
    }

    /**
     * 批量插入entities
     * @param entities 需要被插入到数据库的entity
     */
    fun <E : Entity> batchInsert(vararg entities: E): Boolean {
        return batchInsert(entities.asList())
    }

    /**
     * 根据给定的sql和参数，批量插入数据.
     * @param sqlId 数据库insert语句
     * @param param 要插入的数据
     */
    fun batchInsert(sqlId: String, vararg param: Map<String, Any>): Boolean {
        val sql = Sql.get(sqlId)
        if (sql.isNullOrEmpty()) return false
        val n = template.batchUpdate(sql, param)
        return n.sum() == param.size
    }

    /**
     * 根据给定的sql和参数，批量插入数据.
     * @param sqlId 数据库insert语句
     * @param params 要插入的数据
     */
    fun batchInsert(sqlId: String, params: List<Map<String, Any>>): Boolean {
        return batchInsert(sqlId, *params.toTypedArray())
    }

    /**
     * 根据给定的ID，将entity对象中所有不为空和默认值的数据更新到数据库
     * @param id 对象id
     * @param entity 需要被更新的数据。
     */
    fun <E : Entity> updateById(id: String, entity: E): Boolean {
        if (id.isNullOrBlank()) return false
        val sql = Sql.get(entity.javaClass.kotlin, Sql.Predefine.updateById)
        val n = template.update(sql, BeanPropertySqlParameterSource(entity))
        return n == 1
    }

    /**
     * 根据给定的SQL，更新entity中的数据到数据库。
     * @param sqlId 给定的SQL语句的id。sql语句在 sqls.yml中配置
     * @param entity 需要更新的数据。update语句中的 set x=y 和 where a=b 中需要的数据都从 entity 中获取
     */
    fun <E : Entity> update(sqlId: String, entity: E): Boolean {
        val sql = Sql.get(sqlId)
        if (sql.isNullOrEmpty()) return false
        val n = template.update(sql, BeanPropertySqlParameterSource(entity))
        return n > 0
    }


    /**
     * 根据给定的SQL和参数，更新数据库的数据。
     * @param sql 数据更新语句
     * @param param sql语句的参数,包括要更新的数据和更新的查询条件中的参数。如果sql语句中不需要参数，则该参数可以不填
     * 注：如果查询条件和被更新的数据中含有相同的参数，则更新可能会失败。
     * 例如：update user set name=:name where name=:name
     *        如果要使用 in 语法(update t set x=:x, y=:y where z in (:z)
     *        那么需要保证param里面的参数"z"的类型为 List 类型
     */
    fun update(sqlId: String, param: Map<String, Any> = emptyMap()): Boolean {
        val sql = Sql.get(sqlId)
        if (sql.isNullOrEmpty()) return false
        val n = template.update(sql, param)
        return n > 0
    }

    /**
     * 批量更新数据。
     * 注：entity只能为同一种类型。
     * @param sqlId 数据库更新语句Id
     * @param entities 需要被更新的数据
     */
    fun <E : Entity> batchUpdate(sqlId: String, entities: List<E>): Boolean {
        val sql = Sql.get(sqlId)
        val size = entities.size
        if (sql.isNullOrEmpty() || size == 0) return false
        val params = Array(size) {
            BeanPropertySqlParameterSource(entities[it])
        }
        val n = template.batchUpdate(sql, params)
        return n.sum() == size
    }

    /**
     * 批量更新entity
     * @param sqlId 数据库更新预计的id
     * @param entity 需要被更新的entities
     */
    fun <E : Entity> batchUpdate(sqlId: String, vararg entity: E): Boolean {
        if (entity.isEmpty()) return false
        return batchUpdate(sqlId, entity.asList())
    }

    /**
     * 批量更新数据。
     * @param sqlId SQLID
     * @param param sql参数
     *        如果要使用 in 语法(update t set x=:x,y=:y where z in (:z)
     *        那么需要保证param里面的参数"z"的类型为 List 类型
     */
    fun batchUpdate(sqlId: String, vararg param: Map<String, Any>): Boolean {
        if (param.isEmpty()) return false
        val sql = Sql.get(sqlId)
        if (sql.isNullOrEmpty()) return false

        val n = template.batchUpdate(sql, param)
        return n.sum() == param.size
    }

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
    fun batchDelete(sqlId: String, vararg params: Map<String, Any>): Boolean {
        val sql = Sql.get(sqlId)
        if (sql.isNullOrEmpty()) return false

        val n = template.batchUpdate(sql, params)
        return n.sum() == params.size
    }

    /**
     * 根据id查询一个对象。
     * TODO 注意要处理好各个数据库对查询出来的字段名的大小写问题
     * TODO 处理一对多多对多等关联关系
     * @param id 对象id
     * @param clazz 需要被查询的对象类型
     */
    fun <E : Entity> queryById(clazz: KClass<E>, id: String): E? {
        val sql = Sql.get(clazz, Sql.Predefine.queryById)
        val meta = EntityMeta.get(clazz)

        try {
            val map = template.queryForMap(sql, mapOf(Entity::id.name to id))
            if (map == null || map.isEmpty()) return null

            return toBean(clazz, map, meta)
        } catch (e: EmptyResultDataAccessException) {
            LOGGER.warn("No entity '$clazz' with id=$id found")
            return null
        } catch (e: IncorrectResultSizeDataAccessException) {
            LOGGER.warn("Too many entity '$clazz' with id=$id found, excepted=${e.expectedSize}, actual=${e.actualSize}")
            return null
        }
    }

    /**
     * 根据给定的id列表，查询entities
     * @param clazz 需要被查询的实体类
     * @param ids 给定的id列表
     */
    fun <E : Entity> queryByIds(clazz: KClass<E>, ids: List<String>): List<E> {
        if (ids.isEmpty()) return emptyList()
        val sql = Sql.get(clazz, Sql.Predefine.queryByIds)
        if (sql.isNullOrEmpty()) return emptyList()

        val list = template.queryForList(sql, mapOf(Entity::id.name to ids))
        if (list == null || list.isEmpty()) return emptyList()

        val meta = EntityMeta.get(clazz)
        val result = mutableListOf<E>()

        list.forEach { map ->
            result.add(toBean(clazz, map, meta))
        }
        return result
    }

    /**
     * 根据给定的id列表，查询entities
     * @param clazz 需要被查询的实体类
     * @param ids 给定的id列表
     */
    fun <E : Entity> queryByIds(clazz: KClass<E>, vararg ids: String): List<E> {
        if (ids.isEmpty()) return emptyList()
        return queryByIds(clazz, ids.toList())
    }

    /**
     * 根据给定条件查询对象。
     * @param sqlId 查询SQL语句的ID
     * @param clazz 被查询的对象类型
     * @param params 查询参数。
     *        如果要使用 in 语法(select x from y where z in (:z)
     *        那么需要保证param里面的参数"z"的类型为 List 类型
     */
    fun <E : Entity> query(clazz: KClass<E>, sqlId: String, params: Map<String, Any>): List<E> {
        val sql = Sql.get(sqlId)
        if (sql.isNullOrEmpty()) {
            LOGGER.warn("No sql with id '$sqlId' found")
            return emptyList()
        }
        val list = template.queryForList(sql, params)
        if (list == null || list.isEmpty()) return emptyList()

        val result = mutableListOf<E>()
        val meta = EntityMeta.get(clazz)
        list.forEachIndexed { i, map ->
            result.add(toBean(clazz, map, meta))
        }

        return result
    }

    /**
     * 根据给定条件查询对象。
     * @param sqlId 查询SQL语句的ID
     * @param entity 查询参数
     */
    fun <E : Entity> query(sqlId: String, entity: E): List<E> {
        val sql = Sql.get(sqlId)
        if (sql.isNullOrEmpty()) return emptyList()
        val list = template.queryForList(sql, BeanPropertySqlParameterSource(entity))
        if (list == null || list.isEmpty()) return emptyList()

        val result = mutableListOf<E>()
        val clazz = entity.javaClass.kotlin
        val meta = EntityMeta.get(clazz)

        list.forEach {
            result.add(toBean(clazz, it, meta))
        }

        return result
    }

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

    /**
     * 将一个JdbcTemplate返回的Map转换为JavaBan
     */
    private fun <E : Entity> toBean(clazz: KClass<E>, map: MutableMap<String, Any>, meta: EntityMeta): E {
        val bean = clazz.java.newInstance()
        map.forEach {
            val prop = meta.prop(it.key)
            BeanUtil.pojo.setProperty(bean, prop, it.value)
        }
        return bean
    }
}
