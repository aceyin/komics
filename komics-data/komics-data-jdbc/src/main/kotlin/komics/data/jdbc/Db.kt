package komics.data.jdbc

import komics.data.Entity
import komics.data.jdbc.handler.*
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import javax.sql.DataSource
import kotlin.reflect.KClass

/**
 * TODO 使用spring注入 jdbctemplate/datasource
 * TODO 翻页
 * TODO 联合查询
 * TODO one-to-one, one-to-many
 */
class Db(datasource: DataSource) {
    private val template = NamedParameterJdbcTemplate(datasource)
    private val inserter = InsertHandler(template)
    private val updater = UpdateHandler(template)
    private val deleter = DeleteHandler(template)
    private val simpleQuerier = QueryHandler(template)
    private val counter = CountHandler(template)

    /**
     * 向数据库中插入一个对象。
     * @param entity 被插入的对象
     */
    fun <E : Entity> insert(entity: E): Boolean {
        return inserter.insert(entity)
    }

    /**
     * 按照给定的SQL语句和参数插入数据。
     * @param sqlId 需要执行的SQL预计
     * @param param sql参数
     */
    fun insert(sqlId: String, param: Map<String, Any>): Boolean {
        return inserter.insert(sqlId, param)
    }

    /**
     * 在同一个事务中，批量插入数据到数据库。
     * 如果entities中的数据为不同的entity实例，则会插入到不同的数据库表。
     * @param entities 需要被批量插入的数据
     */
    fun <E : Entity> batchInsert(entities: List<E>): Boolean {
        return inserter.batchInsert(entities)
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
        return inserter.batchInsert(sqlId, param)
    }

    /**
     * 根据给定的sql和参数，批量插入数据.
     * @param sqlId 数据库insert语句
     * @param params 要插入的数据
     */
    fun batchInsert(sqlId: String, params: List<Map<String, Any>>): Boolean {
        return inserter.batchInsert(sqlId, params.toTypedArray())
    }

    /**
     * 根据给定的ID，将entity对象中所有不为空和默认值的数据更新到数据库
     * @param id 对象id
     * @param entity 需要被更新的数据。
     */
    fun <E : Entity> updateById(id: String, entity: E): Boolean {
        return updater.updateById(id, entity)
    }

    /**
     * 根据给定的SQL，更新entity中的数据到数据库。
     * @param sqlId 给定的SQL语句的id。sql语句在 sqls.yml中配置
     * @param entity 需要更新的数据。update语句中的 set x=y 和 where a=b 中需要的数据都从 entity 中获取
     */
    fun <E : Entity> update(sqlId: String, entity: E): Boolean {
        return updater.update(sqlId, entity)
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
        return updater.update(sqlId, param)
    }

    /**
     * 批量更新数据。
     * 注：entity只能为同一种类型。
     * @param sqlId 数据库更新语句Id
     * @param entities 需要被更新的数据
     */
    fun <E : Entity> batchUpdate(sqlId: String, entities: List<E>): Boolean {
        return updater.batchUpdate(sqlId, entities)
    }

    /**
     * 批量更新entity
     * @param sqlId 数据库更新预计的id
     * @param entity 需要被更新的entities
     */
    fun <E : Entity> batchUpdate(sqlId: String, vararg entity: E): Boolean {
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
        return updater.batchUpdate(sqlId, param)
    }


    /**
     * 根据给定的id从数据库中删除一个对象。
     * @param id 需要被删除的对象的id
     * @param clazz 需要被删除的对象类型
     */
    fun <E : Entity> deleteById(clazz: KClass<E>, id: String): Boolean {
        return deleter.deleteById(clazz, id)
    }

    /**
     * 根据给定的id，批量删除entity
     * @param clazz 要被删除的entity类
     * @param ids 被删除的entity的id
     */
    fun <E : Entity> deleteByIds(clazz: KClass<E>, ids: List<String>): Boolean {
        return deleter.deleteByIds(clazz, ids)
    }

    /**
     * 根据指定的sql删除一个entity
     * @param sqlId 查询条件
     * @param entity sql语句的参数
     */
    fun <E : Entity> delete(sqlId: String, entity: E): Boolean {
        return deleter.delete(sqlId, entity)
    }

