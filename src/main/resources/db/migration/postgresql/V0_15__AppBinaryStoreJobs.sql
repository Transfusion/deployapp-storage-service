DROP TABLE IF EXISTS "public"."app_binary_store_jobs";
-- This script only contains the table creation statements and does not fully represent the table in the database. It's still missing: indices, triggers. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."app_binary_store_jobs"
(
    "id"            uuid        NOT NULL,
--     "app_binary_id" uuid        NOT NULL,
    "status"        varchar(10) NOT NULL,
    "created_date"  timestamp   NOT NULL DEFAULT now(),
    "description"   text,
    CONSTRAINT "app_binary_store_jobs_app_binary_id_fkey" FOREIGN KEY ("id") REFERENCES "public"."app_binary" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY ("id")
);

