package komics.data.jdbc

import komics.data.Entity
import komics.test.db.DaoTestBase
import org.junit.Ignore
import org.junit.Test
import javax.persistence.JoinColumn
import javax.persistence.OneToOne

/**
 * 测试Db的One to One 加载功能
 */
@Ignore
class One2OneTest {

    val db = Db(DaoTestBase.datasource)
    val sql = """
            |create table User4One2OneTest (id varchar(32),version bigint(11), created bigint(11),modified bigint(11));
            |create table Car4One2OneTest (id varchar(32),user_id varchar(32),version bigint(11), created bigint(11),modified bigint(11));
        """.trimMargin()

    init {
        DaoTestBase.createTable(sql)
    }

    @Test
    fun should_load_ref_object() {

    }

    fun createUser(): User4One2OneTest {
        return User4One2OneTest(Entity.IdGen.next())
    }

    fun createCar(user: User4One2OneTest): Car4One2OneTest {
        return Car4One2OneTest(Entity.IdGen.next(), user)
    }
}

data class User4One2OneTest(override var id: String) : Entity

data class Car4One2OneTest(override var id: String, @OneToOne @JoinColumn(name = "user_id") val user: User4One2OneTest) : Entity