import com.app.model.ServerHandler;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.nio.NioTcpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;


public class MyServer {

    private static final Logger LOG = LoggerFactory.getLogger(MyServer.class);

    public static void main(String[] args) {
        LOG.info("start server...");
        final NioTcpServer acceptor = new NioTcpServer();
        //new StartRabbit().runRabbit();
        acceptor.setFilters(new LoggingFilter("LoggingFilter1"));
        acceptor.setIoHandler(new ServerHandler());
                try {
                final SocketAddress address = new InetSocketAddress(6000);
                acceptor.bind(address);
                new BufferedReader(new InputStreamReader(System.in)).readLine();
                acceptor.unbind();

            } catch (final IOException e) {
                LOG.error("Interrupted exception", e);
            }
    }

}
