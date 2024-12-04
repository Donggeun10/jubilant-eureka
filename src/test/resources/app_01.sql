-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        8.0.18-google - (Google)
-- 서버 OS:                        Linux
-- HeidiSQL 버전:                  12.8.0.6927
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- app_01 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `app_01` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `app_01`;

-- 프로시저 app_01.SP_MANAGE_DAY_PARTITIONS 구조 내보내기
DELIMITER //
CREATE PROCEDURE `SP_MANAGE_DAY_PARTITIONS`(
	IN `appNo` VARCHAR(25),
	IN `tableName` VARCHAR(100),
	IN `tablePartitionName` VARCHAR(100),
	IN `dayRetentionPeriod` INT,
	OUT `errorMsg` VARCHAR(2000)
)
BEGIN
   DECLARE done INT DEFAULT FALSE;											
   DECLARE v_table_schema varchar(20);
   DECLARE v_table_name varchar(100);
   DECLARE v_min_pt INT;
   DECLARE v_max_pt INT;
   DECLARE v_min_partition_name VARCHAR(100);
   DECLARE v_min_partition_description VARCHAR(100);   
   DECLARE v_max_partition_name VARCHAR(100);
   DECLARE v_max_partition_description VARCHAR(100);   
	
	DECLARE v_min_partition_value varchar(20);
   DECLARE v_table_min_partition_value varchar(100);
   DECLARE v_num_drop_pt INT;
   DECLARE v_table_max_partition_value VARCHAR(100);
   DECLARE v_num_add_pt VARCHAR(100);   
   
   DECLARE pt_date VARCHAR(20) DEFAULT '';
   DECLARE pt_date_less VARCHAR(20) DEFAULT '';
   DECLARE add_pt INT DEFAULT 1;
   DECLARE drop_pt INT DEFAULT 1;

   DECLARE v_partition_name VARCHAR(100);   
   
  	DECLARE ptInfoCursor CURSOR FOR SELECT t.* , a.partition_name AS min_partition_name, 
	                                     a.partition_description AS min_partition_description,
											       b.partition_name AS max_partition_name, 
													 b.partition_description AS max_partition_description
											FROM (
												SELECT table_schema, 
												       TABLE_NAME, 
												       MIN(partition_ordinal_position) as min_pt, 
														 MAX(partition_ordinal_position) AS max_pt
												FROM information_schema.PARTITIONS
												WHERE table_schema = appNo
											     AND TABLE_NAME = tableName
											     AND partition_method = 'RANGE COLUMNS'
												GROUP BY table_schema, TABLE_NAME
											) t INNER JOIN information_schema.PARTITIONS a ON a.table_schema = t.table_schema AND a.TABLE_NAME = t.table_name AND a.partition_ordinal_position = t.min_pt
											INNER JOIN information_schema.PARTITIONS b ON b.table_schema = t.table_schema AND b.TABLE_NAME = t.table_name AND b.partition_ordinal_position = t.max_pt ;										
											
    DECLARE ptInfoSubCursor CURSOR FOR SELECT 
	 											       DATE_SUB(DATE(NOW()), interval dayRetentionPeriod DAY) AS min_partition_value,
 														 DATE_FORMAT(REPLACE(v_min_partition_description, '\'', ''),'%Y%m%d') AS table_min_partition_value, 
														 DATEDIFF(DATE_SUB(DATE(NOW()), interval dayRetentionPeriod DAY) ,DATE_FORMAT(REPLACE(v_min_partition_description, '\'', ''),'%Y%m%d')) AS num_drop_pt, 
														 DATE_FORMAT(REPLACE(v_max_partition_description, '\'', ''),'%Y%m%d') AS table_max_partition_value, 
														 DATEDIFF(DATE_ADD(DATE(NOW()), interval 14 DAY), DATE_FORMAT(REPLACE(v_max_partition_description, '\'', ''),'%Y%m%d')) AS num_add_pt ;
														 
   DECLARE dropPtNamesCursor CURSOR FOR  SELECT table_Schema, TABLE_NAME, partition_name
														   FROM information_schema.partitions 	
															WHERE table_schema = appNo
														     AND TABLE_NAME = tableName
														     AND partition_method = 'RANGE COLUMNS'
															  AND partition_description < CONCAT('\'',DATE_FORMAT(v_min_partition_value,'%Y%m%d'),'\'')	;													 
															 											
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
	
	OPEN ptInfoCursor; 
	
	  -- loop 하며 ptInfoCursor 의 데이터를 불러와 변수에 넣는다.	  
	  FETCH ptInfoCursor 
	   INTO v_table_schema, v_table_name, v_min_pt, v_max_pt, v_min_partition_name, v_min_partition_description, v_max_partition_name, v_max_partition_description;

	   set errorMsg = CONCAT(v_table_schema, '|', v_table_name, '|', v_min_pt, '|', v_max_pt, '|', v_min_partition_name, '|', v_min_partition_description, '|', v_max_partition_name, '|', v_max_partition_description);
	   
    	OPEN ptInfoSubCursor; 
	    	FETCH ptInfoSubCursor 
		   INTO v_min_partition_value, v_table_min_partition_value, v_num_drop_pt, v_table_max_partition_value, v_num_add_pt;
		   
       	set errorMsg = CONCAT(v_min_partition_value, '|', v_table_min_partition_value, '|', v_num_drop_pt, '|', v_table_max_partition_value, '|', v_num_add_pt);
    	CLOSE ptInfoSubCursor;

      -- add
      WHILE add_pt <= v_num_add_pt DO
		   SET pt_date = DATE_FORMAT(DATE_ADD(REPLACE(v_max_partition_description, '\'', ''), INTERVAL add_pt-1 DAY),'%Y%m%d');
		   SET pt_date_less = DATE_FORMAT(DATE_ADD(REPLACE(v_max_partition_description, '\'', ''), INTERVAL add_pt DAY),'%Y%m%d');		   
		   SET @SQL_QUERY = CONCAT('ALTER TABLE ',tableName , ' ADD PARTITION ( PARTITION ', tablePartitionName, pt_date, ' VALUES LESS THAN(\'' , pt_date_less , '\') engine=innodb)');
			PREPARE stmt FROM @SQL_QUERY;
			EXECUTE stmt;

			SET add_pt = add_pt + 1;	

		END WHILE;
		
		OPEN dropPtNamesCursor; 
      drop_loop: LOOP

			FETCH dropPtNamesCursor 
		   INTO v_table_schema, v_table_name, v_partition_name ;
		   
		   IF done THEN
				LEAVE drop_loop;
			END IF;
		   
		   SET @SQL_QUERY = CONCAT('ALTER TABLE ', v_table_name , ' DROP PARTITION ', v_partition_name);
		   PREPARE stmt FROM @SQL_QUERY;
			EXECUTE stmt;
 			
 		END LOOP;  
 		CLOSE dropPtNamesCursor;      

   CLOSE ptInfoCursor;
