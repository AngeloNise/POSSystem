-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 21, 2025 at 02:23 AM
-- Server version: 10.4.24-MariaDB
-- PHP Version: 7.4.29

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `microdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `account`
--

CREATE TABLE `account` (
  `ACCOUNT_ID` int(11) NOT NULL,
  `ACCOUNT_NAME` varchar(50) DEFAULT NULL COMMENT 'Username  of the user',
  `PASSWORD_` varchar(240) DEFAULT NULL,
  `FULL_NAME` varchar(100) DEFAULT NULL COMMENT 'Full name of user ',
  `SEX` varchar(2) DEFAULT '1' COMMENT '0- Female, 1- Male',
  `HOME_ADDRESS` varchar(500) DEFAULT '0' COMMENT 'Complete home address of user',
  `MOBILE_PHONE_A` varchar(20) DEFAULT NULL COMMENT 'Mobile number o user',
  `EMAIL1` varchar(60) DEFAULT NULL COMMENT 'Email address of user',
  `STATE` varchar(2) DEFAULT NULL,
  `CREATE_DATE` datetime DEFAULT NULL COMMENT 'Created date of account',
  `ORGANIZATION_ID` int(11) DEFAULT NULL COMMENT 'FK ID of Organization which user is belonged to',
  `ORG_TYPE` int(11) DEFAULT NULL,
  `COMPANY_ID` varchar(45) DEFAULT NULL,
  `REGION_ID` int(11) DEFAULT NULL,
  `AREA_ID` int(11) DEFAULT NULL COMMENT 'Test org id column for organization_ledger_new03',
  `ROLE_ID` int(11) DEFAULT NULL,
  `FIBERHOMEID` varchar(60) DEFAULT NULL COMMENT 'Fiberhome uid given by employee',
  `STATUS_` varchar(2) DEFAULT NULL,
  `NATIONALITY` varchar(2) DEFAULT NULL,
  `SOURCE_MENU` varchar(45) DEFAULT 'ROLE',
  `TYPE` varchar(45) DEFAULT 'USER',
  `SIGNATURE` varchar(100) DEFAULT NULL,
  `POSITION` varchar(45) DEFAULT NULL,
  `SYSTEM_GENERATED` varchar(2) DEFAULT '0',
  `UPDATED_BY` int(11) DEFAULT NULL,
  `CREATED_BY` int(11) DEFAULT NULL,
  `LAST_UPDATED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Main Table for Users Account Details or User Access' ROW_FORMAT=DYNAMIC;

--
-- Dumping data for table `account`
--

INSERT INTO `account` (`ACCOUNT_ID`, `ACCOUNT_NAME`, `PASSWORD_`, `FULL_NAME`, `SEX`, `HOME_ADDRESS`, `MOBILE_PHONE_A`, `EMAIL1`, `STATE`, `CREATE_DATE`, `ORGANIZATION_ID`, `ORG_TYPE`, `COMPANY_ID`, `REGION_ID`, `AREA_ID`, `ROLE_ID`, `FIBERHOMEID`, `STATUS_`, `NATIONALITY`, `SOURCE_MENU`, `TYPE`, `SIGNATURE`, `POSITION`, `SYSTEM_GENERATED`, `UPDATED_BY`, `CREATED_BY`, `LAST_UPDATED`) VALUES
(1, 'admin', '$2a$12$TtQhSaOoty3PRMFJAxrBduVTE1vU4nPVYvklXjrob76E5VMctFsIa', 'DEVELOPER', '1', 'ADRESS 123', '09270001234', 'FIBERHOME@EMAIL.COM', '1', '2022-07-20 08:31:41', 1, 1, '1', NULL, 0, 1, 'FH 123', '1', '2', 'ROLE', 'USER', NULL, NULL, '0', NULL, NULL, '2025-02-21 01:20:29');

-- --------------------------------------------------------

--
-- Table structure for table `activity_log`
--

