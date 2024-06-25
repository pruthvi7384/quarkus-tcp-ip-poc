package com.tcpserver;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * TCP Server
 */
@ApplicationScoped
public class TCPServerVerticle extends AbstractVerticle {

    @Override
    public void start() {
        NetServer server = vertx.createNetServer();

        server.connectHandler(socket -> {
            System.out.println("Client connected...");

            // Handler for receiving data from the client
            socket.handler(buffer -> {
                String requestData = buffer.toString();
                System.out.println("Received request from client: ".concat(requestData));

                // Send the response back to the client
                socket.write(Buffer.buffer(requestData));
            });

            // Handler for handling client disconnect
            socket.closeHandler(voidValue -> {
                System.out.println("Client disconnected...");
            });
        });

        // Start the server on a specific port and host
        server.listen(5000, "localhost", ar -> {
            if (ar.succeeded()) {
                System.out.println("Server started on port 5000");
            } else {
                System.out.println("Failed to start server. Reason: ".concat(ar.cause().getMessage()));
            }
        });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(TCPServerVerticle.class.getName());
    }
}
