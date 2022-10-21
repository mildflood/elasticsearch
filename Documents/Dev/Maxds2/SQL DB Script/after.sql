
BEGIN;
CREATE SEQUENCE "ops1"."property_propertyid_seq" INCREMENT BY 1 START WITH 1 OWNED BY "ops1"."property"."propertyid";
CREATE SEQUENCE "ops1"."userrole_userroleid_seq" INCREMENT BY 1 START WITH 1 OWNED BY "ops1"."userrole"."userroleid";
ALTER TABLE "ops1"."environment" ADD PRIMARY KEY ("environmentid");
ALTER TABLE "ops1"."idaprole" ADD PRIMARY KEY ("idaproleid");
ALTER TABLE "ops1"."idapuser" ADD PRIMARY KEY ("idapuserid");
ALTER TABLE "ops1"."profile" ADD PRIMARY KEY ("profileid");
ALTER TABLE "ops1"."property" ADD PRIMARY KEY ("propertyid");
ALTER TABLE "ops1"."userrole" ADD PRIMARY KEY ("userroleid");
ALTER TABLE "ops1"."property" ADD CONSTRAINT "uq_profileid_propertykey" UNIQUE ("profileid","propertykey");
ALTER TABLE "ops1"."profile" ADD FOREIGN KEY ("environmentid") REFERENCES "ops1"."environment" ( "environmentid");
ALTER TABLE "ops1"."property" ADD FOREIGN KEY ("profileid") REFERENCES "ops1"."profile" ( "profileid");
ALTER TABLE "ops1"."userrole" ADD FOREIGN KEY ("idaproleid") REFERENCES "ops1"."idaprole" ( "idaproleid");
ALTER TABLE "ops1"."userrole" ADD FOREIGN KEY ("idaproleid") REFERENCES "ops1"."idaprole" ( "idaproleid");
ALTER TABLE "ops1"."userrole" ADD FOREIGN KEY ("idapuserid") REFERENCES "ops1"."idapuser" ( "idapuserid");
ALTER TABLE "ops1"."userrole" ADD FOREIGN KEY ("idapuserid") REFERENCES "ops1"."idapuser" ( "idapuserid");
ALTER TABLE "ops1"."userrole" ADD FOREIGN KEY ("profileid") REFERENCES "ops1"."profile" ( "profileid");
ALTER TABLE "ops1"."property" ALTER COLUMN "propertyid" SET DEFAULT nextval('"ops1"."property_propertyid_seq"');
ALTER TABLE "ops1"."userrole" ALTER COLUMN "userroleid" SET DEFAULT nextval('"ops1"."userrole_userroleid_seq"');
COMMIT;
