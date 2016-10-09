package komics.data.jdbc

import komics.data.Entity
import kotlin.reflect.KClass

/**
 * Created by ace on 2016/10/1.
 */
sealed class Sql {
    companion object {
        /** Create a select Sql instance */
        fun select(columns: String = "*"): Select {
            return Select(columns)
        }

        //TODO unimplemented
        fun insert(columns: String = ""): Insert {
            return Insert(columns)
        }
    }

    class Insert(columns: String = "") : Sql() {
        fun <E : Entity> into(entity: KClass<E>): Insert {
            TODO("to be implemented")
        }
    }

    /**
     * Select sql builder
     */
    class Select(columns: String = "*") : Sql() {
        private val SELECT = "SELECT"
        private val FROM = "FROM"
        private val WHERE = "WHERE"
        private val ORDERBY = "ORDER BY"
        private val LIMIT = "LIMIT"

        private val statement = mutableMapOf(
                SELECT to "",
                FROM to "",
                WHERE to "",
                ORDERBY to "",
                LIMIT to ""
        )

        init {
            this.statement[SELECT] = "$SELECT $columns"
        }

        /** from **/
        fun from(table: String): From {
            return From(table)
        }

        override fun toString(): String {
            return this.statement.values.joinToString(" ").trim()
        }
    }

    class From(table: String = "") : Sql() {

//        fun where(condition: String): Select {
//            if (condition.isNullOrEmpty()) return this
//            this.statement[WHERE] = "$WHERE $condition"
//            return this
//        }
//
//        fun orderBy(order: String): Select {
//            if (order.isNullOrEmpty()) return this
//            this.statement[ORDERBY] = "$ORDERBY $order"
//            return this
//        }
//
//        fun limit(offset: Int, rowNum: Int): Select {
//            this.statement[LIMIT] = "$LIMIT $offset, $rowNum"
//            return this
//        }
    }


}