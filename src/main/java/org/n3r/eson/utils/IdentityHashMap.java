package org.n3r.eson.utils;

public class IdentityHashMap<K, V> {

    public static final int DEFAULT_TABLE_SIZE = 1024;

    private final Entry<K, V>[] buckets;
    private final int indexMask;

    public IdentityHashMap() {
        this(DEFAULT_TABLE_SIZE);
    }

    @SuppressWarnings("unchecked")
    public IdentityHashMap(int tableSize) {
        this.indexMask = tableSize - 1;
        this.buckets = new Entry[tableSize];
    }

    public final V get(K key) {
        final int hash = System.identityHashCode(key);
        final int bucket = hash & indexMask;

        for (Entry<K, V> entry = buckets[bucket]; entry != null; entry = entry.next)
            if (key == entry.key) return entry.value;

        return null;
    }

    public boolean put(K key, V value) {
        final int hash = System.identityHashCode(key);
        final int bucket = hash & indexMask;

        for (Entry<K, V> entry = buckets[bucket]; entry != null; entry = entry.next)
            if (key == entry.key) {
                entry.value = value;
                return true;
            }

        Entry<K, V> entry = new Entry<K, V>(key, value, hash, buckets[bucket]);
        buckets[bucket] = entry; // 并发是处理时会可能导致缓存丢失，但不影响正确性

        return false;
    }

    public int size() {
        int size = 0;
        for (Entry<K, V> bucket : buckets)
            for (Entry<K, V> entry = bucket; entry != null; entry = entry.next)
                size++;
        return size;
    }

    protected static final class Entry<K, V> {

        public final int hashCode;
        public final K key;
        public V value;

        public final Entry<K, V> next;

        public Entry(K key, V value, int hash, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
            this.hashCode = hash;
        }
    }

}
