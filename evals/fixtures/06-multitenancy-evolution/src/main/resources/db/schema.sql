create table projects (id varchar(64) primary key, owner_user varchar(64) not null, name varchar(200) not null);
create table audit_log (id integer generated always as identity, user_id varchar(64), action varchar(100), project_id varchar(64));