CREATE TABLE `activity_log` (
  `id` int(11) NOT NULL,
  `PAGE_NAME` varchar(45) DEFAULT NULL,
  `ACTION` varchar(150) DEFAULT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT current_timestamp(),
  `CREATED_BY_ID` varchar(45) DEFAULT NULL,
  `CREATED_BY_NAME` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `activity_log`
--

INSERT INTO `activity_log` (`id`, `PAGE_NAME`, `ACTION`, `CREATED_DATE`, `CREATED_BY_ID`, `CREATED_BY_NAME`) VALUES
(1, 'Welcome Page', 'Change Password', '2025-02-21 01:20:29', '1', 'DEVELOPER'),
(2, 'Authorization Service', 'Login', '2025-02-21 01:20:35', '1', 'DEVELOPER');

-- --------------------------------------------------------

--
-- Table structure for table `announcements`
--

CREATE TABLE `announcements` (
  `AnnouncementId` int(11) NOT NULL,
  `PublishedDate` datetime NOT NULL,
  `Content` text NOT NULL,
  `PostedBy` int(11) DEFAULT NULL,
  `PostedByName` varchar(45) DEFAULT NULL,
  `Thread` tinyint(4) DEFAULT 1,
  `Subject` varchar(45) NOT NULL,
  `Delivered` tinyint(4) DEFAULT 0,
  `Read` tinyint(4) DEFAULT 0,
  `PostedTo` int(11) DEFAULT NULL,
  `Type` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `id` int(11) NOT NULL,
  `code` varchar(45) DEFAULT NULL COMMENT '1 - Category , 2 - TE Name, 3 - Brand, 4 - TE Status, 5 - OP, 6 - Item Codes',
  `parent_id` int(11) DEFAULT NULL,
  `instance_id` int(11) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `remarks` varchar(45) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(45) DEFAULT NULL,
  `created_id` varchar(45) DEFAULT NULL,
  `updated_date` timestamp NULL DEFAULT NULL,
  `updated_by` varchar(45) DEFAULT NULL,
  `updated_id` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `organization_ledger`
--

CREATE TABLE `organization_ledger` (
  `id_` int(11) NOT NULL,
  `name_` varchar(45) DEFAULT NULL,
  `parent_id_` int(11) DEFAULT NULL,
  `type_` varchar(45) DEFAULT NULL,
  `path_` varchar(100) DEFAULT NULL,
  `create_time_` datetime DEFAULT current_timestamp(),
  `status_` varchar(5) DEFAULT '1',
  `cost_center_` varchar(45) DEFAULT NULL,
  `wbs_` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `organization_ledger`
--

INSERT INTO `organization_ledger` (`id_`, `name_`, `parent_id_`, `type_`, `path_`, `create_time_`, `status_`, `cost_center_`, `wbs_`) VALUES
(1, 'FIBERHOME', 0, '1', '1', '2023-09-07 14:21:08', '1', NULL, NULL),
(2, 'GMM', 1, '2', '1.2', '2023-09-07 14:21:35', '1', NULL, NULL),
(10, 'MAKATI', 2, '3', '1.2.10', '2023-09-07 14:47:06', '1', NULL, NULL),
(11, 'MANDALUYONG', 2, '3', '1.2.11', '2023-09-07 14:47:06', '1', NULL, NULL),
(12, 'NORTH MANILA', 2, '3', '1.2.12', '2023-09-07 14:47:06', '1', NULL, NULL),
(13, 'SOUTH MANILA', 2, '3', '1.2.13', '2023-09-07 14:47:06', '1', NULL, NULL),
(14, 'LAS PINAS', 2, '3', '1.2.14', '2023-09-07 14:47:07', '1', NULL, NULL),
(15, 'PARANAQUE', 2, '3', '1.2.15', '2023-09-07 14:47:07', '1', NULL, NULL),
(16, 'MARIKINA', 2, '3', '1.2.16', '2023-09-07 14:47:07', '1', NULL, NULL),
(17, 'CAMANAVA', 2, '3', '1.2.17', '2023-09-07 14:47:07', '1', NULL, NULL),
(18, 'CTAABMT', 2, '3', '1.2.18', '2023-09-07 14:47:07', '1', NULL, NULL),
(19, 'NORTH QUEZON CITY', 2, '3', '1.2.19', '2023-09-07 14:47:07', '1', NULL, NULL),
(20, 'SOUTH QUEZON CITY', 2, '3', '1.2.20', '2023-09-07 14:47:08', '1', NULL, NULL),
(21, 'PPT', 2, '3', '1.2.21', '2023-09-07 14:47:08', '1', NULL, NULL),
(25, 'BGC', 2, '3', '1.2.25', '2023-09-07 14:50:16', '1', NULL, NULL),
(27, 'MAKATI/MANDALUYONG', 2, '3', '1.2.27', '2023-09-07 14:50:16', '1', NULL, NULL),
(32, 'Project One', 3, '4', '1.2.3.32', '2023-09-07 15:06:50', '1', 'FP77002', NULL),
(35, 'TED', 10, '4', '1.2.10.35', '2023-09-07 15:10:43', '1', 'FP08506', NULL),
(36, 'Fibergen', 11, '4', '1.2.11.36', '2023-09-07 15:14:38', '1', 'FP77002', NULL),
(37, 'Project One', 12, '4', '1.2.12.37', '2023-09-07 15:15:28', '1', 'FP77002', NULL),
(38, 'Project One', 13, '4', '1.2.13.38', '2023-09-07 15:19:02', '1', 'FP77002', NULL),
(39, 'Project One', 14, '4', '1.2.14.39', '2023-09-07 15:19:02', '1', 'FP77002', NULL),
(40, 'Project One', 15, '4', '1.2.15.40', '2023-09-07 15:19:03', '1', 'FP77002', NULL),
(41, 'Project One', 16, '4', '1.2.16.41', '2023-09-07 15:19:03', '1', 'FP77002', NULL),
(42, 'Project One', 17, '4', '1.2.17.42', '2023-09-07 15:19:03', '1', 'FP77002', NULL),
(43, 'Project One', 18, '4', '1.2.18.43', '2023-09-07 15:19:03', '1', 'FP77002', NULL),
(44, 'Project One', 19, '4', '1.2.19.44', '2023-09-07 15:19:03', '1', 'FP77002', NULL),
(45, 'Project One', 20, '4', '1.2.20.45', '2023-09-07 15:19:03', '1', 'FP77002', NULL),
(46, 'Project One', 21, '4', '1.2.21.46', '2023-09-07 15:19:03', '1', 'FP77002', NULL),
(47, 'Fibergen', 22, '4', '1.2.22.47', '2023-09-07 15:19:04', '1', 'FP77016', NULL),
(48, 'Fibergen', 23, '4', '1.2.23.48', '2023-09-07 15:19:04', '1', 'FP77016', NULL),
(49, 'Globe', 24, '4', '1.2.24.49', '2023-09-07 15:19:04', '1', 'FP77017', NULL),
(50, 'Globe', 25, '4', '1.2.25.50', '2023-09-07 15:19:04', '1', 'FP77017', NULL),
(51, 'PTO', 25, '4', '1.2.25.51', '2023-09-07 15:19:04', '1', 'FP77015', NULL),
(52, 'Project One', 27, '4', '1.2.27.52', '2023-09-07 15:19:04', '1', 'FP77015', NULL),
(53, 'PTO', 24, '4', '1.2.24.53', '2023-09-07 15:19:04', '1', 'FP77015', NULL),
(54, 'SKYCON / M&E', 25, '4', '1.2.25.54', '2023-09-07 15:19:05', '1', 'FP77014', NULL),
(56, 'G&E', 25, '4', '1.2.25.56', '2023-09-07 15:19:05', '1', 'FP77029', NULL),
(57, 'North Luzon', 1, '2', '1.57', '2023-09-07 16:06:15', '1', NULL, NULL),
(58, 'PAMPANGA', 57, '3', '1.57.58', '2023-09-07 16:08:18', '1', NULL, NULL),
(59, 'DAGUPAN', 57, '3', '1.57.59', '2023-09-07 16:08:18', '1', NULL, NULL),
(60, 'BAGUIO', 57, '3', '1.57.60', '2023-09-07 16:08:18', '1', NULL, NULL),
(61, 'BULACAN', 57, '3', '1.57.61', '2023-09-07 16:08:18', '1', NULL, NULL),
(62, 'CAGAYAN VALLEY', 57, '3', '1.57.62', '2023-09-07 16:08:18', '1', NULL, NULL),
(63, 'PANGASINAN', 57, '3', '1.57.63', '2023-09-07 16:08:18', '1', NULL, NULL),
(64, 'TARLAC-NUEVA ECIJA', 57, '3', '1.57.64', '2023-09-07 16:08:19', '1', NULL, NULL),
(65, 'OLONGAPO', 57, '3', '1.57.65', '2023-09-07 16:08:19', '1', NULL, NULL),
(66, 'TUGUEGARAO', 57, '3', '1.57.66', '2023-09-07 16:08:19', '1', NULL, NULL),
(67, 'LA UNION INHOUSE', 57, '3', '1.57.67', '2023-09-07 16:08:19', '1', NULL, NULL),
(68, 'Fibergen', 58, '4', '1.57.58.68', '2023-09-07 16:14:29', '1', 'FP77016', NULL),
(69, 'Globe', 59, '4', '1.57.59.69', '2023-09-07 16:15:52', '1', 'FP77017', NULL),
(70, 'Project One', 60, '4', '1.57.60.70', '2023-09-07 16:15:52', '1', 'FP77002', NULL),
(71, 'Project One', 61, '4', '1.57.61.71', '2023-09-07 16:15:52', '1', 'FP77002', NULL),
(72, 'Project One', 62, '4', '1.57.62.72', '2023-09-07 16:15:52', '1', 'FP77002', NULL),
(73, 'Project One', 82, '4', '1.57.82.73', '2023-09-07 16:15:52', '1', 'FP77002', NULL),
(74, 'Project One', 58, '4', '1.57.58.74', '2023-09-07 16:15:52', '1', 'FP77002', NULL),
(75, 'Project One', 63, '4', '1.57.63.75', '2023-09-07 16:15:53', '1', 'FP77002', NULL),
(76, 'Project One', 64, '4', '1.57.64.76', '2023-09-07 16:15:53', '1', 'FP77002', NULL),
(77, 'PTO', 59, '4', '1.57.59.77', '2023-09-07 16:15:53', '1', 'FP77002', NULL),
(78, 'PTO', 82, '4', '1.57.82.78', '2023-09-07 16:15:53', '1', 'FP77015', NULL),
(79, 'PTO', 65, '4', '1.57.65.79', '2023-09-07 16:15:53', '1', 'FP77015', NULL),
(80, 'PTO', 66, '4', '1.57.66.80', '2023-09-07 16:15:53', '1', 'FP77015', NULL),
(81, 'PTO', 67, '4', '1.57.67.81', '2023-09-07 16:15:53', '1', 'FP77015', NULL),
(82, 'ILOCOS', 55, '3', '1.57.82', '2023-09-07 16:20:01', '1', '', NULL),
(83, 'South Luzon', 1, '2', '1.83', '2023-09-07 16:30:50', '1', NULL, NULL),
(84, 'CAVITE', 83, '3', '1.83.84', '2023-09-07 16:32:32', '1', NULL, NULL),
(85, 'BATANGAS', 83, '3', '1.83.85', '2023-09-07 16:32:32', '1', NULL, NULL),
(86, 'BICOL', 83, '3', '1.83.86', '2023-09-07 16:32:32', '1', NULL, NULL),
(87, 'LAGUNA', 83, '3', '1.83.87', '2023-09-07 16:32:32', '1', NULL, NULL),
(88, 'QUEZON PROVINCE', 83, '3', '1.83.88', '2023-09-07 16:32:32', '1', NULL, NULL),
(89, 'Fibergen Design', 84, '4', '1.83.84.89', '2023-09-07 16:38:03', '1', 'FP77016', NULL),
(90, 'Project One OSPM', 85, '4', '1.83.85.90', '2023-09-07 16:38:03', '1', 'FP77017', NULL),
(91, 'Globe Homerun SLI/SLR', 86, '4', '1.83.86.91', '2023-09-07 16:38:04', '1', 'FP77017', NULL),
(92, 'Globe Homerun OSPM', 84, '4', '1.83.84.92', '2023-09-07 16:38:04', '1', 'FP77017', NULL),
(93, 'Project One OSPM', 87, '4', '1.83.87.93', '2023-09-07 16:38:04', '1', 'FP77017', NULL),
(94, 'Project One OSPM', 88, '4', '1.83.88.94', '2023-09-07 16:38:04', '1', 'FP77017', NULL),
(95, 'Project One', 85, '4', '1.83.85.95', '2023-09-07 16:38:04', '1', 'FP77002', NULL),
(96, 'Project One', 86, '4', '1.83.86.96', '2023-09-07 16:38:04', '1', 'FP77002', NULL),
(97, 'Project One', 84, '4', '1.83.84.97', '2023-09-07 16:38:04', '1', 'FP77002', NULL),
(98, 'Project One', 87, '4', '1.83.87.98', '2023-09-07 16:38:04', '1', 'FP77002', NULL),
(99, 'Project One', 88, '4', '1.83.88.99', '2023-09-07 16:38:05', '1', 'FP77002', NULL),
(100, 'Globe Homerun OSPM', 85, '4', '1.83.85.100', '2023-09-07 16:38:05', '1', 'FP77015', NULL),
(101, 'Globe FTTH/FBTS', 84, '4', '1.83.84.101', '2023-09-07 16:38:05', '1', 'FP77015', NULL),
(102, 'Visayas', 1, '2', '1.102', '2023-09-07 16:48:03', '1', NULL, NULL),
(103, 'CEBU', 102, '3', '1.102.103', '2023-09-07 16:48:34', '1', NULL, NULL),
(104, 'BOHOL', 102, '3', '1.102.104', '2023-09-07 16:49:33', '1', NULL, NULL),
(105, 'NORTH CEBU', 102, '3', '1.102.105', '2023-09-07 16:49:33', '1', NULL, NULL),
(106, 'MACTAN', 102, '3', '1.102.106', '2023-09-07 16:49:33', '1', NULL, NULL),
(107, 'MANDAUE', 102, '3', '1.102.107', '2023-09-07 16:49:34', '1', NULL, NULL),
(108, 'TALISAY', 102, '3', '1.102.108', '2023-09-07 16:49:34', '1', NULL, NULL),
(109, 'SOUTH CEBU', 102, '3', '1.102.109', '2023-09-07 16:49:34', '1', NULL, NULL),
(110, 'NEGROS', 102, '3', '1.102.110', '2023-09-07 16:49:34', '1', NULL, NULL),
(112, 'PANAY', 102, '3', '1.102.112', '2023-09-07 16:49:34', '1', NULL, NULL),
(114, 'Fibergen Design', 103, '4', '1.102.103.114', '2023-09-07 16:52:19', '1', '', NULL),
(115, 'Fibergen FTK', 103, '4', '1.102.103.115', '2023-09-07 16:53:00', '1', 'FP77017', NULL),
(116, 'Project One', 104, '4', '1.102.104.116', '2023-09-07 16:53:00', '1', 'FP77015', NULL),
(117, 'Project One', 105, '4', '1.102.105.117', '2023-09-07 16:53:00', '1', 'FP77015', NULL),
(118, 'Project One', 106, '4', '1.102.106.118', '2023-09-07 16:53:01', '1', 'FP77015', NULL),
(119, 'Project One', 107, '4', '1.102.107.119', '2023-09-07 16:53:01', '1', 'FP77015', NULL),
(120, 'Project One', 108, '4', '1.102.108.120', '2023-09-07 16:53:01', '1', 'FP77015', NULL),
(121, 'Project One', 109, '4', '1.102.109.121', '2023-09-07 16:53:01', '1', 'FP77015', NULL),
(122, 'Fibergen Design', 110, '4', '1.102.110.122', '2023-09-07 16:53:01', '1', 'FP77015', NULL),
(123, 'Project One', 111, '4', '1.102.111.123', '2023-09-07 16:53:01', '1', 'FP77015', NULL),
(124, 'Project One', 112, '4', '1.102.112.124', '2023-09-07 16:53:01', '1', 'FP77015', NULL),
(125, 'Project One', 113, '4', '1.102.113.125', '2023-09-07 16:53:01', '1', 'FP77015', NULL),
(126, 'Mindanao', 1, '2', '1.126', '2023-09-07 16:58:41', '1', NULL, NULL),
(127, 'DAVAO', 126, '3', '1.126.127', '2023-09-07 16:59:37', '1', NULL, NULL),
(128, 'GENSAN', 126, '3', '1.126.128', '2023-09-07 16:59:38', '1', NULL, NULL),
(129, 'ZAMBOANGA', 126, '3', '1.126.129', '2023-09-07 16:59:38', '1', NULL, NULL),
(130, 'CDO', 126, '3', '1.126.130', '2023-09-07 16:59:38', '1', NULL, NULL),
(131, 'DIGOS', 126, '3', '1.126.131', '2023-09-07 16:59:38', '1', NULL, NULL),
(132, 'CARAGA', 126, '3', '1.126.132', '2023-09-07 16:59:38', '1', NULL, NULL),
(133, 'TAGUM', 126, '3', '1.126.133', '2023-09-07 16:59:38', '1', NULL, NULL),
(134, 'MATI', 126, '3', '1.126.134', '2023-09-07 16:59:38', '1', NULL, NULL),
(135, 'ILIGAN', 126, '3', '1.126.135', '2023-09-07 16:59:39', '1', NULL, NULL),
(136, 'BUTUAN', 126, '3', '1.126.136', '2023-09-07 16:59:39', '1', NULL, NULL),
(137, 'DAIWEI', 126, '3', '1.126.137', '2023-09-07 16:59:39', '1', NULL, NULL),
(138, 'Fibergen', 127, '4', '1.126.127.138', '2023-09-07 17:02:03', '1', 'FP77016', NULL),
(139, 'Globe Homerun', 127, '4', '1.126.127.139', '2023-09-07 17:02:03', '1', 'FP77017', NULL),
(140, 'Project One', 127, '4', '1.126.127.140', '2023-09-07 17:02:03', '1', 'FP77015', NULL),
(141, 'Project One', 128, '4', '1.126.128.141', '2023-09-07 17:02:03', '1', 'FP77015', NULL),
(142, 'Project One', 129, '4', '1.126.129.142', '2023-09-07 17:02:03', '1', 'FP77015', NULL),
(143, 'Project One', 130, '4', '1.126.130.143', '2023-09-07 17:02:03', '1', 'FP77015', NULL),
(144, 'Project One', 131, '4', '1.126.131.144', '2023-09-07 17:02:04', '1', 'FP77015', NULL),
(145, 'Project One', 132, '4', '1.126.132.145', '2023-09-07 17:02:04', '1', 'FP77015', NULL),
(146, 'Project One', 133, '4', '1.126.133.146', '2023-09-07 17:02:04', '1', 'FP77015', NULL),
(147, 'Project One', 134, '4', '1.126.134.147', '2023-09-07 17:02:04', '1', 'FP77015', NULL),
(148, 'Project One', 135, '4', '1.126.135.148', '2023-09-07 17:02:04', '1', 'FP77015', NULL),
(149, 'PTO', 136, '4', '1.126.136.149', '2023-09-07 17:02:04', '1', 'FP77015', NULL),
(150, 'PTO', 127, '4', '1.126.127.150', '2023-09-07 17:02:04', '1', 'FP77015', NULL),
(151, 'PTO', 137, '4', '1.126.137.151', '2023-09-07 17:02:05', '1', 'FP77015', NULL),
(152, 'REP Office', 1, '2', '1.152', '2023-09-25 15:54:49', '1', '', ''),
(153, 'PMO', 152, '3', '1.152.153', '2023-09-25 15:57:12', '1', '', ''),
(154, 'PM - Project Management', 153, '4', '1.152.153.154', '2023-09-25 15:58:36', '1', '', ''),
(155, 'Globe', 153, '4', '1.152.153.155', '2023-09-28 16:45:35', '1', '', ''),
(156, 'SKYCON / M&E', 153, '4', '1.152.153.156', '2023-09-28 16:45:51', '1', '', ''),
(157, 'PTO', 153, '4', '1.152.153.157', '2023-09-28 16:47:47', '1', '', ''),
(158, 'Project One', 153, '4', '1.152.153.158', '2023-09-28 16:48:20', '1', '', ''),
(159, 'Fibergen', 153, '4', '1.152.153.159', '2023-09-28 16:49:43', '1', '', ''),
(160, 'Project One - OSPM', 84, '4', '1.83.84.160', '2023-09-29 10:29:12', '1', '', ''),
(161, 'Globe FTTH/FBTS', 85, '4', '1.83.85.161', '2023-09-29 10:34:38', '1', '', ''),
(162, 'Fibergen FTTH', 84, '4', '1.83.84.162', '2023-09-29 10:43:35', '1', '', ''),
(163, 'Fibergen FTBTS', 84, '4', '1.83.84.163', '2023-09-29 10:43:54', '1', '', ''),
(164, 'Globe Homerun OSPM', 86, '4', '1.83.86.164', '2023-09-29 10:45:44', '1', '', ''),
(165, 'Project One OSPM', 86, '4', '1.86.165', '2023-09-29 11:19:59', '1', '', ''),
(166, 'Globe FTTH/FBTS', 86, '4', '1.83.86.166', '2023-09-29 11:25:17', '1', '', ''),
(168, 'Globe Homerun OSPM', 87, '4', '1.83.87.168', '2023-09-29 11:26:30', '1', '', ''),
(169, 'Globe FTTH/FBTS', 87, '4', '1.83.87.169', '2023-09-29 11:36:35', '1', '', ''),
(170, 'Globe Homerun OSPM', 88, '4', '1.83.88.170', '2023-09-29 11:39:43', '1', '', ''),
(171, 'Globe FTTH/FBTS', 88, '4', '1.83.88.171', '2023-09-29 11:40:01', '1', '', ''),
(172, 'ILOCOS', 57, '3', '1.57.172', '2023-09-29 11:53:29', '1', '', ''),
(173, 'Project One', 172, '4', '1.57.172.173', '2023-09-29 11:54:08', '1', '', ''),
(174, 'PTO', 172, '4', '1.57.172.174', '2023-09-29 11:54:29', '1', '', ''),
(178, 'Globe Homerun', 103, '4', '1.102.103.178', '2023-09-29 13:06:42', '1', '', ''),
(179, 'Globe OSPM', 103, '4', '1.102.103.179', '2023-09-29 13:07:15', '1', '', ''),
(180, 'Project One', 24, '4', '1.102.24.180', '2023-09-29 13:25:17', '1', '', ''),
(181, 'Fibergen FTK', 110, '4', '1.102.110.181', '2023-09-29 13:30:03', '1', '', ''),
(182, 'Globe Homerun', 110, '4', '1.102.110.182', '2023-09-29 13:30:55', '1', '', ''),
(183, 'Globe OSPM', 110, '4', '1.102.110.183', '2023-09-29 13:31:21', '1', '', ''),
(184, 'Project One', 110, '4', '1.102.110.184', '2023-09-29 13:31:37', '1', '', ''),
(185, 'Fibergen FTK', 112, '4', '1.102.112.185', '2023-09-29 13:33:38', '1', '', ''),
(186, 'BSL', 102, '3', '1.102.186', '2023-09-29 13:33:51', '1', '', ''),
(187, 'Fibergen Design', 112, '4', '1.102.112.187', '2023-09-29 13:33:55', '1', '', ''),
(188, 'Globe Homerun', 112, '4', '1.102.112.188', '2023-09-29 13:34:16', '1', '', ''),
(189, 'Globe OSPM', 112, '4', '1.102.112.189', '2023-09-29 13:34:36', '1', '', ''),
(190, 'Fibergen FTK', 186, '4', '1.102.186.190', '2023-09-29 13:35:08', '1', '', ''),
(191, 'SAMAR/LEYTE', 102, '3', '1.102.191', '2023-09-29 13:37:18', '1', '', ''),
(192, 'Fibergen Design', 186, '4', '1.102.186.192', '2023-09-29 13:37:40', '1', '', ''),
(193, 'Project One', 191, '4', '1.102.191.193', '2023-09-29 13:37:48', '1', '', ''),
(194, 'Globe OSPM', 186, '4', '1.102.186.194', '2023-09-29 13:38:28', '1', '', ''),
(195, 'JONES', 102, '3', '1.102.195', '2023-09-29 13:44:11', '1', '', ''),
(196, 'Globe', 195, '4', '1.102.195.196', '2023-09-29 13:45:18', '1', 'FP77017', ''),
(198, 'PTO', 195, '4', '1.102.195.198', '2023-09-29 13:47:33', '1', 'FP77015', ''),
(200, 'Project One', 195, '4', '1.102.195.200', '2023-09-29 13:49:24', '1', '', ''),
(207, 'Globe - IBS', 11, '4', '1.2.11.207', '2023-09-29 16:21:19', '1', '', ''),
(208, 'Globe - FBTS', 11, '4', '1.2.11.208', '2023-09-29 16:21:40', '1', '', ''),
(209, 'Globe - FBTS', 11, '4', '1.2.11.209', '2023-09-29 16:22:01', '1', '', ''),
(210, 'Globe - VMDU', 11, '4', '1.2.11.210', '2023-09-29 16:22:21', '1', '', ''),
(211, 'Globe - FTTH ODN', 11, '4', '1.2.11.211', '2023-09-29 16:22:45', '1', '', ''),
(212, 'Globe - OLT', 11, '4', '1.2.11.212', '2023-09-29 16:23:19', '1', '', ''),
(213, 'Globe - OSPM', 11, '4', '1.2.11.213', '2023-09-29 16:23:53', '1', '', ''),
(214, 'PTO - IBS', 11, '4', '1.2.11.214', '2023-09-29 16:24:29', '1', '', ''),
(215, 'PTO - OSP', 11, '4', '1.2.11.215', '2023-09-29 16:25:05', '1', '', ''),
(216, 'Fibergen', 84, '4', '1.83.84.216', '2023-10-10 16:57:10', '1', 'FP77016', ''),
(217, 'Fibergen-Inhouse', 85, '4', '1.83.85.217', '2023-10-10 17:00:02', '1', '', ''),
(218, 'Fibergen-Inhouse', 86, '4', '1.83.86.218', '2023-10-10 17:00:40', '1', '', ''),
(219, 'Fibergen-Inhouse', 84, '4', '1.83.84.219', '2023-10-10 17:00:49', '1', '', ''),
(220, 'Fibergen-Inhouse', 87, '4', '1.83.87.220', '2023-10-10 17:01:04', '1', '', ''),
(221, 'Fibergen-Inhouse', 88, '4', '1.83.88.221', '2023-10-10 17:01:12', '1', '', ''),
(222, 'G&E', 84, '4', '1.83.84.222', '2023-10-10 17:06:28', '1', '', ''),
(223, 'IPG', 85, '4', '1.83.85.223', '2023-10-10 17:09:39', '1', '', ''),
(224, 'IPG', 84, '4', '1.83.84.224', '2023-10-10 17:10:00', '1', '', ''),
(225, 'IPG', 87, '4', '1.83.87.225', '2023-10-10 17:10:10', '1', '', ''),
(226, 'IPG', 88, '4', '1.83.88.226', '2023-10-10 17:10:22', '1', '', ''),
(227, 'PTO', 84, '4', '1.83.84.227', '2023-10-10 17:15:02', '1', '', ''),
(228, 'Rep Office', 85, '4', '1.83.85.228', '2023-10-10 17:15:49', '1', '', ''),
(229, 'SLZ', 83, '3', '1.83.229', '2023-12-04 17:05:16', '1', '', ''),
(230, 'Fibergen', 229, '4', '1.83.229.230', '2023-12-04 17:06:13', '1', '', ''),
(231, 'Fibergen-Inhouse', 229, '4', '1.83.229.231', '2023-12-04 17:14:03', '1', '', ''),
(232, 'G&E', 229, '4', '1.83.229.232', '2023-12-04 17:15:47', '1', '', ''),
(233, 'Globe Homerun', 229, '4', '1.83.229.233', '2023-12-04 17:27:05', '1', '', ''),
(234, 'Globe-Inhouse', 84, '4', '1.83.84.234', '2023-12-04 17:29:12', '1', '', ''),
(235, 'IPG', 229, '4', '1.83.229.235', '2023-12-04 17:39:13', '1', '', ''),
(236, 'PTO', 229, '4', '1.83.229.236', '2023-12-04 17:52:28', '1', '', ''),
(237, 'Rep Office', 84, '4', '1.83.84.237', '2023-12-04 17:54:47', '1', '', ''),
(238, 'NLZ', 57, '3', '1.57.238', '2023-12-04 17:57:11', '1', '', ''),
(239, 'Fibergen', 238, '4', '1.57.238.239', '2023-12-04 17:57:31', '1', '', ''),
(240, 'GMM', 2, '3', '1.2.240', '2023-12-05 09:17:00', '1', '', ''),
(241, 'Fibergen', 240, '4', '1.2.240.241', '2023-12-05 09:17:28', '1', '', ''),
(242, 'MARINDUQUE', 83, '3', '1.83.242', '2023-12-05 09:51:26', '1', '', ''),
(243, 'MINDORO', 83, '3', '1.83.243', '2023-12-05 09:52:12', '1', '', ''),
(244, 'ROMBLON', 83, '3', '1.83.244', '2023-12-05 09:52:40', '1', '', ''),
(245, 'Fibergen-Inhouse', 242, '4', '1.83.242.245', '2023-12-05 09:56:04', '1', '', ''),
(246, 'Fibergen-Inhouse', 243, '4', '1.83.243.246', '2023-12-05 09:57:24', '1', '', ''),
(247, 'Fibergen-Inhouse', 244, '4', '1.83.244.247', '2023-12-05 09:57:39', '1', '', ''),
(248, 'IPG', 242, '4', '1.83.242.248', '2023-12-05 10:14:01', '1', '', ''),
(249, 'Globe Homerun', 60, '4', '1.57.60.249', '2023-12-05 10:17:21', '1', '', ''),
(250, 'Globe Homerun', 63, '4', '1.57.63.250', '2023-12-05 10:32:07', '1', '', ''),
(251, 'Globe Homerun', 64, '4', '1.57.64.251', '2023-12-05 10:39:52', '1', '', ''),
(252, 'Globe Homerun', 11, '4', '1.2.11.252', '2023-12-05 10:58:07', '1', '', ''),
(253, 'Project One', 10, '4', '1.2.10.253', '2023-12-05 11:05:32', '1', '', ''),
(254, 'Project One', 11, '4', '1.2.11.254', '2023-12-05 11:06:40', '1', '', ''),
(255, 'QUEZON CITY', 2, '3', '1.2.255', '2023-12-05 13:26:39', '1', '', ''),
(256, 'Project One', 255, '4', '1.2.255.256', '2023-12-05 13:26:58', '1', '', ''),
(257, 'PTO', 11, '4', '1.2.11.257', '2023-12-05 13:33:12', '1', '', ''),
(258, 'Fibergen', 103, '4', '1.102.103.258', '2023-12-05 13:37:30', '1', '', ''),
(259, 'Fibergen', 105, '4', '1.102.105.259', '2023-12-05 13:38:15', '1', '', ''),
(260, 'Fibergen-Inhouse', 104, '4', '1.102.104.260', '2023-12-05 13:41:01', '1', '', ''),
(261, 'Fibergen-Inhouse', 103, '4', '1.102.103.261', '2023-12-05 13:41:50', '1', '', ''),
(262, 'NAGA', 102, '3', '1.102.262', '2023-12-05 14:10:49', '1', '', ''),
(263, 'Fibergen-Inhouse', 262, '4', '1.102.262.263', '2023-12-05 14:11:27', '1', '', ''),
(264, 'Fibergen-Inhouse', 110, '4', '1.102.110.264', '2023-12-05 14:12:06', '1', '', ''),
(265, 'Fibergen-Inhouse', 105, '4', '1.102.105.265', '2023-12-05 14:13:53', '1', '', ''),
(266, 'Fibergen-Inhouse', 112, '4', '1.102.112.266', '2023-12-05 14:15:01', '1', '', ''),
(267, 'Fibergen-Inhouse', 191, '4', '1.102.191.267', '2023-12-05 14:16:11', '1', '', ''),
(268, 'G&E', 110, '4', '1.102.110.268', '2023-12-05 14:23:14', '1', '', ''),
(269, 'G&E', 105, '4', '1.102.105.269', '2023-12-05 14:23:58', '1', '', ''),
(270, 'G&E', 112, '4', '1.102.112.270', '2023-12-05 14:24:48', '1', '', ''),
(271, 'G&E', 191, '4', '1.102.191.271', '2023-12-05 14:25:32', '1', '', ''),
(272, 'Globe Homerun', 105, '4', '1.102.105.272', '2023-12-05 14:29:06', '1', '', ''),
(273, 'IPG', 110, '4', '1.102.110.273', '2023-12-07 09:15:00', '1', '', ''),
(274, 'IPG', 105, '4', '1.102.105.274', '2023-12-07 09:15:41', '1', '', ''),
(275, 'Project One', 103, '4', '1.102.103.275', '2023-12-07 09:18:50', '1', '', ''),
(276, 'PTO', 103, '4', '1.102.103.276', '2023-12-07 09:28:44', '1', '', ''),
(277, 'PTO', 110, '4', '1.102.110.277', '2023-12-07 09:31:47', '1', '', ''),
(278, 'PTO', 105, '4', '1.102.105.278', '2023-12-07 09:32:24', '1', '', ''),
(279, 'PALAWAN', 102, '3', '1.102.279', '2023-12-07 10:04:56', '1', '', ''),
(280, 'PTO', 279, '4', '1.102.279.280', '2023-12-07 10:05:16', '1', '', ''),
(281, 'Fibergen', 130, '4', '1.126.130.281', '2023-12-07 10:07:40', '1', '', ''),
(282, 'Fibergen', 129, '4', '1.126.129.282', '2023-12-07 10:09:06', '1', '', ''),
(283, 'MIN', 126, '3', '1.126.283', '2023-12-07 10:10:19', '1', '', ''),
(284, 'Fibergen', 283, '4', '1.126.283.284', '2023-12-07 10:10:35', '1', '', ''),
(285, 'G&E', 283, '4', '1.126.283.285', '2023-12-07 10:12:08', '1', '', ''),
(286, 'MARATEL', 126, '3', '1.126.286', '2023-12-07 10:24:29', '1', '', ''),
(287, 'Project One', 286, '4', '1.126.286.287', '2023-12-07 10:24:42', '1', '', ''),
(288, 'G&E', 240, '4', '1.2.240.288', '2023-12-07 10:34:01', '1', '', ''),
(289, 'ANTIPOLO', 83, '3', '1.83.289', '2023-12-12 09:47:43', '1', '', ''),
(290, 'IPG', 289, '4', '1.83.289.290', '2023-12-12 09:48:01', '1', '', ''),
(291, 'PMO', 1, '2', '1.291', '2023-12-14 16:00:37', '1', '', ''),
(292, 'BGC', 291, '3', '1.291.292', '2023-12-14 16:00:55', '1', '', ''),
(294, 'CAVITE', 291, '3', '1.291.294', '2023-12-14 16:07:13', '1', '', ''),
(295, 'MAKATI', 291, '3', '1.291.295', '2023-12-14 16:12:34', '1', '', ''),
(296, 'MANDALUYONG', 291, '3', '1.291.296', '2023-12-14 16:12:50', '1', '', ''),
(297, 'Fibergen', 295, '4', '1.291.295.297', '2023-12-14 16:19:31', '1', '', ''),
(298, 'Globe Homerun', 292, '4', '1.291.292.298', '2023-12-14 16:24:00', '1', '', ''),
(299, 'Globe Homerun', 294, '4', '1.291.294.299', '2023-12-14 16:24:18', '1', '', ''),
(300, 'Globe Homerun', 296, '4', '1.291.296.300', '2023-12-14 16:24:31', '1', '', ''),
(301, 'Project One', 295, '4', '1.291.295.301', '2023-12-14 16:33:07', '1', '', ''),
(302, 'PTO', 292, '4', '1.291.292.302', '2023-12-14 16:38:57', '1', '', ''),
(303, 'Rep Office', 295, '4', '1.291.295.303', '2023-12-14 16:44:53', '1', '', ''),
(304, 'Skycon', 296, '4', '1.291.296.304', '2023-12-14 16:49:38', '1', '', ''),
(305, 'Supply Chain Department', 295, '4', '1.291.295.305', '2023-12-14 16:52:46', '1', '', ''),
(306, 'Traditional Equipment and Delivery', 295, '4', '1.291.295.306', '2023-12-14 16:57:19', '1', '', ''),
(307, 'Traditional Equipment and Delivery', 296, '4', '1.291.296.307', '2023-12-14 16:57:53', '1', '', ''),
(308, 'G&E', 11, '4', '1.2.11.308', '2024-01-12 16:04:35', '1', '', ''),
(309, 'G&E', 127, '4', '1.126.127.309', '2024-01-12 16:35:33', '1', '', ''),
(310, 'PHILCOM', 126, '3', '1.126.310', '2024-01-12 16:51:15', '1', '', ''),
(311, 'Fibergen', 310, '4', '1.126.310.311', '2024-01-12 16:51:55', '1', '', ''),
(312, 'Project One', 310, '4', '1.126.310.312', '2024-01-12 16:52:17', '1', '', ''),
(313, 'Globe Homerun', 61, '4', '1.57.61.313', '2024-01-12 17:15:43', '1', '', ''),
(314, 'Fibergen', 172, '4', '1.57.172.314', '2024-01-12 17:19:45', '1', '', ''),
(315, 'PTO', 63, '4', '1.57.63.315', '2024-01-13 09:05:31', '1', '', ''),
(316, 'PTO', 296, '4', '1.291.296.316', '2024-01-13 09:24:10', '1', '', ''),
(317, 'Fibergen', 86, '4', '1.83.86.317', '2024-01-13 09:45:59', '1', '', ''),
(318, 'G&E', 86, '4', '1.83.86.318', '2024-01-13 09:47:39', '1', '', ''),
(319, 'IPG', 86, '4', '1.83.86.319', '2024-01-13 09:51:42', '1', '', ''),
(320, 'PTO', 86, '4', '1.83.86.320', '2024-01-13 09:54:07', '1', '', ''),
(321, 'BICOL', 102, '3', '1.102.321', '2024-01-13 11:27:24', '1', '', ''),
(322, 'Fibergen-Inhouse', 321, '4', '1.102.321.322', '2024-01-13 11:28:10', '1', '', ''),
(323, 'G&E', 103, '4', '1.102.103.323', '2024-01-13 11:32:36', '1', '', ''),
(324, 'IPG', 103, '4', '1.102.103.324', '2024-01-13 11:34:42', '1', '', ''),
(325, 'Rep Office', 296, '4', '1.291.296.325', '2024-02-07 09:29:03', '1', '', ''),
(326, 'MARIKINA', 291, '3', '1.291.326', '2024-02-07 09:31:16', '1', '', ''),
(327, 'Rep Office', 326, '4', '1.291.326.327', '2024-02-07 09:31:49', '1', '', ''),
(328, 'PTO', 112, '4', '1.102.112.328', '2024-02-07 10:19:47', '1', '', ''),
(329, 'PTO', 191, '4', '1.102.191.329', '2024-02-07 10:22:51', '1', '', ''),
(330, 'Globe Homerun', 109, '4', '1.102.109.330', '2024-02-07 10:24:15', '1', '', ''),
(331, 'PTO', 107, '4', '1.102.107.331', '2024-04-22 09:04:47', '1', '', ''),
(332, 'G&E', 107, '4', '1.102.107.332', '2024-04-22 09:05:11', '1', '', ''),
(333, 'PSS', 105, '4', '1.102.105.333', '2024-04-22 09:24:45', '1', '', ''),
(334, 'PTO', 106, '4', '1.102.106.334', '2024-04-22 09:25:18', '1', '', ''),
(335, 'G&E', 106, '4', '1.102.106.335', '2024-04-22 09:25:37', '1', '', ''),
(336, 'Globe', 106, '4', '1.102.106.336', '2024-04-22 09:25:58', '1', '', ''),
(337, 'PTO', 109, '4', '1.102.109.337', '2024-04-22 09:33:05', '1', '', ''),
(338, 'G&E', 109, '4', '1.102.109.338', '2024-04-22 09:33:20', '1', '', ''),
(339, 'G&E', 195, '4', '1.102.195.339', '2024-04-22 09:34:01', '1', '', ''),
(340, 'PTO', 108, '4', '1.102.108.340', '2024-04-22 09:34:49', '1', '', ''),
(341, 'G&E', 108, '4', '1.102.108.341', '2024-04-22 09:35:28', '1', '', ''),
(342, 'Globe', 108, '4', '1.102.108.342', '2024-04-22 09:35:55', '1', '', ''),
(343, 'PSS', 112, '4', '1.102.112.343', '2024-04-22 09:42:27', '1', '', ''),
(344, 'Globe', 191, '4', '1.102.191.344', '2024-04-22 09:44:04', '1', '', ''),
(345, 'PSS', 191, '4', '1.102.191.345', '2024-04-22 09:44:29', '1', '', ''),
(346, 'PSS', 107, '4', '1.102.107.346', '2024-04-24 14:12:38', '1', '', ''),
(347, 'GLOBE SLI/SLR', 21, '4', '1.2.21.347', '2024-05-28 14:47:59', '1', '', ''),
(348, 'PSS', 110, '4', '1.102.110.348', '2024-06-20 16:52:28', '1', '', ''),
(349, 'PSS', 104, '4', '1.102.104.349', '2024-06-20 16:56:50', '1', '', ''),
(350, 'Globe SLI-SLR', 255, '4', '1.2.255.350', '2024-06-21 14:42:04', '1', '', ''),
(351, 'PSS', 127, '4', '1.126.127.351', '2024-06-26 15:26:01', '1', '', ''),
(352, 'PSS', 286, '4', '1.126.286.352', '2024-06-26 15:27:27', '1', '', ''),
(353, 'PSS', 129, '4', '1.126.129.353', '2024-06-26 15:28:23', '1', '', ''),
(354, 'PSS', 128, '4', '1.126.128.354', '2024-06-26 15:28:58', '1', '', ''),
(355, 'PSS', 132, '4', '1.126.132.355', '2024-06-26 15:29:26', '1', '', ''),
(356, 'PSS', 133, '4', '1.126.133.356', '2024-06-26 15:30:42', '1', '', ''),
(357, 'PSS', 130, '4', '1.126.130.357', '2024-06-26 15:31:25', '1', '', ''),
(358, 'GLobe SLI', 84, '4', '1.83.84.358', '2024-06-27 14:51:40', '1', '', ''),
(359, 'GLobe SLI', 87, '4', '1.83.87.359', '2024-06-27 14:52:24', '1', '', ''),
(360, 'Test', 102, '3', '1.102.360', '2025-02-21 09:01:35', '1', '', '');

-- --------------------------------------------------------

--
-- Table structure for table `resources`
--

CREATE TABLE `resources` (
  `RESOURCES_ID` int(11) NOT NULL,
  `PARENT_ID` int(11) DEFAULT NULL,
  `NAME_` varchar(100) DEFAULT NULL,
  `CODE_` varchar(10) DEFAULT NULL,
  `TYPE_` varchar(2) DEFAULT NULL,
  `VALUE_` varchar(150) DEFAULT NULL,
  `REMARK` varchar(200) DEFAULT NULL,
  `STATE` varchar(2) DEFAULT NULL,
  `CREATE_DATE` datetime DEFAULT current_timestamp(),
  `ICON_URL` varchar(100) DEFAULT NULL,
  `STATUS_` int(11) DEFAULT NULL,
  `GRADE` varchar(45) DEFAULT NULL,
  `SORT` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `resources`
--

INSERT INTO `resources` (`RESOURCES_ID`, `PARENT_ID`, `NAME_`, `CODE_`, `TYPE_`, `VALUE_`, `REMARK`, `STATE`, `CREATE_DATE`, `ICON_URL`, `STATUS_`, `GRADE`, `SORT`) VALUES
(1, NULL, 'Access Management', '01', '1', 'menu01', NULL, NULL, '2025-02-20 13:05:06', ' - ', 1, '0', '5'),
(2, 1, 'Manage Organization  ', '0101', '2', '/organization', NULL, NULL, '2025-02-20 13:05:06', 'fa fa-globe', 1, '1', NULL),
(3, 1, 'Manage Roles', '0102', '2', '/roles', NULL, NULL, '2025-02-20 13:05:06', 'fa fa-user-circle', 1, '1', NULL),
(4, 1, 'Manage Users', '0103', '2', '/accounts', NULL, NULL, '2025-02-20 13:05:06', 'fa fa-users', 1, '1', NULL),
(5, NULL, 'Data Dictionary', '08', '1', 'Menu08', NULL, NULL, '2025-02-20 13:05:06', '-', 1, '0', '8'),
(6, 5, 'Attributed List', '0801', '2', '/te-attributed-list', NULL, NULL, '2025-02-20 13:05:06', 'pi pi-book', 1, '1', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `role`
--

CREATE TABLE `role` (
  `ROLE_ID` int(11) NOT NULL,
  `NAME_` varchar(50) DEFAULT NULL,
  `POWER` varchar(50) DEFAULT NULL,
  `DESCRIBES` varchar(200) DEFAULT NULL,
  `STATE` varchar(2) DEFAULT NULL,
  `CREATE_DATE` datetime DEFAULT NULL,
  `TYPE_` varchar(45) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `grade_id` smallint(6) DEFAULT NULL,
  `role_describe` varchar(255) DEFAULT NULL,
  `role_name` varchar(255) DEFAULT NULL,
  `role_name_cn` varchar(255) DEFAULT NULL,
  `role_parent_id` bigint(20) DEFAULT NULL,
  `role_power` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `role`
--

INSERT INTO `role` (`ROLE_ID`, `NAME_`, `POWER`, `DESCRIBES`, `STATE`, `CREATE_DATE`, `TYPE_`, `created_date`, `grade_id`, `role_describe`, `role_name`, `role_name_cn`, `role_parent_id`, `role_power`) VALUES
(1, 'Super Administrator', '', 'OMD', '1', '2022-03-16 09:21:09', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `role_account`
--

CREATE TABLE `role_account` (
  `ROLE_ACCOUNT_ID` int(11) NOT NULL,
  `ROLE_ID` int(11) DEFAULT NULL,
  `ACCOUNT_ID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `role_account`
--

INSERT INTO `role_account` (`ROLE_ACCOUNT_ID`, `ROLE_ID`, `ACCOUNT_ID`) VALUES
(1, 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `role_org_relation`
--

CREATE TABLE `role_org_relation` (
  `id_` int(11) NOT NULL,
  `user_id_` varchar(45) DEFAULT NULL,
  `role_id_` varchar(45) DEFAULT NULL,
  `org_id_` varchar(45) DEFAULT NULL,
  `create_time_` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='user_id_ = account.ACCOUNT_ID, role_id_ = role.ROLE_ID, org_id_ = organization2_.id_';

--
-- Dumping data for table `role_org_relation`
--

INSERT INTO `role_org_relation` (`id_`, `user_id_`, `role_id_`, `org_id_`, `create_time_`) VALUES
(1, '1', '1', '1', '2025-02-20 13:37:23');

-- --------------------------------------------------------

--
-- Table structure for table `role_resource`
--

CREATE TABLE `role_resource` (
  `ROLE_RESOURCE_ID` int(11) NOT NULL,
  `USE_TYPE` varchar(2) DEFAULT NULL,
  `RESOURCES_ID` int(11) NOT NULL,
  `ROLE_ID` int(11) NOT NULL,
  `USER_ID` int(11) DEFAULT NULL,
  `COMPANY_ID` varchar(10) DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `role_resource`
--

INSERT INTO `role_resource` (`ROLE_RESOURCE_ID`, `USE_TYPE`, `RESOURCES_ID`, `ROLE_ID`, `USER_ID`, `COMPANY_ID`) VALUES
(1, '1', 1, 1, NULL, '1'),
(2, '1', 2, 1, NULL, '1'),
(3, '1', 3, 1, NULL, '1'),
(4, '1', 4, 1, NULL, '1'),
(5, '1', 5, 1, NULL, '1'),
(6, '1', 6, 1, NULL, '1');

-- --------------------------------------------------------

--
-- Table structure for table `widget_permission`
--

CREATE TABLE `widget_permission` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  `company_id` varchar(45) DEFAULT NULL,
  `resource_id` int(11) DEFAULT NULL,
  `display` varchar(45) DEFAULT NULL,
  `type` varchar(45) DEFAULT NULL,
  `widget_code` varchar(45) DEFAULT NULL,
  `label` varchar(45) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT current_timestamp(),
  `remarks` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `widget_permission`
--

INSERT INTO `widget_permission` (`id`, `user_id`, `role_id`, `company_id`, `resource_id`, `display`, `type`, `widget_code`, `label`, `created_date`, `remarks`) VALUES
(1, 0, 1, '1', 45, 'flex', 'button', 'w014', 'Add User', '2025-02-21 01:14:23', NULL),
(2, 0, 1, '1', 45, 'flex', 'button', 'w015', 'Import User', '2025-02-21 01:14:23', NULL),
(3, 0, 1, '1', 45, 'flex', 'button', 'w016', 'Admin Suspend', '2025-02-21 01:14:23', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `widget_permission_legend`
--

CREATE TABLE `widget_permission_legend` (
  `id` int(11) NOT NULL,
  `code` varchar(45) DEFAULT NULL,
  `label` varchar(45) DEFAULT NULL,
  `type` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `widget_permission_legend`
--

INSERT INTO `widget_permission_legend` (`id`, `code`, `label`, `type`) VALUES
(1, 'w014', 'Add User', 'button'),
(2, 'w015', 'Import User', 'button'),
(3, 'w016', 'Admin Suspend', 'button');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `account`
--
ALTER TABLE `account`
  ADD PRIMARY KEY (`ACCOUNT_ID`),
  ADD KEY `IDX$$_36840002` (`ACCOUNT_ID`),
  ADD KEY `idx_state` (`STATE`),
  ADD KEY `idx_ORGANIZATIONID_FULLNAME_ACCOUNTNAME` (`ORGANIZATION_ID`,`FULL_NAME`,`ACCOUNT_NAME`),
  ADD KEY `idx_ATTENDANCEMACHINEID_STATE_FULLNAME` (`STATE`,`FULL_NAME`);

--
-- Indexes for table `activity_log`
--
ALTER TABLE `activity_log`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `announcements`
--
ALTER TABLE `announcements`
  ADD PRIMARY KEY (`AnnouncementId`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx__instanceid` (`instance_id`);

--
-- Indexes for table `organization_ledger`
--
ALTER TABLE `organization_ledger`
  ADD PRIMARY KEY (`id_`);

--
-- Indexes for table `resources`
--
ALTER TABLE `resources`
  ADD PRIMARY KEY (`RESOURCES_ID`),
  ADD UNIQUE KEY `PK$RESOURCES` (`RESOURCES_ID`);

--
-- Indexes for table `role`
--
ALTER TABLE `role`
  ADD PRIMARY KEY (`ROLE_ID`),
  ADD UNIQUE KEY `ROLE_PK` (`ROLE_ID`);

--
-- Indexes for table `role_account`
--
ALTER TABLE `role_account`
  ADD PRIMARY KEY (`ROLE_ACCOUNT_ID`);

--
-- Indexes for table `role_org_relation`
--
ALTER TABLE `role_org_relation`
  ADD PRIMARY KEY (`id_`);

--
-- Indexes for table `role_resource`
--
ALTER TABLE `role_resource`
  ADD PRIMARY KEY (`ROLE_RESOURCE_ID`),
  ADD UNIQUE KEY `unq_resource&role` (`ROLE_ID`,`RESOURCES_ID`,`USER_ID`);

--
-- Indexes for table `widget_permission`
--
ALTER TABLE `widget_permission`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `widget_permission_legend`
--
ALTER TABLE `widget_permission_legend`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `account`
--
ALTER TABLE `account`
  MODIFY `ACCOUNT_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15250;

--
-- AUTO_INCREMENT for table `activity_log`
--
ALTER TABLE `activity_log`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `announcements`
--
ALTER TABLE `announcements`
  MODIFY `AnnouncementId` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `organization_ledger`
--
ALTER TABLE `organization_ledger`
  MODIFY `id_` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=361;

--
-- AUTO_INCREMENT for table `resources`
--
ALTER TABLE `resources`
  MODIFY `RESOURCES_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `role`
--
ALTER TABLE `role`
  MODIFY `ROLE_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `role_account`
--
ALTER TABLE `role_account`
  MODIFY `ROLE_ACCOUNT_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `role_org_relation`
--
ALTER TABLE `role_org_relation`
  MODIFY `id_` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `role_resource`
--
ALTER TABLE `role_resource`
  MODIFY `ROLE_RESOURCE_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `widget_permission`
--
ALTER TABLE `widget_permission`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `widget_permission_legend`
--
ALTER TABLE `widget_permission_legend`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
