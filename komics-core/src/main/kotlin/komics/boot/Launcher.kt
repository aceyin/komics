package komics.boot

import komics.core.Application

/**
 * Created by ace on 16/9/7.
 */
class Launcher {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            Application.initialize(args)
        }
    }
}
