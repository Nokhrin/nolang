package com.nokhrin.nolang.common;

public interface Environment <K,V>{
    void put(K key, V value);
    V get(K key);
    boolean contains(K key);
}
