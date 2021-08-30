create table states(
num int unsigned,
id int unsigned,
charId int unsigned,
current double,
done boolean,
primary key(id,charId,num)
);
create table achievements(
id  int unsigned,
charId int unsigned,
primary key (id,charId)
);