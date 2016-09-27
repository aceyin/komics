package komics.core

import com.esotericsoftware.yamlbeans.YamlReader
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import java.io.InputStreamReader
import java.util.*

/**
 * Created by ace on 16/9/7.
 */
class Config {
    /* 已经转化为: key=value 形式的配置 */
    internal val PROPS: MutableMap<String, Any> = mutableMapOf<String, Any>()
    /* 原始的 yaml 配置 */
    internal val ORIGIN: MutableMap<String, Any> = mutableMapOf<String, Any>()

    /**
     * Config loader
     */
    companion object Loader {
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
                if (o is HashMap<*, *>) {
                    o.entries.forEach { it -> conf.ORIGIN.put(it.key as String, it.value) }
                    extractMap("", o, conf)
                } else LOGGER.warn("Ignore non-map configuration item: $o")
            }
            reader.close()
            return conf
        }

        private fun extractMap(preKey: String, map: HashMap<*, *>, conf: Config) {
            val entrySet = map.entries
            for ((k, v) in entrySet) {
                val key = if (preKey.isNullOrBlank()) k else preKey + "." + k
                if (v is HashMap<*, *>) extractMap(key as String, v, conf)
                else if (v is ArrayList<*>) extractList(key as String, v, conf)
                else conf.PROPS.put(key as String, v)
            }
        }

        private fun extractList(prefix: String, list: ArrayList<*>, conf: Config) {
            if (list.size == 0) conf.PROPS.put(prefix, list)
            else if (list[0] is HashMap<*, *>) {
                list.forEach { it ->
                    val p = prefix + "." + (list.indexOf(it) + 1)
                    if (it is HashMap<*, *>) extractMap(p, it, conf)
                    else conf.PROPS.put(p, it)
                }
            } else conf.PROPS.put(prefix, list)
        }
    }

    /**
     * 获取一个字符串列表
     *
     * @param key
     * @return
     */
    fun strs(key: String): List<String> {
        val o = PROPS[key] ?: return emptyList()
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
    fun str(key: String): String? {
        val v = PROPS[key] ?: return null
        if (v is String) return v
        else throw DataFormatException("Value $v for key $key is not a string ")
    }

    /**
     * 获取单个整形配置
     */
    fun int(key: String): Int? {
        val v = PROPS[key] ?: return null
        if (v is Int) return v
        else if (v is String && v.matches(Regex("\\d+"))) return v.toInt()
        else throw DataFormatException("Value $v for key $key is not a Int ")
    }

    /**
     * 获取整形配置列表
     */
    fun ints(key: String): List<Int>? {
        val v = PROPS[key] ?: return null
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
    fun bool(key: String): Boolean? {
        val o = PROPS.get(key) ?: return null
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
        val o = PROPS.get(key) ?: return emptyList<Boolean>()
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
    fun float(key: String): Float? {
        val o = PROPS.get(key) ?: return null
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
        val o = PROPS.get(key) ?: return emptyList()
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
