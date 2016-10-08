package komics.data.jdbc

/**
 * Created by ace on 2016/10/1.
 */
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

class SQLTest {
    init {
//        should("根据Entity类生成正确的SQL") {
//            val sql = SQL.select("id,name")
//                    .from("user")
//                    .where("id=:id")
//                    .orderBy("id desc")
//                    .limit(0, 1)
//            sql.toString() shouldBe "SELECT id,name FROM user WHERE id=:id ORDER BY id desc LIMIT 0, 1"
//        }
//
//        should("根据给定的表名生成正确的SQL") {
//            val sql = SQL.select().from("user")
//            sql.toString() shouldBe "SELECT * FROM user"
//        }
//
//        should("不管方法调用的顺序如何，都应该生成正确的SQL") {
//            val sql = SQL.select("id,name")
//                    .where("id=:id")
//                    .from(User::class)
//                    .limit(0, 1)
//                    .orderby("id desc")
//            sql.toString() shouldBe "SELECT id,name FROM user WHERE id=:id ORDER BY id desc LIMIT 0, 1"
//        }
//
//        should("没有From的SQL生成失败") {
//            val sql = SQL.select().where("1=1").limit(0, 1)
//
//        }
    }
}

@Entity @Table(name = "user")
data class User(
        @Column
        override var id: String,
        @Column
        override var version: Long,
        @Column
        override var created: Date,
        @Column
        override var updated: Date,
        @Column
        val name: String) : komics.data.Entity

