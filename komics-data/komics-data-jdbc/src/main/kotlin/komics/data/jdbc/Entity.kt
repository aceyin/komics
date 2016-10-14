package komics.data.jdbc

import javax.persistence.Column
import javax.persistence.Id

/**
 * Created by ace on 2016/10/1.
 */
interface Entity {
    @get:Id
    @get:Column
    var id: String
    @get:Column
    var version: Long
}