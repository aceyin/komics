package komics.data

import io.kotlintest.specs.ShouldSpec

/**
 * Created by ace on 2016/10/9.
 */

class EntityMetaTest : ShouldSpec() {
    init {
        should("get meta success") {
            val meta = EntityMeta.get(User::class)
            val columns = meta.columns()
            columns.sorted() shouldBe listOf("uuid", "_version", "created", "updated", "name").sorted()
        }
    }
}