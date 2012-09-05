/*==============================================================*/
/* DBMS name:      ORACLE Version 10gR2                         */
/* Created on:     2012/9/3 21:51:38                            */
/*==============================================================*/


drop table PRIZE_ACTIVITY cascade constraints;

drop table PRIZE_BINGOO cascade constraints;

drop table PRIZE_CHECK cascade constraints;

drop table PRIZE_FREQUENCE cascade constraints;

drop table PRIZE_ITEM cascade constraints;

drop table PRIZE_ITEM_ASSIGN cascade constraints;

drop table PRIZE_WHITE cascade constraints;

drop table esql_sql cascade constraints;

drop table prize_record cascade constraints;

/*==============================================================*/
/* Table: PRIZE_ACTIVITY                                        */
/*==============================================================*/
create table PRIZE_ACTIVITY  (
   ACTIVITY_ID          VARCHAR2(10)                    not null,
   ACTIVITY_NAME        VARCHAR2(64)                    not null,
   ACTIVITY_EFF         DATE                            not null,
   ACTIVITY_EXP         DATE                            not null,
   FREQUENCY_SPEC       VARCHAR2(64),
   CHECKERS             VARCHAR2(512)                   not null,
   DRAWERS              VARCHAR2(512)                   not null,
   RESULTERS            VARCHAR2(512)                   not null,
   constraint PK_PRIZE_ACTIVITY primary key (ACTIVITY_ID)
);

comment on table PRIZE_ACTIVITY is
'定义一轮抽奖活动';

comment on column PRIZE_ACTIVITY.ACTIVITY_ID is
'活动标识';

comment on column PRIZE_ACTIVITY.ACTIVITY_NAME is
'活动名称，例如：奥运抽奖、乒乓抽奖等';

comment on column PRIZE_ACTIVITY.ACTIVITY_EFF is
'开始日期';

comment on column PRIZE_ACTIVITY.ACTIVITY_EXP is
'结束日期';

comment on column PRIZE_ACTIVITY.FREQUENCY_SPEC is
'相同用户抽奖频率控制，例如：
每5分钟最多10次，连续超频3次，进黑名单';

comment on column PRIZE_ACTIVITY.CHECKERS is
'定义java类名列表';

comment on column PRIZE_ACTIVITY.DRAWERS is
'定义JAVA类名列表';

comment on column PRIZE_ACTIVITY.RESULTERS is
'定义JAVA类名列表';

