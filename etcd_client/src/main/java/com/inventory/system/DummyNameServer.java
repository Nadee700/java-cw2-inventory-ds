package com.inventory.system;

import java.io.IOException;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class DummyNameServer {
    public static void main(String[] args) {
        try {
            String etcdAddress = "http://localhost:2379";
            NameServiceClient nameServiceClient = new NameServiceClient(etcdAddress);

            // Register a service
            String serviceName = "InventoryService";
            String serviceAddress = "localhost:12500";
            nameServiceClient.registerService(serviceName, serviceAddress);
            System.out.println("Service registered successfully!");

            // Discover the service
            String discoveredAddress = nameServiceClient.discoverService(serviceName);
            System.out.println("Discovered service address: " + discoveredAddress);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}