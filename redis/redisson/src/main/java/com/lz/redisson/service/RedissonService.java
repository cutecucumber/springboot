package com.lz.redisson.service;

import org.redisson.RedissonRedLock;
import org.redisson.api.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author lizhao
 * @class RedissonService
 * @description
 * @create 2023/11/22 8:54
 */
@Service
public class RedissonService {

    private final RedissonClient redissonClient;

    public RedissonService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 普通加锁-可重入，不会走看门狗机制，如果加锁失败，会无限自旋，直到加锁成功，不可取
     *
     * @param key
     */
    public void lockWithLeaseTime(String key, long leaseTime) {
        RLock lock = redissonClient.getLock(key);
        try {
            lock.lock(leaseTime, TimeUnit.SECONDS);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 普通加锁-可重入，会走看门狗机制，如果加锁失败，会无限自旋，直到加锁成功，不可取
     *
     * @param key
     */
    public void lockWithoutLeaseTime(String key) {
        RLock lock = redissonClient.getLock(key);
        try {
            lock.lock();
            Thread.sleep(10_000);
            System.out.println("111");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void lockWithoutLeaseTimeTest(String key) {
        RLock lock = redissonClient.getLock(key);
        try {
            lock.lock();
            System.out.println("222");
        } finally {
            lock.unlock();
        }
    }

    /**
     * 尝试加锁-可重入，会走看门狗机制，加锁成功返回true，加锁失败返回false
     *
     * @param key
     */
    public void tryLockWithoutLeaseTime(String key) {
        RLock lock = redissonClient.getLock(key);
        try {
            if (lock.tryLock()) {
                //执行业务逻辑
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 尝试加锁-可重入，会走看门狗机制，加锁会一直尝试，直到时间超过waitTime，退出加锁
     *
     * @param key
     */
    public void tryLockWithWaitTime(String key, long waitTime) {
        RLock lock = redissonClient.getLock(key);
        try {
            if (lock.tryLock(waitTime, TimeUnit.SECONDS)) {
                //执行业务逻辑
            }
        } catch (Exception e) {

        } finally {
            lock.unlock();
        }
    }

    /**
     * 尝试加锁-可重入，leaseTime == -1 时会走看门狗机制，否则超时自动退出，加锁会一直尝试，直到时间超过waitTime，退出加锁
     *
     * @param key
     */
    public void tryLockWithLeaseTime(String key, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(key);
        try {
            if (lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)) {
                //执行业务逻辑
            }
        } catch (Exception e) {

        } finally {
            lock.unlock();
        }
    }

    /**
     * 尝试加锁-可重入，leaseTime == -1 时会走看门狗机制，否则超时自动退出，加锁会一直尝试，直到时间超过waitTime，退出加锁。
     * 如果加锁失败，会将线程扔到set集合中，按照加锁的顺序给线程排队，set的头部代表了接下来能够成功加锁的线程，当有线程释放了锁之后，
     * 其它加锁失败的线程就会来继续加锁，加锁之前会先判断一下set集合的头部的线程跟当前要加锁的线程是不是同一个，如果是的话，那就加锁成功，
     * 如果不是的话，那么就加锁失败，这样就实现了加锁的顺序性
     *
     * @param key
     */
    public void tryFairLock(String key, long waitTime, long leaseTime) {
        RLock fairLock = redissonClient.getFairLock(key);
        try {
            if (fairLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)) {
                //执行业务逻辑
            }
        } catch (Exception e) {

        } finally {
            fairLock.unlock();
        }
    }

    /**
     * 加锁成功之后会在redis中维护一个hash的数据结构，存储加锁线程和加锁次数。在读写锁的实现中，会往hash数据结构中多维护一个mode（read/write）的字段，来表示当前加锁的模式。
     *
     * 所以能够实现读写锁，最主要是因为维护了一个加锁模式的字段mode，这样有线程来加锁的时候，就能根据当前加锁的模式结合读写的特性来判断要不要让当前来加锁的线程加锁成功。
     *
     * 如果没有加锁，那么不论是读锁还是写锁都能加成功，成功之后根据锁的类型维护mode字段。
     * 如果模式是读锁，那么加锁线程是来加读锁的，就让它加锁成功。
     * 如果模式是读锁，那么加锁线程是来加写锁的，就让它加锁失败。
     * 如果模式是写锁，那么加锁线程是来加写锁的，就让它加锁失败（加锁线程自己除外）。
     * 如果模式是写锁，那么加锁线程是来加读锁的，就让它加锁失败（加锁线程自己除外）。
     * 读锁：共享锁；写锁：独占锁
     *
     * @param key
     */
    public void tryReadWriteLock(String key, long waitTime, long leaseTime) {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(key);
        RLock readLock = readWriteLock.readLock();
        try {
            if (readLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)) {
                //执行业务逻辑
            }
        } catch (Exception e) {

        } finally {
            readLock.unlock();
        }

        RLock writeLock = readWriteLock.writeLock();
        try {
            if (writeLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)) {
                //执行业务逻辑
            }
        } catch (Exception e) {

        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 联锁（根据顺序去依次调用传入key1、key2、key3加锁方法，然后如果都成功加锁了，那么multiLock就算加锁成功。）
     *
     * @param key1
     * @param key2
     * @param key3
     * @param leaseTime
     * @param waitTime
     */
    public void tryMultiLock(String key1, String key2, String key3, long waitTime, long leaseTime) {
        RLock lock1 = redissonClient.getLock(key1);
        RLock lock2 = redissonClient.getLock(key2);
        RLock lock3 = redissonClient.getLock(key3);
        RLock multiLock = redissonClient.getMultiLock(lock1, lock2, lock3);
        try {
            if (multiLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)) {
                //执行业务逻辑
            }
        } catch (Exception e) {

        } finally {
            multiLock.unlock();
        }
    }

    /**
     * 红锁
     * 获取所有的redisson node节点信息，循环向所有的redisson node节点加锁，假设节点数为5。一个redisson node代表一个主从节点。
     * 如果在5个节点当中，有3个节点加锁成功了，那么整个RedissonRedLock加锁是成功的。
     * 如果在5个节点当中，小于3个节点加锁成功，那么整个RedissonRedLock加锁是失败的。
     * 如果中途发现各个节点加锁的总耗时，大于等于设置的最大等待时间，则直接返回失败。
     *
     * @param key1
     * @param key2
     * @param key3
     * @param leaseTime
     * @param waitTime
     */
    public void tryRedLock(String key1, String key2, String key3, long waitTime, long leaseTime) {
        RLock lock1 = redissonClient.getLock(key1);
        RLock lock2 = redissonClient.getLock(key2);
        RLock lock3 = redissonClient.getLock(key3);
        RedissonRedLock redLock = new RedissonRedLock(lock1, lock2, lock3);
        try {
            if (redLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)) {
                //执行业务逻辑
            }
        } catch (Exception e) {

        } finally {
            redLock.unlock();
        }
    }

    /**
     * 布隆过滤器
     *
     * @param key
     */
    public void bloomFilter(String key) {
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(key);
        //声明布隆过滤器的插入元素数量和误报率
        bloomFilter.tryInit(10000, 0.01);
        bloomFilter.add(111);
        bloomFilter.add(222);
        bloomFilter.add(333);

        //布隆过滤器中对象的存活时间
        bloomFilter.expire(30, TimeUnit.SECONDS);

        System.out.println(bloomFilter.contains(444));
    }
}
