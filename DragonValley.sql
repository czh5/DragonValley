CREATE DATABASE DragonValley;

USE DragonValley;



CREATE TABLE userInformation(#用户信息
	id INT PRIMARY KEY AUTO_INCREMENT,
	idNum VARCHAR(25) NOT NULL,#身份证号
	phone VARCHAR(20) NOT NULL,#联系电话
	userId VARCHAR(20) NOT NULL#用户id
);
SELECT * FROM userInformation;
DROP TABLE userinformation;
INSERT INTO userinformation(idNum,phone,userId) VALUES
("lm-123","11111111111","DragonMom"),
("m01-123","11111111111","Manager01"),
("m02-123","11111111111","Manager02"),
("m03-123","11111111111","Manager03"),
("m04-123","11111111111","Manager04"),
("u-dkslfajfn","11111111111","visitor01");






CREATE TABLE users(#用户登陆
	id INT PRIMARY KEY AUTO_INCREMENT,
	loginName VARCHAR(20) NOT NULL,#登陆名
	loginPassword VARCHAR(20) NOT NULL,#登陆密码
	root VARCHAR(10) DEFAULT "visitor"#权限
);
SELECT * FROM users;
INSERT INTO users(loginName,loginPassword,root) VALUES
("DragonMom","123","King"),
("Manager01","123","Manager"),
("Manager02","123","Manager"),
("Manager03","123","Manager"),
("Manager04","123","Manager");

INSERT INTO users(loginName,loginPassword) VALUES
("zhangsan","123");

DROP TABLE users;

DELETE FROM users WHERE loginName="zhangsan"
ALTER TABLE users AUTO_INCREMENT=5;


CREATE TABLE exhibitionHall(#展厅
	id INT PRIMARY KEY AUTO_INCREMENT,#展厅编号
	eType VARCHAR(20),#展厅类型
	introduction VARCHAR(50),#简介 This is a tribe of red dragons
	eSpace VARCHAR(30),#占地面积
	adress VARCHAR(30),#地址
	startTime VARCHAR(10) DEFAULT "8:00",#开始时间
	endTime VARCHAR(10) DEFAULT "20:00",#结束时间
	managerId VARCHAR(20)#管理员编号
);
DROP TABLE exhibitionHall;


SELECT * FROM exhibitionHall;
INSERT INTO exhibitionHall(eType,introduction,eSpace,adress,managerId) VALUES
("red dragon tribe","This is a tribe of red dragons","1000m²","East of the valley","Manager02"),
("blue dragon tribe","This is a tribe of blue dragons","1000m²","West of the valley","Manager01"),
("green dragon tribe","This is a tribe of green dragons","1000m²","North of the valley","Manager03"),
("yellow dragon tribe","This is a tribe of yellow dragons","1000m²","South of the valley","Manager04"),
("black dragon tribe","This is a tribe of black dragons","1000m²","South of the valley","Manager05");



CREATE TABLE dragon(
	id INT PRIMARY KEY AUTO_INCREMENT,#编号
	dName VARCHAR(20),#名字
	dType VARCHAR(20),#类型
	introduction VARCHAR(40),#介绍
	age INT NOT NULL ,#年龄
	healthLevel INT,#健康状况，以分数判定，满分100
	hallId INT #对应的展厅编号
);
SELECT * FROM dragon;

DROP TABLE dragon;

INSERT INTO dragon(dName,dType,introduction,age,healthLevel,hallId) VALUES
("red","red dragon","It is a beautiful red dragon",3,95,1),
("green","green dragon","It is a beautiful green dragon",5,85,3),
("yellow","yellow dragon","It is a beautiful yellow dragon",3,96,4),
("blue","blue dragon","It is a beautiful blue dragon",2,92,2);

#black,black dragon,It is a beautiful blue dragon,1,97



CREATE TABLE tavern AS(#酒馆，用来存放待聘用的驯龙高手
	SELECT userinformation.* 
	FROM userinformation 
	JOIN users 
	ON userinformation.id=users.`id` 
	WHERE users.`root`="visitor"

);
SELECT * FROM tavern;


DROP TABLE tavern;




