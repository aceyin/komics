package komics

import java.text.SimpleDateFormat

/**
 * Created by ace on 2016/10/12.
 */
inline fun java.util.Date.today() = SimpleDateFormat("yyyy-MM-dd").format(this)!!