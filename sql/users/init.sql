create table if not exists users (
	user_id bigint primary key,
	first_name varchar(50),
	last_name varchar(50),
	email varchar(100) not null,
	address varchar(200)
);
