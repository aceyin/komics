package komics.data.jdbc

import komics.data.User
import komics.test.db.DaoTestBase
import org.junit.Test
import org.springframework.core.io.ClassPathResource
import java.util.*
import kotlin.test.assertEquals

/**
 * Created by ace on 2016/10/7.
 */

class DbTest {

    companion object {
        init {
            val path = ClassPathResource("/src/test/resources/tables.sql", DbTest::class.java.classLoader).path
            DaoTestBase.createTables(path)
        }
    }

    val db = Db(DaoTestBase.datasource)

    @Test
    fun should_insert_single_data_success() {
        val user = createUser()

        val inserted = db.insert(user)
        assertEquals(inserted, true)
        val map = DaoTestBase.query("select * from user where id = '${user.id}'")
        assertEquals(map[0]["username"], user.username)
    }

    @Test
    fun should_batch_insert_entities_success() {
        val users = Array<User>(3) {
            createUser()
        }
        val res = db.batchInsert(users.toList())
        assertEquals(res, true)
        val map = DaoTestBase.query("select * from user where id in ('${users[0].id}','${users[1].id}','${users[2].id}') ")
        assertEquals(map.size, 3)
    }

    @Test
    fun should_update_by_id_success() {
        val user = createUser()
        db.insert(user)
        val list1 = DaoTestBase.query("select * from user where id = '${user.id}'")
        assertEquals(list1[0]["version"].toString(), 1.toString())

        val copy = user.copy(password = "new-password")
        db.updateById(user.id, copy)

        val list = DaoTestBase.query("select * from user where id = '${user.id}'")
        assertEquals(list[0]["passwd"], copy.password)
        assertEquals(list[0]["version"].toString(), 2.toString())
    }

    @Test
    fun should_delete_by_id_success() {
        val user = createUser()
        db.insert(user)
        val list = DaoTestBase.query("select * from user where id = '${user.id}'")
        assertEquals(list[0]["username"], user.username)

        db.deleteById(user.id, User::class)
        val list2 = DaoTestBase.query("select * from user where id = '${user.id}'")
        assertEquals(list2.size, 0)
    }

    @Test
    fun should_query_by_id_success() {
        val user = createUser()
        db.insert(user)

        val user2 = db.queryById(user.id, User::class)
        assertEquals(user2?.username, user.username)
    }

    fun createUser(): User {
        val (em, pw, nm, mo) = arrayOf(
                (Math.random() * 1000).toInt(),
                (Math.random() * 1000).toInt(),
                (Math.random() * 1000).toInt(),
                (Math.random() * 1000).toInt())
        return User(
                id = UUID.randomUUID().toString().replace("-".toRegex(), ""),
                version = 1,
                email = "u$em@163.com",
                password = "$pw",
                username = "test-user-$nm",
                mobile = "13800138$mo",
                status = 100
        )
    }
}

