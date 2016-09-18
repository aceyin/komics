package komics.test.db.support

import org.h2.tools.Server

/**
 * Created by ace on 16/9/13.
 */
class H2Server {
    companion object {
        private val port = "9092"

        /**
         * start the H2 server
         */
        fun start(): Server {
            val server = Server.createTcpServer("-tcpPort", port, "-tcpAllowOthers")
            server.start()
            return server
        }

        /**
         * Stop the H2 server
         */
        fun stop() {
            Server.shutdownTcpServer("tcp://localhost:${port}", "", true, true)
        }
    }
}