END//
DELIMITER ;

-- 프로시저 app_01.SP_MANAGE_HOUR_PARTITIONS 구조 내보내기
DELIMITER //
CREATE PROCEDURE `SP_MANAGE_HOUR_PARTITIONS`(
	IN `appNo` VARCHAR(25),
	IN `tableName` VARCHAR(100),
	IN `tablePartitionName` VARCHAR(100),
	OUT `errorMsg` VARCHAR(2000)
)
BEGIN
	DECLARE hourRetentionPeriod INT DEFAULT 792;	
   DECLARE done INT DEFAULT FALSE;											
   DECLARE v_table_schema varchar(20);
   DECLARE v_table_name varchar(100);
   DECLARE v_min_pt INT;
   DECLARE v_max_pt INT;
   DECLARE v_min_partition_name VARCHAR(100);
   DECLARE v_min_partition_description VARCHAR(100);   
   DECLARE v_max_partition_name VARCHAR(100);
   DECLARE v_max_partition_description VARCHAR(100);   
	
	DECLARE v_min_partition_value varchar(20);
   DECLARE v_table_min_partition_value varchar(100);
   DECLARE v_num_drop_pt INT;
   DECLARE v_table_max_partition_value VARCHAR(100);
   DECLARE v_num_add_pt VARCHAR(100);   
   
   DECLARE pt_date VARCHAR(20) DEFAULT '';
   DECLARE pt_date_less VARCHAR(20) DEFAULT '';
   DECLARE add_pt INT DEFAULT 1;
   DECLARE drop_pt INT DEFAULT 1;
   
  	DECLARE ptInfoCursor CURSOR FOR SELECT t.* , a.partition_name AS min_partition_name, 
	                                     a.partition_description AS min_partition_description,
											       b.partition_name AS max_partition_name, 
													 b.partition_description AS max_partition_description
											FROM (
												SELECT table_schema, 
												       TABLE_NAME, 
												       MIN(partition_ordinal_position) as min_pt, 
														 MAX(partition_ordinal_position) AS max_pt
												FROM information_schema.PARTITIONS
												WHERE table_schema= appNo
											     AND TABLE_NAME = tableName
											     AND partition_method = 'RANGE COLUMNS'
												GROUP BY table_schema, TABLE_NAME
											) t INNER JOIN information_schema.PARTITIONS a ON a.table_schema = t.table_schema AND a.TABLE_NAME = t.table_name AND a.partition_ordinal_position = t.min_pt
											INNER JOIN information_schema.PARTITIONS b ON b.table_schema = t.table_schema AND b.TABLE_NAME = t.table_name AND b.partition_ordinal_position = t.max_pt ;										
											
    DECLARE ptInfoSubCursor CURSOR FOR SELECT DATE_SUB(DATE(NOW()), interval hourRetentionPeriod hour) AS min_partition_value,
												       v_min_partition_description AS table_min_partition_value, 
														 TIMESTAMPDIFF(hour,DATE_FORMAT(concat(REPLACE(v_min_partition_description, '\'', ''),'00'),'%Y%m%d%H%i%s'), DATE_SUB(DATE(NOW()), interval hourRetentionPeriod hour)) AS num_drop_pt, 
														 v_max_partition_description AS table_max_partition_value, 
														 TIMESTAMPDIFF(hour,DATE_FORMAT(concat(REPLACE(v_max_partition_description, '\'', ''),'00'),'%Y%m%d%H%i%s'), DATE_ADD(DATE(NOW()), interval 48 hour)) AS num_add_pt ;														 
															 											
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
	
	OPEN ptInfoCursor; 
	my_loop: LOOP
	
	  -- loop 하며 ptInfoCursor 의 데이터를 불러와 변수에 넣는다.	  
	  FETCH ptInfoCursor 
	   INTO v_table_schema, v_table_name, v_min_pt, v_max_pt, v_min_partition_name, v_min_partition_description, v_max_partition_name, v_max_partition_description;

	    IF done THEN
	      LEAVE my_loop;
	    END IF;
	    

	   set errorMsg = CONCAT(v_table_schema, '|', v_table_name, '|', v_min_pt, '|', v_max_pt, '|', v_min_partition_name, '|', v_min_partition_description, '|', v_max_partition_name, '|', v_max_partition_description);
	   
    	OPEN ptInfoSubCursor; 
	    	FETCH ptInfoSubCursor 
		   INTO v_min_partition_value, v_table_min_partition_value, v_num_drop_pt, v_table_max_partition_value, v_num_add_pt;
		   
       	set errorMsg = CONCAT(v_min_partition_value, '|', v_table_min_partition_value, '|', v_num_drop_pt, '|', v_table_max_partition_value, '|', v_num_add_pt);
    	CLOSE ptInfoSubCursor;

      -- add
      WHILE add_pt <= v_num_add_pt DO
		   SET pt_date = DATE_FORMAT(DATE_ADD(concat(REPLACE(v_max_partition_description, '\'', ''),'00'), INTERVAL add_pt-1 hour),'%Y%m%d%H%i');
		   SET pt_date_less = DATE_FORMAT(DATE_ADD(concat(REPLACE(v_max_partition_description, '\'', ''),'00'), INTERVAL add_pt hour),'%Y%m%d%H%i');		   
		   SET @SQL_QUERY = CONCAT('ALTER TABLE ',tableName , ' ADD PARTITION ( PARTITION ', tablePartitionName, pt_date, ' VALUES LESS THAN(\'' , pt_date_less , '\') engine=innodb)');
			PREPARE stmt FROM @SQL_QUERY;
			EXECUTE stmt;

			SET add_pt = add_pt + 1;	

		END WHILE;
		
      -- drop 
    	WHILE drop_pt <= v_num_drop_pt DO
		   SET pt_date = DATE_FORMAT(DATE_ADD(concat(REPLACE(v_min_partition_description, '\'', ''),'00'), INTERVAL drop_pt-2 hour),'%Y%m%d%H%i');
		   SET @SQL_QUERY = CONCAT('ALTER TABLE ',tableName , ' DROP PARTITION ', tablePartitionName, pt_date);
		   PREPARE stmt FROM @SQL_QUERY;
			EXECUTE stmt;

			SET drop_pt = drop_pt + 1;	

		END WHILE;
		
	END LOOP;  
   CLOSE ptInfoCursor;
