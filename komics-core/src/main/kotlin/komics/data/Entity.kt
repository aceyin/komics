package komics.data

import java.util.*

/**
 * Created by ace on 2016/10/1.
 */
interface Entity {
    //uuid
    var id: String
    var version: Long
    var created: Date
    var updated: Date


}
