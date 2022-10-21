\set ON_ERROR_STOP
\set ECHO all
BEGIN;
CREATE SCHEMA IF NOT EXISTS "norm2_ops";


CREATE TABLE "norm2_ops"."maxdfeedback"( 
	"feedbackid" int NOT NULL,
	"issuetype" varchar(255),
	"category" varchar(255),
	"name" varchar(255),
	"email" varchar(255),
	"phone" varchar(255),
	"message" varchar(255),
	"userid" varchar(50) NOT NULL,
	"dtcreated" timestamp);

CREATE TABLE "norm2_ops"."maxdsaccuracytestcases"( 
	"company_name" varchar(255),
	"year" varchar(255),
	"term_name" varchar(255),
	"expected_value" varchar(255),
	"actual_value" varchar(255),
	"test_status" varchar(255),
	"last_run" varchar(255),
	"comments" varchar(50) NOT NULL);

CREATE TABLE "norm2_ops"."maxdspreferences"( 
	"preferenceid" int NOT NULL,
	"companyname" varchar,
	"termname" varchar(255),
	"code" varchar(255),
	"preferencename" varchar(255),
	"results_link" varchar(255),
	"validation_status" varchar(255),
	"research_link" varchar(255),
	"userid" varchar(50) NOT NULL,
	"isquaterly" varchar(255));

CREATE TABLE "norm2_ops"."maxdspreferences46"( 
	"preferenceid" int NOT NULL,
	"companyname" varchar(900),
	"termname" varchar(255),
	"code" varchar(255),
	"preferencename" varchar(255),
	"results_link" varchar(255),
	"validation_status" varchar(255),
	"research_link" varchar(255),
	"userid" varchar(50) NOT NULL,
	"isquaterly" varchar(255),
	"fsqv_link" varchar(255),
	"dt" timestamp);

CREATE TABLE "norm2_ops"."maxdsprofiletestcases"( 
	"company_name" varchar(255),
	"term_name" varchar(255),
	"expected_value" varchar(255),
	"actual_value" varchar(255),
	"test_status" varchar(255),
	"last_run" varchar(255),
	"comments" varchar(50) NOT NULL,
	"year" char(10),
	"expression_name" char(255));

CREATE TABLE "norm2_ops"."maxdssharedpreferences"( 
	"preferenceid" int NOT NULL,
	"companyname" varchar,
	"termname" varchar(255),
	"code" varchar(255),
	"preferencename" varchar(255),
	"results_link" varchar(255),
	"validation_status" varchar(255),
	"research_link" varchar(255),
	"userid" varchar(50) NOT NULL,
	"isquaterly" varchar(255));

CREATE TABLE "norm2_ops"."maxdssharedpreferences46"( 
	"preferenceid" int NOT NULL,
	"companyname" varchar,
	"termname" varchar(255),
	"code" varchar(255),
	"preferencename" varchar(255),
	"results_link" varchar(255),
	"validation_status" varchar(255),
	"research_link" varchar(255),
	"userid" varchar(50) NOT NULL,
	"isquaterly" varchar(255));

CREATE TABLE "norm2_ops"."maxdsfeedback"( 
	"id" int NOT NULL,
	"created" timestamp,
	"typefeedback" varchar(60),
	"category" varchar(60),
	"message" varchar,
	"ip" varchar(48),
	"status" varchar(60),
	"username" varchar(50),
	"useremail" varchar(30),
	"userphonenumber" varchar(30));

COMMIT;
