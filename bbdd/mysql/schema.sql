-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`usuario` (
  `idUsuario` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `apellidos` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `fechaNacimiento` VARCHAR(10) NOT NULL,
  `sexo` INT NULL,
  `peso` DOUBLE NULL,
  `DNI` VARCHAR(9) NOT NULL,
  `secret` VARCHAR(45) NOT NULL,
  `salt` VARCHAR(45) NOT NULL,
  `estado` INT NOT NULL,
  PRIMARY KEY (`idUsuario`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC),
  UNIQUE INDEX `DNI_UNIQUE` (`DNI` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`enfermedad`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`enfermedad` (
  `idEnfermedad` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL,
  `erradicada` TINYINT(1) NULL,
  `numMuertes` INT NULL,
  `numNinyos` INT NULL,
  `numAdultos` INT NULL,
  `numAncianos` INT NULL,
  `numMujeres` INT NULL,
  `numHombres` INT NULL,
  `peso` INT NULL,
  PRIMARY KEY (`idEnfermedad`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`medico`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`medico` (
  `idMedico` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `apellidos` VARCHAR(45) NOT NULL,
  `hospital` VARCHAR(45) NOT NULL,
  `departamento` VARCHAR(45) NULL,
  `secret` VARCHAR(45) NOT NULL,
  `salt` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idMedico`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`contagio`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`contagio` (
  `idContagio` INT NOT NULL AUTO_INCREMENT,
  `idEnfermedad` INT NOT NULL,
  `idMedico` INT NOT NULL,
  `tiempo` INT NOT NULL,
  `distancia` INT NOT NULL,
  `fecha` VARCHAR(10) NOT NULL,
  `nivel` INT NOT NULL,
  `descripcion` VARCHAR(140) NOT NULL,
  `zona` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idContagio`),
  INDEX `fk_contagio_enfermedad1_idx` (`idEnfermedad` ASC),
  INDEX `fk_contagio_medico1_idx` (`idMedico` ASC),
  CONSTRAINT `fk_contagio_enfermedad1`
    FOREIGN KEY (`idEnfermedad`)
    REFERENCES `mydb`.`enfermedad` (`idEnfermedad`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_contagio_medico1`
    FOREIGN KEY (`idMedico`)
    REFERENCES `mydb`.`medico` (`idMedico`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`notificacion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`notificacion` (
  `idUsuario` INT NOT NULL,
  `idContagio` INT NOT NULL,
  `fecha` VARCHAR(10) NOT NULL,
  `confirmado` TINYINT(1) NOT NULL,
  PRIMARY KEY (`idUsuario`, `idContagio`),
  INDEX `fk_usuario_has_contagios_contagios1_idx` (`idContagio` ASC),
  INDEX `fk_usuario_has_contagios_usuario_idx` (`idUsuario` ASC),
  CONSTRAINT `fk_usuario_has_contagios_usuario`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `mydb`.`usuario` (`idUsuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_usuario_has_contagios_contagios1`
    FOREIGN KEY (`idContagio`)
    REFERENCES `mydb`.`contagio` (`idContagio`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`usuarioContagiado`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`usuarioContagiado` (
  `idUsuario` INT NOT NULL,
  `idContagio` INT NOT NULL,
  PRIMARY KEY (`idUsuario`, `idContagio`),
  INDEX `fk_usuario_has_contagios_contagios2_idx` (`idContagio` ASC),
  INDEX `fk_usuario_has_contagios_usuario1_idx` (`idUsuario` ASC),
  CONSTRAINT `fk_usuario_has_contagios_usuario1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `mydb`.`usuario` (`idUsuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_usuario_has_contagios_contagios2`
    FOREIGN KEY (`idContagio`)
    REFERENCES `mydb`.`contagio` (`idContagio`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`foco`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`foco` (
  `idFoco` INT NOT NULL AUTO_INCREMENT,
  `descripcion` VARCHAR(45) NOT NULL,
  `numPersonas` INT NOT NULL,
  `idMedico` INT NOT NULL,
  PRIMARY KEY (`idFoco`),
  INDEX `fk_foco_medico1_idx` (`idMedico` ASC),
  CONSTRAINT `fk_foco_medico1`
    FOREIGN KEY (`idMedico`)
    REFERENCES `mydb`.`medico` (`idMedico`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`usuarioEnFoco`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`usuarioEnFoco` (
  `idUsuario` INT NOT NULL,
  `idFoco` INT NOT NULL,
  PRIMARY KEY (`idUsuario`, `idFoco`),
  INDEX `fk_usuario_has_foco_foco1_idx` (`idFoco` ASC),
  INDEX `fk_usuario_has_foco_usuario1_idx` (`idUsuario` ASC),
  CONSTRAINT `fk_usuario_has_foco_usuario1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `mydb`.`usuario` (`idUsuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_usuario_has_foco_foco1`
    FOREIGN KEY (`idFoco`)
    REFERENCES `mydb`.`foco` (`idFoco`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`lugaresFoco`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`lugaresFoco` (
  `idFoco` INT NOT NULL,
  `lugar` VARCHAR(45) NOT NULL,
  `fecha` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`lugar`, `idFoco`, `fecha`),
  INDEX `fk_lugaresFoco_foco1_idx` (`idFoco` ASC),
  CONSTRAINT `fk_lugaresFoco_foco1`
    FOREIGN KEY (`idFoco`)
    REFERENCES `mydb`.`foco` (`idFoco`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`noticia`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`noticia` (
  `idNoticia` INT NOT NULL AUTO_INCREMENT,
  `descripcion` VARCHAR(45) NOT NULL,
  `idContagio` INT NOT NULL,
  PRIMARY KEY (`idNoticia`),
  INDEX `fk_noticia_contagio1_idx` (`idContagio` ASC),
  CONSTRAINT `fk_noticia_contagio1`
    FOREIGN KEY (`idContagio`)
    REFERENCES `mydb`.`contagio` (`idContagio`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`noticiasPorUsuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`noticiasPorUsuario` (
  `idNoticia` INT NOT NULL,
  `idUsuario` INT NOT NULL,
  PRIMARY KEY (`idNoticia`, `idUsuario`),
  INDEX `fk_noticia_has_usuario_usuario1_idx` (`idUsuario` ASC),
  INDEX `fk_noticia_has_usuario_noticia1_idx` (`idNoticia` ASC),
  CONSTRAINT `fk_noticia_has_usuario_noticia1`
    FOREIGN KEY (`idNoticia`)
    REFERENCES `mydb`.`noticia` (`idNoticia`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_noticia_has_usuario_usuario1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `mydb`.`usuario` (`idUsuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
