package com.inventory.system;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

public class ZooKeeperClient {
    private final ZooKeeper zooKeeper;

    public ZooKeeperClient(String connectString, int sessionTimeout, Watcher watcher) throws IOException {
        this.zooKeeper = new ZooKeeper(connectString, sessionTimeout, watcher);
    }

    public String createNode(String path, byte[] data, CreateMode createMode) throws KeeperException, InterruptedException {
        return zooKeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
    }

    public Stat checkExists(String path) throws KeeperException, InterruptedException {
        return zooKeeper.exists(path, false);
    }

    public void deleteNode(String path) throws KeeperException, InterruptedException {
        zooKeeper.delete(path, -1);
    }

    public List<String> getChildren(String path) throws KeeperException, InterruptedException {
        return zooKeeper.getChildren(path, false);
    }

    public void setData(String path, byte[] data) throws KeeperException, InterruptedException {
        zooKeeper.setData(path, data, -1);
    }

    public byte[] getData(String path) throws KeeperException, InterruptedException {
        return zooKeeper.getData(path, false, null);
    }

    public void addWatch(String path, Watcher watcher) throws KeeperException, InterruptedException {
        zooKeeper.exists(path, watcher);
    }
}
