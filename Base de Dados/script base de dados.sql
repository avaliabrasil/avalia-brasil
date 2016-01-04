-- --------------------------------------------------------
-- Servidor:                     127.0.0.1
-- Versão do servidor:           5.7.10-log - MySQL Community Server (GPL)
-- OS do Servidor:               Win64
-- HeidiSQL Versão:              9.3.0.4984
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Copiando estrutura do banco de dados para avaliabrasil
CREATE DATABASE IF NOT EXISTS `avaliabrasil` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `avaliabrasil`;


-- Copiando estrutura para tabela avaliabrasil.avaliacao
CREATE TABLE IF NOT EXISTS `avaliacao` (
  `idavaliacao` int(11) NOT NULL AUTO_INCREMENT,
  `datacadastro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `idusuario` int(11) NOT NULL,
  `idinstituicao` int(11) NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '0',
  `idinstrumento` int(11) NOT NULL,
  PRIMARY KEY (`idavaliacao`),
  UNIQUE KEY `idavaliacao_UNIQUE` (`idavaliacao`),
  KEY `idusuario_idx` (`idusuario`),
  KEY `idinstituicao_idx` (`idinstituicao`),
  KEY `idinstrumento_idx` (`idinstrumento`),
  CONSTRAINT `idinstituicao` FOREIGN KEY (`idinstituicao`) REFERENCES `instituicao` (`idinstituicao`) ON UPDATE CASCADE,
  CONSTRAINT `idinstrumento` FOREIGN KEY (`idinstrumento`) REFERENCES `instrumento` (`idinstrumento`) ON UPDATE CASCADE,
  CONSTRAINT `idusuario` FOREIGN KEY (`idusuario`) REFERENCES `usuario` (`idusuario`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.


-- Copiando estrutura para tabela avaliabrasil.categoria
CREATE TABLE IF NOT EXISTS `categoria` (
  `idcategoria` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(45) NOT NULL,
  `datacadastro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) unsigned zerofill NOT NULL DEFAULT '0',
  PRIMARY KEY (`idcategoria`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.


-- Copiando estrutura para tabela avaliabrasil.dimensao-master
CREATE TABLE IF NOT EXISTS `dimensao-master` (
  `iddimensao-master` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(45) NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '0',
  `idinstrumento-master` int(11) NOT NULL,
  `datacadastro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`iddimensao-master`),
  KEY `idinstrumento-master_idx` (`idinstrumento-master`),
  CONSTRAINT `idinstrumento-master-dim` FOREIGN KEY (`idinstrumento-master`) REFERENCES `instrumento-master` (`idinstrumento-master`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.


-- Copiando estrutura para tabela avaliabrasil.estado
CREATE TABLE IF NOT EXISTS `estado` (
  `idestado` int(11) NOT NULL,
  `nome` varchar(45) NOT NULL,
  `idregiao` int(11) NOT NULL,
  `idibge` varchar(45) DEFAULT NULL,
  `datacadastro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sigla` varchar(2) NOT NULL,
  PRIMARY KEY (`idestado`),
  KEY `idregiao_idx` (`idregiao`),
  CONSTRAINT `idregiao` FOREIGN KEY (`idregiao`) REFERENCES `regiao` (`idregiao`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.


-- Copiando estrutura para tabela avaliabrasil.instituicao
CREATE TABLE IF NOT EXISTS `instituicao` (
  `idinstituicao` int(11) NOT NULL AUTO_INCREMENT,
  `idtipoinstituicao` int(11) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `datacadastro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) NOT NULL DEFAULT '0',
  `idmunicipio` int(11) NOT NULL,
  PRIMARY KEY (`idinstituicao`),
  UNIQUE KEY `idinstituicao_UNIQUE` (`idinstituicao`),
  KEY `idtipoinstituicao_idx` (`idtipoinstituicao`),
  KEY `idmunicipio_idx` (`idmunicipio`),
  CONSTRAINT `idtipoinstituicao` FOREIGN KEY (`idtipoinstituicao`) REFERENCES `tipoinstituicao` (`idtipoinstituicao`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.


-- Copiando estrutura para tabela avaliabrasil.instrumento
CREATE TABLE IF NOT EXISTS `instrumento` (
  `idinstrumento` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(45) NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '0',
  `idinstrumento-master` int(11) NOT NULL,
  PRIMARY KEY (`idinstrumento`),
  UNIQUE KEY `idinstrumento_UNIQUE` (`idinstrumento`),
  KEY `idinstrumento-master_idx` (`idinstrumento-master`),
  CONSTRAINT `idinstrumento-master` FOREIGN KEY (`idinstrumento-master`) REFERENCES `instrumento-master` (`idinstrumento-master`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.


-- Copiando estrutura para tabela avaliabrasil.instrumento-master
CREATE TABLE IF NOT EXISTS `instrumento-master` (
  `idinstrumento-master` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(45) NOT NULL,
  `datacadastro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idinstrumento-master`),
  UNIQUE KEY `idinstrumento-master_UNIQUE` (`idinstrumento-master`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.


-- Copiando estrutura para tabela avaliabrasil.municipio
CREATE TABLE IF NOT EXISTS `municipio` (
  `idmunicipio` int(11) NOT NULL,
  `nome` varchar(45) NOT NULL,
  `idestado` int(11) NOT NULL,
  `idibge` varchar(45) DEFAULT NULL,
  `datacadastro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idmunicipio`),
  UNIQUE KEY `idmunicipio_UNIQUE` (`idmunicipio`),
  KEY `idestado_idx` (`idestado`),
  CONSTRAINT `idestado` FOREIGN KEY (`idestado`) REFERENCES `estado` (`idestado`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.


-- Copiando estrutura para tabela avaliabrasil.pergunta
CREATE TABLE IF NOT EXISTS `pergunta` (
  `idpergunta` int(11) NOT NULL AUTO_INCREMENT,
  `iddimensao-master` int(11) NOT NULL,
  `idinstrumento` int(11) NOT NULL,
  `pergunta` varchar(100) NOT NULL,
  `idtipopergunta` int(11) NOT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idpergunta`),
  UNIQUE KEY `idpergunta_UNIQUE` (`idpergunta`),
  KEY `idtipopergunta_idx` (`idtipopergunta`),
  KEY `iddimensao-master_idx` (`iddimensao-master`),
  KEY `idinstrumento_idx` (`idinstrumento`),
  CONSTRAINT `iddimensao-master-perg` FOREIGN KEY (`iddimensao-master`) REFERENCES `dimensao-master` (`iddimensao-master`) ON UPDATE CASCADE,
  CONSTRAINT `idinstrumento-perg` FOREIGN KEY (`idinstrumento`) REFERENCES `instrumento` (`idinstrumento`) ON UPDATE CASCADE,
  CONSTRAINT `idtipopergunta-perg` FOREIGN KEY (`idtipopergunta`) REFERENCES `tipopergunta` (`idtipopergunta`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.


-- Copiando estrutura para tabela avaliabrasil.regiao
CREATE TABLE IF NOT EXISTS `regiao` (
  `idregiao` int(11) NOT NULL,
  `nome` varchar(45) NOT NULL,
  `datacadastro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `idibge` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idregiao`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.


-- Copiando estrutura para tabela avaliabrasil.resposta
CREATE TABLE IF NOT EXISTS `resposta` (
  `idresposta` int(11) NOT NULL AUTO_INCREMENT,
  `idavaliacao` int(11) NOT NULL,
  `idpergunta` int(11) NOT NULL,
  `respostalikert` tinyint(1) DEFAULT NULL,
  `respostatexto` varchar(200) DEFAULT NULL,
  `respostanumero` decimal(10,0) DEFAULT NULL,
  PRIMARY KEY (`idresposta`),
  UNIQUE KEY `idresposta_UNIQUE` (`idresposta`),
  KEY `idavaliacao_idx` (`idavaliacao`),
  KEY `idpergunta_idx` (`idpergunta`),
  CONSTRAINT `idavaliacao-resp` FOREIGN KEY (`idavaliacao`) REFERENCES `avaliacao` (`idavaliacao`) ON UPDATE CASCADE,
  CONSTRAINT `idpergunta-resp` FOREIGN KEY (`idpergunta`) REFERENCES `pergunta` (`idpergunta`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.


-- Copiando estrutura para tabela avaliabrasil.tipoinstituicao
CREATE TABLE IF NOT EXISTS `tipoinstituicao` (
  `idtipoinstituicao` int(11) NOT NULL AUTO_INCREMENT,
  `idcategoria` int(11) NOT NULL,
  `nome` varchar(45) DEFAULT NULL,
  `datacadastro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idtipoinstituicao`),
  KEY `idcategoria_idx` (`idcategoria`),
  CONSTRAINT `idcategoria` FOREIGN KEY (`idcategoria`) REFERENCES `categoria` (`idcategoria`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.


-- Copiando estrutura para tabela avaliabrasil.tipopergunta
CREATE TABLE IF NOT EXISTS `tipopergunta` (
  `idtipopergunta` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(45) NOT NULL,
  `islikert` tinyint(1) NOT NULL,
  `isnumeric` tinyint(1) NOT NULL,
  `iscomment` tinyint(1) NOT NULL,
  PRIMARY KEY (`idtipopergunta`),
  UNIQUE KEY `idtipopergunta_UNIQUE` (`idtipopergunta`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.


-- Copiando estrutura para tabela avaliabrasil.usuario
CREATE TABLE IF NOT EXISTS `usuario` (
  `idusuario` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) DEFAULT NULL,
  `datacadastro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` int(11) NOT NULL DEFAULT '0',
  `tipousuario` int(11) NOT NULL DEFAULT '0',
  `email` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`idusuario`),
  UNIQUE KEY `idusuario_UNIQUE` (`idusuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
