package komics.core

import com.esotericsoftware.yamlbeans.YamlReader
import komics.exception.DataFormatException
import org.springframework.core.io.ClassPathResource
import java.io.File
import java.io.InputStreamReader
import java.util.*


/**
 * The config of applicatioin
 */
class Config {
    /* 已经转化为: key=value 形式的配置 */
    internal var PROPS = mutableMapOf<String, Any>()
    /* 原始的 yaml 配置 */
    //TODO : 不应该把 origin 暴露出去，把origin封装到 Config 类里面。
    var ORIGIN = mutableMapOf<String, Any>()

    companion object {
        val CONF_FILE = "conf/application.yml"
        /**
         * Load the configuration from the given path.
         */
        internal fun load(path: String): Config {
            val instance = Config()

            val file = File(path.trim())
            val stream = if (file.exists() && file.isFile) file.inputStream()
            else ClassPathResource(path, Config::class.java.classLoader).file?.inputStream()

            if (stream == null) {
                println("No file found in path '$path', application will starting without configuration file")
                return instance
            }

            println("Loading configuration from file '$path' ")
            val reader = YamlReader(InputStreamReader(stream))
            while (true) {
                val o = reader.read() ?: break
                if (o is HashMap<*, *>) {
                    o.entries.forEach { it -> instance.ORIGIN.put(it.key as String, it.value) }
                    extractMap(instance, "", o)
                } else println("Skipping non-map configuration item: $o")
            }
            reader.close()
            return instance
        }

        private fun extractMap(instance: Config, preKey: String, map: HashMap<*, *>) {
            val entrySet = map.entries
            for ((k, v) in entrySet) {
                val key = if (preKey.isNullOrBlank()) k else preKey + "." + k
                if (v is HashMap<*, *>) extractMap(instance, key as String, v)
                else if (v is ArrayList<*>) extractList(instance, key as String, v)
                else instance.PROPS.put(key as String, v)
            }
        }

        private fun extractList(instance: Config, prefix: String, list: ArrayList<*>) {
            if (list.size == 0) instance.PROPS.put(prefix, list)
            else if (list[0] is HashMap<*, *>) {
                list.forEach { it ->
                    val p = prefix + "." + (list.indexOf(it) + 1)
                    if (it is HashMap<*, *>) extractMap(instance, p, it)
                    else instance.PROPS.put(p, it)
                }
            } else instance.PROPS.put(prefix, list)
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