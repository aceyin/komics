package komics.test.db.support

import org.h2.tools.Server

/**
 * Created by ace on 16/9/13.
 */

object H2Server {
    private val port = "9092"
    private val server = Server.createTcpServer("-tcpPort", port, "-tcpAllowOthers")

    init {
        server.start()
    }

    fun start() {
        if (!server.isRunning(true)) server.start()
    }

    fun stop() {
        Server.shutdownTcpServer("tcp://localhost:$port", "", true, true)
    }
}
