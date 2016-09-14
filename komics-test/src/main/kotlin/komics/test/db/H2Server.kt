package komics.test.db

import org.h2.tools.Server

/**
 * Created by ace on 16/9/13.
 */
class H2Server {
    companion object {
        fun start() {
            val server = Server.createTcpServer()
            server.start()
        }

        fun stop() {
            Server.shutdownTcpServer("tcp://localhost:9123", "", true, true);
        }
    }
}
