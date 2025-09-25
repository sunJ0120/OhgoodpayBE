-- ohgoodpay.grade definition

CREATE TABLE `grade` (
  `grade_name` varchar(255) NOT NULL,
  `limit_price` int(11) NOT NULL,
  `point_percent` float NOT NULL,
  `upgrade` int(11) NOT NULL,
  PRIMARY KEY (`grade_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ohgoodpay.customer definition

CREATE TABLE `customer` (
  `customer_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account` varchar(255) NOT NULL,
  `account_name` varchar(255) NOT NULL,
  `birth` varchar(255) NOT NULL,
  `blocked_cnt` int(11) NOT NULL,
  `email_id` varchar(255) NOT NULL,
  `extension_cnt` int(11) NOT NULL,
  `grade_point` int(11) NOT NULL,
  `hobby` varchar(255) DEFAULT NULL,
  `introduce` varchar(255) DEFAULT NULL,
  `is_auto` bit(1) NOT NULL,
  `is_blocked` bit(1) NOT NULL,
  `is_extension` bit(1) DEFAULT NULL,
  `join_date` datetime(6) NOT NULL DEFAULT sysdate(),
  `name` varchar(255) NOT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `point` int(11) NOT NULL,
  `profile_img` varchar(255) DEFAULT NULL,
  `pwd` varchar(255) NOT NULL,
  `score` int(11) NOT NULL,
  `grade_name` varchar(255) NOT NULL,
  `balance` int(11) NOT NULL,
  PRIMARY KEY (`customer_id`),
  KEY `FKnh2xualbmlwhvj63eu8hg787b` (`grade_name`),
  CONSTRAINT `FKnh2xualbmlwhvj63eu8hg787b` FOREIGN KEY (`grade_name`) REFERENCES `grade` (`grade_name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ohgoodpay.shorts definition

CREATE TABLE `shorts` (
  `shorts_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comment_count` bigint(20) NOT NULL,
  `date` datetime(6) NOT NULL,
  `like_count` bigint(20) NOT NULL,
  `shorts_explain` varchar(255) NOT NULL,
  `shorts_name` varchar(255) NOT NULL,
  `thumbnail` varchar(255) NOT NULL,
  `video_name` varchar(255) NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  PRIMARY KEY (`shorts_id`),
  KEY `FKj8iix7jwt99kjh7fqfkt66vjy` (`customer_id`),
  CONSTRAINT `FKj8iix7jwt99kjh7fqfkt66vjy` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ohgoodpay.reaction definition

CREATE TABLE `reaction` (
  `reaction_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `react` varchar(10) NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  `shorts_id` bigint(20) NOT NULL,
  PRIMARY KEY (`reaction_id`),
  KEY `FKf8x7ojsxl5kirtdcvawaanyh2` (`customer_id`),
  KEY `FKj53fabbu7nnl4m88705lxrfsb` (`shorts_id`),
  CONSTRAINT `FKf8x7ojsxl5kirtdcvawaanyh2` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`),
  CONSTRAINT `FKj53fabbu7nnl4m88705lxrfsb` FOREIGN KEY (`shorts_id`) REFERENCES `shorts` (`shorts_id`)
) ENGINE=InnoDB AUTO_INCREMENT=251 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ohgoodpay.subscription definition

CREATE TABLE `subscription` (
  `subscription_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `follower_id` bigint(20) NOT NULL,
  `following_id` bigint(20) NOT NULL,
  PRIMARY KEY (`subscription_id`),
  KEY `FKhu7wqak5svh86gg4fi5b914t5` (`follower_id`),
  KEY `FKgeb7bao1dix9fp988iwryne37` (`following_id`),
  CONSTRAINT `FKgeb7bao1dix9fp988iwryne37` FOREIGN KEY (`following_id`) REFERENCES `customer` (`customer_id`),
  CONSTRAINT `FKhu7wqak5svh86gg4fi5b914t5` FOREIGN KEY (`follower_id`) REFERENCES `customer` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ohgoodpay.comment definition

CREATE TABLE `comment` (
  `comment_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) NOT NULL,
  `date` datetime(6) NOT NULL,
  `gno` bigint(20) NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  `shorts_id` bigint(20) NOT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `FK_comment_customer` (`customer_id`),
  KEY `FK_comment_shorts` (`shorts_id`),
  CONSTRAINT `FK_comment_shorts` FOREIGN KEY (`shorts_id`) REFERENCES `shorts` (`shorts_id`),
  CONSTRAINT `FKlwqielki359fs4py1a4iw2fdt` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=177 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ohgoodpay.payment_request definition

CREATE TABLE `payment_request` (
  `payment_request_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date` datetime(6) NOT NULL,
  `is_validated` bit(1) NOT NULL,
  `order_id` varchar(255) NOT NULL,
  `pincode` varchar(255) NOT NULL,
  `qrcode` varchar(255) NOT NULL,
  `request_name` varchar(255) NOT NULL,
  `total_price` int(11) NOT NULL,
  PRIMARY KEY (`payment_request_id`)
) ENGINE=InnoDB AUTO_INCREMENT=320 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- ohgoodpay.payment definition

CREATE TABLE `payment` (
  `payment_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date` datetime(6) NOT NULL,
  `is_expired` bit(1) NOT NULL,
  `point` int(11) NOT NULL,
  `price` int(11) NOT NULL,
  `request_name` varchar(255) NOT NULL,
  `total_price` int(11) NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  `payment_request_id` bigint(20) NOT NULL,
  PRIMARY KEY (`payment_id`),
  UNIQUE KEY `UKnwmvcfh661ruc8lvc9kx6nxc6` (`payment_request_id`),
  KEY `FKby2skjf3ov608yb6nm16b49lg` (`customer_id`),
  CONSTRAINT `FKby2skjf3ov608yb6nm16b49lg` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`),
  CONSTRAINT `FKm7r0qd3xwx2306cqntqlk5nnf` FOREIGN KEY (`payment_request_id`) REFERENCES `payment_request` (`payment_request_id`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ohgoodpay.point_history definition

CREATE TABLE `point_history` (
  `point_history_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date` datetime(6) NOT NULL,
  `point` int(11) NOT NULL,
  `point_explain` varchar(255) NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  PRIMARY KEY (`point_history_id`),
  KEY `FK7bpnpboxysn3thylch3iinho2` (`customer_id`),
  CONSTRAINT `FK7bpnpboxysn3thylch3iinho2` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=379 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ohgoodpay.communication definition

CREATE TABLE `communication` (
  `communication_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) NOT NULL,
  `date` datetime(6) NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  PRIMARY KEY (`communication_id`),
  KEY `FKrf3399lfgrboav57jin89lj38` (`customer_id`),
  CONSTRAINT `FKrf3399lfgrboav57jin89lj38` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Customer 데이터 (임의의 유저 3개)
INSERT INTO customer (customer_id, name, email_id, pwd, birth, account, account_name, point, is_blocked, profile_img, nickname, introduce, score, hobby, is_extenstion, is_auto, grade_point, blocked_cnt, extension_cnt, join_date, grade_name, balance) 
VALUES (1, '김민정', 'minjung@ohgood.com', 'password123', '2000-07-26', '110123456789', '신한', 5000, false, 'profile1.jpg', '민정이', '안녕하세요!', 0, '독서,영화감상', false, false, 0, 0, 0, '2025-01-01 10:30:00', 'bronze', 100000);

INSERT INTO customer (customer_id, name, email_id, pwd, birth, account, account_name, point, is_blocked, profile_img, nickname, introduce, score, hobby, is_extenstion, is_auto, grade_point, blocked_cnt, extension_cnt, join_date, grade_name, balance) 
VALUES (2, '오가이', 'ohgai@ohgood.com', 'password123', '1998-03-15', '110123456789', '국민', 5000, false, 'profile1.jpg', '가이', '안녕하세요!', 0, '독서,영화감상', false, false, 0, 0, 0, '2025-01-01 10:30:00', 'bronze', 100000);

INSERT INTO customer (customer_id, name, email_id, pwd, birth, account, account_name, point, is_blocked, profile_img, nickname, introduce, score, hobby, is_extenstion, is_auto, grade_point, blocked_cnt, extension_cnt, join_date, grade_name, balance) 
VALUES (3, '박은호', 'eunho@ohgood.com', 'password123', '2001-12-15', '1101234562189', '토스', 5000, false, 'profile1.jpg', '은호', '안녕하세요!', 0, '독서,영화감상', false, false, 0, 0, 0, '2025-01-01 10:30:00', 'bronze', 100000);

-- Payment Request 더미 데이터 (1월)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(1, '2025-01-05 14:30:00', false, 'ORDER_20250105_001', '1234', 'QR_001', '스타벅스 커피', 4500),
(2, '2025-01-08 09:15:00', false, 'ORDER_20250108_002', '5678', 'QR_002', '맥도날드 햄버거', 6500),
(3, '2025-01-12 18:45:00', false, 'ORDER_20250112_003', '9012', 'QR_003', '교보문고 도서', 12000),
(4, '2025-01-15 11:20:00', false, 'ORDER_20250115_004', '3456', 'QR_004', '올리브영 화장품', 25000),
(5, '2025-01-18 16:30:00', false, 'ORDER_20250118_005', '7890', 'QR_005', 'CGV 영화관', 8000),
(6, '2025-01-22 13:10:00', false, 'ORDER_20250122_006', '2345', 'QR_006', '배달의민족 음식', 15000),
(7, '2025-01-25 20:00:00', false, 'ORDER_20250125_007', '6789', 'QR_007', '네이버페이 충전', 30000),
(8, '2025-01-28 12:45:00', false, 'ORDER_20250128_008', '0123', 'QR_008', '쿠팡 쇼핑', 18000),
(9, '2025-01-30 15:30:00', false, 'ORDER_20250130_009', '4567', 'QR_009', '카카오택시', 3500),
(10, '2025-01-31 19:20:00', false, 'ORDER_20250131_010', '8901', 'QR_010', '편의점 구매', 2800);

-- Payment 더미 데이터 (1월 - 김민정)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(1, '2025-01-05 14:35:00', true, 45, 4500, '스타벅스 커피', 4500, 1, 1),
(2, '2025-01-08 09:20:00', true, 65, 6500, '맥도날드 햄버거', 6500, 1, 2),
(3, '2025-01-12 18:50:00', true, 120, 12000, '교보문고 도서', 12000, 1, 3),
(4, '2025-01-15 11:25:00', true, 250, 25000, '올리브영 화장품', 25000, 1, 4),
(5, '2025-01-18 16:35:00', true, 80, 8000, 'CGV 영화관', 8000, 1, 5),
(6, '2025-01-22 13:15:00', true, 150, 15000, '배달의민족 음식', 15000, 1, 6),
(7, '2025-01-25 20:05:00', true, 300, 30000, '네이버페이 충전', 30000, 1, 7),
(8, '2025-01-28 12:50:00', true, 180, 18000, '쿠팡 쇼핑', 18000, 1, 8),
(9, '2025-01-30 15:35:00', true, 35, 3500, '카카오택시', 3500, 1, 9),
(10, '2025-01-31 19:25:00', true, 28, 2800, '편의점 구매', 2800, 1, 10);

-- Payment Request 더미 데이터 (1월 - 오가이)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(11, '2025-01-03 10:30:00', false, 'ORDER_20250103_011', '1111', 'QR_011', '이마트 장보기', 35000),
(12, '2025-01-07 14:20:00', false, 'ORDER_20250107_012', '2222', 'QR_012', '롯데리아 세트', 5500),
(13, '2025-01-11 16:45:00', false, 'ORDER_20250111_013', '3333', 'QR_013', '다이소 용품', 8000),
(14, '2025-01-14 12:10:00', false, 'ORDER_20250114_014', '4444', 'QR_014', '스타벅스 음료', 5000),
(15, '2025-01-17 19:30:00', false, 'ORDER_20250117_015', '5555', 'QR_015', '네이버 쇼핑', 22000),
(16, '2025-01-21 11:45:00', false, 'ORDER_20250121_016', '6666', 'QR_016', '카카오페이 충전', 15000),
(17, '2025-01-24 15:20:00', false, 'ORDER_20250124_017', '7777', 'QR_017', '교보문고 책', 13000),
(18, '2025-01-27 18:00:00', false, 'ORDER_20250127_018', '8888', 'QR_018', '배달의민족', 12000),
(19, '2025-01-29 13:30:00', false, 'ORDER_20250129_019', '9999', 'QR_019', '올리브영', 18000),
(20, '2025-01-31 17:15:00', false, 'ORDER_20250131_020', '0000', 'QR_020', '편의점', 3200);

-- Payment 더미 데이터 (1월 - 오가이)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(11, '2025-01-03 10:35:00', true, 350, 35000, '이마트 장보기', 35000, 2, 11),
(12, '2025-01-07 14:25:00', true, 55, 5500, '롯데리아 세트', 5500, 2, 12),
(13, '2025-01-11 16:50:00', true, 80, 8000, '다이소 용품', 8000, 2, 13),
(14, '2025-01-14 12:15:00', true, 50, 5000, '스타벅스 음료', 5000, 2, 14),
(15, '2025-01-17 19:35:00', true, 220, 22000, '네이버 쇼핑', 22000, 2, 15),
(16, '2025-01-21 11:50:00', true, 150, 15000, '카카오페이 충전', 15000, 2, 16),
(17, '2025-01-24 15:25:00', true, 130, 13000, '교보문고 책', 13000, 2, 17),
(18, '2025-01-27 18:05:00', true, 120, 12000, '배달의민족', 12000, 2, 18),
(19, '2025-01-29 13:35:00', true, 180, 18000, '올리브영', 18000, 2, 19),
(20, '2025-01-31 17:20:00', true, 32, 3200, '편의점', 3200, 2, 20);

-- Payment Request 더미 데이터 (1월 - 박은호)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(21, '2025-01-02 09:15:00', false, 'ORDER_20250102_021', '1111', 'QR_021', '롯데마트 쇼핑', 28000),
(22, '2025-01-06 13:40:00', false, 'ORDER_20250106_022', '2222', 'QR_022', '버거킹 세트', 7200),
(23, '2025-01-09 17:25:00', false, 'ORDER_20250109_023', '3333', 'QR_023', '교보문고 도서', 15000),
(24, '2025-01-13 11:50:00', false, 'ORDER_20250113_024', '4444', 'QR_024', '스타벅스 커피', 4200),
(25, '2025-01-16 15:30:00', false, 'ORDER_20250116_025', '5555', 'QR_025', '쿠팡 쇼핑', 19000),
(26, '2025-01-19 20:15:00', false, 'ORDER_20250119_026', '6666', 'QR_026', '네이버페이', 25000),
(27, '2025-01-23 14:20:00', false, 'ORDER_20250123_027', '7777', 'QR_027', '배달의민족', 11000),
(28, '2025-01-26 16:45:00', false, 'ORDER_20250126_028', '8888', 'QR_028', '올리브영', 16000),
(29, '2025-01-29 12:30:00', false, 'ORDER_20250129_029', '9999', 'QR_029', 'CGV 영화', 9000),
(30, '2025-01-31 18:40:00', false, 'ORDER_20250131_030', '0000', 'QR_030', '편의점', 4500);

-- Payment 더미 데이터 (1월 - 박은호)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(21, '2025-01-02 09:20:00', true, 280, 28000, '롯데마트 쇼핑', 28000, 3, 21),
(22, '2025-01-06 13:45:00', true, 72, 7200, '버거킹 세트', 7200, 3, 22),
(23, '2025-01-09 17:30:00', true, 150, 15000, '교보문고 도서', 15000, 3, 23),
(24, '2025-01-13 11:55:00', true, 42, 4200, '스타벅스 커피', 4200, 3, 24),
(25, '2025-01-16 15:35:00', true, 190, 19000, '쿠팡 쇼핑', 19000, 3, 25),
(26, '2025-01-19 20:20:00', true, 250, 25000, '네이버페이', 25000, 3, 26),
(27, '2025-01-23 14:25:00', true, 110, 11000, '배달의민족', 11000, 3, 27),
(28, '2025-01-26 16:50:00', true, 160, 16000, '올리브영', 16000, 3, 28),
(29, '2025-01-29 12:35:00', true, 90, 9000, 'CGV 영화', 9000, 3, 29),
(30, '2025-01-31 18:45:00', true, 45, 4500, '편의점', 4500, 3, 30);

-- Payment Request 더미 데이터 (2월)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(31, '2025-02-03 14:30:00', false, 'ORDER_20250203_031', '1234', 'QR_031', '스타벅스 커피', 4800),
(32, '2025-02-07 09:15:00', false, 'ORDER_20250207_032', '5678', 'QR_032', '맥도날드 햄버거', 6800),
(33, '2025-02-11 18:45:00', false, 'ORDER_20250211_033', '9012', 'QR_033', '교보문고 도서', 13000),
(34, '2025-02-14 11:20:00', false, 'ORDER_20250214_034', '3456', 'QR_034', '올리브영 화장품', 22000),
(35, '2025-02-17 16:30:00', false, 'ORDER_20250217_035', '7890', 'QR_035', 'CGV 영화관', 8500),
(36, '2025-02-21 13:10:00', false, 'ORDER_20250221_036', '2345', 'QR_036', '배달의민족 음식', 16000),
(37, '2025-02-24 20:00:00', false, 'ORDER_20250224_037', '6789', 'QR_037', '네이버페이 충전', 28000),
(38, '2025-02-27 12:45:00', false, 'ORDER_20250227_038', '0123', 'QR_038', '쿠팡 쇼핑', 17000),
(39, '2025-02-28 15:30:00', false, 'ORDER_20250228_039', '4567', 'QR_039', '카카오택시', 3800),
(40, '2025-02-28 19:20:00', false, 'ORDER_20250228_040', '8901', 'QR_040', '편의점 구매', 3200);

-- Payment 더미 데이터 (2월 - 김민정)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(31, '2025-02-03 14:35:00', true, 48, 4800, '스타벅스 커피', 4800, 1, 31),
(32, '2025-02-07 09:20:00', true, 68, 6800, '맥도날드 햄버거', 6800, 1, 32),
(33, '2025-02-11 18:50:00', true, 130, 13000, '교보문고 도서', 13000, 1, 33),
(34, '2025-02-14 11:25:00', true, 220, 22000, '올리브영 화장품', 22000, 1, 34),
(35, '2025-02-17 16:35:00', true, 85, 8500, 'CGV 영화관', 8500, 1, 35),
(36, '2025-02-21 13:15:00', true, 160, 16000, '배달의민족 음식', 16000, 1, 36),
(37, '2025-02-24 20:05:00', true, 280, 28000, '네이버페이 충전', 28000, 1, 37),
(38, '2025-02-27 12:50:00', true, 170, 17000, '쿠팡 쇼핑', 17000, 1, 38),
(39, '2025-02-28 15:35:00', true, 38, 3800, '카카오택시', 3800, 1, 39),
(40, '2025-02-28 19:25:00', true, 32, 3200, '편의점 구매', 3200, 1, 40);

-- Payment Request 더미 데이터 (2월 - 오가이)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(41, '2025-02-02 10:30:00', false, 'ORDER_20250202_041', '1111', 'QR_041', '이마트 장보기', 32000),
(42, '2025-02-06 14:20:00', false, 'ORDER_20250206_042', '2222', 'QR_042', '롯데리아 세트', 5800),
(43, '2025-02-10 16:45:00', false, 'ORDER_20250210_043', '3333', 'QR_043', '다이소 용품', 7500),
(44, '2025-02-13 12:10:00', false, 'ORDER_20250213_044', '4444', 'QR_044', '스타벅스 음료', 5200),
(45, '2025-02-16 19:30:00', false, 'ORDER_20250216_045', '5555', 'QR_045', '네이버 쇼핑', 24000),
(46, '2025-02-20 11:45:00', false, 'ORDER_20250220_046', '6666', 'QR_046', '카카오페이 충전', 16000),
(47, '2025-02-23 15:20:00', false, 'ORDER_20250223_047', '7777', 'QR_047', '교보문고 책', 14000),
(48, '2025-02-26 18:00:00', false, 'ORDER_20250226_048', '8888', 'QR_048', '배달의민족', 13000),
(49, '2025-02-28 13:30:00', false, 'ORDER_20250228_049', '9999', 'QR_049', '올리브영', 19000),
(50, '2025-02-28 17:15:00', false, 'ORDER_20250228_050', '0000', 'QR_050', '편의점', 3500);

-- Payment 더미 데이터 (2월 - 오가이)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(41, '2025-02-02 10:35:00', true, 320, 32000, '이마트 장보기', 32000, 2, 41),
(42, '2025-02-06 14:25:00', true, 58, 5800, '롯데리아 세트', 5800, 2, 42),
(43, '2025-02-10 16:50:00', true, 75, 7500, '다이소 용품', 7500, 2, 43),
(44, '2025-02-13 12:15:00', true, 52, 5200, '스타벅스 음료', 5200, 2, 44),
(45, '2025-02-16 19:35:00', true, 240, 24000, '네이버 쇼핑', 24000, 2, 45),
(46, '2025-02-20 11:50:00', true, 160, 16000, '카카오페이 충전', 16000, 2, 46),
(47, '2025-02-23 15:25:00', true, 140, 14000, '교보문고 책', 14000, 2, 47),
(48, '2025-02-26 18:05:00', true, 130, 13000, '배달의민족', 13000, 2, 48),
(49, '2025-02-28 13:35:00', true, 190, 19000, '올리브영', 19000, 2, 49),
(50, '2025-02-28 17:20:00', true, 35, 3500, '편의점', 3500, 2, 50);

-- Payment Request 더미 데이터 (2월 - 박은호)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(51, '2025-02-01 09:15:00', false, 'ORDER_20250201_051', '1111', 'QR_051', '롯데마트 쇼핑', 26000),
(52, '2025-02-05 13:40:00', false, 'ORDER_20250205_052', '2222', 'QR_052', '버거킹 세트', 7500),
(53, '2025-02-08 17:25:00', false, 'ORDER_20250208_053', '3333', 'QR_053', '교보문고 도서', 16000),
(54, '2025-02-12 11:50:00', false, 'ORDER_20250212_054', '4444', 'QR_054', '스타벅스 커피', 4500),
(55, '2025-02-15 15:30:00', false, 'ORDER_20250215_055', '5555', 'QR_055', '쿠팡 쇼핑', 20000),
(56, '2025-02-18 20:15:00', false, 'ORDER_20250218_056', '6666', 'QR_056', '네이버페이', 27000),
(57, '2025-02-22 14:20:00', false, 'ORDER_20250222_057', '7777', 'QR_057', '배달의민족', 12000),
(58, '2025-02-25 16:45:00', false, 'ORDER_20250225_058', '8888', 'QR_058', '올리브영', 17000),
(59, '2025-02-28 12:30:00', false, 'ORDER_20250228_059', '9999', 'QR_059', 'CGV 영화', 9500),
(60, '2025-02-28 18:40:00', false, 'ORDER_20250228_060', '0000', 'QR_060', '편의점', 4800);

-- Payment 더미 데이터 (2월 - 박은호)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(51, '2025-02-01 09:20:00', true, 260, 26000, '롯데마트 쇼핑', 26000, 3, 51),
(52, '2025-02-05 13:45:00', true, 75, 7500, '버거킹 세트', 7500, 3, 52),
(53, '2025-02-08 17:30:00', true, 160, 16000, '교보문고 도서', 16000, 3, 53),
(54, '2025-02-12 11:55:00', true, 45, 4500, '스타벅스 커피', 4500, 3, 54),
(55, '2025-02-15 15:35:00', true, 200, 20000, '쿠팡 쇼핑', 20000, 3, 55),
(56, '2025-02-18 20:20:00', true, 270, 27000, '네이버페이', 27000, 3, 56),
(57, '2025-02-22 14:25:00', true, 120, 12000, '배달의민족', 12000, 3, 57),
(58, '2025-02-25 16:50:00', true, 170, 17000, '올리브영', 17000, 3, 58),
(59, '2025-02-28 12:35:00', true, 95, 9500, 'CGV 영화', 9500, 3, 59),
(60, '2025-02-28 18:45:00', true, 48, 4800, '편의점', 4800, 3, 60);

-- Payment Request 더미 데이터 (3월)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(61, '2025-03-03 14:30:00', false, 'ORDER_20250303_061', '1234', 'QR_061', '스타벅스 커피', 5200),
(62, '2025-03-07 09:15:00', false, 'ORDER_20250307_062', '5678', 'QR_062', '맥도날드 햄버거', 7200),
(63, '2025-03-11 18:45:00', false, 'ORDER_20250311_063', '9012', 'QR_063', '교보문고 도서', 14000),
(64, '2025-03-14 11:20:00', false, 'ORDER_20250314_064', '3456', 'QR_064', '올리브영 화장품', 23000),
(65, '2025-03-17 16:30:00', false, 'ORDER_20250317_065', '7890', 'QR_065', 'CGV 영화관', 9000),
(66, '2025-03-21 13:10:00', false, 'ORDER_20250321_066', '2345', 'QR_066', '배달의민족 음식', 17000),
(67, '2025-03-24 20:00:00', false, 'ORDER_20250324_067', '6789', 'QR_067', '네이버페이 충전', 29000),
(68, '2025-03-27 12:45:00', false, 'ORDER_20250327_068', '0123', 'QR_068', '쿠팡 쇼핑', 18000),
(69, '2025-03-30 15:30:00', false, 'ORDER_20250330_069', '4567', 'QR_069', '카카오택시', 4000),
(70, '2025-03-31 19:20:00', false, 'ORDER_20250331_070', '8901', 'QR_070', '편의점 구매', 3500);

-- Payment 더미 데이터 (3월 - 김민정)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(61, '2025-03-03 14:35:00', true, 52, 5200, '스타벅스 커피', 5200, 1, 61),
(62, '2025-03-07 09:20:00', true, 72, 7200, '맥도날드 햄버거', 7200, 1, 62),
(63, '2025-03-11 18:50:00', true, 140, 14000, '교보문고 도서', 14000, 1, 63),
(64, '2025-03-14 11:25:00', true, 230, 23000, '올리브영 화장품', 23000, 1, 64),
(65, '2025-03-17 16:35:00', true, 90, 9000, 'CGV 영화관', 9000, 1, 65),
(66, '2025-03-21 13:15:00', true, 170, 17000, '배달의민족 음식', 17000, 1, 66),
(67, '2025-03-24 20:05:00', true, 290, 29000, '네이버페이 충전', 29000, 1, 67),
(68, '2025-03-27 12:50:00', true, 180, 18000, '쿠팡 쇼핑', 18000, 1, 68),
(69, '2025-03-30 15:35:00', true, 40, 4000, '카카오택시', 4000, 1, 69),
(70, '2025-03-31 19:25:00', true, 35, 3500, '편의점 구매', 3500, 1, 70);

-- Payment Request 더미 데이터 (3월 - 오가이)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(71, '2025-03-02 10:30:00', false, 'ORDER_20250302_071', '1111', 'QR_071', '이마트 장보기', 33000),
(72, '2025-03-06 14:20:00', false, 'ORDER_20250306_072', '2222', 'QR_072', '롯데리아 세트', 6000),
(73, '2025-03-10 16:45:00', false, 'ORDER_20250310_073', '3333', 'QR_073', '다이소 용품', 8000),
(74, '2025-03-13 12:10:00', false, 'ORDER_20250313_074', '4444', 'QR_074', '스타벅스 음료', 5500),
(75, '2025-03-16 19:30:00', false, 'ORDER_20250316_075', '5555', 'QR_075', '네이버 쇼핑', 25000),
(76, '2025-03-20 11:45:00', false, 'ORDER_20250320_076', '6666', 'QR_076', '카카오페이 충전', 17000),
(77, '2025-03-23 15:20:00', false, 'ORDER_20250323_077', '7777', 'QR_077', '교보문고 책', 15000),
(78, '2025-03-26 18:00:00', false, 'ORDER_20250326_078', '8888', 'QR_078', '배달의민족', 14000),
(79, '2025-03-29 13:30:00', false, 'ORDER_20250329_079', '9999', 'QR_079', '올리브영', 20000),
(80, '2025-03-31 17:15:00', false, 'ORDER_20250331_080', '0000', 'QR_080', '편의점', 3800);

-- Payment 더미 데이터 (3월 - 오가이)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(71, '2025-03-02 10:35:00', true, 330, 33000, '이마트 장보기', 33000, 2, 71),
(72, '2025-03-06 14:25:00', true, 60, 6000, '롯데리아 세트', 6000, 2, 72),
(73, '2025-03-10 16:50:00', true, 80, 8000, '다이소 용품', 8000, 2, 73),
(74, '2025-03-13 12:15:00', true, 55, 5500, '스타벅스 음료', 5500, 2, 74),
(75, '2025-03-16 19:35:00', true, 250, 25000, '네이버 쇼핑', 25000, 2, 75),
(76, '2025-03-20 11:50:00', true, 170, 17000, '카카오페이 충전', 17000, 2, 76),
(77, '2025-03-23 15:25:00', true, 150, 15000, '교보문고 책', 15000, 2, 77),
(78, '2025-03-26 18:05:00', true, 140, 14000, '배달의민족', 14000, 2, 78),
(79, '2025-03-29 13:35:00', true, 200, 20000, '올리브영', 20000, 2, 79),
(80, '2025-03-31 17:20:00', true, 38, 3800, '편의점', 3800, 2, 80);

-- Payment Request 더미 데이터 (3월 - 박은호)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(81, '2025-03-01 09:15:00', false, 'ORDER_20250301_081', '1111', 'QR_081', '롯데마트 쇼핑', 27000),
(82, '2025-03-05 13:40:00', false, 'ORDER_20250305_082', '2222', 'QR_082', '버거킹 세트', 7800),
(83, '2025-03-08 17:25:00', false, 'ORDER_20250308_083', '3333', 'QR_083', '교보문고 도서', 17000),
(84, '2025-03-12 11:50:00', false, 'ORDER_20250312_084', '4444', 'QR_084', '스타벅스 커피', 4800),
(85, '2025-03-15 15:30:00', false, 'ORDER_20250315_085', '5555', 'QR_085', '쿠팡 쇼핑', 21000),
(86, '2025-03-18 20:15:00', false, 'ORDER_20250318_086', '6666', 'QR_086', '네이버페이', 28000),
(87, '2025-03-22 14:20:00', false, 'ORDER_20250322_087', '7777', 'QR_087', '배달의민족', 13000),
(88, '2025-03-25 16:45:00', false, 'ORDER_20250325_088', '8888', 'QR_088', '올리브영', 18000),
(89, '2025-03-28 12:30:00', false, 'ORDER_20250328_089', '9999', 'QR_089', 'CGV 영화', 10000),
(90, '2025-03-31 18:40:00', false, 'ORDER_20250331_090', '0000', 'QR_090', '편의점', 5000);

-- Payment 더미 데이터 (3월 - 박은호)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(81, '2025-03-01 09:20:00', true, 270, 27000, '롯데마트 쇼핑', 27000, 3, 81),
(82, '2025-03-05 13:45:00', true, 78, 7800, '버거킹 세트', 7800, 3, 82),
(83, '2025-03-08 17:30:00', true, 170, 17000, '교보문고 도서', 17000, 3, 83),
(84, '2025-03-12 11:55:00', true, 48, 4800, '스타벅스 커피', 4800, 3, 84),
(85, '2025-03-15 15:35:00', true, 210, 21000, '쿠팡 쇼핑', 21000, 3, 85),
(86, '2025-03-18 20:20:00', true, 280, 28000, '네이버페이', 28000, 3, 86),
(87, '2025-03-22 14:25:00', true, 130, 13000, '배달의민족', 13000, 3, 87),
(88, '2025-03-25 16:50:00', true, 180, 18000, '올리브영', 18000, 3, 88),
(89, '2025-03-28 12:35:00', true, 100, 10000, 'CGV 영화', 10000, 3, 89),
(90, '2025-03-31 18:45:00', true, 50, 5000, '편의점', 5000, 3, 90);

-- Payment Request 더미 데이터 (4월)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(91, '2025-04-03 14:30:00', false, 'ORDER_20250403_091', '1234', 'QR_091', '스타벅스 커피', 5500),
(92, '2025-04-07 09:15:00', false, 'ORDER_20250407_092', '5678', 'QR_092', '맥도날드 햄버거', 7500),
(93, '2025-04-11 18:45:00', false, 'ORDER_20250411_093', '9012', 'QR_093', '교보문고 도서', 15000),
(94, '2025-04-14 11:20:00', false, 'ORDER_20250414_094', '3456', 'QR_094', '올리브영 화장품', 24000),
(95, '2025-04-17 16:30:00', false, 'ORDER_20250417_095', '7890', 'QR_095', 'CGV 영화관', 9500),
(96, '2025-04-21 13:10:00', false, 'ORDER_20250421_096', '2345', 'QR_096', '배달의민족 음식', 18000),
(97, '2025-04-24 20:00:00', false, 'ORDER_20250424_097', '6789', 'QR_097', '네이버페이 충전', 30000),
(98, '2025-04-27 12:45:00', false, 'ORDER_20250427_098', '0123', 'QR_098', '쿠팡 쇼핑', 19000),
(99, '2025-04-30 15:30:00', false, 'ORDER_20250430_099', '4567', 'QR_099', '카카오택시', 4200),
(100, '2025-04-30 19:20:00', false, 'ORDER_20250430_100', '8901', 'QR_100', '편의점 구매', 3800);

-- Payment 더미 데이터 (4월 - 김민정)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(91, '2025-04-03 14:35:00', true, 55, 5500, '스타벅스 커피', 5500, 1, 91),
(92, '2025-04-07 09:20:00', true, 75, 7500, '맥도날드 햄버거', 7500, 1, 92),
(93, '2025-04-11 18:50:00', true, 150, 15000, '교보문고 도서', 15000, 1, 93),
(94, '2025-04-14 11:25:00', true, 240, 24000, '올리브영 화장품', 24000, 1, 94),
(95, '2025-04-17 16:35:00', true, 95, 9500, 'CGV 영화관', 9500, 1, 95),
(96, '2025-04-21 13:15:00', true, 180, 18000, '배달의민족 음식', 18000, 1, 96),
(97, '2025-04-24 20:05:00', true, 300, 30000, '네이버페이 충전', 30000, 1, 97),
(98, '2025-04-27 12:50:00', true, 190, 19000, '쿠팡 쇼핑', 19000, 1, 98),
(99, '2025-04-30 15:35:00', true, 42, 4200, '카카오택시', 4200, 1, 99),
(100, '2025-04-30 19:25:00', true, 38, 3800, '편의점 구매', 3800, 1, 100);

-- Payment Request 더미 데이터 (4월 - 오가이)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(101, '2025-04-02 10:30:00', false, 'ORDER_20250402_101', '1111', 'QR_101', '이마트 장보기', 34000),
(102, '2025-04-06 14:20:00', false, 'ORDER_20250406_102', '2222', 'QR_102', '롯데리아 세트', 6200),
(103, '2025-04-10 16:45:00', false, 'ORDER_20250410_103', '3333', 'QR_103', '다이소 용품', 8500),
(104, '2025-04-13 12:10:00', false, 'ORDER_20250413_104', '4444', 'QR_104', '스타벅스 음료', 5800),
(105, '2025-04-16 19:30:00', false, 'ORDER_20250416_105', '5555', 'QR_105', '네이버 쇼핑', 26000),
(106, '2025-04-20 11:45:00', false, 'ORDER_20250420_106', '6666', 'QR_106', '카카오페이 충전', 18000),
(107, '2025-04-23 15:20:00', false, 'ORDER_20250423_107', '7777', 'QR_107', '교보문고 책', 16000),
(108, '2025-04-26 18:00:00', false, 'ORDER_20250426_108', '8888', 'QR_108', '배달의민족', 15000),
(109, '2025-04-29 13:30:00', false, 'ORDER_20250429_109', '9999', 'QR_109', '올리브영', 21000),
(110, '2025-04-30 17:15:00', false, 'ORDER_20250430_110', '0000', 'QR_110', '편의점', 4000);

-- Payment 더미 데이터 (4월 - 오가이)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(101, '2025-04-02 10:35:00', true, 340, 34000, '이마트 장보기', 34000, 2, 101),
(102, '2025-04-06 14:25:00', true, 62, 6200, '롯데리아 세트', 6200, 2, 102),
(103, '2025-04-10 16:50:00', true, 85, 8500, '다이소 용품', 8500, 2, 103),
(104, '2025-04-13 12:15:00', true, 58, 5800, '스타벅스 음료', 5800, 2, 104),
(105, '2025-04-16 19:35:00', true, 260, 26000, '네이버 쇼핑', 26000, 2, 105),
(106, '2025-04-20 11:50:00', true, 180, 18000, '카카오페이 충전', 18000, 2, 106),
(107, '2025-04-23 15:25:00', true, 160, 16000, '교보문고 책', 16000, 2, 107),
(108, '2025-04-26 18:05:00', true, 150, 15000, '배달의민족', 15000, 2, 108),
(109, '2025-04-29 13:35:00', true, 210, 21000, '올리브영', 21000, 2, 109),
(110, '2025-04-30 17:20:00', true, 40, 4000, '편의점', 4000, 2, 110);

-- Payment Request 더미 데이터 (4월 - 박은호)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(111, '2025-04-01 09:15:00', false, 'ORDER_20250401_111', '1111', 'QR_111', '롯데마트 쇼핑', 28000),
(112, '2025-04-05 13:40:00', false, 'ORDER_20250405_112', '2222', 'QR_112', '버거킹 세트', 8000),
(113, '2025-04-08 17:25:00', false, 'ORDER_20250408_113', '3333', 'QR_113', '교보문고 도서', 18000),
(114, '2025-04-12 11:50:00', false, 'ORDER_20250412_114', '4444', 'QR_114', '스타벅스 커피', 5000),
(115, '2025-04-15 15:30:00', false, 'ORDER_20250415_115', '5555', 'QR_115', '쿠팡 쇼핑', 22000),
(116, '2025-04-18 20:15:00', false, 'ORDER_20250418_116', '6666', 'QR_116', '네이버페이', 29000),
(117, '2025-04-22 14:20:00', false, 'ORDER_20250422_117', '7777', 'QR_117', '배달의민족', 14000),
(118, '2025-04-25 16:45:00', false, 'ORDER_20250425_118', '8888', 'QR_118', '올리브영', 19000),
(119, '2025-04-28 12:30:00', false, 'ORDER_20250428_119', '9999', 'QR_119', 'CGV 영화', 10500),
(120, '2025-04-30 18:40:00', false, 'ORDER_20250430_120', '0000', 'QR_120', '편의점', 5200);

-- Payment 더미 데이터 (4월 - 박은호)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(111, '2025-04-01 09:20:00', true, 280, 28000, '롯데마트 쇼핑', 28000, 3, 111),
(112, '2025-04-05 13:45:00', true, 80, 8000, '버거킹 세트', 8000, 3, 112),
(113, '2025-04-08 17:30:00', true, 180, 18000, '교보문고 도서', 18000, 3, 113),
(114, '2025-04-12 11:55:00', true, 50, 5000, '스타벅스 커피', 5000, 3, 114),
(115, '2025-04-15 15:35:00', true, 220, 22000, '쿠팡 쇼핑', 22000, 3, 115),
(116, '2025-04-18 20:20:00', true, 290, 29000, '네이버페이', 29000, 3, 116),
(117, '2025-04-22 14:25:00', true, 140, 14000, '배달의민족', 14000, 3, 117),
(118, '2025-04-25 16:50:00', true, 190, 19000, '올리브영', 19000, 3, 118),
(119, '2025-04-28 12:35:00', true, 105, 10500, 'CGV 영화', 10500, 3, 119),
(120, '2025-04-30 18:45:00', true, 52, 5200, '편의점', 5200, 3, 120);

-- Payment Request 더미 데이터 (5월)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(121, '2025-05-03 14:30:00', false, 'ORDER_20250503_121', '1234', 'QR_121', '스타벅스 커피', 5800),
(122, '2025-05-07 09:15:00', false, 'ORDER_20250507_122', '5678', 'QR_122', '맥도날드 햄버거', 7800),
(123, '2025-05-11 18:45:00', false, 'ORDER_20250511_123', '9012', 'QR_123', '교보문고 도서', 16000),
(124, '2025-05-14 11:20:00', false, 'ORDER_20250514_124', '3456', 'QR_124', '올리브영 화장품', 25000),
(125, '2025-05-17 16:30:00', false, 'ORDER_20250517_125', '7890', 'QR_125', 'CGV 영화관', 10000),
(126, '2025-05-21 13:10:00', false, 'ORDER_20250521_126', '2345', 'QR_126', '배달의민족 음식', 19000),
(127, '2025-05-24 20:00:00', false, 'ORDER_20250524_127', '6789', 'QR_127', '네이버페이 충전', 31000),
(128, '2025-05-27 12:45:00', false, 'ORDER_20250527_128', '0123', 'QR_128', '쿠팡 쇼핑', 20000),
(129, '2025-05-30 15:30:00', false, 'ORDER_20250530_129', '4567', 'QR_129', '카카오택시', 4500),
(130, '2025-05-31 19:20:00', false, 'ORDER_20250531_130', '8901', 'QR_130', '편의점 구매', 4000);

-- Payment 더미 데이터 (5월 - 김민정)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(121, '2025-05-03 14:35:00', true, 58, 5800, '스타벅스 커피', 5800, 1, 121),
(122, '2025-05-07 09:20:00', true, 78, 7800, '맥도날드 햄버거', 7800, 1, 122),
(123, '2025-05-11 18:50:00', true, 160, 16000, '교보문고 도서', 16000, 1, 123),
(124, '2025-05-14 11:25:00', true, 250, 25000, '올리브영 화장품', 25000, 1, 124),
(125, '2025-05-17 16:35:00', true, 100, 10000, 'CGV 영화관', 10000, 1, 125),
(126, '2025-05-21 13:15:00', true, 190, 19000, '배달의민족 음식', 19000, 1, 126),
(127, '2025-05-24 20:05:00', true, 310, 31000, '네이버페이 충전', 31000, 1, 127),
(128, '2025-05-27 12:50:00', true, 200, 20000, '쿠팡 쇼핑', 20000, 1, 128),
(129, '2025-05-30 15:35:00', true, 45, 4500, '카카오택시', 4500, 1, 129),
(130, '2025-05-31 19:25:00', true, 40, 4000, '편의점 구매', 4000, 1, 130);

-- Payment Request 더미 데이터 (5월 - 오가이)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(131, '2025-05-02 10:30:00', false, 'ORDER_20250502_131', '1111', 'QR_131', '이마트 장보기', 35000),
(132, '2025-05-06 14:20:00', false, 'ORDER_20250506_132', '2222', 'QR_132', '롯데리아 세트', 6500),
(133, '2025-05-10 16:45:00', false, 'ORDER_20250510_133', '3333', 'QR_133', '다이소 용품', 9000),
(134, '2025-05-13 12:10:00', false, 'ORDER_20250513_134', '4444', 'QR_134', '스타벅스 음료', 6000),
(135, '2025-05-16 19:30:00', false, 'ORDER_20250516_135', '5555', 'QR_135', '네이버 쇼핑', 27000),
(136, '2025-05-20 11:45:00', false, 'ORDER_20250520_136', '6666', 'QR_136', '카카오페이 충전', 19000),
(137, '2025-05-23 15:20:00', false, 'ORDER_20250523_137', '7777', 'QR_137', '교보문고 책', 17000),
(138, '2025-05-26 18:00:00', false, 'ORDER_20250526_138', '8888', 'QR_138', '배달의민족', 16000),
(139, '2025-05-29 13:30:00', false, 'ORDER_20250529_139', '9999', 'QR_139', '올리브영', 22000),
(140, '2025-05-31 17:15:00', false, 'ORDER_20250531_140', '0000', 'QR_140', '편의점', 4200);

-- Payment 더미 데이터 (5월 - 오가이)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(131, '2025-05-02 10:35:00', true, 350, 35000, '이마트 장보기', 35000, 2, 131),
(132, '2025-05-06 14:25:00', true, 65, 6500, '롯데리아 세트', 6500, 2, 132),
(133, '2025-05-10 16:50:00', true, 90, 9000, '다이소 용품', 9000, 2, 133),
(134, '2025-05-13 12:15:00', true, 60, 6000, '스타벅스 음료', 6000, 2, 134),
(135, '2025-05-16 19:35:00', true, 270, 27000, '네이버 쇼핑', 27000, 2, 135),
(136, '2025-05-20 11:50:00', true, 190, 19000, '카카오페이 충전', 19000, 2, 136),
(137, '2025-05-23 15:25:00', true, 170, 17000, '교보문고 책', 17000, 2, 137),
(138, '2025-05-26 18:05:00', true, 160, 16000, '배달의민족', 16000, 2, 138),
(139, '2025-05-29 13:35:00', true, 220, 22000, '올리브영', 22000, 2, 139),
(140, '2025-05-31 17:20:00', true, 42, 4200, '편의점', 4200, 2, 140);

-- Payment Request 더미 데이터 (5월 - 박은호)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(141, '2025-05-01 09:15:00', false, 'ORDER_20250501_141', '1111', 'QR_141', '롯데마트 쇼핑', 29000),
(142, '2025-05-05 13:40:00', false, 'ORDER_20250505_142', '2222', 'QR_142', '버거킹 세트', 8200),
(143, '2025-05-08 17:25:00', false, 'ORDER_20250508_143', '3333', 'QR_143', '교보문고 도서', 19000),
(144, '2025-05-12 11:50:00', false, 'ORDER_20250512_144', '4444', 'QR_144', '스타벅스 커피', 5200),
(145, '2025-05-15 15:30:00', false, 'ORDER_20250515_145', '5555', 'QR_145', '쿠팡 쇼핑', 23000),
(146, '2025-05-18 20:15:00', false, 'ORDER_20250518_146', '6666', 'QR_146', '네이버페이', 30000),
(147, '2025-05-22 14:20:00', false, 'ORDER_20250522_147', '7777', 'QR_147', '배달의민족', 15000),
(148, '2025-05-25 16:45:00', false, 'ORDER_20250525_148', '8888', 'QR_148', '올리브영', 20000),
(149, '2025-05-28 12:30:00', false, 'ORDER_20250528_149', '9999', 'QR_149', 'CGV 영화', 11000),
(150, '2025-05-31 18:40:00', false, 'ORDER_20250531_150', '0000', 'QR_150', '편의점', 5400);

-- Payment 더미 데이터 (5월 - 박은호)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(141, '2025-05-01 09:20:00', true, 290, 29000, '롯데마트 쇼핑', 29000, 3, 141),
(142, '2025-05-05 13:45:00', true, 82, 8200, '버거킹 세트', 8200, 3, 142),
(143, '2025-05-08 17:30:00', true, 190, 19000, '교보문고 도서', 19000, 3, 143),
(144, '2025-05-12 11:55:00', true, 52, 5200, '스타벅스 커피', 5200, 3, 144),
(145, '2025-05-15 15:35:00', true, 230, 23000, '쿠팡 쇼핑', 23000, 3, 145),
(146, '2025-05-18 20:20:00', true, 300, 30000, '네이버페이', 30000, 3, 146),
(147, '2025-05-22 14:25:00', true, 150, 15000, '배달의민족', 15000, 3, 147),
(148, '2025-05-25 16:50:00', true, 200, 20000, '올리브영', 20000, 3, 148),
(149, '2025-05-28 12:35:00', true, 110, 11000, 'CGV 영화', 11000, 3, 149),
(150, '2025-05-31 18:45:00', true, 54, 5400, '편의점', 5400, 3, 150);

-- Payment Request 더미 데이터 (6월)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(151, '2025-06-03 14:30:00', false, 'ORDER_20250603_151', '1234', 'QR_151', '스타벅스 커피', 6000),
(152, '2025-06-07 09:15:00', false, 'ORDER_20250607_152', '5678', 'QR_152', '맥도날드 햄버거', 8000),
(153, '2025-06-11 18:45:00', false, 'ORDER_20250611_153', '9012', 'QR_153', '교보문고 도서', 17000),
(154, '2025-06-14 11:20:00', false, 'ORDER_20250614_154', '3456', 'QR_154', '올리브영 화장품', 26000),
(155, '2025-06-17 16:30:00', false, 'ORDER_20250617_155', '7890', 'QR_155', 'CGV 영화관', 10500),
(156, '2025-06-21 13:10:00', false, 'ORDER_20250621_156', '2345', 'QR_156', '배달의민족 음식', 20000),
(157, '2025-06-24 20:00:00', false, 'ORDER_20250624_157', '6789', 'QR_157', '네이버페이 충전', 32000),
(158, '2025-06-27 12:45:00', false, 'ORDER_20250627_158', '0123', 'QR_158', '쿠팡 쇼핑', 21000),
(159, '2025-06-30 15:30:00', false, 'ORDER_20250630_159', '4567', 'QR_159', '카카오택시', 4800),
(160, '2025-06-30 19:20:00', false, 'ORDER_20250630_160', '8901', 'QR_160', '편의점 구매', 4200);

-- Payment 더미 데이터 (6월 - 김민정)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(151, '2025-06-03 14:35:00', true, 60, 6000, '스타벅스 커피', 6000, 1, 151),
(152, '2025-06-07 09:20:00', true, 80, 8000, '맥도날드 햄버거', 8000, 1, 152),
(153, '2025-06-11 18:50:00', true, 170, 17000, '교보문고 도서', 17000, 1, 153),
(154, '2025-06-14 11:25:00', true, 260, 26000, '올리브영 화장품', 26000, 1, 154),
(155, '2025-06-17 16:35:00', true, 105, 10500, 'CGV 영화관', 10500, 1, 155),
(156, '2025-06-21 13:15:00', true, 200, 20000, '배달의민족 음식', 20000, 1, 156),
(157, '2025-06-24 20:05:00', true, 320, 32000, '네이버페이 충전', 32000, 1, 157),
(158, '2025-06-27 12:50:00', true, 210, 21000, '쿠팡 쇼핑', 21000, 1, 158),
(159, '2025-06-30 15:35:00', true, 48, 4800, '카카오택시', 4800, 1, 159),
(160, '2025-06-30 19:25:00', true, 42, 4200, '편의점 구매', 4200, 1, 160);

-- Payment Request 더미 데이터 (6월 - 오가이)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(161, '2025-06-02 10:30:00', false, 'ORDER_20250602_161', '1111', 'QR_161', '이마트 장보기', 36000),
(162, '2025-06-06 14:20:00', false, 'ORDER_20250606_162', '2222', 'QR_162', '롯데리아 세트', 6800),
(163, '2025-06-10 16:45:00', false, 'ORDER_20250610_163', '3333', 'QR_163', '다이소 용품', 9500),
(164, '2025-06-13 12:10:00', false, 'ORDER_20250613_164', '4444', 'QR_164', '스타벅스 음료', 6200),
(165, '2025-06-16 19:30:00', false, 'ORDER_20250616_165', '5555', 'QR_165', '네이버 쇼핑', 28000),
(166, '2025-06-20 11:45:00', false, 'ORDER_20250620_166', '6666', 'QR_166', '카카오페이 충전', 20000),
(167, '2025-06-23 15:20:00', false, 'ORDER_20250623_167', '7777', 'QR_167', '교보문고 책', 18000),
(168, '2025-06-26 18:00:00', false, 'ORDER_20250626_168', '8888', 'QR_168', '배달의민족', 17000),
(169, '2025-06-29 13:30:00', false, 'ORDER_20250629_169', '9999', 'QR_169', '올리브영', 23000),
(170, '2025-06-30 17:15:00', false, 'ORDER_20250630_170', '0000', 'QR_170', '편의점', 4400);

-- Payment 더미 데이터 (6월 - 오가이)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(161, '2025-06-02 10:35:00', true, 360, 36000, '이마트 장보기', 36000, 2, 161),
(162, '2025-06-06 14:25:00', true, 68, 6800, '롯데리아 세트', 6800, 2, 162),
(163, '2025-06-10 16:50:00', true, 95, 9500, '다이소 용품', 9500, 2, 163),
(164, '2025-06-13 12:15:00', true, 62, 6200, '스타벅스 음료', 6200, 2, 164),
(165, '2025-06-16 19:35:00', true, 280, 28000, '네이버 쇼핑', 28000, 2, 165),
(166, '2025-06-20 11:50:00', true, 200, 20000, '카카오페이 충전', 20000, 2, 166),
(167, '2025-06-23 15:25:00', true, 180, 18000, '교보문고 책', 18000, 2, 167),
(168, '2025-06-26 18:05:00', true, 170, 17000, '배달의민족', 17000, 2, 168),
(169, '2025-06-29 13:35:00', true, 230, 23000, '올리브영', 23000, 2, 169),
(170, '2025-06-30 17:20:00', true, 44, 4400, '편의점', 4400, 2, 170);

-- Payment Request 더미 데이터 (6월 - 박은호)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(171, '2025-06-01 09:15:00', false, 'ORDER_20250601_171', '1111', 'QR_171', '롯데마트 쇼핑', 30000),
(172, '2025-06-05 13:40:00', false, 'ORDER_20250605_172', '2222', 'QR_172', '버거킹 세트', 8500),
(173, '2025-06-08 17:25:00', false, 'ORDER_20250608_173', '3333', 'QR_173', '교보문고 도서', 20000),
(174, '2025-06-12 11:50:00', false, 'ORDER_20250612_174', '4444', 'QR_174', '스타벅스 커피', 5400),
(175, '2025-06-15 15:30:00', false, 'ORDER_20250615_175', '5555', 'QR_175', '쿠팡 쇼핑', 24000),
(176, '2025-06-18 20:15:00', false, 'ORDER_20250618_176', '6666', 'QR_176', '네이버페이', 31000),
(177, '2025-06-22 14:20:00', false, 'ORDER_20250622_177', '7777', 'QR_177', '배달의민족', 16000),
(178, '2025-06-25 16:45:00', false, 'ORDER_20250625_178', '8888', 'QR_178', '올리브영', 21000),
(179, '2025-06-28 12:30:00', false, 'ORDER_20250628_179', '9999', 'QR_179', 'CGV 영화', 11500),
(180, '2025-06-30 18:40:00', false, 'ORDER_20250630_180', '0000', 'QR_180', '편의점', 5600);

-- Payment 더미 데이터 (6월 - 박은호)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(171, '2025-06-01 09:20:00', true, 300, 30000, '롯데마트 쇼핑', 30000, 3, 171),
(172, '2025-06-05 13:45:00', true, 85, 8500, '버거킹 세트', 8500, 3, 172),
(173, '2025-06-08 17:30:00', true, 200, 20000, '교보문고 도서', 20000, 3, 173),
(174, '2025-06-12 11:55:00', true, 54, 5400, '스타벅스 커피', 5400, 3, 174),
(175, '2025-06-15 15:35:00', true, 240, 24000, '쿠팡 쇼핑', 24000, 3, 175),
(176, '2025-06-18 20:20:00', true, 310, 31000, '네이버페이', 31000, 3, 176),
(177, '2025-06-22 14:25:00', true, 160, 16000, '배달의민족', 16000, 3, 177),
(178, '2025-06-25 16:50:00', true, 210, 21000, '올리브영', 21000, 3, 178),
(179, '2025-06-28 12:35:00', true, 115, 11500, 'CGV 영화', 11500, 3, 179),
(180, '2025-06-30 18:45:00', true, 56, 5600, '편의점', 5600, 3, 180);

-- Payment Request 더미 데이터 (7월)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(181, '2025-07-03 14:30:00', false, 'ORDER_20250703_181', '1234', 'QR_181', '스타벅스 커피', 6200),
(182, '2025-07-07 09:15:00', false, 'ORDER_20250707_182', '5678', 'QR_182', '맥도날드 햄버거', 8200),
(183, '2025-07-11 18:45:00', false, 'ORDER_20250711_183', '9012', 'QR_183', '교보문고 도서', 18000),
(184, '2025-07-14 11:20:00', false, 'ORDER_20250714_184', '3456', 'QR_184', '올리브영 화장품', 27000),
(185, '2025-07-17 16:30:00', false, 'ORDER_20250717_185', '7890', 'QR_185', 'CGV 영화관', 11000),
(186, '2025-07-21 13:10:00', false, 'ORDER_20250721_186', '2345', 'QR_186', '배달의민족 음식', 21000),
(187, '2025-07-24 20:00:00', false, 'ORDER_20250724_187', '6789', 'QR_187', '네이버페이 충전', 33000),
(188, '2025-07-27 12:45:00', false, 'ORDER_20250727_188', '0123', 'QR_188', '쿠팡 쇼핑', 22000),
(189, '2025-07-30 15:30:00', false, 'ORDER_20250730_189', '4567', 'QR_189', '카카오택시', 5000),
(190, '2025-07-31 19:20:00', false, 'ORDER_20250731_190', '8901', 'QR_190', '편의점 구매', 4400);

-- Payment 더미 데이터 (7월 - 김민정)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(181, '2025-07-03 14:35:00', true, 62, 6200, '스타벅스 커피', 6200, 1, 181),
(182, '2025-07-07 09:20:00', true, 82, 8200, '맥도날드 햄버거', 8200, 1, 182),
(183, '2025-07-11 18:50:00', true, 180, 18000, '교보문고 도서', 18000, 1, 183),
(184, '2025-07-14 11:25:00', true, 270, 27000, '올리브영 화장품', 27000, 1, 184),
(185, '2025-07-17 16:35:00', true, 110, 11000, 'CGV 영화관', 11000, 1, 185),
(186, '2025-07-21 13:15:00', true, 210, 21000, '배달의민족 음식', 21000, 1, 186),
(187, '2025-07-24 20:05:00', true, 330, 33000, '네이버페이 충전', 33000, 1, 187),
(188, '2025-07-27 12:50:00', true, 220, 22000, '쿠팡 쇼핑', 22000, 1, 188),
(189, '2025-07-30 15:35:00', true, 50, 5000, '카카오택시', 5000, 1, 189),
(190, '2025-07-31 19:25:00', true, 44, 4400, '편의점 구매', 4400, 1, 190);

-- Payment Request 더미 데이터 (7월 - 오가이)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(191, '2025-07-02 10:30:00', false, 'ORDER_20250702_191', '1111', 'QR_191', '이마트 장보기', 37000),
(192, '2025-07-06 14:20:00', false, 'ORDER_20250706_192', '2222', 'QR_192', '롯데리아 세트', 7000),
(193, '2025-07-10 16:45:00', false, 'ORDER_20250710_193', '3333', 'QR_193', '다이소 용품', 10000),
(194, '2025-07-13 12:10:00', false, 'ORDER_20250713_194', '4444', 'QR_194', '스타벅스 음료', 6400),
(195, '2025-07-16 19:30:00', false, 'ORDER_20250716_195', '5555', 'QR_195', '네이버 쇼핑', 29000),
(196, '2025-07-20 11:45:00', false, 'ORDER_20250720_196', '6666', 'QR_196', '카카오페이 충전', 21000),
(197, '2025-07-23 15:20:00', false, 'ORDER_20250723_197', '7777', 'QR_197', '교보문고 책', 19000),
(198, '2025-07-26 18:00:00', false, 'ORDER_20250726_198', '8888', 'QR_198', '배달의민족', 18000),
(199, '2025-07-29 13:30:00', false, 'ORDER_20250729_199', '9999', 'QR_199', '올리브영', 24000),
(200, '2025-07-31 17:15:00', false, 'ORDER_20250731_200', '0000', 'QR_200', '편의점', 4600);

-- Payment 더미 데이터 (7월 - 오가이)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(191, '2025-07-02 10:35:00', true, 370, 37000, '이마트 장보기', 37000, 2, 191),
(192, '2025-07-06 14:25:00', true, 70, 7000, '롯데리아 세트', 7000, 2, 192),
(193, '2025-07-10 16:50:00', true, 100, 10000, '다이소 용품', 10000, 2, 193),
(194, '2025-07-13 12:15:00', true, 64, 6400, '스타벅스 음료', 6400, 2, 194),
(195, '2025-07-16 19:35:00', true, 290, 29000, '네이버 쇼핑', 29000, 2, 195),
(196, '2025-07-20 11:50:00', true, 210, 21000, '카카오페이 충전', 21000, 2, 196),
(197, '2025-07-23 15:25:00', true, 190, 19000, '교보문고 책', 19000, 2, 197),
(198, '2025-07-26 18:05:00', true, 180, 18000, '배달의민족', 18000, 2, 198),
(199, '2025-07-29 13:35:00', true, 240, 24000, '올리브영', 24000, 2, 199),
(200, '2025-07-31 17:20:00', true, 46, 4600, '편의점', 4600, 2, 200);

-- Payment Request 더미 데이터 (7월 - 박은호)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(201, '2025-07-01 09:15:00', false, 'ORDER_20250701_201', '1111', 'QR_201', '롯데마트 쇼핑', 31000),
(202, '2025-07-05 13:40:00', false, 'ORDER_20250705_202', '2222', 'QR_202', '버거킹 세트', 8700),
(203, '2025-07-08 17:25:00', false, 'ORDER_20250708_203', '3333', 'QR_203', '교보문고 도서', 21000),
(204, '2025-07-12 11:50:00', false, 'ORDER_20250712_204', '4444', 'QR_204', '스타벅스 커피', 5600),
(205, '2025-07-15 15:30:00', false, 'ORDER_20250715_205', '5555', 'QR_205', '쿠팡 쇼핑', 25000),
(206, '2025-07-18 20:15:00', false, 'ORDER_20250718_206', '6666', 'QR_206', '네이버페이', 32000),
(207, '2025-07-22 14:20:00', false, 'ORDER_20250722_207', '7777', 'QR_207', '배달의민족', 17000),
(208, '2025-07-25 16:45:00', false, 'ORDER_20250725_208', '8888', 'QR_208', '올리브영', 22000),
(209, '2025-07-28 12:30:00', false, 'ORDER_20250728_209', '9999', 'QR_209', 'CGV 영화', 12000),
(210, '2025-07-31 18:40:00', false, 'ORDER_20250731_210', '0000', 'QR_210', '편의점', 5800);

-- Payment 더미 데이터 (7월 - 박은호)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(201, '2025-07-01 09:20:00', true, 310, 31000, '롯데마트 쇼핑', 31000, 3, 201),
(202, '2025-07-05 13:45:00', true, 87, 8700, '버거킹 세트', 8700, 3, 202),
(203, '2025-07-08 17:30:00', true, 210, 21000, '교보문고 도서', 21000, 3, 203),
(204, '2025-07-12 11:55:00', true, 56, 5600, '스타벅스 커피', 5600, 3, 204),
(205, '2025-07-15 15:35:00', true, 250, 25000, '쿠팡 쇼핑', 25000, 3, 205),
(206, '2025-07-18 20:20:00', true, 320, 32000, '네이버페이', 32000, 3, 206),
(207, '2025-07-22 14:25:00', true, 170, 17000, '배달의민족', 17000, 3, 207),
(208, '2025-07-25 16:50:00', true, 220, 22000, '올리브영', 22000, 3, 208),
(209, '2025-07-28 12:35:00', true, 120, 12000, 'CGV 영화', 12000, 3, 209),
(210, '2025-07-31 18:45:00', true, 58, 5800, '편의점', 5800, 3, 210);

-- Payment Request 더미 데이터 (8월)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(211, '2025-08-03 14:30:00', false, 'ORDER_20250803_211', '1234', 'QR_211', '스타벅스 커피', 6400),
(212, '2025-08-07 09:15:00', false, 'ORDER_20250807_212', '5678', 'QR_212', '맥도날드 햄버거', 8400),
(213, '2025-08-11 18:45:00', false, 'ORDER_20250811_213', '9012', 'QR_213', '교보문고 도서', 19000),
(214, '2025-08-14 11:20:00', false, 'ORDER_20250814_214', '3456', 'QR_214', '올리브영 화장품', 28000),
(215, '2025-08-17 16:30:00', false, 'ORDER_20250817_215', '7890', 'QR_215', 'CGV 영화관', 11500),
(216, '2025-08-21 13:10:00', false, 'ORDER_20250821_216', '2345', 'QR_216', '배달의민족 음식', 22000),
(217, '2025-08-24 20:00:00', false, 'ORDER_20250824_217', '6789', 'QR_217', '네이버페이 충전', 34000),
(218, '2025-08-27 12:45:00', false, 'ORDER_20250827_218', '0123', 'QR_218', '쿠팡 쇼핑', 23000),
(219, '2025-08-30 15:30:00', false, 'ORDER_20250830_219', '4567', 'QR_219', '카카오택시', 5200),
(220, '2025-08-31 19:20:00', false, 'ORDER_20250831_220', '8901', 'QR_220', '편의점 구매', 4600);

-- Payment 더미 데이터 (8월 - 김민정)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(211, '2025-08-03 14:35:00', true, 64, 6400, '스타벅스 커피', 6400, 1, 211),
(212, '2025-08-07 09:20:00', true, 84, 8400, '맥도날드 햄버거', 8400, 1, 212),
(213, '2025-08-11 18:50:00', true, 190, 19000, '교보문고 도서', 19000, 1, 213),
(214, '2025-08-14 11:25:00', true, 280, 28000, '올리브영 화장품', 28000, 1, 214),
(215, '2025-08-17 16:35:00', true, 115, 11500, 'CGV 영화관', 11500, 1, 215),
(216, '2025-08-21 13:15:00', true, 220, 22000, '배달의민족 음식', 22000, 1, 216),
(217, '2025-08-24 20:05:00', true, 340, 34000, '네이버페이 충전', 34000, 1, 217),
(218, '2025-08-27 12:50:00', true, 230, 23000, '쿠팡 쇼핑', 23000, 1, 218),
(219, '2025-08-30 15:35:00', true, 52, 5200, '카카오택시', 5200, 1, 219),
(220, '2025-08-31 19:25:00', true, 46, 4600, '편의점 구매', 4600, 1, 220);

-- Payment Request 더미 데이터 (8월 - 오가이)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(221, '2025-08-02 10:30:00', false, 'ORDER_20250802_221', '1111', 'QR_221', '이마트 장보기', 38000),
(222, '2025-08-06 14:20:00', false, 'ORDER_20250806_222', '2222', 'QR_222', '롯데리아 세트', 7200),
(223, '2025-08-10 16:45:00', false, 'ORDER_20250810_223', '3333', 'QR_223', '다이소 용품', 10500),
(224, '2025-08-13 12:10:00', false, 'ORDER_20250813_224', '4444', 'QR_224', '스타벅스 음료', 6600),
(225, '2025-08-16 19:30:00', false, 'ORDER_20250816_225', '5555', 'QR_225', '네이버 쇼핑', 30000),
(226, '2025-08-20 11:45:00', false, 'ORDER_20250820_226', '6666', 'QR_226', '카카오페이 충전', 22000),
(227, '2025-08-23 15:20:00', false, 'ORDER_20250823_227', '7777', 'QR_227', '교보문고 책', 20000),
(228, '2025-08-26 18:00:00', false, 'ORDER_20250826_228', '8888', 'QR_228', '배달의민족', 19000),
(229, '2025-08-29 13:30:00', false, 'ORDER_20250829_229', '9999', 'QR_229', '올리브영', 25000),
(230, '2025-08-31 17:15:00', false, 'ORDER_20250831_230', '0000', 'QR_230', '편의점', 4800);

-- Payment 더미 데이터 (8월 - 오가이)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(221, '2025-08-02 10:35:00', true, 380, 38000, '이마트 장보기', 38000, 2, 221),
(222, '2025-08-06 14:25:00', true, 72, 7200, '롯데리아 세트', 7200, 2, 222),
(223, '2025-08-10 16:50:00', true, 105, 10500, '다이소 용품', 10500, 2, 223),
(224, '2025-08-13 12:15:00', true, 66, 6600, '스타벅스 음료', 6600, 2, 224),
(225, '2025-08-16 19:35:00', true, 300, 30000, '네이버 쇼핑', 30000, 2, 225),
(226, '2025-08-20 11:50:00', true, 220, 22000, '카카오페이 충전', 22000, 2, 226),
(227, '2025-08-23 15:25:00', true, 200, 20000, '교보문고 책', 20000, 2, 227),
(228, '2025-08-26 18:05:00', true, 190, 19000, '배달의민족', 19000, 2, 228),
(229, '2025-08-29 13:35:00', true, 250, 25000, '올리브영', 25000, 2, 229),
(230, '2025-08-31 17:20:00', true, 48, 4800, '편의점', 4800, 2, 230);

-- Payment Request 더미 데이터 (8월 - 박은호)
INSERT INTO payment_request (payment_request_id, date, is_validated, order_id, pincode, qrcode, request_name, total_price) VALUES
(231, '2025-08-01 09:15:00', false, 'ORDER_20250801_231', '1111', 'QR_231', '롯데마트 쇼핑', 32000),
(232, '2025-08-05 13:40:00', false, 'ORDER_20250805_232', '2222', 'QR_232', '버거킹 세트', 8900),
(233, '2025-08-08 17:25:00', false, 'ORDER_20250808_233', '3333', 'QR_233', '교보문고 도서', 22000),
(234, '2025-08-12 11:50:00', false, 'ORDER_20250812_234', '4444', 'QR_234', '스타벅스 커피', 5800),
(235, '2025-08-15 15:30:00', false, 'ORDER_20250815_235', '5555', 'QR_235', '쿠팡 쇼핑', 26000),
(236, '2025-08-18 20:15:00', false, 'ORDER_20250818_236', '6666', 'QR_236', '네이버페이', 33000),
(237, '2025-08-22 14:20:00', false, 'ORDER_20250822_237', '7777', 'QR_237', '배달의민족', 18000),
(238, '2025-08-25 16:45:00', false, 'ORDER_20250825_238', '8888', 'QR_238', '올리브영', 23000),
(239, '2025-08-28 12:30:00', false, 'ORDER_20250828_239', '9999', 'QR_239', 'CGV 영화', 12500),
(240, '2025-08-31 18:40:00', false, 'ORDER_20250831_240', '0000', 'QR_240', '편의점', 6000);

-- Payment 더미 데이터 (8월 - 박은호)
INSERT INTO payment (payment_id, date, is_expired, point, price, request_name, total_price, customer_id, payment_request_id) VALUES
(231, '2025-08-01 09:20:00', true, 320, 32000, '롯데마트 쇼핑', 32000, 3, 231),
(232, '2025-08-05 13:45:00', true, 89, 8900, '버거킹 세트', 8900, 3, 232),
(233, '2025-08-08 17:30:00', true, 220, 22000, '교보문고 도서', 22000, 3, 233),
(234, '2025-08-12 11:55:00', true, 58, 5800, '스타벅스 커피', 5800, 3, 234),
(235, '2025-08-15 15:35:00', true, 260, 26000, '쿠팡 쇼핑', 26000, 3, 235),
(236, '2025-08-18 20:20:00', true, 330, 33000, '네이버페이', 33000, 3, 236),
(237, '2025-08-22 14:25:00', true, 180, 18000, '배달의민족', 18000, 3, 237),
(238, '2025-08-25 16:50:00', true, 230, 23000, '올리브영', 23000, 3, 238),
(239, '2025-08-28 12:35:00', true, 125, 12500, 'CGV 영화', 12500, 3, 239),
(240, '2025-08-31 18:45:00', true, 60, 6000, '편의점', 6000, 3, 240);