    /**
     * 删除所有entity数据
     * @param clazz 需要被删除的数据的类型
     */
    fun <E : Entity> delete(clazz: KClass<E>): Boolean {
        return deleter.delete(clazz)
    }

    /**
     * 根据给定条件删除指定的entities
     * @param sqlId 删除数据的SQL语句
     * @param param 要删除的参数。
     *        如果要使用 in 语法(delete from y where z in (:z)
     *        那么需要保证param里面的参数"z"的类型为 List 类型
     */
    fun delete(sqlId: String, param: Map<String, Any>): Boolean {
        return deleter.delete(sqlId, param)
    }

    /**
     * 根据给定的SQL批量删除entity
     * @param sqlId 删除SQL语句
     * @param entities 需要被删除的entity
     */
    fun <E : Entity> batchDelete(sqlId: String, entities: List<E>): Boolean {
        return deleter.batchDelete(sqlId, entities)
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
        return deleter.batchDelete(sqlId, params)
    }

    /**
     * 根据id查询一个对象。
     * @param id 对象id
     * @param clazz 需要被查询的对象类型
     */
    fun <E : Entity> queryById(clazz: KClass<E>, id: String): E? {
        return simpleQuerier.queryById(clazz, id)
    }

    /**
     * 根据给定的id列表，查询entities
     * @param clazz 需要被查询的实体类
     * @param ids 给定的id列表
     */
    fun <E : Entity> queryByIds(clazz: KClass<E>, ids: List<String>): List<E> {
        return simpleQuerier.queryByIds(clazz, ids)
    }

    /**
     * 根据给定的id列表，查询entities
     * @param clazz 需要被查询的实体类
     * @param ids 给定的id列表
     */
    fun <E : Entity> queryByIds(clazz: KClass<E>, vararg ids: String): List<E> {
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
        return simpleQuerier.query(clazz, sqlId, params)
    }

    /**
     * 根据给定条件查询对象。
     * @param sqlId 查询SQL语句的ID
     * @param entity 查询参数
     */
    fun <E : Entity> query(sqlId: String, entity: E): List<E> {
        return simpleQuerier.query(sqlId, entity)
    }

    /**
     * 根据条件进行翻页查询。
     * @param clazz 要查询的entity的类型
     * @param sqlId 查询语句
     * @param param 查询参数
     * @param page 查询第几页的数据
     * @param pageSize 每页数据条数
     * @return 翻页数据
     */
    fun <E : Entity> pageQuery(clazz: KClass<E>, page: Int, pageSize: Int): Page<E> {
        return simpleQuerier.pageQuery(clazz, page, pageSize)
    }

    /**
     * 根据条件进行翻页查询。
     * 实际操作方式是对原始的SQL再包装一层带limit部分的SQL语句。
     * 例如：原始SQL为：select u.* from user where name=:name and password=:password
     * 那么执行翻页查询最终的SQL语句为：
     *  select * from (select u.* from user where name=:name and password=:password) limit 0,10
     * @param clazz 要查询的entity的类型
     * @param sqlId 查询语句
     * @param param 查询参数
     * @param page 查询第几页的数据
     * @param pageSize 每页数据条数
     * @return 翻页数据
     */
    fun <E : Entity> pageQuery(clazz: KClass<E>, sqlId: String, param: Map<String, Any>, page: Int, pageSize: Int): Page<E> {
        return simpleQuerier.pageQuery(clazz, sqlId, param, page, pageSize)
    }

    /**
     * 统计一个entity的数据条数
     * @param clazz 需要被统计的Entity类
     */
    fun <E : Entity> count(clazz: KClass<E>): Int {
        return counter.count(clazz)
    }

    /**
     * 统计一个entity的数据条数
     * @param sqlId 数据统计SQL语句
     * @param param 统计参数
     */
    fun count(sqlId: String, param: Map<String, Any>): Int {
        return counter.count(sqlId, param)
    }

    /**
     * 统计一个entity的数据条数
     * @param sqlId 数据统计SQL语句
     * @param param 统计参数
     */
    fun <E : Entity> count(sqlId: String, entity: E): Int {
        return counter.count(sqlId, entity)
    }

}
