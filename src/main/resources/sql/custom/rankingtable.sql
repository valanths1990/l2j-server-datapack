use l2jserver;

create table ranking(
id int unsigned,
name varchar(45),
type enum("CLAN","PLAYER"),
rankingtype enum("ALLTIMEPVP", "WEEKLYPVP", "MONTHLYPVP", "ALLTIMEPK", "WEEKLYPK", "MONTHLYPK", "ALLTIMEASSIST", "WEEKLYASSIST", "MONTHLYASSIST", "ALLTIMESCORE", "WEEKLYSCORE", "MONTHLYSCORE"),
count int,
primary key(id,rankingtype)
);



