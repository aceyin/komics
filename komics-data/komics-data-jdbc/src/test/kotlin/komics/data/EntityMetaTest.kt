package komics.data

import io.kotlintest.specs.ShouldSpec
import komics.model.Entity
import javax.persistence.Column
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

/**
 * Created by ace on 2016/10/9.
 */

class EntityMetaTest : ShouldSpec() {
    init {
        should("get meta success") {
            val meta = EntityMeta.get(Class4TestMeta::class)
            val columns = meta.columns()
            columns.sorted() shouldBe
                    listOf("id", "user_name").sorted()
        }

        should("get annotaton on entity") {
            Class4TestMeta::class.members.forEach {
                if (it is KProperty) {
                    val field = it.javaField
                    println("_____" + field?.annotations?.size)
                }
            }
        }
    }
}

data class Class4TestMeta(@Column(name = "user_name") val name: String,
                          override var id: String
) : Entity
