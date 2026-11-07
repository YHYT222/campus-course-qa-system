-- =====================================================
-- 校园课程互助答疑系统 数据库脚本
-- 数据库: course_qa_db  (MySQL 8.x)
-- 密码加密方式: MD5(明文密码 + 'campus')
-- 预置账号: admin/admin123(管理员)  student1~3/123456(学生)
-- =====================================================

CREATE DATABASE IF NOT EXISTS course_qa_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE course_qa_db;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS favorite;
DROP TABLE IF EXISTS post_like;
DROP TABLE IF EXISTS reply;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS course;
DROP TABLE IF EXISTS `user`;
SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
CREATE TABLE `user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `student_no`  VARCHAR(20)  NOT NULL COMMENT '学号',
  `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
  `password`    VARCHAR(64)  NOT NULL COMMENT '密码(MD5加盐)',
  `role`        TINYINT      NOT NULL DEFAULT 0 COMMENT '角色 0学生 1管理员',
  `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `status`      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态 0正常 1禁言 2封禁',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_no` (`student_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 2. 课程分类表
-- ----------------------------
CREATE TABLE `course` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `name`        VARCHAR(50)  NOT NULL COMMENT '课程名称',
  `description` VARCHAR(255) DEFAULT '' COMMENT '课程简介',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程分类表';

-- ----------------------------
-- 3. 答疑帖子表
-- ----------------------------
CREATE TABLE `post` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `user_id`     BIGINT       NOT NULL COMMENT '发布用户ID',
  `course_id`   BIGINT       NOT NULL COMMENT '课程分类ID',
  `title`       VARCHAR(100) NOT NULL COMMENT '问题标题',
  `content`     TEXT         NOT NULL COMMENT '问题内容',
  `image`       VARCHAR(255) DEFAULT NULL COMMENT '配图URL',
  `like_count`  INT          NOT NULL DEFAULT 0 COMMENT '点赞数',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '审核状态 0待审核 1已发布 2已屏蔽',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答疑帖子表';

-- ----------------------------
-- 4. 回复评论表
-- ----------------------------
CREATE TABLE `reply` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '回复ID',
  `post_id`     BIGINT   NOT NULL COMMENT '帖子ID',
  `user_id`     BIGINT   NOT NULL COMMENT '回复用户ID',
  `content`     TEXT     NOT NULL COMMENT '评论内容',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '回复时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回复评论表';

-- ----------------------------
-- 5. 点赞记录表 (用户ID+帖子ID 唯一, 防重复点赞)
-- ----------------------------
CREATE TABLE `post_like` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `user_id`     BIGINT   NOT NULL COMMENT '点赞用户ID',
  `post_id`     BIGINT   NOT NULL COMMENT '帖子ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_post` (`user_id`, `post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞记录表';

-- ----------------------------
-- 6. 收藏表 (用户ID+帖子ID 唯一, 防重复收藏)
-- ----------------------------
CREATE TABLE `favorite` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id`     BIGINT   NOT NULL COMMENT '收藏用户ID',
  `post_id`     BIGINT   NOT NULL COMMENT '帖子ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_post` (`user_id`, `post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- =====================================================
-- 预置数据
-- =====================================================

-- 用户 (密码 = MD5(明文+'campus'))
INSERT INTO `user` (`student_no`, `username`, `password`, `role`, `status`) VALUES
('admin',     '系统管理员', MD5(CONCAT('admin123','campus')), 1, 0),
('20230001',  '张同学',     MD5(CONCAT('123456','campus')),   0, 0),
('20230002',  '李学长',     MD5(CONCAT('123456','campus')),   0, 0),
('20230003',  '王同学',     MD5(CONCAT('123456','campus')),   0, 0);

-- 课程分类
INSERT INTO `course` (`name`, `description`) VALUES
('高等数学',   '极限、微积分、级数等大学数学基础课程答疑'),
('线性代数',   '行列式、矩阵、向量组、二次型等线性代数答疑'),
('大学英语',   '四六级备考、语法、阅读写作等英语学习答疑'),
('数据结构',   '链表、树、图、排序查找等数据结构课程答疑'),
('操作系统',   '进程线程、内存管理、文件系统等操作系统答疑'),
('计算机网络', 'TCP/IP、路由交换、应用层协议等网络答疑');

-- 帖子
INSERT INTO `post` (`user_id`, `course_id`, `title`, `content`, `like_count`, `status`) VALUES
(2, 1, '求极限 lim(x→0) sinx/x 的等价无穷小替换什么时候能用？', '题目里是加减法的时候用等价无穷小替换总是出错，有没有人能讲讲适用条件？', 3, 1),
(3, 4, '数据结构：平衡二叉树旋转到底怎么判断用LL还是LR？', '插入节点后失衡了，看教材的四张图还是分不清什么时候左旋什么时候右旋，求通俗讲解。', 5, 1),
(2, 3, '六级阅读总是做不完，有什么限时训练的方法吗？', '每次都剩两篇阅读没时间做，学长学姐们是怎么练速度的？', 1, 1),
(4, 5, '操作系统：进程和线程的区别面试怎么回答比较全面？', '光说资源分配和调度单位好像不够，求一个加分回答模板。', 2, 1),
(3, 2, '线性代数：为什么矩阵满秩就一定可逆？', '概念有点绕，希望能从行列式和线性方程组两个角度解释一下。', 0, 1),
(2, 6, 'TCP三次握手中最后一次握手能不能省掉？', '为什么必须是三次，两次会有什么问题？求举一个具体例子。', 1, 2);

-- 回复
INSERT INTO `reply` (`post_id`, `user_id`, `content`) VALUES
(1, 3, '记住一条：等价无穷小替换只能用在"乘除因子"里，加减法要用泰勒展开或者洛必达，加一项减一项凑因子也行。'),
(1, 4, '补充一下，加减法里如果替换后极限存在且不为0也可以用，但新手建议统一用泰勒，不容易错。'),
(2, 2, '看失衡节点的左右孩子哪边高：左边高就是L开头，右边高就是R开头；再看孙子节点偏向哪边，同侧单旋，异侧双旋。'),
(3, 4, '每天固定两篇限时15分钟，先看题干再回原文定位，坚持两周就能看到效果。'),
(4, 2, '可以从三个维度答：资源分配单位、调度执行单位、通信方式开销，最后补一句并发与并行的区别就非常完整了。'),
(5, 4, '从行列式角度：满秩则|A|≠0，伴随矩阵法可逆；从方程组角度：满秩说明列向量线性无关，Ax=0只有零解，故可逆。');

-- 点赞记录
INSERT INTO `post_like` (`user_id`, `post_id`) VALUES
(3, 1), (4, 1), (2, 2), (4, 2), (3, 2), (4, 3), (2, 4), (3, 4), (4, 4);

-- 收藏记录
INSERT INTO `favorite` (`user_id`, `post_id`) VALUES
(2, 2), (2, 4), (3, 1), (4, 2);
