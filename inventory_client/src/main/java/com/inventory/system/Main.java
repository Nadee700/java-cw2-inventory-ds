package com.inventory.system;

import inventory.InventoryServiceGrpc;
import inventory.InventoryServiceGrpc.InventoryServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        String target = "localhost:12500";
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();

        InventoryServiceBlockingStub blockingStub = InventoryServiceGrpc.newBlockingStub(channel);

        InventoryClient client = new InventoryClient(blockingStub);
        client.run();

        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
