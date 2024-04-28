import config.ConfigLoader;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Mono;

public class ChessEngineApplication {

    public static void main(String[] args) {
        ConfigLoader config = new ConfigLoader();
        int port = config.getServerPort();

        RSocketServer rSocketServer = RSocketServer.create(SocketAcceptor.forRequestResponse(
                payload -> {
                    System.out.println("Received: " + payload.getDataUtf8());

                    //FIXME This should be the entry call to do the AI move, so it response best AI move!
                    // The cool thing is, we don't need to send json, just the FEN string - Well done Mikkel!
                    return Mono.just(DefaultPayload.create("Hello, " + payload.getDataUtf8()));
                }));

        //FIXME This needs to be environmental values, ensuring Docker can pinpoint to the gateway/backend for connection
        CloseableChannel server = rSocketServer.bindNow(TcpServerTransport.create("localhost", port));

        System.out.println("Server is running on port: " + port);

        server.onClose().block();
    }
}
