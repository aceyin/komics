package komics.data.jdbc

import com.esotericsoftware.yamlbeans.YamlReader
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import java.io.InputStreamReader
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by ace on 2016/10/11.
 */
object Sql {

    enum class Predefine {
        insert, updateById, deleteById, queryById, queryByIds, count, queryAll, deleteAll, truncate
    }

    /**
     * 从配置文件加载一个SQL
     * @param sqlid 配置文件中的sql语句的ID
     */
    fun get(sqlid: String): String {
        var sql = Config.get(sqlid)
        if (sql.isNotEmpty()) return sql
        if (sqlid.matches(Config.SQL_ID_REGEX)) {
            val (clazz, sqltype) = sqlid.split("@")
            sql = load(clazz, Predefine.valueOf(sqltype))
            if (sql.isNotBlank())
                Config.add(sqlid, sql)
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
        return Config.count()
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
            Predefine.queryByIds -> sql = queryByIds(c)
            Predefine.count -> sql = count(c)
            Predefine.queryAll -> sql = queryAll(c)
            Predefine.deleteAll -> sql = deleteAll(c)
            Predefine.truncate -> sql = truncate(c)
        }
        return sql
    }

    /**
     * 生成 truncate 语句
     */
    private fun truncate(clazz: KClass<out Any>): String {
        val meta = EntityMeta.get(clazz)
        return "TRUNCATE TABLE ${meta.table}"
    }

    /**
     * 生成delete all sql 语句
     */
    private fun deleteAll(clazz: KClass<out Any>): String {
        val meta = EntityMeta.get(clazz)
        return "DELETE FROM ${meta.table}"
    }

    /**
     * 生成 select all SQL
     */
    private fun queryAll(clazz: KClass<out Any>): String {
        val meta = EntityMeta.get(clazz)
        val (table, cols) = tableCols(meta, emptyList(), "")
        val columns = Array<String>(cols.size) { "`" + cols[it] + "` " }
        return "SELECT ${columns.joinToString(",")} FROM $table"
    }

    /**
     * 生成Count sql
     */
    private fun count(clazz: KClass<out Any>): String {
        val meta = EntityMeta.get(clazz)
        return "SELECT COUNT(1) as num FROM ${meta.table}"
    }

    /**
     * 生成 query by ids 的sql
     */
    private fun queryByIds(clazz: KClass<out Any>): String {
        val meta = EntityMeta.get(clazz)
        val (table, cols) = tableCols(meta, emptyList(), "")
        val columns = Array<String>(cols.size) { "`" + cols[it] + "` " }
        val id = Entity::id.name
        return "SELECT ${columns.joinToString(",")} FROM $table WHERE $id in (:$id)"
    }

    /**
     * 生成 queryById 的 sql
     */
    private fun queryById(clazz: KClass<out Any>): String {
        val meta = EntityMeta.get(clazz)
        val (table, cols) = tableCols(meta, emptyList(), "")
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

        meta.prop2ColName.entries.forEachIndexed { i, entry ->
            val prop = entry.key.name
            if (!exclusion.contains(prop)) {
                cols.add(entry.value)
                params.add("$propPrefix$prop")
            }
        }
        return Triple(table, cols.toTypedArray(), params.toTypedArray())
    }

    private fun tableCols(meta: EntityMeta, exclusion: List<String> = emptyList(), propPrefix: String = ":"): Pair<String, Array<String>> {
        val table = meta.table
        val cols = mutableListOf<String>()

        meta.prop2ColName.entries.forEachIndexed { i, entry ->
            val prop = entry.key.name
            if (!exclusion.contains(prop)) {
                cols.add(entry.value)
            }
        }
        return Pair(table, cols.toTypedArray())
    }

    /**
     * SQL Config loader
     */
    object Config {
        val SQL_FILE = "conf/sqls.yml"
        val SQL_ID_REGEX = "(\\w+\\.)+(\\w+)@(\\w+)".toRegex()
        private val sqlCache = mutableMapOf<String, String>()
        private val LOGGER = LoggerFactory.getLogger(Config::class.java.name)

        /**
         * Get a sql according to the sqlid.
         */
        fun get(sqlId: String): String {
            val sql = this.sqlCache.get(sqlId)
            if (sql == null) {
                LOGGER.warn("No SQL found for id $sqlId")
                return ""
            }
            return sql
        }

        /**
         * Add a sql into config
         */
        fun add(sqlId: String, sql: String) {
            this.sqlCache.put(sqlId, sql)
        }

        /**
         * sql count
         */
        fun count(): Int {
            return this.sqlCache.size
        }

        /**
         * load from config file
         */
        fun load(path: String) {
            val resource = ClassPathResource(path, Config::class.java.classLoader)
            if (resource.file == null) {
                LOGGER.info("No $path found , no SQL will be loaded")
            }

            val reader = YamlReader(InputStreamReader(resource.inputStream))
            while (true) {
                val o = reader.read() ?: break
                if (o is HashMap<*, *>) {
                    o.forEach { key, value ->
                        if (key is String) {
                            if (value is String) sqlCache.put(key, value)
                            else if (value is Map<*, *>) Config.read(key, value)
                            else if (value is ArrayList<*>) Config.read(key, value)
                        } else {
                            LOGGER.warn("Skipping non-string key : $key")
                        }
                    }
                } else LOGGER.warn("Skipping non-map configuration item: $o")
            }
            reader.close()
        }

        private fun read(prefix: String, list: ArrayList<*>) {
            list.forEach {
                if (it is String) LOGGER.warn("Each SQL should have a name, skip no name SQL: $it")
                else if (it is Map<*, *>) {
                    Config.read(prefix, it)
                } else {
                    LOGGER.warn("The item of a yaml list should be Map or String, skip $it")
                }
            }
        }

        private fun read(prefix: String, map: Map<*, *>) {
            map.forEach { entry ->
                val key = entry.key
                if (key is String) {
                    val value = entry.value
                    if (value is String) {
                        sqlCache.put("$prefix@$key", value)
                    } else if (value is Map<*, *>) {
                        Config.read("$prefix@$key", value)
                    } else if (value is ArrayList<*>) {
                        Config.read("$prefix@$key", value)
                    } else {
                        LOGGER.warn("Skipping non-map configuration item: $value")
                    }
                } else {
                    LOGGER.warn("Skipping non-string key : $key")
                }
            }
        }
    }
}