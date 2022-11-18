-- 2022-11-18 22:18:11.3030
ALTER TABLE "public"."apk" ADD COLUMN "min_sdk_version" varchar(10) NOT NULL;
ALTER TABLE "public"."apk" ADD COLUMN "min_os_version" varchar(10) NOT NULL;
ALTER TABLE "public"."apk" ADD COLUMN "target_sdk_version" varchar(10) NOT NULL;
ALTER TABLE "public"."apk" ADD COLUMN "wear" bool NOT NULL;
ALTER TABLE "public"."apk" ADD COLUMN "tv" bool NOT NULL;
ALTER TABLE "public"."apk" ADD COLUMN "automotive" bool NOT NULL;
ALTER TABLE "public"."apk" ADD COLUMN "device_type" varchar(20) NOT NULL;
ALTER TABLE "public"."apk" ADD COLUMN "use_features" json;
ALTER TABLE "public"."apk" ADD COLUMN "use_permissions" json;
ALTER TABLE "public"."apk" ADD COLUMN "deep_links" json;
ALTER TABLE "public"."apk" ADD COLUMN "schemes" json;
