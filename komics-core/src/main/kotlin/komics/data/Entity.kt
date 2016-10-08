package komics.data

import java.util.*
import javax.persistence.Column

/**
 * Created by ace on 2016/10/1.
 */
interface Entity {
    //uuid
    var id: String
    var version: Long
    var created: Date
    var updated: Date

    /**
     * Get the columns of this entity class
     */
    fun cols(): List<String> {
        //TODO use cache to improve the performance??
        val members = this.javaClass.declaredFields

        val list = mutableListOf<String>()
        members.forEach { m ->
            val col = m.annotations.find { a -> a.annotationClass == Column::class } as? Column
            if (col != null && col.name.isNotBlank()) list.add(col.name)
            else list.add(m.name)
        }
        return list
    }

    fun colstr(): String {
        return this.cols().joinToString(",")
    }
}