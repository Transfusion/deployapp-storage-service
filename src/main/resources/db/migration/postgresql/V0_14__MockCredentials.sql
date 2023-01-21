-- https://stackoverflow.com/questions/69402556/ignore-class-in-hibernate-entity-mapping
CREATE TABLE "public"."mock_credentials"
(
    "id" uuid NOT NULL,
    CONSTRAINT "mock_credentials_id_fkey" FOREIGN KEY ("id") REFERENCES "public"."storage_credentials" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY ("id")
);

