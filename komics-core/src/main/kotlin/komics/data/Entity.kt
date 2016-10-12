package komics.data

import java.util.*
import javax.persistence.Column
import javax.persistence.Id

/**
 * Created by ace on 2016/10/1.
 */
interface Entity {
    var id: String
    var version: Long
    var created: Long
    var updated: Long
}

abstract class BaseEntity(
        @Id
        @Column
        override var id: String = "",
        @Column
        override var version: Long = -1,
        @Column
        override var created: Long = Date().time,
        @Column
        override var updated: Long = -1
) : Entity