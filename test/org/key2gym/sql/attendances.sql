-- phpMyAdmin SQL Dump
-- version 3.3.7deb7
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Aug 16, 2012 at 05:38 AM
-- Server version: 5.1.63
-- PHP Version: 5.3.3-7+squeeze13

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Database: `census_testing_attendances`
--

--
-- Dumping data for table `administrator_adm`
--


--
-- Dumping data for table `ad_source_ads`
--


--
-- Dumping data for table `attendance_atd`
--


--
-- Dumping data for table `cash_adjustment_cad`
--


--
-- Dumping data for table `client_cln`
--

INSERT INTO `client_cln` (`id_cln`, `card`, `full_name`, `registration_date`, `money_balance`, `attendances_balance`, `expiration_date`, `note`) VALUES
(1, NULL, 'Good client', '1996-05-16', '0.00', 5, '2096-05-16', ''),
(2, NULL, 'Client with exp sub', '1996-05-16', '0.00', 5, '1996-05-16', ''),
(3, NULL, 'Client with no attn left', '1996-05-16', '0.00', 0, '2096-05-16', ''),
(4, NULL, 'Client with no sub and attn left', '1996-05-16', '0.00', 0, '1996-05-16', '');

--
-- Dumping data for table `client_freeze_cfz`
--


--
-- Dumping data for table `client_profile_cpf`
--


--
-- Dumping data for table `discount_dsc`
--


--
-- Dumping data for table `item_itm`
--


--
-- Dumping data for table `item_subscription_its`
--


--
-- Dumping data for table `key_key`
--

INSERT INTO `key_key` (`id_key`, `title`) VALUES
(1, '1'),
(2, '2'),
(3, '3'),
(4, '4');

--
-- Dumping data for table `order_line_orl`
--


--
-- Dumping data for table `order_ord`
--


--
-- Dumping data for table `property_pty`
--


--
-- Dumping data for table `session_ssn`
--


--
-- Dumping data for table `time_split_tsp`
--

