# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table address (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  number                    bigint,
  last_login                datetime,
  name                      varchar(255),
  mail_salutation           varchar(255),
  nick_name                 varchar(255),
  pre_name                  varchar(255),
  addressline1              varchar(255),
  addressline2              varchar(255),
  birthdate                 datetime,
  city_id                   bigint,
  preferred_language_code   CHAR(4),
  logo                      varchar(255),
  latitude                  float,
  longitude                 float,
  informally                tinyint(1) default 0,
  local_description         varchar(255),
  gender                    CHAR(1),
  note                      TEXT,
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_address_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint ck_address_gender check (gender in ('c','g','f','m')),
  constraint pk_address primary key (id))
;

create table address_contact (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  address_id                bigint,
  contact_type              CHAR(1),
  value                     varchar(255),
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_address_contact_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint ck_address_contact_contact_type check (contact_type in ('p','e','w','f')),
  constraint pk_address_contact primary key (id))
;

create table address_function (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_address_function_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_address_function primary key (id))
;

create table address_function_language (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  address_function_id       bigint,
  language_code             CHAR(4),
  name                      varchar(255),
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_address_function_language_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_address_function_language primary key (id))
;

create table address_linked_account (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  provider_user_id          varchar(255),
  provider_key              varchar(255),
  email                     varchar(255),
  email_validated           tinyint(1) default 0,
  photo_url                 varchar(255),
  use_this_as_primary_photo tinyint(1) default 0,
  profile_url               varchar(255),
  address_id                bigint,
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_address_linked_account_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_address_linked_account primary key (id))
;

create table address_relation (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  parent_address_id         bigint,
  child_address_id          bigint,
  address_function_id       bigint,
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_address_relation_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_address_relation primary key (id))
;

create table address_setting (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  unique_name               varchar(255),
  parameter_type            varchar(1),
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_address_setting_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint ck_address_setting_parameter_type check (parameter_type in ('I','S')),
  constraint uq_address_setting_unique_name unique (unique_name),
  constraint pk_address_setting primary key (id))
;

create table address_setting_relation (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  address_setting_id        bigint,
  address_id                bigint,
  integer_parameter         integer,
  string_parameter          varchar(255),
  access_or_deny            CHAR(1),
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_address_setting_relation_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint ck_address_setting_relation_access_or_deny check (access_or_deny in ('a','d')),
  constraint pk_address_setting_relation primary key (id))
;

create table address_token (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  token                     varchar(255),
  address_id                bigint,
  type                      CHAR(2),
  expires                   datetime,
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_address_token_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint ck_address_token_type check (type in ('EV','CS','PR','AT')),
  constraint uq_address_token_token unique (token),
  constraint pk_address_token primary key (id))
;

create table article (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_article_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_article primary key (id))
;

create table article_collection (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  parent_collection_id      bigint,
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_article_collection_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_article_collection primary key (id))
;

create table article_collection_language (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  article_collection_id     bigint,
  language_code             CHAR(4),
  name                      varchar(255),
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_article_collection_language_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_article_collection_language primary key (id))
;

create table article_option (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  number                    varchar(255),
  article_id                bigint,
  option_order              integer,
  unit_id                   bigint,
  weight_id                 bigint,
  sell_price                double,
  sell_price_currency_code  CHAR(4),
  primary_picture_id        bigint,
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_article_option_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_article_option primary key (id))
;

create table article_option_language (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  article_option_id         bigint,
  language_code             CHAR(4),
  title                     varchar(255),
  description               varchar(255),
  short_description         varchar(255),
  additional_description    varchar(255),
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_article_option_language_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_article_option_language primary key (id))
;

create table currency (
  code                      CHAR(4) not null,
  symbol                    varchar(255),
  global_exchange_rate_to_chf float,
  row_status                varchar(1),
  constraint ck_currency_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_currency primary key (code))
;

