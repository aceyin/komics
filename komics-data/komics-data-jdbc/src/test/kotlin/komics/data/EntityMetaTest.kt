package komics.data

import io.kotlintest.specs.ShouldSpec
import komics.model.Entity
import javax.persistence.Column

/**
 * Created by ace on 2016/10/9.
 */

class EntityMetaTest : ShouldSpec() {
    init {
        should("get meta success") {
            val meta = EntityMeta.get(Class4TestMeta::class)
            val columns = meta.columns()
            columns.sorted() shouldBe
                    listOf("id", "name" ).sorted()
        }
    }
}

data class Class4TestMeta(@Column val name: String,
                          override var id: String
) : Entity
