package komics.data.jdbc.sql

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * TODO implement it
 */
class SqlBuilder {
    var clazz: KClass<out Any> = Unit::class
    var columns: List<KProperty<out Any>> = emptyList()
    var condition: MutableList<Condition> = mutableListOf<Condition>()
    var groupby: Array<KProperty<Any>> = emptyArray()
    var orderby: Array<SelectStatement.Order> = emptyArray()
    var limit: Array<Int> = emptyArray()

    companion object {
        fun select(vararg columns: KProperty<out Any>): SelectStatement {
            val sql = SqlBuilder()
            sql.columns = columns.asList()

            return SelectStatement(sql)
        }
    }

    /**
     * SQL 运算符
     */
    enum class OP(operator: String) {
        EQ("="),
        NE("!="),
        LT("<"),
        GT(">"),
        LE("<="),
        GE(">="),
        BETWEEN("between"),
        AND("and"),
        IN("in")
    }

    /**
     * order
     */
    enum class ODR(order: String) {
        ASC("asc"),
        DESC("desc")
    }

    class Condition(val prop: KProperty<out Any>, val op: OP, val value: Any)
}

/**
 * Equals, 对应 数据库的 = 操作
 */
infix fun KProperty<out Any>.EQ(value: Any): SqlBuilder.Condition = SqlBuilder.Condition(this, SqlBuilder.OP.EQ, value)

/**
 * Not Equals, 对应 数据库的 != 操作
 */
infix fun KProperty<out Any>.NE(value: Any): SqlBuilder.Condition = SqlBuilder.Condition(this, SqlBuilder.OP.NE, value)

/**
 * Less Than, 对应数据库的 < 操作
 */
infix fun KProperty<out Any>.LT(value: Any): SqlBuilder.Condition = SqlBuilder.Condition(this, SqlBuilder.OP.LT, value)

/**
 * Greater Than, 对应数据库的 > 操作
 */
infix fun KProperty<out Any>.GT(value: Any): SqlBuilder.Condition = SqlBuilder.Condition(this, SqlBuilder.OP.GT, value)

/**
 * Less or Equals Than, 对应数据库的 <= 操作
 */
infix fun KProperty<out Any>.LE(value: Any): SqlBuilder.Condition = SqlBuilder.Condition(this, SqlBuilder.OP.LE, value)

/**
 * Greater or Equals Than, 对应数据库的 >= 操作
 */
infix fun KProperty<out Any>.GE(value: Any): SqlBuilder.Condition = SqlBuilder.Condition(this, SqlBuilder.OP.GE, value)

/**
 * between, 对应数据库的 between and 操作
 */
infix fun KProperty<out Any>.BT(value: Array<out Any>): SqlBuilder.Condition {
    return SqlBuilder.Condition(this, SqlBuilder.OP.BETWEEN, value)
}

/**
 * in, 对应数据库的 in 操作
 */
infix fun KProperty<out Any>.IN(value: Array<Any>): SqlBuilder.Condition = SqlBuilder.Condition(this, SqlBuilder.OP.IN, value)
