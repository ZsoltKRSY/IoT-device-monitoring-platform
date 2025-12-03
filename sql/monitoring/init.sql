create table if not exists consumption (
        id bigserial primary key,
        day date not null,
	hour int not null,
	total_consumption real not null,
	measurement_count int,
        device_id bigint
);

create table if not exists devices (
        id bigint primary key,
        max_consumption real not null
);

alter table consumption add constraint fk_device foreign key(device_id) references devices(id);
