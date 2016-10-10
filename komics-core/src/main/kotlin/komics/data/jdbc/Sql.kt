package komics.data.jdbc

import komics.data.Entity
import komics.data.EntityMeta
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * TODO implement it
 */
class Sql {
    var clazz: KClass<out Any> = Unit::class
    var columns: List<KProperty<out Any>> = emptyList()
    var condition = mutableListOf<Condition>()

    companion object {
        fun select(vararg columns: KProperty<out Any>): SelectStatement {
            val sql = Sql()
            sql.columns = columns.asList()

            return SelectStatement(sql)
        }
    }

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

    class SelectStatement(val sql: Sql) {

        fun from(clazz: KClass<out Entity>): WhereOrderLimit {
            this.sql.clazz = clazz
            if (this.sql.columns.isEmpty())
                this.sql.columns = EntityMeta.get(clazz).props()
            return WhereOrderLimit(this.sql)
        }
    }

    class WhereOrderLimit(val sql: Sql) {
        fun where(cond: Condition): AndOrStatement {
            this.sql.condition.add(cond)
            return AndOrStatement(this.sql)
        }
    }

    class Condition(val prop: KProperty<out Any>, val op: OP, val value: Any)

    /**
     * and or relationship statement
     */
    class AndOrStatement(val sql: Sql) {
        /**
         * SQL 的 and 操作
         */
        fun and(cnd: Condition): AndOrStatement {
            this.sql.condition.add(cnd)
            return this
        }

        /**
         * SQL 的 or 操作
         */
        fun or(cnd: Condition): AndOrStatement {
            this.sql.condition.add(cnd)
            return this
        }
    }
}

/**
 * Equals, 对应 数据库的 = 操作
 */
infix fun KProperty<out Any>.EQ(value: Any): Sql.Condition = Sql.Condition(this, Sql.OP.EQ, value)

/**
 * Not Equals, 对应 数据库的 != 操作
 */
infix fun KProperty<out Any>.NE(value: Any): Sql.Condition = Sql.Condition(this, Sql.OP.NE, value)

/**
 * Less Than, 对应数据库的 < 操作
 */
infix fun KProperty<out Any>.LT(value: Any): Sql.Condition = Sql.Condition(this, Sql.OP.LT, value)

/**
 * Greater Than, 对应数据库的 > 操作
 */
infix fun KProperty<out Any>.GT(value: Any): Sql.Condition = Sql.Condition(this, Sql.OP.GT, value)

/**
 * Less or Equals Than, 对应数据库的 <= 操作
 */
infix fun KProperty<out Any>.LE(value: Any): Sql.Condition = Sql.Condition(this, Sql.OP.LE, value)

/**
 * Greater or Equals Than, 对应数据库的 >= 操作
 */
infix fun KProperty<out Any>.GE(value: Any): Sql.Condition = Sql.Condition(this, Sql.OP.GE, value)

/**
 * between, 对应数据库的 between and 操作
 */
infix fun KProperty<out Any>.BT(value: Array<out Any>): Sql.Condition {
    return Sql.Condition(this, Sql.OP.BETWEEN, value)
}

/**
 * in, 对应数据库的 in 操作
 */
infix fun KProperty<out Any>.IN(value: Array<Any>): Sql.Condition = Sql.Condition(this, Sql.OP.IN, value)
