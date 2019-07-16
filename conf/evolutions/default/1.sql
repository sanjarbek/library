# --- !Ups


create table users
(
    id       serial primary key,
    username    varchar(50)  not null unique,
    password varchar(100) not null
);

insert into users(username, password)
values ('test', 'test12');

create table books
(
    id      serial primary key,
    user_id bigint unsigned not null,
    isn     varchar(50)  not null,
    title   varchar(100) not null,
    author  varchar(100) not null,
    foreign key (user_id) references users(id)
);

create index idx_user on books (user_id);

# --- !Downs

drop table books;
drop table users;
