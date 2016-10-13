package komics.core

import com.esotericsoftware.yamlbeans.YamlReader
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import java.io.InputStreamReader
import java.util.*

/**
 * Created by ace on 2016/10/13.
 */
object SqlConfig {
    val SQL_FILE = "conf/sqls.yml"
    private val sqlCache = mutableMapOf<String, String>()
    private val LOGGER = LoggerFactory.getLogger(SqlConfig::class.java.name)

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
    internal fun load(path: String) {
        val resource = ClassPathResource(path, SqlConfig::class.java.classLoader)
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
                        else if (value is Map<*, *>) read(key, value)
                        else if (value is ArrayList<*>) read(key, value)
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
                read(prefix, it)
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
                    read("$prefix@$key", value)
                } else if (value is ArrayList<*>) {
                    read("$prefix@$key", value)
                } else {
                    LOGGER.warn("Skipping non-map configuration item: $value")
                }
            } else {
                LOGGER.warn("Skipping non-string key : $key")
            }
        }
    }
}