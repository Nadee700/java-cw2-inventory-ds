package com.inventory.system;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class DummyProcess {
    public static void main(String[] args) {
        try {
            String zkAddress = "localhost:2181";
            String lockBasePath = "/locks";
            String lockName = "inventory_lock";

            DistributedLock lock = new DistributedLock(zkAddress, lockBasePath, lockName);

            System.out.println("Attempting to acquire lock...");
            lock.acquireLock();
            lock.waitForLock();
            System.out.println("Lock acquired!");

            // Perform critical section of code
            System.out.println("Performing critical section of code");

            // Simulate some work with the lock
            Thread.sleep(5000);

            // Release the lock
            lock.releaseLock();
            System.out.println("Lock released");

        } catch (IOException | InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }
}
