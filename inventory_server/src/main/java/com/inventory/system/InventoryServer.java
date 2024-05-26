package com.inventory.system;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import com.example.distributedWorker.DistributedLock;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class InventoryServer {

    private final int port;
    private final Server server;

    public InventoryServer(int port, DistributedLock distributedLock) throws IOException, KeeperException, InterruptedException {
        this.port = port;
        server = ServerBuilder.forPort(port)
                .addService(new InventoryServiceImpl(distributedLock))
                .build();
    }

    public void start() throws IOException {
        server.start();
        System.out.println("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            InventoryServer.this.stop();
            System.err.println("*** server shut down");
        }));
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        int port = 50051;
        String connectString = "localhost:2181";
        String lockBasePath = "/inventory_locks";
        String lockName = "inventory_lock";

        DistributedLock distributedLock = new DistributedLock(connectString, lockBasePath, lockName);

        final InventoryServer server = new InventoryServer(port, distributedLock);
        server.start();
        server.blockUntilShutdown();
    }
}
