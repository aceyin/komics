package komics

/**
 * 初始化模块。
 */
abstract class ModuleInitializer {

    /**
     * 模块初始化
     */
    abstract fun initialize(): Unit

    /**
     * app shutdown 之前的清理工作
     */
    abstract fun destroy(): Unit
}