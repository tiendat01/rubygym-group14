drop database if exists rubygym;
create database rubygym;
use rubygym;

CREATE TABLE `trainer` (
  `id` int NOT NULL auto_increment,
  `avatar` varchar(4000) default null,
  `name` varchar(255) default null,
  `sex` varchar(10) default null,
  `date_of_birth` date default null,
  `phone_number` varchar(20) default null,
  `email` varchar(100) default null,
  `description` varchar(4000)  default null,
  `account_trainer_id` int default null,
  primary key (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `student` (
  `id` int NOT NULL auto_increment,
  `avatar` varchar(4000) default null,
  `name` varchar(255) default null,
  `sex` varchar(10) default null,
  `date_of_birth` date default null,
  `phone_number` varchar(20) default null,
  `email` varchar(100) default null,
  `description` varchar(1000) default null,
  `weight` float default null,
  `height` float default null,
  `bmi` float default null,
  `others` varchar(4000) default null,
  `target` varchar(4000) default null,
  `account_student_id` int default null,
  primary key (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table `account_trainer` (
`id` int not null auto_increment,
`username` varchar(100) not null,
`password` varchar(100) not null,
primary key (`id`),
unique key `username_UNIQUE` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `service` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `category` VARCHAR(45) NOT NULL,
  `period_per_week` INT NOT NULL DEFAULT 0,
  `n_months` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY category_UNIQUE (category)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `account_student` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `accumulation` int DEFAULT '0' not null,
  `expire` date,
  `service_id` int,
  PRIMARY KEY (id),
  UNIQUE KEY username_UNIQUE (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `event` (
  `id` int NOT NULL auto_increment,
  `title` varchar(200) default null,
  `description` varchar(200) default null,
  `category` int NOT NULL,
  `expire` date default null,
  primary key (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `picture` (
  `id` int NOT NULL auto_increment,
  `image` varchar(4000),
  `event_id` int NOT NULL,
  primary key (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `trainer_student` (
  `id` int NOT NULL auto_increment,
  `trainer_id` int NOT NULL,
  `student_id` int NOT NULL,
  `route` varchar(4000) default null,
  `comment` varchar(4000) default null,
   primary key (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `time` (
  `id` int NOT NULL auto_increment,
  `date_of_week` int NOT NULL,
  `start` time NOT NULL,
  `finish` time NOT NULL,
   primary key (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `schedule` (
  `id` int NOT NULL auto_increment,
  `trainer_student_id` int NOT NULL,
  `time_id` int not null,
  primary key (`id`)
  
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `requirement` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `trainer_student_id` INT NOT NULL,
  `schedule_id` INT, -- dành cho thao tác xoá, sửa, ko dành cho tạo mới
  `time_id_new` int,
  `category` int NOT NULL,
  PRIMARY KEY (`id`)
  ) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `period` (
  `id` int NOT NULL AUTO_INCREMENT,
  `trainer_student_id` int NOT NULL,
  `content` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `note` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `time_id` int NOT NULL,
  `p_date` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng này dành cho việc phân tích xử lý lưu trữ dữ liệu, ko liên quan đến app
CREATE TABLE `rubygym`.`period_backup` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `trainer_student_id` INT NOT NULL,
  `time_id` INT NOT NULL,
  `p_date` DATE NOT NULL,
  `content` VARCHAR(1000) NULL,
  `note` VARCHAR(1000) NULL,
  PRIMARY KEY (`id`)
  ) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;


use rubygym;

truncate trainer;
insert into trainer (id, avatar, name, sex, date_of_birth, phone_number, email, description, account_trainer_id) values (1, null, 'Karney Domone', 'Nữ', null, null, null, null, 1);
insert into trainer (id, avatar, name, sex, date_of_birth, phone_number, email, description, account_trainer_id) values (2, null, 'Vinni Whistan', 'Nam', null, null, null, null, 2);
insert into trainer (id, avatar, name, sex, date_of_birth, phone_number, email, description, account_trainer_id) values (3, null, 'Randi O''Loughane', 'Nam', '1990-11-03', '332-516-1544', 'roloughane2@salon.com', 'sit amet consectetuer adipiscing elit proin risus praesent ', 3);
insert into trainer (id, avatar, name, sex, date_of_birth, phone_number, email, description, account_trainer_id) values (4, null, 'Amos Sturdey', 'Nam', '2003-01-17', '772-601-2742', 'asturdey3@nydailynews.com', 'in hac habitasse platea dictumst maecenas ut massa quis augue ', 4);
insert into trainer (id, avatar, name, sex, date_of_birth, phone_number, email, description, account_trainer_id) values (5, null, 'Leeanne Pee', 'Nữ', null, null, null, null, 5);

truncate account_trainer;
insert into account_trainer (id, username, password) values (1, 'kd', 'kd');
insert into account_trainer (id, username, password) values (2, 'vw', 'vw');
insert into account_trainer (id, username, password) values (3, 'rol', 'rol');
insert into account_trainer (id, username, password) values (4, 'as', 'as');
insert into account_trainer (id, username, password) values (5, 'lp', 'lp');


truncate student;
insert into student (id, avatar, name, sex, date_of_birth, phone_number, email, description, weight, height, bmi, others, target, account_student_id) values (1, null, 'Haily Gaspar', 'Nữ', '1996-01-10', '125-416-6500', 'hgaspar0@upenn.edu', 'mauris vulputate elementum nullam varius nulla facilisi cras non velit nec', 80.06, 72.91, 10.84, 'sociis natoque penatibus et magnis dis parturient montes nascetur ridiculus mus', 'nonummy maecenas tincidunt lacus at velit vivamus vel nulla eget eros elementum', 1);
insert into student (id, avatar, name, sex, date_of_birth, phone_number, email, description, weight, height, bmi, others, target, account_student_id) values (2, null, 'Sosanna Mary', 'Nam', '2017-02-01', '494-682-1385', 'smary1@hubpages.com', 'ipsum aliquam non mauris morbi non lectus aliquam sit amet diam in magna', 19.91, 74.68, 19.23, 'odio cras mi pede malesuada in imperdiet et commodo vulputate justo in blandit ultrices', 'egestas metus aenean fermentum donec ut mauris eget massa tempor convallis nulla neque libero convallis ', 2);
insert into student (id, avatar, name, sex, date_of_birth, phone_number, email, description, weight, height, bmi, others, target, account_student_id) values (3, null, 'Allianora Akehurst', 'Nữ', '1985-10-14', '376-779-1068', 'aakehurst2@shinystat.com', 'quam sapien varius ut blandit non interdum in ante vestibulum ante ipsum', 25.3, 13.43, 77.59, 'proin at turpis a pede posuere nonummy integer non velit donec diam neque vestibulum eget vulputate ut', 'interdum in ante vestibulum ante ipsum primis in faucibus orci luctus et ', 3);
insert into student (id, avatar, name, sex, date_of_birth, phone_number, email, description, weight, height, bmi, others, target, account_student_id) values (4, null, 'Elwood Manolov', 'Nam', '2006-06-03', '574-282-0222', 'emanolov3@a8.net', 'quam suspendisse potenti nullam porttitor lacus at turpis donec posuere metus', 87.55, 95.18, 89.14, 'eros viverra eget congue eget semper rutrum nulla nunc purus phasellus', 'nulla sed vel enim sit amet nunc viverra dapibus nulla suscipit ligula in lacus curabitur at ipsum', 4);
insert into student (id, avatar, name, sex, date_of_birth, phone_number, email, description, weight, height, bmi, others, target, account_student_id) values (5, null, 'Jeralee Gouldie', 'Nam', null, null, null, null, null, null, null, null, null, 5);
insert into student (id, avatar, name, sex, date_of_birth, phone_number, email, description, weight, height, bmi, others, target, account_student_id) values (6, null, 'Joseph', 'Nam', null, null, null, null, null, null, null, null, null, 6);
insert into student (id, avatar, name, sex, date_of_birth, phone_number, email, description, weight, height, bmi, others, target, account_student_id) values (7, null, 'David', 'Nam', null, null, null, null, null, null, null, null, null, 7);
insert into student (id, avatar, name, sex, date_of_birth, phone_number, email, description, weight, height, bmi, others, target, account_student_id) values (8, null, 'Duckman', 'Nữ', null, null, null, null, null, null, null, null, null, 8);
insert into student (id, avatar, name, sex, date_of_birth, phone_number, email, description, weight, height, bmi, others, target, account_student_id) values (9, null, 'Solaris', 'Nữ', null, null, null, null, null, null, null, null, null, 9);
insert into student (id, avatar, name, sex, date_of_birth, phone_number, email, description, weight, height, bmi, others, target, account_student_id) values (10, null, 'Linus Tovalds', 'Nam', null, null, null, null, null, null, null, null, null, 10);

truncate account_student;
insert into account_student (id, username, password, accumulation, expire, service_id) values (1, 'hg', 'hg', 3, '2022-02-07', 4);
insert into account_student (id, username, password, accumulation, expire, service_id) values (2, 'sm', 'sm', 4, '2021-02-07', 4);
insert into account_student (id, username, password, accumulation, expire, service_id) values (3, 'aa', 'aa', 5, '2022-02-07', 5);
insert into account_student (id, username, password, accumulation, expire, service_id) values (4, 'em', 'em', 6, '2022-02-07', 5);
insert into account_student (id, username, password, accumulation, expire, service_id) values (5, 'jg', 'jg', 7, '2021-02-07', 6);
insert into account_student (id, username, password, accumulation, expire, service_id) values (6, 'j', 'j', 8, '2022-02-07', 6);
insert into account_student (id, username, password, accumulation, expire, service_id) values (7, 'david', 'david', 9, '2022-02-07', 1);
insert into account_student (id, username, password, accumulation, expire, service_id) values (8, 'duckman', 'duckman', 10, '2022-05-07', 2);
insert into account_student (id, username, password, accumulation, expire, service_id) values (9, 'solaris', 'solaris', 11, '2021-06-07', 3);
insert into account_student (id, username, password, accumulation, expire, service_id) values (10, 'lt', 'lt', 12, '2021-02-07', 7);


truncate trainer_student;
insert into trainer_student (id, trainer_id, student_id, route, comment) values (1, 3, 1, 'dolor quis odio consequat varius integer ac leo pellentesque ultrices mattis odio donec vitae nisi nam ultrices', 'orci luctus et ultrices posuere cubilia curae nulla dapibus dolor vel est donec odio justo');
insert into trainer_student (id, trainer_id, student_id, route, comment) values (2, 1, 2, 'eleifend pede libero quis orci nullam molestie nibh in lectus pellentesque', 'semper sapien a libero nam dui proin leo odio porttitor id consequat in consequat ut nulla sed accumsan felis');
insert into trainer_student (id, trainer_id, student_id, route, comment) values (3, 2, 3, 'lacus curabitur at ipsum ac tellus semper interdum mauris ullamcorper purus sit amet nulla quisque arcu libero rutrum ac lobortis', 'at velit vivamus vel nulla eget eros elementum pellentesque quisque porta volutpat erat quisque erat eros viverra eget');
insert into trainer_student (id, trainer_id, student_id, route, comment) values (4, 5, 4, 'sem duis aliquam convallis nunc proin at turpis a pede', 'nibh in hac habitasse platea dictumst aliquam augue quam sollicitudin vitae');
insert into trainer_student (id, trainer_id, student_id, route, comment) values (5, 3, 5, 'luctus et ultrices posuere cubilia curae donec pharetra magna vestibulum aliquet ultrices erat', 'ligula suspendisse ornare consequat lectus in est risus auctor sed tristique');
insert into trainer_student (id, trainer_id, student_id, route, comment) values (6, 1, 6, 'id justo sit amet sapien dignissim vestibulum vestibulum ante ipsum', 'non mi integer ac neque duis bibendum morbi non quam nec dui luctus');
insert into trainer_student (id, trainer_id, student_id, route, comment) values (10, 1, 10, 'arcu adipiscing molestie hendrerit at vulputate vitae nisl aenean lectus pellentesque eget nunc donec quis orci eget orci vehicula', 'donec posuere metus vitae ipsum aliquam non mauris morbi non');

truncate service;
insert into service (id, category, period_per_week, n_months) values (1, 'RB3M', 0, 3);
insert into service (id, category, period_per_week, n_months) values (2, 'RB6M', 0, 6);
insert into service (id, category, period_per_week, n_months) values (3, 'RB12M', 0, 12);
insert into service (id, category, period_per_week, n_months) values (4, 'PT3M', 3, 3);
insert into service (id, category, period_per_week, n_months) values (5, 'PT6M', 5, 6);
insert into service (id, category, period_per_week, n_months) values (6, 'PT12M', 5, 12);
insert into service (id, category, period_per_week, n_months) values (7, 'VIP3M', 3, 3);
insert into service (id, category, period_per_week, n_months) values (8, 'VIP6M', 5, 6);
insert into service (id, category, period_per_week, n_months) values (9, 'VIP12M', 5, 12);

truncate `time`;
insert into `time` (id, date_of_week, start, finish) values (1, 2, '05:00', '06:00');
insert into `time` (id, date_of_week, start, finish) values (2, 2, '06:00', '07:00');
insert into `time` (id, date_of_week, start, finish) values (3, 2, '07:00', '08:00');
insert into `time` (id, date_of_week, start, finish) values (4, 2, '08:00', '09:00');
insert into `time` (id, date_of_week, start, finish) values (5, 2, '09:00', '10:00');
insert into `time` (id, date_of_week, start, finish) values (6, 2, '10:00', '11:00');
insert into `time` (id, date_of_week, start, finish) values (7, 2, '14:00', '15:00');
insert into `time` (id, date_of_week, start, finish) values (8, 2, '15:00', '16:00');
insert into `time` (id, date_of_week, start, finish) values (9, 2, '16:00', '17:00');
insert into `time` (id, date_of_week, start, finish) values (10, 2, '17:00', '18:00');
insert into `time` (id, date_of_week, start, finish) values (11, 2, '18:00', '19:00');
insert into `time` (id, date_of_week, start, finish) values (12, 2, '19:00', '20:00');

insert into `time` (id, date_of_week, start, finish) values (13, 3, '05:00', '06:00');
insert into `time` (id, date_of_week, start, finish) values (14, 3, '06:00', '07:00');
insert into `time` (id, date_of_week, start, finish) values (15, 3, '07:00', '08:00');
insert into `time` (id, date_of_week, start, finish) values (16, 3, '08:00', '09:00');
insert into `time` (id, date_of_week, start, finish) values (17, 3, '09:00', '10:00');
insert into `time` (id, date_of_week, start, finish) values (18, 3, '10:00', '11:00');
insert into `time` (id, date_of_week, start, finish) values (19, 3, '14:00', '15:00');
insert into `time` (id, date_of_week, start, finish) values (20, 3, '15:00', '16:00');
insert into `time` (id, date_of_week, start, finish) values (21, 3, '16:00', '17:00');
insert into `time` (id, date_of_week, start, finish) values (22, 3, '17:00', '18:00');
insert into `time` (id, date_of_week, start, finish) values (23, 3, '18:00', '19:00');
insert into `time` (id, date_of_week, start, finish) values (24, 3, '19:00', '20:00');

insert into `time` (id, date_of_week, start, finish) values (25, 4, '05:00', '06:00');
insert into `time` (id, date_of_week, start, finish) values (26, 4, '06:00', '07:00');
insert into `time` (id, date_of_week, start, finish) values (27, 4, '07:00', '08:00');
insert into `time` (id, date_of_week, start, finish) values (28, 4, '08:00', '09:00');
insert into `time` (id, date_of_week, start, finish) values (29, 4, '09:00', '10:00');
insert into `time` (id, date_of_week, start, finish) values (30, 4, '10:00', '11:00');
insert into `time` (id, date_of_week, start, finish) values (31, 4, '14:00', '15:00');
insert into `time` (id, date_of_week, start, finish) values (32, 4, '15:00', '16:00');
insert into `time` (id, date_of_week, start, finish) values (33, 4, '16:00', '17:00');
insert into `time` (id, date_of_week, start, finish) values (34, 4, '17:00', '18:00');
insert into `time` (id, date_of_week, start, finish) values (35, 4, '18:00', '19:00');
insert into `time` (id, date_of_week, start, finish) values (36, 4, '19:00', '20:00');

insert into `time` (id, date_of_week, start, finish) values (37, 5, '05:00', '06:00');
insert into `time` (id, date_of_week, start, finish) values (38, 5, '06:00', '07:00');
insert into `time` (id, date_of_week, start, finish) values (39, 5, '07:00', '08:00');
insert into `time` (id, date_of_week, start, finish) values (40, 5, '08:00', '09:00');
insert into `time` (id, date_of_week, start, finish) values (41, 5, '09:00', '10:00');
insert into `time` (id, date_of_week, start, finish) values (42, 5, '10:00', '11:00');
insert into `time` (id, date_of_week, start, finish) values (43, 5, '14:00', '15:00');
insert into `time` (id, date_of_week, start, finish) values (44, 5, '15:00', '16:00');
insert into `time` (id, date_of_week, start, finish) values (45, 5, '16:00', '17:00');
insert into `time` (id, date_of_week, start, finish) values (46, 5, '17:00', '18:00');
insert into `time` (id, date_of_week, start, finish) values (47, 5, '18:00', '19:00');
insert into `time` (id, date_of_week, start, finish) values (48, 5, '19:00', '20:00');

insert into `time` (id, date_of_week, start, finish) values (49, 6, '05:00', '06:00');
insert into `time` (id, date_of_week, start, finish) values (50, 6, '06:00', '07:00');
insert into `time` (id, date_of_week, start, finish) values (51, 6, '07:00', '08:00');
insert into `time` (id, date_of_week, start, finish) values (52, 6, '08:00', '09:00');
insert into `time` (id, date_of_week, start, finish) values (53, 6, '09:00', '10:00');
insert into `time` (id, date_of_week, start, finish) values (54, 6, '10:00', '11:00');
insert into `time` (id, date_of_week, start, finish) values (55, 6, '14:00', '15:00');
insert into `time` (id, date_of_week, start, finish) values (56, 6, '15:00', '16:00');
insert into `time` (id, date_of_week, start, finish) values (57, 6, '16:00', '17:00');
insert into `time` (id, date_of_week, start, finish) values (58, 6, '17:00', '18:00');
insert into `time` (id, date_of_week, start, finish) values (59, 6, '18:00', '19:00');
insert into `time` (id, date_of_week, start, finish) values (60, 6, '19:00', '20:00');

insert into `time` (id, date_of_week, start, finish) values (61, 7, '05:00', '06:00');
insert into `time` (id, date_of_week, start, finish) values (62, 7, '06:00', '07:00');
insert into `time` (id, date_of_week, start, finish) values (63, 7, '07:00', '08:00');
insert into `time` (id, date_of_week, start, finish) values (64, 7, '08:00', '09:00');
insert into `time` (id, date_of_week, start, finish) values (65, 7, '09:00', '10:00');
insert into `time` (id, date_of_week, start, finish) values (66, 7, '10:00', '11:00');
insert into `time` (id, date_of_week, start, finish) values (67, 7, '14:00', '15:00');
insert into `time` (id, date_of_week, start, finish) values (68, 7, '15:00', '16:00');
insert into `time` (id, date_of_week, start, finish) values (69, 7, '16:00', '17:00');
insert into `time` (id, date_of_week, start, finish) values (70, 7, '17:00', '18:00');
insert into `time` (id, date_of_week, start, finish) values (71, 7, '18:00', '19:00');
insert into `time` (id, date_of_week, start, finish) values (72, 7, '19:00', '20:00');

insert into `time` (id, date_of_week, start, finish) values (73, 8, '05:00', '06:00');
insert into `time` (id, date_of_week, start, finish) values (74, 8, '06:00', '07:00');
insert into `time` (id, date_of_week, start, finish) values (75, 8, '07:00', '08:00');
insert into `time` (id, date_of_week, start, finish) values (76, 8, '08:00', '09:00');
insert into `time` (id, date_of_week, start, finish) values (77, 8, '09:00', '10:00');
insert into `time` (id, date_of_week, start, finish) values (78, 8, '10:00', '11:00');
insert into `time` (id, date_of_week, start, finish) values (79, 8, '14:00', '15:00');
insert into `time` (id, date_of_week, start, finish) values (80, 8, '15:00', '16:00');
insert into `time` (id, date_of_week, start, finish) values (81, 8, '16:00', '17:00');
insert into `time` (id, date_of_week, start, finish) values (82, 8, '17:00', '18:00');
insert into `time` (id, date_of_week, start, finish) values (83, 8, '18:00', '19:00');
insert into `time` (id, date_of_week, start, finish) values (84, 8, '19:00', '20:00');


truncate schedule;
INSERT INTO `rubygym`.`schedule` (`trainer_student_id`, `time_id`) VALUES ('1', '2');
INSERT INTO `rubygym`.`schedule` (`trainer_student_id`, `time_id`) VALUES ('1', '15');
INSERT INTO `rubygym`.`schedule` (`trainer_student_id`, `time_id`) VALUES ('5', '1');
INSERT INTO `rubygym`.`schedule` (`trainer_student_id`, `time_id`) VALUES ('5', '15');

truncate requirement;
INSERT INTO `rubygym`.`requirement` (`trainer_student_id`, `schedule_id`, `category`) VALUES ('1', '1', '-1');
INSERT INTO `rubygym`.`requirement` (`trainer_student_id`, `schedule_id`, `time_id_new`, `category`) VALUES ('1', '2', '30', '0');
INSERT INTO `rubygym`.`requirement` (`trainer_student_id`, `time_id_new`, `category`) VALUES ('1', '40', '1');

create table `review_student` (
    `student_id` int NOT NULL,
    `review` varchar(1000) default null,
    `rate` int default null,
    `date` date default null,
    `state` int default 0, -- 0: chờ xử lý, -1: từ chối , 1: phê duyệt
    primary key(student_id)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- hiển thị ra public để quảng bá
create table `review_admin` (
    `student_id` int NOT NULL,
    `review` varchar(1000) default null,
    `rate` int default null,
    `date` date default null,
    primary key(student_id)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;