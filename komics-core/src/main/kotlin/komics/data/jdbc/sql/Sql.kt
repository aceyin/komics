package komics.data.jdbc.sql

import komics.core.SqlConfig
import komics.data.Entity
import komics.data.EntityMeta
import kotlin.reflect.KClass

/**
 * Created by ace on 2016/10/11.
 */
object Sql {

    enum class Predefine {
        insert, updateById, deleteById, queryById
    }

    private val SQL_ID_RGX = "(\\w+\\.)+(\\w+)@(\\w+)".toRegex()
    /**
     * 从配置文件加载一个SQL
     * @param sqlid 配置文件中的sql语句的ID
     */
    fun get(sqlid: String): String {
        var sql = SqlConfig.get(sqlid)
        if (sql.isNotEmpty()) return sql
        if (sqlid.matches(SQL_ID_RGX)) {
            val (clazz, sqltype) = sqlid.split("@")
            sql = load(clazz, Predefine.valueOf(sqltype))
            if (sql.isNotBlank())
                SqlConfig.add(sqlid, sql)
        }
        return sql
    }

    /**
     * 根据 entity 的类型获取一个predefine的 sql
     * @param clazz  entity 的类型
     * @param type 预定义的一些sql
     * @return 预定义的sql语句
     */
    fun get(clazz: KClass<out Any>, type: Predefine): String {
        val id = sqlId(clazz, type)
        return get(id)
    }

    /**
     * 根据Entity类和查询类型，获取一个sql id
     */
    fun sqlId(clazz: KClass<out Any>, type: Predefine): String = "${clazz.qualifiedName}@${type.name}"

    /**
     * Cached sql count
     */
    fun count(): Int {
        return SqlConfig.count()
    }

    /**
     * 从一个实体类中获取SQL
     */
    private fun load(clazz: String, type: Predefine): String {
        val c = Class.forName(clazz).kotlin
        var sql = ""
        when (type) {
            Predefine.insert -> sql = insertSql(c)
            Predefine.updateById -> sql = updateByIdSql(c)
            Predefine.deleteById -> sql = deleteById(c)
            Predefine.queryById -> sql = queryById(c)
        }
        return sql
    }

    /**
     * 生成 queryById 的 sql
     */
    private fun queryById(clazz: KClass<out Any>): String {
        val meta = EntityMeta.get(clazz)
        val (table, cols, props) = tableColsProps(meta, emptyList(), "")

        val columns = Array<String>(cols.size) { "`" + cols[it] + "` " }

        val id = Entity::id.name
        return "SELECT ${columns.joinToString(",")} FROM $table WHERE $id=:$id"
    }

    /**
     * 生成 deleteById 的 sql
     */
    private fun deleteById(clazz: KClass<out Any>): String {
        val meta = EntityMeta.get(clazz)
        val id = Entity::id.name
        return "DELETE FROM ${meta.table} WHERE $id=:$id"
    }

    /**
     * 生成 update by id 的sql
     * TODO entity的属性的值如果为空，则不应该被update
     */
    private fun updateByIdSql(clazz: KClass<out Any>): String {
        val meta = EntityMeta.get(clazz)

        val id = Entity::id.name
        val version = Entity::version.name

        val exclusion = arrayOf(id, version)
        val (table, cols, params) = tableColsProps(meta, exclusion.toList())

        val columns = Array<String>(cols.size) {
            cols[it] + "=" + params[it]
        }.joinToString(",")

        return "UPDATE $table SET $columns,$version=$version+1 WHERE $id=:$id"
    }

    /**
     * 生成 insert sql语句
     */
    private fun insertSql(clazz: KClass<out Any>): String {
        val meta = EntityMeta.get(clazz)

        val version = Entity::version.name

        val exclusion = arrayOf(version)
        val (table, cols, params) = tableColsProps(meta, exclusion.toList())

        val v = params.joinToString(",")
        val c = cols.joinToString(",")

        return "INSERT INTO $table($c,$version) VALUES ($v,1)"
    }

    /**
     * 从 meta 中获取数据库的：表名，字段名 以及 字段对应的entity的属性名
     * @param meta entity 的 metadata
     * @param exclusion 需要被过滤掉的属性
     * @param propPrefix 属性名前缀。默认属性名前面都带了冒号(:)以方便NamedPreparedStatement设置数据
     * @return Triple(表名,字段名数组,属性名数组)。
     */
    private fun tableColsProps(meta: EntityMeta, exclusion: List<String> = emptyList(), propPrefix: String = ":"): Triple<String, Array<String>, Array<String>> {
        val table = meta.table
        val cols = mutableListOf<String>()
        val params = mutableListOf<String>()

        var i = 0
        meta.prop2ColName.forEach {
            val prop = it.key.name
            if (!exclusion.contains(prop)) {
                cols.add(i, it.value)
                params.add(i, "$propPrefix$prop")
                i++
            }
        }
        return Triple(table, cols.toTypedArray(), params.toTypedArray())
    }
}