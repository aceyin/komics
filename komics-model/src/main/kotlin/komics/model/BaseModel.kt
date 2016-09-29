package komics.model

import com.avaje.ebean.Model
import com.avaje.ebean.annotation.WhenCreated
import com.avaje.ebean.annotation.WhenModified
import java.util.*
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Version

/**
 * 所有domain对象的父类
 */
@MappedSuperclass
open class BaseModel() : Model() {

    @Id
    var id: Long = 0

    @Version
    var version: Long = 1

    @WhenCreated
    lateinit var created: Date

    @WhenModified
    lateinit var modified: Date
}
