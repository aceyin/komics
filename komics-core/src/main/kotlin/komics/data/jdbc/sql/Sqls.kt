package komics.data.jdbc.sql

import komics.data.Entity
import komics.data.EntityMeta
import kotlin.reflect.KClass

/**
 * Created by ace on 2016/10/11.
 */
class Sqls {

    enum class Predefine {
        insert, updateById, deleteById, queryById
    }

    companion object {
        private val SQL_ID_RGX = "(\\w+\\.)+(\\w+)@(\\w+)".toRegex()
        internal val SQL_CACHE = mutableMapOf<String, String>()
        /**
         * 从配置文件加载一个SQL
         * @param sqlid 配置文件中的sql语句的ID
         */
        fun get(sqlid: String): String {
            var sql = SQL_CACHE[sqlid] ?: ""
            if (sql.isNotEmpty()) return sql
            if (sqlid.matches(SQL_ID_RGX)) {
                val (clazz, sqltype) = sqlid.split("@")
                sql = load(clazz, Predefine.valueOf(sqltype))
                if (sql.isNotBlank())
                    SQL_CACHE.put(sqlid, sql)
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
            val id = Entity::id.name
            return "SELECT * FROM ${meta.table} WHERE $id=:$id"
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
            val (table, cols, params) = tableAndColumns(meta, arrayOf(id).toList())

            val update = Array<String>(cols.size) {
                cols[it] + "=" + params[it]
            }.joinToString(",")

            return "UPDATE $table SET $update WHERE $id=:$id"
        }

        /**
         * 生成 insert sql语句
         */
        private fun insertSql(clazz: KClass<out Any>): String {
            val meta = EntityMeta.get(clazz)
            val (table, cols, params) = tableAndColumns(meta)

            val v = params.joinToString(",")
            val c = cols.joinToString(",")

            return "INSERT INTO $table($c) VALUES ($v)"
        }

        /**
         * 从 meta 中获取数据库的：表名，字段名 以及 字段对应的entity的属性名
         * @param meta entity 的 metadata
         * @param exclusion 需要被过滤掉的属性
         * @return Triple(表名,字段名数组,属性名数组)。
         * 注：属性名前面都带了冒号(:)以方便NamedPreparedStatement设置数据
         */
        private fun tableAndColumns(meta: EntityMeta, exclusion: List<String> = emptyList()): Triple<String, Array<String>, Array<String>> {
            val table = meta.table
            val cols = mutableListOf<String>()
            val params = mutableListOf<String>()

            var i = 0
            meta.member2columnName.forEach {
                val prop = it.key.name
                if (!exclusion.contains(prop)) {
                    cols.add(i, it.value)
                    params.add(i, ":$prop")
                    i++
                }
            }
            return Triple(table, cols.toTypedArray(), params.toTypedArray())
        }
    }
}