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
'����һ�ֳ齱�';

comment on column PRIZE_ACTIVITY.ACTIVITY_ID is
'���ʶ';

comment on column PRIZE_ACTIVITY.ACTIVITY_NAME is
'����ƣ����磺���˳齱��ƹ�ҳ齱��';

comment on column PRIZE_ACTIVITY.ACTIVITY_EFF is
'��ʼ����';

comment on column PRIZE_ACTIVITY.ACTIVITY_EXP is
'��������';

comment on column PRIZE_ACTIVITY.FREQUENCY_SPEC is
'��ͬ�û��齱Ƶ�ʿ��ƣ����磺
ÿ5�������10�Σ�������Ƶ3�Σ���������';

comment on column PRIZE_ACTIVITY.CHECKERS is
'����java�����б�';

comment on column PRIZE_ACTIVITY.DRAWERS is
'����JAVA�����б�';

comment on column PRIZE_ACTIVITY.RESULTERS is
'����JAVA�����б�';

insert into PRIZE_ACTIVITY (ACTIVITY_ID, ACTIVITY_NAME, ACTIVITY_EFF, ACTIVITY_EXP, FREQUENCY_SPEC, CHECKERS, DRAWERS, RESULTERS)
values ('Olympic', '���˳齱�', to_date('28-08-2012', 'dd-mm-yyyy'), to_date('15-09-2012', 'dd-mm-yyyy'),  '10000r/5m 3max by userId', 'org.n3r.prizedraw.checker.TotalOnceBingooPrizeDrawChecker
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
'�����н����';

comment on column PRIZE_BINGOO.order_no is
'�����������';

comment on column PRIZE_BINGOO.ACTIVITY_ID is
'���ʶ';

comment on column PRIZE_BINGOO.BINGOO_TIME is
'�н�ʱ��';

comment on column PRIZE_BINGOO.USER_ID is
'�н��û�ID';

comment on column PRIZE_BINGOO.ITEM_ID is
'�����ʶ�����磺1��ʾһ�Ƚ���2��ʾ���Ƚ���';

comment on column PRIZE_BINGOO.ITEM_JOIN is
'�ǲ��ǲ����������Ĳ��뽱��';

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
'�齱�ʸ����';

comment on column PRIZE_CHECK.ACTIVITY_ID is
'���ʶ��ע�⣺ʹ��0��ʾ���ȫ�����';

comment on column PRIZE_CHECK.CHECK_PROPERTY is
'��������';

comment on column PRIZE_CHECK.NOTEXISTS_TAG is
'���Բ�����ʱ���Ƿ�ͨ��У�顣';

comment on column PRIZE_CHECK.CHECK_VALUE is
'����У��ֵ';

comment on column PRIZE_CHECK.CHECK_COND is
'=,!=,>,<,>=,<=��';

comment on column PRIZE_CHECK.COMPARATOR is
'����ֵ��У��ֵ֮��ıȽ�����Ĭ�����ַ����Ƚ���';

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
'����齱��Ƶ�ʿ���';

comment on column PRIZE_FREQUENCE.ACTIVITY_ID is
'���ʶ��ע�⣺ʹ��0��ʾ���ȫ�����';

comment on column PRIZE_FREQUENCE.FREQUENCY_TAG is
'�������Сʱ���ƣ���ô��Ǿ���ÿ����Сʱ';

comment on column PRIZE_FREQUENCE.FREQUENCY_PROPERTY is
'���ĸ������Ͽ���Ƶ��';

comment on column PRIZE_FREQUENCE.FREQUENCY_VALUE is
'Ƶ�����Զ�Ӧ��Ƶ��ֵ';

comment on column PRIZE_FREQUENCE.FREQUENCY_TIMES is
'��ǰƵ�ʴ���';

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
'����һ�γ齱�������Щ����Լ��������õ�';

comment on column PRIZE_ITEM.ACTIVITY_ID is
'���ʶ';

comment on column PRIZE_ITEM.ITEM_ID is
'�����ʶ�����磺1��ʾһ�Ƚ���2��ʾ���Ƚ���';

comment on column PRIZE_ITEM.ITEM_NAME is
'��������';

comment on column PRIZE_ITEM.ITEM_DESC is
'��Ʒ����';

comment on column PRIZE_ITEM.ITEM_JOIN is
'�ǲ��ǲ����������Ĳ��뽱��';

comment on column PRIZE_ITEM.ITEM_TOTAL is
'��Ʒ������-1��ʶ����������';

comment on column PRIZE_ITEM.ITEM_OUT is
'�Ѿ��н�����';

comment on column PRIZE_ITEM.ITEM_IN is
'ʣ�ཱƷ����';

comment on column PRIZE_ITEM.ITEM_RANDBASE is
'�н����ʻ���';

comment on column PRIZE_ITEM.ITEM_LUCKNUM is
'���ֵ���ڵ�ǰֵ����Ϊ�н�';

comment on column PRIZE_ITEM.CHECKERS is
'����java�����б�';

comment on column PRIZE_ITEM.ITEM_SPEC is
'����ÿһ��ʱ��Σ����Ŷ��ٽ������һ�Ƚ����Ų��ԣ�

Ĭ��ÿ�쿪5�� 09:00~11:30��2��11:30~14:00��3��
2012-08-01��10�� 09:00~11:30��3�� 11:30~14:00��7��
2012-08-02��15 ��00:00~09:00��5�� 09:00~11:00��5�� 11:00~15:00��10��
';

comment on column PRIZE_ITEM.ITEM_MD5 is
'����ʹ�ã������˹�ά����
���Ų���ժҪ�����ڱȶԷ��Ų��ԣ��������ɽ������ʱ��';

comment on column PRIZE_ITEM.ITEM_CHECKPOINTS is
'����ʹ�ã������˹�ά����';


insert into PRIZE_ITEM (ACTIVITY_ID, ITEM_ID, ITEM_NAME, ITEM_DESC, ITEM_TOTAL, ITEM_OUT, ITEM_IN, ITEM_RANDBASE, ITEM_LUCKNUM, ITEM_SPEC, ITEM_MD5)
values ('Olympic', '1', 'һ�Ƚ�', 'iPhone5һ̨', 15, 0, 15, 1000, 777, 'ÿ�쿪5
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
'������������
���ݡ��齱����еķ��Ų����Զ����ɡ�
ÿ�����Ų��Ե���ʱ��ժҪ�仯������Ӧ�Ľ�����䶼�������ɡ�';

comment on column PRIZE_ITEM_ASSIGN.ACTIVITY_ID is
'���ʶ';

comment on column PRIZE_ITEM_ASSIGN.ITEM_ID is
'�����ʶ�����磺1��ʾһ�Ƚ���2��ʾ���Ƚ���';

comment on column PRIZE_ITEM_ASSIGN.ASSIGN_TIME_FROM is
'�������ʱ�俪ʼ��';

comment on column PRIZE_ITEM_ASSIGN.ASSIGN_TIME_TO is
'�������ʱ�������';

comment on column PRIZE_ITEM_ASSIGN.LUCK_TIME is
'���������ʱ�䷶Χ�ڣ����ñ���ʱ��㻹�н�������ʱ������';

comment on column PRIZE_ITEM_ASSIGN.ASSIGN_NUM is
'��������';

comment on column PRIZE_ITEM_ASSIGN.available_num is
'��������';

/*==============================================================*/
/* Table: PRIZE_WHITE                                           */
/*==============================================================*/
create table PRIZE_WHITE  (
   ACTIVITY_ID          VARCHAR2(10)                    not null,
   USER_ID              VARCHAR2(128)                   not null
);

comment on table PRIZE_WHITE is
'�齱������';

comment on column PRIZE_WHITE.ACTIVITY_ID is
'���ʶ';

comment on column PRIZE_WHITE.USER_ID is
'�н��û�ID';

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
'����esql�е�sql���';

comment on column esql_sql.id is
'SQL��ʶ';

comment on column esql_sql.options is
'SQLѡ��';

comment on column esql_sql.sql is
'SQL���';

comment on column esql_sql.valid is
'�Ƿ���Ч1����Ч 0��Ч';

comment on column esql_sql.remark is
'SQL��ע';

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
'��¼�û�����齱��ĳ齱��¼������������ϸ���û���';

comment on column prize_record.ACTIVITY_ID is
'���ʶ';

comment on column prize_record.LAST_TIME is
'����齱ʱ��';

comment on column prize_record.USER_ID is
'�н��û�ID';

comment on column prize_record.ROBOT_TAG is
'1: �����ǻ����� 0 ����';

comment on column prize_record.DRAW_TIMES is
'���ֻ���齱����';

comment on column prize_record.BINGOO_TIMES is
'���ֻ�н�����';

