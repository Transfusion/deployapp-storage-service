CREATE TABLE "public"."apk_certs" (
    "id" uuid NOT NULL,
    "subject" text NOT NULL,
    "issuer" text NOT NULL,
    "not_before" timestamp NOT NULL,
    "not_after" timestamp NOT NULL,
    "path" text NOT NULL,
    "app_binary_id" uuid NOT NULL,
    CONSTRAINT "apk_certs_app_binary_id_fkey" FOREIGN KEY ("app_binary_id") REFERENCES "public"."app_binary"("id") ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY ("id")
);
