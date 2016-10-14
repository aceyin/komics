package komics.data.jdbc

import komics.data.User
import komics.test.db.DaoTestBase
import org.junit.Before
import org.junit.Test
import org.springframework.core.io.ClassPathResource
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
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
        Sql.Config.add("manual-sql", update_sql)

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

        Sql.Config.add("manual-sql", update_sql)
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

        Sql.Config.add("update-by-id", "update user set passwd=:password where id=:id")
        db.batchUpdate("update-by-id", *users)
        sleep()

        val params = mapOf("password" to listOf("password:0", "password:1", "password:2"))

        val sqlId = "select-by-password"
        Sql.Config.add(sqlId, "select * from user where passwd in (:password)")

        val list = db.query(User::class, sqlId, params)
        assertEquals(3, list.size)
    }

    @Test
    fun should_batch_update_by_sql_and_param_success() {
        val users = Array(3) { createUser() }

        db.batchInsert(*users)

        Sql.Config.add("update-by-id", "update user set username=:username where id=:id")

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

        Sql.Config.add("delete-an-entity", "delete from user where username=:username and passwd=:password")
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
        Sql.Config.add(sqlId, "select * from user where email=:email and passwd=:password")
        val list = db.query(User::class, sqlId, mapOf("email" to user.email, "password" to user.password))
        assertEquals(1, list.size)
        assertEquals(user.mobile, list[0].mobile)
    }

    @Test
    fun should_query_by_sql_and_param_using_in_syntax_success() {
        val users = Array<User>(3) { createUser() }
        db.batchInsert(*users)

        val sqlid = "query-users-by-username-use-in"
        Sql.Config.add(sqlid, "select * from user where username in (:username)")

        val names = Array<String>(3) { users[it].username }.asList()

        val list = db.query(User::class, sqlid, mapOf("username" to names))
        assertEquals(3, list.size)
    }

    @Test
    fun should_insert_success_use_sql_and_param() {
        val sqlId = "insert-by-sql-and-param"
        Sql.Config.add(sqlId, "insert into user (id,version,email,passwd,username,mobile,status) values (:id,:version,:email,:passwd,:username,:mobile,:status)")

        val user = createUser()

        db.insert(sqlId, mapOf(
                "id" to user.id,
                "version" to user.version,
                "email" to user.email,
                "passwd" to user.password,
                "username" to user.username,
                "mobile" to user.mobile,
                "status" to user.status
        ))

        val u = db.queryById(User::class, user.id)
        assertEquals(user.username, u?.username)
    }

    @Test
    fun should_batch_insert_success_with_sql_and_param() {
        val sqlId = "insert-by-sql-and-param"
        Sql.Config.add(sqlId, "insert into user (id,version,email,passwd,username,mobile,status) values (:id,:version,:email,:passwd,:username,:mobile,:status)")

        val users = Array<User>(3) { createUser() }
        val maps = Array<Map<String, Any>>(3) {
            mapOf(
                    "id" to users[it].id,
                    "version" to users[it].version,
                    "email" to users[it].email,
                    "passwd" to users[it].password,
                    "username" to users[it].username,
                    "mobile" to users[it].mobile,
                    "status" to users[it].status
            )
        }

        db.batchInsert(sqlId, *maps)
        val ids = Array<String>(3) { users[it].id }
        val list = db.queryByIds(User::class, *ids)
        assertEquals(3, list.size)
    }

    @Test
    fun should_delete_by_sql_and_param_success() {
        val user = createUser()
        db.insert(user)

        val u = db.queryById(User::class, user.id)
        assertEquals(u?.username, user.username)

        val sqlId = "delete-by-sql-and-param"
        Sql.Config.add(sqlId, "delete from user where username=:username")

        db.delete(sqlId, mapOf("username" to user.username))
        val u2 = db.queryById(User::class, user.id)
        assertNull(u2)
    }

    @Test
    fun should_batch_delete_entities_success() {
        val users = Array<User>(3) { createUser() }
        db.batchInsert(*users)

        val ids = Array<String>(3) { users[it].id }
        sleep()
        val list = db.queryByIds(User::class, *ids)
        assertEquals(3, list.size)

        val sqlId = "batch-delete-users-by-username"
        Sql.Config.add(sqlId, "delete from user where username=:username")

        sleep()
        db.batchDelete(sqlId, *users)
        val list2 = db.queryByIds(User::class, *ids)
        assertEquals(true, list2.isEmpty())
    }

    @Test
    fun should_batch_delete_by_sql_and_param_success() {
        val users = Array<User>(3) { createUser() }
        db.batchInsert(*users)
        val ids = Array<String>(3) { users[it].id }
        val list = db.queryByIds(User::class, *ids)
        assertEquals(3, list.size)

        val sqlId = "batch-delete-by-sql-and-param"
        Sql.Config.add(sqlId, "delete from user where email=:email and passwd=:password")

        val params = Array<Map<String, Any>>(3) {
            mapOf(
                    "email" to users[it].email,
                    "password" to users[it].password
            )
        }
        db.batchDelete(sqlId, *params)
        val list2 = db.queryByIds(User::class, *ids)
        assertEquals(true, list2.isEmpty())
    }

    @Test
    fun should_query_by_sql_and_entity_success() {
        val user = createUser()
        db.insert(user)
        val u = db.queryById(User::class, user.id)
        assertEquals(user.username, u?.username)

        val sqlId = "query-by-sql-and-entity"
        Sql.Config.add(sqlId, "select * from user where username=:username and email=:email")
        val u3 = db.query(sqlId, user)
        assertEquals(user.id, u3[0]?.id)
    }

    @Test
    fun should_count_by_entity_class_success() {
        val n = db.count(User::class)
        val user = createUser()
        db.insert(user)
        val m = db.count(User::class)
        assertEquals(n + 1, m)
    }

    @Test
    fun should_count_by_sql_and_param_success() {
        val user = createUser()
        db.insert(user)
        val sqlId = "count-by-param-and-sql"
        Sql.Config.add(sqlId, "select count(1) num from user where username=:username and passwd=:password")

        val c = db.count(sqlId, user)
        assertEquals(1, c)

        val d = db.count(sqlId, mapOf("username" to user.username, "password" to user.password))
        assertEquals(1, d)
    }

    @Test
    fun should_query_data_by_page_success() {
        db.delete(User::class)
        sleep()

        val users = Array(10) { createUser() }
        db.batchInsert(*users)

        sleep()
        val page = db.pageQuery(User::class, 0, 0)
        assertEquals(0, page.rowNum)

        val page2 = db.pageQuery(User::class, 1, 3)
        assertEquals(10, page2.rowNum)
        assertEquals(4, page2.pageNum)
        assertEquals(3, page2.data.size)

        val sqlId = "page-query-by-status"
        Sql.Config.add(sqlId, "select * from user where status=:status order by id")

        val page3 = db.pageQuery(User::class, sqlId, mapOf("status" to users[0].status), 1, 5)
        assertEquals(10, page3.rowNum)
        assertEquals(2, page3.pageNum)
        assertEquals(5, page3.data.size)
        assertEquals(1, page3.current)

        val page5 = db.pageQuery(User::class, sqlId, mapOf("status" to users[0].status), 2, 5)
        assertEquals(10, page3.rowNum)
        assertEquals(2, page3.pageNum)
        assertEquals(5, page3.data.size)
        assertEquals(2, page5.current)
        assertNotEquals(page3.data[0].id, page5.data[0].id)

        val sqlId2 = "page-query-by-status-with-limit"
        Sql.Config.add(sqlId2, "select * from user where status=:status order by id limit 0,10")

        val page4 = db.pageQuery(User::class, sqlId2, mapOf("status" to users[0].status), 1, 5)
        assertEquals(10, page4.rowNum)
        // 因为指定了limit部分，所以结果就不准了
        assertEquals(2, page4.pageNum)
        assertEquals(10, page4.data.size)
    }

    fun createUser(): User {
        val (email, passwd, name, mobile) = arrayOf(
                (Math.random() * 100000).toInt(),
                (Math.random() * 1000).toInt(),
                (Math.random() * 100000).toInt(),
                (Math.random() * 1000).toInt())
        return User(
                id = UUID.randomUUID().toString().replace("-".toRegex(), ""),
                version = 1,
                email = "u$email@163.com",
                password = "$passwd",
                username = "test-user-$name",
                mobile = "13800138$mobile",
                status = 100
        )
    }
}

