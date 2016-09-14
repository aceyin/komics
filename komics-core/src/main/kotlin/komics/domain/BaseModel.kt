package komics.domain

import com.avaje.ebean.Model
import com.avaje.ebean.annotation.WhenCreated
import com.avaje.ebean.annotation.WhenModified
import java.sql.Timestamp
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Version

/**
 * 所有domain对象的父类
 */
@MappedSuperclass
open class BaseModel : Model() {
    @Id
    var id: Long? = null

    @Version
    var version: Long? = null

    @WhenCreated
    var created: Timestamp? = null

    @WhenModified
    var modified: Timestamp? = null
}
