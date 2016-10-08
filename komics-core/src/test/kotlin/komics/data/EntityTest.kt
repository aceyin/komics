package komics.data

import io.kotlintest.specs.ShouldSpec
import java.util.*
import javax.persistence.Column

/**
 * Created by ace on 2016/10/8.
 */

class EntityTest : ShouldSpec() {
    init {
        should("generate columns success with the column name from @Column annotation") {
            val ec = EClass("id", 1, Date(), Date(), "eclass")
            val cols = ec.cols()
            cols.size shouldBe 5
            cols shouldBe listOf("e_id", "version", "created", "updated", "name")

            val colstr = ec.colstr()
            colstr shouldBe "e_id,version,created,updated,name"
        }
    }

    data class EClass(
            @Column(name = "e_id")
            override var id: String,
            @Column
            override var version: Long,
            override var created: Date,
            override var updated: Date,
            var name: String
    ) : Entity
}