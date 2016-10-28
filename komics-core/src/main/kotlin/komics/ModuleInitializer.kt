package komics

import komics.core.Application

/**
 * 初始化模块。
 */
interface ModuleInitializer {

    /**
     * 模块初始化
     */
    fun initialize(application: Application): Unit

    /**
     * app shutdown 之前的清理工作
     */
    fun destroy(application: Application): Unit
}