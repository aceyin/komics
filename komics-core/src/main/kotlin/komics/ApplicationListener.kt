package komics

import komics.core.Application

/**
 * App初始化监听器。
 * 用来增加app启动时的定制化功能。
 */
interface ApplicationListener {

    /**
     * app 初始化之后的事件
     */
    fun postInitialized(application: Application): Unit

    /**
     * app shutdown 之前的清理工作
     */
    fun preShutdown(application: Application): Unit
}