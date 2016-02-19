-- Sequence: seq_brand_id

-- DROP SEQUENCE seq_brand_id;

CREATE SEQUENCE seq_brand_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE seq_brand_id
  OWNER TO af;

  -- Sequence: seq_category_id

-- DROP SEQUENCE seq_category_id;

CREATE SEQUENCE seq_category_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE seq_category_id
  OWNER TO af;

-- Sequence: seq_gender_id

-- DROP SEQUENCE seq_gender_id;

CREATE SEQUENCE seq_gender_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE seq_gender_id
  OWNER TO af;

-- Sequence: seq_image_id

-- DROP SEQUENCE seq_image_id;

CREATE SEQUENCE seq_image_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE seq_image_id
  OWNER TO af;

-- Sequence: seq_product_id

-- DROP SEQUENCE seq_product_id;

CREATE SEQUENCE seq_product_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE seq_product_id
  OWNER TO af;

-- Sequence: seq_snapshot_detail_id

-- DROP SEQUENCE seq_snapshot_detail_id;

CREATE SEQUENCE seq_snapshot_detail_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE seq_snapshot_detail_id
  OWNER TO af;

-- Sequence: seq_snapshot_id

-- DROP SEQUENCE seq_snapshot_id;

CREATE SEQUENCE seq_snapshot_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE seq_snapshot_id
  OWNER TO af;

-- Table: brands

-- DROP TABLE brands;

CREATE TABLE brands
(
  id bigint NOT NULL, -- brand id
  name text NOT NULL,
  create_date timestamp without time zone NOT NULL DEFAULT now(),
  update_date timestamp without time zone NOT NULL DEFAULT now(),
  is_active boolean DEFAULT true,
  CONSTRAINT brand_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE brands
  OWNER TO af;
COMMENT ON COLUMN brands.id IS 'brand id';

-- Table: categories

-- DROP TABLE categories;

CREATE TABLE categories
(
  id bigint NOT NULL,
  name text NOT NULL,
  create_date timestamp without time zone NOT NULL DEFAULT now(),
  update_date timestamp without time zone NOT NULL DEFAULT now(),
  is_active boolean NOT NULL DEFAULT true,
  CONSTRAINT category_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE categories
  OWNER TO af;

-- Table: genders

-- DROP TABLE genders;

CREATE TABLE genders
(
  id bigint NOT NULL,
  name text NOT NULL,
  create_date timestamp without time zone NOT NULL DEFAULT now(),
  update_date timestamp without time zone NOT NULL DEFAULT now(),
  is_active boolean NOT NULL DEFAULT true,
  CONSTRAINT gender_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE genders
  OWNER TO af;

-- Table: products

-- DROP TABLE products;

CREATE TABLE products
(
  id bigint NOT NULL,
  product_id text,
  category_id bigint NOT NULL,
  brand_id bigint NOT NULL,
  gender_id bigint NOT NULL,
  name text NOT NULL,
  url text NOT NULL,
  create_date timestamp without time zone DEFAULT now(),
  update_date timestamp without time zone NOT NULL DEFAULT now(),
  is_active boolean NOT NULL DEFAULT true,
  name_tsvector tsvector,
  count bigint NOT NULL DEFAULT 1,
  CONSTRAINT product_pk PRIMARY KEY (id),
  CONSTRAINT brand_fk FOREIGN KEY (brand_id)
      REFERENCES brands (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT category_fk FOREIGN KEY (category_id)
      REFERENCES categories (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT gender_fk FOREIGN KEY (gender_id)
      REFERENCES genders (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE products
  OWNER TO af;

-- Index: products_product_id_idx

-- DROP INDEX products_product_id_idx;

CREATE INDEX products_product_id_idx
  ON products
  USING btree
  (product_id COLLATE pg_catalog."default" varchar_ops);

-- Table: images

-- DROP TABLE images;

CREATE TABLE images
(
  id bigint NOT NULL,
  url text,
  product_id bigint,
  create_date timestamp without time zone NOT NULL DEFAULT now(),
  update_date timestamp without time zone NOT NULL DEFAULT now(),
  is_active boolean NOT NULL DEFAULT true,
  CONSTRAINT image_pk PRIMARY KEY (id),
  CONSTRAINT product_fk FOREIGN KEY (product_id)
      REFERENCES products (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE images
  OWNER TO af;

-- Table: snapshot_detail

-- DROP TABLE snapshot_detail;

CREATE TABLE snapshot_detail
(
  id bigint NOT NULL,
  product_id bigint NOT NULL,
  price_regular numeric NOT NULL,
  price_discount numeric,
  snapshot_id bigint NOT NULL,
  is_active boolean NOT NULL DEFAULT true,
  create_date timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT snapshot_detail_pk PRIMARY KEY (id),
  CONSTRAINT product_fk FOREIGN KEY (product_id)
      REFERENCES products (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE snapshot_detail
  OWNER TO af;

-- Table: snapshots

-- DROP TABLE snapshots;

CREATE TABLE snapshots
(
  id bigint NOT NULL,
  create_date timestamp without time zone NOT NULL DEFAULT now(),
  update_date timestamp without time zone NOT NULL DEFAULT now(),
  is_active boolean NOT NULL DEFAULT true,
  brand_id bigint,
  snapshot text NOT NULL,
  CONSTRAINT id PRIMARY KEY (id),
  CONSTRAINT brand_fk FOREIGN KEY (brand_id)
      REFERENCES brands (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE snapshots
  OWNER TO af;

/*
alter sequence seq_brand_id start with 1 restart;
alter sequence seq_category_id start with 1 restart;
alter sequence seq_gender_id start with 1 restart;
alter sequence seq_image_id start with 1 restart;
alter sequence seq_product_id start with 1 restart;
alter sequence seq_snapshot_detail_id start with 1 restart;
alter sequence seq_snapshot_id start with 1 restart;

truncate table brands cascade;
truncate table categories cascade;
truncate table genders cascade;
truncate table images;
truncate table products cascade;
truncate table snapshot_detail;
truncate table snapshots;
*/
