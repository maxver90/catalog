create schema `maxver90_catalog`;

create table `categories`
(
    `id`   bigint unsigned auto_increment,
    `name` varchar(50) not null,
    primary key (id)
);

create table `characteristics`
(
    `id`          bigint unsigned auto_increment,
    `category_id` bigint unsigned,
    `title`       varchar(50),
    primary key (`id`),
    foreign key (category_id) references categories (id)
);

create table `products`
(
    `id`          bigint unsigned auto_increment,
    `category_id` bigint unsigned,
    `name`        varchar(50) not null,
    `description` text,
    `price`       int,
    primary key (`id`),
    foreign key (category_id) references categories (id)
);

create table `values`
(
    `id`                bigint unsigned auto_increment,
    `product_id`        bigint unsigned,
    `characteristic_id` bigint unsigned,
    `value`             text,
    primary key (`id`),
    foreign key (`product_id`) references products (id),
    foreign key (characteristic_id) references characteristics (id)
);