create table currency_exchange_rate (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  valid_from                datetime,
  from_code                 CHAR(4),
  to_code                   CHAR(4),
  exchangerate              float,
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_currency_exchange_rate_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_currency_exchange_rate primary key (id))
;

create table currency_language (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  currency_code             CHAR(4),
  language_code             CHAR(4),
  short_name                varchar(255),
  long_name                 varchar(255),
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_currency_language_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_currency_language primary key (id))
;

create table document (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  document_type_id          bigint,
  document_number           bigint,
  client_address_id         bigint,
  invoice_address_id        bigint,
  delivery_address_id       bigint,
  invoice_date              datetime,
  delivery_date             datetime,
  title                     varchar(255),
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_document_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_document primary key (id))
;

create table document_row (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  document_id               bigint,
  article_option_id         bigint,
  text                      TEXT,
  row_order                 integer,
  quantity                  double,
  price                     double,
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_document_row_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_document_row primary key (id))
;

create table document_type (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_document_type_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_document_type primary key (id))
;

create table document_type_language (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  document_type_id          bigint,
  language_code             CHAR(4),
  abbrevation               varchar(255),
  name                      varchar(255),
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_document_type_language_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_document_type_language primary key (id))
;

create table language (
  code                      CHAR(4) not null,
  english_name              varchar(255),
  local_name                varchar(255),
  row_status                varchar(1),
  constraint ck_language_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_language primary key (code))
;

create table local_file (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  file_name                 varchar(255),
  file_size                 bigint,
  mimetype                  varchar(255),
  file_size_unit_id         bigint,
  file_hash                 varchar(255),
  file_hash_algorithm       CHAR(8),
  file_signature_algorithm  varchar(3),
  file_signature            CHAR(36),
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_local_file_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint ck_local_file_file_hash_algorithm check (file_hash_algorithm in ('SHA-512','SHA-256','bcrypt','MD5','SHA-384')),
  constraint ck_local_file_file_signature_algorithm check (file_signature_algorithm in ('rsa')),
  constraint pk_local_file primary key (id))
;

create table notification (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  message                   varchar(255),
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_notification_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_notification primary key (id))
;

create table region (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  parent_region_id          bigint,
  region_code               varchar(255),
  region_level              varchar(3),
  local_name                varchar(255),
  postal_code_format        varchar(255),
  postal_code_denomination  varchar(255),
  local_state_name          varchar(255),
  local_district_name       varchar(255),
  local_subdistrict_name    varchar(255),
  local_commune_name        varchar(255),
  telephone_prefix          varchar(255),
  postal_code               varchar(255),
  latitude                  float,
  longitude                 float,
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_region_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint ck_region_region_level check (region_level in ('SUB','DIS','STA','COM','COU')),
  constraint pk_region primary key (id))
;

create table region_language (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  region_id                 bigint,
  language_code             CHAR(4),
  name                      varchar(255),
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_region_language_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_region_language primary key (id))
;

create table security_role (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  role_name                 varchar(255),
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_security_role_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_security_role primary key (id))
;

create table unit (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  type                      CHAR(20),
  root_unit_id              bigint,
  factor_to_root_unit       double,
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_unit_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint ck_unit_type check (type in ('time','electric charge','count','angle','digital','mass','length','luminous intensity','temperature')),
  constraint pk_unit primary key (id))
;

create table unit_language (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  unit_id                   bigint,
  language_code             CHAR(4),
  name                      varchar(255),
  abbrevation               varchar(255),
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_unit_language_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_unit_language primary key (id))
;

create table website (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  root_menu_id              integer,
  parent_website_id         bigint,
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_website_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint uq_website_root_menu_id unique (root_menu_id),
  constraint pk_website primary key (id))
;

