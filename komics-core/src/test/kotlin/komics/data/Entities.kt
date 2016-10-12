package komics.data

import javax.persistence.Column
import javax.persistence.Table

/**
 * Created by ace on 2016/10/9.
 */
class EClass(
        var name: String
) : BaseEntity()

@javax.persistence.Entity
@Table(name = "user")
class User(
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
) : BaseEntity()