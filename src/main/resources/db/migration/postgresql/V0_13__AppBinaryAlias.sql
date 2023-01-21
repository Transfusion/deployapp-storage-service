DROP TABLE IF EXISTS "public"."app_binary_alias";
CREATE TABLE "public"."app_binary_alias"
(
    "alias"         varchar(50) NOT NULL,
    "app_binary_id" uuid        NOT NULL,
    PRIMARY KEY ("alias")
);

