DROP TABLE "app_binary_downloads";
CREATE TABLE "public"."app_binary_downloads" (
    "id" uuid NOT NULL,
    "app_binary_id" uuid NOT NULL,
    -- to avoid future headaches since timestamp is a reserved keyword
    "ts" timestamp NOT NULL DEFAULT now(),
    "ip" text NOT NULL,
    "ua" text NOT NULL,
    "os" varchar(50) NULL,
    "version" varchar(50) NULL,
    CONSTRAINT "app_binary_downloads_app_binary_id_fkey" FOREIGN KEY ("app_binary_id") REFERENCES "public"."app_binary"("id") ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY ("id")
);