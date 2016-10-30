package komics.boot

import komics.core.Application
import org.apache.commons.cli.*

/**
 * 启动应用时可用的命令行参数:
 * -c <configuration-file-path>:
 *  系统配置文件路径。
 *  配置文件必须是yaml格式的, 如果不指定默认会读取 conf/application.yml
 */
object Launcher {
    @JvmStatic fun main(args: Array<String>) {
        // parse command line args
        val opts = parsArgs(args)

        //TODO: 将从命令行传入的系统参数和java环境变量都抽取出来, 是的 initialize方法可以只用一个opts参数
        Application.start(args, opts)
    }

    private fun parsArgs(args: Array<String>): Map<String, String> {
        val options = Options()

        val conf = Option("c", "conf", true, "YAML config file path")
        conf.isRequired = false
        options.addOption(conf)

        val parser = DefaultParser()
        val formatter = HelpFormatter()
        var opts = mutableMapOf<String, String>()

        try {
            val cmd = parser.parse(options, args)
            val confFile = cmd.getOptionValue(conf.longOpt)

            if (confFile != null) opts.put(conf.longOpt, confFile)
        } catch (e: ParseException) {
            formatter.printHelp("Usage:", options)
            System.exit(1)
        }
        return opts
    }
}