END//
DELIMITER ;

-- 프로시저 app_01.SP_MANAGE_MONTH_PARTITIONS 구조 내보내기
DELIMITER //
CREATE PROCEDURE `SP_MANAGE_MONTH_PARTITIONS`(
	IN `appNo` VARCHAR(25),
	IN `tableName` VARCHAR(100),
	IN `tablePartitionName` VARCHAR(100),
	OUT `errorMsg` VARCHAR(2000)
)
BEGIN
	DECLARE monthRetentionPeriod INT DEFAULT 48;	
   DECLARE done INT DEFAULT FALSE;												
   DECLARE v_table_schema varchar(20);
   DECLARE v_table_name varchar(100);
   DECLARE v_min_pt INT;
   DECLARE v_max_pt INT;
   
   DECLARE v_min_partition_name VARCHAR(100);   
   DECLARE v_min_partition_description VARCHAR(100);  	 
   DECLARE v_max_partition_name VARCHAR(100);
   DECLARE v_max_partition_description VARCHAR(100);   
	
	DECLARE v_min_partition_value varchar(20);
   DECLARE v_table_min_partition_value varchar(100);
   DECLARE v_num_drop_pt INT;
   DECLARE v_table_max_partition_value VARCHAR(100);
   DECLARE v_num_add_pt VARCHAR(100);   
   
   DECLARE pt_date VARCHAR(20) DEFAULT '';
   DECLARE pt_date_less VARCHAR(20) DEFAULT '';
   DECLARE add_pt INT DEFAULT 1;
   DECLARE drop_pt INT DEFAULT 1;
   
   
  	DECLARE ptInfoCursor CURSOR FOR SELECT t.* , a.partition_name AS min_partition_name, 
	                                     a.partition_description AS min_partition_description,
											       b.partition_name AS max_partition_name, 
													 b.partition_description AS max_partition_description
											FROM (
												SELECT table_schema, 
												       TABLE_NAME, 
												       MIN(partition_ordinal_position) as min_pt, 
														 MAX(partition_ordinal_position) AS max_pt
												FROM information_schema.PARTITIONS
												WHERE table_schema= appNo
											     AND TABLE_NAME = tableName
											     AND partition_method = 'RANGE COLUMNS'
												GROUP BY table_schema, TABLE_NAME
											) t INNER JOIN information_schema.PARTITIONS a ON a.table_schema = t.table_schema AND a.TABLE_NAME = t.table_name AND a.partition_ordinal_position = t.min_pt
											INNER JOIN information_schema.PARTITIONS b ON b.table_schema = t.table_schema AND b.TABLE_NAME = t.table_name AND b.partition_ordinal_position = t.max_pt ;										
							
    DECLARE ptInfoSubCursor CURSOR FOR SELECT 
	 											       DATE_SUB(DATE(NOW()), interval monthRetentionPeriod MONTH) AS min_partition_value,
 														 DATE_FORMAT(REPLACE(v_min_partition_description, '\'', ''),'%Y%m%d') AS table_min_partition_value,
														 TIMESTAMPDIFF(MONTH ,DATE_FORMAT(REPLACE(v_min_partition_description, '\'', ''),'%Y%m%d'), DATE_SUB(DATE(NOW()), interval monthRetentionPeriod MONTH)) AS num_drop_pt, 
														 DATE_FORMAT(REPLACE(v_max_partition_description, '\'', ''),'%Y%m') AS table_max_partition_value, 
														 TIMESTAMPDIFF(MONTH, DATE_FORMAT(REPLACE(v_max_partition_description, '\'', ''),'%Y%m%d'), DATE_ADD(DATE(NOW()), interval 3 Month)) AS num_add_pt ;
											 											
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
	

	OPEN ptInfoCursor; 
	my_loop: LOOP	   
	  -- loop 하며 ptInfoCursor 의 데이터를 불러와 변수에 넣는다.	  
	  FETCH ptInfoCursor 
	   INTO v_table_schema, v_table_name, v_min_pt, v_max_pt, v_min_partition_name, v_min_partition_description, v_max_partition_name, v_max_partition_description;

	    IF done THEN
	      LEAVE my_loop;
	    END IF;
	    

	   set errorMsg = CONCAT(v_table_schema, '|', v_table_name, '|', v_min_pt, '|', v_max_pt, '|', v_min_partition_name, '|', v_min_partition_description, '|', v_max_partition_name, '|', v_max_partition_description);
	   
    	OPEN ptInfoSubCursor; 
	    	FETCH ptInfoSubCursor 
		   INTO v_min_partition_value, v_table_min_partition_value, v_num_drop_pt, v_table_max_partition_value, v_num_add_pt;
		   
       	set errorMsg = CONCAT(v_min_partition_value, '|', v_table_min_partition_value, '|', v_num_drop_pt, '|', v_table_max_partition_value, '|', v_num_add_pt);
    	CLOSE ptInfoSubCursor;

      -- add
      WHILE add_pt <= v_num_add_pt DO
		   SET pt_date = DATE_FORMAT(DATE_ADD(REPLACE(v_max_partition_description, '\'', ''), INTERVAL add_pt-1 MONTH),'%Y%m%d');
		   SET pt_date_less = DATE_FORMAT(DATE_ADD(REPLACE(v_max_partition_description, '\'', ''), INTERVAL add_pt MONTH),'%Y%m%d');		   
		   SET @SQL_QUERY = CONCAT('ALTER TABLE ',tableName , ' ADD PARTITION ( PARTITION ', tablePartitionName, pt_date, ' VALUES LESS THAN(\'' , pt_date_less , '\') engine=innodb)');
		   
			PREPARE stmt FROM @SQL_QUERY;
			EXECUTE stmt;

			SET add_pt = add_pt + 1;	

		END WHILE;
		
      -- drop 
    	WHILE drop_pt <= v_num_drop_pt DO
		   SET pt_date = DATE_FORMAT(DATE_ADD(REPLACE(v_min_partition_description, '\'', ''), INTERVAL drop_pt-2 MONTH),'%Y%m%d');
		   SET @SQL_QUERY = CONCAT('ALTER TABLE ',tableName , ' DROP PARTITION ', tablePartitionName, pt_date);
		   PREPARE stmt FROM @SQL_QUERY;
			EXECUTE stmt;

			SET drop_pt = drop_pt + 1;	

		END WHILE;
		
	END LOOP;  
   CLOSE ptInfoCursor;
