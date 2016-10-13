package komics.data.jdbc

import io.kotlintest.specs.ShouldSpec
import komics.data.User
import komics.test.db.DaoTestBase
import java.util.*

/**
 * Created by ace on 2016/10/7.
 */

class DbTest : ShouldSpec() {
    val db = Db(DaoTestBase.datasource)

    override fun beforeAll() {
        DaoTestBase.createTables("/Users/ace/Documents/workspace/git/komics/komics-core/src/test/resources/tables.sql")
    }

    init {
        should("insert single data success") {
            val user = createUser()

            val inserted = db.insert(user)
            inserted shouldBe true
            val map = DaoTestBase.query("select * from user where id = '${user.id}'")
            map[0]["username"] shouldBe user.username
        }

        should("batch insert entities success") {
            val users = Array<User>(3) {
                createUser()
            }
            val res = db.batchInsert(users.toList())
            res shouldBe true
            val map = DaoTestBase.query("select * from user where id in ('${users[0].id}','${users[1].id}','${users[2].id}') ")
            map.size shouldBe 3
        }

        should("update by id success") {
            val user = createUser()
            db.insert(user)
            val list1 = DaoTestBase.query("select * from user where id = '${user.id}'")
            list1[0]["version"].toString() shouldBe 1.toString()

            val copy = user.copy(password = "new-password")
            db.updateById(user.id, copy)

            val list = DaoTestBase.query("select * from user where id = '${user.id}'")
            list[0]["passwd"] shouldBe copy.password
            list[0]["version"].toString() shouldBe 2.toString()
        }

        should("delete by id success") {
            val user = createUser()
            db.insert(user)
            val list = DaoTestBase.query("select * from user where id = '${user.id}'")
            list[0]["username"] shouldBe user.username

            db.deleteById(user.id, User::class)
            val list2 = DaoTestBase.query("select * from user where id = '${user.id}'")
            list2.size shouldBe 0
        }

        should("query by id success") {
            val user = createUser()
            db.insert(user)

            val user2 = db.queryById(user.id, User::class)
            user2?.username shouldBe user.username
        }
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