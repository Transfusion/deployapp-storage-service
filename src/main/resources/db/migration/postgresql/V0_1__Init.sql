-- Table Definition
CREATE TABLE "public"."apk" (
    "id" uuid NOT NULL,
    PRIMARY KEY ("id")
);

-- This script only contains the table creation statements and does not fully represent the table in the database. It's still missing: indices, triggers. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."app_binary" (
    "id" uuid NOT NULL,
    "version" text NOT NULL,
    "build" text NOT NULL,
    "upload_date" timestamp NOT NULL,
    "name" text NOT NULL,
    "last_install_date" timestamp,
    "identifier" text NOT NULL,
    "assets_on_front_page" bool NOT NULL DEFAULT false,
    "size_bytes" numeric NOT NULL,
    "file_name" varchar(50) NOT NULL,
    "storage_credential" uuid,
    "user_id" uuid,
    "organization_id" uuid,
    "description" text,
    PRIMARY KEY ("id")
);

-- This script only contains the table creation statements and does not fully represent the table in the database. It's still missing: indices, triggers. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."app_binary_assets" (
    "app_binary_id" uuid NOT NULL,
    "type" varchar(50) NOT NULL,
    "value" json,
    "status" varchar(15),
    "id" uuid NOT NULL,
    "file_name" varchar(100),
    "description" varchar(255),
    PRIMARY KEY ("id")
);

-- This script only contains the table creation statements and does not fully represent the table in the database. It's still missing: indices, triggers. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."app_binary_downloads" (
    "app_binary_id" uuid NOT NULL,
    "timestamp" timestamp NOT NULL DEFAULT now(),
    "ip" text NOT NULL,
    "ua" text NOT NULL,
    "os" varchar(10) NOT NULL,
    "version" varchar(10) NOT NULL,
    PRIMARY KEY ("app_binary_id")
);

-- This script only contains the table creation statements and does not fully represent the table in the database. It's still missing: indices, triggers. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."ipa" (
    "id" uuid NOT NULL,
    "min_sdk_version" varchar(10) NOT NULL,
    "iphone" bool NOT NULL,
    "ipad" bool NOT NULL,
    "universal" bool NOT NULL,
    "device_type" varchar(10),
    "archs" json NOT NULL,
    "display_name" text,
    "release_type" varchar(15),
    "build_type" varchar(15),
    "devices" json,
    "team_name" text,
    "expired_date" timestamp,
    "plist_json" json NOT NULL,
    PRIMARY KEY ("id")
);

-- This script only contains the table creation statements and does not fully represent the table in the database. It's still missing: indices, triggers. Do not use it as a backup.

-- Table Definition
-- CREATE TABLE "public"."ipa_framework" (
--     "id" uuid NOT NULL,
--     "display_name" text,
--     "bundle_name" text,
--     "release_version" text,
--     "build_version" text,
--     "identifier" text,
--     "bundle_id" text,
--     "min_sdk_version" varchar,
--     "device_type" varchar,
--     "name" text,
--     "lib" bool NOT NULL,
--     "stored" bool NOT NULL,
--     PRIMARY KEY ("id")
-- );

-- This script only contains the table creation statements and does not fully represent the table in the database. It's still missing: indices, triggers. Do not use it as a backup.

-- Table Definition
-- CREATE TABLE "public"."ipa_icons" (
--     "id" uuid NOT NULL,
--     "name" text NOT NULL,
--     "dimensions" json NOT NULL,
--     PRIMARY KEY ("id")
-- );

-- This script only contains the table creation statements and does not fully represent the table in the database. It's still missing: indices, triggers. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."ipa_mobileprovision" (
    "id" uuid NOT NULL,
    "app_binary_id" uuid NOT NULL,
    "name" text,
    "app_name" text,
    "type" text,
    "platforms" json,
    "platform" text,
    "devices" json,
    "team_identifier" json,
    "team_name" text,
    "profile_name" text,
    "created_date" timestamp,
    "expired_date" timestamp,
    "development" bool NOT NULL,
    "appstore" bool NOT NULL,
    "adhoc" bool NOT NULL,
    "enterprise" bool NOT NULL,
    "inhouse" bool NOT NULL,
    "enabled_capabilities" json,
    PRIMARY KEY ("id")
);