create table website_language (
  id                        bigint auto_increment not null,
  row_status                varchar(1),
  created_by_id             bigint,
  updated_by_id             bigint,
  website_id                bigint,
  language_code             CHAR(4),
  title                     varchar(255),
  content                   TEXT,
  created_on                datetime not null,
  updated_on                datetime not null,
  constraint ck_website_language_row_status check (row_status in ('v','s','i','u','d','b','a')),
  constraint pk_website_language primary key (id))
;


create table address_security_role (
  address_id                     bigint not null,
  security_role_id               bigint not null,
  constraint pk_address_security_role primary key (address_id, security_role_id))
;

create table article_article_collection (
  article_id                     bigint not null,
  article_collection_id          bigint not null,
  constraint pk_article_article_collection primary key (article_id, article_collection_id))
;

create table article_option_local_file (
  article_option_id              bigint not null,
  local_file_id                  bigint not null,
  constraint pk_article_option_local_file primary key (article_option_id, local_file_id))
;
alter table address add constraint fk_address_createdBy_1 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_address_createdBy_1 on address (created_by_id);
alter table address add constraint fk_address_updatedBy_2 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_address_updatedBy_2 on address (updated_by_id);
alter table address add constraint fk_address_city_3 foreign key (city_id) references region (id) on delete restrict on update restrict;
create index ix_address_city_3 on address (city_id);
alter table address add constraint fk_address_preferredLanguage_4 foreign key (preferred_language_code) references language (code) on delete restrict on update restrict;
create index ix_address_preferredLanguage_4 on address (preferred_language_code);
alter table address_contact add constraint fk_address_contact_createdBy_5 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_address_contact_createdBy_5 on address_contact (created_by_id);
alter table address_contact add constraint fk_address_contact_updatedBy_6 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_address_contact_updatedBy_6 on address_contact (updated_by_id);
alter table address_contact add constraint fk_address_contact_address_7 foreign key (address_id) references address (id) on delete restrict on update restrict;
create index ix_address_contact_address_7 on address_contact (address_id);
alter table address_function add constraint fk_address_function_createdBy_8 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_address_function_createdBy_8 on address_function (created_by_id);
alter table address_function add constraint fk_address_function_updatedBy_9 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_address_function_updatedBy_9 on address_function (updated_by_id);
alter table address_function_language add constraint fk_address_function_language_createdBy_10 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_address_function_language_createdBy_10 on address_function_language (created_by_id);
alter table address_function_language add constraint fk_address_function_language_updatedBy_11 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_address_function_language_updatedBy_11 on address_function_language (updated_by_id);
alter table address_function_language add constraint fk_address_function_language_addressFunction_12 foreign key (address_function_id) references address_function (id) on delete restrict on update restrict;
create index ix_address_function_language_addressFunction_12 on address_function_language (address_function_id);
alter table address_function_language add constraint fk_address_function_language_language_13 foreign key (language_code) references language (code) on delete restrict on update restrict;
create index ix_address_function_language_language_13 on address_function_language (language_code);
alter table address_linked_account add constraint fk_address_linked_account_createdBy_14 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_address_linked_account_createdBy_14 on address_linked_account (created_by_id);
alter table address_linked_account add constraint fk_address_linked_account_updatedBy_15 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_address_linked_account_updatedBy_15 on address_linked_account (updated_by_id);
alter table address_linked_account add constraint fk_address_linked_account_address_16 foreign key (address_id) references address (id) on delete restrict on update restrict;
create index ix_address_linked_account_address_16 on address_linked_account (address_id);
alter table address_relation add constraint fk_address_relation_createdBy_17 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_address_relation_createdBy_17 on address_relation (created_by_id);
alter table address_relation add constraint fk_address_relation_updatedBy_18 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_address_relation_updatedBy_18 on address_relation (updated_by_id);
alter table address_relation add constraint fk_address_relation_parentAddress_19 foreign key (parent_address_id) references address (id) on delete restrict on update restrict;
create index ix_address_relation_parentAddress_19 on address_relation (parent_address_id);
alter table address_relation add constraint fk_address_relation_childAddress_20 foreign key (child_address_id) references address (id) on delete restrict on update restrict;
create index ix_address_relation_childAddress_20 on address_relation (child_address_id);
alter table address_relation add constraint fk_address_relation_addressFunction_21 foreign key (address_function_id) references address_function (id) on delete restrict on update restrict;
create index ix_address_relation_addressFunction_21 on address_relation (address_function_id);
alter table address_setting add constraint fk_address_setting_createdBy_22 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_address_setting_createdBy_22 on address_setting (created_by_id);
alter table address_setting add constraint fk_address_setting_updatedBy_23 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_address_setting_updatedBy_23 on address_setting (updated_by_id);
alter table address_setting_relation add constraint fk_address_setting_relation_createdBy_24 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_address_setting_relation_createdBy_24 on address_setting_relation (created_by_id);
alter table address_setting_relation add constraint fk_address_setting_relation_updatedBy_25 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_address_setting_relation_updatedBy_25 on address_setting_relation (updated_by_id);
alter table address_setting_relation add constraint fk_address_setting_relation_addressSetting_26 foreign key (address_setting_id) references address_setting (id) on delete restrict on update restrict;
create index ix_address_setting_relation_addressSetting_26 on address_setting_relation (address_setting_id);
alter table address_setting_relation add constraint fk_address_setting_relation_address_27 foreign key (address_id) references address (id) on delete restrict on update restrict;
create index ix_address_setting_relation_address_27 on address_setting_relation (address_id);
alter table address_token add constraint fk_address_token_createdBy_28 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_address_token_createdBy_28 on address_token (created_by_id);
alter table address_token add constraint fk_address_token_updatedBy_29 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_address_token_updatedBy_29 on address_token (updated_by_id);
alter table address_token add constraint fk_address_token_address_30 foreign key (address_id) references address (id) on delete restrict on update restrict;
create index ix_address_token_address_30 on address_token (address_id);
alter table article add constraint fk_article_createdBy_31 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_article_createdBy_31 on article (created_by_id);
alter table article add constraint fk_article_updatedBy_32 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_article_updatedBy_32 on article (updated_by_id);
alter table article_collection add constraint fk_article_collection_createdBy_33 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_article_collection_createdBy_33 on article_collection (created_by_id);
alter table article_collection add constraint fk_article_collection_updatedBy_34 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_article_collection_updatedBy_34 on article_collection (updated_by_id);
alter table article_collection add constraint fk_article_collection_parentCollection_35 foreign key (parent_collection_id) references article_collection (id) on delete restrict on update restrict;
create index ix_article_collection_parentCollection_35 on article_collection (parent_collection_id);
alter table article_collection_language add constraint fk_article_collection_language_createdBy_36 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_article_collection_language_createdBy_36 on article_collection_language (created_by_id);
alter table article_collection_language add constraint fk_article_collection_language_updatedBy_37 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_article_collection_language_updatedBy_37 on article_collection_language (updated_by_id);
alter table article_collection_language add constraint fk_article_collection_language_articleCollection_38 foreign key (article_collection_id) references article_collection (id) on delete restrict on update restrict;
create index ix_article_collection_language_articleCollection_38 on article_collection_language (article_collection_id);
alter table article_collection_language add constraint fk_article_collection_language_language_39 foreign key (language_code) references language (code) on delete restrict on update restrict;
create index ix_article_collection_language_language_39 on article_collection_language (language_code);
alter table article_option add constraint fk_article_option_createdBy_40 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_article_option_createdBy_40 on article_option (created_by_id);
alter table article_option add constraint fk_article_option_updatedBy_41 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_article_option_updatedBy_41 on article_option (updated_by_id);
alter table article_option add constraint fk_article_option_article_42 foreign key (article_id) references article (id) on delete restrict on update restrict;
create index ix_article_option_article_42 on article_option (article_id);
alter table article_option add constraint fk_article_option_unit_43 foreign key (unit_id) references unit (id) on delete restrict on update restrict;
create index ix_article_option_unit_43 on article_option (unit_id);
alter table article_option add constraint fk_article_option_weight_44 foreign key (weight_id) references unit (id) on delete restrict on update restrict;
create index ix_article_option_weight_44 on article_option (weight_id);
alter table article_option add constraint fk_article_option_sellPriceCurrency_45 foreign key (sell_price_currency_code) references currency (code) on delete restrict on update restrict;
create index ix_article_option_sellPriceCurrency_45 on article_option (sell_price_currency_code);
alter table article_option add constraint fk_article_option_primaryPicture_46 foreign key (primary_picture_id) references local_file (id) on delete restrict on update restrict;
create index ix_article_option_primaryPicture_46 on article_option (primary_picture_id);
alter table article_option_language add constraint fk_article_option_language_createdBy_47 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_article_option_language_createdBy_47 on article_option_language (created_by_id);
alter table article_option_language add constraint fk_article_option_language_updatedBy_48 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_article_option_language_updatedBy_48 on article_option_language (updated_by_id);
alter table article_option_language add constraint fk_article_option_language_articleOption_49 foreign key (article_option_id) references article_option (id) on delete restrict on update restrict;
create index ix_article_option_language_articleOption_49 on article_option_language (article_option_id);
alter table article_option_language add constraint fk_article_option_language_language_50 foreign key (language_code) references language (code) on delete restrict on update restrict;
create index ix_article_option_language_language_50 on article_option_language (language_code);
alter table currency_exchange_rate add constraint fk_currency_exchange_rate_createdBy_51 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_currency_exchange_rate_createdBy_51 on currency_exchange_rate (created_by_id);
alter table currency_exchange_rate add constraint fk_currency_exchange_rate_updatedBy_52 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_currency_exchange_rate_updatedBy_52 on currency_exchange_rate (updated_by_id);
alter table currency_exchange_rate add constraint fk_currency_exchange_rate_from_53 foreign key (from_code) references currency (code) on delete restrict on update restrict;
create index ix_currency_exchange_rate_from_53 on currency_exchange_rate (from_code);
alter table currency_exchange_rate add constraint fk_currency_exchange_rate_to_54 foreign key (to_code) references currency (code) on delete restrict on update restrict;
create index ix_currency_exchange_rate_to_54 on currency_exchange_rate (to_code);
alter table currency_language add constraint fk_currency_language_createdBy_55 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_currency_language_createdBy_55 on currency_language (created_by_id);
alter table currency_language add constraint fk_currency_language_updatedBy_56 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_currency_language_updatedBy_56 on currency_language (updated_by_id);
alter table currency_language add constraint fk_currency_language_currency_57 foreign key (currency_code) references currency (code) on delete restrict on update restrict;
create index ix_currency_language_currency_57 on currency_language (currency_code);
alter table currency_language add constraint fk_currency_language_language_58 foreign key (language_code) references language (code) on delete restrict on update restrict;
create index ix_currency_language_language_58 on currency_language (language_code);
alter table document add constraint fk_document_createdBy_59 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_document_createdBy_59 on document (created_by_id);
alter table document add constraint fk_document_updatedBy_60 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_document_updatedBy_60 on document (updated_by_id);
alter table document add constraint fk_document_documentType_61 foreign key (document_type_id) references document_type (id) on delete restrict on update restrict;
create index ix_document_documentType_61 on document (document_type_id);
alter table document add constraint fk_document_clientAddress_62 foreign key (client_address_id) references address (id) on delete restrict on update restrict;
create index ix_document_clientAddress_62 on document (client_address_id);
alter table document add constraint fk_document_invoiceAddress_63 foreign key (invoice_address_id) references address (id) on delete restrict on update restrict;
create index ix_document_invoiceAddress_63 on document (invoice_address_id);
alter table document add constraint fk_document_deliveryAddress_64 foreign key (delivery_address_id) references address (id) on delete restrict on update restrict;
create index ix_document_deliveryAddress_64 on document (delivery_address_id);
alter table document_row add constraint fk_document_row_createdBy_65 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_document_row_createdBy_65 on document_row (created_by_id);
alter table document_row add constraint fk_document_row_updatedBy_66 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_document_row_updatedBy_66 on document_row (updated_by_id);
alter table document_row add constraint fk_document_row_document_67 foreign key (document_id) references document (id) on delete restrict on update restrict;
create index ix_document_row_document_67 on document_row (document_id);
alter table document_row add constraint fk_document_row_articleOption_68 foreign key (article_option_id) references article_option (id) on delete restrict on update restrict;
create index ix_document_row_articleOption_68 on document_row (article_option_id);
alter table document_type add constraint fk_document_type_createdBy_69 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_document_type_createdBy_69 on document_type (created_by_id);
alter table document_type add constraint fk_document_type_updatedBy_70 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_document_type_updatedBy_70 on document_type (updated_by_id);
alter table document_type_language add constraint fk_document_type_language_createdBy_71 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_document_type_language_createdBy_71 on document_type_language (created_by_id);
alter table document_type_language add constraint fk_document_type_language_updatedBy_72 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_document_type_language_updatedBy_72 on document_type_language (updated_by_id);
alter table document_type_language add constraint fk_document_type_language_documentType_73 foreign key (document_type_id) references document_type (id) on delete restrict on update restrict;
create index ix_document_type_language_documentType_73 on document_type_language (document_type_id);
alter table document_type_language add constraint fk_document_type_language_language_74 foreign key (language_code) references language (code) on delete restrict on update restrict;
create index ix_document_type_language_language_74 on document_type_language (language_code);
alter table local_file add constraint fk_local_file_createdBy_75 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_local_file_createdBy_75 on local_file (created_by_id);
alter table local_file add constraint fk_local_file_updatedBy_76 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_local_file_updatedBy_76 on local_file (updated_by_id);
alter table local_file add constraint fk_local_file_fileSizeUnit_77 foreign key (file_size_unit_id) references unit (id) on delete restrict on update restrict;
create index ix_local_file_fileSizeUnit_77 on local_file (file_size_unit_id);
alter table notification add constraint fk_notification_createdBy_78 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_notification_createdBy_78 on notification (created_by_id);
alter table notification add constraint fk_notification_updatedBy_79 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_notification_updatedBy_79 on notification (updated_by_id);
alter table region add constraint fk_region_createdBy_80 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_region_createdBy_80 on region (created_by_id);
alter table region add constraint fk_region_updatedBy_81 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_region_updatedBy_81 on region (updated_by_id);
alter table region add constraint fk_region_parentRegion_82 foreign key (parent_region_id) references region (id) on delete restrict on update restrict;
create index ix_region_parentRegion_82 on region (parent_region_id);
alter table region_language add constraint fk_region_language_createdBy_83 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_region_language_createdBy_83 on region_language (created_by_id);
alter table region_language add constraint fk_region_language_updatedBy_84 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_region_language_updatedBy_84 on region_language (updated_by_id);
alter table region_language add constraint fk_region_language_region_85 foreign key (region_id) references region (id) on delete restrict on update restrict;
create index ix_region_language_region_85 on region_language (region_id);
alter table region_language add constraint fk_region_language_language_86 foreign key (language_code) references language (code) on delete restrict on update restrict;
create index ix_region_language_language_86 on region_language (language_code);
alter table security_role add constraint fk_security_role_createdBy_87 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_security_role_createdBy_87 on security_role (created_by_id);
alter table security_role add constraint fk_security_role_updatedBy_88 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_security_role_updatedBy_88 on security_role (updated_by_id);
alter table unit add constraint fk_unit_createdBy_89 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_unit_createdBy_89 on unit (created_by_id);
alter table unit add constraint fk_unit_updatedBy_90 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_unit_updatedBy_90 on unit (updated_by_id);
alter table unit add constraint fk_unit_rootUnit_91 foreign key (root_unit_id) references unit (id) on delete restrict on update restrict;
create index ix_unit_rootUnit_91 on unit (root_unit_id);
alter table unit_language add constraint fk_unit_language_createdBy_92 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_unit_language_createdBy_92 on unit_language (created_by_id);
alter table unit_language add constraint fk_unit_language_updatedBy_93 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_unit_language_updatedBy_93 on unit_language (updated_by_id);
alter table unit_language add constraint fk_unit_language_unit_94 foreign key (unit_id) references unit (id) on delete restrict on update restrict;
create index ix_unit_language_unit_94 on unit_language (unit_id);
alter table unit_language add constraint fk_unit_language_language_95 foreign key (language_code) references language (code) on delete restrict on update restrict;
create index ix_unit_language_language_95 on unit_language (language_code);
alter table website add constraint fk_website_createdBy_96 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_website_createdBy_96 on website (created_by_id);
alter table website add constraint fk_website_updatedBy_97 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_website_updatedBy_97 on website (updated_by_id);
alter table website add constraint fk_website_parentWebsite_98 foreign key (parent_website_id) references website (id) on delete restrict on update restrict;
create index ix_website_parentWebsite_98 on website (parent_website_id);
alter table website_language add constraint fk_website_language_createdBy_99 foreign key (created_by_id) references address (id) on delete restrict on update restrict;
create index ix_website_language_createdBy_99 on website_language (created_by_id);
alter table website_language add constraint fk_website_language_updatedBy_100 foreign key (updated_by_id) references address (id) on delete restrict on update restrict;
create index ix_website_language_updatedBy_100 on website_language (updated_by_id);
alter table website_language add constraint fk_website_language_website_101 foreign key (website_id) references website (id) on delete restrict on update restrict;
create index ix_website_language_website_101 on website_language (website_id);
alter table website_language add constraint fk_website_language_language_102 foreign key (language_code) references language (code) on delete restrict on update restrict;
create index ix_website_language_language_102 on website_language (language_code);



