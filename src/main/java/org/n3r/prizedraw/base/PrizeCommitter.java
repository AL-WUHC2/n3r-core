package org.n3r.prizedraw.base;

import java.util.List;

import com.google.common.collect.Lists;

public class PrizeCommitter {
    private static ThreadLocal<List<PrizeBingooCommitable>> committerLocal = new ThreadLocal<List<PrizeBingooCommitable>>();
    private static ThreadLocal<Boolean> commitOrRollbackFlag = new ThreadLocal<Boolean>();

    public static void addCommitter(PrizeBingooCommitable committer) {
        List<PrizeBingooCommitable> committers = committerLocal.get();
        if (committers == null) {
            committers = Lists.newArrayList();
            committerLocal.set(committers);
        }
        committers.add(committer);
    }

    public static void commit() {
        if (commitOrRollbackFlag.get() != null) return;

        List<PrizeBingooCommitable> committers = committerLocal.get();
        if (committers != null) for (PrizeBingooCommitable prizeBingooCommitable : committers)
            prizeBingooCommitable.commit();

        commitOrRollbackFlag.set(true);
    }

    public static void rollback() {
        if (commitOrRollbackFlag.get() != null) return;

        List<PrizeBingooCommitable> committers = committerLocal.get();
        if (committers != null) for (PrizeBingooCommitable prizeBingooCommitable : committers)
            prizeBingooCommitable.rollback();

        commitOrRollbackFlag.set(true);
    }

    public static void close() {
        List<PrizeBingooCommitable> committers = committerLocal.get();
        if (committers != null) for (PrizeBingooCommitable prizeBingooCommitable : committers)
            prizeBingooCommitable.close();

        committerLocal.set(null);
        commitOrRollbackFlag.set(null);
    }

}
