set timezone to 'Asia/Chongqing';
-- 元数据管理层
create schema md;

--------------------------------------------------------------------------------
-- Metadata
--------------------------------------------------------------------------------

-- 数据集 元数据
create table if not exists md.coll_metadata
(
    id                bigserial,
    schema_name       varchar   null,
    coll_name         varchar   not null, -- 数据集合存储名（RDBMS：表名，ES：索引名）
    biz_type          varchar   not null, -- 数据集合业务类型类型
    columns           jsonb     not null, -- RDBMS：数据列定义，ES：索引模式
    create_time       timestamp not null,
    db_type           int       not null, -- 数据库类型
    connection_params jsonb     null,     -- 连接参数，非 PG data_warehouse 库时使用
    constraint table_metadata_pk primary key (id),
    constraint table_metadata_k_schema_name_coll_name unique (schema_name, coll_name)
);
create unique index table_metadata_uidx_schema_name_coll_name on md.coll_metadata (schema_name, coll_name);

-- table_metadata#columns [{}]
-- column_name: string = 例名
-- column_type: string = 例数据类型
-- allow_null: bool(false) = 是否能为空，默认址：false
-- default: string? = 默认值
-- comment: string? = 描述

-- coll_metadata 历史
create table if not exists md.table_metadata_history
(
    tmd_id      bigint    not null,
    history     jsonb     not null,
    create_time timestamp not null
);
