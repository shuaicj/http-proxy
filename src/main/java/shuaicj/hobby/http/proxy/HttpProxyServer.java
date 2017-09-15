package shuaicj.hobby.http.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * A simple http proxy server.
 *
 * @author shuaicj 2017/09/15
 */
@Component
@Slf4j
public class HttpProxyServer {

    private final int port;
    private final ExecutorService pool;
    private ServerSocket serverSocket;
    private long taskCount;

    public HttpProxyServer(@Value("${proxy.port}") int port, @Autowired ExecutorService pool) {
        this.port = port;
        this.pool = pool;
    }

    @PostConstruct
    @SuppressWarnings("InfiniteLoopStatement")
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        logger.info("HttpProxyServer started on port: {}", port);
        pool.submit(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    pool.submit(new HttpProxyServerTask("task-" + taskCount++, socket, pool));
                } catch (IOException e) {
                    logger.error("shit happens", e);
                }
            }
        });
    }
}
