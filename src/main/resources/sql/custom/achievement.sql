use l2jserver;

create table achievements
(
    id     int unsigned,
    charId int unsigned,
    primary key (id, charId)
);
create table states
(
    num     int unsigned,
    id      int unsigned,
    charId  int unsigned,
    current double,
    done    boolean,
    primary key (id, charId, num),
    foreign key (id, charId) references achievements (id, charId) on delete CASCADE
);

insert ignore into achievements(id, charId) values (2, 1);
insert into states(num, id, charId, current, done) values (1, 1, 1, 1, true) on duplicate key update current = 3 , done = true;

select * from states;
select * from achievements inner join states s on achievements.id = s.id and achievements.charId = s.charId;

delete from achievements where id = 1;