END//
DELIMITER ;

-- 프로시저 app_01.SP_MANAGE_YEAR_PARTITIONS 구조 내보내기
DELIMITER //
CREATE PROCEDURE `SP_MANAGE_YEAR_PARTITIONS`(
	IN `appNo` VARCHAR(25),
	IN `tableName` VARCHAR(100),
	IN `tablePartitionName` VARCHAR(100),
	OUT `errorMsg` VARCHAR(2000)
)
BEGIN
	DECLARE YEARRetentionPeriod INT DEFAULT 3;	
   DECLARE done INT DEFAULT FALSE;
   DECLARE v_table_schema varchar(20);
   DECLARE v_table_name varchar(100);
   DECLARE v_min_pt INT;
   DECLARE v_max_pt INT;
   
   DECLARE v_min_partition_name VARCHAR(100);   
   DECLARE v_min_partition_description VARCHAR(100);  	 
   DECLARE v_max_partition_name VARCHAR(100);
   DECLARE v_max_partition_description VARCHAR(100);   
	
	DECLARE v_min_partition_value varchar(20);
   DECLARE v_table_min_partition_value varchar(100);
   DECLARE v_num_drop_pt INT;
   DECLARE v_table_max_partition_value VARCHAR(100);
   DECLARE v_num_add_pt VARCHAR(100);   
   
   DECLARE pt_date VARCHAR(20) DEFAULT '';
   DECLARE pt_date_less VARCHAR(20) DEFAULT '';
   DECLARE add_pt INT DEFAULT 1;
   DECLARE drop_pt INT DEFAULT 1;
   
   
  	DECLARE ptInfoCursor CURSOR FOR SELECT t.* , a.partition_name AS min_partition_name, 
	                                     a.partition_description AS min_partition_description,
											       b.partition_name AS max_partition_name, 
													 b.partition_description AS max_partition_description
											FROM (
												SELECT table_schema, 
												       TABLE_NAME, 
												       MIN(partition_ordinal_position) as min_pt, 
														 MAX(partition_ordinal_position) AS max_pt
												FROM information_schema.PARTITIONS
												WHERE table_schema= appNo
											     AND TABLE_NAME = tableName
											     AND partition_method = 'RANGE COLUMNS'
												GROUP BY table_schema, TABLE_NAME
											) t INNER JOIN information_schema.PARTITIONS a ON a.table_schema = t.table_schema AND a.TABLE_NAME = t.table_name AND a.partition_ordinal_position = t.min_pt
											INNER JOIN information_schema.PARTITIONS b ON b.table_schema = t.table_schema AND b.TABLE_NAME = t.table_name AND b.partition_ordinal_position = t.max_pt ;										
							
    DECLARE ptInfoSubCursor CURSOR FOR SELECT 
	 											       DATE_SUB(DATE(NOW()), interval YEARRetentionPeriod YEAR) AS min_partition_value,
 														 DATE_FORMAT(REPLACE(v_min_partition_description, '\'', ''),'%Y%m%d') AS table_min_partition_value,
														 TIMESTAMPDIFF(YEAR ,DATE_FORMAT(REPLACE(v_min_partition_description, '\'', ''),'%Y%m%d'), DATE_SUB(DATE(NOW()), interval YEARRetentionPeriod YEAR)) AS num_drop_pt, 
														 DATE_FORMAT(REPLACE(v_max_partition_description, '\'', ''),'%Y%m') AS table_max_partition_value, 
														 TIMESTAMPDIFF(YEAR, DATE_FORMAT(REPLACE(v_max_partition_description, '\'', ''),'%Y%m%d'), DATE_ADD(DATE(NOW()), interval 3 YEAR)) AS num_add_pt ;
											 											
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
	

	OPEN ptInfoCursor; 
	my_loop: LOOP	   
	  -- loop 하며 ptInfoCursor 의 데이터를 불러와 변수에 넣는다.	  
	  FETCH ptInfoCursor 
	   INTO v_table_schema, v_table_name, v_min_pt, v_max_pt, v_min_partition_name, v_min_partition_description, v_max_partition_name, v_max_partition_description;

	    IF done THEN
	      LEAVE my_loop;
	    END IF;
	    

	   set errorMsg = CONCAT(v_table_schema, '|', v_table_name, '|', v_min_pt, '|', v_max_pt, '|', v_min_partition_name, '|', v_min_partition_description, '|', v_max_partition_name, '|', v_max_partition_description);
	   
    	OPEN ptInfoSubCursor; 
	    	FETCH ptInfoSubCursor 
		   INTO v_min_partition_value, v_table_min_partition_value, v_num_drop_pt, v_table_max_partition_value, v_num_add_pt;
		   
       	set errorMsg = CONCAT(v_min_partition_value, '|', v_table_min_partition_value, '|', v_num_drop_pt, '|', v_table_max_partition_value, '|', v_num_add_pt);
    	CLOSE ptInfoSubCursor;

      -- add
      WHILE add_pt <= v_num_add_pt DO
		   SET pt_date = 
		   DATE_FORMAT(DATE_ADD(REPLACE(v_max_partition_description, 
		   '\'', ''), INTERVAL add_pt-1 YEAR),'%Y%m%d');
		   SET pt_date_less = DATE_FORMAT(DATE_ADD(REPLACE(v_max_partition_description, '\'', ''), INTERVAL add_pt YEAR),'%Y%m%d');		   
		   SET @SQL_QUERY = CONCAT('ALTER TABLE ',tableName , ' ADD PARTITION ( PARTITION ', tablePartitionName, pt_date, ' VALUES LESS THAN(\'' , pt_date_less , '\') engine=innodb)');
		   
			PREPARE stmt FROM @SQL_QUERY;
			EXECUTE stmt;

			SET add_pt = add_pt + 1;	

		END WHILE;
		
      -- drop 
    	WHILE drop_pt <= v_num_drop_pt DO
		   SET pt_date = DATE_FORMAT(DATE_ADD(REPLACE(v_min_partition_description, '\'', ''), INTERVAL drop_pt-2 YEAR),'%Y%m%d');
		   SET @SQL_QUERY = CONCAT('ALTER TABLE ',tableName , ' DROP PARTITION ', tablePartitionName, pt_date);
		   PREPARE stmt FROM @SQL_QUERY;
			EXECUTE stmt;

			SET drop_pt = drop_pt + 1;	

		END WHILE;
		
	END LOOP;  
   CLOSE ptInfoCursor;
END//
DELIMITER ;

