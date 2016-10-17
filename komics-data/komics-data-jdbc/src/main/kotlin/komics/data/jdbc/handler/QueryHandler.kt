package komics.data.jdbc.handler

import jodd.bean.BeanUtil
import komics.data.Entity
import komics.data.EntityMeta
import komics.data.jdbc.Page
import komics.data.jdbc.Sql
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.statement.select.PlainSelect
import net.sf.jsqlparser.statement.select.Select
import org.slf4j.LoggerFactory
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.dao.IncorrectResultSizeDataAccessException
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import kotlin.reflect.KClass

/**
 * Created by ace on 2016/10/17.
 */
internal class QueryHandler(val template: NamedParameterJdbcTemplate) {

    private val LOGGER = LoggerFactory.getLogger(QueryHandler::class.java)

    /**
     * 根据id查询一个对象。
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
        list.forEach { map ->
            result.add(toBean(clazz, map, meta))
        }

        return result
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
        if (page < 1 || pageSize <= 0) return Page.empty()

        var count = Sql.get(clazz, Sql.Predefine.count)
        var select = Sql.get(clazz, Sql.Predefine.queryAll)

        if (count.isNullOrEmpty() || select.isNullOrEmpty()) {
            LOGGER.error("Cannot find COUNT/QUERY_ALL SQL for class '$clazz'")
            return Page.empty()
        }

        val num = template.queryForObject(count, emptyMap<String, Any>(), Int::class.java)
        if (num == 0) return Page.empty()

        val limit = limit(page, pageSize)
        val list = template.queryForList("$select $limit", emptyMap<String, Any>())

        val meta = EntityMeta.get(clazz)
        val result = mutableListOf<E>()
        list.forEach {
            result.add(toBean(clazz, it, meta))
        }

        val pages = pageCount(num, pageSize)
        return Page(num, pages, page, result)
    }

    /**
     * 根据条件进行翻页查询。
     * 注意：原始的SQL语句不需要指定 limit 语句, 否则page和pageSize参数就会失效，结果就是根据SQL里面的limit查询出来的了。
     *      如果指定了 limit 语句且 limit 语句的参数为形参的话，SQL解析会报错
     * @param clazz 要查询的entity的类型
     * @param sqlId 查询语句
     * @param param 查询参数
     * @param page 查询第几页的数据
     * @param pageSize 每页数据条数
     * @return 翻页数据
     */
    fun <E : Entity> pageQuery(clazz: KClass<E>, sqlId: String, param: Map<String, Any>, page: Int, pageSize: Int): Page<E> {
        if (page < 1 || pageSize <= 0) return Page.empty()

        var sql = Sql.get(sqlId)
        if (sql.isNullOrEmpty()) {
            LOGGER.error("Cannot find SQL by sqlId '$sqlId'")
            return Page.empty()
        }

        val select = CCJSqlParserUtil.parse(sql)
        if (select !is Select) throw IllegalArgumentException("SQL '$sql' with id '$sqlId' is not a valid SELECT SQL")

        val plainSelect = select.selectBody as PlainSelect

        val where = plainSelect.where.toString()
        if (where.isNullOrEmpty()) throw IllegalArgumentException("Failed extracting WHERE statement from SQL '$sql'(sqlId='$sqlId')")

        // count data
        val count = Sql.get(clazz, Sql.Predefine.count)
        if (count.isNullOrEmpty()) throw IllegalArgumentException("Cannot get COUNT SQL for entity '$clazz'")

        val countSql = "$count WHERE $where"

        val num = template.queryForObject(countSql, param, Int::class.java)
        if (num == 0) {
            return Page.empty()
        }

        // query data
        val newLimit = limit(page, pageSize)
        val limit = plainSelect.limit
        if (limit == null) sql = "$sql $newLimit"

        val list = template.queryForList(sql, param)

        val result = mutableListOf<E>()
        val meta = EntityMeta.get(clazz)
        list.forEach {
            result.add(toBean(clazz, it, meta))
        }
        val pages = pageCount(num, pageSize)
        return Page(num, pages, page, result)
    }

    private fun pageCount(num: Int, pageSize: Int): Int {
        return if (num % pageSize > 0) num / pageSize + 1 else num / pageSize
    }

    private fun limit(page: Int, pageSize: Int) = "LIMIT $pageSize OFFSET ${(page - 1) * pageSize}"

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