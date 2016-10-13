package komics.data.jdbc

import komics.util.EntityUtil
import org.junit.Test
import javax.persistence.Entity
import javax.persistence.Table
import kotlin.test.assertEquals

/**
 * Created by ace on 2016/10/7.
 */

class EntityUtilTest {

    @Test
    fun should_return_annotation_name_of_entity_class() {
        val table = EntityUtil.table(ATable::class)
        assertEquals(table, "a_table")
    }

    @Test
    fun should_return_class_name_if_no_annotation_name() {
        val table = EntityUtil.table(BClass::class)
        assertEquals(table, "BClass")
    }

    @Test
    fun should_return_class_name_if_no_annotation() {
        val table = EntityUtil.table(CClass::class)
        assertEquals(table, "CClass")
    }
}

@Entity
@Table(name = "a_table")
data class ATable(val name: String, override var id: String, override var version: Long) : komics.data.Entity

@Table
data class BClass(val name: String, override var id: String, override var version: Long) : komics.data.Entity

data class CClass(val name: String, override var id: String, override var version: Long) : komics.data.Entity