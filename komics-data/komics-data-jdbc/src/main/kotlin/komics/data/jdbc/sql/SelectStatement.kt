package komics.data.jdbc.sql

import komics.data.Entity
import komics.data.EntityMeta
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Created by ace on 2016/10/11.
 */

/**
 * Select 语句
 */
class SelectStatement(val sql: SqlBuilder) {
    fun from(clazz: KClass<out Entity>): WhereStatement {
        this.sql.clazz = clazz
        if (this.sql.columns.isEmpty())
            this.sql.columns = EntityMeta.get(clazz).props()
        return WhereStatement(this.sql)
    }


    /**
     * Where语句
     */
    class WhereStatement(sql: SqlBuilder) : GroupOrderLimit(sql) {
        /**
         * Where 条件
         */
        fun where(cond: SqlBuilder.Condition): AndOrStatement {
            this.sql.condition.add(cond)
            return AndOrStatement(this.sql)
        }
    }

    open class GroupOrderLimit(val sql: SqlBuilder) {
        /**
         * Group操作
         */
        fun group(vararg props: KProperty<out Any>): OrderLimit {
            if (props.size > 0) {
                this.sql.groupby.plus(props)
            }
            return OrderLimit(this.sql)
        }

        /**
         * Order by
         */
        fun order(vararg orders: Order): LimitStatement {
            if (orders.size > 0) {
                this.sql.orderby.plus(orders)
            }
            return LimitStatement(this.sql)
        }

        /**
         * limit
         */
        fun limit(offset: Int, rownum: Int): SqlBuilder {
            this.sql.limit = arrayOf(offset, rownum)
            return this.sql
        }
    }

    class Order(order: SqlBuilder.ODR, props: Array<KProperty<Any>>)

    class OrderLimit(val sql: SqlBuilder) {

        fun order(order: SqlBuilder.ODR, vararg props: KProperty<Any>): LimitStatement {
//            if (props.size > 0) {
//                this.sql.orderby.plus(Order(order, props.copyOf()))
//            }
            return LimitStatement(sql)
        }
    }

    class LimitStatement(val sql: SqlBuilder) {
    }

    /**
     * and or relationship statement
     */
    class AndOrStatement(sql: SqlBuilder) : GroupOrderLimit(sql) {
        /**
         * SQL 的 and 操作
         */
        fun and(cnd: SqlBuilder.Condition): AndOrStatement {
            this.sql.condition.add(cnd)
            return this
        }

        /**
         * SQL 的 or 操作
         */
        fun or(cnd: SqlBuilder.Condition): AndOrStatement {
            this.sql.condition.add(cnd)
            return this
        }
    }

}
