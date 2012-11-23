package org.n3r.esql.demo;

import java.sql.Timestamp;
import java.util.Date;

import org.n3r.esql.Esql;

public class HuaXiaoDemo {
    void synBulletinSendLog(Object bulletinId) throws Exception {
        try {
            String sqlid = checkBulletinSendLog(bulletinId)
                    ? "updateSendLog" : "insertSendLog";

            Timestamp timestamp = new Timestamp(new Date().getTime());
            new Esql().id(sqlid).params(bulletinId, timestamp).execute();
        } catch (Exception e) {
            throw new Exception("同步公告发布日志表(tab_bulletin_send_log)出错 "
                    + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
        }
    }

    private boolean checkBulletinSendLog(Object bulletinId) {
        return false;
    }

}
