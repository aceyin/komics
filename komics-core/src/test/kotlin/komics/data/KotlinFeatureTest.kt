package komics.data

import io.kotlintest.specs.ShouldSpec
import javax.persistence.Column
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.declaredFunctions
import kotlin.reflect.functions
import kotlin.reflect.jvm.javaField

/**
 * Created by ace on 2016/10/12.
 */
class KotlinFeatureTest : ShouldSpec() {
    init {
        should("test copy") {
            val b = B("1", "B")
            val copy = b.copy(name = "C")
            b.id shouldBe copy.id
            copy.name shouldBe "C"
        }

        should("test_array") {
            val arr = Array<String>(2) {
                it ->
                "S$it"
            }
            arr.joinToString(",") shouldBe "S0,S1"
        }

        should("get annotation on interface property") {
            B::class.members.forEach {
                if (it is KProperty) {
                    val ann = it.javaField?.annotations?.find { a ->
                        a.annotationClass == Column::class
                    }
                    println("Field ${it.name}'s annotation is :$ann")

                    println("Get method's annotation:${it.getter.annotations}")
                } else if (it is KFunction) {
                    println(it.annotations)
                }
            }

            B::class.java.declaredMethods.forEach {
                println("KKKKK $it's annotation : ${it.annotations}")
                val ann = it.annotations.forEach { an ->
                    println("HHHHH ann = $an")
                }
            }
        }

        should("get interface annotation") {
            A::class.members.forEach {
                println("method $it's annotation is :${it.annotations}")
            }

            A::class.declaredFunctions.forEach {
                println("IIIII $it's annotation : ${it.annotations}")
            }

            A::class.functions.forEach {
                println("BBB $it's annotation : ${it.annotations}")
            }

            A::class.java.declaredMethods.forEach {
                it.annotations.forEach { an ->
                    println("CCC $it's annotation : ${an}")
                }
            }
        }
    }

    interface A {
        @get:Column
        val id: String
    }

    data class B(
            override val id: String,
            @Column val name: String
    ) : A
}