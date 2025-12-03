create table if not exists credentials (
	id bigserial primary key,
	username varchar(50) unique not null,
	password varchar(100) not null,
	is_admin boolean default false
);
