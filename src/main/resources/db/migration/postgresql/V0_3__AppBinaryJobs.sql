CREATE TABLE "public"."app_binary_jobs" (
    "id" uuid NOT NULL,
    "app_binary_id" uuid NOT NULL,
    "name" varchar(100) NOT NULL,
    "description" text,
    PRIMARY KEY ("id")
);
