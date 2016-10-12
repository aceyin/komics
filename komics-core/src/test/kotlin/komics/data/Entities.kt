package komics.data

import javax.persistence.Column
import javax.persistence.Table

/**
 * Created by ace on 2016/10/9.
 */
data class EClass(
        override var id: String,
        override var version: Long,
        override var created: Long,
        override var updated: Long,
        var name: String
) : Entity

@javax.persistence.Entity
@Table(name = "user")
data class User(
        override var id: String,
        override var version: Long,
        override var created: Long,
        override var updated: Long,
        @Column
        var username: String = "",
        @Column
        var password: String = "",
        @Column
        var mobile: String = "",
        @Column
        var email: String = "",
        @Column
        var status: Int = 0
) : Entity