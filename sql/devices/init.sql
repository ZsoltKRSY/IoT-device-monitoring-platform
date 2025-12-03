create table if not exists devices
(
	id bigserial primary key,
	name varchar(50) not null,
	max_consumption real not null,
	manufacturer varchar(50),
	model varchar(100),
	description text,
	user_id bigint
);

create table if not exists users
(
	id bigint primary key
);

alter table devices add constraint kf_user foreign key(user_id) references users(id);
