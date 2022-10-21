
BEGIN;
CREATE SCHEMA IF NOT EXISTS "ops1";


CREATE TABLE "ops1"."environment"( 
	"environmentid" varchar(20) NOT NULL,
	"name" varchar NOT NULL,
	"description" varchar,
	"sortorder" varchar(10));

CREATE TABLE "ops1"."idaprole"( 
	"idaproleid" varchar(20) NOT NULL,
	"description" varchar(2000));

CREATE TABLE "ops1"."idapuser"( 
	"idapuserid" varchar(20) NOT NULL,
	"firstname" varchar(100),
	"lastname" varchar(100));

CREATE TABLE "ops1"."profile"( 
	"profileid" varchar(20) NOT NULL,
	"name" varchar NOT NULL,
	"description" varchar,
	"sortorder" varchar(10),
	"environmentid" varchar(20));

CREATE TABLE "ops1"."property"( 
	"propertyid" int NOT NULL,
	"profileid" varchar(20),
	"propertykey" varchar(800) NOT NULL,
	"propertyvalue" varchar NOT NULL,
	"info" varchar,
	"dtstart" timestamp NOT NULL,
	"dtend" timestamp NOT NULL,
	"dtcreated" timestamp,
	"dtupdated" timestamp);

CREATE TABLE "ops1"."userrole"( 
	"userroleid" int NOT NULL,
	"idapuserid" varchar(20),
	"idaproleid" varchar(20),
	"profileid" varchar(20),
	"dtstart" timestamp NOT NULL,
	"dtend" timestamp NOT NULL,
	"dtlastloggedin" timestamp);

COMMIT;
