package komics.data.jdbc.sql

import komics.data.Entity
import komics.data.EntityMeta
import kotlin.reflect.KClass

/**
 * Created by ace on 2016/10/11.
 */
class Sqls {

    enum class SqlType {
        INSERT, UPDATE, DELETE, SELECT
    }

    companion object {
        val SQL_CACHE = mutableMapOf<String, String>()
        /**
         * 从配置文件加载一个SQL
         * @param id 配置文件中的sql语句的ID
         */
        fun load(id: String): String {
            TODO("to be implemented")
        }

        /**
         * 从一个实体类中获取SQL
         */
        fun <E : Entity> get(clazz: KClass<E>, type: SqlType): String {
            val key = "${clazz.qualifiedName}_$type"
            var sql = SQL_CACHE[key] ?: ""
            if (sql.isNotEmpty()) return sql

            when (type) {
                SqlType.INSERT -> sql = insertSql(clazz)
            }

            SQL_CACHE.put(key, sql)

            return sql
        }

        private fun <E : Entity> insertSql(clazz: KClass<E>): String {
            val meta = EntityMeta.get(clazz)
            val cols = mutableListOf<String>()
            val params = mutableListOf<String>()

            meta.member2columnName.forEach {
                val col = it.value
                val prop = it.key
                cols.add(col)
                params.add(":${prop.name}")
            }

            val v = params.joinToString(",")
            val c = cols.joinToString(",")

            return "INSERT INTO ${meta.table}($c) VALUES ($v)"
        }
    }
}