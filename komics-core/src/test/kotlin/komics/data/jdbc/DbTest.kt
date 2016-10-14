package komics.data.jdbc

import komics.core.SqlConfig
import komics.data.User
import komics.test.db.DaoTestBase
import org.junit.Before
import org.junit.Test
import org.springframework.core.io.ClassPathResource
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

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

    @Before
    fun sleep() {
        // sleep to make H2 database free
        Thread.sleep(300)
    }

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

        db.deleteById(User::class, user.id)
        val list2 = DaoTestBase.query("select * from user where id = '${user.id}'")
        assertEquals(list2.size, 0)
    }

    @Test
    fun should_query_by_id_success() {
        val user = createUser()
        db.insert(user)

        val user2 = db.queryById(User::class, user.id)
        assertEquals(user2?.username, user.username)
    }

    val update_sql = "update user set username=:username, email=:email,passwd=:password where id=:id and version=:version"
    @Test
    fun should_update_by_sql_for_an_entity_success() {
        val user = createUser()
        db.insert(user)
        // update
        SqlConfig.add("manual-sql", update_sql)

        val u = user.copy(username = "hahahaha", email = "hehe@111.com", password = "111111")
        val n = db.update("manual-sql", u)

        assertEquals(true, n)

        val qu = db.queryById(User::class, u.id)
        assertEquals("hehe@111.com", qu?.email)
        assertEquals("hahahaha", qu?.username)
        assertEquals("111111", qu?.password)
    }

    @Test
    fun should_update_by_param_success() {
        val user = createUser()
        db.insert(user)
        // update

        SqlConfig.add("manual-sql", update_sql)
        val n = db.update("manual-sql", mapOf(
                "version" to user.version,
                "id" to user.id,
                "username" to "111",
                "email" to "aaa",
                "password" to "333"))

        assertEquals(true, n)

        val qu = db.queryById(User::class, user.id)
        assertEquals("aaa", qu?.email)
        assertEquals("111", qu?.username)
        assertEquals("333", qu?.password)
    }

    @Test
    fun should_batch_update_entities_success() {
        val users = Array(3) { createUser() }

        db.batchInsert(*users)

        users.forEachIndexed { i, user -> user.password = "password:$i" }
        sleep()

        SqlConfig.add("update-by-id", "update user set passwd=:password where id=:id")
        db.batchUpdate("update-by-id", *users)
        sleep()
        val list = DaoTestBase.query("select count(1) num from user where passwd in ('password:0','password:1','password:2')")
        assertEquals("3", list[0]["num"].toString())
    }

    @Test
    fun should_batch_update_by_sql_and_param_success() {
        val users = Array(3) { createUser() }

        db.batchInsert(*users)

        SqlConfig.add("update-by-id", "update user set username=:username where id=:id")

        sleep()

        db.batchUpdate("update-by-id", mapOf("username" to "username0", "id" to users[0].id),
                mapOf("username" to "username1", "id" to users[1].id),
                mapOf("username" to "username2", "id" to users[2].id)
        )

        val list = DaoTestBase.query("select count(1) num from user where username in ('username0','username1','username2')")
        assertEquals("3", list[0]["num"].toString())
    }

    @Test
    fun should_delete_by_ids_success() {
        val users = Array(3) { createUser() }
        val ids = Array(3) { users[it].id }.asList()
        val (id1, id2, id3) = ids

        db.batchInsert(*users)
        val list1 = DaoTestBase.query("select count(1) num from user where id in ('$id1','$id2','$id3') ")
        assertEquals("3", list1[0]["num"])

        db.deleteByIds(User::class, ids)

        val list = DaoTestBase.query("select count(1) num from user where id in ('$id1','$id2','$id3') ")
        assertEquals("0", list[0]["num"])
    }


    @Test
    fun should_delete_entity_by_sql_success() {
        val user = createUser()
        db.insert(user)

        val u = db.queryById(User::class, user.id)
        assertEquals(user.email, u?.email)

        SqlConfig.add("delete-an-entity", "delete from user where username=:username and passwd=:password")
        db.delete("delete-an-entity", user)

        val u2 = db.queryById(User::class, user.id)
        assertNull(u2)
    }

    @Test
    fun should_query_by_ids_success() {
        val users = Array(3) { createUser() }
        db.batchInsert(*users)
        val ids = Array(3) { users[it].id }.asList()
        val list = db.queryByIds(User::class, ids)
        assertEquals(3, list.size)
    }

    @Test
    fun should_query_by_sql_and_param_success() {
        val user = createUser()
        db.insert(user)

        val sqlId = "query-by-sql-and-param"
        SqlConfig.add(sqlId, "select * from user where email=:email and passwd=:password")
        val list = db.query(User::class, sqlId, mapOf("email" to user.email, "password" to user.password))
        assertEquals(1, list.size)
        assertEquals(user.mobile, list[0].mobile)
    }

    @Test
    fun should_query_by_sql_and_param_using_in_syntax_success() {
        val users = Array<User>(3) { createUser() }
        db.batchInsert(*users)

        val sqlid = "query-users-by-username-use-in"
        SqlConfig.add(sqlid, "select * from user where username in (:username)")

        val names = Array<String>(3) { users[it].username }.asList()

        val list = db.query(User::class, sqlid, mapOf("username" to names))
        assertEquals(3, list.size)
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

