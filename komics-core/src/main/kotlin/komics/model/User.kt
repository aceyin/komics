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
    val username: String? = null

    @Column(length = 32, nullable = false, unique = true, columnDefinition = "COMMENT '密码'")
    val email: String? = null

    @Column(length = 32, nullable = false)
    val password: String? = null

    @Column(length = 15, nullable = false)
    val mobile: String? = null

    @Column(length = 20, nullable = false, columnDefinition = "")
    val status: String? = null

}