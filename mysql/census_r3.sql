-- phpMyAdmin SQL Dump
-- version 3.3.7deb7
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jul 23, 2012 at 05:07 AM
-- Server version: 5.1.63
-- PHP Version: 5.3.3-7+squeeze13

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `census`
--

-- --------------------------------------------------------

--
-- Table structure for table `administrator_adm`
--

CREATE TABLE IF NOT EXISTS `administrator_adm` (
  `id_adm` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `username` text COLLATE utf8_unicode_ci NOT NULL,
  `full_name` text COLLATE utf8_unicode_ci NOT NULL,
  `password` text COLLATE utf8_unicode_ci NOT NULL,
  `address` text COLLATE utf8_unicode_ci NOT NULL,
  `telephone` text COLLATE utf8_unicode_ci NOT NULL,
  `note` text COLLATE utf8_unicode_ci NOT NULL,
  `permissions_level` tinyint(1) NOT NULL DEFAULT '5',
  PRIMARY KEY (`id_adm`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `ad_source_ads`
--

CREATE TABLE IF NOT EXISTS `ad_source_ads` (
  `id_ads` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `title` text CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id_ads`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `attendance_atd`
--

CREATE TABLE IF NOT EXISTS `attendance_atd` (
  `id_atd` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `idcln_atd` smallint(5) unsigned DEFAULT NULL,
  `datetime_begin` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `datetime_end` timestamp NOT NULL DEFAULT '2004-04-04 09:00:01',
  `idkey_atd` tinyint(2) unsigned NOT NULL,
  PRIMARY KEY (`id_atd`),
  KEY `idcln_atd` (`idcln_atd`),
  KEY `idkey_atd` (`idkey_atd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `cash_adjustment_cad`
--

CREATE TABLE IF NOT EXISTS `cash_adjustment_cad` (
  `date_recorded` date NOT NULL,
  `amount` decimal(6,2) NOT NULL,
  `note` text COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`date_recorded`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `client_cln`
--

CREATE TABLE IF NOT EXISTS `client_cln` (
  `id_cln` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `card` int(8) unsigned DEFAULT NULL,
  `full_name` text COLLATE utf8_unicode_ci NOT NULL,
  `registration_date` date NOT NULL,
  `money_balance` decimal(5,2) NOT NULL DEFAULT '0.00',
  `attendances_balance` tinyint(3) NOT NULL DEFAULT '0',
  `expiration_date` date NOT NULL,
  `note` text COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id_cln`),
  UNIQUE KEY `card` (`card`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `client_freeze_cfz`
--

CREATE TABLE IF NOT EXISTS `client_freeze_cfz` (
  `id_cfz` tinyint(3) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The ID of the freeze',
  `idcln_cfz` smallint(5) unsigned NOT NULL COMMENT 'The ID of the client',
  `date_issued` date NOT NULL COMMENT 'The date when the freeze was initiated',
  `days` tinyint(4) NOT NULL COMMENT 'The number of days the client''s expiration date was postponed by',
  `idadm_cfz` tinyint(3) unsigned NOT NULL COMMENT 'The ID of the administrator who issued the freeze',
  `note` text COLLATE utf8_unicode_ci NOT NULL COMMENT 'The comment about the freeze',
  PRIMARY KEY (`id_cfz`),
  KEY `idcln_cfz` (`idcln_cfz`),
  KEY `idadm_cfz` (`idadm_cfz`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Client freeze is a postponing of the expiration date.' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `client_profile_cpf`
--

CREATE TABLE IF NOT EXISTS `client_profile_cpf` (
  `idcln_cpf` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `sex` tinyint(1) NOT NULL DEFAULT '2',
  `birthday` date NOT NULL DEFAULT '2096-05-16',
  `address` text COLLATE utf8_unicode_ci NOT NULL,
  `telephone` text COLLATE utf8_unicode_ci NOT NULL,
  `goal` text COLLATE utf8_unicode_ci NOT NULL,
  `possible_attendance_rate` text COLLATE utf8_unicode_ci NOT NULL,
  `health_restrictions` text COLLATE utf8_unicode_ci NOT NULL,
  `favourite_sport` text COLLATE utf8_unicode_ci NOT NULL,
  `fitness_experience` tinyint(1) unsigned NOT NULL,
  `special_wishes` text COLLATE utf8_unicode_ci NOT NULL,
  `height` tinyint(3) unsigned NOT NULL,
  `weight` tinyint(3) unsigned NOT NULL,
  `idads_cpf` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`idcln_cpf`),
  KEY `idads_cpf` (`idads_cpf`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `item_itm`
--

CREATE TABLE IF NOT EXISTS `item_itm` (
  `id_itm` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `barcode` bigint(20) unsigned DEFAULT NULL,
  `title` text COLLATE utf8_unicode_ci NOT NULL,
  `quantity` tinyint(4) unsigned DEFAULT NULL,
  `price` decimal(5,2) unsigned NOT NULL,
  PRIMARY KEY (`id_itm`),
  UNIQUE KEY `barcode` (`barcode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `item_subscription_its`
--

CREATE TABLE IF NOT EXISTS `item_subscription_its` (
  `iditm_its` tinyint(3) unsigned NOT NULL,
  `units` tinyint(3) unsigned NOT NULL,
  `term_days` tinyint(3) unsigned NOT NULL,
  `term_months` tinyint(3) unsigned NOT NULL,
  `term_years` tinyint(3) unsigned NOT NULL,
  `idtmr_its` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`iditm_its`),
  KEY `id_tmr` (`idtmr_its`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `key_key`
--

CREATE TABLE IF NOT EXISTS `key_key` (
  `id_key` tinyint(2) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(3) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `order_line_orl`
--

CREATE TABLE IF NOT EXISTS `order_line_orl` (
  `idord_orl` smallint(5) unsigned NOT NULL,
  `quantity` tinyint(3) unsigned NOT NULL,
  `iditm_orl` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`idord_orl`,`iditm_orl`),
  KEY `idord_orl` (`idord_orl`),
  KEY `iditm_orl` (`iditm_orl`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `order_ord`
--

CREATE TABLE IF NOT EXISTS `order_ord` (
  `id_ord` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `date_recorded` date NOT NULL,
  `idcln_ord` smallint(5) unsigned DEFAULT NULL,
  `idatd_ord` smallint(5) unsigned DEFAULT NULL,
  `payment` decimal(5,2) NOT NULL,
  PRIMARY KEY (`id_ord`),
  KEY `idatd_fna` (`idatd_ord`),
  KEY `idcln_fna` (`idcln_ord`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `property_pty`
--

CREATE TABLE IF NOT EXISTS `property_pty` (
  `id_pty` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` text COLLATE utf8_unicode_ci NOT NULL,
  `current_value` text COLLATE utf8_unicode_ci,
  PRIMARY KEY (`id_pty`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `session_ssn`
--

CREATE TABLE IF NOT EXISTS `session_ssn` (
  `id_ssn` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `idadm_ssn` tinyint(3) unsigned NOT NULL,
  `datetime_begin` datetime NOT NULL,
  `datetime_end` datetime DEFAULT NULL,
  PRIMARY KEY (`id_ssn`),
  KEY `idadm_ssn` (`idadm_ssn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `time_range_tmr`
--

CREATE TABLE IF NOT EXISTS `time_range_tmr` (
  `id_tmr` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `time_begin` time NOT NULL,
  `time_end` time NOT NULL,
  PRIMARY KEY (`id_tmr`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `attendance_atd`
--
ALTER TABLE `attendance_atd`
  ADD CONSTRAINT `idcln_atd` FOREIGN KEY (`idcln_atd`) REFERENCES `client_cln` (`id_cln`) ON UPDATE CASCADE,
  ADD CONSTRAINT `idkey_atd` FOREIGN KEY (`idkey_atd`) REFERENCES `key_key` (`id_key`) ON UPDATE CASCADE;

--
-- Constraints for table `client_freeze_cfz`
--
ALTER TABLE `client_freeze_cfz`
  ADD CONSTRAINT `client_freeze_cfz_ibfk_1` FOREIGN KEY (`idcln_cfz`) REFERENCES `client_cln` (`id_cln`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `client_freeze_cfz_ibfk_2` FOREIGN KEY (`idadm_cfz`) REFERENCES `administrator_adm` (`id_adm`) ON UPDATE CASCADE;

--
-- Constraints for table `client_profile_cpf`
--
ALTER TABLE `client_profile_cpf`
  ADD CONSTRAINT `client_profile_cpf_ibfk_1` FOREIGN KEY (`idcln_cpf`) REFERENCES `client_cln` (`id_cln`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `idads_cpf` FOREIGN KEY (`idads_cpf`) REFERENCES `ad_source_ads` (`id_ads`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `item_subscription_its`
--
ALTER TABLE `item_subscription_its`
  ADD CONSTRAINT `item_subscription_its_ibfk_1` FOREIGN KEY (`iditm_its`) REFERENCES `item_itm` (`id_itm`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `item_subscription_its_ibfk_2` FOREIGN KEY (`idtmr_its`) REFERENCES `time_range_tmr` (`id_tmr`) ON UPDATE CASCADE;

--
-- Constraints for table `order_line_orl`
--
ALTER TABLE `order_line_orl`
  ADD CONSTRAINT `order_line_orl_ibfk_1` FOREIGN KEY (`idord_orl`) REFERENCES `order_ord` (`id_ord`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `order_line_orl_ibfk_2` FOREIGN KEY (`iditm_orl`) REFERENCES `item_itm` (`id_itm`) ON UPDATE CASCADE;

--
-- Constraints for table `order_ord`
--
ALTER TABLE `order_ord`
  ADD CONSTRAINT `order_ord_ibfk_1` FOREIGN KEY (`idcln_ord`) REFERENCES `client_cln` (`id_cln`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `order_ord_ibfk_2` FOREIGN KEY (`idatd_ord`) REFERENCES `attendance_atd` (`id_atd`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `session_ssn`
--
ALTER TABLE `session_ssn`
  ADD CONSTRAINT `session_ssn_ibfk_1` FOREIGN KEY (`idadm_ssn`) REFERENCES `administrator_adm` (`id_adm`) ON UPDATE CASCADE;
