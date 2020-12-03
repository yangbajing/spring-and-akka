set timezone to 'Asia/Chongqing';
create schema oauth;

create table oauth.managed_key
(
    id             char(24)    not null,
    algorithm      varchar     not null,
    identity_id    varchar(24) not null,
    key            text        not null,
    public_key     text        not null,
    activated_on   timestamp   not null,
    deactivated_on timestamp   not null,
    constraint managed_key_pk primary key (id)
);
create index managed_key_idx_key_algorithm on oauth.managed_key (algorithm);

create table oauth.client_detail
(
    id                            char(24)    not null,
    client_id                     varchar(36) not null,
    client_secret                 varchar(48) not null,
    authorization_grant_types     text[]      not null,
    client_authentication_methods text[]      not null,
    scopes                        text[]      not null,
    redirect_uris                 text[]      not null,
    utc8_create                   timestamp   not null,
    utc8_modified                 timestamp   not null,
    user_id                       bigint      null,
    data_push_url                 varchar(60) null,
    constraint client_detail_pk primary key (id),
    constraint client_detail_key_client_id unique (client_id)
);


-- 测试数据
INSERT INTO oauth.client_detail (id, client_id, client_secret, authorization_grant_types, client_authentication_methods,
                                 scopes, redirect_uris, utc8_create, utc8_modified, user_id, data_push_url)
VALUES ('000000000000000000000000', 'messaging-client', 'secret', '{client_credentials}', '{basic}',
        '{message.read,message.write}', array []::integer[], now(), now(), null, null);
INSERT INTO oauth.client_detail (id, client_id, client_secret, authorization_grant_types, client_authentication_methods,
                                 scopes, redirect_uris, utc8_create, utc8_modified, user_id, data_push_url)
VALUES ('000000000000000000000001', 'ec-client', 'secret', '{client_credentials}', '{basic}',
        '{message.read,message.write,api.read,api.write}', array []::integer[], now(), now(), null, null);


