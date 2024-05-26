package com.inventory.system;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DistributedLock {
    private final ZooKeeper zooKeeper;
    private final String lockBasePath;
    private final String lockName;
    private String currentLockPath;
    private final CountDownLatch latch = new CountDownLatch(1);

    public DistributedLock(String connectString, String lockBasePath, String lockName) throws IOException, InterruptedException, KeeperException {
        this.zooKeeper = new ZooKeeper(connectString, 3000, null);
        this.lockBasePath = lockBasePath;
        this.lockName = lockName;

        // Ensure the lock base path exists
        if (zooKeeper.exists(lockBasePath, false) == null) {
            zooKeeper.create(lockBasePath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    public void acquireLock() throws KeeperException, InterruptedException {
        currentLockPath = zooKeeper.create(lockBasePath + "/" + lockName, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        attemptLockAcquisition();
        waitForLock();
    }

    private void attemptLockAcquisition() throws KeeperException, InterruptedException {
        List<String> children = zooKeeper.getChildren(lockBasePath, false);
        Collections.sort(children);

        String smallestChild = children.get(0);
        if (currentLockPath.endsWith(smallestChild)) {
            latch.countDown();
        } else {
            int previousNodeIndex = Collections.binarySearch(children, currentLockPath.substring(currentLockPath.lastIndexOf('/') + 1)) - 1;
            String previousNode = children.get(previousNodeIndex);

            Stat stat = zooKeeper.exists(lockBasePath + "/" + previousNode, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.NodeDeleted) {
                        try {
                            attemptLockAcquisition();
                        } catch (KeeperException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            if (stat == null) {
                attemptLockAcquisition();
            }
        }
    }

    public void waitForLock() throws InterruptedException {
        latch.await();
    }

    public void releaseLock() throws KeeperException, InterruptedException {
        zooKeeper.delete(currentLockPath, -1);
        zooKeeper.close();
    }
}
