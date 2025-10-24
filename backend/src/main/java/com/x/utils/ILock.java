package com.x.utils;

public interface ILock {

    boolean tryLock(Long timeoutSec);

    void unlock();
}