package komics.model

import com.avaje.ebean.EbeanServerFactory
import com.avaje.ebean.config.ServerConfig
import komics.test.db.DaoTestBase
import org.junit.BeforeClass
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

/**
 * Created by ace on 16/9/13.
 */
class UserTest : DaoTestBase() {

    companion object {
        @BeforeClass
        @JvmStatic fun beforeAll() {
            val url = UserTest::class.java.getResource("/sqls")
            createTables(url.path)
        }
    }

    @Test
    fun test() {
        val user = User()

        val clz = User::class.java

        kotlin.with(user) {
            version = 1
            created = Date(System.currentTimeMillis())
            modified = Date(System.currentTimeMillis())
            username = "aceyin"
            email = "ync@163.com"
            password = "123456"
            mobile = "13800138000"
            status = "0"
        }

        val cfg = ServerConfig()
        kotlin.with(cfg) {
            dataSource = DaoTestBase.datasource
            name = "default-ebean-server"
        }

        val server = EbeanServerFactory.create(cfg)
        server.save(user)

        val data = query("select * from USER where username = '${user.username}'")
        assertEquals("ync@163.com", data["email"])
    }
}