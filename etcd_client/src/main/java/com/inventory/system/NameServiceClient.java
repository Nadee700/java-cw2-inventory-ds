package com.inventory.system;

import java.io.IOException;

public class NameServiceClient {
    private final EtcdClient etcdClient;

    public NameServiceClient(String etcdAddress) throws IOException {
        this.etcdClient = new EtcdClient(etcdAddress);
    }

    public void registerService(String serviceName, String address) throws IOException {
        etcdClient.put(serviceName, address);
    }

    public String discoverService(String serviceName) throws IOException {
        return etcdClient.get(serviceName);
    }
}
