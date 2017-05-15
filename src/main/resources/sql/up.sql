CREATE SCHEMA `24mccmicro` ;

CREATE TABLE `24mccmicro`.`beneficiary` (
  `id` VARCHAR(50) NOT NULL,
  `bank_account_holder_name` VARCHAR(75) NULL,
  `name` VARCHAR(125) NULL,
  `id_24money` VARCHAR(50) NULL,
  `currency` VARCHAR(3) NULL,
  `payment_type` VARCHAR(8) NULL,
  `account_type` VARCHAR(10) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC));

CREATE TABLE `24mccmicro`.`payment` (
  `id` VARCHAR(40) NOT NULL,
  `quick_pay` TINYINT NULL,
  `short_reference` VARCHAR(20) NULL,
  `conversion_id` VARCHAR(40) NULL,
  `amount` INT NULL,
  `currency` VARCHAR(3) NULL,
  `reference` VARCHAR(256) NULL,
  `payment_date` DATETIME NULL,
  `status` VARCHAR(15) NULL,
  `beneficiary_id` VARCHAR(40) NULL,
  PRIMARY KEY (`id`));
