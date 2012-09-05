package org.n3r.prizedraw.base;

public interface PrizeBingooCommitable {
    void commit();

    void rollback();

    void close();
}
