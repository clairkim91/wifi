-- mysql 로 사용자 변경
use mysql;

-- 사용자 검색
select * from user;

-- 사용자 생성
create user 'wifi'@'%' identified by 'wnsah12';

-- grant all privileges on 데이터베이스명.* to '사용자이름'@'%' identified by '사용자비밀번호';
grant all privileges on *.* to 'wifi'@'%' identified by 'wnsah12';

-- 권한 적용
flush privileges;

-- 데이터베이스 생성
CREATE DATABASE `wifiservice_mariadb` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;