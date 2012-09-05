package org.n3r.prizedraw.util;

import org.n3r.core.lang.RClose;
import org.n3r.core.text.RRand;
import org.n3r.esql.EsqlTransaction;
import org.n3r.prizedraw.base.PrizeBingooCommitable;
import org.n3r.prizedraw.base.PrizeCommitter;
import org.n3r.prizedraw.drawer.PrizeItem;

public class PrizeUtils {
    public static boolean randomize(PrizeItem prizeItem) {
        int rand = RRand.randInt(prizeItem.getItemRandbase());
        return rand == prizeItem.getItemLucknum() % prizeItem.getItemRandbase();
    }

    public static void addCommitter(final EsqlTransaction transaction) {
        PrizeBingooCommitable committer = new PrizeBingooCommitable() {
            @Override
            public void rollback() {
                transaction.rollback();
            }

            @Override
            public void commit() {
                transaction.commit();
            }

            @Override
            public void close() {
                RClose.closeQuietly(transaction);
            }
        };

        PrizeCommitter.addCommitter(committer);

        transaction.start();
    }
}
