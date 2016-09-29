package komics.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

/**
 * Created by ace on 16/9/13.
 */
@Entity
@Table(name = "user")
class User : BaseModel() {
    @Column(length = 32, nullable = false, unique = true, columnDefinition = "COMMENT '用户名'")
    lateinit var username: String

    @Column(length = 32, nullable = false, unique = true, columnDefinition = "COMMENT '密码'")
    lateinit var email: String

    @Column(length = 32, nullable = false, columnDefinition = "COMMENT '密码'")
    lateinit var password: String

    @Column(length = 15, nullable = false, columnDefinition = "COMMENT '手机'")
    lateinit var mobile: String

    @Column(length = 20, nullable = false, columnDefinition = "COMMENT '状态'")
    lateinit var status: String

}