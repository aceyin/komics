package komics.core

import com.esotericsoftware.yamlbeans.YamlReader
import komics.exception.DataFormatException
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import java.io.InputStreamReader
import java.util.*

/**
 * Created by ace on 16/9/7.
 */
class Config {
    internal val HOLDER: MutableMap<String, Any> = mutableMapOf()

    companion object {
        val CONF_FILE = "conf/application.yml"
        val EMPTY_CONF: Config = Config()
        private val LOGGER = LoggerFactory.getLogger(Config::class.java.name)
        /**
         * Load the configuration from the given path.
         */
        fun load(path: String): Config {
            val resource = ClassPathResource(path, Config::class.java.classLoader)
            if (resource.file == null) {
                LOGGER.warn("No $path found , application will start without configuration file.")
                return EMPTY_CONF
            }
            val conf = Config()
            val reader = YamlReader(InputStreamReader(resource.inputStream))
            while (true) {
                val o = reader.read() ?: break
                if (o is HashMap<*, *>) extractConfig("", o, conf)
                else LOGGER.info("Ignore non-map configuration item: $o")
            }
            reader.close()
            return conf
        }

        private fun extractConfig(key: String, map: HashMap<*, *>, conf: Config) {
            val entrySet = map.entries
            for ((k, v) in entrySet) {
                val k2 = if ("" === key) k else key + "." + k
                if (v is HashMap<*, *>) extractConfig(k2 as String, v, conf)
                else conf.HOLDER.put(k2 as String, v)
            }
        }
    }

    /**
     * 获取一个字符串列表
     *
     * @param key
     * @return
     */
    fun strs(key: String): List<String> {
        val o = HOLDER[key] ?: return emptyList()
        val res = mutableListOf<String>()
        when (o) {
            is ArrayList<*> -> o.forEach {
                if (it is String) res.add(it)
                else throw DataFormatException("Value $it for key $key is not a string ")
            }
            is String -> res.add(o)
            else -> throw DataFormatException("Value $o for key $key is not a string ")
        }
        return res
    }

    /**
     * 获取单个字符串配置
     */
    fun strv(key: String): String? {
        val v = HOLDER[key] ?: return null
        if (v is String) return v
        else throw DataFormatException("Value $v for key $key is not a string ")
    }

    /**
     * 获取单个整形配置
     */
    fun intv(key: String): Int? {
        val v = HOLDER[key] ?: return null
        if (v is Int) return v
        else if (v is String && v.matches(Regex("\\d+"))) return v.toInt()
        else throw DataFormatException("Value $v for key $key is not a Int ")
    }

    /**
     * 获取整形配置列表
     */
    fun ints(key: String): List<Int>? {
        val v = HOLDER[key] ?: return null
        if (v is Int) return listOf(v)
        else if (v is ArrayList<*>) {
            val list = mutableListOf<Int>()
            for (i in v) {
                if (i is Int) list.add(i)
                else if (i is String && i.matches(Regex("\\d+"))) list.add(i.toInt())
                else throw DataFormatException("Value $v for key $key is not a Int ")
            }
            return list
        } else throw DataFormatException("Value $v for key $key is not a int list")
    }

    /**
     * 获取boolean值
     * @param key
     * @return
     */
    fun boolv(key: String): Boolean? {
        val o = HOLDER.get(key) ?: return null
        if (o is Boolean) return o
        else if (o is String) return o.toBoolean()
        else throw DataFormatException("Value of '$key' is not a boolean type: $o")
    }

    /**
     * 获取boolean列表
     * @param key
     * @return
     */
    fun bools(key: String): List<Boolean> {
        val o = HOLDER.get(key) ?: return emptyList<Boolean>()
        if (o is ArrayList<*>) {
            val res = mutableListOf<Boolean>()
            o.forEach { i ->
                if (i is Boolean) res.add(i)
                else if (i is String) res.add(i.toBoolean())
                else throw DataFormatException("Value of '$key' is not a valid boolean: $i")
            }
            return res
        } else if (o is String) return listOf(o.toBoolean())
        else
            throw DataFormatException("Value of '$key' is not a valid boolean: $o")
    }

    /**
     * 获取float值
     * @param key
     * @return
     */
    fun floatv(key: String): Float? {
        val o = HOLDER.get(key) ?: return null
        if (o is Float) return o
        else if (o is String) return o.toFloat()
        else throw DataFormatException("Value of '$key' is not a valid number format: $o")
    }

    /**
     * get float list
     * @param key
     * @return
     */
    fun floats(key: String): List<Float> {
        val o = HOLDER.get(key) ?: return emptyList()
        if (o is ArrayList<*>) {
            val res = mutableListOf<Float>()
            o.forEach { i ->
                if (i is Float) res.add(i)
                else if (i is String) res.add(i.toFloat())
                else throw DataFormatException("Value of '$key' is not a valid number format: $i")
            }
            return res
        } else if (o is String) return listOf(o.toFloat())
        throw DataFormatException("Value of '$key' is not a valid number format: $o")
    }
}