alter table address_security_role add constraint fk_address_security_role_address_01 foreign key (address_id) references address (id) on delete restrict on update restrict;

alter table address_security_role add constraint fk_address_security_role_security_role_02 foreign key (security_role_id) references security_role (id) on delete restrict on update restrict;

alter table article_article_collection add constraint fk_article_article_collection_article_01 foreign key (article_id) references article (id) on delete restrict on update restrict;

alter table article_article_collection add constraint fk_article_article_collection_article_collection_02 foreign key (article_collection_id) references article_collection (id) on delete restrict on update restrict;

alter table article_option_local_file add constraint fk_article_option_local_file_article_option_01 foreign key (article_option_id) references article_option (id) on delete restrict on update restrict;

alter table article_option_local_file add constraint fk_article_option_local_file_local_file_02 foreign key (local_file_id) references local_file (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table address;

drop table address_security_role;

drop table address_contact;

drop table address_function;

drop table address_function_language;

drop table address_linked_account;

drop table address_relation;

drop table address_setting;

drop table address_setting_relation;

drop table address_token;

drop table article;

drop table article_article_collection;

drop table article_collection;

drop table article_collection_language;

drop table article_option;

drop table article_option_local_file;

drop table article_option_language;

drop table currency;

drop table currency_exchange_rate;

drop table currency_language;

drop table document;

drop table document_row;

drop table document_type;

drop table document_type_language;

drop table language;

drop table local_file;

drop table notification;

drop table region;

drop table region_language;

drop table security_role;

drop table unit;

drop table unit_language;

drop table website;

drop table website_language;

SET FOREIGN_KEY_CHECKS=1;

