CREATE TABLE LEOS_USER (
    USER_LOGIN              VARCHAR2(50 CHAR),
    USER_PER_ID             NUMBER(8,0) NOT NULL AUTO_INCREMENT,
    USER_LASTNAME           VARCHAR2(50 CHAR),
    USER_FIRSTNAME          VARCHAR2(50 CHAR),
    USER_EMAIL              VARCHAR2(320 CHAR),
    CONSTRAINT PK_LEOS_USER PRIMARY KEY(USER_LOGIN));

CREATE TABLE LEOS_ENTITY (
    ENTITY_ID               VARCHAR2(20 CHAR),
    ENTITY_NAME             VARCHAR2(50 CHAR) NOT NULL,
    ENTITY_PARENT_ID        VARCHAR2(20 CHAR),
    ENTITY_ORG_NAME         VARCHAR2(4000 CHAR) NOT NULL,
    CONSTRAINT PK_LEOS_ENTITY PRIMARY KEY(ENTITY_ID));

ALTER TABLE LEOS_ENTITY ADD CONSTRAINT FK_ENTITY_PARENT FOREIGN KEY (ENTITY_PARENT_ID)
    REFERENCES LEOS_ENTITY(ENTITY_ID) ON DELETE CASCADE;

CREATE TABLE LEOS_USER_ENTITY
   (USER_LOGIN VARCHAR2(50 CHAR),
    ENTITY_ID VARCHAR2(20 CHAR),
    CONSTRAINT PK_LEOS_USER_ENTITY PRIMARY KEY(USER_LOGIN,ENTITY_ID));

ALTER TABLE LEOS_USER_ENTITY ADD CONSTRAINT FK_LEOS_USER_ENTITY_LEOS_USER FOREIGN KEY (USER_LOGIN)
     REFERENCES LEOS_USER (USER_LOGIN) ON DELETE CASCADE;

ALTER TABLE LEOS_USER_ENTITY ADD CONSTRAINT FK_LEOS_USER_ENTITY_LEOS_ENTITY FOREIGN KEY (ENTITY_ID)
    REFERENCES LEOS_ENTITY (ENTITY_ID) ON DELETE CASCADE;

CREATE TABLE LEOS_ROLE
   (ROLE_NAME VARCHAR2(50 CHAR), 
    ROLE_DESC VARCHAR2(50 CHAR),
    CONSTRAINT PK_ROLE PRIMARY KEY (ROLE_NAME));

CREATE TABLE LEOS_USER_ROLE
   (USER_LOGIN VARCHAR2(50 CHAR),
    ROLE_NAME VARCHAR2(50 CHAR),
    CONSTRAINT PK_LEOS_USER_ROLE PRIMARY KEY(USER_LOGIN,ROLE_NAME));

ALTER TABLE LEOS_USER_ROLE ADD CONSTRAINT FK_LEOS_USER_ROLE_LEOS_USER FOREIGN KEY (USER_LOGIN)
     REFERENCES LEOS_USER (USER_LOGIN) ON DELETE CASCADE;

ALTER TABLE LEOS_USER_ROLE ADD CONSTRAINT FK_LEOS_USER_ROLE_LEOS_ROLE FOREIGN KEY (ROLE_NAME)
    REFERENCES LEOS_ROLE (ROLE_NAME) ON DELETE CASCADE;

CREATE ALIAS deAccent AS '
  String deAccent(String value) throws Exception{
      return value.toUpperCase();
  }
';
