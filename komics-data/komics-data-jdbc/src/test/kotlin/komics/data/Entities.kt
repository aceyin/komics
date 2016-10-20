package komics.data

import javax.persistence.Column
import javax.persistence.Table

/**
 * Created by ace on 2016/10/9.
 */
data class EClass(
        override var id: String
) : Entity

@Table(name = "user")
data class User(
        override var id: String,
        @Column
        var username: String = "",
        @Column(name = "passwd")
        var password: String = "",
        @Column
        var mobile: String = "",
        @Column
        var email: String = "",
        @Column
        var status: Int = 0
) : Entity {
    public constructor() : this(id = "")
}

@Table(name = "user_profile")
data class UserProfile(
        @Column(name = "user_id")
        var userId: String = "",
        var name: String = "",
        var gender: String = "",
        var age: Int = 0,
        override var id: String = ""
) : Entity {
    public constructor() : this(id = "")
}