insert into PRIZE_ACTIVITY (ACTIVITY_ID, ACTIVITY_NAME, ACTIVITY_EFF, ACTIVITY_EXP, FREQUENCY_SPEC, CHECKERS, DRAWERS, RESULTERS)
values ('Olympic', '奥运抽奖活动', to_date('28-08-2012', 'dd-mm-yyyy'), to_date('15-09-2012', 'dd-mm-yyyy'),  '10000r/5m 3max by userId', 'org.n3r.prizedraw.checker.TotalOnceBingooPrizeDrawChecker
org.n3r.prizedraw.checker.DefaultPrizeDrawChecker', 'org.n3r.prizedraw.drawer.WeightedPrizeItemIndexDrawer,org.n3r.prizedraw.drawer.IntelligentPrizeItemDrawer', 'org.n3r.prizedraw.resulter.LogPrizeDrawResulter');


/*==============================================================*/
/* Table: PRIZE_BINGOO                                          */
/*==============================================================*/
create table PRIZE_BINGOO  (
   order_no             varchar2(16)                    not null,
   ACTIVITY_ID          VARCHAR2(10)                    not null,
   BINGOO_TIME          DATE                            not null,
   USER_ID              VARCHAR2(128)                   not null,
   ITEM_ID              VARCHAR2(10)                    not null,
   ITEM_JOIN            NUMBER(1)                      default 0 not null,
   constraint PK_PRIZE_BINGOO primary key (order_no)
);

comment on table PRIZE_BINGOO is
'保存中奖结果';

comment on column PRIZE_BINGOO.order_no is
'随机订单号码';

comment on column PRIZE_BINGOO.ACTIVITY_ID is
'活动标识';

comment on column PRIZE_BINGOO.BINGOO_TIME is
'中奖时间';

comment on column PRIZE_BINGOO.USER_ID is
'中奖用户ID';

comment on column PRIZE_BINGOO.ITEM_ID is
'奖项标识，例如：1表示一等奖，2表示二等奖等';

comment on column PRIZE_BINGOO.ITEM_JOIN is
'是不是不限制数量的参与奖项';

/*==============================================================*/
/* Table: PRIZE_CHECK                                           */
/*==============================================================*/
create table PRIZE_CHECK  (
   ACTIVITY_ID          VARCHAR2(10)                    not null,
   CHECK_PROPERTY       VARCHAR2(32)                    not null,
   NOTEXISTS_TAG        number(1)                      default 0 not null,
   CHECK_VALUE          VARCHAR2(128)                   not null,
   CHECK_COND           VARCHAR2(8)                     not null,
   COMPARATOR           VARCHAR2(128),
   REMARK               VARCHAR2(128),
   constraint PK_PRIZE_CHECK primary key (ACTIVITY_ID, CHECK_PROPERTY, CHECK_VALUE, CHECK_COND)
);

comment on table PRIZE_CHECK is
'抽奖资格审查';

comment on column PRIZE_CHECK.ACTIVITY_ID is
'活动标识。注意：使用0表示针对全部活动。';

comment on column PRIZE_CHECK.CHECK_PROPERTY is
'属性名称';

comment on column PRIZE_CHECK.NOTEXISTS_TAG is
'属性不存在时，是否通过校验。';

comment on column PRIZE_CHECK.CHECK_VALUE is
'属性校验值';

comment on column PRIZE_CHECK.CHECK_COND is
'=,!=,>,<,>=,<=等';

comment on column PRIZE_CHECK.COMPARATOR is
'属性值与校验值之间的比较器，默认是字符串比较器';

/*==============================================================*/
/* Table: PRIZE_FREQUENCE                                       */
/*==============================================================*/
create table PRIZE_FREQUENCE  (
   ACTIVITY_ID          VARCHAR2(10)                    not null,
   FREQUENCY_TAG        VARCHAR2(128)                   not null,
   FREQUENCY_PROPERTY   VARCHAR2(32)                    not null,
   FREQUENCY_VALUE      VARCHAR2(128)                   not null,
   FREQUENCY_TIMES      int,
   constraint PK_PRIZE_FREQUENCE primary key (ACTIVITY_ID, FREQUENCY_TAG, FREQUENCY_PROPERTY, FREQUENCY_VALUE)
);

comment on table PRIZE_FREQUENCE is
'定义抽奖的频率控制';

comment on column PRIZE_FREQUENCE.ACTIVITY_ID is
'活动标识。注意：使用0表示针对全部活动。';

comment on column PRIZE_FREQUENCE.FREQUENCY_TAG is
'如果按照小时控制，那么标记就是每个整小时';

comment on column PRIZE_FREQUENCE.FREQUENCY_PROPERTY is
'在哪个属性上控制频率';

comment on column PRIZE_FREQUENCE.FREQUENCY_VALUE is
'频率属性对应的频率值';

comment on column PRIZE_FREQUENCE.FREQUENCY_TIMES is
'当前频率次数';

/*==============================================================*/
/* Table: PRIZE_ITEM                                            */
/*==============================================================*/
create table PRIZE_ITEM  (
   ACTIVITY_ID          VARCHAR2(10)                    not null,
   ITEM_ID              VARCHAR2(10)                    not null,
   ITEM_NAME            VARCHAR2(64)                    not null,
   ITEM_DESC            VARCHAR2(256)                   not null,
   ITEM_JOIN            NUMBER(1)                      default 0 not null,
   ITEM_TOTAL           int                             not null,
   ITEM_OUT             int                            default 0 not null,
   ITEM_IN              int                             not null,
   ITEM_RANDBASE        int                            default 10000 not null,
   ITEM_LUCKNUM         int                            default 888 not null,
   CHECKERS             VARCHAR2(512),
   ITEM_SPEC            VARCHAR2(512)                   not null,
   ITEM_MD5             VARCHAR2(64),
   ITEM_CHECKPOINTS     date,
   constraint PK_PRIZE_ITEM primary key (ACTIVITY_ID, ITEM_ID)
);

comment on table PRIZE_ITEM is
'定义一次抽奖活动，有哪些奖项，以及奖项设置等';

comment on column PRIZE_ITEM.ACTIVITY_ID is
'活动标识';

comment on column PRIZE_ITEM.ITEM_ID is
'奖项标识，例如：1表示一等奖，2表示二等奖等';

comment on column PRIZE_ITEM.ITEM_NAME is
'奖项名称';

comment on column PRIZE_ITEM.ITEM_DESC is
'奖品描述';

comment on column PRIZE_ITEM.ITEM_JOIN is
'是不是不限制数量的参与奖项';

comment on column PRIZE_ITEM.ITEM_TOTAL is
'奖品数量，-1标识不限制数量';

comment on column PRIZE_ITEM.ITEM_OUT is
'已经中奖数量';

comment on column PRIZE_ITEM.ITEM_IN is
'剩余奖品数量';

comment on column PRIZE_ITEM.ITEM_RANDBASE is
'中奖概率基数';

comment on column PRIZE_ITEM.ITEM_LUCKNUM is
'随机值等于当前值，即为中奖';

comment on column PRIZE_ITEM.CHECKERS is
'定义java类名列表';

comment on column PRIZE_ITEM.ITEM_SPEC is
'定义每一个时间段，开放多少奖项，例如一等奖发放策略：

默认每天开5， 09:00~11:30开2，11:30~14:00开3。
2012-08-01开10， 09:00~11:30开3， 11:30~14:00开7。
2012-08-02开15 ，00:00~09:00开5， 09:00~11:00开5， 11:00~15:00开10。
';

comment on column PRIZE_ITEM.ITEM_MD5 is
'程序使用，无需人工维护。
发放策略摘要，用于比对发放策略，重新生成奖项分配时间';

comment on column PRIZE_ITEM.ITEM_CHECKPOINTS is
'程序使用，无需人工维护。';


insert into PRIZE_ITEM (ACTIVITY_ID, ITEM_ID, ITEM_NAME, ITEM_DESC, ITEM_TOTAL, ITEM_OUT, ITEM_IN, ITEM_RANDBASE, ITEM_LUCKNUM, ITEM_SPEC, ITEM_MD5)
values ('Olympic', '1', '一等奖', 'iPhone5一台', 15, 0, 15, 1000, 777, '每天开5
2012-09-04 09:00~11:00 8', NULL);


/*==============================================================*/
/* Table: PRIZE_ITEM_ASSIGN                                     */
/*==============================================================*/
create table PRIZE_ITEM_ASSIGN  (
   ACTIVITY_ID          VARCHAR2(10)                    not null,
   ITEM_ID              VARCHAR2(10)                    not null,
   ASSIGN_TIME_FROM     DATE                            not null,
   ASSIGN_TIME_TO       DATE                            not null,
   LUCK_TIME            date,
   ASSIGN_NUM           INT                             not null,
   available_num        int                             not null,
   constraint PK_PRIZE_ITEM_ASSIGN primary key (ACTIVITY_ID, ITEM_ID, ASSIGN_TIME_FROM)
);

comment on table PRIZE_ITEM_ASSIGN is
'奖项随机分配表。
根据“抽奖奖项”中的发放策略自动生成。
每当发放策略调整时（摘要变化），对应的奖项分配都重新生成。';

comment on column PRIZE_ITEM_ASSIGN.ACTIVITY_ID is
'活动标识';

comment on column PRIZE_ITEM_ASSIGN.ITEM_ID is
'奖项标识，例如：1表示一等奖，2表示二等奖等';

comment on column PRIZE_ITEM_ASSIGN.ASSIGN_TIME_FROM is
'随机分配时间开始点';

comment on column PRIZE_ITEM_ASSIGN.ASSIGN_TIME_TO is
'随机分配时间结束点';

comment on column PRIZE_ITEM_ASSIGN.LUCK_TIME is
'本随机分配时间范围内，到该必中时间点还有奖项数量时，必中';

comment on column PRIZE_ITEM_ASSIGN.ASSIGN_NUM is
'分配数量';

comment on column PRIZE_ITEM_ASSIGN.available_num is
'可用数量';

/*==============================================================*/
/* Table: PRIZE_WHITE                                           */
/*==============================================================*/
create table PRIZE_WHITE  (
   ACTIVITY_ID          VARCHAR2(10)                    not null,
   USER_ID              VARCHAR2(128)                   not null
);

comment on table PRIZE_WHITE is
'抽奖白名单';

comment on column PRIZE_WHITE.ACTIVITY_ID is
'活动标识';

comment on column PRIZE_WHITE.USER_ID is
'中奖用户ID';

/*==============================================================*/
/* Table: esql_sql                                              */
/*==============================================================*/
create table esql_sql  (
   id                   varchar2(32)                    not null,
   options              varchar2(512),
   sql                  varchar2(1024)                  not null,
   valid                number(1)                      default 1 not null,
   remark               varchar2(1024),
   constraint PK_ESQL_SQL primary key (id)
);

comment on table esql_sql is
'定义esql中的sql语句';

comment on column esql_sql.id is
'SQL标识';

comment on column esql_sql.options is
'SQL选项';

comment on column esql_sql.sql is
'SQL语句';

comment on column esql_sql.valid is
'是否有效1：有效 0无效';

comment on column esql_sql.remark is
'SQL备注';

/*==============================================================*/
/* Table: prize_record                                          */
/*==============================================================*/
create table prize_record  (
   ACTIVITY_ID          VARCHAR2(10)                    not null,
   LAST_TIME            date,
   USER_ID              VARCHAR2(128)                   not null,
   ROBOT_TAG            int                            default 0 not null,
   DRAW_TIMES           int                             not null,
   BINGOO_TIMES         int                            default 0 not null,
   constraint PK_PRIZE_RECORD primary key (ACTIVITY_ID, USER_ID)
);

comment on table prize_record is
'记录用户参与抽奖活动的抽奖记录（经过各项检查合格的用户）';

comment on column prize_record.ACTIVITY_ID is
'活动标识';

comment on column prize_record.LAST_TIME is
'最近抽奖时间';

comment on column prize_record.USER_ID is
'中奖用户ID';

comment on column prize_record.ROBOT_TAG is
'1: 可能是机器人 0 正常';

comment on column prize_record.DRAW_TIMES is
'本轮活动共抽奖次数';

comment on column prize_record.BINGOO_TIMES is
'本轮活动中奖次数';

