/*
Navicat MySQL Data Transfer

Source Server         : wampmysql
Source Server Version : 50617
Source Host           : localhost:3306
Source Database       : qqchat

Target Server Type    : MYSQL
Target Server Version : 50617
File Encoding         : 65001

Date: 2016-03-25 15:40:29
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for images
-- ----------------------------
DROP TABLE IF EXISTS `images`;
CREATE TABLE `images` (
  `id` int(11) NOT NULL,
  `image` mediumblob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for privatechat
-- ----------------------------
DROP TABLE IF EXISTS `privatechat`;
CREATE TABLE `privatechat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user1_id` int(11) DEFAULT NULL,
  `user2_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for privatechatlog
-- ----------------------------
DROP TABLE IF EXISTS `privatechatlog`;
CREATE TABLE `privatechatlog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pid` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for roomchat
-- ----------------------------
DROP TABLE IF EXISTS `roomchat`;
CREATE TABLE `roomchat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `roomname` varchar(255) DEFAULT NULL,
  `creator_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for roommenber
-- ----------------------------
DROP TABLE IF EXISTS `roommenber`;
CREATE TABLE `roommenber` (
  `room_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`room_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `sex` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
