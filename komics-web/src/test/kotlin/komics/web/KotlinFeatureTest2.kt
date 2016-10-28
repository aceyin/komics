package komics.web

import org.hibernate.validator.constraints.NotEmpty
import org.junit.Test
import javax.persistence.Column
import javax.persistence.Id
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

/**
 * Created by ace on 2016/10/28.
 */


class KotlinFeatureTest2 {

    @Test
    fun test_get_annotation() {
        // can get field's annotation for BeanUseJPAAnnotation success
        println("Getting field annotations for BeanUseJPAAnnotation :")
        BeanUseJPAAnnotation::class.members.forEach {
            if (it is KProperty) {
                val field = it.javaField
                println("${field?.name}'s annotations:")
                field?.annotations?.forEachIndexed { i, an ->
                    println("        $i is: $an")
                }
            }
        }

        println("--------------------")
        println("Getting field annotations for BeanUseValidationAnnotation :")
        // CANT get field's annotation for BeanUseJPAAnnotation success
        BeanUseValidationAnnotation::class.members.forEach {
            if (it is KProperty) {
                val field = it.javaField
                println("${field?.name}'s annotations:")
                field?.annotations?.forEachIndexed { i, an ->
                    println("        $i is: $an")
                }
            }
        }


        println("--------------------")
        println("Getting field annotations for BeanUseValidationAnnotationOnMethod :")
        // CANT get field's annotation for BeanUseJPAAnnotation success
        BeanUseValidationAnnotationOnMethod::class.members.forEach {
            println("(${it.javaClass})${it.name}'s annotations: ")
            it.annotations.forEachIndexed { i, an ->
                println("        $i is: $an")
            }
        }
    }

    @Test
    fun test_inherit() {
        val c1 = Child1()
        c1.set("abc")

        val c2 = Child2()
        println("c2.size=${c2.size()}")
    }
}

abstract class Base {
    companion object {
        val list = mutableListOf<String>()
    }

    fun set(v: String) {
        list.add(v)
    }

    fun size(): Int {
        return list.size
    }
}

class Child1 : Base() {}

class Child2 : Base() {}


data class BeanUseJPAAnnotation(
        @Column(name = "id") @Id val id: String,
        @Column(name = "user_name") val name: String)


data class BeanUseValidationAnnotation(
        @NotEmpty(message = "name can not be empty")
        val name: String,

        @Min(value = 1)
        @Max(value = 100)
        val age: Int
)

data class BeanUseValidationAnnotationOnMethod(
        @get:NotEmpty(message = "name can not be empty")
        val name: String,

        @get:Min(value = 1)
        @get:Max(value = 100)
        val age: Int)