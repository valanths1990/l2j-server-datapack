use l2jserver;

drop table player_wearing_skin;
create table player_wearing_skin(
char_id  int unsigned,
skin_id  int unsigned,
skin_part varchar(45),
primary key(char_id,skin_id,skin_part),
foreign key(char_id) references player_skins(char_id) on delete cascade,
foreign key(skin_id) references player_skins(skin_id) on delete cascade
);

create table player_skins(
char_id int unsigned,
skin_id int unsigned,
skin_part varchar(45),
icon varchar(45),
skin_type varchar(45) default 'NONE',
primary key(skin_id,char_id),
foreign key(char_id) references characters(charId) on delete cascade
);

create table player_skin_config(
char_id int unsigned,
visibility enum('NONE','MINE','THEIR','ALL') default 'NONE',

primary key(char_id),
foreign key(char_id) references characters(charId) on delete cascade

);

select * from characters;
select * from player_wearing_skin;
select * from player_skins;
insert into player_skins(char_id,skin_id,icon,skin_part) values(268481322,6408,"",'alldress');
insert into player_skins(char_id,skin_id,icon,skin_part) values(268481322,1,"","LIGHTCHEST");

insert into player_wearing_skin(char_id,skin_id,skin_part) values(268481322,6408,"ALLDRESS") on  duplicate key update skin_id = 6408 and skin_part="ALLDRESS";

update player_wearing_skin set skin_id= 1 where char_id=268481322;

delete from player_wearing_skin where char_id>0;
delete from player_skins where char_id>0;
delete from player_skin_config where char_id>0;

