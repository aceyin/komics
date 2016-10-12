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
            map[0]["USERNAME"] shouldBe user.username
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

            val copy = user.copy(updated = Date().time)
            db.updateById(user.id, copy)

            val list = DaoTestBase.query("select * from user where id = '${user.id}'")
            list[0]["UPDATED"].toString() shouldBe copy.updated.toString()
        }
    }

    fun createUser(): User {
        val em = (Math.random() * 1000).toInt()
        val pw = (Math.random() * 1000).toInt()
        val nm = (Math.random() * 1000).toInt()
        val mo = (Math.random() * 1000).toInt()
        return User(
                id = UUID.randomUUID().toString().replace("-".toRegex(), ""),
                created = Date().time,
                updated = -1,
                version = 1,
                email = "u$em@163.com",
                password = "$pw",
                username = "test-user-$nm",
                mobile = "13800138$mo",
                status = 100
        )
    }
}