-- This script only contains the table creation statements and does not fully represent the table in the database. It's still missing: indices, triggers. Do not use it as a backup.

-- Table Definition
-- CREATE TABLE "public"."ipa_mobileprovision_devcert" (
--     "id" uuid NOT NULL,
--     "name" text NOT NULL,
--     "created_date" timestamp NOT NULL,
--     "expired_date" timestamp NOT NULL,
--     "pem" text NOT NULL,
--     PRIMARY KEY ("id")
-- );

-- This script only contains the table creation statements and does not fully represent the table in the database. It's still missing: indices, triggers. Do not use it as a backup.

-- Table Definition
-- CREATE TABLE "public"."ipa_plugin" (
--     "id" uuid NOT NULL,
--     "display_name" text,
--     "bundle_name" text,
--     "release_version" text,
--     "build_version" text,
--     "identifier" text,
--     "bundle_id" text,
--     "min_sdk_version" varchar(10),
--     "device_type" varchar(10),
--     "name" text,
--     "lib" bool NOT NULL,
--     "stored" bool NOT NULL,
--     PRIMARY KEY ("id")
-- );

-- This script only contains the table creation statements and does not fully represent the table in the database. It's still missing: indices, triggers. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."s3_credentials" (
    "id" uuid NOT NULL,
    "server" varchar NOT NULL,
    "aws_region" varchar,
    "access_key" varchar NOT NULL,
    "secret_key" varchar NOT NULL,
    "bucket" varchar NOT NULL,
    PRIMARY KEY ("id")
);

-- This script only contains the table creation statements and does not fully represent the table in the database. It's still missing: indices, triggers. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."storage_credentials" (
    "id" uuid NOT NULL,
    "name" varchar,
    "user_id" uuid,
    "organization_id" uuid,
    "created_on" timestamp NOT NULL DEFAULT now(),
    "checked_on" timestamp NOT NULL DEFAULT now(),
    "last_used" timestamp,
    "status" varchar,
    PRIMARY KEY ("id")
);

ALTER TABLE "public"."apk" ADD FOREIGN KEY ("id") REFERENCES "public"."app_binary"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "public"."app_binary_assets" ADD FOREIGN KEY ("app_binary_id") REFERENCES "public"."app_binary"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "public"."app_binary_downloads" ADD FOREIGN KEY ("app_binary_id") REFERENCES "public"."app_binary"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "public"."ipa_mobileprovision" ADD FOREIGN KEY ("app_binary_id") REFERENCES "public"."app_binary"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "public"."ipa" ADD FOREIGN KEY ("id") REFERENCES "public"."app_binary"("id") ON DELETE CASCADE ON UPDATE CASCADE;
-- ALTER TABLE "public"."ipa_framework" ADD FOREIGN KEY ("id") REFERENCES "public"."ipa"("id") ON DELETE CASCADE ON UPDATE CASCADE;
-- ALTER TABLE "public"."ipa_icons" ADD FOREIGN KEY ("id") REFERENCES "public"."ipa"("id") ON DELETE CASCADE ON UPDATE CASCADE;
-- ALTER TABLE "public"."ipa_mobileprovision_devcert" ADD FOREIGN KEY ("id") REFERENCES "public"."ipa_mobileprovision"("id") ON DELETE CASCADE ON UPDATE CASCADE;
-- ALTER TABLE "public"."ipa_plugin" ADD FOREIGN KEY ("id") REFERENCES "public"."ipa"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "public"."s3_credentials" ADD FOREIGN KEY ("id") REFERENCES "public"."storage_credentials"("id") ON DELETE CASCADE ON UPDATE CASCADE;
