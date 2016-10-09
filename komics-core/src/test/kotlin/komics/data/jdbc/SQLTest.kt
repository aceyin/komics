package komics.data.jdbc

/**
 * Created by ace on 2016/10/1.
 */
import io.kotlintest.specs.ShouldSpec
import komics.data.User
import java.util.*
import javax.persistence.Column
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

class SQLTest : ShouldSpec() {
    init {
        should("根据Entity类生成正确的SQL") {
            val sql = Sql.select("id,name")
            val name = User::name
            test(User::name, User::updated)

            val u = User("1", 1, Date(), Date(), "user")

            val m = User::class.members
            m.forEach {
                if (it is KProperty) {
                    println("member name = " + it.name)
                    val anns = it.javaField?.annotations
                    if (anns !== null && anns.size > 0) {
                        val ann = anns.find { a -> a.annotationClass == Column::class } as? Column
                        val column = if (ann?.name.isNullOrEmpty()) it.name else ann?.name
                        println("column name = $column")
                    }
                }
            }
        }

        should("test foreach break") {
            (1..20).forEach lab@ {
                if (it % 5 == 0) return@lab
                else println("it=$it")
            }
        }

        should("get field annotation") {
            println(anno(User::class))
        }
    }

    fun anno(clazz: KClass<out Any>): String {
        clazz.members.forEach { p ->
            if (p is KProperty) {
                val anns = p.javaField?.declaredAnnotations
                if (anns == null || anns.size == 0) {
                    println("annotation of $p is empty")
                } else {
                    val col = anns.find { it.annotationClass == Column::class } as? Column
                    if (col != null) {
                        println("column for $p is $col")
                    } else {
                        println("no column annotation found for $p")
                    }
                }
            }
        }
        return ""
    }

    fun test(vararg property: KProperty<out Any>) {
        property.forEach {
            println(it.name + ",")
            val anno = it.annotations
            if (anno?.size > 0) {
                println(anno[0])
            }
        }
    }
}


