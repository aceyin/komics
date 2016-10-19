package komics.data

import io.kotlintest.specs.ShouldSpec
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.select.*
import javax.persistence.Column
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.declaredFunctions
import kotlin.reflect.functions
import kotlin.reflect.jvm.javaField

/**
 * Created by ace on 2016/10/12.
 */
class KotlinFeatureTest : ShouldSpec() {
    init {
        should("test copy") {
            val b = B("1", "B")
            val copy = b.copy(name = "C")
            b.id shouldBe copy.id
            copy.name shouldBe "C"
        }

        should("test_array") {
            val arr = Array<String>(2) {
                it ->
                "S$it"
            }
            arr.joinToString(",") shouldBe "S0,S1"
        }

        should("get annotation on interface property") {
            B::class.members.forEach {
                if (it is KProperty) {
                    val ann = it.javaField?.annotations?.find { a ->
                        a.annotationClass == Column::class
                    }
                    println("Field ${it.name}'s annotation is :$ann")

                    println("Get method's annotation:${it.getter.annotations}")
                } else if (it is KFunction) {
                    println(it.annotations)
                }
            }

            B::class.java.declaredMethods.forEach {
                println("KKKKK $it's annotation : ${it.annotations}")
                val ann = it.annotations.forEach { an ->
                    println("HHHHH ann = $an")
                }
            }
        }

        should("get interface annotation") {
            A::class.members.forEach {
                println("method $it's annotation is :${it.annotations}")
            }

            A::class.declaredFunctions.forEach {
                println("IIIII $it's annotation : ${it.annotations}")
            }

            A::class.functions.forEach {
                println("BBB $it's annotation : ${it.annotations}")
            }

            A::class.java.declaredMethods.forEach {
                it.annotations.forEach { an ->
                    println("CCC $it's annotation : ${an}")
                }
            }
        }

        should("parse where condition success from sql") {

            val sql = """select * from user
                    | where name=:name
                    | and email=:email
                    | or (id=:id)
                    | and a=:a and b between c and d or (e in (1,2,3))
                    | group by c,d,e,f,g
                    | order by h,i,j
                    | limit 15,10
                    """.trimMargin()

            val expression = CCJSqlParserUtil.parse(sql)
            if (expression is Select) {
                val body = expression.selectBody
                if (body is PlainSelect) {
                    val where = body.where
                    println(where)
                    val limit = body.limit
                    val offset = body.offset

                    println("$limit --- $offset")
                }
            }
        }

        should("test sql parse 2 ") {
            val sql = "select * from user where status=:status order by id limit 0,10"
            val exp = CCJSqlParserUtil.parse(sql)
            if (exp is Select) {
                val body = exp.selectBody
                if (body is PlainSelect) {
                    println(body.where)
                }
            }
        }

        should("get tables in join sql") {
            val sql = """select
  u.id,u.username,u.email,u.mobile,u.status,
  p.id `p.id`,p.name,p.gender,p.age
  from user u join user_profile p on u.id = p.user_id"""
            val exp = CCJSqlParserUtil.parse(sql)
            if (exp is Select) {
                val body = exp.selectBody
                if (body is PlainSelect) {
                    val from = body.fromItem as Table
                    val name = from.name
                    val alias = from.alias

                    println("table in select is :$name , $alias")

                    val joins = body.joins
                    joins.forEach {
                        val right = it.rightItem as Table
                        val joinName = right.name
                        val joinAlias = right.alias
                        println("tables in join are :$joinName , $joinAlias")
                    }
                }
            }
        }

        should("get tables in union style sql") {
            val sql = """select u.*,p.* from user u left join profile p on u.id = p.id
union all
select d.*,e.* from user_d d left join profile_e e on d.id = e.id
"""
            val exp = CCJSqlParserUtil.parse(sql)
            if (exp is Select) {
                val body = exp.selectBody
                if (body is SetOperationList) {
                    val selects = body.selects
                    selects.forEach {
                        if (it is PlainSelect)
                            println(it)
                    }
                }
            }
        }

        should("get tables in subjoin sql") {
            val sql = """
select u.* from (user u join profile p on u.id = p.id)
"""
            val exp = CCJSqlParserUtil.parse(sql)
            if (exp is Select) {
                val body = exp.selectBody
                if (body is PlainSelect) {
                    val from = body.fromItem
                    if (from is SubJoin) {
                        val left = from.left
                        val right = from.join

                        println("$left ---- $right")
                    }
                }
            }
        }

        should("get tables in sub select sql ") {
            val sql = """
select * from (select u.*,p.* from user u, profile p where u.id=pi.d)
"""
            val exp = CCJSqlParserUtil.parse(sql)
            if (exp is Select) {
                val body = exp.selectBody
                if (body is PlainSelect) {
                    val from = body.fromItem
                    if (from is SubSelect) {
                        val sb = from.selectBody
                    }
                }
            }
        }
    }

    interface A {
        @get:Column
        val id: String
    }

    data class B(
            override val id: String,
            @Column val name: String
    ) : A
}