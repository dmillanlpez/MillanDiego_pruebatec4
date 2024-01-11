CREATE DATABASE  IF NOT EXISTS `AgenciaTurismo` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `AgenciaTurismo`;
-- MySQL dump 10.13  Distrib 8.0.34, for macos13 (arm64)
--
-- Host: 127.0.0.1    Database: AgenciaTurismo
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.28-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `flight`
--

DROP TABLE IF EXISTS `flight`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flight` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flight_arrival` varchar(255) DEFAULT NULL,
  `cod_flight` varchar(255) DEFAULT NULL,
  `flight_date` date DEFAULT NULL,
  `flight_departure` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `last_deleted_date` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flight`
--

LOCK TABLES `flight` WRITE;
/*!40000 ALTER TABLE `flight` DISABLE KEYS */;
/*!40000 ALTER TABLE `flight` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flight_reservation`
--

DROP TABLE IF EXISTS `flight_reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flight_reservation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `arrival` varchar(255) DEFAULT NULL,
  `cod_flight` varchar(255) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `departure` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `last_deleted_date` date DEFAULT NULL,
  `peopleq` int(11) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `seat_type` varchar(255) DEFAULT NULL,
  `id_flight` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKaorff68tvbi4ys5ekyppyxpf0` (`id_flight`),
  CONSTRAINT `FKaorff68tvbi4ys5ekyppyxpf0` FOREIGN KEY (`id_flight`) REFERENCES `flight` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flight_reservation`
--

LOCK TABLES `flight_reservation` WRITE;
/*!40000 ALTER TABLE `flight_reservation` DISABLE KEYS */;
/*!40000 ALTER TABLE `flight_reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flight_reservation_passengers`
--

DROP TABLE IF EXISTS `flight_reservation_passengers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flight_reservation_passengers` (
  `flight_reservation_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  KEY `FKmbts2x94s6sjm60wtw63jhed3` (`user_id`),
  KEY `FKcvtt62e3w9fkwmmfv1fa2hub1` (`flight_reservation_id`),
  CONSTRAINT `FKcvtt62e3w9fkwmmfv1fa2hub1` FOREIGN KEY (`flight_reservation_id`) REFERENCES `flight_reservation` (`id`),
  CONSTRAINT `FKmbts2x94s6sjm60wtw63jhed3` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flight_reservation_passengers`
--

LOCK TABLES `flight_reservation_passengers` WRITE;
/*!40000 ALTER TABLE `flight_reservation_passengers` DISABLE KEYS */;
/*!40000 ALTER TABLE `flight_reservation_passengers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hotel`
--

DROP TABLE IF EXISTS `hotel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cod_hotel` varchar(255) DEFAULT NULL,
  `hotel_name` varchar(255) DEFAULT NULL,
  `is_booked` bit(1) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `last_deleted_date` date DEFAULT NULL,
  `hotel_location` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel`
--

LOCK TABLES `hotel` WRITE;
/*!40000 ALTER TABLE `hotel` DISABLE KEYS */;
INSERT INTO `hotel` VALUES (1,'HOPA-8837','Hotel Paraiso',_binary '\0',_binary '\0',NULL,'Coruna');
/*!40000 ALTER TABLE `hotel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hotel_reservation`
--

DROP TABLE IF EXISTS `hotel_reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel_reservation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cod_hotel` varchar(255) DEFAULT NULL,
  `date_from` date DEFAULT NULL,
  `date_to` date DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `nights` int(11) NOT NULL,
  `peopleq` int(11) NOT NULL,
  `price` double DEFAULT NULL,
  `room_type` varchar(255) DEFAULT NULL,
  `last_deleted_date` date DEFAULT NULL,
  `hotel_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2s13e6gavac76hjvuphflka53` (`hotel_id`),
  CONSTRAINT `FK2s13e6gavac76hjvuphflka53` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel_reservation`
--

LOCK TABLES `hotel_reservation` WRITE;
/*!40000 ALTER TABLE `hotel_reservation` DISABLE KEYS */;
/*!40000 ALTER TABLE `hotel_reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hotel_reservation_users`
--

DROP TABLE IF EXISTS `hotel_reservation_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel_reservation_users` (
  `hotel_reservation_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  KEY `FK8idq1dy3qwbhj2lrrt5fsajsu` (`user_id`),
  KEY `FKa313de7n0s5aei3m9wrx6lh4p` (`hotel_reservation_id`),
  CONSTRAINT `FK8idq1dy3qwbhj2lrrt5fsajsu` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKa313de7n0s5aei3m9wrx6lh4p` FOREIGN KEY (`hotel_reservation_id`) REFERENCES `hotel_reservation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel_reservation_users`
--

LOCK TABLES `hotel_reservation_users` WRITE;
/*!40000 ALTER TABLE `hotel_reservation_users` DISABLE KEYS */;
/*!40000 ALTER TABLE `hotel_reservation_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `avaliable_date_from` date DEFAULT NULL,
  `avaliable_date_to` date DEFAULT NULL,
  `room_price` double DEFAULT NULL,
  `room_type` varchar(255) DEFAULT NULL,
  `hotel_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_fdtq4rubhga537amx7apm0eel` (`hotel_id`),
  CONSTRAINT `FKdosq3ww4h9m2osim6o0lugng8` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
INSERT INTO `room` VALUES (1,'2024-05-15','2024-05-25',150,'doble',1);
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `age` int(11) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `passport` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-01-11 23:26:15
