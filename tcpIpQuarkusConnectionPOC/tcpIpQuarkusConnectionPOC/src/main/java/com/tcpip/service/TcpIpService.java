package com.tcpip.service;

import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TcpIpService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    Vertx vertx;

    @ConfigProperty(name = "tcp.ip.connection.timeout")
    int timeOut;
    @ConfigProperty(name = "tcp.ip.connection.port")
    int port;
    @ConfigProperty(name = "tcp.ip.connection.host")
    String host;

    @ConfigProperty(name = "quarkus.certificate.file.url")
    String jksCertificate;

    @ConfigProperty(name = "quarkus.certificate.file.password", defaultValue = "changeit")
    String jksFilePassword;

    public Uni<String> tcpIpProcess(String request) {
        logger.info("Tcp Server Request :- {}",request);
        NetClientOptions options = new NetClientOptions().setConnectTimeout(timeOut).setTrustAll(true);
        NetClient client = vertx.createNetClient(options);
        return Uni.createFrom().emitter(emitter ->
                client.connect(port, host, ar -> {
                    if (ar.succeeded()) {
                        logger.info("Connected to server...");
                        NetSocket socket = ar.result();

                        // Send the request to the server
                        boolean messageSendStatus = socket.write(request).succeeded();
                        logger.info("Server Request Sent Status: {}", messageSendStatus);

                        if (!messageSendStatus) {
                            // Process the request and emit the result
                            emitter.complete("Failed to send request to server.");

                            // Close the client connection and socket
                            socket.close();
                            client.close();
                        }

                        socket.handler(buffer -> {
                            // Handle the response received from the server
                            String response = buffer.toString();

                            // Process the response and emit the result
                            emitter.complete(response);

                            // Close the client connection and socket
                            socket.close();
                            client.close();
                        });
                    } else {
                        // Prepare the response
                        logger.error("Failed to connect to server Error: ", ar.cause());

                        // Process the response and emit the result
                        emitter.complete( "Failed to connect to server with error message: " + ar.cause().getMessage());
                    }
                })
        );
    }

}
