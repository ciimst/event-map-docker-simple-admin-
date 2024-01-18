--
-- PostgreSQL database dump
--

-- Dumped from database version 13.4 (Debian 13.4-4.pgdg110+1)
-- Dumped by pg_dump version 15.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: event_map; Type: DATABASE; Schema: -; Owner: imst
--

CREATE DATABASE event_map WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en_US.utf8';

-- \connect event_map

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: imst
--

-- *not* creating schema, since initdb creates it

--
-- Name: postgis; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;


--
-- Name: EXTENSION postgis; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION postgis IS 'PostGIS geometry, geography, and raster spatial types and functions';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: action_state; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.action_state (
    id integer NOT NULL,
    state_type character varying NOT NULL,
    description character varying
);


--
-- Name: alert; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.alert (
    id integer NOT NULL,
    name character varying(64),
    fk_user_id integer,
    fk_layer_id integer,
    polygon_coordinate public.geometry,
    create_date timestamp without time zone DEFAULT now(),
    update_date timestamp without time zone,
    query character varying(256),
    fk_event_type_id integer,
    fk_event_group_id integer,
    reserved_key character varying(256),
    reserved_type character varying(256),
    reserved_id character varying(256),
    reserved_link character varying(4096),
    event_group_db_name character varying(64),
    shared_by character varying(256),
    color character varying
);

--
-- Name: TABLE alert; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON TABLE public.alert IS 'Web Projesinden yönetilecek';


--
-- Name: COLUMN alert.shared_by; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.alert.shared_by IS 'Alarm kriterini paylaşan kullanıcı';


--
-- Name: alert_event; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.alert_event (
    id integer NOT NULL,
    db_name character varying(64) NOT NULL,
    event_id integer NOT NULL,
    fk_user_id integer NOT NULL,
    fk_alert_id integer NOT NULL,
    create_date timestamp without time zone,
    event_id_db_name character varying(128),
    read_state boolean DEFAULT false,
    ip character varying(64)
);


--
-- Name: alert_event_cron; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.alert_event_cron (
    id integer NOT NULL,
    fk_event_id integer NOT NULL,
    state character varying,
    retry_count integer DEFAULT 0,
    create_date timestamp without time zone,
    update_date timestamp without time zone
);


--
-- Name: alert_event_cron_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.alert_event_cron_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: alert_event_cron_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: imst
--

ALTER SEQUENCE public.alert_event_cron_id_seq OWNED BY public.alert_event_cron.id;


--
-- Name: alert_event_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.alert_event_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: alert_event_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: imst
--

ALTER SEQUENCE public.alert_event_id_seq OWNED BY public.alert_event.id;


--
-- Name: alert_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.alert_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: alert_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: imst
--

ALTER SEQUENCE public.alert_id_seq OWNED BY public.alert.id;


--
-- Name: alert_state; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.alert_state (
    id integer NOT NULL,
    db_name character varying(64) NOT NULL,
    last_id integer NOT NULL,
    create_date timestamp without time zone DEFAULT now(),
    update_date timestamp without time zone
);


--
-- Name: alert_state_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.alert_state_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: alert_state_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: imst
--

ALTER SEQUENCE public.alert_state_id_seq OWNED BY public.alert_state.id;


--
-- Name: black_list_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.black_list_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


--
-- Name: black_list; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.black_list (
    id integer DEFAULT nextval('public.black_list_id_seq'::regclass) NOT NULL,
    name character varying(256) NOT NULL,
    fk_layer_id integer NOT NULL,
    create_date timestamp without time zone,
    update_date timestamp without time zone,
    create_user character varying(64),
    fk_event_group_id integer,
    fk_event_type_id integer,
    tag character varying(256) NOT NULL,
    fk_state_id integer,
    action_date timestamp without time zone,
    fk_action_state_id integer DEFAULT 1 NOT NULL
);


--
-- Name: comment_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.comment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


--
-- Name: count_events_time_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.count_events_time_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: database_dump; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.database_dump (
    id integer NOT NULL,
    create_date timestamp without time zone,
    name character varying(255) NOT NULL,
    key character varying(14) NOT NULL
);


--
-- Name: database_dump_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.database_dump_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: database_dump_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: imst
--

ALTER SEQUENCE public.database_dump_id_seq OWNED BY public.database_dump.id;


--
-- Name: event_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.event_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: event; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.event (
    id integer DEFAULT nextval('public.event_id_seq'::regclass) NOT NULL,
    title character varying(256),
    spot character varying(512),
    description character varying(4096),
    event_date timestamp without time zone NOT NULL,
    fk_event_type_id integer NOT NULL,
    country character varying(64),
    city character varying(64),
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    create_user character varying(64),
    fk_event_group_id integer NOT NULL,
    create_date timestamp without time zone,
    update_date timestamp without time zone,
    fk_state_id integer DEFAULT 0 NOT NULL,
    reserved_key character varying(256),
    reserved_type character varying(256),
    reserved_id character varying(256),
    reserved_link character varying(4096),
    user_id integer,
    group_id integer,
    black_list_tag character varying(256),
    reserved_1 character varying(500),
    reserved_2 character varying(500),
    reserved_3 character varying(500),
    reserved_4 character varying(500),
    reserved_5 character varying(500)
);


--
-- Name: TABLE event; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON TABLE public.event IS 'Olay bilgilerini tutar, harita üzerinde pinlenirler, media bilgileri olabilir';


--
-- Name: COLUMN event.title; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.event.title IS 'Olayın Başlığı';


--
-- Name: COLUMN event.spot; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.event.spot IS 'Olayın kısa açıklaması';


--
-- Name: COLUMN event.description; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.event.description IS 'Olayın detaylı açıklaması';


--
-- Name: COLUMN event.event_date; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.event.event_date IS 'Olayın gerçekleşme tarihi';


--
-- Name: COLUMN event.fk_event_type_id; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.event.fk_event_type_id IS 'Olayın türü, imgeler';


--
-- Name: COLUMN event.latitude; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.event.latitude IS 'Enlem';


--
-- Name: COLUMN event.longitude; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.event.longitude IS 'Boylam';


--
-- Name: COLUMN event.create_user; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.event.create_user IS 'Olayı sistme dahil eden';


--
-- Name: COLUMN event.fk_event_group_id; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.event.fk_event_group_id IS 'Bağlı olduğu grup bilgisi';


--
-- Name: COLUMN event.fk_state_id; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.event.fk_state_id IS 'aktif pasif';


--
-- Name: event_black_list_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.event_black_list_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: event_black_list; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.event_black_list (
    id integer DEFAULT nextval('public.event_black_list_id_seq'::regclass) NOT NULL,
    fk_event_id integer NOT NULL,
    fk_black_list_id integer NOT NULL
);


--
-- Name: event_column; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.event_column (
    id integer NOT NULL,
    name character varying
);


--
-- Name: event_group_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.event_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: event_group; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.event_group (
    id integer DEFAULT nextval('public.event_group_id_seq'::regclass) NOT NULL,
    name character varying(64) NOT NULL,
    color character varying(16) NOT NULL,
    fk_layer_id integer NOT NULL,
    description text,
    parent_id integer
);


--
-- Name: event_link_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.event_link_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: event_link; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.event_link (
    id integer DEFAULT nextval('public.event_link_id_seq'::regclass) NOT NULL,
    fk_event_column_id integer NOT NULL,
    link character varying(2048) NOT NULL,
    display_name character varying(128) NOT NULL,
    color character varying NOT NULL,
    create_date timestamp without time zone,
    update_date timestamp without time zone
);


--
-- Name: event_media; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.event_media (
    id integer NOT NULL,
    fk_event_id integer NOT NULL,
    path character varying(4096) NOT NULL,
    cover_image_path character varying(4096),
    is_video boolean DEFAULT false
);


--
-- Name: TABLE event_media; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON TABLE public.event_media IS 'olayların birden fazla medya bilgilerini tutar';


--
-- Name: COLUMN event_media.fk_event_id; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.event_media.fk_event_id IS 'Bagli oldugu olay';


--
-- Name: COLUMN event_media.path; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.event_media.path IS 'medya klasor yolu';


--
-- Name: COLUMN event_media.cover_image_path; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.event_media.cover_image_path IS 'Video icin video calismadan onceki gosterilen resim, fotograflar icin bos kalabilir veya thumbnail gibi birsey olabilir';


--
-- Name: COLUMN event_media.is_video; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.event_media.is_video IS 'videomu fotografmi bilgisi';


--
-- Name: event_media_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.event_media_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: event_media_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: imst
--

ALTER SEQUENCE public.event_media_id_seq OWNED BY public.event_media.id;


--
-- Name: event_tag; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.event_tag (
    id integer NOT NULL,
    fk_event_id integer NOT NULL,
    fk_tag_id integer NOT NULL
);


--
-- Name: event_tag_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.event_tag_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: event_tag_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: imst
--

ALTER SEQUENCE public.event_tag_id_seq OWNED BY public.event_tag.id;


--
-- Name: event_type; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.event_type (
    id integer NOT NULL,
    name character varying(64) NOT NULL,
    image character varying(8192) NOT NULL,
    code character varying(64),
    path_data character varying
);


--
-- Name: TABLE event_type; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON TABLE public.event_type IS 'Olay türleri ve imge fotoğraflarını tutar';


--
-- Name: COLUMN event_type.image; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.event_type.image IS 'imgeler veritabanında resim olarak tutulur';


--
-- Name: COLUMN event_type.path_data; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.event_type.path_data IS 'Mobil path listesi';


--
-- Name: event_type_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.event_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: event_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: imst
--

ALTER SEQUENCE public.event_type_id_seq OWNED BY public.event_type.id;


--
-- Name: events_time_count; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.events_time_count (
    id integer DEFAULT nextval('public.count_events_time_id_seq'::regclass) NOT NULL,
    fk_layer_id integer NOT NULL,
    fk_event_group_id integer NOT NULL,
    event_count bigint NOT NULL,
    event_day integer NOT NULL,
    event_month integer NOT NULL,
    event_year integer NOT NULL
);


--
-- Name: TABLE events_time_count; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON TABLE public.events_time_count IS 'Zamana göre olay sayıları';


--
-- Name: fake_layer_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.fake_layer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


--
-- Name: fake_layer_id; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.fake_layer_id (
    id integer DEFAULT nextval('public.fake_layer_id_seq'::regclass) NOT NULL,
    fk_layer_id integer NOT NULL,
    role_id character varying NOT NULL
);


--
-- Name: geo_layer; Type: TABLE; Schema: public; Owner: imst_admin
--

CREATE TABLE public.geo_layer (
    id integer NOT NULL,
    name character varying(64) NOT NULL,
    data text NOT NULL,
    create_date timestamp without time zone,
    fk_layer_id integer NOT NULL,
    state boolean DEFAULT true
);


--
-- Name: geo_layer_id_seq; Type: SEQUENCE; Schema: public; Owner: imst_admin
--

CREATE SEQUENCE public.geo_layer_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: geo_layer_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: imst_admin
--

ALTER SEQUENCE public.geo_layer_id_seq OWNED BY public.geo_layer.id;


--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: layer_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.layer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: layer; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.layer (
    id integer DEFAULT nextval('public.layer_id_seq'::regclass) NOT NULL,
    name character varying(64) NOT NULL,
    create_date timestamp without time zone,
    update_date timestamp without time zone,
    state boolean DEFAULT true,
    is_temp boolean DEFAULT false,
    guid character varying(64)
);


--
-- Name: TABLE layer; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON TABLE public.layer IS 'Olay ve alanlari bir grup altında toplayıp toplu bir şekilde yönetmek için kullanılır';


--
-- Name: layer_export; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.layer_export (
    id integer NOT NULL,
    min_z integer NOT NULL,
    max_z integer NOT NULL,
    create_date timestamp without time zone DEFAULT now() NOT NULL,
    start_date timestamp without time zone,
    finish_date timestamp without time zone,
    fk_layer_id integer,
    tile_create_date timestamp without time zone,
    event_create_date timestamp without time zone,
    name character varying(256),
    fk_tile_server_id integer,
    event_export_count integer
);


--
-- Name: COLUMN layer_export.event_export_count; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.layer_export.event_export_count IS 'Export alınan olay sayısı';


--
-- Name: layer_export_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.layer_export_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: layer_export_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: imst
--

ALTER SEQUENCE public.layer_export_id_seq OWNED BY public.layer_export.id;


--
-- Name: log_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: log; Type: TABLE; Schema: public; Owner: imst_admin
--

CREATE TABLE public.log (
    id integer DEFAULT nextval('public.log_id_seq'::regclass) NOT NULL,
    username character varying(64),
    user_id integer,
    ip character varying(64),
    description text,
    create_date timestamp without time zone,
    fk_log_type_id integer,
    unique_id integer,
    searchable_description text
);


--
-- Name: log_type; Type: TABLE; Schema: public; Owner: imst_admin
--

CREATE TABLE public.log_type (
    id integer NOT NULL,
    name character varying(64),
    related_table character varying(64)
);


--
-- Name: TABLE log_type; Type: COMMENT; Schema: public; Owner: imst_admin
--

COMMENT ON TABLE public.log_type IS 'Sistem loglarını tutacağımız tabloya referans olarak vereceğimiz, uygulama içinde yapılabilecek aksiyonların isimlerinin tutulacağı tablo';


--
-- Name: map_area; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.map_area (
    id integer NOT NULL,
    title character varying(256),
    coordinate_info text NOT NULL,
    state boolean DEFAULT true,
    create_date timestamp without time zone,
    update_date timestamp without time zone,
    fk_map_area_group_id integer NOT NULL
);


--
-- Name: TABLE map_area; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON TABLE public.map_area IS 'Harita üzerinde bir alanı seçmek için kullanılır';


--
-- Name: COLUMN map_area.title; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.map_area.title IS 'Haritada alan üzerine gelinde görünecek başlık';


--
-- Name: COLUMN map_area.coordinate_info; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.map_area.coordinate_info IS 'json olarak alan bilgisi tutulacak';


--
-- Name: map_area_group; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.map_area_group (
    id integer NOT NULL,
    name character varying(64) NOT NULL,
    color character varying(16) NOT NULL,
    fk_layer_id integer NOT NULL
);


--
-- Name: map_area_group_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.map_area_group_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: map_area_group_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: imst
--

ALTER SEQUENCE public.map_area_group_id_seq OWNED BY public.map_area_group.id;


--
-- Name: map_area_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.map_area_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: map_area_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: imst
--

ALTER SEQUENCE public.map_area_id_seq OWNED BY public.map_area.id;


--
-- Name: permission; Type: TABLE; Schema: public; Owner: imst_admin
--

CREATE TABLE public.permission (
    id integer NOT NULL,
    name character varying(64) NOT NULL,
    description character varying(512),
    state boolean,
    group_name character varying(64),
    display_order integer
);


--
-- Name: TABLE permission; Type: COMMENT; Schema: public; Owner: imst_admin
--

COMMENT ON TABLE public.permission IS 'Uygulama içinde kullanılacak olan yetkilerin tanımlanacağı tablo';


--
-- Name: profile_id_seq; Type: SEQUENCE; Schema: public; Owner: imst_admin
--

CREATE SEQUENCE public.profile_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: profile; Type: TABLE; Schema: public; Owner: imst_admin
--

CREATE TABLE public.profile (
    id integer DEFAULT nextval('public.profile_id_seq'::regclass) NOT NULL,
    name character varying(64) NOT NULL,
    description character varying(512),
    create_date timestamp without time zone,
    update_date timestamp without time zone,
    is_default boolean
);

--
-- Name: profile_permission; Type: TABLE; Schema: public; Owner: imst_admin
--

CREATE TABLE public.profile_permission (
    id integer NOT NULL,
    fk_profile_id integer NOT NULL,
    fk_permission_id integer NOT NULL
);


--
-- Name: profile_permission_id_seq; Type: SEQUENCE; Schema: public; Owner: imst_admin
--

CREATE SEQUENCE public.profile_permission_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: profile_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: imst_admin
--

ALTER SEQUENCE public.profile_permission_id_seq OWNED BY public.profile_permission.id;


--
-- Name: settings; Type: TABLE; Schema: public; Owner: imst_admin
--

CREATE TABLE public.settings (
    id integer NOT NULL,
    settings_key character varying(64) NOT NULL,
    settings_value character varying(2048),
    description character varying(2048),
    group_name character varying(64),
    type character varying(32)
);


--
-- Name: settings_id_seq; Type: SEQUENCE; Schema: public; Owner: imst_admin
--

CREATE SEQUENCE public.settings_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: settings_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: imst_admin
--

ALTER SEQUENCE public.settings_id_seq OWNED BY public.settings.id;


--
-- Name: spatial_test; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.spatial_test (
    id integer NOT NULL,
    name character varying(32),
    poly_test public.geometry,
    point_test public.geometry
);


--
-- Name: spatial_test_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.spatial_test_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: spatial_test_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: imst
--

ALTER SEQUENCE public.spatial_test_id_seq OWNED BY public.spatial_test.id;


--
-- Name: spring_session; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.spring_session (
    primary_id character(36) NOT NULL,
    session_id character(36) NOT NULL,
    creation_time bigint NOT NULL,
    last_access_time bigint NOT NULL,
    max_inactive_interval integer NOT NULL,
    expiry_time bigint NOT NULL,
    principal_name character varying(100)
);


--
-- Name: spring_session_attributes; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.spring_session_attributes (
    session_primary_id character(36) NOT NULL,
    attribute_name character varying(200) NOT NULL,
    attribute_bytes bytea NOT NULL
);


--
-- Name: state; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.state (
    id integer NOT NULL,
    state_type character varying NOT NULL,
    description character varying
);


--
-- Name: tag; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.tag (
    id integer NOT NULL,
    name character varying(64) NOT NULL
);


--
-- Name: TABLE tag; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON TABLE public.tag IS 'Olaylara keywordler tanımlamak için kullanılır';


--
-- Name: tag_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.tag_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: tag_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: imst
--

ALTER SEQUENCE public.tag_id_seq OWNED BY public.tag.id;


--
-- Name: tile_export_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.tile_export_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


--
-- Name: tile_export; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.tile_export (
    id integer DEFAULT nextval('public.tile_export_id_seq'::regclass) NOT NULL,
    name character varying(256),
    min_z integer NOT NULL,
    max_z integer NOT NULL,
    create_date timestamp without time zone DEFAULT now() NOT NULL,
    max_lat double precision,
    min_lat double precision,
    max_long double precision,
    min_long double precision,
    fk_tile_server_id integer
);


--
-- Name: tile_server; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.tile_server (
    id integer NOT NULL,
    name character varying(64) NOT NULL,
    url character varying(4096) NOT NULL,
    create_date timestamp without time zone,
    update_date timestamp without time zone,
    sort_order integer,
    state boolean DEFAULT true
);


--
-- Name: COLUMN tile_server.name; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.tile_server.name IS 'Ekranlarda kullanılmak istenilen harita altlığının seçilmesinde kullanılacak isimlendirme';


--
-- Name: COLUMN tile_server.url; Type: COMMENT; Schema: public; Owner: imst
--

COMMENT ON COLUMN public.tile_server.url IS 'içerisinde x,y,z parametrelerini alabilecek olan harita altlığı sunucusu linki';


--
-- Name: tile_server_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.tile_server_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: tile_server_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: imst
--

ALTER SEQUENCE public.tile_server_id_seq OWNED BY public.tile_server.id;


--
-- Name: user; Type: TABLE; Schema: public; Owner: imst_admin
--

CREATE TABLE public."user" (
    id integer NOT NULL,
    username character varying(64) NOT NULL,
    name character varying(256) NOT NULL,
    create_date timestamp without time zone,
    fk_profile_id integer NOT NULL,
    state boolean DEFAULT true,
    password character varying(64) NOT NULL,
    update_date timestamp without time zone,
    is_db_user boolean DEFAULT false,
    provider_user_id integer
);


--
-- Name: COLUMN "user".password; Type: COMMENT; Schema: public; Owner: imst_admin
--

COMMENT ON COLUMN public."user".password IS 'password hashlenerek kaydedilecek';


--
-- Name: user_event_group_permission_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.user_event_group_permission_id_seq
    START WITH 1
    INCREMENT BY 17
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


--
-- Name: user_event_group_permission; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.user_event_group_permission (
    id integer DEFAULT nextval('public.user_event_group_permission_id_seq'::regclass) NOT NULL,
    fk_event_group_id integer,
    fk_user_id integer
);


--
-- Name: user_group_id; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.user_group_id (
    id integer NOT NULL,
    group_id integer,
    fk_user_id integer
);


--
-- Name: user_group_id_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.user_group_id_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: user_group_id_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: imst
--

ALTER SEQUENCE public.user_group_id_id_seq OWNED BY public.user_group_id.id;


--
-- Name: user_id_seq; Type: SEQUENCE; Schema: public; Owner: imst_admin
--

CREATE SEQUENCE public.user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: imst_admin
--

ALTER SEQUENCE public.user_id_seq OWNED BY public."user".id;


--
-- Name: user_layer_permission_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.user_layer_permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


--
-- Name: user_layer_permission; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.user_layer_permission (
    id integer DEFAULT nextval('public.user_layer_permission_id_seq'::regclass) NOT NULL,
    fk_layer_id integer,
    fk_user_id integer
);


--
-- Name: user_settings_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.user_settings_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


--
-- Name: user_settings; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.user_settings (
    fk_user_settings_type_id integer NOT NULL,
    fk_user_id integer NOT NULL,
    settings_value character varying(2048),
    id integer DEFAULT nextval('public.user_settings_id_seq'::regclass) NOT NULL,
    fk_layer_id integer
);


--
-- Name: user_settings_type_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.user_settings_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


--
-- Name: user_settings_type; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.user_settings_type (
    id integer DEFAULT nextval('public.user_settings_type_id_seq'::regclass) NOT NULL,
    settings_key character varying(64) NOT NULL,
    settings_value character varying(2048),
    description character varying(2048),
    group_name character varying(64),
    type character varying(32),
    is_layer boolean DEFAULT true,
    "order" integer
);


--
-- Name: user_user_id; Type: TABLE; Schema: public; Owner: imst
--

CREATE TABLE public.user_user_id (
    id integer NOT NULL,
    user_id integer,
    fk_user_id integer
);


--
-- Name: user_user_id_id_seq; Type: SEQUENCE; Schema: public; Owner: imst
--

CREATE SEQUENCE public.user_user_id_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: user_user_id_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: imst
--

ALTER SEQUENCE public.user_user_id_id_seq OWNED BY public.user_user_id.id;


--
-- Name: alert id; Type: DEFAULT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.alert ALTER COLUMN id SET DEFAULT nextval('public.alert_id_seq'::regclass);


--
-- Name: alert_event id; Type: DEFAULT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.alert_event ALTER COLUMN id SET DEFAULT nextval('public.alert_event_id_seq'::regclass);


--
-- Name: alert_event_cron id; Type: DEFAULT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.alert_event_cron ALTER COLUMN id SET DEFAULT nextval('public.alert_event_cron_id_seq'::regclass);


--
-- Name: alert_state id; Type: DEFAULT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.alert_state ALTER COLUMN id SET DEFAULT nextval('public.alert_state_id_seq'::regclass);


--
-- Name: database_dump id; Type: DEFAULT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.database_dump ALTER COLUMN id SET DEFAULT nextval('public.database_dump_id_seq'::regclass);


--
-- Name: event_media id; Type: DEFAULT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.event_media ALTER COLUMN id SET DEFAULT nextval('public.event_media_id_seq'::regclass);


--
-- Name: event_tag id; Type: DEFAULT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.event_tag ALTER COLUMN id SET DEFAULT nextval('public.event_tag_id_seq'::regclass);


--
-- Name: event_type id; Type: DEFAULT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.event_type ALTER COLUMN id SET DEFAULT nextval('public.event_type_id_seq'::regclass);


--
-- Name: geo_layer id; Type: DEFAULT; Schema: public; Owner: imst_admin
--

ALTER TABLE ONLY public.geo_layer ALTER COLUMN id SET DEFAULT nextval('public.geo_layer_id_seq'::regclass);


--
-- Name: layer_export id; Type: DEFAULT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.layer_export ALTER COLUMN id SET DEFAULT nextval('public.layer_export_id_seq'::regclass);


--
-- Name: map_area id; Type: DEFAULT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.map_area ALTER COLUMN id SET DEFAULT nextval('public.map_area_id_seq'::regclass);


--
-- Name: map_area_group id; Type: DEFAULT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.map_area_group ALTER COLUMN id SET DEFAULT nextval('public.map_area_group_id_seq'::regclass);


--
-- Name: profile_permission id; Type: DEFAULT; Schema: public; Owner: imst_admin
--

ALTER TABLE ONLY public.profile_permission ALTER COLUMN id SET DEFAULT nextval('public.profile_permission_id_seq'::regclass);


--
-- Name: settings id; Type: DEFAULT; Schema: public; Owner: imst_admin
--

ALTER TABLE ONLY public.settings ALTER COLUMN id SET DEFAULT nextval('public.settings_id_seq'::regclass);


--
-- Name: spatial_test id; Type: DEFAULT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.spatial_test ALTER COLUMN id SET DEFAULT nextval('public.spatial_test_id_seq'::regclass);


--
-- Name: tag id; Type: DEFAULT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.tag ALTER COLUMN id SET DEFAULT nextval('public.tag_id_seq'::regclass);


--
-- Name: tile_server id; Type: DEFAULT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.tile_server ALTER COLUMN id SET DEFAULT nextval('public.tile_server_id_seq'::regclass);


--
-- Name: user id; Type: DEFAULT; Schema: public; Owner: imst_admin
--

ALTER TABLE ONLY public."user" ALTER COLUMN id SET DEFAULT nextval('public.user_id_seq'::regclass);


--
-- Name: user_group_id id; Type: DEFAULT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.user_group_id ALTER COLUMN id SET DEFAULT nextval('public.user_group_id_id_seq'::regclass);


--
-- Name: user_user_id id; Type: DEFAULT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.user_user_id ALTER COLUMN id SET DEFAULT nextval('public.user_user_id_id_seq'::regclass);


--
-- Data for Name: action_state; Type: TABLE DATA; Schema: public; Owner: imst
--

INSERT INTO public.action_state (id, state_type, description) VALUES (1, 'pending', NULL);
INSERT INTO public.action_state (id, state_type, description) VALUES (2, 'running', NULL);
INSERT INTO public.action_state (id, state_type, description) VALUES (3, 'finished', NULL);


--
-- Data for Name: alert; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: alert_event; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: alert_event_cron; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: alert_state; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: black_list; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: database_dump; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: event; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: event_black_list; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: event_column; Type: TABLE DATA; Schema: public; Owner: imst
--

INSERT INTO public.event_column (id, name) VALUES (1, 'id');
INSERT INTO public.event_column (id, name) VALUES (2, 'title');
INSERT INTO public.event_column (id, name) VALUES (3, 'spot');
INSERT INTO public.event_column (id, name) VALUES (4, 'description');
INSERT INTO public.event_column (id, name) VALUES (5, 'eventDate');
INSERT INTO public.event_column (id, name) VALUES (6, 'eventType');
INSERT INTO public.event_column (id, name) VALUES (7, 'country');
INSERT INTO public.event_column (id, name) VALUES (8, 'city');
INSERT INTO public.event_column (id, name) VALUES (9, 'latitude');
INSERT INTO public.event_column (id, name) VALUES (10, 'eventGroup');
INSERT INTO public.event_column (id, name) VALUES (11, 'state');
INSERT INTO public.event_column (id, name) VALUES (12, 'reservedKey');
INSERT INTO public.event_column (id, name) VALUES (13, 'reservedType');
INSERT INTO public.event_column (id, name) VALUES (14, 'reservedId');
INSERT INTO public.event_column (id, name) VALUES (15, 'reservedLink');
INSERT INTO public.event_column (id, name) VALUES (16, 'blackListTag');
INSERT INTO public.event_column (id, name) VALUES (17, 'reserved1');
INSERT INTO public.event_column (id, name) VALUES (18, 'reserved2');
INSERT INTO public.event_column (id, name) VALUES (19, 'reserved3');
INSERT INTO public.event_column (id, name) VALUES (20, 'reserved4');
INSERT INTO public.event_column (id, name) VALUES (21, 'reserved5');
INSERT INTO public.event_column (id, name) VALUES (22, 'layer');
INSERT INTO public.event_column (id, name) VALUES (23, 'longitude');


--
-- Data for Name: event_group; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: event_link; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: event_media; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: event_tag; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: event_type; Type: TABLE DATA; Schema: public; Owner: imst
--

INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (227, 'mustafa', '<polygon fill="#010101" points="50.201,44.588 50.193,44.595 50.193,44.588 "/> <polygon fill="#010101" points="50.201,44.588 50.193,44.595 50.193,44.588 "/> <path fill="#FFFFFF" d="M47.504,70.496l-10.995,2.391l-3.251-3.729c-0.479-0.573-1.338-0.669-2.008-0.191l-7.936,5.642 c-0.669,0.574-0.86,1.529-0.382,2.199c0.286,0.479,0.765,0.67,1.243,0.67c0.287,0,0.669-0.096,0.765-0.384l6.884-4.875l2.964,3.347 c0.382,0.383,0.956,0.572,1.53,0.478l11.855-2.581c0.765-0.189,1.338-0.956,1.147-1.816C49.13,70.878,48.365,70.305,47.504,70.496z "/> <path fill="#FFFFFF" d="M76.953,74.605l-10.614-5.832c-0.573-0.287-1.146-0.19-1.624,0.097l-5.738,3.92l-4.875-2.199 c-0.765-0.382-1.625,0-2.008,0.765c-0.383,0.766,0,1.627,0.766,2.009l5.641,2.581c0.479,0.285,1.053,0.191,1.531-0.096l5.639-3.824 l9.753,5.26c0.287,0.096,0.479,0.191,0.767,0.191c0.574,0,1.053-0.287,1.434-0.768C78.004,75.945,77.717,74.989,76.953,74.605z"/> <path fill="#FFFFFF" d="M49.894,50.131c-0.668-0.479-1.625-0.289-2.103,0.477l-3.06,5.068c-0.382,0.574-0.287,1.338,0.191,1.816 l2.199,2.39l-0.686,3.739l-8.684,1.52L35.266,50.8l26.198-4.589l2.485,14.247l-8.986,1.528c-0.861,0.189-1.438,0.957-1.245,1.816 c0.192,0.86,0.958,1.436,1.819,1.243l10.516-1.817c0.383-0.095,0.766-0.285,0.956-0.668c0.288-0.287,0.384-0.767,0.288-1.147 l-3.061-17.305c-0.084-0.375-0.279-0.691-0.543-0.917c-0.143-0.25-0.33-0.475-0.605-0.612l-16.54-8.893 c-0.669-0.382-1.434-0.287-1.912,0.287L32.207,47.835c-0.402,0.469-0.503,1.074-0.31,1.594c-0.012,0.142-0.01,0.283,0.023,0.413 l3.059,17.307c0.191,0.766,0.765,1.242,1.53,1.242c0.096,0,0.191,0,0.286,0.096L47.6,66.574c0.222-0.051,0.423-0.139,0.6-0.254 c0.577-0.141,1.026-0.557,1.026-1.18l0.956-5.451c0.096-0.478-0.096-0.953-0.383-1.336l-1.912-2.104l2.486-4.019 C50.852,51.564,50.659,50.607,49.894,50.131z M46.166,37.031l12.193,6.549l-21.548,3.802L46.166,37.031z"/>', 'earthquake-5', '[{"datad":"M47.504,70.496l-10.995,2.391l-3.251-3.729c-0.479-0.573-1.338-0.669-2.008-0.191l-7.936,5.642 c-0.669,0.574-0.86,1.529-0.382,2.199c0.286,0.479,0.765,0.67,1.243,0.67c0.287,0,0.669-0.096,0.765-0.384l6.884-4.875l2.964,3.347 c0.382,0.383,0.956,0.572,1.53,0.478l11.855-2.581c0.765-0.189,1.338-0.956,1.147-1.816C49.13,70.878,48.365,70.305,47.504,70.496z "},{"datad":"M76.953,74.605l-10.614-5.832c-0.573-0.287-1.146-0.19-1.624,0.097l-5.738,3.92l-4.875-2.199 c-0.765-0.382-1.625,0-2.008,0.765c-0.383,0.766,0,1.627,0.766,2.009l5.641,2.581c0.479,0.285,1.053,0.191,1.531-0.096l5.639-3.824 l9.753,5.26c0.287,0.096,0.479,0.191,0.767,0.191c0.574,0,1.053-0.287,1.434-0.768C78.004,75.945,77.717,74.989,76.953,74.605z"},{"datad":"M49.894,50.131c-0.668-0.479-1.625-0.289-2.103,0.477l-3.06,5.068c-0.382,0.574-0.287,1.338,0.191,1.816 l2.199,2.39l-0.686,3.739l-8.684,1.52L35.266,50.8l26.198-4.589l2.485,14.247l-8.986,1.528c-0.861,0.189-1.438,0.957-1.245,1.816 c0.192,0.86,0.958,1.436,1.819,1.243l10.516-1.817c0.383-0.095,0.766-0.285,0.956-0.668c0.288-0.287,0.384-0.767,0.288-1.147 l-3.061-17.305c-0.084-0.375-0.279-0.691-0.543-0.917c-0.143-0.25-0.33-0.475-0.605-0.612l-16.54-8.893 c-0.669-0.382-1.434-0.287-1.912,0.287L32.207,47.835c-0.402,0.469-0.503,1.074-0.31,1.594c-0.012,0.142-0.01,0.283,0.023,0.413 l3.059,17.307c0.191,0.766,0.765,1.242,1.53,1.242c0.096,0,0.191,0,0.286,0.096L47.6,66.574c0.222-0.051,0.423-0.139,0.6-0.254 c0.577-0.141,1.026-0.557,1.026-1.18l0.956-5.451c0.096-0.478-0.096-0.953-0.383-1.336l-1.912-2.104l2.486-4.019 C50.852,51.564,50.659,50.607,49.894,50.131z M46.166,37.031l12.193,6.549l-21.548,3.802L46.166,37.031z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (225, 'Deneme İcon', '<path fill="#FFFFFF" d="M65.408,52.361l-1.029,4.571v12.361c0,1.996-1.868,3.541-3.929,3.026c-1.416-0.321-2.381-1.674-2.381-3.155v-5.149c0-0.451-0.322-0.838-0.773-0.902l-11.009-1.353c-0.193,0-0.516-0.064-0.709-0.129l-8.37-2.317c-0.451-0.128-0.901,0.193-0.901,0.708v9.4c0,1.996-1.867,3.541-3.928,3.026c-1.416-0.321-2.382-1.674-2.382-3.155v-14.68V52.49v-8.242c0-0.322-0.129-0.644-0.387-0.837l-3.863-3.799c-0.837-0.837-0.901-2.189-0.129-2.961c0.837-0.837,2.189-0.837,2.962,0l3.798,3.798c0.193,0.193,0.515,0.322,0.837,0.322h20.924c0.451,0,0.902,0.193,1.288,0.515l9.337,9.336C65.216,51.01,65.602,51.717,65.408,52.361z M69.529,45.085l5.279,3.219c0.644,0.386,1.481,0.258,1.996-0.258l3.541-3.863c0.516-0.58,0.516-1.416,0.064-1.996l-9.142-10.816v-4.958c0-0.773-0.902-1.095-1.417-0.58L57.103,38.518c-0.386,0.386-0.386,0.966,0,1.288l8.757,8.756c0.322,0.321,0.901,0.192,1.029-0.193l1.16-2.769C68.241,45.021,69.014,44.764,69.529,45.085z"/>', 'deneme', '');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (229, 'mustafa3', '<polygon fill="#010101" points="50.201,44.588 50.193,44.595 50.193,44.588 "/> <polygon fill="#010101" points="50.201,44.588 50.193,44.595 50.193,44.588 "/> <path fill="#FFFFFF" d="M69.789,64.084c-0.467,0.31-1.076,0.465-1.688,0.465c-0.612,0-1.224-0.155-1.688-0.465l-2.709-1.806 c-0.466-0.311-1.075-0.465-1.688-0.465c-0.611,0-1.224,0.154-1.688,0.465l-2.709,1.806c-0.466,0.31-1.074,0.465-1.688,0.465 c-0.609,0-1.222-0.155-1.688-0.465l-2.709-1.806c-0.465-0.311-1.075-0.465-1.687-0.465c-0.61,0-1.222,0.154-1.687,0.465 l-2.709,1.806c-0.465,0.31-1.076,0.465-1.687,0.465c-0.611,0-1.223-0.155-1.688-0.465l-2.709-1.806 c-0.465-0.311-1.075-0.465-1.687-0.465c-0.61,0-1.222,0.154-1.687,0.465l-2.709,1.806c-0.465,0.31-1.076,0.465-1.687,0.465 c-0.611,0-1.223-0.155-1.688-0.465l-2.709-1.806c-0.465-0.311-1.075-0.465-1.687-0.465c-0.001,0-0.004,0-0.007,0v2.028 c0.003,0,0.006,0,0.007,0c0.298,0,0.498,0.08,0.563,0.124l2.709,1.806c0.778,0.518,1.776,0.803,2.811,0.803 s2.033-0.285,2.812-0.803l2.709-1.806c0.065-0.044,0.264-0.124,0.563-0.124c0.298,0,0.497,0.08,0.563,0.124l2.709,1.806 c0.778,0.518,1.776,0.803,2.811,0.803s2.033-0.285,2.812-0.803l2.709-1.806c0.065-0.044,0.264-0.124,0.563-0.124 c0.298,0,0.497,0.08,0.563,0.124l2.709,1.806c0.778,0.518,1.776,0.803,2.812,0.803c1.033,0,2.032-0.285,2.81-0.803l2.709-1.806 c0.066-0.044,0.264-0.124,0.564-0.124c0.298,0,0.495,0.08,0.562,0.124l2.708,1.806c0.779,0.518,1.777,0.803,2.813,0.803 c1.034,0,2.032-0.285,2.811-0.803l2.709-1.806c0.065-0.044,0.261-0.123,0.555-0.124v-2.028c-0.608,0.002-1.218,0.157-1.677,0.465 L69.789,64.084z"/> <path fill="#FFFFFF" d="M72.498,54.521l-2.709,1.807c-0.467,0.309-1.076,0.463-1.688,0.463c-0.612,0-1.224-0.154-1.688-0.463 l-2.709-1.807c-0.208-0.14-0.448-0.247-0.701-0.323l0.035-12.886l1.86,1.859l0,0l0,0c0.396,0.396,1.036,0.396,1.432,0 c0.397-0.396,0.397-1.038,0-1.434l0,0l0,0L50.559,25.964l0,0c-0.396-0.396-1.037-0.396-1.433,0l0,0l0,0L33.355,41.736l0,0h-0.002 c-0.396,0.396-0.396,1.038,0,1.434c0.397,0.396,1.039,0.396,1.434,0c0,0,0,0,0.001,0l1.867-1.869l-0.036,12.92 c-0.224,0.074-0.435,0.175-0.622,0.299l-2.709,1.807c-0.465,0.309-1.076,0.463-1.687,0.463c-0.611,0-1.223-0.154-1.688-0.463 l-2.709-1.807c-0.465-0.311-1.075-0.465-1.687-0.465c-0.001,0-0.004,0-0.007,0v2.027c0.003,0,0.006,0,0.007,0 c0.298,0,0.498,0.08,0.563,0.125l2.709,1.806c0.778,0.519,1.776,0.804,2.812,0.804c1.035,0,2.033-0.285,2.812-0.804l2.709-1.806 c0.065-0.045,0.264-0.125,0.563-0.125c0.298,0,0.497,0.08,0.563,0.125l2.709,1.806c0.778,0.519,1.776,0.804,2.811,0.804 s2.033-0.285,2.812-0.804l2.709-1.806c0.065-0.045,0.264-0.125,0.563-0.125c0.298,0,0.497,0.08,0.563,0.125l2.71,1.806 c0.777,0.519,1.775,0.804,2.812,0.804c1.033,0,2.03-0.285,2.81-0.804l2.709-1.806c0.065-0.045,0.264-0.125,0.563-0.125 c0.298,0,0.496,0.08,0.563,0.125l2.708,1.806c0.779,0.519,1.777,0.804,2.813,0.804c1.034,0,2.032-0.285,2.811-0.804l2.709-1.806 c0.065-0.044,0.261-0.122,0.555-0.125v-2.027C73.566,54.058,72.958,54.212,72.498,54.521z M49.851,54.056 c-0.61,0-1.222,0.154-1.687,0.465l-0.349,0.232v-6.78h4.056v6.768l-0.333-0.22C51.072,54.21,50.462,54.056,49.851,54.056z M57.621,56.327c-0.466,0.309-1.074,0.463-1.688,0.463c-0.609,0-1.222-0.154-1.688-0.463l-0.348-0.233v-9.135 c0-0.56-0.455-1.014-1.013-1.014h-6.084c-0.562,0-1.015,0.455-1.015,1.014c0,0.024,0.001,0.049,0.004,0.073h-0.004v9.071 l-0.333,0.224c-0.465,0.309-1.076,0.463-1.687,0.463c-0.611,0-1.223-0.154-1.688-0.463l-2.709-1.807 c-0.215-0.144-0.46-0.252-0.723-0.33l0.042-14.922l11.153-11.153l11.174,11.173l-0.042,14.925 c-0.234,0.075-0.452,0.177-0.646,0.307L57.621,56.327z"/> <path fill="#FFFFFF" d="M69.789,71.841c-0.467,0.311-1.076,0.465-1.688,0.465c-0.612,0-1.224-0.154-1.688-0.465l-2.709-1.806 c-0.466-0.31-1.075-0.463-1.688-0.463c-0.611,0-1.224,0.153-1.688,0.463l-2.709,1.806c-0.466,0.311-1.074,0.465-1.688,0.465 c-0.612,0-1.222-0.154-1.688-0.465l-2.709-1.806c-0.465-0.31-1.075-0.463-1.687-0.463s-1.222,0.153-1.687,0.463l-2.709,1.806 c-0.465,0.311-1.076,0.465-1.687,0.465c-0.611,0-1.223-0.154-1.688-0.465l-2.709-1.806c-0.465-0.31-1.075-0.463-1.687-0.463 c-0.61,0-1.222,0.153-1.687,0.463l-2.709,1.806c-0.465,0.311-1.076,0.465-1.687,0.465c-0.611,0-1.223-0.154-1.688-0.465 l-2.709-1.806c-0.465-0.31-1.075-0.463-1.687-0.463c-0.001,0-0.004,0-0.007,0V71.6c0.003,0,0.004,0,0.007,0 c0.298,0,0.498,0.081,0.563,0.122l2.709,1.807c0.778,0.52,1.776,0.805,2.811,0.805s2.033-0.285,2.812-0.805l2.709-1.807 c0.065-0.041,0.264-0.122,0.563-0.122c0.298,0,0.497,0.081,0.563,0.122l2.709,1.807c0.778,0.52,1.776,0.805,2.811,0.805 s2.033-0.285,2.812-0.805l2.709-1.807c0.065-0.041,0.264-0.122,0.563-0.122c0.298,0,0.497,0.081,0.563,0.122l2.709,1.807 c0.778,0.52,1.776,0.805,2.812,0.805c1.033,0,2.032-0.285,2.81-0.805l2.709-1.807c0.066-0.041,0.264-0.122,0.564-0.122 c0.298,0,0.495,0.081,0.562,0.122l2.708,1.807c0.779,0.52,1.777,0.805,2.813,0.805c1.034,0,2.032-0.285,2.811-0.805l2.709-1.807 c0.065-0.041,0.261-0.122,0.555-0.122v-2.027c-0.608,0.001-1.218,0.155-1.677,0.463L69.789,71.841z"/>', 'floods-7', '');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (228, 'mustafa2', '<polygon fill="#010101" points="50.201,44.588 50.193,44.595 50.193,44.588 "/> <polygon fill="#010101" points="50.201,44.588 50.193,44.595 50.193,44.588 "/> <path fill="#FFFFFF" d="M69.789,64.084c-0.467,0.31-1.076,0.465-1.688,0.465c-0.612,0-1.224-0.155-1.688-0.465l-2.709-1.806 c-0.466-0.311-1.075-0.465-1.688-0.465c-0.611,0-1.224,0.154-1.688,0.465l-2.709,1.806c-0.466,0.31-1.074,0.465-1.688,0.465 c-0.609,0-1.222-0.155-1.688-0.465l-2.709-1.806c-0.465-0.311-1.075-0.465-1.687-0.465c-0.61,0-1.222,0.154-1.687,0.465 l-2.709,1.806c-0.465,0.31-1.076,0.465-1.687,0.465c-0.611,0-1.223-0.155-1.688-0.465l-2.709-1.806 c-0.465-0.311-1.075-0.465-1.687-0.465c-0.61,0-1.222,0.154-1.687,0.465l-2.709,1.806c-0.465,0.31-1.076,0.465-1.687,0.465 c-0.611,0-1.223-0.155-1.688-0.465l-2.709-1.806c-0.465-0.311-1.075-0.465-1.687-0.465c-0.001,0-0.004,0-0.007,0v2.028 c0.003,0,0.006,0,0.007,0c0.298,0,0.498,0.08,0.563,0.124l2.709,1.806c0.778,0.518,1.776,0.803,2.811,0.803 s2.033-0.285,2.812-0.803l2.709-1.806c0.065-0.044,0.264-0.124,0.563-0.124c0.298,0,0.497,0.08,0.563,0.124l2.709,1.806 c0.778,0.518,1.776,0.803,2.811,0.803s2.033-0.285,2.812-0.803l2.709-1.806c0.065-0.044,0.264-0.124,0.563-0.124 c0.298,0,0.497,0.08,0.563,0.124l2.709,1.806c0.778,0.518,1.776,0.803,2.812,0.803c1.033,0,2.032-0.285,2.81-0.803l2.709-1.806 c0.066-0.044,0.264-0.124,0.564-0.124c0.298,0,0.495,0.08,0.562,0.124l2.708,1.806c0.779,0.518,1.777,0.803,2.813,0.803 c1.034,0,2.032-0.285,2.811-0.803l2.709-1.806c0.065-0.044,0.261-0.123,0.555-0.124v-2.028c-0.608,0.002-1.218,0.157-1.677,0.465 L69.789,64.084z"/> <path fill="#FFFFFF" d="M72.498,54.521l-2.709,1.807c-0.467,0.309-1.076,0.463-1.688,0.463c-0.612,0-1.224-0.154-1.688-0.463 l-2.709-1.807c-0.208-0.14-0.448-0.247-0.701-0.323l0.035-12.886l1.86,1.859l0,0l0,0c0.396,0.396,1.036,0.396,1.432,0 c0.397-0.396,0.397-1.038,0-1.434l0,0l0,0L50.559,25.964l0,0c-0.396-0.396-1.037-0.396-1.433,0l0,0l0,0L33.355,41.736l0,0h-0.002 c-0.396,0.396-0.396,1.038,0,1.434c0.397,0.396,1.039,0.396,1.434,0c0,0,0,0,0.001,0l1.867-1.869l-0.036,12.92 c-0.224,0.074-0.435,0.175-0.622,0.299l-2.709,1.807c-0.465,0.309-1.076,0.463-1.687,0.463c-0.611,0-1.223-0.154-1.688-0.463 l-2.709-1.807c-0.465-0.311-1.075-0.465-1.687-0.465c-0.001,0-0.004,0-0.007,0v2.027c0.003,0,0.006,0,0.007,0 c0.298,0,0.498,0.08,0.563,0.125l2.709,1.806c0.778,0.519,1.776,0.804,2.812,0.804c1.035,0,2.033-0.285,2.812-0.804l2.709-1.806 c0.065-0.045,0.264-0.125,0.563-0.125c0.298,0,0.497,0.08,0.563,0.125l2.709,1.806c0.778,0.519,1.776,0.804,2.811,0.804 s2.033-0.285,2.812-0.804l2.709-1.806c0.065-0.045,0.264-0.125,0.563-0.125c0.298,0,0.497,0.08,0.563,0.125l2.71,1.806 c0.777,0.519,1.775,0.804,2.812,0.804c1.033,0,2.03-0.285,2.81-0.804l2.709-1.806c0.065-0.045,0.264-0.125,0.563-0.125 c0.298,0,0.496,0.08,0.563,0.125l2.708,1.806c0.779,0.519,1.777,0.804,2.813,0.804c1.034,0,2.032-0.285,2.811-0.804l2.709-1.806 c0.065-0.044,0.261-0.122,0.555-0.125v-2.027C73.566,54.058,72.958,54.212,72.498,54.521z M49.851,54.056 c-0.61,0-1.222,0.154-1.687,0.465l-0.349,0.232v-6.78h4.056v6.768l-0.333-0.22C51.072,54.21,50.462,54.056,49.851,54.056z M57.621,56.327c-0.466,0.309-1.074,0.463-1.688,0.463c-0.609,0-1.222-0.154-1.688-0.463l-0.348-0.233v-9.135 c0-0.56-0.455-1.014-1.013-1.014h-6.084c-0.562,0-1.015,0.455-1.015,1.014c0,0.024,0.001,0.049,0.004,0.073h-0.004v9.071 l-0.333,0.224c-0.465,0.309-1.076,0.463-1.687,0.463c-0.611,0-1.223-0.154-1.688-0.463l-2.709-1.807 c-0.215-0.144-0.46-0.252-0.723-0.33l0.042-14.922l11.153-11.153l11.174,11.173l-0.042,14.925 c-0.234,0.075-0.452,0.177-0.646,0.307L57.621,56.327z"/> <path fill="#FFFFFF" d="M69.789,71.841c-0.467,0.311-1.076,0.465-1.688,0.465c-0.612,0-1.224-0.154-1.688-0.465l-2.709-1.806 c-0.466-0.31-1.075-0.463-1.688-0.463c-0.611,0-1.224,0.153-1.688,0.463l-2.709,1.806c-0.466,0.311-1.074,0.465-1.688,0.465 c-0.612,0-1.222-0.154-1.688-0.465l-2.709-1.806c-0.465-0.31-1.075-0.463-1.687-0.463s-1.222,0.153-1.687,0.463l-2.709,1.806 c-0.465,0.311-1.076,0.465-1.687,0.465c-0.611,0-1.223-0.154-1.688-0.465l-2.709-1.806c-0.465-0.31-1.075-0.463-1.687-0.463 c-0.61,0-1.222,0.153-1.687,0.463l-2.709,1.806c-0.465,0.311-1.076,0.465-1.687,0.465c-0.611,0-1.223-0.154-1.688-0.465 l-2.709-1.806c-0.465-0.31-1.075-0.463-1.687-0.463c-0.001,0-0.004,0-0.007,0V71.6c0.003,0,0.004,0,0.007,0 c0.298,0,0.498,0.081,0.563,0.122l2.709,1.807c0.778,0.52,1.776,0.805,2.811,0.805s2.033-0.285,2.812-0.805l2.709-1.807 c0.065-0.041,0.264-0.122,0.563-0.122c0.298,0,0.497,0.081,0.563,0.122l2.709,1.807c0.778,0.52,1.776,0.805,2.811,0.805 s2.033-0.285,2.812-0.805l2.709-1.807c0.065-0.041,0.264-0.122,0.563-0.122c0.298,0,0.497,0.081,0.563,0.122l2.709,1.807 c0.778,0.52,1.776,0.805,2.812,0.805c1.033,0,2.032-0.285,2.81-0.805l2.709-1.807c0.066-0.041,0.264-0.122,0.564-0.122 c0.298,0,0.495,0.081,0.562,0.122l2.708,1.807c0.779,0.52,1.777,0.805,2.813,0.805c1.034,0,2.032-0.285,2.811-0.805l2.709-1.807 c0.065-0.041,0.261-0.122,0.555-0.122v-2.027c-0.608,0.001-1.218,0.155-1.677,0.463L69.789,71.841z"/>', 'floods-5', '[{"datad":"M69.789,64.084c-0.467,0.31-1.076,0.465-1.688,0.465c-0.612,0-1.224-0.155-1.688-0.465l-2.709-1.806 c-0.466-0.311-1.075-0.465-1.688-0.465c-0.611,0-1.224,0.154-1.688,0.465l-2.709,1.806c-0.466,0.31-1.074,0.465-1.688,0.465 c-0.609,0-1.222-0.155-1.688-0.465l-2.709-1.806c-0.465-0.311-1.075-0.465-1.687-0.465c-0.61,0-1.222,0.154-1.687,0.465 l-2.709,1.806c-0.465,0.31-1.076,0.465-1.687,0.465c-0.611,0-1.223-0.155-1.688-0.465l-2.709-1.806 c-0.465-0.311-1.075-0.465-1.687-0.465c-0.61,0-1.222,0.154-1.687,0.465l-2.709,1.806c-0.465,0.31-1.076,0.465-1.687,0.465 c-0.611,0-1.223-0.155-1.688-0.465l-2.709-1.806c-0.465-0.311-1.075-0.465-1.687-0.465c-0.001,0-0.004,0-0.007,0v2.028 c0.003,0,0.006,0,0.007,0c0.298,0,0.498,0.08,0.563,0.124l2.709,1.806c0.778,0.518,1.776,0.803,2.811,0.803 s2.033-0.285,2.812-0.803l2.709-1.806c0.065-0.044,0.264-0.124,0.563-0.124c0.298,0,0.497,0.08,0.563,0.124l2.709,1.806 c0.778,0.518,1.776,0.803,2.811,0.803s2.033-0.285,2.812-0.803l2.709-1.806c0.065-0.044,0.264-0.124,0.563-0.124 c0.298,0,0.497,0.08,0.563,0.124l2.709,1.806c0.778,0.518,1.776,0.803,2.812,0.803c1.033,0,2.032-0.285,2.81-0.803l2.709-1.806 c0.066-0.044,0.264-0.124,0.564-0.124c0.298,0,0.495,0.08,0.562,0.124l2.708,1.806c0.779,0.518,1.777,0.803,2.813,0.803 c1.034,0,2.032-0.285,2.811-0.803l2.709-1.806c0.065-0.044,0.261-0.123,0.555-0.124v-2.028c-0.608,0.002-1.218,0.157-1.677,0.465 L69.789,64.084z"},{"datad":"M72.498,54.521l-2.709,1.807c-0.467,0.309-1.076,0.463-1.688,0.463c-0.612,0-1.224-0.154-1.688-0.463 l-2.709-1.807c-0.208-0.14-0.448-0.247-0.701-0.323l0.035-12.886l1.86,1.859l0,0l0,0c0.396,0.396,1.036,0.396,1.432,0 c0.397-0.396,0.397-1.038,0-1.434l0,0l0,0L50.559,25.964l0,0c-0.396-0.396-1.037-0.396-1.433,0l0,0l0,0L33.355,41.736l0,0h-0.002 c-0.396,0.396-0.396,1.038,0,1.434c0.397,0.396,1.039,0.396,1.434,0c0,0,0,0,0.001,0l1.867-1.869l-0.036,12.92 c-0.224,0.074-0.435,0.175-0.622,0.299l-2.709,1.807c-0.465,0.309-1.076,0.463-1.687,0.463c-0.611,0-1.223-0.154-1.688-0.463 l-2.709-1.807c-0.465-0.311-1.075-0.465-1.687-0.465c-0.001,0-0.004,0-0.007,0v2.027c0.003,0,0.006,0,0.007,0 c0.298,0,0.498,0.08,0.563,0.125l2.709,1.806c0.778,0.519,1.776,0.804,2.812,0.804c1.035,0,2.033-0.285,2.812-0.804l2.709-1.806 c0.065-0.045,0.264-0.125,0.563-0.125c0.298,0,0.497,0.08,0.563,0.125l2.709,1.806c0.778,0.519,1.776,0.804,2.811,0.804 s2.033-0.285,2.812-0.804l2.709-1.806c0.065-0.045,0.264-0.125,0.563-0.125c0.298,0,0.497,0.08,0.563,0.125l2.71,1.806 c0.777,0.519,1.775,0.804,2.812,0.804c1.033,0,2.03-0.285,2.81-0.804l2.709-1.806c0.065-0.045,0.264-0.125,0.563-0.125 c0.298,0,0.496,0.08,0.563,0.125l2.708,1.806c0.779,0.519,1.777,0.804,2.813,0.804c1.034,0,2.032-0.285,2.811-0.804l2.709-1.806 c0.065-0.044,0.261-0.122,0.555-0.125v-2.027C73.566,54.058,72.958,54.212,72.498,54.521z M49.851,54.056 c-0.61,0-1.222,0.154-1.687,0.465l-0.349,0.232v-6.78h4.056v6.768l-0.333-0.22C51.072,54.21,50.462,54.056,49.851,54.056z M57.621,56.327c-0.466,0.309-1.074,0.463-1.688,0.463c-0.609,0-1.222-0.154-1.688-0.463l-0.348-0.233v-9.135 c0-0.56-0.455-1.014-1.013-1.014h-6.084c-0.562,0-1.015,0.455-1.015,1.014c0,0.024,0.001,0.049,0.004,0.073h-0.004v9.071 l-0.333,0.224c-0.465,0.309-1.076,0.463-1.687,0.463c-0.611,0-1.223-0.154-1.688-0.463l-2.709-1.807 c-0.215-0.144-0.46-0.252-0.723-0.33l0.042-14.922l11.153-11.153l11.174,11.173l-0.042,14.925 c-0.234,0.075-0.452,0.177-0.646,0.307L57.621,56.327z"},{"datad":"M69.789,71.841c-0.467,0.311-1.076,0.465-1.688,0.465c-0.612,0-1.224-0.154-1.688-0.465l-2.709-1.806 c-0.466-0.31-1.075-0.463-1.688-0.463c-0.611,0-1.224,0.153-1.688,0.463l-2.709,1.806c-0.466,0.311-1.074,0.465-1.688,0.465 c-0.612,0-1.222-0.154-1.688-0.465l-2.709-1.806c-0.465-0.31-1.075-0.463-1.687-0.463s-1.222,0.153-1.687,0.463l-2.709,1.806 c-0.465,0.311-1.076,0.465-1.687,0.465c-0.611,0-1.223-0.154-1.688-0.465l-2.709-1.806c-0.465-0.31-1.075-0.463-1.687-0.463 c-0.61,0-1.222,0.153-1.687,0.463l-2.709,1.806c-0.465,0.311-1.076,0.465-1.687,0.465c-0.611,0-1.223-0.154-1.688-0.465 l-2.709-1.806c-0.465-0.31-1.075-0.463-1.687-0.463c-0.001,0-0.004,0-0.007,0V71.6c0.003,0,0.004,0,0.007,0 c0.298,0,0.498,0.081,0.563,0.122l2.709,1.807c0.778,0.52,1.776,0.805,2.811,0.805s2.033-0.285,2.812-0.805l2.709-1.807 c0.065-0.041,0.264-0.122,0.563-0.122c0.298,0,0.497,0.081,0.563,0.122l2.709,1.807c0.778,0.52,1.776,0.805,2.811,0.805 s2.033-0.285,2.812-0.805l2.709-1.807c0.065-0.041,0.264-0.122,0.563-0.122c0.298,0,0.497,0.081,0.563,0.122l2.709,1.807 c0.778,0.52,1.776,0.805,2.812,0.805c1.033,0,2.032-0.285,2.81-0.805l2.709-1.807c0.066-0.041,0.264-0.122,0.564-0.122 c0.298,0,0.495,0.081,0.562,0.122l2.708,1.807c0.779,0.52,1.777,0.805,2.813,0.805c1.034,0,2.032-0.285,2.811-0.805l2.709-1.807 c0.065-0.041,0.261-0.122,0.555-0.122v-2.027c-0.608,0.001-1.218,0.155-1.677,0.463L69.789,71.841z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (223, 'Yeni deneme iconu', '<rect x="-59.3" y="49.005" fill="#010101" width="0.244" height="0.221"/> <polygon fill="#010101" points="-53.738,52.108 -53.746,52.115 -53.746,52.108 "/> <polygon fill="#010101" points="-53.738,52.108 -53.746,52.115 -53.746,52.108 "/> <path fill="#FFFFFF" d="M50.97,18.948h0.048v0.112h-0.001c0.578,1.421,1.604,4.576,1.604,7.408c0,2.83,0.174,3.277,0.174,3.896 s2.058,12.398,2.058,12.398l1.548,3.982v0.669l14.297,13.089h0.015c-0.003-1.48,0.37,5.29,0.37,5.29 c-0.097,0.064-0.275,0.011-0.366-0.02l-0.019,0.011c0,0.005,0,0.01,0,0.01l-0.019-0.015l-13.897-3.557v2.111l7.509,7.568 c0,0-0.63,1.126-0.965,1.552c-0.334,0.426-0.74,1.116-0.892,1.127c-0.152,0.01-22.963,0.011-23.116,0s-0.559-0.699-0.892-1.126 c-0.335-0.426-0.964-1.552-0.964-1.552l7.507-7.569v-2.11L31.07,65.78l-0.017,0.014c0,0,0-0.004,0-0.009l-0.019-0.01 c-0.091,0.028-0.269,0.083-0.366,0.019c0,0,0.372-6.771,0.37-5.29h0.015L45.35,47.415v-0.67l1.547-3.982c0,0,2.06-11.78,2.06-12.398 s0.172-1.066,0.172-3.896c0-2.832,1.025-5.987,1.604-7.408v-0.113h0.049"/>', 'new-deneme-icon', '[{"datad":"M50.97,18.948h0.048v0.112h-0.001c0.578,1.421,1.604,4.576,1.604,7.408c0,2.83,0.174,3.277,0.174,3.896 s2.058,12.398,2.058,12.398l1.548,3.982v0.669l14.297,13.089h0.015c-0.003-1.48,0.37,5.29,0.37,5.29 c-0.097,0.064-0.275,0.011-0.366-0.02l-0.019,0.011c0,0.005,0,0.01,0,0.01l-0.019-0.015l-13.897-3.557v2.111l7.509,7.568 c0,0-0.63,1.126-0.965,1.552c-0.334,0.426-0.74,1.116-0.892,1.127c-0.152,0.01-22.963,0.011-23.116,0s-0.559-0.699-0.892-1.126 c-0.335-0.426-0.964-1.552-0.964-1.552l7.507-7.569v-2.11L31.07,65.78l-0.017,0.014c0,0,0-0.004,0-0.009l-0.019-0.01 c-0.091,0.028-0.269,0.083-0.366,0.019c0,0,0.372-6.771,0.37-5.29h0.015L45.35,47.415v-0.67l1.547-3.982c0,0,2.06-11.78,2.06-12.398 s0.172-1.066,0.172-3.896c0-2.832,1.025-5.987,1.604-7.408v-0.113h0.049"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (219, 'Balloon', '<path fill="#FFFFFF" d="M77.858	47.969c0-7.423-6.039-13.463-13.463-13.463c-0.832	0-1.683	0.083-2.536	0.249c-1.631-8.104-8.704-13.932-17.011-13.932c-9.579	0-17.373	7.793-17.373	17.372c0	8.216	5.67	15.195	13.616	16.942l-1.378	1.379c-0.498	0.497-0.646	1.238-0.376	1.89c0.269	0.65	0.897	1.07	1.602	1.07h2.175v16.633c0	0.956	0.778	1.734	1.734	1.734c0.956	0	1.734-0.778	1.734-1.734V59.477h2.195c0.956	0	1.733-0.778	1.733-1.734c0-0.546-0.263-1.067-0.648-1.348l-1.256-1.257c1.281-0.282	2.519-0.707	3.695-1.268c1.672	3.446	4.751	6.017	8.422	7.049l-1.463	1.462c-0.497	0.497-0.645	1.238-0.376	1.889s0.898	1.07	1.604	1.07h2.176v10.768c0	0.956	0.777	1.734	1.733	1.734s1.734-0.778	1.734-1.734V65.342h2.196c0.956	0	1.733-0.778	1.733-1.734c0-0.55-0.265-1.072-0.65-1.348l-1.341-1.341C73.792	59.291	77.858	53.983	77.858	47.969z M74.39	47.969c0	5.51-4.483	9.994-9.994	9.994c-3.981	0-7.521-2.303-9.122-5.899c4.353-3.279	6.94-8.426	6.946-13.851c0.713-0.158	1.442-0.238	2.176-0.238C69.906	37.975	74.39	42.458	74.39	47.969z M44.848	24.292c7.667	0	13.904	6.237	13.904	13.904c0	7.667-6.237	13.904-13.904	13.904c-7.667	0-13.904-6.237-13.904-13.904C30.944	30.529	37.181	24.292	44.848	24.292z"/><path  fill="#FFFFFF" d="M32.423,32.774a4.507,2.423 0 1,0 9.014,0a4.507,2.423 0 1,0 -9.014,0" />', 'balloon-2', '[{"datad":"M77.858,47.969c0-7.423-6.039-13.463-13.463-13.463c-0.832,0-1.683,0.083-2.536,0.249c-1.631-8.104-8.704-13.932-17.011-13.932c-9.579,0-17.373,7.793-17.373,17.372c0,8.216,5.67,15.195,13.616,16.942l-1.378,1.379c-0.498,0.497-0.646,1.238-0.376,1.89c0.269,0.65,0.897,1.07,1.602,1.07h2.175v16.633c0,0.956,0.778,1.734,1.734,1.734c0.956,0,1.734-0.778,1.734-1.734V59.477h2.195c0.956,0,1.733-0.778,1.733-1.734c0-0.546-0.263-1.067-0.648-1.348l-1.256-1.257c1.281-0.282,2.519-0.707,3.695-1.268c1.672,3.446,4.751,6.017,8.422,7.049l-1.463,1.462c-0.497,0.497-0.645,1.238-0.376,1.889s0.898,1.07,1.604,1.07h2.176v10.768c0,0.956,0.777,1.734,1.733,1.734s1.734-0.778,1.734-1.734V65.342h2.196c0.956,0,1.733-0.778,1.733-1.734c0-0.55-0.265-1.072-0.65-1.348l-1.341-1.341C73.792,59.291,77.858,53.983,77.858,47.969z M74.39,47.969c0,5.51-4.483,9.994-9.994,9.994c-3.981,0-7.521-2.303-9.122-5.899c4.353-3.279,6.94-8.426,6.946-13.851c0.713-0.158,1.442-0.238,2.176-0.238C69.906,37.975,74.39,42.458,74.39,47.969z M44.848,24.292c7.667,0,13.904,6.237,13.904,13.904c0,7.667-6.237,13.904-13.904,13.904c-7.667,0-13.904-6.237-13.904-13.904C30.944,30.529,37.181,24.292,44.848,24.292z"},{"datad": "M32.423,32.774a4.507,2.423 0 1,0 9.014,0a4.507,2.423 0 1,0 -9.014,0"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (218, 'Motobike', '<path fill="#FFFFFF" d="M70.765,47.179c-5.281,0-9.576,4.293-9.576,9.58c0,5.281,4.295,9.577,9.576,9.577s9.577-4.296,9.577-9.577C80.342,51.473,76.046,47.179,70.765,47.179z M70.765,61.738c-2.746,0-4.979-2.233-4.979-4.979c0-2.747,2.232-4.981,4.979-4.981c2.747,0,4.982,2.234,4.982,4.981C75.747,59.505,73.512,61.738,70.765,61.738z"/><path fill="#FFFFFF" d="M73.761,45.401c1.75,0.066,1.901-10.088-11.034-13.637c-2.746-0.756-0.521,3.016-0.736,5.158c0,0-11.774-6.255-16.114,1.607c-1.618,2.934-1.992,4.34-6.26,4.34c-4.271,0-2.525-3.674-8.144-3.745c-3.536-0.04-7.157,1.629-6.8,2.969c0.293,1.098,10.108,1.552,13.939,8.959c-7.457,3.359-10.357,4.307-9.76,5.925c0.615,1.682,14.765,3.507,14.765,3.507s14.938,2.711,15.159-0.287C59.349,52.344,61.938,44.971,73.761,45.401z"/><path fill="#FFFFFF" d="M29.229,61.738c-2.742,0-4.979-2.233-4.979-4.979c0-2.747,2.237-4.981,4.979-4.981c0.987,0,1.902,0.289,2.672,0.782l4.487-2.158c-1.752-1.975-4.313-3.222-7.161-3.222c-5.281,0-9.572,4.293-9.572,9.58c0,5.281,4.292,9.577,9.572,9.577c3.688,0,6.89-2.093,8.493-5.148l-4.79-1.106C32.019,61.097,30.701,61.738,29.229,61.738z"/>', 'moto-2', '[{"datad":"M70.765,47.179c-5.281,0-9.576,4.293-9.576,9.58c0,5.281,4.295,9.577,9.576,9.577s9.577-4.296,9.577-9.577C80.342,51.473,76.046,47.179,70.765,47.179z M70.765,61.738c-2.746,0-4.979-2.233-4.979-4.979c0-2.747,2.232-4.981,4.979-4.981c2.747,0,4.982,2.234,4.982,4.981C75.747,59.505,73.512,61.738,70.765,61.738z"},{"datad":"M73.761,45.401c1.75,0.066,1.901-10.088-11.034-13.637c-2.746-0.756-0.521,3.016-0.736,5.158c0,0-11.774-6.255-16.114,1.607c-1.618,2.934-1.992,4.34-6.26,4.34c-4.271,0-2.525-3.674-8.144-3.745c-3.536-0.04-7.157,1.629-6.8,2.969c0.293,1.098,10.108,1.552,13.939,8.959c-7.457,3.359-10.357,4.307-9.76,5.925c0.615,1.682,14.765,3.507,14.765,3.507s14.938,2.711,15.159-0.287C59.349,52.344,61.938,44.971,73.761,45.401z"},{"datad":"M29.229,61.738c-2.742,0-4.979-2.233-4.979-4.979c0-2.747,2.237-4.981,4.979-4.981c0.987,0,1.902,0.289,2.672,0.782l4.487-2.158c-1.752-1.975-4.313-3.222-7.161-3.222c-5.281,0-9.572,4.293-9.572,9.58c0,5.281,4.292,9.577,9.572,9.577c3.688,0,6.89-2.093,8.493-5.148l-4.79-1.106C32.019,61.097,30.701,61.738,29.229,61.738z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (217, 'Airplanes: light plane', '<path fill="#FFFFFF" d="M75.256,38.772H55.284c0-0.003,0-0.007,0-0.01v-0.941h-0.008c-0.021-0.146-0.083-0.317-0.177-0.501	c-0.211-1.695-0.657-4.573-1.447-7.065h7.854c0.558,0,1.01-0.451,1.01-1.008c0-0.558-0.452-1.009-1.01-1.009H52.88	c-0.743-1.589-1.695-2.714-2.903-2.714c-1.221,0-2.157,1.124-2.871,2.714h-8.41c-0.558,0-1.009,0.451-1.009,1.009	c0,0.557,0.451,1.008,1.009,1.008h7.676c-0.779,2.617-1.164,5.659-1.325,7.312c-0.029,0.087-0.052,0.17-0.063,0.243l-0.006,0.517	c0,0.003,0,0.006,0,0.008l-0.002,0.379l-0.001,0.049l0,0v0.009H24.946c-2.148,0-3.83,1.33-3.83,3.026v4.931	c0,1.67,1.631,2.984,3.73,3.026l20.925,3.962c0.216,2.189,0.465,4.479,0.731,6.706l-8.028,1.118v5.982l8.767,0.751	c0.028,0.002,0.058,0.004,0.086,0.004c0.057,0,0.107-0.021,0.162-0.031c-0.002,0.227-0.007,0.451-0.007,0.683	c0,1.318,0.059,2.568,0.164,3.521c0.105,0.946,0.35,3.161,2.37,3.161s2.265-2.215,2.37-3.161c0.085-0.765,0.138-1.724,0.154-2.753	c0.075-0.44,0.152-0.92,0.231-1.435c0.027,0.002,0.048,0.015,0.074,0.015c0.028,0,0.056-0.001,0.085-0.003l8.939-0.75v-5.985	l-8.088-1.103c0-0.002,0-0.003,0-0.004c0.25-2.21,0.487-4.511,0.693-6.726l20.88-3.953c2.1-0.042,3.73-1.354,3.73-3.026v-4.931	C79.086,40.102,77.404,38.772,75.256,38.772z M46.992,38.133c0.21-0.331,1.123-1.564,3.014-1.564c1.725,0,2.816,1.027,3.191,1.506	c0.013,0.13,0.021,0.252,0.028,0.365c-0.332-0.02-0.775-0.104-1.209-0.186c-0.606-0.116-1.232-0.237-1.796-0.237	c-0.558,0-1.199,0.122-1.817,0.239c-0.51,0.098-1.04,0.198-1.412,0.205L46.992,38.133L46.992,38.133z"/>', 'lightplane-2', '[{"datad":"M75.256,38.772H55.284c0-0.003,0-0.007,0-0.01v-0.941h-0.008c-0.021-0.146-0.083-0.317-0.177-0.501 c-0.211-1.695-0.657-4.573-1.447-7.065h7.854c0.558,0,1.01-0.451,1.01-1.008c0-0.558-0.452-1.009-1.01-1.009H52.88 c-0.743-1.589-1.695-2.714-2.903-2.714c-1.221,0-2.157,1.124-2.871,2.714h-8.41c-0.558,0-1.009,0.451-1.009,1.009 c0,0.557,0.451,1.008,1.009,1.008h7.676c-0.779,2.617-1.164,5.659-1.325,7.312c-0.029,0.087-0.052,0.17-0.063,0.243l-0.006,0.517 c0,0.003,0,0.006,0,0.008l-0.002,0.379l-0.001,0.049l0,0v0.009H24.946c-2.148,0-3.83,1.33-3.83,3.026v4.931 c0,1.67,1.631,2.984,3.73,3.026l20.925,3.962c0.216,2.189,0.465,4.479,0.731,6.706l-8.028,1.118v5.982l8.767,0.751 c0.028,0.002,0.058,0.004,0.086,0.004c0.057,0,0.107-0.021,0.162-0.031c-0.002,0.227-0.007,0.451-0.007,0.683 c0,1.318,0.059,2.568,0.164,3.521c0.105,0.946,0.35,3.161,2.37,3.161s2.265-2.215,2.37-3.161c0.085-0.765,0.138-1.724,0.154-2.753 c0.075-0.44,0.152-0.92,0.231-1.435c0.027,0.002,0.048,0.015,0.074,0.015c0.028,0,0.056-0.001,0.085-0.003l8.939-0.75v-5.985 l-8.088-1.103c0-0.002,0-0.003,0-0.004c0.25-2.21,0.487-4.511,0.693-6.726l20.88-3.953c2.1-0.042,3.73-1.354,3.73-3.026v-4.931 C79.086,40.102,77.404,38.772,75.256,38.772z M46.992,38.133c0.21-0.331,1.123-1.564,3.014-1.564c1.725,0,2.816,1.027,3.191,1.506 c0.013,0.13,0.021,0.252,0.028,0.365c-0.332-0.02-0.775-0.104-1.209-0.186c-0.606-0.116-1.232-0.237-1.796-0.237 c-0.558,0-1.199,0.122-1.817,0.239c-0.51,0.098-1.04,0.198-1.412,0.205L46.992,38.133L46.992,38.133z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (216, 'Stun grenade', '<path fill="#FFFFFF" d="M80.508,39.876c-0.615-0.569-1.306-1.042-2.058-1.407c0.559-2.177-0.117-4.512-1.784-6.054		c-1.142-1.057-2.629-1.641-4.19-1.641c-1.505,0-2.919,0.528-4.035,1.497c-0.25-0.302-0.52-0.588-0.807-0.854		c-1.658-1.536-3.81-2.383-6.052-2.383c-0.062,0-0.123,0.002-0.184,0.003c0.154-0.239,0.215-0.537,0.139-0.833		c-0.459-1.803-1.595-3.321-3.194-4.272c-1.081-0.64-2.307-0.978-3.549-0.978c-0.581,0-1.164,0.074-1.734,0.219		c-0.838,0.213-1.644,0.59-2.348,1.095c-2.342-1.456-5.026-2.224-7.784-2.224c-1.227,0-2.451,0.154-3.642,0.458		c-3.816,0.974-7.025,3.375-9.034,6.762c-1.999,3.366-2.574,7.307-1.624,11.104c-3.801,2.349-5.596,6.782-4.481,11.155		c0.118,0.467,0.541,0.796,1.023,0.796c0.088,0,0.177-0.012,0.262-0.034c0.273-0.069,0.504-0.241,0.648-0.484		c0.144-0.242,0.184-0.527,0.114-0.801c-0.938-3.68,0.77-7.454,4.155-9.177c0.46-0.234,0.683-0.761,0.53-1.255		c-0.074-0.24-0.136-0.456-0.187-0.655c-0.833-3.268-0.344-6.667,1.377-9.568c1.721-2.9,4.469-4.958,7.738-5.791		c1.019-0.261,2.069-0.393,3.119-0.393c2.601,0,5.105,0.791,7.244,2.285c0.179,0.126,0.388,0.191,0.604,0.191		c0.268,0,0.522-0.1,0.719-0.281c0.586-0.542,1.309-0.934,2.087-1.131c0.398-0.102,0.805-0.154,1.21-0.154		c0.864,0,1.719,0.236,2.471,0.683c1.116,0.662,1.907,1.719,2.229,2.976c0.049,0.194,0.15,0.362,0.288,0.495		c-1.829,0.366-3.495,1.286-4.787,2.681c-0.191,0.208-0.291,0.477-0.28,0.759c0.012,0.282,0.132,0.543,0.339,0.735		c0.195,0.181,0.449,0.282,0.718,0.282c0.292,0,0.576-0.124,0.775-0.339c1.239-1.339,2.927-2.115,4.749-2.186		c0.09-0.003,0.179-0.005,0.268-0.005c1.724,0,3.37,0.646,4.638,1.819c0.476,0.44,0.889,0.952,1.227,1.521		c0.184,0.307,0.506,0.5,0.86,0.516c0.014,0,0.034,0.001,0.054,0.001c0.333,0,0.652-0.163,0.85-0.437		c0.111-0.154,0.209-0.276,0.311-0.383c0.764-0.825,1.85-1.298,2.978-1.298c1.026,0,2.003,0.382,2.753,1.077		c1.287,1.191,1.659,3.062,0.932,4.658c-0.121,0.271-0.126,0.583-0.009,0.857c0.119,0.274,0.349,0.486,0.629,0.582		c0.859,0.293,1.63,0.753,2.29,1.364c1.266,1.171,1.996,2.766,2.064,4.486c0.064,1.722-0.542,3.366-1.713,4.63		c-0.396,0.427-0.369,1.097,0.057,1.494c0.198,0.182,0.454,0.281,0.72,0.281c0.293,0,0.576-0.122,0.774-0.34		c1.555-1.677,2.361-3.861,2.273-6.148C83.16,43.546,82.186,41.43,80.508,39.876z"/>	<path fill="#FFFFFF" d="M66.41,47.613c-0.297-2.114-2.085-3.79-4.256-3.899c-0.077-0.005-0.152-0.006-0.229-0.006		c-1.447,0-2.813,0.699-3.658,1.854c-0.486-0.2-1-0.314-1.535-0.342c-0.08-0.003-0.157-0.004-0.237-0.004		c-1.169,0-2.29,0.43-3.156,1.211c-0.932,0.842-1.481,1.997-1.547,3.254c-0.027,0.582,0.421,1.08,1.003,1.109		c0.018,0.002,0.037,0.002,0.055,0.002c0.563,0,1.026-0.442,1.056-1.004c0.036-0.69,0.338-1.327,0.852-1.791		c0.484-0.437,1.082-0.667,1.729-0.667c0.045,0,0.091,0.001,0.14,0.003c0.518,0.028,1.008,0.204,1.419,0.514		c0.182,0.136,0.407,0.212,0.636,0.212c0.1,0,0.198-0.013,0.294-0.043c0.316-0.09,0.576-0.329,0.694-0.638		c0.359-0.944,1.245-1.555,2.255-1.555c0.041,0,0.084,0.001,0.125,0.003c1.329,0.069,2.357,1.205,2.288,2.534		c-0.005,0.088-0.014,0.175-0.027,0.263c-0.058,0.363,0.082,0.735,0.363,0.973c0.189,0.159,0.432,0.247,0.681,0.247		c0.116,0,0.229-0.018,0.338-0.055c0.139-0.047,0.278-0.07,0.421-0.07c0.022,0,0.044,0,0.065,0.002		c0.349,0.017,0.67,0.17,0.904,0.429c0.232,0.26,0.352,0.594,0.334,0.943c-0.015,0.281,0.082,0.555,0.271,0.763		c0.189,0.211,0.449,0.333,0.732,0.349c0.012,0,0.032,0.002,0.053,0.002c0.564,0,1.027-0.44,1.057-1.003		c0.046-0.915-0.265-1.792-0.877-2.469C68.067,48.08,67.274,47.688,66.41,47.613z"/>	<path fill="#FFFFFF" d="M47.429,40.243c0.187,0.246,0.496,0.4,0.824,0.414c0.353-0.001,0.669-0.161,0.865-0.425		c0.621-0.838,1.471-1.465,2.458-1.814c0.568-0.199,1.158-0.301,1.75-0.301c0.781-0.001,1.541,0.172,2.256,0.514		c1.263,0.603,2.216,1.661,2.682,2.98c0.146,0.423,0.548,0.707,0.997,0.707c0.118,0,0.238-0.02,0.351-0.06		c0.267-0.095,0.481-0.286,0.603-0.542c0.121-0.254,0.138-0.542,0.042-0.809c-0.652-1.851-1.99-3.337-3.763-4.185		C55.49,36.243,54.426,36,53.327,36c-0.832,0-1.657,0.143-2.453,0.424c-0.978,0.344-1.874,0.893-2.619,1.601		c-1.289-1.174-2.989-1.839-4.74-1.839c-0.8,0-1.593,0.136-2.354,0.404c-3.42,1.208-5.35,4.868-4.498,8.336		c-0.144,0.038-0.287,0.082-0.427,0.132c-1.294,0.456-2.333,1.39-2.925,2.627c-0.592,1.239-0.666,2.634-0.208,3.928		c0.148,0.423,0.548,0.706,0.997,0.706c0.119,0,0.238-0.021,0.352-0.061c0.267-0.094,0.481-0.287,0.603-0.542		c0.121-0.254,0.137-0.541,0.042-0.809c-0.269-0.76-0.225-1.582,0.123-2.31c0.349-0.73,0.961-1.279,1.722-1.548		c0.32-0.114,0.656-0.17,0.997-0.17c0.041,0,0.083,0,0.125,0.003h0.01h0.024c0.368,0,0.693-0.175,0.893-0.476		c0.202-0.309,0.229-0.706,0.069-1.038c-0.077-0.16-0.145-0.325-0.204-0.488c-0.905-2.565,0.445-5.39,3.011-6.295		c0.533-0.19,1.088-0.285,1.649-0.285C45.059,38.301,46.486,39.009,47.429,40.243z"/>	<path fill="#FFFFFF" d="M53.039,58.168v-2.84c0-1.064-0.862-1.926-1.926-1.926c-1.063,0-1.925,0.861-1.925,1.926v2.84		c-3.216,0.852-5.59,3.773-5.59,7.257v9.734c0,4.15,3.365,7.516,7.515,7.516c4.151,0,7.516-3.365,7.516-7.516v-9.734		C58.629,61.941,56.256,59.02,53.039,58.168z"/>', 'stun-2', '[{"datad":"M80.508,39.876c-0.615-0.569-1.306-1.042-2.058-1.407c0.559-2.177-0.117-4.512-1.784-6.054  c-1.142-1.057-2.629-1.641-4.19-1.641c-1.505,0-2.919,0.528-4.035,1.497c-0.25-0.302-0.52-0.588-0.807-0.854  c-1.658-1.536-3.81-2.383-6.052-2.383c-0.062,0-0.123,0.002-0.184,0.003c0.154-0.239,0.215-0.537,0.139-0.833  c-0.459-1.803-1.595-3.321-3.194-4.272c-1.081-0.64-2.307-0.978-3.549-0.978c-0.581,0-1.164,0.074-1.734,0.219  c-0.838,0.213-1.644,0.59-2.348,1.095c-2.342-1.456-5.026-2.224-7.784-2.224c-1.227,0-2.451,0.154-3.642,0.458  c-3.816,0.974-7.025,3.375-9.034,6.762c-1.999,3.366-2.574,7.307-1.624,11.104c-3.801,2.349-5.596,6.782-4.481,11.155  c0.118,0.467,0.541,0.796,1.023,0.796c0.088,0,0.177-0.012,0.262-0.034c0.273-0.069,0.504-0.241,0.648-0.484  c0.144-0.242,0.184-0.527,0.114-0.801c-0.938-3.68,0.77-7.454,4.155-9.177c0.46-0.234,0.683-0.761,0.53-1.255  c-0.074-0.24-0.136-0.456-0.187-0.655c-0.833-3.268-0.344-6.667,1.377-9.568c1.721-2.9,4.469-4.958,7.738-5.791  c1.019-0.261,2.069-0.393,3.119-0.393c2.601,0,5.105,0.791,7.244,2.285c0.179,0.126,0.388,0.191,0.604,0.191  c0.268,0,0.522-0.1,0.719-0.281c0.586-0.542,1.309-0.934,2.087-1.131c0.398-0.102,0.805-0.154,1.21-0.154  c0.864,0,1.719,0.236,2.471,0.683c1.116,0.662,1.907,1.719,2.229,2.976c0.049,0.194,0.15,0.362,0.288,0.495  c-1.829,0.366-3.495,1.286-4.787,2.681c-0.191,0.208-0.291,0.477-0.28,0.759c0.012,0.282,0.132,0.543,0.339,0.735  c0.195,0.181,0.449,0.282,0.718,0.282c0.292,0,0.576-0.124,0.775-0.339c1.239-1.339,2.927-2.115,4.749-2.186  c0.09-0.003,0.179-0.005,0.268-0.005c1.724,0,3.37,0.646,4.638,1.819c0.476,0.44,0.889,0.952,1.227,1.521  c0.184,0.307,0.506,0.5,0.86,0.516c0.014,0,0.034,0.001,0.054,0.001c0.333,0,0.652-0.163,0.85-0.437  c0.111-0.154,0.209-0.276,0.311-0.383c0.764-0.825,1.85-1.298,2.978-1.298c1.026,0,2.003,0.382,2.753,1.077  c1.287,1.191,1.659,3.062,0.932,4.658c-0.121,0.271-0.126,0.583-0.009,0.857c0.119,0.274,0.349,0.486,0.629,0.582  c0.859,0.293,1.63,0.753,2.29,1.364c1.266,1.171,1.996,2.766,2.064,4.486c0.064,1.722-0.542,3.366-1.713,4.63  c-0.396,0.427-0.369,1.097,0.057,1.494c0.198,0.182,0.454,0.281,0.72,0.281c0.293,0,0.576-0.122,0.774-0.34  c1.555-1.677,2.361-3.861,2.273-6.148C83.16,43.546,82.186,41.43,80.508,39.876z"},{"datad":"M66.41,47.613c-0.297-2.114-2.085-3.79-4.256-3.899c-0.077-0.005-0.152-0.006-0.229-0.006  c-1.447,0-2.813,0.699-3.658,1.854c-0.486-0.2-1-0.314-1.535-0.342c-0.08-0.003-0.157-0.004-0.237-0.004  c-1.169,0-2.29,0.43-3.156,1.211c-0.932,0.842-1.481,1.997-1.547,3.254c-0.027,0.582,0.421,1.08,1.003,1.109  c0.018,0.002,0.037,0.002,0.055,0.002c0.563,0,1.026-0.442,1.056-1.004c0.036-0.69,0.338-1.327,0.852-1.791  c0.484-0.437,1.082-0.667,1.729-0.667c0.045,0,0.091,0.001,0.14,0.003c0.518,0.028,1.008,0.204,1.419,0.514  c0.182,0.136,0.407,0.212,0.636,0.212c0.1,0,0.198-0.013,0.294-0.043c0.316-0.09,0.576-0.329,0.694-0.638  c0.359-0.944,1.245-1.555,2.255-1.555c0.041,0,0.084,0.001,0.125,0.003c1.329,0.069,2.357,1.205,2.288,2.534  c-0.005,0.088-0.014,0.175-0.027,0.263c-0.058,0.363,0.082,0.735,0.363,0.973c0.189,0.159,0.432,0.247,0.681,0.247  c0.116,0,0.229-0.018,0.338-0.055c0.139-0.047,0.278-0.07,0.421-0.07c0.022,0,0.044,0,0.065,0.002  c0.349,0.017,0.67,0.17,0.904,0.429c0.232,0.26,0.352,0.594,0.334,0.943c-0.015,0.281,0.082,0.555,0.271,0.763  c0.189,0.211,0.449,0.333,0.732,0.349c0.012,0,0.032,0.002,0.053,0.002c0.564,0,1.027-0.44,1.057-1.003  c0.046-0.915-0.265-1.792-0.877-2.469C68.067,48.08,67.274,47.688,66.41,47.613z"},{"datad":"M47.429,40.243c0.187,0.246,0.496,0.4,0.824,0.414c0.353-0.001,0.669-0.161,0.865-0.425  c0.621-0.838,1.471-1.465,2.458-1.814c0.568-0.199,1.158-0.301,1.75-0.301c0.781-0.001,1.541,0.172,2.256,0.514  c1.263,0.603,2.216,1.661,2.682,2.98c0.146,0.423,0.548,0.707,0.997,0.707c0.118,0,0.238-0.02,0.351-0.06  c0.267-0.095,0.481-0.286,0.603-0.542c0.121-0.254,0.138-0.542,0.042-0.809c-0.652-1.851-1.99-3.337-3.763-4.185  C55.49,36.243,54.426,36,53.327,36c-0.832,0-1.657,0.143-2.453,0.424c-0.978,0.344-1.874,0.893-2.619,1.601  c-1.289-1.174-2.989-1.839-4.74-1.839c-0.8,0-1.593,0.136-2.354,0.404c-3.42,1.208-5.35,4.868-4.498,8.336  c-0.144,0.038-0.287,0.082-0.427,0.132c-1.294,0.456-2.333,1.39-2.925,2.627c-0.592,1.239-0.666,2.634-0.208,3.928  c0.148,0.423,0.548,0.706,0.997,0.706c0.119,0,0.238-0.021,0.352-0.061c0.267-0.094,0.481-0.287,0.603-0.542  c0.121-0.254,0.137-0.541,0.042-0.809c-0.269-0.76-0.225-1.582,0.123-2.31c0.349-0.73,0.961-1.279,1.722-1.548  c0.32-0.114,0.656-0.17,0.997-0.17c0.041,0,0.083,0,0.125,0.003h0.01h0.024c0.368,0,0.693-0.175,0.893-0.476  c0.202-0.309,0.229-0.706,0.069-1.038c-0.077-0.16-0.145-0.325-0.204-0.488c-0.905-2.565,0.445-5.39,3.011-6.295  c0.533-0.19,1.088-0.285,1.649-0.285C45.059,38.301,46.486,39.009,47.429,40.243z"},{"datad":"M53.039,58.168v-2.84c0-1.064-0.862-1.926-1.926-1.926c-1.063,0-1.925,0.861-1.925,1.926v2.84  c-3.216,0.852-5.59,3.773-5.59,7.257v9.734c0,4.15,3.365,7.516,7.515,7.516c4.151,0,7.516-3.365,7.516-7.516v-9.734  C58.629,61.941,56.256,59.02,53.039,58.168z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (165, 'Destruction', '<path fill="#FFFFFF" d="M42.363,58.43c-0.874,0-1.583,0.71-1.583,1.583v3.609c0,0.873,0.709,1.583,1.583,1.583 s1.583-0.709,1.583-1.583v-3.609C43.945,59.14,43.236,58.43,42.363,58.43z"/> <path fill="#FFFFFF" d="M58.352,51.025c0.874,0,1.582-0.708,1.582-1.583v-3.607c0-0.874-0.708-1.583-1.582-1.583 c-0.875,0-1.583,0.709-1.583,1.583v3.607C56.769,50.316,57.477,51.025,58.352,51.025z"/> <path fill="#FFFFFF" d="M65.665,65.205c0.874,0,1.583-0.709,1.583-1.583v-3.609c0-0.873-0.709-1.583-1.583-1.583 s-1.583,0.71-1.583,1.583v3.609C64.082,64.495,64.791,65.205,65.665,65.205z"/> <path fill="#FFFFFF" d="M65.665,51.025c0.874,0,1.583-0.708,1.583-1.583v-3.607c0-0.874-0.709-1.583-1.583-1.583 s-1.583,0.709-1.583,1.583v3.607C64.082,50.316,64.791,51.025,65.665,51.025z"/> <path fill="#FFFFFF" d="M72.633,36.682H53.172c-1.806-5.92-7.308-10.229-13.82-10.229c-7.98,0-14.45,6.469-14.45,14.45 c0,6.019,3.68,11.174,8.911,13.348v18.144c0,0.873,0.709,1.583,1.583,1.583h14.059h6.49h16.689c0.874,0,1.583-0.709,1.583-1.583 V38.265C74.216,37.39,73.507,36.682,72.633,36.682z M33.812,49.65c-0.683,0.029-1.38-0.204-1.929-0.702l-0.019-0.019 c-1.071-0.975-1.098-2.651-0.103-3.704l2.05-2.165l1.579-1.667L33.812,39.9l-2.527-2.393c-1.051-0.996-1.096-2.653-0.102-3.703 l0.018-0.021c0.995-1.049,2.653-1.095,3.703-0.101l3.167,3l0.939,0.889l0.842-0.889l3.046-3.217 c0.995-1.051,2.652-1.096,3.703-0.102l0.019,0.019c0.925,0.876,1.069,2.263,0.413,3.3c-0.089,0.141-0.191,0.276-0.311,0.402 l-2.616,2.764l-1.272,1.344l3.833,3.628c1.051,0.996,1.096,2.654,0.101,3.703l-0.018,0.021c-0.995,1.049-2.653,1.095-3.703,0.1 l-3.833-3.628l-2.238,2.364l-1.39,1.468C35.102,49.36,34.463,49.624,33.812,49.65z M58.566,70.45h-8.97V59.556h8.97V70.45z M71.051,70.811h-9.318V57.973c0-0.873-0.709-1.583-1.583-1.583H48.013c-0.874,0-1.583,0.71-1.583,1.583v12.838h-9.453V55.143 c0.774,0.128,1.563,0.212,2.374,0.212c7.981,0,14.45-6.47,14.45-14.452c0-0.356-0.027-0.705-0.053-1.054h0.75 c0.003,0,0.006-0.001,0.008-0.001h16.544V70.811z"/>', 'destroy-2', '[{"datad":"M42.363,58.43c-0.874,0-1.583,0.71-1.583,1.583v3.609c0,0.873,0.709,1.583,1.583,1.583 s1.583-0.709,1.583-1.583v-3.609C43.945,59.14,43.236,58.43,42.363,58.43z"},{"datad":"M58.352,51.025c0.874,0,1.582-0.708,1.582-1.583v-3.607c0-0.874-0.708-1.583-1.582-1.583 c-0.875,0-1.583,0.709-1.583,1.583v3.607C56.769,50.316,57.477,51.025,58.352,51.025z"},{"datad":"M65.665,65.205c0.874,0,1.583-0.709,1.583-1.583v-3.609c0-0.873-0.709-1.583-1.583-1.583 s-1.583,0.71-1.583,1.583v3.609C64.082,64.495,64.791,65.205,65.665,65.205z"},{"datad":"M65.665,51.025c0.874,0,1.583-0.708,1.583-1.583v-3.607c0-0.874-0.709-1.583-1.583-1.583 s-1.583,0.709-1.583,1.583v3.607C64.082,50.316,64.791,51.025,65.665,51.025z"},{"datad":"M72.633,36.682H53.172c-1.806-5.92-7.308-10.229-13.82-10.229c-7.98,0-14.45,6.469-14.45,14.45 c0,6.019,3.68,11.174,8.911,13.348v18.144c0,0.873,0.709,1.583,1.583,1.583h14.059h6.49h16.689c0.874,0,1.583-0.709,1.583-1.583 V38.265C74.216,37.39,73.507,36.682,72.633,36.682z M33.812,49.65c-0.683,0.029-1.38-0.204-1.929-0.702l-0.019-0.019 c-1.071-0.975-1.098-2.651-0.103-3.704l2.05-2.165l1.579-1.667L33.812,39.9l-2.527-2.393c-1.051-0.996-1.096-2.653-0.102-3.703 l0.018-0.021c0.995-1.049,2.653-1.095,3.703-0.101l3.167,3l0.939,0.889l0.842-0.889l3.046-3.217 c0.995-1.051,2.652-1.096,3.703-0.102l0.019,0.019c0.925,0.876,1.069,2.263,0.413,3.3c-0.089,0.141-0.191,0.276-0.311,0.402 l-2.616,2.764l-1.272,1.344l3.833,3.628c1.051,0.996,1.096,2.654,0.101,3.703l-0.018,0.021c-0.995,1.049-2.653,1.095-3.703,0.1 l-3.833-3.628l-2.238,2.364l-1.39,1.468C35.102,49.36,34.463,49.624,33.812,49.65z M58.566,70.45h-8.97V59.556h8.97V70.45z M71.051,70.811h-9.318V57.973c0-0.873-0.709-1.583-1.583-1.583H48.013c-0.874,0-1.583,0.71-1.583,1.583v12.838h-9.453V55.143 c0.774,0.128,1.563,0.212,2.374,0.212c7.981,0,14.45-6.47,14.45-14.452c0-0.356-0.027-0.705-0.053-1.054h0.75 c0.003,0,0.006-0.001,0.008-0.001h16.544V70.811z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (215, 'Machinegun', '<path fill="#FFFFFF" d="M83.812,39.833c0.931,0,1.685-0.754,1.685-1.685c0-0.93-0.754-1.685-1.685-1.685H63.442v-1.378h20.369		c0.931,0,1.685-0.754,1.685-1.685s-0.754-1.685-1.685-1.685H63.442v-0.613c0-1.353-1.098-2.45-2.45-2.45h-5.36		c-1.354,0-2.45,1.097-2.45,2.45V33.4H35.722c-0.338,0-0.613,0.274-0.613,0.613v6.738c0,0.339,0.274,0.613,0.613,0.613h17.459v3.675		c0,1.354,1.097,2.451,2.45,2.451h5.36c1.353,0,2.45-1.098,2.45-2.451v-0.306h20.369c0.931,0,1.685-0.755,1.685-1.685		c0-0.931-0.754-1.685-1.685-1.685H63.442v-1.532H83.812z"/>	<path fill="#FFFFFF" d="M27.995,40.598h4.358c0.338,0,0.613-0.274,0.613-0.612v-5.973c0-0.338-0.274-0.613-0.613-0.613h-4.358		c-0.338,0-0.613,0.274-0.613,0.613v5.973C27.382,40.324,27.656,40.598,27.995,40.598z"/>	<path fill="#FFFFFF" d="M37.79,31.715c0.973,0,1.761-0.789,1.761-1.761v-0.919c0-0.973-0.789-1.761-1.761-1.761		s-1.761,0.789-1.761,1.761v0.919C36.028,30.927,36.817,31.715,37.79,31.715z"/>	<path fill="#FFFFFF" d="M48.625,31.715h0.076c0.867,0,1.57-0.703,1.57-1.57s-0.703-1.57-1.57-1.57h-0.076		c-0.867,0-1.57,0.703-1.57,1.57S47.758,31.715,48.625,31.715z"/>	<path fill="#FFFFFF" d="M59.741,71.076L48.127,49.634h1.837c1.015,0,1.838-0.822,1.838-1.838v-2.909		c0-1.016-0.823-1.838-1.838-1.838H38.249c-1.015,0-1.838,0.822-1.838,1.838v2.909c0,1.016,0.823,1.838,1.838,1.838h3.174		L29.81,71.075h-0.667c-0.973,0-1.761,0.788-1.761,1.761c0,0.974,0.789,1.762,1.761,1.762h4.824c0.973,0,1.761-0.788,1.761-1.762		c0-0.965-0.777-1.746-1.74-1.759l8.548-15.782v18.384h-0.459c-0.973,0-1.761,0.788-1.761,1.761c0,0.974,0.789,1.762,1.761,1.762		h4.824c0.973,0,1.761-0.788,1.761-1.762c0-0.973-0.788-1.761-1.761-1.761h-0.689V53.817l9.347,17.258h-0.655		c-0.973,0-1.762,0.788-1.762,1.761c0,0.974,0.789,1.762,1.762,1.762h4.824c0.973,0,1.761-0.788,1.761-1.762		C61.489,71.868,60.708,71.084,59.741,71.076z"/>', 'machinegun-2', '[{"datad":"M83.812,39.833c0.931,0,1.685-0.754,1.685-1.685c0-0.93-0.754-1.685-1.685-1.685H63.442v-1.378h20.369  c0.931,0,1.685-0.754,1.685-1.685s-0.754-1.685-1.685-1.685H63.442v-0.613c0-1.353-1.098-2.45-2.45-2.45h-5.36  c-1.354,0-2.45,1.097-2.45,2.45V33.4H35.722c-0.338,0-0.613,0.274-0.613,0.613v6.738c0,0.339,0.274,0.613,0.613,0.613h17.459v3.675  c0,1.354,1.097,2.451,2.45,2.451h5.36c1.353,0,2.45-1.098,2.45-2.451v-0.306h20.369c0.931,0,1.685-0.755,1.685-1.685  c0-0.931-0.754-1.685-1.685-1.685H63.442v-1.532H83.812z"},{"datad":"M27.995,40.598h4.358c0.338,0,0.613-0.274,0.613-0.612v-5.973c0-0.338-0.274-0.613-0.613-0.613h-4.358  c-0.338,0-0.613,0.274-0.613,0.613v5.973C27.382,40.324,27.656,40.598,27.995,40.598z"},{"datad":"M37.79,31.715c0.973,0,1.761-0.789,1.761-1.761v-0.919c0-0.973-0.789-1.761-1.761-1.761  s-1.761,0.789-1.761,1.761v0.919C36.028,30.927,36.817,31.715,37.79,31.715z"},{"datad":"M48.625,31.715h0.076c0.867,0,1.57-0.703,1.57-1.57s-0.703-1.57-1.57-1.57h-0.076  c-0.867,0-1.57,0.703-1.57,1.57S47.758,31.715,48.625,31.715z"},{"datad":"M59.741,71.076L48.127,49.634h1.837c1.015,0,1.838-0.822,1.838-1.838v-2.909  c0-1.016-0.823-1.838-1.838-1.838H38.249c-1.015,0-1.838,0.822-1.838,1.838v2.909c0,1.016,0.823,1.838,1.838,1.838h3.174  L29.81,71.075h-0.667c-0.973,0-1.761,0.788-1.761,1.761c0,0.974,0.789,1.762,1.761,1.762h4.824c0.973,0,1.761-0.788,1.761-1.762  c0-0.965-0.777-1.746-1.74-1.759l8.548-15.782v18.384h-0.459c-0.973,0-1.761,0.788-1.761,1.761c0,0.974,0.789,1.762,1.761,1.762  h4.824c0.973,0,1.761-0.788,1.761-1.762c0-0.973-0.788-1.761-1.761-1.761h-0.689V53.817l9.347,17.258h-0.655  c-0.973,0-1.762,0.788-1.762,1.761c0,0.974,0.789,1.762,1.762,1.762h4.824c0.973,0,1.761-0.788,1.761-1.762  C61.489,71.868,60.708,71.084,59.741,71.076z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (214, 'Animals: Bear', '<path fill="#FFFFFF" d="M80.544,45.125c-0.061-0.078-1.495-1.891-2.235-2.729c-0.331-0.383-0.551-0.85-0.639-1.349	c-0.091-0.361-0.201-0.717-0.33-1.066c-0.398-1.061-2.235-2.693-3.054-3.391c0.378-0.743,0.502-1.589,0.355-2.409	c-0.126-0.437-0.427-0.801-0.831-1.006c-1.206-0.633-2.38,0.108-3.013,0.765c-5.764-0.877-11.656,0.161-16.773,2.958	c-0.802,0-4.818,0.126-12.461-0.35c-5.571-0.349-11.263,1.59-14.84,5.047c-2.792,2.655-4.297,6.39-4.125,10.239	c0.289,9.829-0.717,15.587-0.729,15.658l-0.042,0.242l1.5,2.187l8.746-0.067v-0.602c0.072-2.277-1.253-3.307-2.132-3.986	c-0.343-0.267-0.692-0.543-0.729-0.688c0.073-0.477,1.861-1.867,3.831-2.977l0.102-0.071l0.175-0.156l1.927,3.747	c-0.211,0.812-0.692,3.233,0.603,4.535l0.174,0.174h9.42l0.133-0.421c0.231-0.882,0.11-1.816-0.338-2.609	c-0.602-1.09-1.807-1.866-3.511-2.294c0.079-0.601,0.26-1.183,0.536-1.723c0.706-1.575,1.298-3.2,1.771-4.86	c1.954-0.048,3.901-0.229,5.83-0.542l-1.307,10.335l0.91,2.138h9.636l0.036-0.559c0.139-2.078-1.366-3.615-2.56-4.216	c0.467-1.777,0.774-3.591,0.916-5.422l0.439-0.38l3.451,6.072c-0.169,0.74-0.52,2.896,0.602,4.318l0.181,0.228h9.462v-0.601	c-0.081-2.381-1.807-4.386-4.149-4.819c-1.072-2.487-2.976-12.588-2.717-13.66c0.398-0.851,0.743-1.725,1.03-2.619	c0.866-0.076,1.694-0.375,2.409-0.868l3.114,1.325h0.107c1.549,0.084,6.686,0.277,7.83-0.801c0.673-0.593,1.16-1.366,1.405-2.229	l0.059-0.277L80.544,45.125z M74.709,42.333c-0.943,0-1.708-0.765-1.708-1.708s0.765-1.708,1.708-1.708s1.708,0.765,1.708,1.708	S75.652,42.333,74.709,42.333z"/>', 'bear-2', '[{"datad":"M80.544,45.125c-0.061-0.078-1.495-1.891-2.235-2.729c-0.331-0.383-0.551-0.85-0.639-1.349 c-0.091-0.361-0.201-0.717-0.33-1.066c-0.398-1.061-2.235-2.693-3.054-3.391c0.378-0.743,0.502-1.589,0.355-2.409 c-0.126-0.437-0.427-0.801-0.831-1.006c-1.206-0.633-2.38,0.108-3.013,0.765c-5.764-0.877-11.656,0.161-16.773,2.958 c-0.802,0-4.818,0.126-12.461-0.35c-5.571-0.349-11.263,1.59-14.84,5.047c-2.792,2.655-4.297,6.39-4.125,10.239 c0.289,9.829-0.717,15.587-0.729,15.658l-0.042,0.242l1.5,2.187l8.746-0.067v-0.602c0.072-2.277-1.253-3.307-2.132-3.986 c-0.343-0.267-0.692-0.543-0.729-0.688c0.073-0.477,1.861-1.867,3.831-2.977l0.102-0.071l0.175-0.156l1.927,3.747 c-0.211,0.812-0.692,3.233,0.603,4.535l0.174,0.174h9.42l0.133-0.421c0.231-0.882,0.11-1.816-0.338-2.609 c-0.602-1.09-1.807-1.866-3.511-2.294c0.079-0.601,0.26-1.183,0.536-1.723c0.706-1.575,1.298-3.2,1.771-4.86 c1.954-0.048,3.901-0.229,5.83-0.542l-1.307,10.335l0.91,2.138h9.636l0.036-0.559c0.139-2.078-1.366-3.615-2.56-4.216 c0.467-1.777,0.774-3.591,0.916-5.422l0.439-0.38l3.451,6.072c-0.169,0.74-0.52,2.896,0.602,4.318l0.181,0.228h9.462v-0.601 c-0.081-2.381-1.807-4.386-4.149-4.819c-1.072-2.487-2.976-12.588-2.717-13.66c0.398-0.851,0.743-1.725,1.03-2.619 c0.866-0.076,1.694-0.375,2.409-0.868l3.114,1.325h0.107c1.549,0.084,6.686,0.277,7.83-0.801c0.673-0.593,1.16-1.366,1.405-2.229 l0.059-0.277L80.544,45.125z M74.709,42.333c-0.943,0-1.708-0.765-1.708-1.708s0.765-1.708,1.708-1.708s1.708,0.765,1.708,1.708 S75.652,42.333,74.709,42.333z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (213, 'Flares', '<path fill="#FFFFFF" d="M68.435,34.655c1.012-1.75,0.411-3.989-1.339-5c-1.751-1.011-3.99-0.411-5.001,1.34c-0.75,1.3-0.611,2.867,0.226,4.002l-9.235,15.996c-0.404,0.7-0.164,1.596,0.536,2c0.23,0.133,0.481,0.196,0.73,0.196c0.506,0,0.997-0.263,1.27-0.732l9.234-15.996C66.258,36.618,67.685,35.955,68.435,34.655z"/><path fill="#FFFFFF" d="M58.271,56.181l15.996-9.235c1.136,0.836,2.703,0.975,4.002,0.224c1.751-1.01,2.351-3.248,1.34-5c-1.011-1.75-3.249-2.35-5-1.339c-1.299,0.75-1.962,2.178-1.806,3.578l-15.996,9.236c-0.7,0.404-0.94,1.299-0.536,2c0.271,0.469,0.763,0.732,1.27,0.732C57.789,56.377,58.041,56.313,58.271,56.181z"/><path fill="#FFFFFF" d="M80.529,55.604c-1.499,0-2.788,0.904-3.354,2.196H58.705c-0.808,0-1.464,0.655-1.464,1.464s0.656,1.465,1.464,1.465h18.471c0.565,1.292,1.854,2.195,3.354,2.195c2.021,0.001,3.661-1.638,3.661-3.659C84.19,57.243,82.552,55.604,80.529,55.604z"/><path fill="#FFFFFF" d="M41.294,57.801H22.823c-0.565-1.292-1.854-2.196-3.354-2.196c-2.021,0-3.66,1.639-3.66,3.661c0,2.021,1.639,3.659,3.661,3.66c1.5,0,2.788-0.904,3.354-2.196h18.47c0.809,0,1.464-0.656,1.464-1.465S42.103,57.801,41.294,57.801z"/><path fill="#FFFFFF" d="M43.192,53.645l-15.997-9.236c0.157-1.401-0.507-2.828-1.806-3.578c-1.75-1.011-3.989-0.411-5,1.339c-1.01,1.751-0.41,3.989,1.34,5c1.299,0.751,2.867,0.612,4.002-0.224l15.996,9.235c0.231,0.133,0.482,0.196,0.731,0.196c0.505,0,0.998-0.264,1.269-0.732C44.132,54.943,43.893,54.049,43.192,53.645z"/><path fill="#FFFFFF" d="M37.679,34.998c0.836-1.135,0.976-2.703,0.225-4.002c-1.011-1.751-3.249-2.351-5-1.34c-1.75,1.011-2.35,3.249-1.339,5c0.75,1.299,2.176,1.963,3.578,1.806l9.236,15.996c0.271,0.47,0.763,0.733,1.27,0.733c0.248,0,0.5-0.064,0.73-0.196c0.7-0.405,0.94-1.301,0.536-2.001L37.679,34.998z"/><path fill="#FFFFFF" d="M53.66,28.735c0-2.021-1.639-3.661-3.66-3.661s-3.661,1.639-3.661,3.661c0,1.5,0.904,2.789,2.197,3.354v18.47c0,0.809,0.655,1.465,1.464,1.465c0.808,0,1.464-0.656,1.464-1.465v-18.47C52.756,31.523,53.66,30.235,53.66,28.735z"/>', 'flares-2', '[{"datad":"M68.435,34.655c1.012-1.75,0.411-3.989-1.339-5c-1.751-1.011-3.99-0.411-5.001,1.34c-0.75,1.3-0.611,2.867,0.226,4.002l-9.235,15.996c-0.404,0.7-0.164,1.596,0.536,2c0.23,0.133,0.481,0.196,0.73,0.196c0.506,0,0.997-0.263,1.27-0.732l9.234-15.996C66.258,36.618,67.685,35.955,68.435,34.655z"},{"datad":"M58.271,56.181l15.996-9.235c1.136,0.836,2.703,0.975,4.002,0.224c1.751-1.01,2.351-3.248,1.34-5c-1.011-1.75-3.249-2.35-5-1.339c-1.299,0.75-1.962,2.178-1.806,3.578l-15.996,9.236c-0.7,0.404-0.94,1.299-0.536,2c0.271,0.469,0.763,0.732,1.27,0.732C57.789,56.377,58.041,56.313,58.271,56.181z"},{"datad":"M80.529,55.604c-1.499,0-2.788,0.904-3.354,2.196H58.705c-0.808,0-1.464,0.655-1.464,1.464s0.656,1.465,1.464,1.465h18.471c0.565,1.292,1.854,2.195,3.354,2.195c2.021,0.001,3.661-1.638,3.661-3.659C84.19,57.243,82.552,55.604,80.529,55.604z"},{"datad":"M41.294,57.801H22.823c-0.565-1.292-1.854-2.196-3.354-2.196c-2.021,0-3.66,1.639-3.66,3.661c0,2.021,1.639,3.659,3.661,3.66c1.5,0,2.788-0.904,3.354-2.196h18.47c0.809,0,1.464-0.656,1.464-1.465S42.103,57.801,41.294,57.801z"},{"datad":"M43.192,53.645l-15.997-9.236c0.157-1.401-0.507-2.828-1.806-3.578c-1.75-1.011-3.989-0.411-5,1.339c-1.01,1.751-0.41,3.989,1.34,5c1.299,0.751,2.867,0.612,4.002-0.224l15.996,9.235c0.231,0.133,0.482,0.196,0.731,0.196c0.505,0,0.998-0.264,1.269-0.732C44.132,54.943,43.893,54.049,43.192,53.645z"},{"datad":"M37.679,34.998c0.836-1.135,0.976-2.703,0.225-4.002c-1.011-1.751-3.249-2.351-5-1.34c-1.75,1.011-2.35,3.249-1.339,5c0.75,1.299,2.176,1.963,3.578,1.806l9.236,15.996c0.271,0.47,0.763,0.733,1.27,0.733c0.248,0,0.5-0.064,0.73-0.196c0.7-0.405,0.94-1.301,0.536-2.001L37.679,34.998z"},{"datad":"M53.66,28.735c0-2.021-1.639-3.661-3.66-3.661s-3.661,1.639-3.661,3.661c0,1.5,0.904,2.789,2.197,3.354v18.47c0,0.809,0.655,1.465,1.464,1.465c0.808,0,1.464-0.656,1.464-1.465v-18.47C52.756,31.523,53.66,30.235,53.66,28.735z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (212, 'Supply', '<path fill="#FFFFFF" d="M26.839,34.492v29.016L50,72.713l23.161-9.205V34.492L50,25.288L26.839,34.492z M65.711,36.235L50,42.48l-15.711-6.245L50,29.991L65.711,36.235z M31.211,39.717l16.603,6.598v20.826l-16.603-6.599V39.717z M52.186,67.139V46.314l16.604-6.597v20.825L52.186,67.139z"/><path fill="#FFFFFF" d="M55.324,35.663v1.21c0,0.157-0.203,0.289-0.456,0.289h-3.494v2.272c0,0.165-0.202,0.289-0.443,0.289h-1.86c-0.241,0-0.444-0.124-0.444-0.289v-2.272h-3.493c-0.253,0-0.456-0.132-0.456-0.289v-1.21c0-0.164,0.203-0.288,0.456-0.288h3.493v-2.28c0-0.157,0.203-0.289,0.444-0.289h1.86c0.241,0,0.443,0.132,0.443,0.289v2.28h3.494C55.121,35.375,55.324,35.499,55.324,35.663z"/>', 'supply-2', '[{"datad":"M26.839,34.492v29.016L50,72.713l23.161-9.205V34.492L50,25.288L26.839,34.492z M65.711,36.235L50,42.48l-15.711-6.245L50,29.991L65.711,36.235z M31.211,39.717l16.603,6.598v20.826l-16.603-6.599V39.717z M52.186,67.139V46.314l16.604-6.597v20.825L52.186,67.139z"},{"datad":"M55.324,35.663v1.21c0,0.157-0.203,0.289-0.456,0.289h-3.494v2.272c0,0.165-0.202,0.289-0.443,0.289h-1.86c-0.241,0-0.444-0.124-0.444-0.289v-2.272h-3.493c-0.253,0-0.456-0.132-0.456-0.289v-1.21c0-0.164,0.203-0.288,0.456-0.288h3.493v-2.28c0-0.157,0.203-0.289,0.444-0.289h1.86c0.241,0,0.443,0.132,0.443,0.289v2.28h3.494C55.121,35.375,55.324,35.499,55.324,35.663z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (211, 'Animals: Wolf', '<path fill="#FFFFFF" d="M73.644,48.18L72.464,47l-4.455-24.901c-0.065-0.262-0.197-0.459-0.459-0.524c-0.263-0.065-0.46,0-0.655,0.197l-9.764,9.895l-0.263-0.263c-0.13-0.131-0.262-0.196-0.458-0.196h-6.619h-6.88c-0.197,0-0.328,0.065-0.458,0.196l-0.262,0.263l-9.764-9.895c-0.197-0.197-0.459-0.262-0.655-0.197c-0.263,0.065-0.393,0.262-0.459,0.524L26.856,47l-1.18,1.18c-0.131,0.131-0.197,0.262-0.197,0.458v7.863c0,0.197,0.131,0.394,0.262,0.525l14.088,9.477l3.979-8.212l-0.645-2.638l-4.568-2.664c-0.472-0.283-1.686-0.77-1.591-1.339c0.109-0.545,0.608-2.932,1.19-2.932h5.063c0.53,0,0.955,2.343,1.058,2.854l1.626,6.568c0.113,0.288-0.005,0.604-0.089,0.772l-3.59,12.238l0.646,5.533c0,0.065,0.065,0.065,0.131,0.131c0,0,0,0.066,0.066,0.066c0.065,0.065,0.131,0.065,0.197,0.131l5.831,1.965c0.132,0.132,0.264,0.197,0.46,0.197c0.065,0,0.065,0,0.131,0s0.065,0,0.132,0c0.196,0,0.328-0.065,0.458-0.197l5.832-1.965c0.065,0,0.132-0.065,0.196-0.131l0.066-0.066c0.065-0.065,0.065-0.065,0.131-0.131l0.689-5.625l-3.553-12.16c-0.078-0.155-0.195-0.471-0.082-0.759l1.629-6.586c0.1-0.493,0.523-2.836,1.055-2.836h5.063c0.581,0,1.081,2.387,1.188,2.919c0.098,0.582-1.117,1.068-1.585,1.349l-4.571,2.667l-0.645,2.638l3.959,8.168l14.021-9.433c0.196-0.132,0.263-0.328,0.263-0.525v-7.863C73.84,48.507,73.774,48.311,73.644,48.18z"/>', 'wolf-2', '[{"datad":"M73.644,48.18L72.464,47l-4.455-24.901c-0.065-0.262-0.197-0.459-0.459-0.524c-0.263-0.065-0.46,0-0.655,0.197l-9.764,9.895l-0.263-0.263c-0.13-0.131-0.262-0.196-0.458-0.196h-6.619h-6.88c-0.197,0-0.328,0.065-0.458,0.196l-0.262,0.263l-9.764-9.895c-0.197-0.197-0.459-0.262-0.655-0.197c-0.263,0.065-0.393,0.262-0.459,0.524L26.856,47l-1.18,1.18c-0.131,0.131-0.197,0.262-0.197,0.458v7.863c0,0.197,0.131,0.394,0.262,0.525l14.088,9.477l3.979-8.212l-0.645-2.638l-4.568-2.664c-0.472-0.283-1.686-0.77-1.591-1.339c0.109-0.545,0.608-2.932,1.19-2.932h5.063c0.53,0,0.955,2.343,1.058,2.854l1.626,6.568c0.113,0.288-0.005,0.604-0.089,0.772l-3.59,12.238l0.646,5.533c0,0.065,0.065,0.065,0.131,0.131c0,0,0,0.066,0.066,0.066c0.065,0.065,0.131,0.065,0.197,0.131l5.831,1.965c0.132,0.132,0.264,0.197,0.46,0.197c0.065,0,0.065,0,0.131,0s0.065,0,0.132,0c0.196,0,0.328-0.065,0.458-0.197l5.832-1.965c0.065,0,0.132-0.065,0.196-0.131l0.066-0.066c0.065-0.065,0.065-0.065,0.131-0.131l0.689-5.625l-3.553-12.16c-0.078-0.155-0.195-0.471-0.082-0.759l1.629-6.586c0.1-0.493,0.523-2.836,1.055-2.836h5.063c0.581,0,1.081,2.387,1.188,2.919c0.098,0.582-1.117,1.068-1.585,1.349l-4.571,2.667l-0.645,2.638l3.959,8.168l14.021-9.433c0.196-0.132,0.263-0.328,0.263-0.525v-7.863C73.84,48.507,73.774,48.311,73.644,48.18z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (210, 'Animals: Shark', '<path fill="#FFFFFF" d="M63.516,74.2c-2.407,0-3.703-1.325-4.561-2.202C58.213,71.24,57.944,71,57.253,71c-0.688,0-0.956,0.24-1.697,0.998c-0.858,0.877-2.153,2.202-4.56,2.202c-2.407,0-3.702-1.325-4.561-2.202C45.693,71.24,45.427,71,44.736,71c-0.689,0-0.957,0.24-1.699,0.998c-0.857,0.877-2.152,2.202-4.559,2.202c-2.405,0-3.701-1.325-4.559-2.202C33.179,71.24,32.912,71,32.223,71c-1.105,0-2-0.895-2-2s0.895-2,2-2c2.406,0,3.701,1.324,4.558,2.201c0.741,0.759,1.009,0.999,1.698,0.999s0.957-0.24,1.699-0.999C41.035,68.324,42.33,67,44.736,67s3.701,1.324,4.559,2.201c0.742,0.759,1.01,0.999,1.701,0.999c0.689,0,0.957-0.24,1.699-0.999C53.553,68.324,54.848,67,57.253,67c2.407,0,3.703,1.324,4.562,2.201c0.742,0.759,1.01,0.999,1.701,0.999c0.69,0,0.958-0.24,1.701-0.999C66.074,68.324,67.369,67,69.777,67c1.104,0,2,0.895,2,2s-0.896,2-2,2c-0.691,0-0.959,0.24-1.702,0.999C67.218,72.875,65.922,74.2,63.516,74.2z"/>', 'shark-2', '[{"datad":"M63.516,74.2c-2.407,0-3.703-1.325-4.561-2.202C58.213,71.24,57.944,71,57.253,71c-0.688,0-0.956,0.24-1.697,0.998c-0.858,0.877-2.153,2.202-4.56,2.202c-2.407,0-3.702-1.325-4.561-2.202C45.693,71.24,45.427,71,44.736,71c-0.689,0-0.957,0.24-1.699,0.998c-0.857,0.877-2.152,2.202-4.559,2.202c-2.405,0-3.701-1.325-4.559-2.202C33.179,71.24,32.912,71,32.223,71c-1.105,0-2-0.895-2-2s0.895-2,2-2c2.406,0,3.701,1.324,4.558,2.201c0.741,0.759,1.009,0.999,1.698,0.999s0.957-0.24,1.699-0.999C41.035,68.324,42.33,67,44.736,67s3.701,1.324,4.559,2.201c0.742,0.759,1.01,0.999,1.701,0.999c0.689,0,0.957-0.24,1.699-0.999C53.553,68.324,54.848,67,57.253,67c2.407,0,3.703,1.324,4.562,2.201c0.742,0.759,1.01,0.999,1.701,0.999c0.69,0,0.958-0.24,1.701-0.999C66.074,68.324,67.369,67,69.777,67c1.104,0,2,0.895,2,2s-0.896,2-2,2c-0.691,0-0.959,0.24-1.702,0.999C67.218,72.875,65.922,74.2,63.516,74.2z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (209, 'Animals: Lion', '<path fill="#FFFFFF" d="M58.257,36.788c-1.692-0.647-4.362-1.019-7.324-1.019c-2.984,0-5.894,0.378-7.982,1.038c-1.988,0.63-3.714,4.509-4.477,7.846c-0.294,1.274,0.083,2.583,1.009,3.502l1.699,1.695c0.83-1.105,1.742-2.15,2.72-3.117c0.237-0.239,0.293-0.613,0.132-0.922c-0.618-1.154-1.206-1.566-2.242-1.566l-0.208-0.005c-0.663-0.075-1.18-0.591-1.257-1.255c-0.091-0.782,0.472-1.492,1.254-1.583l0.106-0.007c0,0,0.001,0,0.001,0c2.901,0,4.167,1.755,4.874,3.076c0.744,1.423,0.489,3.137-0.631,4.273c-2.577,2.602-5.111,6.131-4.502,8.232c0.325,1.189,1.808,1.877,3.156,2.264c1.389-1.031,2.986-1.713,4.683-2.006v-0.668l-1.883-1.52c-0.339-0.277-0.559-0.662-0.626-1.09c-0.073-0.461,0.038-0.924,0.314-1.305c0.276-0.379,0.683-0.627,1.146-0.701c1.779-0.234,3.531-0.23,5.261-0.002c0.481,0.068,0.909,0.328,1.186,0.715c0.561,0.789,0.376,1.883-0.408,2.441l-2.117,1.539v0.578c1.67,0.273,3.255,0.98,4.575,2.043c1.797-0.514,2.85-1.271,3.134-2.254c0.604-2.111-1.929-5.637-4.499-8.232c-1.122-1.139-1.378-2.853-0.64-4.267c0.711-1.331,1.983-3.089,4.871-3.089l0.115,0.007c0.66,0.076,1.177,0.591,1.255,1.253c0.044,0.381-0.063,0.755-0.301,1.055c-0.236,0.298-0.575,0.486-0.954,0.53l-0.115,0.007c-1.17,0-1.726,0.382-2.357,1.571c-0.157,0.302-0.101,0.676,0.145,0.924c0.974,0.96,1.886,2.004,2.717,3.109l1.72-1.721c0.925-0.921,1.303-2.229,1.012-3.499C62.056,41.315,60.351,37.585,58.257,36.788z"/><path fill="#FFFFFF" d="M46.376,61.486v0.561c0,0.043,0.004,0.088,0.009,0.133c0.063,0.533,0.569,0.938,1.128,0.873l2.375-0.299l1.541,0.008l2.372,0.299c0.027,0.002,0.068,0.006,0.108,0.006c0.563,0,1.02-0.455,1.021-1.016l0.009-0.574c-1.341-1.023-2.747-1.543-4.188-1.543C48.744,59.934,47.106,60.947,46.376,61.486z"/><path fill="#FFFFFF" d="M63.628,28.798c1.426-0.41,2.893-0.659,4.373-0.744l1.009-0.073l-0.755-0.671c-6.694-5.967-16.798-2.406-18.252-1.848c-1.449-0.563-11.552-4.119-18.252,1.854l-0.755,0.67l1.009,0.068c1.48,0.084,2.947,0.333,4.373,0.744c-2.874,1.087-11.485,5.573-10.758,20.151l0.04,0.873l0.676-0.563c0.428-0.383,1.64-1.245,2.632-1.245c-0.344,2.439-1.562,14.5,6.429,20.744l1.048,0.787l-0.361-1.256c0,0-0.637-2.287,0.377-3.512c1.325,1.906,6.988,9.451,13.542,9.84c6.525-0.389,12.189-7.928,13.524-9.834c1.008,1.225,0.383,3.49,0.378,3.512l-0.339,1.25l1.025-0.799c7.997-6.238,6.763-18.303,6.43-20.744c0.958,0,2.227,0.862,2.648,1.257l0.665,0.563l0.045-0.885C75.113,34.377,66.502,29.886,63.628,28.798z M63.797,50.178l-2.156,2.156c1.085,2.064,1.388,3.854,0.926,5.447c-0.555,1.912-2.169,3.295-4.802,4.113v0.156c-0.012,2.115-1.737,3.84-3.847,3.852c-0.033,0.008-0.27,0.01-0.438-0.004l-2.311-0.289h-1.102l-2.19,0.283c-0.165,0.025-0.342,0.035-0.52,0.035h-0.002c-1.034-0.002-2.004-0.408-2.733-1.141s-1.128-1.705-1.125-2.738v-0.154c-3.256-1.021-4.413-2.775-4.801-4.113c-0.454-1.563-0.143-3.391,0.927-5.445l-2.161-2.161c-1.623-1.62-2.284-3.921-1.766-6.156c0.452-1.994,2.254-8.613,6.398-9.924c2.412-0.767,5.557-1.189,8.856-1.189c3.32,0,6.277,0.429,8.323,1.208c3.303,1.256,5.424,5.942,6.307,9.908C66.094,46.255,65.429,48.559,63.797,50.178z"/>', 'lion-2', '[{"datad":"M58.257,36.788c-1.692-0.647-4.362-1.019-7.324-1.019c-2.984,0-5.894,0.378-7.982,1.038c-1.988,0.63-3.714,4.509-4.477,7.846c-0.294,1.274,0.083,2.583,1.009,3.502l1.699,1.695c0.83-1.105,1.742-2.15,2.72-3.117c0.237-0.239,0.293-0.613,0.132-0.922c-0.618-1.154-1.206-1.566-2.242-1.566l-0.208-0.005c-0.663-0.075-1.18-0.591-1.257-1.255c-0.091-0.782,0.472-1.492,1.254-1.583l0.106-0.007c0,0,0.001,0,0.001,0c2.901,0,4.167,1.755,4.874,3.076c0.744,1.423,0.489,3.137-0.631,4.273c-2.577,2.602-5.111,6.131-4.502,8.232c0.325,1.189,1.808,1.877,3.156,2.264c1.389-1.031,2.986-1.713,4.683-2.006v-0.668l-1.883-1.52c-0.339-0.277-0.559-0.662-0.626-1.09c-0.073-0.461,0.038-0.924,0.314-1.305c0.276-0.379,0.683-0.627,1.146-0.701c1.779-0.234,3.531-0.23,5.261-0.002c0.481,0.068,0.909,0.328,1.186,0.715c0.561,0.789,0.376,1.883-0.408,2.441l-2.117,1.539v0.578c1.67,0.273,3.255,0.98,4.575,2.043c1.797-0.514,2.85-1.271,3.134-2.254c0.604-2.111-1.929-5.637-4.499-8.232c-1.122-1.139-1.378-2.853-0.64-4.267c0.711-1.331,1.983-3.089,4.871-3.089l0.115,0.007c0.66,0.076,1.177,0.591,1.255,1.253c0.044,0.381-0.063,0.755-0.301,1.055c-0.236,0.298-0.575,0.486-0.954,0.53l-0.115,0.007c-1.17,0-1.726,0.382-2.357,1.571c-0.157,0.302-0.101,0.676,0.145,0.924c0.974,0.96,1.886,2.004,2.717,3.109l1.72-1.721c0.925-0.921,1.303-2.229,1.012-3.499C62.056,41.315,60.351,37.585,58.257,36.788z"},{"datad":"M46.376,61.486v0.561c0,0.043,0.004,0.088,0.009,0.133c0.063,0.533,0.569,0.938,1.128,0.873l2.375-0.299l1.541,0.008l2.372,0.299c0.027,0.002,0.068,0.006,0.108,0.006c0.563,0,1.02-0.455,1.021-1.016l0.009-0.574c-1.341-1.023-2.747-1.543-4.188-1.543C48.744,59.934,47.106,60.947,46.376,61.486z"},{"datad":"M63.628,28.798c1.426-0.41,2.893-0.659,4.373-0.744l1.009-0.073l-0.755-0.671c-6.694-5.967-16.798-2.406-18.252-1.848c-1.449-0.563-11.552-4.119-18.252,1.854l-0.755,0.67l1.009,0.068c1.48,0.084,2.947,0.333,4.373,0.744c-2.874,1.087-11.485,5.573-10.758,20.151l0.04,0.873l0.676-0.563c0.428-0.383,1.64-1.245,2.632-1.245c-0.344,2.439-1.562,14.5,6.429,20.744l1.048,0.787l-0.361-1.256c0,0-0.637-2.287,0.377-3.512c1.325,1.906,6.988,9.451,13.542,9.84c6.525-0.389,12.189-7.928,13.524-9.834c1.008,1.225,0.383,3.49,0.378,3.512l-0.339,1.25l1.025-0.799c7.997-6.238,6.763-18.303,6.43-20.744c0.958,0,2.227,0.862,2.648,1.257l0.665,0.563l0.045-0.885C75.113,34.377,66.502,29.886,63.628,28.798z M63.797,50.178l-2.156,2.156c1.085,2.064,1.388,3.854,0.926,5.447c-0.555,1.912-2.169,3.295-4.802,4.113v0.156c-0.012,2.115-1.737,3.84-3.847,3.852c-0.033,0.008-0.27,0.01-0.438-0.004l-2.311-0.289h-1.102l-2.19,0.283c-0.165,0.025-0.342,0.035-0.52,0.035h-0.002c-1.034-0.002-2.004-0.408-2.733-1.141s-1.128-1.705-1.125-2.738v-0.154c-3.256-1.021-4.413-2.775-4.801-4.113c-0.454-1.563-0.143-3.391,0.927-5.445l-2.161-2.161c-1.623-1.62-2.284-3.921-1.766-6.156c0.452-1.994,2.254-8.613,6.398-9.924c2.412-0.767,5.557-1.189,8.856-1.189c3.32,0,6.277,0.429,8.323,1.208c3.303,1.256,5.424,5.942,6.307,9.908C66.094,46.255,65.429,48.559,63.797,50.178z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (208, 'Animals: Cow', '<path fill="#FFFFFF" d="M80.23,38.165l-2.11-1.853c-0.581-1.281-1.39-2.286-2.536-3.159l-0.692-0.524c-0.399-0.656-0.601-1.504-0.612-2.587c-0.006-0.546-0.308-1.055-0.784-1.326c-0.467-0.267-1.066-0.271-1.545,0c-1.609,0.922-2.897,2.123-3.881,3.602c-0.377,0.37-3.521,1.161-7.586,1.562c-2.758,0.269-5.288,0.4-7.73,0.4c-3.289,0-6.114-0.232-9.119-0.479c-3.486-0.286-7.092-0.581-12.017-0.583c-2.238,0-4.422,0.922-5.994,2.532c-1.521,1.563-2.341,3.6-2.304,5.679c-0.066,0.619-0.127,1.452-0.194,2.363l-0.013,0.177c-0.136,1.796-0.363,4.801-0.698,5.638l-1.961,4.89c-0.155,0.387-0.15,0.809,0.014,1.19c0.163,0.382,0.465,0.677,0.843,0.827c0.771,0.32,1.714-0.079,2.029-0.861l1.958-4.89c0.111-0.275,0.21-0.618,0.301-1.042c0.136,0.793,0.071,1.816-0.395,3.308c-0.07,0.227-0.088,0.459-0.054,0.688l2.088,14.234c0.114,0.756,0.775,1.326,1.539,1.326h4.687c0.459,0,0.892-0.201,1.189-0.552c0.295-0.351,0.421-0.809,0.345-1.255l-1.439-8.614c0.956,0.535,2.046,0.823,3.165,0.823c3.072,0,5.68-2.183,6.252-5.152c3.885,0.398,7.005,0.287,12.027-0.428l1.564,2.52l0.001,11.105c0,0.857,0.698,1.556,1.557,1.556h4.683c0.858,0,1.557-0.698,1.557-1.556V56.753c2.995-3.423,3.953-5.906,4.727-7.909c0.56-1.451,1.003-2.602,2.113-3.859c0.05,0.012,0.121,0.035,0.203,0.063c1.168,0.38,2.202,0.484,3.385,0.352l3.049-0.327c1.099-0.116,2.032-0.736,2.56-1.698l0.588-1.054C81.771,40.898,81.457,39.187,80.23,38.165z M39.957,53.01l0.028,0.301c0,1.799-1.463,3.262-3.261,3.262c-1.218,0-2.363-0.681-2.919-1.734c-0.27-0.507-0.799-0.823-1.473-0.823c-0.002,0-0.006,0-0.008,0c-0.615,0.045-1.146,0.444-1.352,1.016l-0.689,1.869c-0.09,0.248-0.116,0.52-0.074,0.79l1.417,8.479h-1.51l-1.841-12.565c1.194-4.138-0.062-6.17-0.985-7.664c-0.457-0.742-0.787-1.278-0.804-2l-0.046-1.819c-0.014-0.51-0.028-1.089,0.046-1.548c0.163-0.99,0.634-1.907,1.361-2.652c0.992-1.014,2.366-1.595,3.77-1.595c4.741,0,8.285,0.29,11.763,0.573c3.055,0.25,5.941,0.486,9.4,0.486c2.569,0,5.189-0.136,8.009-0.414c5.6-0.551,8.89-1.502,9.78-2.824c0.317-0.47,0.658-0.892,1.034-1.275c0.211,0.636,0.502,1.21,0.876,1.737c0.096,0.132,0.201,0.242,0.326,0.338l0.899,0.68c0.792,0.602,1.313,1.282,1.687,2.204c0.09,0.223,0.232,0.426,0.414,0.585l2.404,2.109c0.094,0.079,0.114,0.186,0.058,0.29l-0.586,1.051c-0.044,0.081-0.083,0.106-0.175,0.116l-3.049,0.328c-0.731,0.08-1.365,0.019-2.097-0.217l-0.277-0.09c-1.109-0.355-2.362-0.018-3.138,0.844c-1.529,1.701-2.124,3.239-2.759,4.884c-0.754,1.95-1.608,4.161-4.531,7.389c-0.26,0.287-0.402,0.657-0.402,1.042v10.01h-1.577l-0.002-9.998c0-0.286-0.08-0.568-0.233-0.82l-2.339-3.756c-0.319-0.515-0.901-0.81-1.552-0.718c-3.495,0.531-6.104,0.767-8.454,0.767c-1.732,0-3.443-0.128-5.382-0.403c-0.475-0.068-0.937,0.1-1.306,0.459C40.07,52.035,39.898,52.544,39.957,53.01z"/>', 'cow-2', '[{"datad":"M80.23,38.165l-2.11-1.853c-0.581-1.281-1.39-2.286-2.536-3.159l-0.692-0.524c-0.399-0.656-0.601-1.504-0.612-2.587c-0.006-0.546-0.308-1.055-0.784-1.326c-0.467-0.267-1.066-0.271-1.545,0c-1.609,0.922-2.897,2.123-3.881,3.602c-0.377,0.37-3.521,1.161-7.586,1.562c-2.758,0.269-5.288,0.4-7.73,0.4c-3.289,0-6.114-0.232-9.119-0.479c-3.486-0.286-7.092-0.581-12.017-0.583c-2.238,0-4.422,0.922-5.994,2.532c-1.521,1.563-2.341,3.6-2.304,5.679c-0.066,0.619-0.127,1.452-0.194,2.363l-0.013,0.177c-0.136,1.796-0.363,4.801-0.698,5.638l-1.961,4.89c-0.155,0.387-0.15,0.809,0.014,1.19c0.163,0.382,0.465,0.677,0.843,0.827c0.771,0.32,1.714-0.079,2.029-0.861l1.958-4.89c0.111-0.275,0.21-0.618,0.301-1.042c0.136,0.793,0.071,1.816-0.395,3.308c-0.07,0.227-0.088,0.459-0.054,0.688l2.088,14.234c0.114,0.756,0.775,1.326,1.539,1.326h4.687c0.459,0,0.892-0.201,1.189-0.552c0.295-0.351,0.421-0.809,0.345-1.255l-1.439-8.614c0.956,0.535,2.046,0.823,3.165,0.823c3.072,0,5.68-2.183,6.252-5.152c3.885,0.398,7.005,0.287,12.027-0.428l1.564,2.52l0.001,11.105c0,0.857,0.698,1.556,1.557,1.556h4.683c0.858,0,1.557-0.698,1.557-1.556V56.753c2.995-3.423,3.953-5.906,4.727-7.909c0.56-1.451,1.003-2.602,2.113-3.859c0.05,0.012,0.121,0.035,0.203,0.063c1.168,0.38,2.202,0.484,3.385,0.352l3.049-0.327c1.099-0.116,2.032-0.736,2.56-1.698l0.588-1.054C81.771,40.898,81.457,39.187,80.23,38.165z M39.957,53.01l0.028,0.301c0,1.799-1.463,3.262-3.261,3.262c-1.218,0-2.363-0.681-2.919-1.734c-0.27-0.507-0.799-0.823-1.473-0.823c-0.002,0-0.006,0-0.008,0c-0.615,0.045-1.146,0.444-1.352,1.016l-0.689,1.869c-0.09,0.248-0.116,0.52-0.074,0.79l1.417,8.479h-1.51l-1.841-12.565c1.194-4.138-0.062-6.17-0.985-7.664c-0.457-0.742-0.787-1.278-0.804-2l-0.046-1.819c-0.014-0.51-0.028-1.089,0.046-1.548c0.163-0.99,0.634-1.907,1.361-2.652c0.992-1.014,2.366-1.595,3.77-1.595c4.741,0,8.285,0.29,11.763,0.573c3.055,0.25,5.941,0.486,9.4,0.486c2.569,0,5.189-0.136,8.009-0.414c5.6-0.551,8.89-1.502,9.78-2.824c0.317-0.47,0.658-0.892,1.034-1.275c0.211,0.636,0.502,1.21,0.876,1.737c0.096,0.132,0.201,0.242,0.326,0.338l0.899,0.68c0.792,0.602,1.313,1.282,1.687,2.204c0.09,0.223,0.232,0.426,0.414,0.585l2.404,2.109c0.094,0.079,0.114,0.186,0.058,0.29l-0.586,1.051c-0.044,0.081-0.083,0.106-0.175,0.116l-3.049,0.328c-0.731,0.08-1.365,0.019-2.097-0.217l-0.277-0.09c-1.109-0.355-2.362-0.018-3.138,0.844c-1.529,1.701-2.124,3.239-2.759,4.884c-0.754,1.95-1.608,4.161-4.531,7.389c-0.26,0.287-0.402,0.657-0.402,1.042v10.01h-1.577l-0.002-9.998c0-0.286-0.08-0.568-0.233-0.82l-2.339-3.756c-0.319-0.515-0.901-0.81-1.552-0.718c-3.495,0.531-6.104,0.767-8.454,0.767c-1.732,0-3.443-0.128-5.382-0.403c-0.475-0.068-0.937,0.1-1.306,0.459C40.07,52.035,39.898,52.544,39.957,53.01z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (207, 'Animals: Horse', '<path fill="#FFFFFF" d="M75.487,32.653l-5.935-5.934v-2.694c0-0.695-0.267-1.262-0.731-1.628c-0.384-0.385-0.922-0.605-1.477-0.605c-0.116,0-0.232,0.009-0.321,0.025c-9.663,1.244-17.553,9.651-18.615,19.702h-8.252c-7.928,0-14.377,6.45-14.377,14.377l0.001,11.458c-0.012,0.195-0.012,0.195,0.137,2.409c0.01,0.141,0.059,0.276,0.143,0.391c2.318,3.183,5.857,5.103,11.137,6.043c0.044,0.008,0.088,0.012,0.131,0.012c0.008,0,0.014,0,0.02,0c0.414,0,0.75-0.336,0.75-0.75c0-0.179-0.063-0.342-0.167-0.471c-0.478-0.988-0.754-2.051-0.822-3.161c-0.002-0.034-0.006-0.067-0.013-0.101l-0.014-0.42c0-4.67,3.799-8.469,8.469-8.469c4.669,0,8.468,3.799,8.468,8.469c0,1.238-0.27,2.442-0.803,3.581c-0.122,0.261-0.083,0.569,0.099,0.792c0.183,0.224,0.478,0.325,0.758,0.254c2.374-0.575,5.805-1.727,8.704-3.944c0.087-0.033,0.169-0.084,0.24-0.152c1.795-1.688,1.938-1.944,1.923-3.422l-0.003-17.94c0-3.107,0.696-6.02,2.071-8.672c1.123,0.965,2.55,1.491,4.065,1.491c1.677,0,3.244-0.644,4.413-1.813c1.181-1.181,1.831-2.748,1.831-4.413S76.668,33.834,75.487,32.653z M60.506,50.475V69.13c-0.697,0.333-1.441,0.651-2.226,0.953c-0.654-6.564-6.081-11.565-12.776-11.565c-6.604,0-12.017,4.858-12.759,11.242c-0.742-0.314-1.529-0.68-2.463-1.141V55.896c0-5.484,4.462-9.947,9.947-9.947h10.331c0.578,0,1.131-0.234,1.556-0.659c0.425-0.425,0.659-0.978,0.659-1.557c0-7.868,5.139-14.854,12.348-16.978v0.859c0,0.578,0.234,1.131,0.659,1.556l6.594,6.595c0.359,0.358,0.513,0.748,0.513,1.301c0,0.445-0.201,0.935-0.513,1.301c-0.711,0.71-1.895,0.708-2.604,0l-1.685-1.683c-0.419-0.419-1.039-0.66-1.701-0.66c-0.041,0-0.082,0.003-0.123,0.01c-0.565,0.094-1.073,0.361-1.507,0.795c-0.035,0.035-0.066,0.073-0.094,0.114C61.943,41.022,60.506,45.701,60.506,50.475z"/>', 'horse-2', '[{"datad":"M75.487,32.653l-5.935-5.934v-2.694c0-0.695-0.267-1.262-0.731-1.628c-0.384-0.385-0.922-0.605-1.477-0.605c-0.116,0-0.232,0.009-0.321,0.025c-9.663,1.244-17.553,9.651-18.615,19.702h-8.252c-7.928,0-14.377,6.45-14.377,14.377l0.001,11.458c-0.012,0.195-0.012,0.195,0.137,2.409c0.01,0.141,0.059,0.276,0.143,0.391c2.318,3.183,5.857,5.103,11.137,6.043c0.044,0.008,0.088,0.012,0.131,0.012c0.008,0,0.014,0,0.02,0c0.414,0,0.75-0.336,0.75-0.75c0-0.179-0.063-0.342-0.167-0.471c-0.478-0.988-0.754-2.051-0.822-3.161c-0.002-0.034-0.006-0.067-0.013-0.101l-0.014-0.42c0-4.67,3.799-8.469,8.469-8.469c4.669,0,8.468,3.799,8.468,8.469c0,1.238-0.27,2.442-0.803,3.581c-0.122,0.261-0.083,0.569,0.099,0.792c0.183,0.224,0.478,0.325,0.758,0.254c2.374-0.575,5.805-1.727,8.704-3.944c0.087-0.033,0.169-0.084,0.24-0.152c1.795-1.688,1.938-1.944,1.923-3.422l-0.003-17.94c0-3.107,0.696-6.02,2.071-8.672c1.123,0.965,2.55,1.491,4.065,1.491c1.677,0,3.244-0.644,4.413-1.813c1.181-1.181,1.831-2.748,1.831-4.413S76.668,33.834,75.487,32.653z M60.506,50.475V69.13c-0.697,0.333-1.441,0.651-2.226,0.953c-0.654-6.564-6.081-11.565-12.776-11.565c-6.604,0-12.017,4.858-12.759,11.242c-0.742-0.314-1.529-0.68-2.463-1.141V55.896c0-5.484,4.462-9.947,9.947-9.947h10.331c0.578,0,1.131-0.234,1.556-0.659c0.425-0.425,0.659-0.978,0.659-1.557c0-7.868,5.139-14.854,12.348-16.978v0.859c0,0.578,0.234,1.131,0.659,1.556l6.594,6.595c0.359,0.358,0.513,0.748,0.513,1.301c0,0.445-0.201,0.935-0.513,1.301c-0.711,0.71-1.895,0.708-2.604,0l-1.685-1.683c-0.419-0.419-1.039-0.66-1.701-0.66c-0.041,0-0.082,0.003-0.123,0.01c-0.565,0.094-1.073,0.361-1.507,0.795c-0.035,0.035-0.066,0.073-0.094,0.114C61.943,41.022,60.506,45.701,60.506,50.475z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (206, 'Animals: Panda(rare animals)', '<path fill="#FFFFFF" d="M45.336,52.161c1.541,0,2.792-1.466,2.792-2.422c0-1.009-1.148-1.713-2.792-1.713s-2.792,0.704-2.792,1.713C42.545,50.695,43.795,52.161,45.336,52.161z"/><path fill="#FFFFFF" d="M38.068,47.13c0.25,0,0.483-0.033,0.691-0.099c1.102-0.346,1.491-1.432,1.803-2.303c0.157-0.438,0.319-0.89,0.529-1.139c0.164-0.194,0.333-0.37,0.495-0.537c0.627-0.652,1.338-1.391,1.163-2.617c-0.148-1.038-1.375-1.883-2.735-1.883c-0.876,0-2.579,0.353-4.13,2.713c-1.334,2.031-1.095,3.42-0.66,4.228C35.834,46.624,37.091,47.13,38.068,47.13z"/><path fill="#FFFFFF" d="M49.208,43.589c0.21,0.248,0.372,0.7,0.528,1.138c0.313,0.873,0.701,1.958,1.803,2.304c0.208,0.065,0.441,0.099,0.692,0.099c0.976,0,2.233-0.506,2.843-1.636c0.436-0.808,0.676-2.197-0.66-4.228c-1.551-2.36-3.253-2.713-4.13-2.713c-1.36,0-2.587,0.845-2.735,1.883c-0.175,1.226,0.536,1.965,1.163,2.616C48.874,43.22,49.043,43.395,49.208,43.589z"/><path fill="#FFFFFF" d="M62.626,34.24c1.012-2.34-0.056-5.25-2.557-6.705c-2.674-1.554-5.989-0.862-7.39,1.545c-0.329,0.564-0.546,1.242-0.662,1.964c-2.01-1.07-4.264-1.727-6.681-1.727c-2.376,0-4.595,0.635-6.578,1.672c-0.12-0.702-0.333-1.36-0.653-1.91c-1.4-2.408-4.716-3.1-7.39-1.545c-1.278,0.743-2.227,1.887-2.671,3.22c-0.458,1.373-0.331,2.782,0.358,3.967c0.695,1.195,1.999,2.19,3.451,2.704c-1.497,2.347-2.349,4.836-2.349,6.926c0,2.863,1.493,5.475,3.933,7.477c-0.479,1.548-1.544,5.865-0.235,10.347c1.586,5.427,4.549,8.255,4.675,8.373c0.164,0.153,0.381,0.239,0.606,0.239h3.84c0.337,0,0.646-0.191,0.795-0.494c0.085-0.174,1.946-3.985,2.146-7.54h1.708c0.108,1.552,0.497,4.863,1.889,7.555c0.152,0.294,0.456,0.479,0.787,0.479h5.257c0.218,0,0.428-0.08,0.59-0.225c0.198-0.177,3.164-2.867,5.52-7.405h0.607c0.122,1.588,0.604,4.955,2.522,7.304c0.167,0.207,0.42,0.326,0.686,0.326h4.845c0.25,0,0.487-0.105,0.655-0.291c0.249-0.273,6.085-6.809,5.842-16.095C75.949,45.812,69.637,36.595,62.626,34.24z M59.883,37.011c0.016,0.008,0.026,0.023,0.043,0.03c0,0,0.001,0,0.002,0l0,0c0.001,0.001,0.016,0.007,0.028,0.013c0.565,0.258,10.302,4.816,9.722,12.458c-0.491,6.482-4.471,8.95-7.017,9.733c0.64-1.928,1.104-4.053,1.225-6.37c0.474-9.227-2.449-11.833-3.548-12.481c-0.38-0.979-0.884-1.97-1.499-2.938C59.198,37.335,59.548,37.188,59.883,37.011zM31.278,44.353c0-5.237,6.298-13.262,14.059-13.262c7.762,0,14.06,8.024,14.06,13.262c0,2.244-1.143,4.306-3.039,5.942c-1.181,0.968-2.687,1.826-4.525,2.564c-1.945,0.694-4.152,1.094-6.495,1.094c-1.371,0-2.693-0.142-3.947-0.393c-0.157-0.031-0.313-0.063-0.469-0.1C35.328,52.192,31.278,48.592,31.278,44.353z"/>', 'panda-2', '[{"datad":"M45.336,52.161c1.541,0,2.792-1.466,2.792-2.422c0-1.009-1.148-1.713-2.792-1.713s-2.792,0.704-2.792,1.713C42.545,50.695,43.795,52.161,45.336,52.161z"},{"datad":"M38.068,47.13c0.25,0,0.483-0.033,0.691-0.099c1.102-0.346,1.491-1.432,1.803-2.303c0.157-0.438,0.319-0.89,0.529-1.139c0.164-0.194,0.333-0.37,0.495-0.537c0.627-0.652,1.338-1.391,1.163-2.617c-0.148-1.038-1.375-1.883-2.735-1.883c-0.876,0-2.579,0.353-4.13,2.713c-1.334,2.031-1.095,3.42-0.66,4.228C35.834,46.624,37.091,47.13,38.068,47.13z"},{"datad":"M49.208,43.589c0.21,0.248,0.372,0.7,0.528,1.138c0.313,0.873,0.701,1.958,1.803,2.304c0.208,0.065,0.441,0.099,0.692,0.099c0.976,0,2.233-0.506,2.843-1.636c0.436-0.808,0.676-2.197-0.66-4.228c-1.551-2.36-3.253-2.713-4.13-2.713c-1.36,0-2.587,0.845-2.735,1.883c-0.175,1.226,0.536,1.965,1.163,2.616C48.874,43.22,49.043,43.395,49.208,43.589z"},{"datad":"M62.626,34.24c1.012-2.34-0.056-5.25-2.557-6.705c-2.674-1.554-5.989-0.862-7.39,1.545c-0.329,0.564-0.546,1.242-0.662,1.964c-2.01-1.07-4.264-1.727-6.681-1.727c-2.376,0-4.595,0.635-6.578,1.672c-0.12-0.702-0.333-1.36-0.653-1.91c-1.4-2.408-4.716-3.1-7.39-1.545c-1.278,0.743-2.227,1.887-2.671,3.22c-0.458,1.373-0.331,2.782,0.358,3.967c0.695,1.195,1.999,2.19,3.451,2.704c-1.497,2.347-2.349,4.836-2.349,6.926c0,2.863,1.493,5.475,3.933,7.477c-0.479,1.548-1.544,5.865-0.235,10.347c1.586,5.427,4.549,8.255,4.675,8.373c0.164,0.153,0.381,0.239,0.606,0.239h3.84c0.337,0,0.646-0.191,0.795-0.494c0.085-0.174,1.946-3.985,2.146-7.54h1.708c0.108,1.552,0.497,4.863,1.889,7.555c0.152,0.294,0.456,0.479,0.787,0.479h5.257c0.218,0,0.428-0.08,0.59-0.225c0.198-0.177,3.164-2.867,5.52-7.405h0.607c0.122,1.588,0.604,4.955,2.522,7.304c0.167,0.207,0.42,0.326,0.686,0.326h4.845c0.25,0,0.487-0.105,0.655-0.291c0.249-0.273,6.085-6.809,5.842-16.095C75.949,45.812,69.637,36.595,62.626,34.24z M59.883,37.011c0.016,0.008,0.026,0.023,0.043,0.03c0,0,0.001,0,0.002,0l0,0c0.001,0.001,0.016,0.007,0.028,0.013c0.565,0.258,10.302,4.816,9.722,12.458c-0.491,6.482-4.471,8.95-7.017,9.733c0.64-1.928,1.104-4.053,1.225-6.37c0.474-9.227-2.449-11.833-3.548-12.481c-0.38-0.979-0.884-1.97-1.499-2.938C59.198,37.335,59.548,37.188,59.883,37.011zM31.278,44.353c0-5.237,6.298-13.262,14.059-13.262c7.762,0,14.06,8.024,14.06,13.262c0,2.244-1.143,4.306-3.039,5.942c-1.181,0.968-2.687,1.826-4.525,2.564c-1.945,0.694-4.152,1.094-6.495,1.094c-1.371,0-2.693-0.142-3.947-0.393c-0.157-0.031-0.313-0.063-0.469-0.1C35.328,52.192,31.278,48.592,31.278,44.353z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (205, 'ATGM', '<path fill="#FFFFFF" d="M73.385,48.382l-7.983-0.904l-0.793,0.962H64.22c-0.928-2.03-2.977-3.367-5.233-3.367c-1.941,0-3.722,0.992-4.774,2.563l-0.756-1.516l-36.669-4.16l0.743-6.532l39.952,4.532l0.053-0.488l-1.346-0.153l0.289-2.552l1.346,0.154l0.09-0.795l-2.346-0.265l0.373-3.31l3.154,0.361l0.098-0.858l3.922,0.441l-0.098,0.86l1.077,0.125l-0.077,0.661l1.848,0.212l-0.152,1.358l-1.422-0.159l-0.26,2.29l1.421,0.16l-0.153,1.363l-1.425-0.164l-0.164,1.462l10.42,1.186L73.385,48.382z"/><path fill="#FFFFFF" d="M38.573,66.764l14.782-10.826h-0.248v-2.324h1.602c-0.211-0.322-0.385-0.67-0.52-1.043H51.44v-3.483h2.748c0.713-1.96,2.591-3.366,4.798-3.366c2.21,0,4.087,1.406,4.798,3.366h2.75v3.483h-2.75c-0.135,0.373-0.307,0.719-0.519,1.043h1.602v2.324l16.179,12.219h2.167v1.281h-3.405l-18-11.939l-6.805,16.117h-2.44V72.34h1.2l3.75-15.887L39.812,68.041h-3.406v-1.277H38.573z"/>', 'atgm-2', '[{"datad":"M73.385,48.382l-7.983-0.904l-0.793,0.962H64.22c-0.928-2.03-2.977-3.367-5.233-3.367c-1.941,0-3.722,0.992-4.774,2.563l-0.756-1.516l-36.669-4.16l0.743-6.532l39.952,4.532l0.053-0.488l-1.346-0.153l0.289-2.552l1.346,0.154l0.09-0.795l-2.346-0.265l0.373-3.31l3.154,0.361l0.098-0.858l3.922,0.441l-0.098,0.86l1.077,0.125l-0.077,0.661l1.848,0.212l-0.152,1.358l-1.422-0.159l-0.26,2.29l1.421,0.16l-0.153,1.363l-1.425-0.164l-0.164,1.462l10.42,1.186L73.385,48.382z"},{"datad":"M38.573,66.764l14.782-10.826h-0.248v-2.324h1.602c-0.211-0.322-0.385-0.67-0.52-1.043H51.44v-3.483h2.748c0.713-1.96,2.591-3.366,4.798-3.366c2.21,0,4.087,1.406,4.798,3.366h2.75v3.483h-2.75c-0.135,0.373-0.307,0.719-0.519,1.043h1.602v2.324l16.179,12.219h2.167v1.281h-3.405l-18-11.939l-6.805,16.117h-2.44V72.34h1.2l3.75-15.887L39.812,68.041h-3.406v-1.277H38.573z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (204, 'Animals: Snake', '<path fill="#FFFFFF" d="M74.375,38.75H72.5c-6.213,0-11.25,5.037-11.25,11.25v11.25c0,2.071-1.679,3.75-3.75,3.75l0,0c-2.071,0-3.75-1.679-3.75-3.75v-22.5c0-6.213-5.037-11.25-11.25-11.25l0,0c-6.213,0-11.25,5.037-11.25,11.25v7.5c0,2.071-1.679,3.75-3.75,3.75h-5.625C20.839,50,20,50.84,20,51.875l0,0c0,1.035,0.839,1.874,1.875,1.874H27.5c6.213,0,11.25-5.036,11.25-11.25v-3.75c0-2.071,1.679-3.75,3.75-3.75l0,0c2.071,0,3.75,1.679,3.75,3.75v22.5c0,6.214,5.037,11.25,11.25,11.25l0,0c6.213,0,11.25-5.036,11.25-11.25v-7.5c0-2.071,1.679-3.75,3.75-3.75h1.875C77.481,50,80,47.481,80,44.375l0,0C80,41.269,77.481,38.75,74.375,38.75z"/>', 'snake-2', '[{"datad":"M74.375,38.75H72.5c-6.213,0-11.25,5.037-11.25,11.25v11.25c0,2.071-1.679,3.75-3.75,3.75l0,0c-2.071,0-3.75-1.679-3.75-3.75v-22.5c0-6.213-5.037-11.25-11.25-11.25l0,0c-6.213,0-11.25,5.037-11.25,11.25v7.5c0,2.071-1.679,3.75-3.75,3.75h-5.625C20.839,50,20,50.84,20,51.875l0,0c0,1.035,0.839,1.874,1.875,1.874H27.5c6.213,0,11.25-5.036,11.25-11.25v-3.75c0-2.071,1.679-3.75,3.75-3.75l0,0c2.071,0,3.75,1.679,3.75,3.75v22.5c0,6.214,5.037,11.25,11.25,11.25l0,0c6.213,0,11.25-5.036,11.25-11.25v-7.5c0-2.071,1.679-3.75,3.75-3.75h1.875C77.481,50,80,47.481,80,44.375l0,0C80,41.269,77.481,38.75,74.375,38.75z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (203, 'Stocks Down', '<path fill="#FFFFFF" d="M21.286,27.291l8.11,15.264c0.309,0.582,0.868,0.99,1.517,1.103c0.648,0.113,1.314-0.074,1.802-0.512l4.256-3.818l9.674,8.675c0.505,0.452,1.189,0.645,1.863,0.502l9.6-1.98l11.045,22.295l-3.084-1.063c-1.12-0.383-2.338,0.209-2.723,1.325c0.386,1.119,0.209,2.337,1.325,2.724l8.019,2.766c0.002,0.001,0.004,0.001,0.006,0.001c0.074,0.026,0.148,0.038,0.223,0.056c0.063,0.014,0.121,0.032,0.182,0.041c0.039,0.006,0.08,0.002,0.119,0.006c0.098,0.008,0.192,0.015,0.289,0.01c0.041-0.002,0.08-0.012,0.119-0.018c0.096-0.01,0.191-0.021,0.283-0.045c0.043-0.011,0.085-0.029,0.128-0.043c0.091-0.029,0.179-0.058,0.263-0.099c0.014-0.007,0.025-0.009,0.037-0.015c0.037-0.018,0.066-0.041,0.098-0.062c0.074-0.042,0.151-0.085,0.219-0.136c0.051-0.037,0.097-0.077,0.142-0.118c0.054-0.046,0.106-0.097,0.157-0.149c0.048-0.052,0.091-0.104,0.132-0.158c0.039-0.053,0.076-0.104,0.11-0.16c0.041-0.063,0.074-0.131,0.107-0.198c0.018-0.032,0.039-0.06,0.053-0.095l3.086-7.185c0.469-1.086-0.033-2.345-1.121-2.812c-0.275-0.118-0.563-0.174-0.844-0.174c-0.832,0-1.619,0.484-1.969,1.295l-1.258,2.93L61.211,43.141c-0.432-0.873-1.4-1.344-2.354-1.146l-10.159,2.099l-10.299-9.234c-0.811-0.729-2.045-0.729-2.856,0l-3.648,3.271l-6.488-12.41"/>', 'stocks_down-2', '[{"datad":"M21.286,27.291l8.11,15.264c0.309,0.582,0.868,0.99,1.517,1.103c0.648,0.113,1.314-0.074,1.802-0.512l4.256-3.818l9.674,8.675c0.505,0.452,1.189,0.645,1.863,0.502l9.6-1.98l11.045,22.295l-3.084-1.063c-1.12-0.383-2.338,0.209-2.723,1.325c0.386,1.119,0.209,2.337,1.325,2.724l8.019,2.766c0.002,0.001,0.004,0.001,0.006,0.001c0.074,0.026,0.148,0.038,0.223,0.056c0.063,0.014,0.121,0.032,0.182,0.041c0.039,0.006,0.08,0.002,0.119,0.006c0.098,0.008,0.192,0.015,0.289,0.01c0.041-0.002,0.08-0.012,0.119-0.018c0.096-0.01,0.191-0.021,0.283-0.045c0.043-0.011,0.085-0.029,0.128-0.043c0.091-0.029,0.179-0.058,0.263-0.099c0.014-0.007,0.025-0.009,0.037-0.015c0.037-0.018,0.066-0.041,0.098-0.062c0.074-0.042,0.151-0.085,0.219-0.136c0.051-0.037,0.097-0.077,0.142-0.118c0.054-0.046,0.106-0.097,0.157-0.149c0.048-0.052,0.091-0.104,0.132-0.158c0.039-0.053,0.076-0.104,0.11-0.16c0.041-0.063,0.074-0.131,0.107-0.198c0.018-0.032,0.039-0.06,0.053-0.095l3.086-7.185c0.469-1.086-0.033-2.345-1.121-2.812c-0.275-0.118-0.563-0.174-0.844-0.174c-0.832,0-1.619,0.484-1.969,1.295l-1.258,2.93L61.211,43.141c-0.432-0.873-1.4-1.344-2.354-1.146l-10.159,2.099l-10.299-9.234c-0.811-0.729-2.045-0.729-2.856,0l-3.648,3.271l-6.488-12.41"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (202, 'Animals: Dog', '<path fill="#FFFFFF" d="M65.408,52.361l-1.029,4.571v12.361c0,1.996-1.868,3.541-3.929,3.026c-1.416-0.321-2.381-1.674-2.381-3.155v-5.149c0-0.451-0.322-0.838-0.773-0.902l-11.009-1.353c-0.193,0-0.516-0.064-0.709-0.129l-8.37-2.317c-0.451-0.128-0.901,0.193-0.901,0.708v9.4c0,1.996-1.867,3.541-3.928,3.026c-1.416-0.321-2.382-1.674-2.382-3.155v-14.68V52.49v-8.242c0-0.322-0.129-0.644-0.387-0.837l-3.863-3.799c-0.837-0.837-0.901-2.189-0.129-2.961c0.837-0.837,2.189-0.837,2.962,0l3.798,3.798c0.193,0.193,0.515,0.322,0.837,0.322h20.924c0.451,0,0.902,0.193,1.288,0.515l9.337,9.336C65.216,51.01,65.602,51.717,65.408,52.361z M69.529,45.085l5.279,3.219c0.644,0.386,1.481,0.258,1.996-0.258l3.541-3.863c0.516-0.58,0.516-1.416,0.064-1.996l-9.142-10.816v-4.958c0-0.773-0.902-1.095-1.417-0.58L57.103,38.518c-0.386,0.386-0.386,0.966,0,1.288l8.757,8.756c0.322,0.321,0.901,0.192,1.029-0.193l1.16-2.769C68.241,45.021,69.014,44.764,69.529,45.085z"/>', 'dog-2', '[{"datad":"M65.408,52.361l-1.029,4.571v12.361c0,1.996-1.868,3.541-3.929,3.026c-1.416-0.321-2.381-1.674-2.381-3.155v-5.149c0-0.451-0.322-0.838-0.773-0.902l-11.009-1.353c-0.193,0-0.516-0.064-0.709-0.129l-8.37-2.317c-0.451-0.128-0.901,0.193-0.901,0.708v9.4c0,1.996-1.867,3.541-3.928,3.026c-1.416-0.321-2.382-1.674-2.382-3.155v-14.68V52.49v-8.242c0-0.322-0.129-0.644-0.387-0.837l-3.863-3.799c-0.837-0.837-0.901-2.189-0.129-2.961c0.837-0.837,2.189-0.837,2.962,0l3.798,3.798c0.193,0.193,0.515,0.322,0.837,0.322h20.924c0.451,0,0.902,0.193,1.288,0.515l9.337,9.336C65.216,51.01,65.602,51.717,65.408,52.361z M69.529,45.085l5.279,3.219c0.644,0.386,1.481,0.258,1.996-0.258l3.541-3.863c0.516-0.58,0.516-1.416,0.064-1.996l-9.142-10.816v-4.958c0-0.773-0.902-1.095-1.417-0.58L57.103,38.518c-0.386,0.386-0.386,0.966,0,1.288l8.757,8.756c0.322,0.321,0.901,0.192,1.029-0.193l1.16-2.769C68.241,45.021,69.014,44.764,69.529,45.085z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (201, 'Animals: Cat', '<path fill="#FFFFFF" d="M73.827,29.59c-0.582-0.263-1.268-0.156-1.745,0.273l-8.645,7.784c-0.475-0.052-0.966-0.048-1.439,0.004l-8.597-7.784c-0.475-0.434-1.159-0.539-1.745-0.278c-0.585,0.261-0.96,0.8440.955,1.485l0.07,10.154H31.468c-0.921,0-2.009,0.231-3.017,0.744v-7.282c0-0.51,0.146-3.045,3.017-3.045H43.05c0.893,0,1.616-0.724,1.616-1.616c0-0.893-0.724-1.616-1.616-1.616H31.468c-4.583,0-6.249,3.754-6.249,6.277v12.813c0,24.066,2.627,26.081,5.09,26.081c4.126,0,5.54-6.485,6.015-11.648h12.529c0.781,9.568,2.421,11.648,4.622,11.648c2.072,0,3.453-1.65,4.383-3.938c0.87,3.084,2.045,3.938,3.445,3.938c5.555,0,6.201-11.742,6.247-16.193c3.842-1.708,6.659-5.389,7.124-9.974l0.106-16.342C74.786,30.436,74.41,29.854,73.827,29.59z M71.449,47.244c-0.462,4.541-4.208,7.965-8.708,7.965c-4.498,0-8.242-3.42-8.701-7.801l-0.082-12.677l6.425,5.817c0.349,0.317,0.824,0.467,1.284,0.406l0.318-0.042c0.473-0.063,0.961-0.073,1.437-0.006l0.354,0.046c0.457,0.048,0.925-0.095,1.271-0.404l6.479-5.833L71.449,47.244z"/><path fill="#FFFFFF" d="M58.61600000000001,47.19a1.66,1.655 0 1,0 3.32,0a1.66,1.655 0 1,0 -3.32,0"/><path fill="#FFFFFF" d="M64.997,47.19a1.66,1.655 0 1,0 3.32,0a1.66,1.655 0 1,0 -3.32,0"/>', 'cat-2', '[{"datad":"M73.827,29.59c-0.582-0.263-1.268-0.156-1.745,0.273l-8.645,7.784c-0.475-0.052-0.966-0.048-1.439,0.004l-8.597-7.784c-0.475-0.434-1.159-0.539-1.745-0.278c-0.585,0.261-0.96,0.8440.955,1.485l0.07,10.154H31.468c-0.921,0-2.009,0.231-3.017,0.744v-7.282c0-0.51,0.146-3.045,3.017-3.045H43.05c0.893,0,1.616-0.724,1.616-1.616c0-0.893-0.724-1.616-1.616-1.616H31.468c-4.583,0-6.249,3.754-6.249,6.277v12.813c0,24.066,2.627,26.081,5.09,26.081c4.126,0,5.54-6.485,6.015-11.648h12.529c0.781,9.568,2.421,11.648,4.622,11.648c2.072,0,3.453-1.65,4.383-3.938c0.87,3.084,2.045,3.938,3.445,3.938c5.555,0,6.201-11.742,6.247-16.193c3.842-1.708,6.659-5.389,7.124-9.974l0.106-16.342C74.786,30.436,74.41,29.854,73.827,29.59z M71.449,47.244c-0.462,4.541-4.208,7.965-8.708,7.965c-4.498,0-8.242-3.42-8.701-7.801l-0.082-12.677l6.425,5.817c0.349,0.317,0.824,0.467,1.284,0.406l0.318-0.042c0.473-0.063,0.961-0.073,1.437-0.006l0.354,0.046c0.457,0.048,0.925-0.095,1.271-0.404l6.479-5.833L71.449,47.244z"}, {"datad":"M58.61600000000001,47.19a1.66,1.655 0 1,0 3.32,0a1.66,1.655 0 1,0 -3.32,0"},{"datad":"M64.997,47.19a1.66,1.655 0 1,0 3.32,0a1.66,1.655 0 1,0 -3.32,0"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (200, 'Bus', '<path fill="#FFFFFF" d="M38.086,60.205a3.243,3.243 0 1,0 6.486,0a3.243,3.243 0 1,0 -6.486,0"/><path fill="#FFFFFF" d="M59.733,60.205a3.243,3.243 0 1,0 6.486,0a3.243,3.243 0 1,0 -6.486,0"/> <path fill="#FFFFFF" d="M27.047,49.971h2.286v17.066c0,2.12,1.345,3.914,3.216,4.619v2.467c0,1.844,1.501,3.35,3.351,3.35h3.145c1.846,0,3.35-1.504,3.35-3.35v-2.137h18.092v2.137c0,1.844,1.501,3.35,3.35,3.35h3.145c1.847,0,3.351-1.504,3.351-3.35v-2.467c1.874-0.705,3.216-2.499,3.216-4.619V49.971h2.288c0.971,0,1.753-0.785,1.753-1.755V35.981c0-0.97-0.782-1.754-1.753-1.754h-2.288v-5.165c0-3.608-2.936-6.542-6.541-6.542H35.877c-3.608,0-6.542,2.936-6.542,6.542v5.165h-2.288c-0.968,0-1.754,0.784-1.754,1.754v12.238C25.292,49.186,26.079,49.971,27.047,49.971z M38.887,73.967h-2.829v-1.98h2.829V73.967z M66.819,73.967H63.99v-1.98h2.829V73.967z M53.906,26.027h13.095c1.675,0,3.035,1.362,3.035,3.035v6.917V48.06h-16.13V26.027z M32.839,35.981v-6.919c0-1.673,1.36-3.035,3.035-3.035h14.523V48.06H32.839V35.981z M32.839,51.566h37.194v15.471c0,0.794-0.646,1.442-1.437,1.442H34.281c-0.794,0-1.439-0.648-1.439-1.442V51.566H32.839z"/>', 'bus-2', '[{"datad":"M38.086,60.205a3.243,3.243 0 1,0 6.486,0a3.243,3.243 0 1,0 -6.486,0"},{"datad":"M59.733,60.205a3.243,3.243 0 1,0 6.486,0a3.243,3.243 0 1,0 -6.486,0"},{"datad":"M27.047,49.971h2.286v17.066c0,2.12,1.345,3.914,3.216,4.619v2.467c0,1.844,1.501,3.35,3.351,3.35h3.145c1.846,0,3.35-1.504,3.35-3.35v-2.137h18.092v2.137c0,1.844,1.501,3.35,3.35,3.35h3.145c1.847,0,3.351-1.504,3.351-3.35v-2.467c1.874-0.705,3.216-2.499,3.216-4.619V49.971h2.288c0.971,0,1.753-0.785,1.753-1.755V35.981c0-0.97-0.782-1.754-1.753-1.754h-2.288v-5.165c0-3.608-2.936-6.542-6.541-6.542H35.877c-3.608,0-6.542,2.936-6.542,6.542v5.165h-2.288c-0.968,0-1.754,0.784-1.754,1.754v12.238C25.292,49.186,26.079,49.971,27.047,49.971z M38.887,73.967h-2.829v-1.98h2.829V73.967z M66.819,73.967H63.99v-1.98h2.829V73.967z M53.906,26.027h13.095c1.675,0,3.035,1.362,3.035,3.035v6.917V48.06h-16.13V26.027z M32.839,35.981v-6.919c0-1.673,1.36-3.035,3.035-3.035h14.523V48.06H32.839V35.981z M32.839,51.566h37.194v15.471c0,0.794-0.646,1.442-1.437,1.442H34.281c-0.794,0-1.439-0.648-1.439-1.442V51.566H32.839z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (199, 'Sun', '<path fill="#FFFFFF" d="M49.938,62.925c-7.104,0-12.863-5.76-12.863-12.862c0-7.104,5.759-12.864,12.863-12.864c7.104,0,12.863,5.759,12.863,12.864C62.801,57.165,57.041,62.925,49.938,62.925L49.938,62.925z M38.572,64.857c2.269-2.27-1.161-5.698-3.429-3.431l-6.564,6.565c-2.269,2.269,1.161,5.697,3.429,3.43L38.572,64.857L38.572,64.857zM64.732,38.697l6.564-6.564c2.268-2.269-1.161-5.697-3.43-3.429l-6.565,6.565C59.034,37.536,62.463,40.966,64.732,38.697L64.732,38.697z M31.44,52.486h-9.285c-3.208,0-3.208-4.849,0-4.849h9.285C34.648,47.638,34.648,52.486,31.44,52.486L31.44,52.486zM68.436,52.486h9.284c3.207,0,3.206-4.849,0-4.849h-9.284C65.227,47.638,65.227,52.486,68.436,52.486L68.436,52.486zM35.143,38.697c2.268,2.269,5.698-1.161,3.429-3.429l-6.564-6.565c-2.269-2.268-5.698,1.161-3.429,3.429L35.143,38.697L35.143,38.697z M61.302,64.857l6.565,6.564c2.269,2.268,5.696-1.161,3.43-3.43l-6.564-6.565C62.463,59.158,59.034,62.588,61.302,64.857L61.302,64.857z M47.513,31.565V22.28c0-3.208,4.85-3.208,4.85,0v9.285C52.362,34.772,47.513,34.772,47.513,31.565L47.513,31.565z M47.513,68.56v9.285c0,3.207,4.85,3.206,4.85,0V68.56C52.362,65.352,47.513,65.352,47.513,68.56L47.513,68.56z M49.938,58.812c4.831,0,8.749-3.918,8.749-8.749c0-4.833-3.918-8.749-8.749-8.749c-4.832,0-8.749,3.917-8.749,8.749C41.189,54.894,45.105,58.812,49.938,58.812z"/>', 'sun-2', '[{"datad":"M49.938,62.925c-7.104,0-12.863-5.76-12.863-12.862c0-7.104,5.759-12.864,12.863-12.864c7.104,0,12.863,5.759,12.863,12.864C62.801,57.165,57.041,62.925,49.938,62.925L49.938,62.925z M38.572,64.857c2.269-2.27-1.161-5.698-3.429-3.431l-6.564,6.565c-2.269,2.269,1.161,5.697,3.429,3.43L38.572,64.857L38.572,64.857zM64.732,38.697l6.564-6.564c2.268-2.269-1.161-5.697-3.43-3.429l-6.565,6.565C59.034,37.536,62.463,40.966,64.732,38.697L64.732,38.697z M31.44,52.486h-9.285c-3.208,0-3.208-4.849,0-4.849h9.285C34.648,47.638,34.648,52.486,31.44,52.486L31.44,52.486zM68.436,52.486h9.284c3.207,0,3.206-4.849,0-4.849h-9.284C65.227,47.638,65.227,52.486,68.436,52.486L68.436,52.486zM35.143,38.697c2.268,2.269,5.698-1.161,3.429-3.429l-6.564-6.565c-2.269-2.268-5.698,1.161-3.429,3.429L35.143,38.697L35.143,38.697z M61.302,64.857l6.565,6.564c2.269,2.268,5.696-1.161,3.43-3.43l-6.564-6.565C62.463,59.158,59.034,62.588,61.302,64.857L61.302,64.857z M47.513,31.565V22.28c0-3.208,4.85-3.208,4.85,0v9.285C52.362,34.772,47.513,34.772,47.513,31.565L47.513,31.565z M47.513,68.56v9.285c0,3.207,4.85,3.206,4.85,0V68.56C52.362,65.352,47.513,65.352,47.513,68.56L47.513,68.56z M49.938,58.812c4.831,0,8.749-3.918,8.749-8.749c0-4.833-3.918-8.749-8.749-8.749c-4.832,0-8.749,3.917-8.749,8.749C41.189,54.894,45.105,58.812,49.938,58.812z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (198, 'Arrested', '<path fill="#FFFFFF" d="M71.438,46.331l-0.201-0.15v-12.07c0-0.783-0.637-1.42-1.42-1.42l-16.74-0.003c-0.776,0.077-1.323,0.688-1.32,1.421v10.268l-0.865-0.924c-0.787-0.841-1.625-1.595-2.492-2.242l-0.201-0.15V28.992c0.002-0.567-0.114-0.951-0.347-1.184c-0.199-0.2-0.49-0.297-0.89-0.297c-1.834,0-16.822,0.061-16.822,0.061l-0.102-0.003c-0.778,0.077-1.325,0.687-1.32,1.42v12.072l-0.201,0.15c-4.346,3.243-6.839,8.119-6.839,13.379c0,9.252,7.528,16.779,16.78,16.779c2.915,0,5.849-0.833,8.483-2.409l0.405-0.243l0.265,0.392c3.135,4.621,8.327,7.38,13.885,7.38c9.253,0,16.78-7.526,16.78-16.777C78.277,54.449,75.784,49.573,71.438,46.331z M46.464,52.375c-1.149,2.282-1.737,4.73-1.746,7.277l0,0.187l-0.123,0.141c-1.572,1.799-3.751,2.79-6.136,2.79c-4.511,0-8.181-3.669-8.181-8.18c0-4.51,3.67-8.179,8.181-8.179c3.027,0,6.622,2.482,8.015,5.532l0.1,0.219L46.464,52.375z M38.458,43.571c-6.077,0-11.021,4.943-11.021,11.019c0,6.076,4.944,11.02,11.021,11.02c2.18,0,4.284-0.629,6.084-1.817l0.578-0.382l0.18,0.669c0.154,0.571,0.343,1.146,0.563,1.708l0.152,0.389l-0.355,0.219c-2.299,1.416-4.722,2.135-7.202,2.135c-7.687,0-13.94-6.253-13.94-13.939c0-4.75,2.388-9.131,6.389-11.719c0.404-0.261,0.648-0.703,0.651-1.184V30.412h13.8v11.279c0.003,0.477,0.247,0.919,0.651,1.179c1.64,1.061,3.067,2.473,4.127,4.085l0.232,0.353l-0.308,0.288c-0.469,0.438-0.882,0.864-1.265,1.303l-0.424,0.487l-0.365-0.533C45.772,45.595,42.114,43.571,38.458,43.571z M61.497,73.65c-7.687,0-13.939-6.253-13.939-13.939c0-4.75,2.388-9.132,6.389-11.72c0.404-0.261,0.647-0.703,0.651-1.185V35.531h13.8V46.81c0.004,0.478,0.247,0.919,0.65,1.179c4.001,2.589,6.39,6.971,6.39,11.722C75.438,67.397,69.184,73.65,61.497,73.65z"/><path fill="#FFFFFF" d="M61.497,48.691c-6.076,0-11.02,4.944-11.02,11.02s4.943,11.02,11.02,11.02c6.077,0,11.021-4.943,11.021-11.02S67.574,48.691,61.497,48.691z M61.497,67.891c-4.511,0-8.18-3.669-8.18-8.18s3.669-8.18,8.18-8.18s8.181,3.669,8.181,8.18S66.008,67.891,61.497,67.891z"/><rect x="37" y="34.25" fill="#FFFFFF" width="2.75" height="2.75"/><rect x="60.123" y="39" fill="#FFFFFF" width="2.75" height="2.75"/>', 'arrested-2', '[{"datad":"M71.438,46.331l-0.201-0.15v-12.07c0-0.783-0.637-1.42-1.42-1.42l-16.74-0.003c-0.776,0.077-1.323,0.688-1.32,1.421v10.268l-0.865-0.924c-0.787-0.841-1.625-1.595-2.492-2.242l-0.201-0.15V28.992c0.002-0.567-0.114-0.951-0.347-1.184c-0.199-0.2-0.49-0.297-0.89-0.297c-1.834,0-16.822,0.061-16.822,0.061l-0.102-0.003c-0.778,0.077-1.325,0.687-1.32,1.42v12.072l-0.201,0.15c-4.346,3.243-6.839,8.119-6.839,13.379c0,9.252,7.528,16.779,16.78,16.779c2.915,0,5.849-0.833,8.483-2.409l0.405-0.243l0.265,0.392c3.135,4.621,8.327,7.38,13.885,7.38c9.253,0,16.78-7.526,16.78-16.777C78.277,54.449,75.784,49.573,71.438,46.331z M46.464,52.375c-1.149,2.282-1.737,4.73-1.746,7.277l0,0.187l-0.123,0.141c-1.572,1.799-3.751,2.79-6.136,2.79c-4.511,0-8.181-3.669-8.181-8.18c0-4.51,3.67-8.179,8.181-8.179c3.027,0,6.622,2.482,8.015,5.532l0.1,0.219L46.464,52.375z M38.458,43.571c-6.077,0-11.021,4.943-11.021,11.019c0,6.076,4.944,11.02,11.021,11.02c2.18,0,4.284-0.629,6.084-1.817l0.578-0.382l0.18,0.669c0.154,0.571,0.343,1.146,0.563,1.708l0.152,0.389l-0.355,0.219c-2.299,1.416-4.722,2.135-7.202,2.135c-7.687,0-13.94-6.253-13.94-13.939c0-4.75,2.388-9.131,6.389-11.719c0.404-0.261,0.648-0.703,0.651-1.184V30.412h13.8v11.279c0.003,0.477,0.247,0.919,0.651,1.179c1.64,1.061,3.067,2.473,4.127,4.085l0.232,0.353l-0.308,0.288c-0.469,0.438-0.882,0.864-1.265,1.303l-0.424,0.487l-0.365-0.533C45.772,45.595,42.114,43.571,38.458,43.571z M61.497,73.65c-7.687,0-13.939-6.253-13.939-13.939c0-4.75,2.388-9.132,6.389-11.72c0.404-0.261,0.647-0.703,0.651-1.185V35.531h13.8V46.81c0.004,0.478,0.247,0.919,0.65,1.179c4.001,2.589,6.39,6.971,6.39,11.722C75.438,67.397,69.184,73.65,61.497,73.65z"},{"datad":"M61.497,48.691c-6.076,0-11.02,4.944-11.02,11.02s4.943,11.02,11.02,11.02c6.077,0,11.021-4.943,11.021-11.02S67.574,48.691,61.497,48.691z M61.497,67.891c-4.511,0-8.18-3.669-8.18-8.18s3.669-8.18,8.18-8.18s8.181,3.669,8.181,8.18S66.008,67.891,61.497,67.891z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (197, 'Alcohol', '<path fill="#FFFFFF" d="M66.275,47.256h-1.791V42.94c2.286-0.819,3.929-3.002,3.929-5.568c0-3.19-2.538-5.799-5.7-5.913c-0.808-3.052-3.558-5.207-6.763-5.207c-0.727,0-1.446,0.118-2.146,0.353c-1.902-2.134-4.595-3.347-7.47-3.347c-4.87,0-8.991,3.483-9.845,8.206c-3.137,0.142-5.645,2.739-5.645,5.911c0,2.564,1.643,4.747,3.929,5.566v26.202c0,4.191,3.41,7.602,7.601,7.602h14.512c4.189,0,7.599-3.409,7.599-7.601v-4.098h1.791c2.865,0,5.196-2.331,5.196-5.197v-7.393C71.472,49.588,69.141,47.256,66.275,47.256z M66.275,61.969h-1.791V50.331h1.791c1.17,0,2.121,0.953,2.121,2.124v7.393C68.396,61.018,67.445,61.969,66.275,61.969z M56.886,73.669H42.374c-2.495,0-4.525-2.03-4.525-4.526V43.291h23.564v25.852C61.412,71.639,59.382,73.669,56.886,73.669z M36.761,34.53h1.109c0.833,0,1.508-0.652,1.537-1.486c0.124-3.764,3.166-6.712,6.927-6.712c2.289,0,4.43,1.13,5.727,3.022c0.432,0.631,1.348,0.844,2.008,0.479c0.611-0.335,1.244-0.506,1.8810.506c2.089,0,3.811,1.645,3.921,3.743c0.042,0.818,0.716,1.459,1.533,1.459h1.093c1.567,0,2.842,1.275,2.842,2.842c0,1.567-1.274,2.842-2.842,2.842H36.761c-1.567,0-2.842-1.275-2.842-2.842C33.919,35.805,35.194,34.53,36.761,34.53z"/><path fill="#FFFFFF" d="M43.085,47.188c-0.848,0-1.537,0.69-1.537,1.538v19.179c0,0.848,0.689,1.537,1.537,1.537c0.848,0,1.538-0.689,1.538-1.537V48.726C44.624,47.878,43.934,47.188,43.085,47.188z"/><path fill="#FFFFFF" d="M56.172,47.188c-0.848,0-1.538,0.69-1.538,1.538v19.179c0,0.848,0.69,1.537,1.538,1.537s1.538-0.689,1.538-1.537V48.726C57.71,47.878,57.02,47.188,56.172,47.188z"/><path fill="#FFFFFF" d="M49.628,47.188c-0.848,0-1.538,0.69-1.538,1.538v19.179c0,0.848,0.69,1.537,1.538,1.537s1.538-0.689,1.538-1.537V48.726C51.166,47.878,50.476,47.188,49.628,47.188z"/>', 'alcohol-2', '[{"datad":"M66.275,47.256h-1.791V42.94c2.286-0.819,3.929-3.002,3.929-5.568c0-3.19-2.538-5.799-5.7-5.913c-0.808-3.052-3.558-5.207-6.763-5.207c-0.727,0-1.446,0.118-2.146,0.353c-1.902-2.134-4.595-3.347-7.47-3.347c-4.87,0-8.991,3.483-9.845,8.206c-3.137,0.142-5.645,2.739-5.645,5.911c0,2.564,1.643,4.747,3.929,5.566v26.202c0,4.191,3.41,7.602,7.601,7.602h14.512c4.189,0,7.599-3.409,7.599-7.601v-4.098h1.791c2.865,0,5.196-2.331,5.196-5.197v-7.393C71.472,49.588,69.141,47.256,66.275,47.256z M66.275,61.969h-1.791V50.331h1.791c1.17,0,2.121,0.953,2.121,2.124v7.393C68.396,61.018,67.445,61.969,66.275,61.969z M56.886,73.669H42.374c-2.495,0-4.525-2.03-4.525-4.526V43.291h23.564v25.852C61.412,71.639,59.382,73.669,56.886,73.669z M36.761,34.53h1.109c0.833,0,1.508-0.652,1.537-1.486c0.124-3.764,3.166-6.712,6.927-6.712c2.289,0,4.43,1.13,5.727,3.022c0.432,0.631,1.348,0.844,2.008,0.479c0.611-0.335,1.244-0.506,1.8810.506c2.089,0,3.811,1.645,3.921,3.743c0.042,0.818,0.716,1.459,1.533,1.459h1.093c1.567,0,2.842,1.275,2.842,2.842c0,1.567-1.274,2.842-2.842,2.842H36.761c-1.567,0-2.842-1.275-2.842-2.842C33.919,35.805,35.194,34.53,36.761,34.53z"},{"datad":"M43.085,47.188c-0.848,0-1.537,0.69-1.537,1.538v19.179c0,0.848,0.689,1.537,1.537,1.537c0.848,0,1.538-0.689,1.538-1.537V48.726C44.624,47.878,43.934,47.188,43.085,47.188z"},{"datad":"M56.172,47.188c-0.848,0-1.538,0.69-1.538,1.538v19.179c0,0.848,0.69,1.537,1.538,1.537s1.538-0.689,1.538-1.537V48.726C57.71,47.878,57.02,47.188,56.172,47.188z"},{"datad":"M49.628,47.188c-0.848,0-1.538,0.69-1.538,1.538v19.179c0,0.848,0.69,1.537,1.538,1.537s1.538-0.689,1.538-1.537V48.726C51.166,47.878,50.476,47.188,49.628,47.188z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (196, 'Satellite', '<path fill="#FFFFFF" d="M77.049,55.68l-7.9-7.894l3.831-3.831c0.344-0.344,0.513-0.827,0.458-1.311c-0.824-7.301-6.501-12.955-13.806-13.749c-0.479-0.05-0.962,0.117-1.304,0.459l-3.747,3.747l-7.986-7.992c-0.3-0.3-0.707-0.469-1.131-0.469c0,0,0,0,0,0c-0.424,0-0.831,0.168-1.131,0.469l-8.534,8.533c-0.3,0.3-0.469,0.707-0.469,1.131s0.168,0.832,0.469,1.131l7.99,7.989l-3.777,3.777c-0.344,0.344-0.513,0.827-0.458,1.311c0.823,7.303,6.5,12.957,13.806,13.75c0.058,0.006,0.115,0.009,0.173,0.009c0.422,0,0.83-0.167,1.132-0.468l3.806-3.807l7.901,7.895c0.313,0.313,0.722,0.468,1.131,0.468s0.819-0.156,1.132-0.468l8.417-8.417c0.3-0.301,0.468-0.708,0.468-1.132C77.518,56.387,77.35,55.979,77.049,55.68z M39.192,34.774l6.271-6.27l6.855,6.861l-6.268,6.267L39.192,34.774z M52.962,59.447c-5.198-0.875-9.225-4.884-10.123-10.079l4.301-4.301c0.015-0.014,0.032-0.023,0.047-0.038s0.024-0.032,0.038-0.046L60.028,32.18c5.198,0.875,9.224,4.884,10.123,10.078L52.962,59.447z M67.501,62.966l-6.769-6.763l6.154-6.154l6.768,6.763L67.501,62.966z"/><path fill="#FFFFFF" d="M50.31,72.473c1.564,0,3.154-0.181,4.74-0.559c0.86-0.205,1.391-1.067,1.187-1.927s-1.066-1.392-1.927-1.187c-9.267,2.203-18.599-3.538-20.804-12.805c-0.636-2.674-0.622-5.497,0.04-8.166c0.213-0.857-0.31-1.725-1.167-1.938c-0.854-0.213-1.725,0.309-1.938,1.167c-0.785,3.162-0.801,6.507-0.048,9.676C32.628,66.133,41.046,72.473,50.31,72.473z"/><path fill="#FFFFFF" d="M54.393,74.178c-2.76,0.504-5.554,0.49-8.305-0.037c-12.207-2.346-20.23-14.187-17.885-26.394c0.167-0.868-0.402-1.706-1.27-1.873c-0.865-0.166-1.706,0.401-1.873,1.27c-2.678,13.939,6.484,27.461,20.424,30.14c1.608,0.309,3.23,0.463,4.853,0.463c1.546,0,3.093-0.14,4.63-0.42c0.869-0.158,1.445-0.992,1.287-1.861C56.096,74.595,55.253,74.021,54.393,74.178z"/>', 'satellite-2', '[{"datad":"M77.049,55.68l-7.9-7.894l3.831-3.831c0.344-0.344,0.513-0.827,0.458-1.311c-0.824-7.301-6.501-12.955-13.806-13.749c-0.479-0.05-0.962,0.117-1.304,0.459l-3.747,3.747l-7.986-7.992c-0.3-0.3-0.707-0.469-1.131-0.469c0,0,0,0,0,0c-0.424,0-0.831,0.168-1.131,0.469l-8.534,8.533c-0.3,0.3-0.469,0.707-0.469,1.131s0.168,0.832,0.469,1.131l7.99,7.989l-3.777,3.777c-0.344,0.344-0.513,0.827-0.458,1.311c0.823,7.303,6.5,12.957,13.806,13.75c0.058,0.006,0.115,0.009,0.173,0.009c0.422,0,0.83-0.167,1.132-0.468l3.806-3.807l7.901,7.895c0.313,0.313,0.722,0.468,1.131,0.468s0.819-0.156,1.132-0.468l8.417-8.417c0.3-0.301,0.468-0.708,0.468-1.132C77.518,56.387,77.35,55.979,77.049,55.68z M39.192,34.774l6.271-6.27l6.855,6.861l-6.268,6.267L39.192,34.774z M52.962,59.447c-5.198-0.875-9.225-4.884-10.123-10.079l4.301-4.301c0.015-0.014,0.032-0.023,0.047-0.038s0.024-0.032,0.038-0.046L60.028,32.18c5.198,0.875,9.224,4.884,10.123,10.078L52.962,59.447z M67.501,62.966l-6.769-6.763l6.154-6.154l6.768,6.763L67.501,62.966z"},{"datad":"M50.31,72.473c1.564,0,3.154-0.181,4.74-0.559c0.86-0.205,1.391-1.067,1.187-1.927s-1.066-1.392-1.927-1.187c-9.267,2.203-18.599-3.538-20.804-12.805c-0.636-2.674-0.622-5.497,0.04-8.166c0.213-0.857-0.31-1.725-1.167-1.938c-0.854-0.213-1.725,0.309-1.938,1.167c-0.785,3.162-0.801,6.507-0.048,9.676C32.628,66.133,41.046,72.473,50.31,72.473z"},{"datad":"M54.393,74.178c-2.76,0.504-5.554,0.49-8.305-0.037c-12.207-2.346-20.23-14.187-17.885-26.394c0.167-0.868-0.402-1.706-1.27-1.873c-0.865-0.166-1.706,0.401-1.873,1.27c-2.678,13.939,6.484,27.461,20.424,30.14c1.608,0.309,3.23,0.463,4.853,0.463c1.546,0,3.093-0.14,4.63-0.42c0.869-0.158,1.445-0.992,1.287-1.861C56.096,74.595,55.253,74.021,54.393,74.178z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (134, 'Captured', '<path fill="#FFFFFF" d="M55.211,37.09c-7.295-4.742-11.871-4.184-13.73-3.593v-1.224c0-0.527-0.428-0.956-0.957-0.956 s-0.956,0.428-0.956,0.956v50.455c0,0.528,0.427,0.956,0.956,0.956s0.957-0.428,0.957-0.956V56.127 c1.859-0.593,6.435-1.151,13.73,3.592c9.792,6.363,17.135,0,17.135,0V37.09C72.344,37.09,65.003,43.454,55.211,37.09z"/>', 'capture-2', '[{"datad":"M55.211,37.09c-7.295-4.742-11.871-4.184-13.73-3.593v-1.224c0-0.527-0.428-0.956-0.957-0.956 s-0.956,0.428-0.956,0.956v50.455c0,0.528,0.427,0.956,0.956,0.956s0.957-0.428,0.957-0.956V56.127 c1.859-0.593,6.435-1.151,13.73,3.592c9.792,6.363,17.135,0,17.135,0V37.09C72.344,37.09,65.003,43.454,55.211,37.09z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (195, 'Corruption', '<path fill="#FFFFFF" d="M68.126,50.225c-0.437-5.476-3.354-10.398-7.897-13.384l3.139-2.511c0.904-0.723,1.422-1.803,1.422-2.961c0-1.519-0.902-2.887-2.299-3.486l-4.943-2.118l0.154-0.384c0.177-0.44,0.267-0.904,0.267-1.379c0-2.043-1.662-3.705-3.705-3.705h-7.558C44.662,20.295,43,21.957,43,24c0,0.477,0.089,0.94,0.264,1.375l0.155,0.388l-4.943,2.119c-1.396,0.598-2.298,1.966-2.298,3.486c0,1.158,0.519,2.237,1.422,2.962l3.139,2.511c-4.543,2.986-7.46,7.909-7.897,13.384c-2.942,2.705-4.624,6.526-4.624,10.53c0,7.888,6.417,14.306,14.306,14.306h15.92c7.889,0,14.307-6.418,14.307-14.306C72.75,56.751,71.068,52.93,68.126,50.225z M58.443,71.465h-15.92c-5.905,0-10.709-4.805-10.709-10.71c0-3.218,1.429-6.237,3.919-8.281l0.628-0.514l0.029-0.813c0.272-7.632,6.463-13.611,14.09313.611s13.82,5.979,14.094,13.612l0.029,0.812l0.627,0.513c2.491,2.045,3.92,5.064,3.92,8.282C69.153,66.66,64.349,71.465,58.443,71.465z M44.263,35.054l-4.417-3.534c-0.047-0.038-0.073-0.092-0.073-0.153c0-0.079,0.047-0.15,0.12-0.181l4.863-2.083l2.084,5.21C45.961,34.496,45.099,34.743,44.263,35.054z M61.121,31.522l-4.416,3.532c-0.838-0.312-1.701-0.56-2.579-0.742l0.735-1.841l0.004,0.001l1.347-3.369l4.861,2.083c0.073,0.031,0.12,0.102,0.12,0.181C61.193,31.43,61.167,31.485,61.121,31.522zM51.525,31.136l0.185,0.074l-0.179-0.067l-1.048,2.595L46.596,24c0-0.06,0.048-0.108,0.108-0.108h7.558c0.059,0,0.108,0.05,0.102,0.149L51.525,31.136z"/><path fill="#FFFFFF" d="M53.326,46.449h-1.045v-2.274h-3.596v2.274h-1.045c-2.559,0-4.64,1.955-4.64,4.357v1.705c0,2.402,2.082,4.356,4.64,4.356h5.686c0.566,0,1.045,0.349,1.045,0.761v1.705c0,0.412-0.479,0.761-1.045,0.761h-5.686c-0.566,0-1.044-0.349-1.044-0.761v-0.661H43v0.661c0,2.402,2.082,4.357,4.64,4.357h1.045v2.273h3.596V63.69h1.045c2.56,0,4.642-1.955,4.642-4.357v-1.705c0-2.402-2.082-4.357-4.642-4.357h-5.686c-0.566,0-1.044-0.348-1.044-0.76v-1.705c0-0.413,0.479-0.761,1.044-0.761h5.686c0.566,0,1.045,0.348,1.045,0.761v1.513h3.597v-1.513C57.968,48.403,55.886,46.449,53.326,46.449z"/>', 'corruption-2', '[{"datad":"M68.126,50.225c-0.437-5.476-3.354-10.398-7.897-13.384l3.139-2.511c0.904-0.723,1.422-1.803,1.422-2.961c0-1.519-0.902-2.887-2.299-3.486l-4.943-2.118l0.154-0.384c0.177-0.44,0.267-0.904,0.267-1.379c0-2.043-1.662-3.705-3.705-3.705h-7.558C44.662,20.295,43,21.957,43,24c0,0.477,0.089,0.94,0.264,1.375l0.155,0.388l-4.943,2.119c-1.396,0.598-2.298,1.966-2.298,3.486c0,1.158,0.519,2.237,1.422,2.962l3.139,2.511c-4.543,2.986-7.46,7.909-7.897,13.384c-2.942,2.705-4.624,6.526-4.624,10.53c0,7.888,6.417,14.306,14.306,14.306h15.92c7.889,0,14.307-6.418,14.307-14.306C72.75,56.751,71.068,52.93,68.126,50.225z M58.443,71.465h-15.92c-5.905,0-10.709-4.805-10.709-10.71c0-3.218,1.429-6.237,3.919-8.281l0.628-0.514l0.029-0.813c0.272-7.632,6.463-13.611,14.09313.611s13.82,5.979,14.094,13.612l0.029,0.812l0.627,0.513c2.491,2.045,3.92,5.064,3.92,8.282C69.153,66.66,64.349,71.465,58.443,71.465z M44.263,35.054l-4.417-3.534c-0.047-0.038-0.073-0.092-0.073-0.153c0-0.079,0.047-0.15,0.12-0.181l4.863-2.083l2.084,5.21C45.961,34.496,45.099,34.743,44.263,35.054z M61.121,31.522l-4.416,3.532c-0.838-0.312-1.701-0.56-2.579-0.742l0.735-1.841l0.004,0.001l1.347-3.369l4.861,2.083c0.073,0.031,0.12,0.102,0.12,0.181C61.193,31.43,61.167,31.485,61.121,31.522zM51.525,31.136l0.185,0.074l-0.179-0.067l-1.048,2.595L46.596,24c0-0.06,0.048-0.108,0.108-0.108h7.558c0.059,0,0.108,0.05,0.102,0.149L51.525,31.136z"},{"datad":"M53.326,46.449h-1.045v-2.274h-3.596v2.274h-1.045c-2.559,0-4.64,1.955-4.64,4.357v1.705c0,2.402,2.082,4.356,4.64,4.356h5.686c0.566,0,1.045,0.349,1.045,0.761v1.705c0,0.412-0.479,0.761-1.045,0.761h-5.686c-0.566,0-1.044-0.349-1.044-0.761v-0.661H43v0.661c0,2.402,2.082,4.357,4.64,4.357h1.045v2.273h3.596V63.69h1.045c2.56,0,4.642-1.955,4.642-4.357v-1.705c0-2.402-2.082-4.357-4.642-4.357h-5.686c-0.566,0-1.044-0.348-1.044-0.76v-1.705c0-0.413,0.479-0.761,1.044-0.761h5.686c0.566,0,1.045,0.348,1.045,0.761v1.513h3.597v-1.513C57.968,48.403,55.886,46.449,53.326,46.449z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (194, 'Manpads', '<path fill="#FFFFFF" d="M85.463,42.587c-0.487,0-0.882,0.396-0.882,0.881v1.119c-0.184-0.244-0.474-0.405-0.803-0.405h-0.152c-0.557,0-1.008,0.451-1.008,1.007v0.914l-0.661-0.002H79.27l-17.977,0.009H47v-3.444h4v-1.326H39.501v0.003c-0.502-1.171-1.667-1.996-3.019-1.996H23.766c-1.807,0-3.282,1.473-3.282,3.281v4.496h-1.779l-1.981-1.984h-1.697c-1.829,2.887-1.829,5.795,0,8.681h1.697l1.981-1.982h1.779v1.541c0,1.809,1.475,3.283,3.282,3.283h8.465v4.096c0,1.477,1.19,2.667,2.666,2.667c1.477,0,2.666-1.19,2.666-2.667v-4.272c1.278-0.438,2.203-1.65,2.203-3.106V52.83h2.291v1.979c0,1.322,1.256,2.402,2.797,2.402h2.666l0.13-0.116v6.574c0,1.477,1.189,2.665,2.666,2.665s2.666-1.188,2.666-2.665V57.63l0.066-4.8h8.27l21.303,0.011v0.744c0,0.557,0.451,1.008,1.008,1.008h0.152c0.329,0,0.619-0.16,0.803-0.404v1.453c0,0.487,0.395,0.883,0.882,0.883c0.486,0,0.881-0.396,0.881-0.883V43.469C86.344,42.983,85.949,42.587,85.463,42.587zM39.765,46.111v-3.444h5.318v3.444h-2.063H39.765z M47.541,56.285h-2.666c-1.036,0-1.872-0.659-1.872-1.477V52.83h0.018h2.062c-0.048,0.633-0.244,1.263-0.604,1.803c0.242,0.111,0.507,0.221,0.772,0.332c0.064,0.023,0.109,0.043,0.176,0.043c0.087,0,0.133-0.043,0.197-0.088c0.678-0.546,1.16-1.283,1.425-2.09h0.493V56.285z"/>', 'manpads-2', '[{"datad":"M85.463,42.587c-0.487,0-0.882,0.396-0.882,0.881v1.119c-0.184-0.244-0.474-0.405-0.803-0.405h-0.152c-0.557,0-1.008,0.451-1.008,1.007v0.914l-0.661-0.002H79.27l-17.977,0.009H47v-3.444h4v-1.326H39.501v0.003c-0.502-1.171-1.667-1.996-3.019-1.996H23.766c-1.807,0-3.282,1.473-3.282,3.281v4.496h-1.779l-1.981-1.984h-1.697c-1.829,2.887-1.829,5.795,0,8.681h1.697l1.981-1.982h1.779v1.541c0,1.809,1.475,3.283,3.282,3.283h8.465v4.096c0,1.477,1.19,2.667,2.666,2.667c1.477,0,2.666-1.19,2.666-2.667v-4.272c1.278-0.438,2.203-1.65,2.203-3.106V52.83h2.291v1.979c0,1.322,1.256,2.402,2.797,2.402h2.666l0.13-0.116v6.574c0,1.477,1.189,2.665,2.666,2.665s2.666-1.188,2.666-2.665V57.63l0.066-4.8h8.27l21.303,0.011v0.744c0,0.557,0.451,1.008,1.008,1.008h0.152c0.329,0,0.619-0.16,0.803-0.404v1.453c0,0.487,0.395,0.883,0.882,0.883c0.486,0,0.881-0.396,0.881-0.883V43.469C86.344,42.983,85.949,42.587,85.463,42.587zM39.765,46.111v-3.444h5.318v3.444h-2.063H39.765z M47.541,56.285h-2.666c-1.036,0-1.872-0.659-1.872-1.477V52.83h0.018h2.062c-0.048,0.633-0.244,1.263-0.604,1.803c0.242,0.111,0.507,0.221,0.772,0.332c0.064,0.023,0.109,0.043,0.176,0.043c0.087,0,0.133-0.043,0.197-0.088c0.678-0.546,1.16-1.283,1.425-2.09h0.493V56.285z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (193, 'Pollution', '<path fill="#FFFFFF" d="M81.259,66.651h-2.745l-2.472-8.47c-1.47-5.042-4.468-9.373-8.669-12.523c-3.696-2.772-8.048-4.406-12.626-4.748v-7.387c0-1.006-0.818-1.824-1.824-1.824h-7.579c-1.006,0-1.824,0.818-1.824,1.824v0.907H18.741c-1.006,0-1.824,0.818-1.824,1.824c0,1.006,0.818,1.825,1.824,1.825H43.52v15.681H18.741c-1.006,0-1.824,0.818-1.824,1.824c0,1.007,0.818,1.825,1.824,1.825H43.52v0.906c0,1.006,0.818,1.824,1.824,1.824h7.58c1.006,0,1.824-0.818,1.824-1.824v-0.107c1.391,1.306,2.944,3.793,3.17,8.442H35.081c-1.006,0-1.824,0.818-1.824,1.824s0.818,1.824,1.824,1.824h46.178c1.006,0,1.824-0.818,1.824-1.824S82.265,66.651,81.259,66.651z M51.099,56.492h-3.931V35.348h3.931V56.492z M70.416,66.651c-0.031-3.653-1.611-7.315-4.586-10.597c-2.224-2.455-4.438-3.842-4.531-3.899c-0.625-0.388-1.443-0.195-1.83,0.429c-0.388,0.623-0.195,1.443,0.428,1.83c0.079,0.049,7.782,4.921,7.86,12.237h-6.188c-0.147-3.313-0.981-7.615-3.903-10.67c-1.042-1.089-2.102-1.757-2.918-2.157v-9.253c3.783,0.333,7.376,1.71,10.436,4.005c3.566,2.674,6.11,6.349,7.357,10.626l2.173,7.448H70.416z"/>', 'polution-2', '[{"datad":"M81.259,66.651h-2.745l-2.472-8.47c-1.47-5.042-4.468-9.373-8.669-12.523c-3.696-2.772-8.048-4.406-12.626-4.748v-7.387c0-1.006-0.818-1.824-1.824-1.824h-7.579c-1.006,0-1.824,0.818-1.824,1.824v0.907H18.741c-1.006,0-1.824,0.818-1.824,1.824c0,1.006,0.818,1.825,1.824,1.825H43.52v15.681H18.741c-1.006,0-1.824,0.818-1.824,1.824c0,1.007,0.818,1.825,1.824,1.825H43.52v0.906c0,1.006,0.818,1.824,1.824,1.824h7.58c1.006,0,1.824-0.818,1.824-1.824v-0.107c1.391,1.306,2.944,3.793,3.17,8.442H35.081c-1.006,0-1.824,0.818-1.824,1.824s0.818,1.824,1.824,1.824h46.178c1.006,0,1.824-0.818,1.824-1.824S82.265,66.651,81.259,66.651z M51.099,56.492h-3.931V35.348h3.931V56.492z M70.416,66.651c-0.031-3.653-1.611-7.315-4.586-10.597c-2.224-2.455-4.438-3.842-4.531-3.899c-0.625-0.388-1.443-0.195-1.83,0.429c-0.388,0.623-0.195,1.443,0.428,1.83c0.079,0.049,7.782,4.921,7.86,12.237h-6.188c-0.147-3.313-0.981-7.615-3.903-10.67c-1.042-1.089-2.102-1.757-2.918-2.157v-9.253c3.783,0.333,7.376,1.71,10.436,4.005c3.566,2.674,6.11,6.349,7.357,10.626l2.173,7.448H70.416z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (192, 'Snow', '<rect x="-59.3" y="49.005" fill="#010101" width="0.244" height="0.221"/> <polygon fill="#010101" points="-53.738,52.108 -53.746,52.115 -53.746,52.108 "/> <polygon fill="#010101" points="-53.738,52.108 -53.746,52.115 -53.746,52.108 "/> <path fill="#FFFFFF" d="M50.002,27.88c-1.018,0-1.84,0.823-1.84,1.841v3.437l-3.22-3.514c-0.687-0.749-1.852-0.8-2.604-0.114 c-0.749,0.687-0.798,1.852-0.112,2.601l5.936,6.479v7.703l-6.67-3.85l-2.644-8.384c-0.306-0.968-1.346-1.505-2.313-1.202 c-0.966,0.307-1.506,1.342-1.2,2.311l1.435,4.55l-2.981-1.721c-0.88-0.51-2.006-0.206-2.513,0.674 c-0.512,0.88-0.209,2.006,0.672,2.515l2.982,1.721l-4.658,1.031c-0.996,0.219-1.622,1.202-1.402,2.194 c0.191,0.859,0.954,1.443,1.799,1.443c0.128,0,0.265-0.015,0.397-0.042l8.586-1.9L46.32,49.5l-6.67,3.851l-8.583-1.902 c-1.007-0.218-1.978,0.408-2.196,1.398c-0.219,0.992,0.406,1.976,1.399,2.197l4.658,1.031l-2.979,1.72 c-0.88,0.508-1.182,1.633-0.672,2.516c0.341,0.588,0.959,0.92,1.597,0.92c0.313,0,0.629-0.079,0.917-0.247l2.982-1.721l-1.436,4.552 c-0.306,0.969,0.234,2.004,1.2,2.312c0.184,0.057,0.37,0.084,0.554,0.084c0.784,0,1.51-0.5,1.758-1.286l2.644-8.386l6.669-3.85 v7.697l-5.939,6.483c-0.687,0.749-0.635,1.915,0.112,2.602c0.357,0.324,0.803,0.484,1.245,0.484c0.501,0,0.996-0.201,1.359-0.599 l3.224-3.519v3.44c0,1.019,0.822,1.842,1.84,1.842c1.016,0,1.841-0.823,1.841-1.842v-3.441l3.226,3.521 c0.361,0.396,0.856,0.597,1.354,0.597c0.445,0,0.891-0.16,1.245-0.482c0.752-0.687,0.802-1.852,0.115-2.602l-5.94-6.485v-7.696 l6.668,3.851l2.64,8.384c0.249,0.786,0.974,1.286,1.754,1.286c0.185,0,0.373-0.025,0.554-0.084c0.971-0.306,1.511-1.341,1.205-2.312 l-1.434-4.551l2.98,1.72c0.287,0.168,0.603,0.247,0.916,0.247c0.638,0,1.256-0.329,1.597-0.92c0.512-0.881,0.21-2.008-0.672-2.516 l-2.979-1.72l4.658-1.031c0.992-0.222,1.618-1.203,1.398-2.197c-0.219-0.99-1.193-1.618-2.195-1.398l-8.583,1.901L53.684,49.5 l6.667-3.849l8.583,1.9c0.132,0.024,0.267,0.042,0.399,0.042c0.845,0,1.603-0.585,1.793-1.443c0.219-0.992-0.408-1.974-1.4-2.194 l-4.654-1.031l2.981-1.721c0.881-0.509,1.183-1.633,0.673-2.515s-1.635-1.182-2.513-0.674l-2.98,1.72l1.434-4.549 c0.306-0.97-0.233-2.004-1.204-2.31c-0.967-0.304-2.003,0.233-2.309,1.202l-2.64,8.383l-6.67,3.85v-7.7l5.938-6.481 c0.687-0.75,0.636-1.915-0.115-2.602c-0.751-0.687-1.913-0.633-2.6,0.114l-3.224,3.519v-3.44 C51.844,28.703,51.021,27.88,50.002,27.88z"/>', 'snow-2', '[{"datad":"M50.002,27.88c-1.018,0-1.84,0.823-1.84,1.841v3.437l-3.22-3.514c-0.687-0.749-1.852-0.8-2.604-0.114 c-0.749,0.687-0.798,1.852-0.112,2.601l5.936,6.479v7.703l-6.67-3.85l-2.644-8.384c-0.306-0.968-1.346-1.505-2.313-1.202 c-0.966,0.307-1.506,1.342-1.2,2.311l1.435,4.55l-2.981-1.721c-0.88-0.51-2.006-0.206-2.513,0.674 c-0.512,0.88-0.209,2.006,0.672,2.515l2.982,1.721l-4.658,1.031c-0.996,0.219-1.622,1.202-1.402,2.194 c0.191,0.859,0.954,1.443,1.799,1.443c0.128,0,0.265-0.015,0.397-0.042l8.586-1.9L46.32,49.5l-6.67,3.851l-8.583-1.902 c-1.007-0.218-1.978,0.408-2.196,1.398c-0.219,0.992,0.406,1.976,1.399,2.197l4.658,1.031l-2.979,1.72 c-0.88,0.508-1.182,1.633-0.672,2.516c0.341,0.588,0.959,0.92,1.597,0.92c0.313,0,0.629-0.079,0.917-0.247l2.982-1.721l-1.436,4.552 c-0.306,0.969,0.234,2.004,1.2,2.312c0.184,0.057,0.37,0.084,0.554,0.084c0.784,0,1.51-0.5,1.758-1.286l2.644-8.386l6.669-3.85 v7.697l-5.939,6.483c-0.687,0.749-0.635,1.915,0.112,2.602c0.357,0.324,0.803,0.484,1.245,0.484c0.501,0,0.996-0.201,1.359-0.599 l3.224-3.519v3.44c0,1.019,0.822,1.842,1.84,1.842c1.016,0,1.841-0.823,1.841-1.842v-3.441l3.226,3.521 c0.361,0.396,0.856,0.597,1.354,0.597c0.445,0,0.891-0.16,1.245-0.482c0.752-0.687,0.802-1.852,0.115-2.602l-5.94-6.485v-7.696 l6.668,3.851l2.64,8.384c0.249,0.786,0.974,1.286,1.754,1.286c0.185,0,0.373-0.025,0.554-0.084c0.971-0.306,1.511-1.341,1.205-2.312 l-1.434-4.551l2.98,1.72c0.287,0.168,0.603,0.247,0.916,0.247c0.638,0,1.256-0.329,1.597-0.92c0.512-0.881,0.21-2.008-0.672-2.516 l-2.979-1.72l4.658-1.031c0.992-0.222,1.618-1.203,1.398-2.197c-0.219-0.99-1.193-1.618-2.195-1.398l-8.583,1.901L53.684,49.5 l6.667-3.849l8.583,1.9c0.132,0.024,0.267,0.042,0.399,0.042c0.845,0,1.603-0.585,1.793-1.443c0.219-0.992-0.408-1.974-1.4-2.194 l-4.654-1.031l2.981-1.721c0.881-0.509,1.183-1.633,0.673-2.515s-1.635-1.182-2.513-0.674l-2.98,1.72l1.434-4.549 c0.306-0.97-0.233-2.004-1.204-2.31c-0.967-0.304-2.003,0.233-2.309,1.202l-2.64,8.383l-6.67,3.85v-7.7l5.938-6.481 c0.687-0.75,0.636-1.915-0.115-2.602c-0.751-0.687-1.913-0.633-2.6,0.114l-3.224,3.519v-3.44 C51.844,28.703,51.021,27.88,50.002,27.88z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (191, 'Volcano', '<rect x="-59.3" y="49.005" fill="#010101" width="0.244" height="0.221"/> <polygon fill="#010101" points="-53.738,52.108 -53.746,52.115 -53.746,52.108 "/> <polygon fill="#010101" points="-53.738,52.108 -53.746,52.115 -53.746,52.108 "/> <path fill="#FFFFFF" d="M71.828,73.707L56.742,48.655h-0.381v-5.582c0.174,0.019,0.352,0.03,0.529,0.03 c0.568,0,1.131-0.107,1.658-0.312c0.748,0.764,1.783,1.206,2.863,1.206c1.855,0,3.453-1.294,3.887-3.048 c2.473-0.214,4.42-2.293,4.42-4.821c0-2.668-2.172-4.84-4.84-4.84c-2.383,0-4.369,1.73-4.766,4 c-0.375-0.371-0.811-0.678-1.291-0.902c-0.146-3.067-2.689-5.518-5.795-5.518c-1.271,0-2.48,0.405-3.493,1.171 c-0.705,0.534-1.283,1.229-1.681,2.015c-0.774-0.5-1.675-0.767-2.617-0.767c-1.104,0-2.185,0.382-3.042,1.076 c-0.583,0.472-1.047,1.075-1.357,1.75c-0.68-0.357-1.443-0.548-2.219-0.548c-2.629,0-4.769,2.139-4.769,4.768 c0,2.63,2.139,4.769,4.769,4.769c1.879,0,3.536-1.081,4.309-2.722c0.713,0.387,1.52,0.593,2.339,0.587 c0.081,0.278,0.188,0.544,0.32,0.796v6.891h-0.465L29.89,73.707h-1.681v1.424h45.583v-1.424H71.828z M45.236,39.545 c-0.809,0-1.595-0.29-2.211-0.814l-0.885-0.752l-0.269,1.131c-0.358,1.513-1.697,2.57-3.253,2.57c-1.845,0-3.345-1.5-3.345-3.345 c0-1.843,1.5-3.345,3.345-3.345c0.78,0,1.54,0.274,2.138,0.773l0.894,0.744l0.256-1.135c0.348-1.541,1.749-2.66,3.331-2.66 c0.913,0,1.771,0.355,2.416,1l0.843,0.842l0.342-1.142c0.549-1.838,2.273-3.121,4.193-3.121c2.414,0,4.377,1.964,4.377,4.377 c0,0.058-0.002,0.115-0.004,0.172l-0.021,0.532l0.508,0.169c0.805,0.269,1.477,0.862,1.844,1.629l0.271,0.566l0.596-0.197 c0.107-0.036,0.219-0.065,0.332-0.086l0.676-0.127l-0.105-0.68c-0.025-0.171-0.039-0.345-0.039-0.52 c0-1.885,1.531-3.417,3.416-3.417c1.883,0,3.416,1.532,3.416,3.417c0,1.883-1.533,3.416-3.416,3.416 c-0.051,0-0.104-0.002-0.152-0.004l-0.689-0.029l-0.051,0.685c-0.107,1.333-1.236,2.377-2.574,2.377 c-0.826,0-1.609-0.401-2.094-1.071l-0.369-0.509l-0.551,0.302c-0.457,0.252-0.98,0.384-1.508,0.384 c-1.357,0-2.557-0.865-2.988-2.151l-0.168-0.506l-0.533,0.02c-0.047,0.001-0.094,0.003-0.143,0.003l-0.707,0.006v0.708 c0,1.609-1.309,2.918-2.916,2.918c-1.468,0-2.712-1.098-2.895-2.552l-0.09-0.72l-0.719,0.104 C45.566,39.533,45.399,39.545,45.236,39.545z M47.009,43.356c0.697,0.47,1.533,0.744,2.427,0.744c1.773,0,3.303-1.07,3.975-2.6 c0.422,0.494,0.941,0.89,1.527,1.166v5.989h-7.929V43.356z M40.895,58.346c0.281,0.23,0.646,0.452,1.106,0.582 c0.926,0.263,1.943,0.079,3.022-0.547c2.526-1.463,2.941-1.523,6.356-0.944c3.531,0.599,4.197,0.835,6.605,3.122 c1.563,1.485,2.598,1.971,3.615,1.971c0.529,0,1.051-0.131,1.645-0.319l6.924,11.497H31.556L40.895,58.346z"/> <rect x="34.977" y="65.665" transform="matrix(-0.464 0.8858 -0.8858 -0.464 117.4158 61.7077)" fill="#FFFFFF" width="10.124" height="1.424"/> <rect x="50.04" y="60.042" fill="#FFFFFF" width="1.423" height="6.335"/> <rect x="62.244" y="64.77" transform="matrix(-0.8903 0.4554 -0.4554 -0.8903 150.1403 100.5609)" fill="#FFFFFF" width="1.424" height="7.194"/> <rect x="54.046" y="64.42" transform="matrix(0.3317 0.9434 -0.9434 0.3317 99.8596 -10.6968)" fill="#FFFFFF" width="6.866" height="1.423"/> <rect x="42.89" y="63.886" transform="matrix(0.8944 0.4474 -0.4474 0.8944 34.396 -12.4703)" fill="#FFFFFF" width="1.423" height="5.411"/>', 'volcano-2', '[{"datad":"M71.828,73.707L56.742,48.655h-0.381v-5.582c0.174,0.019,0.352,0.03,0.529,0.03 c0.568,0,1.131-0.107,1.658-0.312c0.748,0.764,1.783,1.206,2.863,1.206c1.855,0,3.453-1.294,3.887-3.048 c2.473-0.214,4.42-2.293,4.42-4.821c0-2.668-2.172-4.84-4.84-4.84c-2.383,0-4.369,1.73-4.766,4 c-0.375-0.371-0.811-0.678-1.291-0.902c-0.146-3.067-2.689-5.518-5.795-5.518c-1.271,0-2.48,0.405-3.493,1.171 c-0.705,0.534-1.283,1.229-1.681,2.015c-0.774-0.5-1.675-0.767-2.617-0.767c-1.104,0-2.185,0.382-3.042,1.076 c-0.583,0.472-1.047,1.075-1.357,1.75c-0.68-0.357-1.443-0.548-2.219-0.548c-2.629,0-4.769,2.139-4.769,4.768 c0,2.63,2.139,4.769,4.769,4.769c1.879,0,3.536-1.081,4.309-2.722c0.713,0.387,1.52,0.593,2.339,0.587 c0.081,0.278,0.188,0.544,0.32,0.796v6.891h-0.465L29.89,73.707h-1.681v1.424h45.583v-1.424H71.828z M45.236,39.545 c-0.809,0-1.595-0.29-2.211-0.814l-0.885-0.752l-0.269,1.131c-0.358,1.513-1.697,2.57-3.253,2.57c-1.845,0-3.345-1.5-3.345-3.345 c0-1.843,1.5-3.345,3.345-3.345c0.78,0,1.54,0.274,2.138,0.773l0.894,0.744l0.256-1.135c0.348-1.541,1.749-2.66,3.331-2.66 c0.913,0,1.771,0.355,2.416,1l0.843,0.842l0.342-1.142c0.549-1.838,2.273-3.121,4.193-3.121c2.414,0,4.377,1.964,4.377,4.377 c0,0.058-0.002,0.115-0.004,0.172l-0.021,0.532l0.508,0.169c0.805,0.269,1.477,0.862,1.844,1.629l0.271,0.566l0.596-0.197 c0.107-0.036,0.219-0.065,0.332-0.086l0.676-0.127l-0.105-0.68c-0.025-0.171-0.039-0.345-0.039-0.52 c0-1.885,1.531-3.417,3.416-3.417c1.883,0,3.416,1.532,3.416,3.417c0,1.883-1.533,3.416-3.416,3.416 c-0.051,0-0.104-0.002-0.152-0.004l-0.689-0.029l-0.051,0.685c-0.107,1.333-1.236,2.377-2.574,2.377 c-0.826,0-1.609-0.401-2.094-1.071l-0.369-0.509l-0.551,0.302c-0.457,0.252-0.98,0.384-1.508,0.384 c-1.357,0-2.557-0.865-2.988-2.151l-0.168-0.506l-0.533,0.02c-0.047,0.001-0.094,0.003-0.143,0.003l-0.707,0.006v0.708 c0,1.609-1.309,2.918-2.916,2.918c-1.468,0-2.712-1.098-2.895-2.552l-0.09-0.72l-0.719,0.104 C45.566,39.533,45.399,39.545,45.236,39.545z M47.009,43.356c0.697,0.47,1.533,0.744,2.427,0.744c1.773,0,3.303-1.07,3.975-2.6 c0.422,0.494,0.941,0.89,1.527,1.166v5.989h-7.929V43.356z M40.895,58.346c0.281,0.23,0.646,0.452,1.106,0.582 c0.926,0.263,1.943,0.079,3.022-0.547c2.526-1.463,2.941-1.523,6.356-0.944c3.531,0.599,4.197,0.835,6.605,3.122 c1.563,1.485,2.598,1.971,3.615,1.971c0.529,0,1.051-0.131,1.645-0.319l6.924,11.497H31.556L40.895,58.346z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (190, 'Map', '<polygon fill="#010101" points="50.201,44.588 50.193,44.595 50.193,44.588 "/> <polygon fill="#010101" points="50.201,44.588 50.193,44.595 50.193,44.588 "/> <path fill="#FFFFFF" d="M72.808,44.799v21.649l-9.79,4.4V51.538c0-1.137-1.729-1.137-1.729,0v19.311l-9.791-4.401V36.128 l0.954,0.429c1.046,0.469,1.75-1.107,0.708-1.576l-2.172-0.976c-0.229-0.105-0.474-0.103-0.708,0l-11.164,5.017l-11.165-5.017 c-0.528-0.237-1.218,0.151-1.218,0.788v32.214c0,0.34,0.2,0.648,0.509,0.788l11.519,5.176c0.27,0.119,0.477,0.103,0.708,0 l11.164-5.018l11.165,5.018c0.255,0.112,0.461,0.109,0.709,0l11.519-5.176c0.31-0.14,0.511-0.448,0.511-0.788V44.799 C74.536,43.644,72.808,43.644,72.808,44.799z M38.251,70.849l-9.791-4.4v-30.32l9.791,4.4V70.849z M49.77,66.448l-9.791,4.4v-30.32 l9.791-4.399V66.448z"/> <path fill="#FFFFFF" d="M65.004,23.447c-5.87,0-10.646,4.777-10.646,10.648c0,1.191,0.195,2.361,0.58,3.478 c1.736,5.004,4.724,9.529,8.621,13.068c0.804,0.798,2.097,0.793,2.872,0.018c3.918-3.556,6.904-8.082,8.643-13.087 c0.385-1.115,0.58-2.284,0.58-3.477C75.653,28.224,70.876,23.447,65.004,23.447z M65.004,40.463c-3.532,0-6.406-2.874-6.406-6.405 s2.874-6.404,6.406-6.404s6.404,2.873,6.404,6.404S68.536,40.463,65.004,40.463z"/>', 'map-2', '[{"datad":"M72.808,44.799v21.649l-9.79,4.4V51.538c0-1.137-1.729-1.137-1.729,0v19.311l-9.791-4.401V36.128 l0.954,0.429c1.046,0.469,1.75-1.107,0.708-1.576l-2.172-0.976c-0.229-0.105-0.474-0.103-0.708,0l-11.164,5.017l-11.165-5.017 c-0.528-0.237-1.218,0.151-1.218,0.788v32.214c0,0.34,0.2,0.648,0.509,0.788l11.519,5.176c0.27,0.119,0.477,0.103,0.708,0 l11.164-5.018l11.165,5.018c0.255,0.112,0.461,0.109,0.709,0l11.519-5.176c0.31-0.14,0.511-0.448,0.511-0.788V44.799 C74.536,43.644,72.808,43.644,72.808,44.799z M38.251,70.849l-9.791-4.4v-30.32l9.791,4.4V70.849z M49.77,66.448l-9.791,4.4v-30.32 l9.791-4.399V66.448z"},{"datad":"M65.004,23.447c-5.87,0-10.646,4.777-10.646,10.648c0,1.191,0.195,2.361,0.58,3.478 c1.736,5.004,4.724,9.529,8.621,13.068c0.804,0.798,2.097,0.793,2.872,0.018c3.918-3.556,6.904-8.082,8.643-13.087 c0.385-1.115,0.58-2.284,0.58-3.477C75.653,28.224,70.876,23.447,65.004,23.447z M65.004,40.463c-3.532,0-6.406-2.874-6.406-6.405 s2.874-6.404,6.406-6.404s6.404,2.873,6.404,6.404S68.536,40.463,65.004,40.463z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (189, 'Sports', '<rect x="-59.3" y="49.005" fill="#010101" width="0.244" height="0.221"/> <polygon fill="#010101" points="-53.738,52.108 -53.746,52.115 -53.746,52.108 "/> <polygon fill="#010101" points="-53.738,52.108 -53.746,52.115 -53.746,52.108 "/> <path fill="#FFFFFF" d="M30.22,30.306c-5.224,5.291-8.104,12.257-8.104,19.691s2.88,14.4,8.171,19.691 c5.425,5.426,12.525,8.104,19.691,8.104c7.167,0,14.266-2.679,19.69-8.104c5.225-5.225,8.171-12.257,8.171-19.691 s-2.879-14.4-8.171-19.691C58.753,19.456,41.07,19.456,30.22,30.306z M58.149,38.879c-2.88-3.483-4.42-7.904-4.487-12.391 c4.152,0.67,8.171,2.411,11.587,5.292L58.149,38.879z M55.337,41.759l-5.425,5.425L34.574,31.846 c4.354-3.684,9.711-5.559,15.07-5.626C49.644,31.913,51.653,37.405,55.337,41.759z M47.099,49.997l-5.425,5.425 c-4.354-3.684-9.846-5.693-15.539-5.626c0.067-5.559,2.009-10.85,5.626-15.069L47.099,49.997z M41.673,61.114 c2.88,3.483,4.42,7.838,4.487,12.392c-4.152-0.671-8.171-2.411-11.587-5.292L41.673,61.114z M44.486,58.234l5.425-5.425 l15.337,15.338c-4.354,3.684-9.711,5.56-15.07,5.626C50.179,68.08,48.17,62.589,44.486,58.234z M52.724,49.997l5.358-5.358 c4.354,3.684,9.778,5.626,15.405,5.626c0.067,0,0.067,0,0.134,0c-0.066,5.559-2.01,10.85-5.626,15.07L52.724,49.997z M61.029,41.759l7.1-7.1c2.813,3.349,4.621,7.3,5.291,11.587C68.866,46.179,64.512,44.639,61.029,41.759z M26.402,53.748 c4.555,0.066,8.908,1.607,12.391,4.486l-7.033,7.101C28.881,61.986,27.073,58.033,26.402,53.748z"/>', 'sports-2', '[{"datad":"M30.22,30.306c-5.224,5.291-8.104,12.257-8.104,19.691s2.88,14.4,8.171,19.691 c5.425,5.426,12.525,8.104,19.691,8.104c7.167,0,14.266-2.679,19.69-8.104c5.225-5.225,8.171-12.257,8.171-19.691 s-2.879-14.4-8.171-19.691C58.753,19.456,41.07,19.456,30.22,30.306z M58.149,38.879c-2.88-3.483-4.42-7.904-4.487-12.391 c4.152,0.67,8.171,2.411,11.587,5.292L58.149,38.879z M55.337,41.759l-5.425,5.425L34.574,31.846 c4.354-3.684,9.711-5.559,15.07-5.626C49.644,31.913,51.653,37.405,55.337,41.759z M47.099,49.997l-5.425,5.425 c-4.354-3.684-9.846-5.693-15.539-5.626c0.067-5.559,2.009-10.85,5.626-15.069L47.099,49.997z M41.673,61.114 c2.88,3.483,4.42,7.838,4.487,12.392c-4.152-0.671-8.171-2.411-11.587-5.292L41.673,61.114z M44.486,58.234l5.425-5.425 l15.337,15.338c-4.354,3.684-9.711,5.56-15.07,5.626C50.179,68.08,48.17,62.589,44.486,58.234z M52.724,49.997l5.358-5.358 c4.354,3.684,9.778,5.626,15.405,5.626c0.067,0,0.067,0,0.134,0c-0.066,5.559-2.01,10.85-5.626,15.07L52.724,49.997z M61.029,41.759l7.1-7.1c2.813,3.349,4.621,7.3,5.291,11.587C68.866,46.179,64.512,44.639,61.029,41.759z M26.402,53.748 c4.555,0.066,8.908,1.607,12.391,4.486l-7.033,7.101C28.881,61.986,27.073,58.033,26.402,53.748z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (188, 'Animals, wildlife', '<rect x="-59.3" y="49.005" fill="#010101" width="0.244" height="0.221"/> <polygon fill="#010101" points="-53.738,52.108 -53.746,52.115 -53.746,52.108 "/> <polygon fill="#010101" points="-53.738,52.108 -53.746,52.115 -53.746,52.108 "/> <path fill="#FFFFFF" d="M74.582,56.065c0.087-0.462-0.161-2.708-3.452-13.234c-3.377-10.796-12.037-11.437-12.037-11.437 s-2.749-0.73-5.543,3.001c-4.023,5.153-2.964,11.648,2.172,15.85c5.136,4.2,11.066,1.375,13.502,0 c1.836,1.093-5.931,4.129-4.519,4.587c1.412,0.46,1.659,2.754,1.659,2.754s0.175,2.321-0.165,3.971 c-1.015,4.916-3.508,2.698-3.508,2.698s-7.815-2.988-0.147,2.515c7.667,5.502,12.052-10.706,12.052-10.706L74.582,56.065z M60.032,44.548c-0.895,0-1.621-0.726-1.621-1.622c0-0.896,0.727-1.621,1.621-1.621c0.897,0,1.623,0.726,1.623,1.621 C61.655,43.822,60.93,44.548,60.032,44.548z"/> <path fill="#FFFFFF" d="M61.452,55.444c-0.032-0.008-0.059-0.016-0.091-0.026c-0.66-0.226-1.66-0.298-2.328-0.477 c-0.022-0.007-0.042-0.012-0.065-0.016c-2.649-0.558-5.017-1.88-6.861-3.725c-2.483-2.483-4.019-5.912-4.019-9.7 c0-3.314,1.177-6.355,3.134-8.728c0.313-0.382,0.376-0.879,0.163-1.326c-0.212-0.446-0.639-0.711-1.133-0.708 c-25.858,0.243-28.62,27.335-21.387,38.935c0.234,0.374,0.613,0.586,1.055,0.586h5.291c0.684,0,1.241-0.56,1.241-1.243 c0-1.966-0.617-6.535,2.25-6.535h5.519c2.906,0,2.203,4.482,2.203,6.535c0,0.684,0.559,1.243,1.241,1.243h4.407 c0.387,0,0.721-0.159,0.965-0.46c1.664-2.055,1.377-4.983,1.916-7.401c0.781-3.508,4.047-6.248,6.813-6.718 C61.69,55.564,61.592,55.48,61.452,55.444z"/>', 'animals-2', '[{"datad":"M74.582,56.065c0.087-0.462-0.161-2.708-3.452-13.234c-3.377-10.796-12.037-11.437-12.037-11.437 s-2.749-0.73-5.543,3.001c-4.023,5.153-2.964,11.648,2.172,15.85c5.136,4.2,11.066,1.375,13.502,0 c1.836,1.093-5.931,4.129-4.519,4.587c1.412,0.46,1.659,2.754,1.659,2.754s0.175,2.321-0.165,3.971 c-1.015,4.916-3.508,2.698-3.508,2.698s-7.815-2.988-0.147,2.515c7.667,5.502,12.052-10.706,12.052-10.706L74.582,56.065z M60.032,44.548c-0.895,0-1.621-0.726-1.621-1.622c0-0.896,0.727-1.621,1.621-1.621c0.897,0,1.623,0.726,1.623,1.621 C61.655,43.822,60.93,44.548,60.032,44.548z"},{"datad":"M61.452,55.444c-0.032-0.008-0.059-0.016-0.091-0.026c-0.66-0.226-1.66-0.298-2.328-0.477 c-0.022-0.007-0.042-0.012-0.065-0.016c-2.649-0.558-5.017-1.88-6.861-3.725c-2.483-2.483-4.019-5.912-4.019-9.7 c0-3.314,1.177-6.355,3.134-8.728c0.313-0.382,0.376-0.879,0.163-1.326c-0.212-0.446-0.639-0.711-1.133-0.708 c-25.858,0.243-28.62,27.335-21.387,38.935c0.234,0.374,0.613,0.586,1.055,0.586h5.291c0.684,0,1.241-0.56,1.241-1.243 c0-1.966-0.617-6.535,2.25-6.535h5.519c2.906,0,2.203,4.482,2.203,6.535c0,0.684,0.559,1.243,1.241,1.243h4.407 c0.387,0,0.721-0.159,0.965-0.46c1.664-2.055,1.377-4.983,1.916-7.401c0.781-3.508,4.047-6.248,6.813-6.718 C61.69,55.564,61.592,55.48,61.452,55.444z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (187, 'Tsunami', '<rect x="-59.3" y="49.005" fill="#010101" width="0.244" height="0.221"/> <polygon fill="#010101" points="-53.738,52.108 -53.746,52.115 -53.746,52.108 "/> <polygon fill="#010101" points="-53.738,52.108 -53.746,52.115 -53.746,52.108 "/> <path fill="#FFFFFF" d="M74.018,62.945c-1.054,0-2.107,0.425-3.223,0.875c-1.221,0.492-2.483,1.002-3.876,1.002 c-1.395,0-2.655-0.51-3.878-1.002c-1.114-0.45-2.168-0.875-3.221-0.875c-1.056,0-2.107,0.425-3.223,0.875 c-1.222,0.492-2.483,1.002-3.878,1.002c-1.392,0-2.656-0.51-3.876-1.002c-1.115-0.45-2.168-0.875-3.222-0.875 c-1.054,0-2.108,0.425-3.223,0.875c-1.221,0.492-2.483,1.002-3.877,1.002c-1.393,0-2.656-0.51-3.876-1.002 c-1.115-0.45-2.169-0.875-3.223-0.875c-1.455,0-2.93,0.542-4.494,1.115c-0.52,0.191-1.052,0.377-1.587,0.551 c0.351,0.508,0.718,1.005,1.098,1.49c1.584-0.566,3.223-1.142,4.983-1.142c1.394,0,2.656,0.509,3.877,1.002 c1.116,0.451,2.169,0.875,3.222,0.875c1.054,0,2.107-0.424,3.223-0.875c1.22-0.493,2.483-1.002,3.876-1.002 c1.394,0,2.656,0.509,3.877,1.002c1.115,0.451,2.168,0.875,3.222,0.875s2.107-0.424,3.223-0.875 c1.22-0.493,2.483-1.002,3.877-1.002s2.656,0.509,3.877,1.002c1.115,0.451,2.167,0.875,3.221,0.875s2.107-0.424,3.223-0.875 c1.221-0.493,2.483-1.002,3.877-1.002c0.313,0,0.619,0.022,0.924,0.056c0.411-0.576,0.809-1.164,1.179-1.771 C75.418,63.063,74.722,62.945,74.018,62.945z"/> <path fill="#FFFFFF" d="M66.919,68.714c-1.395,0-2.655-0.51-3.878-1.002c-1.114-0.45-2.168-0.875-3.221-0.875 c-1.056,0-2.107,0.425-3.223,0.875c-1.222,0.492-2.483,1.002-3.878,1.002c-1.392,0-2.656-0.51-3.876-1.002 c-1.115-0.45-2.168-0.875-3.222-0.875c-1.054,0-2.108,0.425-3.223,0.875c-1.221,0.492-2.483,1.002-3.877,1.002 c-1.393,0-2.656-0.51-3.876-1.002c-1.115-0.45-2.169-0.875-3.223-0.875c-1.201,0-2.417,0.371-3.684,0.822 c0.456,0.512,0.931,1.003,1.421,1.481c0.735-0.173,1.486-0.289,2.262-0.289c1.394,0,2.656,0.51,3.877,1.002 c1.116,0.451,2.169,0.875,3.222,0.875c1.054,0,2.107-0.424,3.223-0.875c1.22-0.492,2.483-1.002,3.876-1.002 c1.394,0,2.656,0.51,3.877,1.002c1.115,0.451,2.168,0.875,3.222,0.875s2.107-0.424,3.223-0.875c1.22-0.492,2.483-1.002,3.877-1.002 s2.656,0.51,3.877,1.002c1.115,0.451,2.167,0.875,3.221,0.875s2.107-0.424,3.223-0.875c0.294-0.118,0.592-0.237,0.893-0.352 c0.873-0.824,1.701-1.696,2.472-2.618c-0.884,0.107-1.774,0.451-2.709,0.828C69.574,68.204,68.312,68.714,66.919,68.714z"/> <path fill="#FFFFFF" d="M63.041,71.604c-1.114-0.45-2.168-0.875-3.221-0.875c-1.056,0-2.107,0.425-3.223,0.875 c-1.222,0.492-2.483,1.003-3.878,1.003c-1.392,0-2.656-0.511-3.876-1.003c-1.115-0.45-2.168-0.875-3.222-0.875 c-1.054,0-2.108,0.425-3.223,0.875c-1.221,0.492-2.483,1.003-3.877,1.003c-1.393,0-2.656-0.511-3.876-1.003 c-1.115-0.45-2.169-0.875-3.223-0.875c-0.161,0-0.322,0.009-0.483,0.021c1.316,1.096,2.73,2.077,4.221,2.94 c0.046,0.019,0.093,0.036,0.139,0.056c1.116,0.45,2.169,0.874,3.222,0.874c1.054,0,2.107-0.424,3.223-0.874 c1.22-0.493,2.483-1.003,3.876-1.003c1.394,0,2.656,0.51,3.877,1.003c1.115,0.45,2.168,0.874,3.222,0.874s2.107-0.424,3.223-0.874 c1.22-0.493,2.483-1.003,3.877-1.003s2.656,0.51,3.877,1.003c0.325,0.132,0.645,0.256,0.962,0.374 c0.866-0.47,1.707-0.978,2.52-1.525c-0.087,0.003-0.172,0.012-0.259,0.012C65.524,72.606,64.264,72.096,63.041,71.604z"/> <path fill="#FFFFFF" d="M56.598,75.495c-1.222,0.492-2.483,1.003-3.878,1.003c-1.392,0-2.656-0.511-3.876-1.003 c-1.115-0.45-2.168-0.875-3.222-0.875c-1.054,0-2.108,0.425-3.223,0.875c-0.584,0.235-1.179,0.475-1.792,0.66 c0.955,0.322,1.93,0.598,2.923,0.825c0.67-0.204,1.362-0.345,2.091-0.345c1.394,0,2.656,0.509,3.877,1.002 c0.08,0.033,0.159,0.063,0.238,0.094c0.184,0.004,0.365,0.015,0.548,0.015c4.297,0,8.383-0.902,12.084-2.517 c-0.87-0.337-1.711-0.609-2.549-0.609C58.765,74.62,57.713,75.045,56.598,75.495z"/> <path fill="#FFFFFF" d="M31.567,57.167c1.394,0,2.656,0.51,3.876,1.003c1.116,0.449,2.169,0.874,3.223,0.874 s2.107-0.425,3.223-0.874c1.22-0.493,2.483-1.003,3.876-1.003c1.394,0,2.657,0.51,3.877,1.003c1.115,0.449,2.168,0.874,3.222,0.874 s2.107-0.425,3.223-0.874c1.222-0.493,2.483-1.003,3.877-1.003c1.393,0,2.656,0.51,3.876,1.003 c1.115,0.449,2.169,0.874,3.223,0.874c1.053,0,2.108-0.425,3.223-0.874c0.863-0.349,1.75-0.703,2.688-0.883 c-10.754-1.023-25.132-4.463-25.132-14.103c0-4.957,3.082-6.551,5.926-5.789c0.771,0.207,2.653,1.482,1.46,3.55 c-0.858,1.487-3.032,1.234-2.941,1.701c0.07,0.36,0.341,0.658,0.697,0.909c6.482,4.563,16.173-4.892,6.389-12.954 c-11.489-9.469-33.697-2.155-35.058,17.306c-0.227,3.251-0.182,7.226-0.053,11.16c0.632-0.185,1.283-0.405,1.97-0.648 C27.974,57.803,29.777,57.167,31.567,57.167z"/> <path fill="#FFFFFF" d="M70.941,59.792c-1.222,0.492-2.485,1.003-3.878,1.003c-1.394,0-2.655-0.511-3.875-1.003 c-1.115-0.45-2.169-0.876-3.223-0.876c-1.055,0-2.108,0.426-3.224,0.876c-1.22,0.492-2.483,1.003-3.877,1.003 c-1.393,0-2.655-0.511-3.876-1.003c-1.115-0.45-2.168-0.876-3.222-0.876s-2.107,0.426-3.223,0.876 c-1.221,0.492-2.483,1.003-3.877,1.003c-1.393,0-2.657-0.511-3.877-1.003c-1.115-0.45-2.168-0.876-3.222-0.876 c-1.49,0-3.149,0.587-4.753,1.154c-0.822,0.289-1.672,0.589-2.487,0.804l-0.002-0.01c0.027,0.743,0.057,1.478,0.088,2.204 c0.621-0.188,1.25-0.407,1.911-0.65c1.638-0.601,3.331-1.223,5.097-1.223c1.394,0,2.656,0.509,3.877,1.002 c1.115,0.449,2.168,0.875,3.222,0.875s2.107-0.426,3.222-0.875c1.221-0.493,2.483-1.002,3.877-1.002 c1.393,0,2.656,0.509,3.877,1.002c1.115,0.449,2.169,0.875,3.222,0.875c1.055,0,2.107-0.426,3.222-0.875 c1.223-0.493,2.485-1.002,3.878-1.002c1.395,0,2.656,0.509,3.878,1.002c1.113,0.449,2.167,0.875,3.221,0.875 s2.107-0.426,3.223-0.875c1.221-0.493,2.483-1.002,3.876-1.002c1.036,0,2.03,0.201,2.996,0.481 c0.336-0.632,0.647-1.278,0.939-1.936c-1.287-0.449-2.524-0.825-3.787-0.825C73.109,58.916,72.057,59.342,70.941,59.792z"/>', 'tsunami-2', '[{"datad":"M74.018,62.945c-1.054,0-2.107,0.425-3.223,0.875c-1.221,0.492-2.483,1.002-3.876,1.002 c-1.395,0-2.655-0.51-3.878-1.002c-1.114-0.45-2.168-0.875-3.221-0.875c-1.056,0-2.107,0.425-3.223,0.875 c-1.222,0.492-2.483,1.002-3.878,1.002c-1.392,0-2.656-0.51-3.876-1.002c-1.115-0.45-2.168-0.875-3.222-0.875 c-1.054,0-2.108,0.425-3.223,0.875c-1.221,0.492-2.483,1.002-3.877,1.002c-1.393,0-2.656-0.51-3.876-1.002 c-1.115-0.45-2.169-0.875-3.223-0.875c-1.455,0-2.93,0.542-4.494,1.115c-0.52,0.191-1.052,0.377-1.587,0.551 c0.351,0.508,0.718,1.005,1.098,1.49c1.584-0.566,3.223-1.142,4.983-1.142c1.394,0,2.656,0.509,3.877,1.002 c1.116,0.451,2.169,0.875,3.222,0.875c1.054,0,2.107-0.424,3.223-0.875c1.22-0.493,2.483-1.002,3.876-1.002 c1.394,0,2.656,0.509,3.877,1.002c1.115,0.451,2.168,0.875,3.222,0.875s2.107-0.424,3.223-0.875 c1.22-0.493,2.483-1.002,3.877-1.002s2.656,0.509,3.877,1.002c1.115,0.451,2.167,0.875,3.221,0.875s2.107-0.424,3.223-0.875 c1.221-0.493,2.483-1.002,3.877-1.002c0.313,0,0.619,0.022,0.924,0.056c0.411-0.576,0.809-1.164,1.179-1.771 C75.418,63.063,74.722,62.945,74.018,62.945z"},{"datad":"M66.919,68.714c-1.395,0-2.655-0.51-3.878-1.002c-1.114-0.45-2.168-0.875-3.221-0.875 c-1.056,0-2.107,0.425-3.223,0.875c-1.222,0.492-2.483,1.002-3.878,1.002c-1.392,0-2.656-0.51-3.876-1.002 c-1.115-0.45-2.168-0.875-3.222-0.875c-1.054,0-2.108,0.425-3.223,0.875c-1.221,0.492-2.483,1.002-3.877,1.002 c-1.393,0-2.656-0.51-3.876-1.002c-1.115-0.45-2.169-0.875-3.223-0.875c-1.201,0-2.417,0.371-3.684,0.822 c0.456,0.512,0.931,1.003,1.421,1.481c0.735-0.173,1.486-0.289,2.262-0.289c1.394,0,2.656,0.51,3.877,1.002 c1.116,0.451,2.169,0.875,3.222,0.875c1.054,0,2.107-0.424,3.223-0.875c1.22-0.492,2.483-1.002,3.876-1.002 c1.394,0,2.656,0.51,3.877,1.002c1.115,0.451,2.168,0.875,3.222,0.875s2.107-0.424,3.223-0.875c1.22-0.492,2.483-1.002,3.877-1.002 s2.656,0.51,3.877,1.002c1.115,0.451,2.167,0.875,3.221,0.875s2.107-0.424,3.223-0.875c0.294-0.118,0.592-0.237,0.893-0.352 c0.873-0.824,1.701-1.696,2.472-2.618c-0.884,0.107-1.774,0.451-2.709,0.828C69.574,68.204,68.312,68.714,66.919,68.714z"},{"datad":"M63.041,71.604c-1.114-0.45-2.168-0.875-3.221-0.875c-1.056,0-2.107,0.425-3.223,0.875 c-1.222,0.492-2.483,1.003-3.878,1.003c-1.392,0-2.656-0.511-3.876-1.003c-1.115-0.45-2.168-0.875-3.222-0.875 c-1.054,0-2.108,0.425-3.223,0.875c-1.221,0.492-2.483,1.003-3.877,1.003c-1.393,0-2.656-0.511-3.876-1.003 c-1.115-0.45-2.169-0.875-3.223-0.875c-0.161,0-0.322,0.009-0.483,0.021c1.316,1.096,2.73,2.077,4.221,2.94 c0.046,0.019,0.093,0.036,0.139,0.056c1.116,0.45,2.169,0.874,3.222,0.874c1.054,0,2.107-0.424,3.223-0.874 c1.22-0.493,2.483-1.003,3.876-1.003c1.394,0,2.656,0.51,3.877,1.003c1.115,0.45,2.168,0.874,3.222,0.874s2.107-0.424,3.223-0.874 c1.22-0.493,2.483-1.003,3.877-1.003s2.656,0.51,3.877,1.003c0.325,0.132,0.645,0.256,0.962,0.374 c0.866-0.47,1.707-0.978,2.52-1.525c-0.087,0.003-0.172,0.012-0.259,0.012C65.524,72.606,64.264,72.096,63.041,71.604z"},{"datad":"M56.598,75.495c-1.222,0.492-2.483,1.003-3.878,1.003c-1.392,0-2.656-0.511-3.876-1.003 c-1.115-0.45-2.168-0.875-3.222-0.875c-1.054,0-2.108,0.425-3.223,0.875c-0.584,0.235-1.179,0.475-1.792,0.66 c0.955,0.322,1.93,0.598,2.923,0.825c0.67-0.204,1.362-0.345,2.091-0.345c1.394,0,2.656,0.509,3.877,1.002 c0.08,0.033,0.159,0.063,0.238,0.094c0.184,0.004,0.365,0.015,0.548,0.015c4.297,0,8.383-0.902,12.084-2.517 c-0.87-0.337-1.711-0.609-2.549-0.609C58.765,74.62,57.713,75.045,56.598,75.495z"},{"datad":"M31.567,57.167c1.394,0,2.656,0.51,3.876,1.003c1.116,0.449,2.169,0.874,3.223,0.874 s2.107-0.425,3.223-0.874c1.22-0.493,2.483-1.003,3.876-1.003c1.394,0,2.657,0.51,3.877,1.003c1.115,0.449,2.168,0.874,3.222,0.874 s2.107-0.425,3.223-0.874c1.222-0.493,2.483-1.003,3.877-1.003c1.393,0,2.656,0.51,3.876,1.003 c1.115,0.449,2.169,0.874,3.223,0.874c1.053,0,2.108-0.425,3.223-0.874c0.863-0.349,1.75-0.703,2.688-0.883 c-10.754-1.023-25.132-4.463-25.132-14.103c0-4.957,3.082-6.551,5.926-5.789c0.771,0.207,2.653,1.482,1.46,3.55 c-0.858,1.487-3.032,1.234-2.941,1.701c0.07,0.36,0.341,0.658,0.697,0.909c6.482,4.563,16.173-4.892,6.389-12.954 c-11.489-9.469-33.697-2.155-35.058,17.306c-0.227,3.251-0.182,7.226-0.053,11.16c0.632-0.185,1.283-0.405,1.97-0.648 C27.974,57.803,29.777,57.167,31.567,57.167z"},{"datad":"M70.941,59.792c-1.222,0.492-2.485,1.003-3.878,1.003c-1.394,0-2.655-0.511-3.875-1.003 c-1.115-0.45-2.169-0.876-3.223-0.876c-1.055,0-2.108,0.426-3.224,0.876c-1.22,0.492-2.483,1.003-3.877,1.003 c-1.393,0-2.655-0.511-3.876-1.003c-1.115-0.45-2.168-0.876-3.222-0.876s-2.107,0.426-3.223,0.876 c-1.221,0.492-2.483,1.003-3.877,1.003c-1.393,0-2.657-0.511-3.877-1.003c-1.115-0.45-2.168-0.876-3.222-0.876 c-1.49,0-3.149,0.587-4.753,1.154c-0.822,0.289-1.672,0.589-2.487,0.804l-0.002-0.01c0.027,0.743,0.057,1.478,0.088,2.204 c0.621-0.188,1.25-0.407,1.911-0.65c1.638-0.601,3.331-1.223,5.097-1.223c1.394,0,2.656,0.509,3.877,1.002 c1.115,0.449,2.168,0.875,3.222,0.875s2.107-0.426,3.222-0.875c1.221-0.493,2.483-1.002,3.877-1.002 c1.393,0,2.656,0.509,3.877,1.002c1.115,0.449,2.169,0.875,3.222,0.875c1.055,0,2.107-0.426,3.222-0.875 c1.223-0.493,2.485-1.002,3.878-1.002c1.395,0,2.656,0.509,3.878,1.002c1.113,0.449,2.167,0.875,3.221,0.875 s2.107-0.426,3.223-0.875c1.221-0.493,2.483-1.002,3.876-1.002c1.036,0,2.03,0.201,2.996,0.481 c0.336-0.632,0.647-1.278,0.939-1.936c-1.287-0.449-2.524-0.825-3.787-0.825C73.109,58.916,72.057,59.342,70.941,59.792z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (185, 'Earthquake', '<polygon fill="#010101" points="50.201,44.588 50.193,44.595 50.193,44.588 "/> <polygon fill="#010101" points="50.201,44.588 50.193,44.595 50.193,44.588 "/> <path fill="#FFFFFF" d="M47.504,70.496l-10.995,2.391l-3.251-3.729c-0.479-0.573-1.338-0.669-2.008-0.191l-7.936,5.642 c-0.669,0.574-0.86,1.529-0.382,2.199c0.286,0.479,0.765,0.67,1.243,0.67c0.287,0,0.669-0.096,0.765-0.384l6.884-4.875l2.964,3.347 c0.382,0.383,0.956,0.572,1.53,0.478l11.855-2.581c0.765-0.189,1.338-0.956,1.147-1.816C49.13,70.878,48.365,70.305,47.504,70.496z "/> <path fill="#FFFFFF" d="M76.953,74.605l-10.614-5.832c-0.573-0.287-1.146-0.19-1.624,0.097l-5.738,3.92l-4.875-2.199 c-0.765-0.382-1.625,0-2.008,0.765c-0.383,0.766,0,1.627,0.766,2.009l5.641,2.581c0.479,0.285,1.053,0.191,1.531-0.096l5.639-3.824 l9.753,5.26c0.287,0.096,0.479,0.191,0.767,0.191c0.574,0,1.053-0.287,1.434-0.768C78.004,75.945,77.717,74.989,76.953,74.605z"/> <path fill="#FFFFFF" d="M49.894,50.131c-0.668-0.479-1.625-0.289-2.103,0.477l-3.06,5.068c-0.382,0.574-0.287,1.338,0.191,1.816 l2.199,2.39l-0.686,3.739l-8.684,1.52L35.266,50.8l26.198-4.589l2.485,14.247l-8.986,1.528c-0.861,0.189-1.438,0.957-1.245,1.816 c0.192,0.86,0.958,1.436,1.819,1.243l10.516-1.817c0.383-0.095,0.766-0.285,0.956-0.668c0.288-0.287,0.384-0.767,0.288-1.147 l-3.061-17.305c-0.084-0.375-0.279-0.691-0.543-0.917c-0.143-0.25-0.33-0.475-0.605-0.612l-16.54-8.893 c-0.669-0.382-1.434-0.287-1.912,0.287L32.207,47.835c-0.402,0.469-0.503,1.074-0.31,1.594c-0.012,0.142-0.01,0.283,0.023,0.413 l3.059,17.307c0.191,0.766,0.765,1.242,1.53,1.242c0.096,0,0.191,0,0.286,0.096L47.6,66.574c0.222-0.051,0.423-0.139,0.6-0.254 c0.577-0.141,1.026-0.557,1.026-1.18l0.956-5.451c0.096-0.478-0.096-0.953-0.383-1.336l-1.912-2.104l2.486-4.019 C50.852,51.564,50.659,50.607,49.894,50.131z M46.166,37.031l12.193,6.549l-21.548,3.802L46.166,37.031z"/>', 'earthquake-2', '[{"datad":"M47.504,70.496l-10.995,2.391l-3.251-3.729c-0.479-0.573-1.338-0.669-2.008-0.191l-7.936,5.642 c-0.669,0.574-0.86,1.529-0.382,2.199c0.286,0.479,0.765,0.67,1.243,0.67c0.287,0,0.669-0.096,0.765-0.384l6.884-4.875l2.964,3.347 c0.382,0.383,0.956,0.572,1.53,0.478l11.855-2.581c0.765-0.189,1.338-0.956,1.147-1.816C49.13,70.878,48.365,70.305,47.504,70.496z "},{"datad":"M76.953,74.605l-10.614-5.832c-0.573-0.287-1.146-0.19-1.624,0.097l-5.738,3.92l-4.875-2.199 c-0.765-0.382-1.625,0-2.008,0.765c-0.383,0.766,0,1.627,0.766,2.009l5.641,2.581c0.479,0.285,1.053,0.191,1.531-0.096l5.639-3.824 l9.753,5.26c0.287,0.096,0.479,0.191,0.767,0.191c0.574,0,1.053-0.287,1.434-0.768C78.004,75.945,77.717,74.989,76.953,74.605z"},{"datad":"M49.894,50.131c-0.668-0.479-1.625-0.289-2.103,0.477l-3.06,5.068c-0.382,0.574-0.287,1.338,0.191,1.816 l2.199,2.39l-0.686,3.739l-8.684,1.52L35.266,50.8l26.198-4.589l2.485,14.247l-8.986,1.528c-0.861,0.189-1.438,0.957-1.245,1.816 c0.192,0.86,0.958,1.436,1.819,1.243l10.516-1.817c0.383-0.095,0.766-0.285,0.956-0.668c0.288-0.287,0.384-0.767,0.288-1.147 l-3.061-17.305c-0.084-0.375-0.279-0.691-0.543-0.917c-0.143-0.25-0.33-0.475-0.605-0.612l-16.54-8.893 c-0.669-0.382-1.434-0.287-1.912,0.287L32.207,47.835c-0.402,0.469-0.503,1.074-0.31,1.594c-0.012,0.142-0.01,0.283,0.023,0.413 l3.059,17.307c0.191,0.766,0.765,1.242,1.53,1.242c0.096,0,0.191,0,0.286,0.096L47.6,66.574c0.222-0.051,0.423-0.139,0.6-0.254 c0.577-0.141,1.026-0.557,1.026-1.18l0.956-5.451c0.096-0.478-0.096-0.953-0.383-1.336l-1.912-2.104l2.486-4.019 C50.852,51.564,50.659,50.607,49.894,50.131z M46.166,37.031l12.193,6.549l-21.548,3.802L46.166,37.031z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (184, 'Drugs', '<path fill="#FFFFFF" d="M54.012,56.106c6.18-0.113,17.331-1.334,26.034-7.524c-8.492-2.285-20.08,1.496-26.15,4.38 c-0.017-0.035-0.047-0.059-0.062-0.089c6.08-4.239,16.998-12.948,20.362-26.479C61.842,31.873,55.011,44,51.949,50.959 c-0.061-0.037-0.106-0.082-0.163-0.111c2.138-8.721,4.542-25.446-1.78-38.38c-6.335,12.934-3.927,29.66-1.782,38.38 c-0.068,0.027-0.112,0.072-0.168,0.111c-3.066-6.958-9.897-19.086-22.24-24.564c3.359,13.532,14.271,22.24,20.357,26.479 c-0.018,0.03-0.046,0.054-0.068,0.089c-6.069-2.885-17.652-6.663-26.151-4.38c8.702,6.188,19.851,7.411,26.038,7.524 c0.022,0.059,0.012,0.135,0.033,0.195c-5.229,0.636-13.672,2.354-17.82,7.086c7.011,1.253,14.44-2.669,18.526-5.261 c-2.291,2.104-5.406,8.926-5.406,13.052c4.002-1.42,6.256-9.146,7.467-12.411c0.036,0.013,0.075,0.013,0.108,0.028l-0.542,23.708 h3.287l-0.541-23.708c0.032-0.017,0.073-0.017,0.106-0.028c1.206,3.265,3.465,10.989,7.461,12.411 c0-4.126-3.108-10.947-5.398-13.052c4.082,2.59,11.519,6.514,18.522,5.261c-4.142-4.732-12.591-6.45-17.819-7.086 C53.992,56.241,53.992,56.163,54.012,56.106z"/>', 'drugs-2', '[{"datad":"M54.012,56.106c6.18-0.113,17.331-1.334,26.034-7.524c-8.492-2.285-20.08,1.496-26.15,4.38 c-0.017-0.035-0.047-0.059-0.062-0.089c6.08-4.239,16.998-12.948,20.362-26.479C61.842,31.873,55.011,44,51.949,50.959 c-0.061-0.037-0.106-0.082-0.163-0.111c2.138-8.721,4.542-25.446-1.78-38.38c-6.335,12.934-3.927,29.66-1.782,38.38 c-0.068,0.027-0.112,0.072-0.168,0.111c-3.066-6.958-9.897-19.086-22.24-24.564c3.359,13.532,14.271,22.24,20.357,26.479 c-0.018,0.03-0.046,0.054-0.068,0.089c-6.069-2.885-17.652-6.663-26.151-4.38c8.702,6.188,19.851,7.411,26.038,7.524 c0.022,0.059,0.012,0.135,0.033,0.195c-5.229,0.636-13.672,2.354-17.82,7.086c7.011,1.253,14.44-2.669,18.526-5.261 c-2.291,2.104-5.406,8.926-5.406,13.052c4.002-1.42,6.256-9.146,7.467-12.411c0.036,0.013,0.075,0.013,0.108,0.028l-0.542,23.708 h3.287l-0.541-23.708c0.032-0.017,0.073-0.017,0.106-0.028c1.206,3.265,3.465,10.989,7.461,12.411 c0-4.126-3.108-10.947-5.398-13.052c4.082,2.59,11.519,6.514,18.522,5.261c-4.142-4.732-12.591-6.45-17.819-7.086 C53.992,56.241,53.992,56.163,54.012,56.106z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (183, 'Mobile, applications', '<polygon fill="#010101" points="50.201,44.588 50.193,44.595 50.193,44.588 "/> <polygon fill="#010101" points="50.201,44.588 50.193,44.595 50.193,44.588 "/> <path fill="#FFFFFF" d="M58.246,23.923H42.16c-5.083,0-9.238,4.145-9.238,9.241v31.676c0,5.09,4.155,9.235,9.238,9.235h16.085 c5.096,0,9.235-4.146,9.235-9.235V33.165C67.48,28.068,63.342,23.923,58.246,23.923z M63.785,64.841c0,3.056-2.484,5.54-5.539,5.54 H42.16c-3.059,0-5.54-2.484-5.54-5.54v-5.179h27.166V64.841z M63.785,57.811H36.62V35.471h27.166V57.811z M63.785,33.627H36.62 v-0.462c0-3.058,2.481-5.543,5.54-5.543h16.085c3.055,0,5.54,2.484,5.54,5.543V33.627z"/> <path fill="#FFFFFF" d="M50.089,68.999c2.129,0,3.858-1.736,3.858-3.859c0-2.131-1.729-3.861-3.858-3.861 c-2.13,0-3.857,1.73-3.857,3.861C46.232,67.263,47.96,68.999,50.089,68.999z M50.089,63.123c1.109,0,2.012,0.906,2.012,2.016 c0,1.108-0.903,2.01-2.012,2.01c-1.109,0-2.01-0.901-2.01-2.01C48.08,64.029,48.98,63.123,50.089,63.123z"/>', 'mobile-2', '[{"datad":"M58.246,23.923H42.16c-5.083,0-9.238,4.145-9.238,9.241v31.676c0,5.09,4.155,9.235,9.238,9.235h16.085 c5.096,0,9.235-4.146,9.235-9.235V33.165C67.48,28.068,63.342,23.923,58.246,23.923z M63.785,64.841c0,3.056-2.484,5.54-5.539,5.54 H42.16c-3.059,0-5.54-2.484-5.54-5.54v-5.179h27.166V64.841z M63.785,57.811H36.62V35.471h27.166V57.811z M63.785,33.627H36.62 v-0.462c0-3.058,2.481-5.543,5.54-5.543h16.085c3.055,0,5.54,2.484,5.54,5.543V33.627z"},{"datad":"M50.089,68.999c2.129,0,3.858-1.736,3.858-3.859c0-2.131-1.729-3.861-3.858-3.861 c-2.13,0-3.857,1.73-3.857,3.861C46.232,67.263,47.96,68.999,50.089,68.999z M50.089,63.123c1.109,0,2.012,0.906,2.012,2.016 c0,1.108-0.903,2.01-2.012,2.01c-1.109,0-2.01-0.901-2.01-2.01C48.08,64.029,48.98,63.123,50.089,63.123z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (182, 'Natural resource', '<path fill="#FFFFFF" d="M70.172,49.875c0-1.527-1.392-2.803-3.41-3.332V35.317c2.019-0.529,3.41-1.804,3.41-3.332 c0-2.015-2.421-3.594-5.513-3.594H35.341c-3.091,0-5.513,1.578-5.513,3.594c0,1.527,1.393,2.803,3.412,3.332v11.226 c-2.02,0.528-3.412,1.804-3.412,3.332c0,1.527,1.393,2.804,3.412,3.331v11.225c-2.02,0.529-3.412,1.805-3.412,3.333 c0,2.017,2.421,3.596,5.513,3.596h11.041c0.794,1.127,2.098,1.871,3.58,1.871c1.48,0,2.785-0.744,3.579-1.871h11.119 c3.092,0,5.513-1.579,5.513-3.596c0-1.527-1.392-2.803-3.41-3.333V53.206C68.78,52.678,70.172,51.402,70.172,49.875z M50.868,61.257 c-4.491,0-8.145-3.654-8.145-8.144c0-4.187,6.634-12.415,7.391-13.34c0.185-0.226,0.461-0.356,0.754-0.356 c0.292,0,0.568,0.131,0.753,0.356c0.756,0.925,7.391,9.153,7.391,13.34C59.012,57.603,55.357,61.257,50.868,61.257z"/>', 'natural_resource-2', '[{"datad":"M70.172,49.875c0-1.527-1.392-2.803-3.41-3.332V35.317c2.019-0.529,3.41-1.804,3.41-3.332 c0-2.015-2.421-3.594-5.513-3.594H35.341c-3.091,0-5.513,1.578-5.513,3.594c0,1.527,1.393,2.803,3.412,3.332v11.226 c-2.02,0.528-3.412,1.804-3.412,3.332c0,1.527,1.393,2.804,3.412,3.331v11.225c-2.02,0.529-3.412,1.805-3.412,3.333 c0,2.017,2.421,3.596,5.513,3.596h11.041c0.794,1.127,2.098,1.871,3.58,1.871c1.48,0,2.785-0.744,3.579-1.871h11.119 c3.092,0,5.513-1.579,5.513-3.596c0-1.527-1.392-2.803-3.41-3.333V53.206C68.78,52.678,70.172,51.402,70.172,49.875z M50.868,61.257 c-4.491,0-8.145-3.654-8.145-8.144c0-4.187,6.634-12.415,7.391-13.34c0.185-0.226,0.461-0.356,0.754-0.356 c0.292,0,0.568,0.131,0.753,0.356c0.756,0.925,7.391,9.153,7.391,13.34C59.012,57.603,55.357,61.257,50.868,61.257z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (181, 'Rescue operation', '<path fill="#FFFFFF" d="M49.979,21.125c-15.856,0-28.75,12.899-28.75,28.75c0,15.856,12.894,28.75,28.75,28.75 c15.852,0,28.75-12.894,28.75-28.75C78.729,34.024,65.83,21.125,49.979,21.125z M49.979,25.094c6.295,0,12.051,2.361,16.427,6.247 l-6.117,6.122c-0.189,0.193-0.318,0.421-0.383,0.669c-2.684-2.272-6.146-3.642-9.927-3.642s-7.243,1.369-9.927,3.642 c-0.064-0.248-0.193-0.477-0.387-0.669l-6.117-6.122C37.923,27.456,43.683,25.094,49.979,25.094z M49.979,61.787 c-6.568,0-11.912-5.343-11.912-11.912s5.344-11.912,11.912-11.912s11.912,5.343,11.912,11.912S56.547,61.787,49.979,61.787z M25.197,49.875c0-6.295,2.361-12.051,6.246-16.426l6.118,6.117c0.194,0.193,0.427,0.323,0.675,0.382 c-2.272,2.684-3.642,6.147-3.642,9.927s1.369,7.243,3.642,9.927c-0.248,0.064-0.481,0.193-0.675,0.388l-6.118,6.117 C27.56,61.931,25.197,56.17,25.197,49.875z M49.979,74.656c-6.295,0-12.056-2.361-16.432-6.247l6.117-6.117 c0.194-0.193,0.328-0.427,0.387-0.674c2.684,2.272,6.147,3.642,9.927,3.642s7.243-1.369,9.927-3.642 c0.06,0.247,0.188,0.48,0.383,0.674l6.122,6.117C62.034,72.294,56.273,74.656,49.979,74.656z M68.514,66.307l-6.122-6.122 c-0.188-0.193-0.422-0.322-0.665-0.387c2.268-2.68,3.637-6.147,3.637-9.923c0-3.78-1.369-7.243-3.642-9.927 c0.247-0.059,0.48-0.188,0.669-0.382l6.122-6.117c3.885,4.375,6.246,10.131,6.246,16.426C74.76,56.17,72.397,61.931,68.514,66.307z "/>', 'rescue-2', '[{"datad":"M49.979,21.125c-15.856,0-28.75,12.899-28.75,28.75c0,15.856,12.894,28.75,28.75,28.75 c15.852,0,28.75-12.894,28.75-28.75C78.729,34.024,65.83,21.125,49.979,21.125z M49.979,25.094c6.295,0,12.051,2.361,16.427,6.247 l-6.117,6.122c-0.189,0.193-0.318,0.421-0.383,0.669c-2.684-2.272-6.146-3.642-9.927-3.642s-7.243,1.369-9.927,3.642 c-0.064-0.248-0.193-0.477-0.387-0.669l-6.117-6.122C37.923,27.456,43.683,25.094,49.979,25.094z M49.979,61.787 c-6.568,0-11.912-5.343-11.912-11.912s5.344-11.912,11.912-11.912s11.912,5.343,11.912,11.912S56.547,61.787,49.979,61.787z M25.197,49.875c0-6.295,2.361-12.051,6.246-16.426l6.118,6.117c0.194,0.193,0.427,0.323,0.675,0.382 c-2.272,2.684-3.642,6.147-3.642,9.927s1.369,7.243,3.642,9.927c-0.248,0.064-0.481,0.193-0.675,0.388l-6.118,6.117 C27.56,61.931,25.197,56.17,25.197,49.875z M49.979,74.656c-6.295,0-12.056-2.361-16.432-6.247l6.117-6.117 c0.194-0.193,0.328-0.427,0.387-0.674c2.684,2.272,6.147,3.642,9.927,3.642s7.243-1.369,9.927-3.642 c0.06,0.247,0.188,0.48,0.383,0.674l6.122,6.117C62.034,72.294,56.273,74.656,49.979,74.656z M68.514,66.307l-6.122-6.122 c-0.188-0.193-0.422-0.322-0.665-0.387c2.268-2.68,3.637-6.147,3.637-9.923c0-3.78-1.369-7.243-3.642-9.927 c0.247-0.059,0.48-0.188,0.669-0.382l6.122-6.117c3.885,4.375,6.246,10.131,6.246,16.426C74.76,56.17,72.397,61.931,68.514,66.307z "}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (179, 'Stocks', '<path fill="#FFFFFF" d="M25.407,73.686l6.488-12.41l3.648,3.271c0.811,0.729,2.045,0.729,2.856,0l10.299-9.234l10.16,2.098 c0.952,0.199,1.921-0.273,2.353-1.146l12.039-24.302l1.258,2.929c0.349,0.811,1.137,1.296,1.969,1.296 c0.281,0,0.568-0.056,0.843-0.174c1.088-0.467,1.59-1.726,1.122-2.812l-3.086-7.185c-0.014-0.034-0.035-0.062-0.053-0.094 c-0.033-0.068-0.067-0.135-0.107-0.199c-0.035-0.056-0.072-0.107-0.111-0.159c-0.041-0.054-0.084-0.107-0.132-0.159 c-0.05-0.053-0.103-0.103-0.157-0.149c-0.045-0.041-0.091-0.081-0.141-0.119c-0.068-0.05-0.145-0.093-0.219-0.135 c-0.032-0.02-0.061-0.044-0.098-0.062c-0.012-0.005-0.023-0.008-0.037-0.014c-0.084-0.041-0.172-0.07-0.263-0.099 c-0.043-0.014-0.085-0.032-0.128-0.043c-0.092-0.024-0.188-0.035-0.283-0.045c-0.039-0.005-0.078-0.015-0.119-0.018 c-0.097-0.004-0.191,0.003-0.289,0.011c-0.039,0.003-0.08,0-0.119,0.005c-0.061,0.009-0.119,0.028-0.182,0.042 c-0.074,0.017-0.148,0.029-0.222,0.055c-0.003,0-0.004,0-0.007,0.001l-8.018,2.766c-1.117,0.386-1.711,1.604-1.326,2.723 c0.386,1.117,1.603,1.708,2.724,1.326l3.083-1.063L58.107,52.883l-9.599-1.98c-0.674-0.143-1.358,0.049-1.863,0.502l-9.674,8.674 l-4.256-3.818c-0.488-0.438-1.154-0.625-1.802-0.512c-0.649,0.113-1.208,0.52-1.517,1.102l-8.11,15.264"/>', 'stocks-2', '[{"datad":"M25.407,73.686l6.488-12.41l3.648,3.271c0.811,0.729,2.045,0.729,2.856,0l10.299-9.234l10.16,2.098 c0.952,0.199,1.921-0.273,2.353-1.146l12.039-24.302l1.258,2.929c0.349,0.811,1.137,1.296,1.969,1.296 c0.281,0,0.568-0.056,0.843-0.174c1.088-0.467,1.59-1.726,1.122-2.812l-3.086-7.185c-0.014-0.034-0.035-0.062-0.053-0.094 c-0.033-0.068-0.067-0.135-0.107-0.199c-0.035-0.056-0.072-0.107-0.111-0.159c-0.041-0.054-0.084-0.107-0.132-0.159 c-0.05-0.053-0.103-0.103-0.157-0.149c-0.045-0.041-0.091-0.081-0.141-0.119c-0.068-0.05-0.145-0.093-0.219-0.135 c-0.032-0.02-0.061-0.044-0.098-0.062c-0.012-0.005-0.023-0.008-0.037-0.014c-0.084-0.041-0.172-0.07-0.263-0.099 c-0.043-0.014-0.085-0.032-0.128-0.043c-0.092-0.024-0.188-0.035-0.283-0.045c-0.039-0.005-0.078-0.015-0.119-0.018 c-0.097-0.004-0.191,0.003-0.289,0.011c-0.039,0.003-0.08,0-0.119,0.005c-0.061,0.009-0.119,0.028-0.182,0.042 c-0.074,0.017-0.148,0.029-0.222,0.055c-0.003,0-0.004,0-0.007,0.001l-8.018,2.766c-1.117,0.386-1.711,1.604-1.326,2.723 c0.386,1.117,1.603,1.708,2.724,1.326l3.083-1.063L58.107,52.883l-9.599-1.98c-0.674-0.143-1.358,0.049-1.863,0.502l-9.674,8.674 l-4.256-3.818c-0.488-0.438-1.154-0.625-1.802-0.512c-0.649,0.113-1.208,0.52-1.517,1.102l-8.11,15.264"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (178, 'Nature, disasters', '<path fill="#FFFFFF" d="M61.263,32.634c-0.902,0-1.799,0.099-2.677,0.294c-2.907-4.972-8.162-8.011-13.991-8.011 c-8.943,0-16.219,7.274-16.219,16.218c0,8.942,7.275,16.218,16.219,16.218h1.57c0.123-1.066,0.136-2.044-0.034-2.967h-1.537 c-7.308,0-13.251-5.945-13.251-13.251c0-7.307,5.944-13.252,13.252-13.252c5.103,0,9.666,2.851,11.91,7.44l0.569,1.165 l1.229-0.408c0.957-0.317,1.953-0.478,2.959-0.478c5.18,0,9.393,4.213,9.393,9.392s-4.213,9.393-9.393,9.393h-0.489 c-0.224,0.944-0.552,1.957-1.037,2.967h1.526c6.815-0.001,12.36-5.545,12.36-12.36C73.623,38.179,68.078,32.634,61.263,32.634z"/> <path fill="#FFFFFF" d="M45.056,49.77h14.812c0,0,0.554,6.17-3.584,10.146c-3.751,3.605-5.68,8.446-4.373,15.542 c-5.429-2.927-6.473-6.411-5.914-9.939C47.088,58.658,49.405,54.119,45.056,49.77z"/>', 'nature-2', '[{"datad":"M61.263,32.634c-0.902,0-1.799,0.099-2.677,0.294c-2.907-4.972-8.162-8.011-13.991-8.011 c-8.943,0-16.219,7.274-16.219,16.218c0,8.942,7.275,16.218,16.219,16.218h1.57c0.123-1.066,0.136-2.044-0.034-2.967h-1.537 c-7.308,0-13.251-5.945-13.251-13.251c0-7.307,5.944-13.252,13.252-13.252c5.103,0,9.666,2.851,11.91,7.44l0.569,1.165 l1.229-0.408c0.957-0.317,1.953-0.478,2.959-0.478c5.18,0,9.393,4.213,9.393,9.392s-4.213,9.393-9.393,9.393h-0.489 c-0.224,0.944-0.552,1.957-1.037,2.967h1.526c6.815-0.001,12.36-5.545,12.36-12.36C73.623,38.179,68.078,32.634,61.263,32.634z"},{"datad":"M45.056,49.77h14.812c0,0,0.554,6.17-3.584,10.146c-3.751,3.605-5.68,8.446-4.373,15.542 c-5.429-2.927-6.473-6.411-5.914-9.939C47.088,58.658,49.405,54.119,45.056,49.77z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (177, 'Biohazard', '<path fill="#FFFFFF" d="M41.557,48.791l-0.216-0.092c-0.088-0.038-0.18-0.068-0.271-0.098l-0.146-0.048 c-0.616-0.225-1.18-0.372-1.725-0.447l-0.354-0.05l-0.028,0.357c-0.029,0.363-0.05,0.729-0.05,1.098c0,2.948,0.955,5.81,2.69,8.056 c1.325,1.716,3.042,3.006,4.966,3.728l0.348,0.132l0.097-0.359c0.202-0.751,0.325-1.552,0.364-2.382l0.01-0.236l-0.217-0.093 c-0.594-0.25-1.165-0.575-1.695-0.968c-2.382-1.762-3.803-4.706-3.803-7.877c0-0.101,0.006-0.203,0.011-0.304L41.557,48.791z"/> <path fill="#FFFFFF" d="M42.646,40.516c0.438,0.655,0.932,1.241,1.467,1.741l0.23,0.215l0.23-0.215 c1.531-1.427,3.473-2.213,5.467-2.213c1.997,0,3.937,0.786,5.466,2.213l0.23,0.215l0.229-0.215 c0.533-0.498,1.028-1.083,1.467-1.741l0.16-0.237l-0.206-0.196c-2.051-1.95-4.66-3.023-7.347-3.023 c-2.685,0-5.294,1.074-7.347,3.023l-0.207,0.196L42.646,40.516z"/> <path fill="#FFFFFF" d="M62.021,43.407c-0.459,0-0.925,0.026-1.387,0.081c1.724-2.506,2.665-5.658,2.665-8.973 c0-5.855-4.15-10.473-5.934-12.187l-0.571-0.549v3.854l0.088,0.098c2.325,2.544,3.657,5.746,3.657,8.784 c0,2.721-0.776,5.247-2.246,7.307c-0.47,0.657-0.912,1.176-1.39,1.632c-1.921,1.829-4.363,2.836-6.875,2.836 c-2.516,0-4.959-1.007-6.879-2.836c-0.511-0.486-0.986-1.035-1.413-1.632C40.579,40.2,39.2,37.615,39.2,34.515 c0-3.274,1.209-6.476,3.318-8.784l0.088-0.098v-3.854l-0.572,0.549c-1.312,1.26-5.592,5.863-5.592,12.187 c0,3.163,1.017,6.317,2.879,8.97c-0.444-0.053-0.875-0.078-1.319-0.078c-7.349,0-13.328,6.621-13.328,14.758 c0,0.937,0.095,1.925,0.28,2.935l0.103,0.557l2.486-2.001l-0.019-0.182c-0.044-0.436-0.066-0.875-0.066-1.309 c0-6.494,4.756-11.777,10.602-11.777c0.434,0,0.886,0.038,1.426,0.121c0.665,0.098,1.323,0.268,1.956,0.504 c4.318,1.617,7.22,6.098,7.22,11.152c0,0.353-0.021,0.7-0.048,1.045c-0.058,0.736-0.183,1.48-0.371,2.211 c-1.297,5.017-5.484,8.521-10.181,8.521c-0.292,0-0.598-0.019-0.959-0.055l-0.138-0.015l-2.942,2.377l0.619,0.182 c1.117,0.327,2.268,0.493,3.42,0.493c5.11,0,9.733-3.203,11.979-8.23c2.246,5.027,6.869,8.23,11.978,8.23 c1.386,0,2.755-0.236,4.072-0.703l0.569-0.202l-2.807-2.267L63.7,69.779c-0.631,0.11-1.164,0.162-1.682,0.162 c-4.694,0-8.88-3.504-10.179-8.521c-0.189-0.729-0.313-1.474-0.371-2.21c-0.028-0.346-0.048-0.693-0.048-1.046 c0-5.055,2.899-9.536,7.217-11.152c0.634-0.235,1.292-0.406,1.96-0.504c0.542-0.082,0.994-0.12,1.423-0.12 c5.845,0,10.6,5.283,10.6,11.777c0,0.147-0.009,0.292-0.018,0.438l-0.021,0.377l2.553,2.063l0.083-0.59 c0.105-0.755,0.16-1.524,0.16-2.288C75.378,50.028,69.386,43.407,62.021,43.407z"/> <path fill="#FFFFFF" d="M53.058,58.355l-0.206,0.087v0.086l-0.008,0.007l0.007,0.149c0.04,0.829,0.162,1.63,0.363,2.381l0.097,0.36 l0.349-0.132c3.225-1.211,5.838-4.049,6.991-7.593c0.078-0.24,0.149-0.484,0.214-0.731c0.216-0.825,0.356-1.647,0.416-2.445 c0.022-0.334,0.036-0.672,0.036-1.014c0-0.37-0.021-0.735-0.051-1.098l-0.027-0.357l-0.355,0.05 c-0.328,0.046-0.645,0.121-0.912,0.188c-0.465,0.119-0.866,0.251-1.23,0.406l-0.215,0.091l0.019,0.409 c0.008,0.104,0.013,0.208,0.013,0.311c0,2.681-1.033,5.247-2.832,7.039C54.938,57.335,54.04,57.943,53.058,58.355z"/>', 'biohazard-2', '[{"datad":"M41.557,48.791l-0.216-0.092c-0.088-0.038-0.18-0.068-0.271-0.098l-0.146-0.048 c-0.616-0.225-1.18-0.372-1.725-0.447l-0.354-0.05l-0.028,0.357c-0.029,0.363-0.05,0.729-0.05,1.098c0,2.948,0.955,5.81,2.69,8.056 c1.325,1.716,3.042,3.006,4.966,3.728l0.348,0.132l0.097-0.359c0.202-0.751,0.325-1.552,0.364-2.382l0.01-0.236l-0.217-0.093 c-0.594-0.25-1.165-0.575-1.695-0.968c-2.382-1.762-3.803-4.706-3.803-7.877c0-0.101,0.006-0.203,0.011-0.304L41.557,48.791z"},{"datad":"M42.646,40.516c0.438,0.655,0.932,1.241,1.467,1.741l0.23,0.215l0.23-0.215 c1.531-1.427,3.473-2.213,5.467-2.213c1.997,0,3.937,0.786,5.466,2.213l0.23,0.215l0.229-0.215 c0.533-0.498,1.028-1.083,1.467-1.741l0.16-0.237l-0.206-0.196c-2.051-1.95-4.66-3.023-7.347-3.023 c-2.685,0-5.294,1.074-7.347,3.023l-0.207,0.196L42.646,40.516z"},{"datad":"M62.021,43.407c-0.459,0-0.925,0.026-1.387,0.081c1.724-2.506,2.665-5.658,2.665-8.973 c0-5.855-4.15-10.473-5.934-12.187l-0.571-0.549v3.854l0.088,0.098c2.325,2.544,3.657,5.746,3.657,8.784 c0,2.721-0.776,5.247-2.246,7.307c-0.47,0.657-0.912,1.176-1.39,1.632c-1.921,1.829-4.363,2.836-6.875,2.836 c-2.516,0-4.959-1.007-6.879-2.836c-0.511-0.486-0.986-1.035-1.413-1.632C40.579,40.2,39.2,37.615,39.2,34.515 c0-3.274,1.209-6.476,3.318-8.784l0.088-0.098v-3.854l-0.572,0.549c-1.312,1.26-5.592,5.863-5.592,12.187 c0,3.163,1.017,6.317,2.879,8.97c-0.444-0.053-0.875-0.078-1.319-0.078c-7.349,0-13.328,6.621-13.328,14.758 c0,0.937,0.095,1.925,0.28,2.935l0.103,0.557l2.486-2.001l-0.019-0.182c-0.044-0.436-0.066-0.875-0.066-1.309 c0-6.494,4.756-11.777,10.602-11.777c0.434,0,0.886,0.038,1.426,0.121c0.665,0.098,1.323,0.268,1.956,0.504 c4.318,1.617,7.22,6.098,7.22,11.152c0,0.353-0.021,0.7-0.048,1.045c-0.058,0.736-0.183,1.48-0.371,2.211 c-1.297,5.017-5.484,8.521-10.181,8.521c-0.292,0-0.598-0.019-0.959-0.055l-0.138-0.015l-2.942,2.377l0.619,0.182 c1.117,0.327,2.268,0.493,3.42,0.493c5.11,0,9.733-3.203,11.979-8.23c2.246,5.027,6.869,8.23,11.978,8.23 c1.386,0,2.755-0.236,4.072-0.703l0.569-0.202l-2.807-2.267L63.7,69.779c-0.631,0.11-1.164,0.162-1.682,0.162 c-4.694,0-8.88-3.504-10.179-8.521c-0.189-0.729-0.313-1.474-0.371-2.21c-0.028-0.346-0.048-0.693-0.048-1.046 c0-5.055,2.899-9.536,7.217-11.152c0.634-0.235,1.292-0.406,1.96-0.504c0.542-0.082,0.994-0.12,1.423-0.12 c5.845,0,10.6,5.283,10.6,11.777c0,0.147-0.009,0.292-0.018,0.438l-0.021,0.377l2.553,2.063l0.083-0.59 c0.105-0.755,0.16-1.524,0.16-2.288C75.378,50.028,69.386,43.407,62.021,43.407z"},{"datad":"M53.058,58.355l-0.206,0.087v0.086l-0.008,0.007l0.007,0.149c0.04,0.829,0.162,1.63,0.363,2.381l0.097,0.36 l0.349-0.132c3.225-1.211,5.838-4.049,6.991-7.593c0.078-0.24,0.149-0.484,0.214-0.731c0.216-0.825,0.356-1.647,0.416-2.445 c0.022-0.334,0.036-0.672,0.036-1.014c0-0.37-0.021-0.735-0.051-1.098l-0.027-0.357l-0.355,0.05 c-0.328,0.046-0.645,0.121-0.912,0.188c-0.465,0.119-0.866,0.251-1.23,0.406l-0.215,0.091l0.019,0.409 c0.008,0.104,0.013,0.208,0.013,0.311c0,2.681-1.033,5.247-2.832,7.039C54.938,57.335,54.04,57.943,53.058,58.355z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (176, 'Electricity, blackout', '<polyline fill="#FFFFFF" points="64.652,22.932 52.272,43.224 67.899,43.224 33.203,76.818 47.452,53.411 32.101,53.411 50.701,22.934 "/>', 'elect-2', '[{"datad":"M64.652,22.932 52.272,43.224 67.899,43.224 33.203,76.818 47.452,53.411 32.101,53.411 50.701,22.934"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (175, 'Facebook', '<path fill-rule="evenodd" clip-rule="evenodd" fill="#FFFFFF" d="M45.556,70.551V48.307h-7.317V41.06h7.317c0,0,0-5.302,0-6.385 c0-1.085-0.226-8.341,9.389-8.341c1.288,0,3.735,0,6.309,0c0,3.153,0,6.559,0,7.546c-1.928,0-4.723,0-5.707,0 c-0.986,0-1.985,0.997-1.985,1.738c0,0.739,0,5.416,0,5.416s7.355,0,8.156,0c-0.279,3.799-0.853,7.273-0.853,7.273h-7.338V70.66 L45.556,70.551z"/>', 'facebookico-2', '[{"datad":"M45.556,70.551V48.307h-7.317V41.06h7.317c0,0,0-5.302,0-6.385 c0-1.085-0.226-8.341,9.389-8.341c1.288,0,3.735,0,6.309,0c0,3.153,0,6.559,0,7.546c-1.928,0-4.723,0-5.707,0 c-0.986,0-1.985,0.997-1.985,1.738c0,0.739,0,5.416,0,5.416s7.355,0,8.156,0c-0.279,3.799-0.853,7.273-0.853,7.273h-7.338V70.66 L45.556,70.551z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (174, 'Twitter', '<path fill="#FFFFFF" d="M73.57,35.88c-1.661,0.736-3.445,1.235-5.317,1.458c1.912-1.146,3.376-2.961,4.07-5.12 c-1.794,1.061-3.775,1.831-5.884,2.249c-1.69-1.802-4.094-2.925-6.759-2.925c-5.114,0-9.261,4.146-9.261,9.258 c0,0.725,0.082,1.433,0.24,2.11c-7.695-0.386-14.519-4.073-19.085-9.675c-0.798,1.365-1.252,2.957-1.252,4.654 c0,3.213,1.636,6.048,4.118,7.707c-1.517-0.051-2.945-0.468-4.194-1.162v0.116c0,4.485,3.193,8.227,7.427,9.08 c-0.776,0.209-1.594,0.325-2.44,0.325c-0.598,0-1.176-0.06-1.743-0.173c1.179,3.682,4.597,6.358,8.648,6.431 c-3.168,2.483-7.162,3.959-11.5,3.959c-0.747,0-1.484-0.045-2.209-0.128c4.099,2.633,8.965,4.166,14.195,4.166 c17.033,0,26.346-14.108,26.346-26.345l-0.031-1.199C70.758,39.366,72.331,37.736,73.57,35.88z"/>', 'twitterico-2', '[{"datad":"M73.57,35.88c-1.661,0.736-3.445,1.235-5.317,1.458c1.912-1.146,3.376-2.961,4.07-5.12 c-1.794,1.061-3.775,1.831-5.884,2.249c-1.69-1.802-4.094-2.925-6.759-2.925c-5.114,0-9.261,4.146-9.261,9.258 c0,0.725,0.082,1.433,0.24,2.11c-7.695-0.386-14.519-4.073-19.085-9.675c-0.798,1.365-1.252,2.957-1.252,4.654 c0,3.213,1.636,6.048,4.118,7.707c-1.517-0.051-2.945-0.468-4.194-1.162v0.116c0,4.485,3.193,8.227,7.427,9.08 c-0.776,0.209-1.594,0.325-2.44,0.325c-0.598,0-1.176-0.06-1.743-0.173c1.179,3.682,4.597,6.358,8.648,6.431 c-3.168,2.483-7.162,3.959-11.5,3.959c-0.747,0-1.484-0.045-2.209-0.128c4.099,2.633,8.965,4.166,14.195,4.166 c17.033,0,26.346-14.108,26.346-26.345l-0.031-1.199C70.758,39.366,72.331,37.736,73.57,35.88z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (173, 'Submarine', '<path fill="#FFFFFF" d="M64.298,71.05h-0.002c-0.007,0-0.755,0.626-1.752,0.678c-0.674,0.046-1.399-0.481-2.165-1.248 c-1.436-1.435-2.989-2.178-4.586-2.082c-2.344,0.146-3.797,1.868-3.984,2.104c-0.002,0.002-0.002-0.016-0.002-0.016 c-0.009,0.01-0.757,0.907-1.754,0.959c-0.676,0.048-1.398-0.342-2.166-1.105c-1.434-1.435-2.988-1.816-4.585-1.722 c-2.342,0.146-3.798,1.649-3.985,2.431c0,0-0.002,0-0.002,0c-0.008,0-0.756,0.626-1.752,0.678c-0.668,0.047-1.4-0.481-2.166-1.248 c-1.434-1.434-2.988-2.176-4.586-2.081c-1.155,0.072-2.087,0.464-2.766,0.943v3.644c0.458,0,0.914-0.193,1.223-0.574 c0.008-0.011,0.756-0.917,1.753-0.969c0.684-0.03,1.4,0.336,2.166,1.102c1.342,1.341,2.777,2.02,4.275,2.02 c0.104,0,0.208,0.097,0.311,0.09c2.342-0.146,3.798-1.262,3.985-2.042c0,0,0.001,0,0.001,0c0.008,0,0.756-1.017,1.753-1.069 c0.676-0.028,1.4,0.286,2.166,1.053c1.341,1.342,2.778,1.994,4.276,1.994c0.104,0,0.208,0.084,0.31,0.079 c2.343-0.147,3.798-1.275,3.985-2.056h0.001c0.008,0,0.756-1.017,1.754-1.068c0.678-0.028,1.398,0.285,2.166,1.053 c1.34,1.342,2.775,1.993,4.273,1.993c0.104,0,0.209,0.085,0.311,0.079c2.343-0.147,3.797-1.274,3.985-2.055 c0.002,0,0.002,0,0.002,0c0.009,0,0.756-1.018,1.753-1.069c0.679-0.028,1.399,0.286,2.165,1.052 c0.361,0.362,0.73,0.622,1.104,0.885v-4.079c-1.117-0.786-2.287-0.854-3.481-0.782C65.942,68.762,64.485,70.268,64.298,71.05z"/> <path fill="#FFFFFF" d="M60.835,32.403c2.156,0,3.904,1.952,3.904,4.294h2.342c0-3.903-2.795-6.636-6.245-6.636L60.835,32.403 L60.835,32.403z"/> <path fill="#FFFFFF" d="M37.561,68.313c0.997-0.051,1.745-0.959,1.753-0.968c0-0.001,0-0.001,0-0.001 c0.188-0.238,1.644-1.996,3.985-2.142c0.382-0.023,0.764,0.001,1.14,0.067v-13.35c0-2.586,2.098-4.293,4.684-4.293h11.712 c6.035,0,10.93-4.894,10.93-10.931s-4.892-10.931-10.93-10.931H43.659c-6.037,0-10.93,4.893-10.93,10.93c0,0.264,0.021,0,0.04,0.78 h-0.04v27.995c0.92,0.313,1.814,0.889,2.666,1.739C36.162,67.978,36.892,68.36,37.561,68.313z M68.642,36.307 c0,4.312-3.494,7.808-7.807,7.808s-7.808-3.496-7.808-7.808c0-4.313,3.495-7.808,7.808-7.808 C65.148,28.499,68.642,31.994,68.642,36.307z"/>', 'submarine-2', '[{"datad":"M64.298,71.05h-0.002c-0.007,0-0.755,0.626-1.752,0.678c-0.674,0.046-1.399-0.481-2.165-1.248 c-1.436-1.435-2.989-2.178-4.586-2.082c-2.344,0.146-3.797,1.868-3.984,2.104c-0.002,0.002-0.002-0.016-0.002-0.016 c-0.009,0.01-0.757,0.907-1.754,0.959c-0.676,0.048-1.398-0.342-2.166-1.105c-1.434-1.435-2.988-1.816-4.585-1.722 c-2.342,0.146-3.798,1.649-3.985,2.431c0,0-0.002,0-0.002,0c-0.008,0-0.756,0.626-1.752,0.678c-0.668,0.047-1.4-0.481-2.166-1.248 c-1.434-1.434-2.988-2.176-4.586-2.081c-1.155,0.072-2.087,0.464-2.766,0.943v3.644c0.458,0,0.914-0.193,1.223-0.574 c0.008-0.011,0.756-0.917,1.753-0.969c0.684-0.03,1.4,0.336,2.166,1.102c1.342,1.341,2.777,2.02,4.275,2.02 c0.104,0,0.208,0.097,0.311,0.09c2.342-0.146,3.798-1.262,3.985-2.042c0,0,0.001,0,0.001,0c0.008,0,0.756-1.017,1.753-1.069 c0.676-0.028,1.4,0.286,2.166,1.053c1.341,1.342,2.778,1.994,4.276,1.994c0.104,0,0.208,0.084,0.31,0.079 c2.343-0.147,3.798-1.275,3.985-2.056h0.001c0.008,0,0.756-1.017,1.754-1.068c0.678-0.028,1.398,0.285,2.166,1.053 c1.34,1.342,2.775,1.993,4.273,1.993c0.104,0,0.209,0.085,0.311,0.079c2.343-0.147,3.797-1.274,3.985-2.055 c0.002,0,0.002,0,0.002,0c0.009,0,0.756-1.018,1.753-1.069c0.679-0.028,1.399,0.286,2.165,1.052 c0.361,0.362,0.73,0.622,1.104,0.885v-4.079c-1.117-0.786-2.287-0.854-3.481-0.782C65.942,68.762,64.485,70.268,64.298,71.05z"},{"datad":"M60.835,32.403c2.156,0,3.904,1.952,3.904,4.294h2.342c0-3.903-2.795-6.636-6.245-6.636L60.835,32.403 L60.835,32.403z"},{"datad":"M37.561,68.313c0.997-0.051,1.745-0.959,1.753-0.968c0-0.001,0-0.001,0-0.001 c0.188-0.238,1.644-1.996,3.985-2.142c0.382-0.023,0.764,0.001,1.14,0.067v-13.35c0-2.586,2.098-4.293,4.684-4.293h11.712 c6.035,0,10.93-4.894,10.93-10.931s-4.892-10.931-10.93-10.931H43.659c-6.037,0-10.93,4.893-10.93,10.93c0,0.264,0.021,0,0.04,0.78 h-0.04v27.995c0.92,0.313,1.814,0.889,2.666,1.739C36.162,67.978,36.892,68.36,37.561,68.313z M68.642,36.307 c0,4.312-3.494,7.808-7.807,7.808s-7.808-3.496-7.808-7.808c0-4.313,3.495-7.808,7.808-7.808 C65.148,28.499,68.642,31.994,68.642,36.307z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (172, 'Rocket', '<path fill="#FFFFFF" d="M49.385,19.326c0,0-5.467,3.859-5.467,16.722h5.467h5.468C54.854,23.185,49.385,19.326,49.385,19.326z"/> <rect x="43.919" y="37.603" fill="#FFFFFF" width="10.935" height="2.17"/> <rect x="43.919" y="41.193" fill="#FFFFFF" width="10.935" height="33.494"/> <polygon fill="#FFFFFF" points="42.634,60.977 35.45,70.731 35.45,77.416 42.634,77.416 "/> <polygon fill="#FFFFFF" points="56.032,60.977 63.216,70.731 63.216,77.416 56.032,77.416 "/>', 'rocket-2', '[{"datad":"M49.385,19.326c0,0-5.467,3.859-5.467,16.722h5.467h5.468C54.854,23.185,49.385,19.326,49.385,19.326z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (171, 'No Water', '<path fill="#FFFFFF" d="M49.98,71c9.227,0,16.706-7.481,16.706-16.694c0-9.236-16.706-34.063-16.706-34.063 S33.27,45.069,33.27,54.306C33.271,63.519,40.747,71,49.98,71z M54.266,43.807c0,0,5.129,7.836,5.129,10.61 c0,2.767-2.303,5.021-5.129,5.021c-2.84,0-5.144-2.254-5.144-5.021C49.122,51.643,54.266,43.807,54.266,43.807z"/>', 'nowater-2', '[{"datad":"M49.98,71c9.227,0,16.706-7.481,16.706-16.694c0-9.236-16.706-34.063-16.706-34.063 S33.27,45.069,33.27,54.306C33.271,63.519,40.747,71,49.98,71z M54.266,43.807c0,0,5.129,7.836,5.129,10.61 c0,2.767-2.303,5.021-5.129,5.021c-2.84,0-5.144-2.254-5.144-5.021C49.122,51.643,54.266,43.807,54.266,43.807z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (170, 'Anti-air, SAM', '<path fill="#FFFFFF" d="M61.475,28.887c1.088-0.638,1.451-2.038,0.813-3.126c-0.638-1.088-2.038-1.452-3.126-0.814L17.05,45.152c-1.088,0.639-1.453,2.039-0.813,3.126c0.638,1.089,2.039,1.454,3.126,0.815L61.475,28.887z"/><path fill="#FFFFFF" d="M84.033,56.11L82,50.419h-7.014v-2.104h-2.812V46.12H59.579v9.531H41.005v-0.152l-11.101-5.095l34.214-16.417c1.087-0.638,1.45-2.038,0.813-3.126c-0.638-1.088-2.039-1.452-3.126-0.814L19.692,50.252c-1.088,0.638-1.452,2.038-0.813,3.125c0.639,1.088,2.038,1.453,3.126,0.814l2.631-1.263l-0.124,2.721h-0.745c-1.258,0-2.278,1.021-2.278,2.278c0,1.259,1.02,2.278,2.278,2.278h3.831c-0.054,0.264-0.083,0.537-0.083,0.817c0,2.283,1.853,4.135,4.136,4.135c2.284,0,4.136-1.852,4.136-4.135c0-0.28-0.029-0.554-0.083-0.817h0.704c-0.054,0.264-0.082,0.537-0.082,0.817c0,2.283,1.851,4.135,4.135,4.135s4.136-1.852,4.136-4.135c0-0.28-0.029-0.554-0.082-0.817H56.94c-0.054,0.264-0.081,0.537-0.081,0.817c0,2.283,1.851,4.135,4.136,4.135c2.283,0,4.135-1.852,4.135-4.135c0-0.28-0.028-0.554-0.081-0.817h3.073c-0.055,0.264-0.083,0.537-0.083,0.817c0,2.283,1.852,4.135,4.136,4.135s4.136-1.852,4.136-4.135c0-0.28-0.028-0.554-0.082-0.817h1.326c1.257,0,2.277-1.02,2.277-2.278c0-0.4-0.104-0.774-0.284-1.102L84.033,56.11z"/>', 'aa-2', '[{"datad":"M61.475,28.887c1.088-0.638,1.451-2.038,0.813-3.126c-0.638-1.088-2.038-1.452-3.126-0.814L17.05,45.152c-1.088,0.639-1.453,2.039-0.813,3.126c0.638,1.089,2.039,1.454,3.126,0.815L61.475,28.887z"},{"datad":"M84.033,56.11L82,50.419h-7.014v-2.104h-2.812V46.12H59.579v9.531H41.005v-0.152l-11.101-5.095l34.214-16.417c1.087-0.638,1.45-2.038,0.813-3.126c-0.638-1.088-2.039-1.452-3.126-0.814L19.692,50.252c-1.088,0.638-1.452,2.038-0.813,3.125c0.639,1.088,2.038,1.453,3.126,0.814l2.631-1.263l-0.124,2.721h-0.745c-1.258,0-2.278,1.021-2.278,2.278c0,1.259,1.02,2.278,2.278,2.278h3.831c-0.054,0.264-0.083,0.537-0.083,0.817c0,2.283,1.853,4.135,4.136,4.135c2.284,0,4.136-1.852,4.136-4.135c0-0.28-0.029-0.554-0.083-0.817h0.704c-0.054,0.264-0.082,0.537-0.082,0.817c0,2.283,1.851,4.135,4.135,4.135s4.136-1.852,4.136-4.135c0-0.28-0.029-0.554-0.082-0.817H56.94c-0.054,0.264-0.081,0.537-0.081,0.817c0,2.283,1.851,4.135,4.136,4.135c2.283,0,4.135-1.852,4.135-4.135c0-0.28-0.028-0.554-0.081-0.817h3.073c-0.055,0.264-0.083,0.537-0.083,0.817c0,2.283,1.852,4.135,4.136,4.135s4.136-1.852,4.136-4.135c0-0.28-0.028-0.554-0.082-0.817h1.326c1.257,0,2.277-1.02,2.277-2.278c0-0.4-0.104-0.774-0.284-1.102L84.033,56.11z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (169, 'House', '<path fill="#FFFFFF" d="M35.368,61.516c1.044,0,1.892-0.847,1.892-1.892V55.31c0-1.044-0.848-1.893-1.892-1.893 s-1.892,0.849-1.892,1.893v4.314C33.476,60.668,34.323,61.516,35.368,61.516z"/> <path fill="#FFFFFF" d="M43.559,44.566c1.045,0,1.892-0.847,1.892-1.892v-4.312c0-1.045-0.848-1.892-1.892-1.892 s-1.892,0.847-1.892,1.892v4.312C41.667,43.718,42.514,44.566,43.559,44.566z"/> <path fill="#FFFFFF" d="M35.368,44.566c1.045,0,1.892-0.847,1.892-1.892v-4.312c0-1.045-0.848-1.892-1.892-1.892 s-1.892,0.847-1.892,1.892v4.312C33.476,43.718,34.323,44.566,35.368,44.566z"/> <path fill="#FFFFFF" d="M54.48,44.566c1.045,0,1.892-0.847,1.892-1.892v-4.312c0-1.045-0.847-1.892-1.892-1.892 s-1.892,0.847-1.892,1.892v4.312C52.589,43.718,53.436,44.566,54.48,44.566z"/> <path fill="#FFFFFF" d="M63.224,61.516c1.045,0,1.893-0.847,1.893-1.892V55.31c0-1.044-0.848-1.893-1.893-1.893 s-1.892,0.849-1.892,1.893v4.314C61.332,60.668,62.179,61.516,63.224,61.516z"/> <path fill="#FFFFFF" d="M63.224,44.566c1.045,0,1.893-0.847,1.893-1.892v-4.312c0-1.045-0.848-1.892-1.893-1.892 s-1.892,0.847-1.892,1.892v4.312C61.332,43.718,62.179,44.566,63.224,44.566z"/> <path fill="#FFFFFF" d="M71.554,27.419H27.447c-1.045,0-1.892,0.847-1.892,1.892v40.798c0,1.045,0.847,1.893,1.892,1.893h44.107 c1.045,0,1.892-0.848,1.892-1.893V29.312C73.445,28.267,72.599,27.419,71.554,27.419z M54.738,67.787H44.015V54.763h10.724V67.787z M69.661,68.218H58.522V52.87c0-1.044-0.848-1.892-1.893-1.892H42.123c-1.044,0-1.892,0.848-1.892,1.892v15.348H29.339V31.203 h40.322V68.218z"/>', 'house-2', '[{"datad":"M35.368,61.516c1.044,0,1.892-0.847,1.892-1.892V55.31c0-1.044-0.848-1.893-1.892-1.893 s-1.892,0.849-1.892,1.893v4.314C33.476,60.668,34.323,61.516,35.368,61.516z"},{"datad":"M43.559,44.566c1.045,0,1.892-0.847,1.892-1.892v-4.312c0-1.045-0.848-1.892-1.892-1.892 s-1.892,0.847-1.892,1.892v4.312C41.667,43.718,42.514,44.566,43.559,44.566z"},{"datad":"M35.368,44.566c1.045,0,1.892-0.847,1.892-1.892v-4.312c0-1.045-0.848-1.892-1.892-1.892 s-1.892,0.847-1.892,1.892v4.312C33.476,43.718,34.323,44.566,35.368,44.566z"},{"datad":"M54.48,44.566c1.045,0,1.892-0.847,1.892-1.892v-4.312c0-1.045-0.847-1.892-1.892-1.892 s-1.892,0.847-1.892,1.892v4.312C52.589,43.718,53.436,44.566,54.48,44.566z"},{"datad":"M63.224,61.516c1.045,0,1.893-0.847,1.893-1.892V55.31c0-1.044-0.848-1.893-1.893-1.893 s-1.892,0.849-1.892,1.893v4.314C61.332,60.668,62.179,61.516,63.224,61.516z"},{"datad":"M63.224,44.566c1.045,0,1.893-0.847,1.893-1.892v-4.312c0-1.045-0.848-1.892-1.893-1.892 s-1.892,0.847-1.892,1.892v4.312C61.332,43.718,62.179,44.566,63.224,44.566z"},{"datad":"M71.554,27.419H27.447c-1.045,0-1.892,0.847-1.892,1.892v40.798c0,1.045,0.847,1.893,1.892,1.893h44.107 c1.045,0,1.892-0.848,1.892-1.893V29.312C73.445,28.267,72.599,27.419,71.554,27.419z M54.738,67.787H44.015V54.763h10.724V67.787z M69.661,68.218H58.522V52.87c0-1.044-0.848-1.892-1.893-1.892H42.123c-1.044,0-1.892,0.848-1.892,1.892v15.348H29.339V31.203 h40.322V68.218z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (168, 'Railway', '<path fill="#FFFFFF" d="M47.097,23.701c-1.198-0.004-2.175-0.981-2.177-2.177c0.002-1.213,0.979-2.19,2.177-2.177 c1.211-0.013,2.187,0.964,2.177,2.177C49.284,22.72,48.307,23.696,47.097,23.701L47.097,23.701z"/> <path fill="#FFFFFF" d="M52.903,23.701c1.2-0.004,2.178-0.981,2.178-2.177c0-1.213-0.978-2.19-2.178-2.177 c-1.209-0.013-2.186,0.964-2.178,2.177C50.718,22.72,51.694,23.696,52.903,23.701L52.903,23.701z"/> <path fill="#FFFFFF" d="M61.548,67.748c3.021-0.503,6.032-3.657,6.026-7.508V32.125c0.007-3.964-3.332-7.608-7.857-7.604h-9.686 h-9.718c-4.551-0.004-7.892,3.64-7.887,7.604V60.24c-0.004,3.851,3.008,7.007,6.026,7.508l-9.055,13.6h5.236l6.468-9.529H50h8.898 l6.497,9.529h5.208L61.548,67.748z M44.888,27.426c0.001-0.705,0.601-1.285,1.293-1.295H50h3.819 c0.692,0.01,1.294,0.59,1.294,1.295v2.207c0,0.702-0.567,1.297-1.294,1.294H50h-3.818c-0.723,0.003-1.291-0.592-1.292-1.294 L44.888,27.426L44.888,27.426z M40.124,63.646c-1.896-0.015-3.442-1.561-3.438-3.471c-0.004-1.904,1.542-3.449,3.438-3.439 c1.917-0.01,3.462,1.535,3.471,3.439C43.586,62.086,42.04,63.632,40.124,63.646z M40.566,45.378 c-2.175-0.013-3.949-1.501-3.945-3.945v-5.047c0.015-2.105,1.376-3.941,3.945-3.945H50h9.434c2.572,0.004,3.934,1.839,3.944,3.945 v5.047c0.006,2.444-1.767,3.933-3.944,3.945H50H40.566z M56.343,60.177c-0.006-1.905,1.538-3.448,3.438-3.438 c1.917-0.012,3.463,1.533,3.473,3.438c-0.01,1.909-1.556,3.455-3.473,3.471C57.881,63.632,56.337,62.086,56.343,60.177z"/>', 'railway-2', '[{"datad":"M47.097,23.701c-1.198-0.004-2.175-0.981-2.177-2.177c0.002-1.213,0.979-2.19,2.177-2.177 c1.211-0.013,2.187,0.964,2.177,2.177C49.284,22.72,48.307,23.696,47.097,23.701L47.097,23.701z"},{"datad":"M52.903,23.701c1.2-0.004,2.178-0.981,2.178-2.177c0-1.213-0.978-2.19-2.178-2.177 c-1.209-0.013-2.186,0.964-2.178,2.177C50.718,22.72,51.694,23.696,52.903,23.701L52.903,23.701z"},{"datad":"M61.548,67.748c3.021-0.503,6.032-3.657,6.026-7.508V32.125c0.007-3.964-3.332-7.608-7.857-7.604h-9.686 h-9.718c-4.551-0.004-7.892,3.64-7.887,7.604V60.24c-0.004,3.851,3.008,7.007,6.026,7.508l-9.055,13.6h5.236l6.468-9.529H50h8.898 l6.497,9.529h5.208L61.548,67.748z M44.888,27.426c0.001-0.705,0.601-1.285,1.293-1.295H50h3.819 c0.692,0.01,1.294,0.59,1.294,1.295v2.207c0,0.702-0.567,1.297-1.294,1.294H50h-3.818c-0.723,0.003-1.291-0.592-1.292-1.294 L44.888,27.426L44.888,27.426z M40.124,63.646c-1.896-0.015-3.442-1.561-3.438-3.471c-0.004-1.904,1.542-3.449,3.438-3.439 c1.917-0.01,3.462,1.535,3.471,3.439C43.586,62.086,42.04,63.632,40.124,63.646z M40.566,45.378 c-2.175-0.013-3.949-1.501-3.945-3.945v-5.047c0.015-2.105,1.376-3.941,3.945-3.945H50h9.434c2.572,0.004,3.934,1.839,3.944,3.945 v5.047c0.006,2.444-1.767,3.933-3.944,3.945H50H40.566z M56.343,60.177c-0.006-1.905,1.538-3.448,3.438-3.438 c1.917-0.012,3.463,1.533,3.473,3.438c-0.01,1.909-1.556,3.455-3.473,3.471C57.881,63.632,56.337,62.086,56.343,60.177z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (167, 'Crane, construction', '<path fill="#FFFFFF" d="M80.688,46.631h-6.215V31.634h2.485c0.684,0,1.243-0.557,1.243-1.243c0-0.686-0.56-1.243-1.243-1.243h-2.415 L39.667,20.86v-1.658c0-0.686-0.557-1.243-1.243-1.243c-0.687,0-1.244,0.557-1.244,1.243v2.112l-7.974,7.833h-1.97 c-0.687,0-1.243,0.557-1.243,1.243c0,0.686,0.557,1.243,1.243,1.243h9.944v56.325c0,0.684,0.557,1.244,1.244,1.244 c0.686,0,1.243-0.561,1.243-1.244V31.634h32.321v14.998h-6.217c-0.683,0-1.242,0.56-1.242,1.243v7.459 c0,0.684,0.56,1.242,1.242,1.242h14.918c0.684,0,1.243-0.559,1.243-1.242v-7.459C81.932,47.191,81.372,46.631,80.688,46.631z M32.759,29.148l4.421-4.343v4.343H32.759z M39.667,23.416l24.126,5.732H39.667V23.416z M79.445,54.09H67.014v-4.972h12.432V54.09z" />', 'crane-2', '[{"datad":"M80.688,46.631h-6.215V31.634h2.485c0.684,0,1.243-0.557,1.243-1.243c0-0.686-0.56-1.243-1.243-1.243h-2.415 L39.667,20.86v-1.658c0-0.686-0.557-1.243-1.243-1.243c-0.687,0-1.244,0.557-1.244,1.243v2.112l-7.974,7.833h-1.97 c-0.687,0-1.243,0.557-1.243,1.243c0,0.686,0.557,1.243,1.243,1.243h9.944v56.325c0,0.684,0.557,1.244,1.244,1.244 c0.686,0,1.243-0.561,1.243-1.244V31.634h32.321v14.998h-6.217c-0.683,0-1.242,0.56-1.242,1.243v7.459 c0,0.684,0.56,1.242,1.242,1.242h14.918c0.684,0,1.243-0.559,1.243-1.242v-7.459C81.932,47.191,81.372,46.631,80.688,46.631z M32.759,29.148l4.421-4.343v4.343H32.759z M39.667,23.416l24.126,5.732H39.667V23.416z M79.445,54.09H67.014v-4.972h12.432V54.09z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (166, 'Landmines, IEDs', '<path fill="#FFFFFF" d="M50.5,28.807l-7.897,22.114L23.648,38.285l6.318,20.534c1.448,4.705,3.159,4.738,6.318,4.738h6.318v-3.159 h15.795v3.159h6.319c3.158,0,4.759-1.359,6.318-4.738l6.317-20.534L58.397,50.921L50.5,28.807z"/> <path fill="#FFFFFF" d="M45.762,63.557v3.16c-7,0-12.636,2.113-12.636,4.739c0,2.625,5.636,4.737,12.636,4.737h9.478 c7,0,12.636-2.113,12.636-4.737c0-2.627-5.636-4.739-12.636-4.739v-3.16H45.762z"/>', 'mine-2', '[{"datad":"M50.5,28.807l-7.897,22.114L23.648,38.285l6.318,20.534c1.448,4.705,3.159,4.738,6.318,4.738h6.318v-3.159 h15.795v3.159h6.319c3.158,0,4.759-1.359,6.318-4.738l6.317-20.534L58.397,50.921L50.5,28.807z"},{"datad":"M45.762,63.557v3.16c-7,0-12.636,2.113-12.636,4.739c0,2.625,5.636,4.737,12.636,4.737h9.478 c7,0,12.636-2.113,12.636-4.737c0-2.627-5.636-4.739-12.636-4.739v-3.16H45.762z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (164, 'Video', '<path fill="#FFFFFF" d="M73.594,38.282c-0.586-2.534-2.667-4.401-5.172-4.68c-5.933-0.66-11.937-1.073-17.914-1.069 c-5.979-0.004-11.983,0.409-17.916,1.069c-2.505,0.279-4.585,2.146-5.17,4.68c-0.832,3.608-0.843,7.545-0.843,11.26 c0,3.714,0,7.651,0.833,11.261c0.585,2.531,2.666,4.4,5.17,4.678c5.932,0.66,11.936,1.072,17.916,1.07 c5.978,0.002,11.982-0.41,17.914-1.07c2.505-0.277,4.586-2.146,5.171-4.678c0.833-3.609,0.839-7.547,0.839-11.261 C74.422,45.828,74.427,41.89,73.594,38.282 M58.657,50.644L47.38,56.74c-1.121,0.607-2.039,0.061-2.039-1.213v-11.97 c0-1.274,0.917-1.822,2.039-1.215l11.277,6.098C59.779,49.045,59.779,50.038,58.657,50.644"/>', 'video-2', '[{"datad":"M73.594,38.282c-0.586-2.534-2.667-4.401-5.172-4.68c-5.933-0.66-11.937-1.073-17.914-1.069 c-5.979-0.004-11.983,0.409-17.916,1.069c-2.505,0.279-4.585,2.146-5.17,4.68c-0.832,3.608-0.843,7.545-0.843,11.26 c0,3.714,0,7.651,0.833,11.261c0.585,2.531,2.666,4.4,5.17,4.678c5.932,0.66,11.936,1.072,17.916,1.07 c5.978,0.002,11.982-0.41,17.914-1.07c2.505-0.277,4.586-2.146,5.171-4.678c0.833-3.609,0.839-7.547,0.839-11.261 C74.422,45.828,74.427,41.89,73.594,38.282 M58.657,50.644L47.38,56.74c-1.121,0.607-2.039,0.061-2.039-1.213v-11.97 c0-1.274,0.917-1.822,2.039-1.215l11.277,6.098C59.779,49.045,59.779,50.038,58.657,50.644"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (163, 'Fort', '<path fill="#FFFFFF" d="M63.028,36.273l3.655-4.094v-1.496v-1.497v-2.992c0-0.826-0.959-1.362-1.785-1.362h-4.487 c-0.826,0-1.831,0.536-1.831,1.362v2.928h-4.29v-2.928c0-0.826-0.532-1.362-1.358-1.362h-5.983c-0.826,0-1.238,0.536-1.238,1.362 v2.928h-4.767v-2.928c0-0.826-0.648-1.362-1.474-1.362h-4.488c-0.826,0-1.666,0.536-1.666,1.362v2.992v1.496v1.496l3.535,4.094 H63.028z"/> <path fill="#FFFFFF" d="M31.99,63.92c-0.825,0-1.533,0.34-1.533,1.167v5.982c0,0.826,0.708,1.431,1.533,1.431h35.899 c0.827,0,1.655-0.604,1.655-1.431v-5.982c0-0.827-0.828-1.167-1.654-1.167H31.99z"/> <path fill="#FFFFFF" d="M61.906,38.656H37.974L35.158,62.49h29.564L61.906,38.656z M52.86,51.526h-5.72v-4.39 c0-1.542,1.318-2.991,2.86-2.991s2.86,1.45,2.86,2.991V51.526z"/>', 'fort-2', '[{"datad":"M63.028,36.273l3.655-4.094v-1.496v-1.497v-2.992c0-0.826-0.959-1.362-1.785-1.362h-4.487 c-0.826,0-1.831,0.536-1.831,1.362v2.928h-4.29v-2.928c0-0.826-0.532-1.362-1.358-1.362h-5.983c-0.826,0-1.238,0.536-1.238,1.362 v2.928h-4.767v-2.928c0-0.826-0.648-1.362-1.474-1.362h-4.488c-0.826,0-1.666,0.536-1.666,1.362v2.992v1.496v1.496l3.535,4.094 H63.028z"},{"datad":"M31.99,63.92c-0.825,0-1.533,0.34-1.533,1.167v5.982c0,0.826,0.708,1.431,1.533,1.431h35.899 c0.827,0,1.655-0.604,1.655-1.431v-5.982c0-0.827-0.828-1.167-1.654-1.167H31.99z"},{"datad":"M61.906,38.656H37.974L35.158,62.49h29.564L61.906,38.656z M52.86,51.526h-5.72v-4.39 c0-1.542,1.318-2.991,2.86-2.991s2.86,1.45,2.86,2.991V51.526z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (162, 'Phone', '<path fill="#FFFFFF" d="M56.436,52.926c-0.745,0-1.35-0.604-1.35-1.352c0-3.611-2.937-6.547-6.548-6.547 c-0.746,0-1.351-0.604-1.351-1.351c0-0.745,0.604-1.351,1.351-1.351c5.098,0,9.248,4.15,9.248,9.249 C57.786,52.321,57.182,52.926,56.436,52.926z"/> <path fill="#FFFFFF" d="M63.836,53.036c-0.745,0-1.35-0.604-1.35-1.352c0-7.751-6.306-14.057-14.059-14.057 c-0.746,0-1.35-0.605-1.35-1.351c0-0.746,0.604-1.351,1.35-1.351c9.242,0,16.759,7.518,16.759,16.759 C65.188,52.431,64.582,53.036,63.836,53.036z"/> <path fill="#FFFFFF" d="M71.09,52.853c-0.746,0-1.351-0.604-1.351-1.351c0-11.65-9.479-21.128-21.128-21.128 c-0.746,0-1.351-0.603-1.351-1.35c0-0.747,0.604-1.351,1.351-1.351c13.138,0,23.829,10.69,23.829,23.828 C72.439,52.248,71.834,52.853,71.09,52.853z"/> <path fill="#FFFFFF" d="M58.494,72.077c-6.83,0-13.981-3.54-20.679-10.238c-10.293-10.295-12.99-21.461-7.398-30.637 c1.052-1.728,2.884-2.759,4.897-2.759c1.506,0,2.922,0.588,3.99,1.658l2.112,2.111c1.816,1.815,2.181,4.635,0.886,6.855 c-2.885,4.943-1.88,11.628,2.389,15.895c2.546,2.547,6.076,4.007,9.685,4.007c2.25,0,4.398-0.56,6.21-1.617 c0.865-0.505,1.853-0.771,2.855-0.771c1.511,0,2.934,0.589,4.002,1.656l2.112,2.112c1.234,1.233,1.823,2.938,1.614,4.67 c-0.209,1.755-1.2,3.294-2.718,4.218C65.356,71.121,62.007,72.077,58.494,72.077z"/>', 'phone-2', '[{"datad":"M56.436,52.926c-0.745,0-1.35-0.604-1.35-1.352c0-3.611-2.937-6.547-6.548-6.547 c-0.746,0-1.351-0.604-1.351-1.351c0-0.745,0.604-1.351,1.351-1.351c5.098,0,9.248,4.15,9.248,9.249 C57.786,52.321,57.182,52.926,56.436,52.926z"},{"datad":"M63.836,53.036c-0.745,0-1.35-0.604-1.35-1.352c0-7.751-6.306-14.057-14.059-14.057 c-0.746,0-1.35-0.605-1.35-1.351c0-0.746,0.604-1.351,1.35-1.351c9.242,0,16.759,7.518,16.759,16.759 C65.188,52.431,64.582,53.036,63.836,53.036z"},{"datad":"M71.09,52.853c-0.746,0-1.351-0.604-1.351-1.351c0-11.65-9.479-21.128-21.128-21.128 c-0.746,0-1.351-0.603-1.351-1.35c0-0.747,0.604-1.351,1.351-1.351c13.138,0,23.829,10.69,23.829,23.828 C72.439,52.248,71.834,52.853,71.09,52.853z"},{"datad":"M58.494,72.077c-6.83,0-13.981-3.54-20.679-10.238c-10.293-10.295-12.99-21.461-7.398-30.637 c1.052-1.728,2.884-2.759,4.897-2.759c1.506,0,2.922,0.588,3.99,1.658l2.112,2.111c1.816,1.815,2.181,4.635,0.886,6.855 c-2.885,4.943-1.88,11.628,2.389,15.895c2.546,2.547,6.076,4.007,9.685,4.007c2.25,0,4.398-0.56,6.21-1.617 c0.865-0.505,1.853-0.771,2.855-0.771c1.511,0,2.934,0.589,4.002,1.656l2.112,2.112c1.234,1.233,1.823,2.938,1.614,4.67 c-0.209,1.755-1.2,3.294-2.718,4.218C65.356,71.121,62.007,72.077,58.494,72.077z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (161, 'Press', '<path fill="#FFFFFF" d="M71.299,48.304l-11.1-17.574c1.294-0.555,2.682-0.925,4.255-0.925c5.826,0,10.544,4.717,10.544,10.544 C74.904,43.587,73.518,46.362,71.299,48.304z M57.425,50.617l-4.07,2.498l-0.925,0.555l-0.925,0.555l-3.608,2.22l-0.925,0.556 l-0.925,0.555L29.306,68.283l-1.942-3.33c0-0.186,0-0.37-0.092-0.463c-0.092-0.185-0.277-0.277-0.462-0.369l-1.85-3.146 L52.43,43.772l0.37-0.185l1.388,2.127l1.201,1.943L57.425,50.617L57.425,50.617z M51.597,91.406H47.99v-32.65l3.606-2.22V91.406z M58.813,49.322l-4.811-7.399c-0.093-0.462-0.093-0.925-0.093-1.48c0-3.607,1.851-6.844,4.717-8.787l11.285,17.759 c-1.573,0.925-3.423,1.572-5.457,1.572C62.326,50.894,60.384,50.339,58.813,49.322z"/>', 'press-2', '[{"datad":"M71.299,48.304l-11.1-17.574c1.294-0.555,2.682-0.925,4.255-0.925c5.826,0,10.544,4.717,10.544,10.544 C74.904,43.587,73.518,46.362,71.299,48.304z M57.425,50.617l-4.07,2.498l-0.925,0.555l-0.925,0.555l-3.608,2.22l-0.925,0.556 l-0.925,0.555L29.306,68.283l-1.942-3.33c0-0.186,0-0.37-0.092-0.463c-0.092-0.185-0.277-0.277-0.462-0.369l-1.85-3.146 L52.43,43.772l0.37-0.185l1.388,2.127l1.201,1.943L57.425,50.617L57.425,50.617z M51.597,91.406H47.99v-32.65l3.606-2.22V91.406z M58.813,49.322l-4.811-7.399c-0.093-0.462-0.093-0.925-0.093-1.48c0-3.607,1.851-6.844,4.717-8.787l11.285,17.759 c-1.573,0.925-3.423,1.572-5.457,1.572C62.326,50.894,60.384,50.339,58.813,49.322z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (160, 'Money', '<path fill="#FFFFFF" d="M44.282,36.662h11.621l1.063-1.516c0.867-1.308,1.823-2.561,2.65-3.895 c0.393-0.634,1.128-1.671,0.838-2.474c-0.207-0.574-0.736-0.769-1.303-0.869c-0.411-0.072-2.508-0.602-3.354-0.429 c-0.751,0.154-1.445,0.51-2.167,0.761c-1.015,0.353-2.138,0.649-3.252,0.588c-0.961-0.052-2.055-0.175-2.961-0.503 c-0.751-0.272-1.478-0.673-2.273-0.809c-0.687-0.118-3.188,0.229-3.691,0.315c-3.543,0.609-0.967,3.305,1.767,7.314L44.282,36.662z M69.884,57.915c-2.978-6.864-8.373-14.35-14.023-17.961H44.136c-5.65,3.612-11.043,11.097-14.021,17.961 c-2.986,6.88,0.596,13.641,7.422,13.641h24.927C69.289,71.556,72.871,64.795,69.884,57.915z M51.089,64.146v2.786H48.87V64.31 c-1.703-0.054-3.408-0.541-4.382-1.191l0.731-2.542c1.055,0.622,2.57,1.189,4.22,1.189c1.705,0,2.868-0.838,2.868-2.136 c0-1.218-0.947-2.003-2.949-2.732c-2.841-1.028-4.68-2.326-4.68-4.843c0-2.327,1.623-4.111,4.355-4.598v-2.651h2.218v2.516 c1.704,0.054,2.866,0.459,3.733,0.893l-0.73,2.488c-0.649-0.298-1.84-0.919-3.679-0.919c-1.894,0-2.57,0.974-2.57,1.894 c0,1.109,0.974,1.73,3.273,2.65c3.029,1.138,4.382,2.599,4.382,5.004C55.66,61.631,54.064,63.66,51.089,64.146z"/>', 'money-2', '[{"datad":"M44.282,36.662h11.621l1.063-1.516c0.867-1.308,1.823-2.561,2.65-3.895 c0.393-0.634,1.128-1.671,0.838-2.474c-0.207-0.574-0.736-0.769-1.303-0.869c-0.411-0.072-2.508-0.602-3.354-0.429 c-0.751,0.154-1.445,0.51-2.167,0.761c-1.015,0.353-2.138,0.649-3.252,0.588c-0.961-0.052-2.055-0.175-2.961-0.503 c-0.751-0.272-1.478-0.673-2.273-0.809c-0.687-0.118-3.188,0.229-3.691,0.315c-3.543,0.609-0.967,3.305,1.767,7.314L44.282,36.662z M69.884,57.915c-2.978-6.864-8.373-14.35-14.023-17.961H44.136c-5.65,3.612-11.043,11.097-14.021,17.961 c-2.986,6.88,0.596,13.641,7.422,13.641h24.927C69.289,71.556,72.871,64.795,69.884,57.915z M51.089,64.146v2.786H48.87V64.31 c-1.703-0.054-3.408-0.541-4.382-1.191l0.731-2.542c1.055,0.622,2.57,1.189,4.22,1.189c1.705,0,2.868-0.838,2.868-2.136 c0-1.218-0.947-2.003-2.949-2.732c-2.841-1.028-4.68-2.326-4.68-4.843c0-2.327,1.623-4.111,4.355-4.598v-2.651h2.218v2.516 c1.704,0.054,2.866,0.459,3.733,0.893l-0.73,2.488c-0.649-0.298-1.84-0.919-3.679-0.919c-1.894,0-2.57,0.974-2.57,1.894 c0,1.109,0.974,1.73,3.273,2.65c3.029,1.138,4.382,2.599,4.382,5.004C55.66,61.631,54.064,63.66,51.089,64.146z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (159, 'Food', '<path fill="#FFFFFF" d="M65.521,57.593V45.179c0-2.691-1.035-5.227-2.95-7.142c-1.449-1.449-2.225-3.364-2.225-5.382v-0.362 c0.775-0.258,1.293-0.983,1.293-1.812v-1.293c0-1.191-0.672-2.226-1.656-2.795c-0.206-0.259-0.568-0.414-0.931-0.414h-0.673h-9.004 h-0.673c-0.362,0-0.724,0.155-0.931,0.414c-0.984,0.569-1.656,1.604-1.656,2.846v1.293c0,0.828,0.518,1.553,1.294,1.811v0.363 c0,2.018-0.776,3.933-2.225,5.33c-1.915,1.915-2.95,4.451-2.95,7.142v12.396h-4.628c2.604,2.817,4.355,5.488,4.628,6.978v0.672 c-0.034,0.129-0.084,0.244-0.169,0.329c-0.985,0.985-5.113-1.543-9.219-5.649c-0.623-0.624-1.209-1.247-1.753-1.861 c-4.086,1.017-6.988,3.578-6.988,6.58v0.881c0,3.894,4.877,7.05,10.894,7.05h11.117h15.524h3.319c6.016,0,10.895-3.156,10.895-7.05 v-0.881C75.853,60.854,71.274,57.783,65.521,57.593z M47.408,29.241c0-1.087,0.88-1.966,1.966-1.966h9.056 c1.087,0,1.966,0.879,1.966,1.966v1.293c0,0.362-0.31,0.673-0.673,0.673H48.081c-0.362,0-0.672-0.311-0.672-0.673V29.241z M47.046,39.797c1.915-1.863,2.95-4.398,2.95-7.142v-0.207h7.762v0.207c0,2.691,1.035,5.227,2.95,7.142 c0.621,0.621,1.138,1.345,1.5,2.122c-0.673-0.104-1.139-0.259-1.656-0.414c-0.828-0.258-1.707-0.569-3.415-0.569 s-2.588,0.259-3.415,0.569c-0.776,0.259-1.501,0.466-3.053,0.466c-1.501,0-2.278-0.259-3.054-0.466 c-0.465-0.155-0.983-0.311-1.604-0.414C46.321,40.625,46.632,40.211,47.046,39.797z M45.39,42.385 c0.776,0.104,1.293,0.259,1.863,0.414c0.828,0.259,1.708,0.569,3.416,0.569c1.706,0,2.586-0.259,3.414-0.569 c0.776-0.258,1.501-0.465,3.054-0.465s2.277,0.259,3.053,0.465c0.674,0.208,1.397,0.414,2.535,0.518 c0.156,0.621,0.26,1.242,0.26,1.863v12.396h-0.13c0.048,0.05,0.084,0.092,0.13,0.141v4.718c-0.874,0.416-2.203,0.155-4.889-2.531 c-0.785-0.781-1.51-1.565-2.165-2.327h-5.702c3.389,3.665,5.345,7.092,4.46,7.977c-0.985,0.986-5.113-1.541-9.219-5.649 c-0.225-0.225-0.434-0.449-0.649-0.673v-14.05C44.821,44.196,45.027,43.265,45.39,42.385z"/>', 'food-2', '[{"datad":"M65.521,57.593V45.179c0-2.691-1.035-5.227-2.95-7.142c-1.449-1.449-2.225-3.364-2.225-5.382v-0.362 c0.775-0.258,1.293-0.983,1.293-1.812v-1.293c0-1.191-0.672-2.226-1.656-2.795c-0.206-0.259-0.568-0.414-0.931-0.414h-0.673h-9.004 h-0.673c-0.362,0-0.724,0.155-0.931,0.414c-0.984,0.569-1.656,1.604-1.656,2.846v1.293c0,0.828,0.518,1.553,1.294,1.811v0.363 c0,2.018-0.776,3.933-2.225,5.33c-1.915,1.915-2.95,4.451-2.95,7.142v12.396h-4.628c2.604,2.817,4.355,5.488,4.628,6.978v0.672 c-0.034,0.129-0.084,0.244-0.169,0.329c-0.985,0.985-5.113-1.543-9.219-5.649c-0.623-0.624-1.209-1.247-1.753-1.861 c-4.086,1.017-6.988,3.578-6.988,6.58v0.881c0,3.894,4.877,7.05,10.894,7.05h11.117h15.524h3.319c6.016,0,10.895-3.156,10.895-7.05 v-0.881C75.853,60.854,71.274,57.783,65.521,57.593z M47.408,29.241c0-1.087,0.88-1.966,1.966-1.966h9.056 c1.087,0,1.966,0.879,1.966,1.966v1.293c0,0.362-0.31,0.673-0.673,0.673H48.081c-0.362,0-0.672-0.311-0.672-0.673V29.241z M47.046,39.797c1.915-1.863,2.95-4.398,2.95-7.142v-0.207h7.762v0.207c0,2.691,1.035,5.227,2.95,7.142 c0.621,0.621,1.138,1.345,1.5,2.122c-0.673-0.104-1.139-0.259-1.656-0.414c-0.828-0.258-1.707-0.569-3.415-0.569 s-2.588,0.259-3.415,0.569c-0.776,0.259-1.501,0.466-3.053,0.466c-1.501,0-2.278-0.259-3.054-0.466 c-0.465-0.155-0.983-0.311-1.604-0.414C46.321,40.625,46.632,40.211,47.046,39.797z M45.39,42.385 c0.776,0.104,1.293,0.259,1.863,0.414c0.828,0.259,1.708,0.569,3.416,0.569c1.706,0,2.586-0.259,3.414-0.569 c0.776-0.258,1.501-0.465,3.054-0.465s2.277,0.259,3.053,0.465c0.674,0.208,1.397,0.414,2.535,0.518 c0.156,0.621,0.26,1.242,0.26,1.863v12.396h-0.13c0.048,0.05,0.084,0.092,0.13,0.141v4.718c-0.874,0.416-2.203,0.155-4.889-2.531 c-0.785-0.781-1.51-1.565-2.165-2.327h-5.702c3.389,3.665,5.345,7.092,4.46,7.977c-0.985,0.986-5.113-1.541-9.219-5.649 c-0.225-0.225-0.434-0.449-0.649-0.673v-14.05C44.821,44.196,45.027,43.265,45.39,42.385z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (158, 'Picture(photo)', '<path fill="#FFFFFF" d="M63.8,68.076H36.202c-5.115,0-9.277-4.161-9.277-9.276V40.201c0-5.115,4.161-9.277,9.277-9.277H63.8 c5.114,0,9.276,4.161,9.276,9.277V58.8C73.076,63.915,68.914,68.076,63.8,68.076z M36.202,33.737c-3.564,0-6.464,2.9-6.464,6.464 v18.6c0,3.563,2.9,6.463,6.464,6.463H63.8c3.564,0,6.464-2.899,6.464-6.463v-18.6c0-3.564-2.899-6.464-6.464-6.464H36.202 L36.202,33.737z"/> <path fill="#FFFFFF" d="M56.375,56.349c-0.29,0-0.578-0.111-0.796-0.33L42.345,42.785l-12.736,8.137 c-0.525,0.336-1.219,0.182-1.554-0.342s-0.181-1.219,0.342-1.554l13.499-8.623c0.446-0.284,1.028-0.221,1.401,0.152l13.208,13.208 l7.853-5.461c0.424-0.293,0.992-0.263,1.383,0.076l5.999,5.249c0.467,0.41,0.515,1.121,0.105,1.588 c-0.408,0.469-1.119,0.516-1.588,0.105l-5.337-4.668l-7.903,5.496C56.823,56.283,56.599,56.349,56.375,56.349z"/> <path fill="#FFFFFF" d="M58.25,48.476c-3.101,0-5.625-2.524-5.625-5.625c0-3.101,2.524-5.624,5.625-5.624s5.624,2.523,5.624,5.624 C63.874,45.952,61.352,48.476,58.25,48.476z M58.25,39.476c-1.861,0-3.375,1.514-3.375,3.374c0,1.861,1.514,3.375,3.375,3.375 c1.86,0,3.374-1.514,3.374-3.375C61.624,40.99,60.11,39.476,58.25,39.476z"/>', 'picture-2', '[{"datad":"M63.8,68.076H36.202c-5.115,0-9.277-4.161-9.277-9.276V40.201c0-5.115,4.161-9.277,9.277-9.277H63.8 c5.114,0,9.276,4.161,9.276,9.277V58.8C73.076,63.915,68.914,68.076,63.8,68.076z M36.202,33.737c-3.564,0-6.464,2.9-6.464,6.464 v18.6c0,3.563,2.9,6.463,6.464,6.463H63.8c3.564,0,6.464-2.899,6.464-6.463v-18.6c0-3.564-2.899-6.464-6.464-6.464H36.202 L36.202,33.737z"},{"datad":"M56.375,56.349c-0.29,0-0.578-0.111-0.796-0.33L42.345,42.785l-12.736,8.137 c-0.525,0.336-1.219,0.182-1.554-0.342s-0.181-1.219,0.342-1.554l13.499-8.623c0.446-0.284,1.028-0.221,1.401,0.152l13.208,13.208 l7.853-5.461c0.424-0.293,0.992-0.263,1.383,0.076l5.999,5.249c0.467,0.41,0.515,1.121,0.105,1.588 c-0.408,0.469-1.119,0.516-1.588,0.105l-5.337-4.668l-7.903,5.496C56.823,56.283,56.599,56.349,56.375,56.349z"},{"datad":"M58.25,48.476c-3.101,0-5.625-2.524-5.625-5.625c0-3.101,2.524-5.624,5.625-5.624s5.624,2.523,5.624,5.624 C63.874,45.952,61.352,48.476,58.25,48.476z M58.25,39.476c-1.861,0-3.375,1.514-3.375,3.374c0,1.861,1.514,3.375,3.375,3.375 c1.86,0,3.374-1.514,3.374-3.375C61.624,40.99,60.11,39.476,58.25,39.476z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (157, 'Hackers, computers', '<path fill="#FFFFFF" d="M49.979,25.333c-13.624,0-24.667,11.044-24.667,24.667s11.043,24.667,24.667,24.667 c13.623,0,24.667-11.044,24.667-24.667S63.602,25.333,49.979,25.333z M49.979,72.2c-12.242,0-22.2-9.958-22.2-22.2 s9.958-22.2,22.2-22.2c12.242,0,22.2,9.958,22.2,22.2S62.221,72.2,49.979,72.2z"/> <path fill="#FFFFFF" d="M49.979,43.833c-3.406,0-6.167,2.761-6.167,6.167c0,3.405,2.761,6.167,6.167,6.167 c3.405,0,6.167-2.762,6.167-6.167C56.146,46.594,53.384,43.833,49.979,43.833z M49.979,53.7c-2.041,0-3.7-1.66-3.7-3.7 c0-2.041,1.66-3.7,3.7-3.7c2.04,0,3.7,1.66,3.7,3.7C53.679,52.04,52.019,53.7,49.979,53.7z"/> <path fill="#FFFFFF" d="M63.934,36.045l-7.85,7.85c1.56,1.567,2.528,3.726,2.528,6.105h11.1 C69.712,44.549,67.504,39.615,63.934,36.045z"/> <path fill="#FFFFFF" d="M61.819,34.213c-0.944-0.715-1.956-1.338-3.016-1.869l-4.965,9.935c0.462,0.234,0.907,0.505,1.32,0.814 L61.819,34.213z"/>', 'comp-2', '[{"datad":"M49.979,25.333c-13.624,0-24.667,11.044-24.667,24.667s11.043,24.667,24.667,24.667 c13.623,0,24.667-11.044,24.667-24.667S63.602,25.333,49.979,25.333z M49.979,72.2c-12.242,0-22.2-9.958-22.2-22.2 s9.958-22.2,22.2-22.2c12.242,0,22.2,9.958,22.2,22.2S62.221,72.2,49.979,72.2z"},{"datad":"M49.979,43.833c-3.406,0-6.167,2.761-6.167,6.167c0,3.405,2.761,6.167,6.167,6.167 c3.405,0,6.167-2.762,6.167-6.167C56.146,46.594,53.384,43.833,49.979,43.833z M49.979,53.7c-2.041,0-3.7-1.66-3.7-3.7 c0-2.041,1.66-3.7,3.7-3.7c2.04,0,3.7,1.66,3.7,3.7C53.679,52.04,52.019,53.7,49.979,53.7z"},{"datad":"M63.934,36.045l-7.85,7.85c1.56,1.567,2.528,3.726,2.528,6.105h11.1 C69.712,44.549,67.504,39.615,63.934,36.045z"},{"datad":"M61.819,34.213c-0.944-0.715-1.956-1.338-3.016-1.869l-4.965,9.935c0.462,0.234,0.907,0.505,1.32,0.814 L61.819,34.213z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (156, 'Trucks', '<path fill="#FFFFFF" d="M58.926,61.391a4.523,4.523 0 1,0 9.046,0a4.523,4.523 0 1,0 -9.046,0"/> <path fill="#FFFFFF" d="M60.435,41.79v-4.523v-1.508c0-1.666-1.351-3.016-3.016-3.016H28.77c-1.665,0-3.016,1.35-3.016,3.016 v16.585c0,1.666,1.351,3.017,3.016,3.017h3.016v0.037c1.263-0.957,2.82-1.546,4.523-1.546c4.157,0,7.539,3.383,7.539,7.539h12.063 c0-3.082,1.865-5.733,4.523-6.9V41.79z"/> <path fill="#FFFFFF" d="M72.337,44.487c-0.745-1.49-2.699-2.698-4.364-2.698h-6.03v12.214c0.486-0.099,0.992-0.152,1.507-0.152 c4.158,0,7.539,3.383,7.539,7.539c1.666,0,3.017-1.35,3.017-3.016v-4.523v-6.031L72.337,44.487z M71.859,49.329h-6.901v-6.031 h3.015c0.834,0,1.811,0.604,2.182,1.349l1.705,3.411V49.329L71.859,49.329z"/> <path fill="#FFFFFF" d="M31.785999999999998,61.391a4.523,4.523 0 1,0 9.046,0a4.523,4.523 0 1,0 -9.046,0"/>', 'truck-2', '[{"datad":"M58.926,61.391a4.523,4.523 0 1,0 9.046,0a4.523,4.523 0 1,0 -9.046,0"},{"datad":"M31.785999999999998,61.391a4.523,4.523 0 1,0 9.046,0a4.523,4.523 0 1,0 -9.046,0"},{"datad":"M60.435,41.79v-4.523v-1.508c0-1.666-1.351-3.016-3.016-3.016H28.77c-1.665,0-3.016,1.35-3.016,3.016 v16.585c0,1.666,1.351,3.017,3.016,3.017h3.016v0.037c1.263-0.957,2.82-1.546,4.523-1.546c4.157,0,7.539,3.383,7.539,7.539h12.063 c0-3.082,1.865-5.733,4.523-6.9V41.79z"},{"datad":"M72.337,44.487c-0.745-1.49-2.699-2.698-4.364-2.698h-6.03v12.214c0.486-0.099,0.992-0.152,1.507-0.152 c4.158,0,7.539,3.383,7.539,7.539c1.666,0,3.017-1.35,3.017-3.016v-4.523v-6.031L72.337,44.487z M71.859,49.329h-6.901v-6.031 h3.015c0.834,0,1.811,0.604,2.182,1.349l1.705,3.411V49.329L71.859,49.329z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (155, 'Shelling', '<path fill="#FFFFFF" d="M60.23,25.703c-0.197-0.2-0.469-0.324-0.771-0.324c-0.223,0-0.427,0.068-0.6,0.181l-7.857,5.334 l0.001-4.562c-0.001-0.555-0.45-1.003-1.003-1.003s-1.001,0.448-1.002,1.003v8.669c0,0.345-0.278,0.624-0.624,0.624 c-0.344,0-0.622-0.278-0.622-0.624V30.65c0-0.433-0.247-0.764-0.593-1.001l-6.02-4.089c-0.172-0.112-0.376-0.18-0.598-0.18 c-0.303,0-0.576,0.125-0.773,0.324c-0.193,0.194-0.312,0.465-0.312,0.762v10.64c0,1.687,0.863,3.171,2.168,4.035l4.604,3.128 l-3.477,3.6c-0.006,0.007-0.013,0.011-0.019,0.017c-0.191,0.196-0.311,0.463-0.313,0.759c0,0-0.002,6.878-0.002,6.88 c0,0.345,0.277,0.623,0.62,0.629h13.92c0.343-0.006,0.62-0.284,0.62-0.629c0-0.002-0.002-6.88-0.002-6.88 c-0.001-0.296-0.121-0.563-0.313-0.759c-0.006-0.005-0.013-0.01-0.019-0.017l-3.477-3.6l4.604-3.128 c1.306-0.863,2.168-2.348,2.168-4.035l0.001-10.64C60.543,26.169,60.424,25.899,60.23,25.703z"/> <path fill="#FFFFFF" d="M56.862,57.593H43.137c-0.345,0-0.625,0.279-0.625,0.625c0,0.003,0.002,0.007,0.002,0.011l-0.002,0.003 c0.491,5.852,2.994,11.129,6.814,15.135c0.165,0.186,0.406,0.304,0.673,0.304s0.508-0.118,0.673-0.304 c3.82-4.006,6.322-9.282,6.814-15.135l-0.002-0.002c0-0.004,0.002-0.008,0.002-0.012C57.487,57.872,57.208,57.593,56.862,57.593z" />', 'bomb-2', '[{"datad":"M60.23,25.703c-0.197-0.2-0.469-0.324-0.771-0.324c-0.223,0-0.427,0.068-0.6,0.181l-7.857,5.334 l0.001-4.562c-0.001-0.555-0.45-1.003-1.003-1.003s-1.001,0.448-1.002,1.003v8.669c0,0.345-0.278,0.624-0.624,0.624 c-0.344,0-0.622-0.278-0.622-0.624V30.65c0-0.433-0.247-0.764-0.593-1.001l-6.02-4.089c-0.172-0.112-0.376-0.18-0.598-0.18 c-0.303,0-0.576,0.125-0.773,0.324c-0.193,0.194-0.312,0.465-0.312,0.762v10.64c0,1.687,0.863,3.171,2.168,4.035l4.604,3.128 l-3.477,3.6c-0.006,0.007-0.013,0.011-0.019,0.017c-0.191,0.196-0.311,0.463-0.313,0.759c0,0-0.002,6.878-0.002,6.88 c0,0.345,0.277,0.623,0.62,0.629h13.92c0.343-0.006,0.62-0.284,0.62-0.629c0-0.002-0.002-6.88-0.002-6.88 c-0.001-0.296-0.121-0.563-0.313-0.759c-0.006-0.005-0.013-0.01-0.019-0.017l-3.477-3.6l4.604-3.128 c1.306-0.863,2.168-2.348,2.168-4.035l0.001-10.64C60.543,26.169,60.424,25.899,60.23,25.703z"},{"datad":"M56.862,57.593H43.137c-0.345,0-0.625,0.279-0.625,0.625c0,0.003,0.002,0.007,0.002,0.011l-0.002,0.003 c0.491,5.852,2.994,11.129,6.814,15.135c0.165,0.186,0.406,0.304,0.673,0.304s0.508-0.118,0.673-0.304 c3.82-4.006,6.322-9.282,6.814-15.135l-0.002-0.002c0-0.004,0.002-0.008,0.002-0.012C57.487,57.872,57.208,57.593,56.862,57.593z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (154, 'Explosion, blasts', '<path fill="#FFFFFF" d="M62.922,56.979c4.845,0,8.784-3.871,8.784-8.629c0-2.377-0.997-4.639-2.749-6.265 c0.27-0.938,0.407-1.92,0.407-2.925c0-6.024-4.991-10.925-11.126-10.925c-0.724,0-1.455,0.084-2.232,0.255 c-1.949-1.6-4.468-2.478-7.127-2.478c-2.49,0-4.892,0.791-6.79,2.231c-0.081-0.005-0.164-0.009-0.248-0.009 c-6.135,0-11.126,4.901-11.126,10.925c0,0.993,0.137,1.975,0.408,2.925c-1.753,1.63-2.75,3.892-2.75,6.265 c0,4.758,3.94,8.629,8.784,8.629c1.445,0,2.843-0.343,4.099-0.997v19.942c0,2.21-1.839,4.01-4.099,4.01 c-0.969,0-1.757,0.777-1.757,1.732c0,0.956,0.788,1.734,1.757,1.734c4.198,0,7.613-3.355,7.613-7.477v-23.78 c0-0.762-0.516-1.446-1.243-1.66c-0.725-0.227-1.548,0.063-1.976,0.696c-0.979,1.452-2.622,2.319-4.394,2.319 c-2.906,0-5.27-2.313-5.27-5.157c0-1.696,0.856-3.285,2.29-4.25c0.683-0.459,0.949-1.341,0.632-2.1 c-0.384-0.912-0.58-1.869-0.58-2.844c0-4.11,3.415-7.455,7.613-7.455c0.062,0,0.121,0.009,0.18,0.016l0.484,0.061l0.102,0.003 c0.461,0,0.876-0.154,1.197-0.44c1.366-1.201,3.168-1.862,5.073-1.862c2.057,0,4.027,0.783,5.406,2.149 c0.453,0.449,1.177,0.618,1.75,0.439c0.841-0.248,1.542-0.363,2.207-0.363c4.197,0,7.612,3.345,7.612,7.456 c0,0.978-0.195,1.935-0.584,2.847c-0.315,0.75-0.048,1.633,0.637,2.099c1.434,0.967,2.29,2.556,2.29,4.251 c0,2.846-2.364,5.16-5.271,5.16c-1.771,0-3.414-0.867-4.397-2.326c-0.427-0.617-1.234-0.909-1.964-0.692 c-0.748,0.225-1.251,0.894-1.251,1.663v23.772c0,4.121,3.415,7.477,7.612,7.477c0.968,0,1.754-0.778,1.754-1.734 c0-0.955-0.787-1.732-1.757-1.732c-2.259,0-4.098-1.8-4.098-4.01V55.98C60.081,56.637,61.48,56.979,62.922,56.979z"/> <path fill="#FFFFFF" d="M52.385,74.774c-0.647,0-1.172-0.521-1.172-1.151v-18.38c0-0.632,0.524-1.147,1.172-1.147 c0.646,0,1.171,0.518,1.171,1.147v18.38C53.556,74.258,53.031,74.774,52.385,74.774z"/> <path fill="#FFFFFF" d="M37.159,40.309c-0.646,0-1.171-0.518-1.171-1.147c0-3.169,2.628-5.743,5.855-5.743 c0.646,0,1.171,0.517,1.171,1.147c0,0.632-0.525,1.151-1.171,1.151c-1.938,0-3.514,1.545-3.514,3.445 C38.33,39.791,37.806,40.309,37.159,40.309z"/> <path fill="#FFFFFF" d="M62.925,51.799c-0.642,0-1.171-0.519-1.171-1.151s0.529-1.148,1.171-1.148s1.171-0.517,1.171-1.149 c0-0.633,0.53-1.149,1.172-1.149s1.171,0.518,1.171,1.149C66.438,50.252,64.862,51.799,62.925,51.799z"/>', 'explode-2', '[{"datad":"M62.922,56.979c4.845,0,8.784-3.871,8.784-8.629c0-2.377-0.997-4.639-2.749-6.265 c0.27-0.938,0.407-1.92,0.407-2.925c0-6.024-4.991-10.925-11.126-10.925c-0.724,0-1.455,0.084-2.232,0.255 c-1.949-1.6-4.468-2.478-7.127-2.478c-2.49,0-4.892,0.791-6.79,2.231c-0.081-0.005-0.164-0.009-0.248-0.009 c-6.135,0-11.126,4.901-11.126,10.925c0,0.993,0.137,1.975,0.408,2.925c-1.753,1.63-2.75,3.892-2.75,6.265 c0,4.758,3.94,8.629,8.784,8.629c1.445,0,2.843-0.343,4.099-0.997v19.942c0,2.21-1.839,4.01-4.099,4.01 c-0.969,0-1.757,0.777-1.757,1.732c0,0.956,0.788,1.734,1.757,1.734c4.198,0,7.613-3.355,7.613-7.477v-23.78 c0-0.762-0.516-1.446-1.243-1.66c-0.725-0.227-1.548,0.063-1.976,0.696c-0.979,1.452-2.622,2.319-4.394,2.319 c-2.906,0-5.27-2.313-5.27-5.157c0-1.696,0.856-3.285,2.29-4.25c0.683-0.459,0.949-1.341,0.632-2.1 c-0.384-0.912-0.58-1.869-0.58-2.844c0-4.11,3.415-7.455,7.613-7.455c0.062,0,0.121,0.009,0.18,0.016l0.484,0.061l0.102,0.003 c0.461,0,0.876-0.154,1.197-0.44c1.366-1.201,3.168-1.862,5.073-1.862c2.057,0,4.027,0.783,5.406,2.149 c0.453,0.449,1.177,0.618,1.75,0.439c0.841-0.248,1.542-0.363,2.207-0.363c4.197,0,7.612,3.345,7.612,7.456 c0,0.978-0.195,1.935-0.584,2.847c-0.315,0.75-0.048,1.633,0.637,2.099c1.434,0.967,2.29,2.556,2.29,4.251 c0,2.846-2.364,5.16-5.271,5.16c-1.771,0-3.414-0.867-4.397-2.326c-0.427-0.617-1.234-0.909-1.964-0.692 c-0.748,0.225-1.251,0.894-1.251,1.663v23.772c0,4.121,3.415,7.477,7.612,7.477c0.968,0,1.754-0.778,1.754-1.734 c0-0.955-0.787-1.732-1.757-1.732c-2.259,0-4.098-1.8-4.098-4.01V55.98C60.081,56.637,61.48,56.979,62.922,56.979z"},{"datad":"M52.385,74.774c-0.647,0-1.172-0.521-1.172-1.151v-18.38c0-0.632,0.524-1.147,1.172-1.147 c0.646,0,1.171,0.518,1.171,1.147v18.38C53.556,74.258,53.031,74.774,52.385,74.774z"},{"datad":"M37.159,40.309c-0.646,0-1.171-0.518-1.171-1.147c0-3.169,2.628-5.743,5.855-5.743 c0.646,0,1.171,0.517,1.171,1.147c0,0.632-0.525,1.151-1.171,1.151c-1.938,0-3.514,1.545-3.514,3.445 C38.33,39.791,37.806,40.309,37.159,40.309z"},{"datad":"M62.925,51.799c-0.642,0-1.171-0.519-1.171-1.151s0.529-1.148,1.171-1.148s1.171-0.517,1.171-1.149 c0-0.633,0.53-1.149,1.172-1.149s1.171,0.518,1.171,1.149C66.438,50.252,64.862,51.799,62.925,51.799z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (153, 'Rifle Gun, armed men', '<rect x="-59.3" y="49.005" fill="#010101" width="0.244" height="0.221"/> <polygon fill="#010101" points="-53.738,52.108 -53.746,52.115 -53.746,52.108 "/> <polygon fill="#010101" points="-53.738,52.108 -53.746,52.115 -53.746,52.108 "/> <polygon fill="#FFFFFF" points="75.021,31.9 79.35,29.584 79.151,29.215 79.162,27.941 78.718,25.871 79.77,25.307 81.456,28.458 82.457,27.921 82.957,28.853 81.955,29.388 82.418,30.253 75.985,33.696 76.109,33.93 73.263,35.454 73.139,35.223 68.128,37.902 66.938,35.675 66.709,35.251 66.162,34.231 71.896,31.163 "/> <path fill="#FFFFFF" d="M19.784,65.724l5.825,7.051l11.362-15.499l-0.005-0.003l1.688-0.991l0.531,0.991l0.009-0.005l1.153,1.197 l0.213,4.631l2.192,0.59l1.882-1.008L43.925,61.1l0.033-5.542l0.779,0.308l2.87-1.537l0.176-0.795l-1.069-1.999l0.142-0.077 l0.877,1.636l0.857-0.456l0.195-0.104l0.444-0.237l-0.875-1.639l0.022-0.011l0.416-0.643c4.928,7.504,14.4,7.923,14.4,7.923 l0.492-4.834c0,0-6.985-0.026-10.623-5.382l3.46-1.853l-1.454-2.717l0.215-0.121l1.31,2.448l0.833,0.252l1.195-0.641l8.996-6.283 l-0.031-0.061l0.12-0.605l-1.965-3.672l-0.521-0.139l-0.024-0.048l-8.438,4.515l-0.423-0.264l-4.712,2.524l-0.361,1.164 l-14.498,7.761l-0.087,1.863l-0.601,0.324l0.211,1.473l0.218,0.412h-0.001l0.001,0.004l-1.503,0.861l-0.008-0.016l-3.752,2.94 l-1.927,0.16L19.784,65.724z M43.291,53.666l0.871,0.11l0.849,0.694l0.83,0.137l0.094-0.313l-0.815-0.123l-0.782-0.688l0.372-0.625 l-0.106-0.195l1.845-0.991l0.999,1.868l-0.152,0.684l-2.47,1.322l-0.67-0.267L43.291,53.666z"/>', 'ak-2', '[{"datad":"M19.784,65.724l5.825,7.051l11.362-15.499l-0.005-0.003l1.688-0.991l0.531,0.991l0.009-0.005l1.153,1.197 l0.213,4.631l2.192,0.59l1.882-1.008L43.925,61.1l0.033-5.542l0.779,0.308l2.87-1.537l0.176-0.795l-1.069-1.999l0.142-0.077 l0.877,1.636l0.857-0.456l0.195-0.104l0.444-0.237l-0.875-1.639l0.022-0.011l0.416-0.643c4.928,7.504,14.4,7.923,14.4,7.923 l0.492-4.834c0,0-6.985-0.026-10.623-5.382l3.46-1.853l-1.454-2.717l0.215-0.121l1.31,2.448l0.833,0.252l1.195-0.641l8.996-6.283 l-0.031-0.061l0.12-0.605l-1.965-3.672l-0.521-0.139l-0.024-0.048l-8.438,4.515l-0.423-0.264l-4.712,2.524l-0.361,1.164 l-14.498,7.761l-0.087,1.863l-0.601,0.324l0.211,1.473l0.218,0.412h-0.001l0.001,0.004l-1.503,0.861l-0.008-0.016l-3.752,2.94 l-1.927,0.16L19.784,65.724z M43.291,53.666l0.871,0.11l0.849,0.694l0.83,0.137l0.094-0.313l-0.815-0.123l-0.782-0.688l0.372-0.625 l-0.106-0.195l1.845-0.991l0.999,1.868l-0.152,0.684l-2.47,1.322l-0.67-0.267L43.291,53.666z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (152, 'No Connection, Jamming, Radar', '<path fill="#FFFFFF" d="M50,74.446c-3.138,0-5.69-2.553-5.69-5.69c0-3.141,2.552-5.692,5.69-5.692c3.138,0,5.69,2.552,5.69,5.692 C55.69,71.891,53.139,74.446,50,74.446z"/> <path fill="#FFFFFF" d="M44.902,52.353c-2.614,0.617-5.125,1.711-7.368,3.294c-1.026,0.73-1.271,2.149-0.547,3.175 c0.727,1.03,2.146,1.276,3.174,0.55c1.517-1.071,3.19-1.856,4.935-2.369L44.902,52.353z"/> <path fill="#FFFFFF" d="M62.46,55.646c-2.211-1.561-4.685-2.644-7.259-3.266l-0.194,4.649c1.706,0.513,3.341,1.291,4.828,2.341 c0.4,0.282,0.857,0.418,1.313,0.418c0.714,0,1.419-0.335,1.862-0.968C63.729,57.794,63.486,56.373,62.46,55.646z"/> <path fill="#FFFFFF" d="M78.203,37.907c-6.61-4.676-14.137-7.59-22.037-8.591l-0.191,4.57c7.021,0.953,13.709,3.575,19.604,7.739 c0.398,0.282,0.855,0.416,1.31,0.416c0.715,0,1.417-0.335,1.865-0.963C79.474,40.053,79.233,38.631,78.203,37.907z"/> <path fill="#FFFFFF" d="M43.938,29.301C36,30.29,28.438,33.212,21.793,37.907c-1.026,0.724-1.269,2.146-0.544,3.171 c0.724,1.026,2.144,1.274,3.172,0.547c5.922-4.186,12.647-6.814,19.708-7.754L43.938,29.301z"/> <path fill="#FFFFFF" d="M44.417,40.748c-5.281,0.845-10.302,2.886-14.751,6.032c-1.028,0.725-1.271,2.146-0.544,3.172 c0.724,1.025,2.144,1.271,3.172,0.545c3.725-2.631,7.912-4.374,12.315-5.16L44.417,40.748z"/> <path fill="#FFFFFF" d="M70.335,46.78c-4.422-3.125-9.404-5.16-14.647-6.014l-0.192,4.588c4.367,0.794,8.518,2.53,12.213,5.143 c0.401,0.284,0.855,0.418,1.313,0.418c0.715,0,1.417-0.336,1.861-0.963C71.602,48.927,71.361,47.505,70.335,46.78z"/> <polygon fill="#FFFFFF" points="52.504,58.715 53.892,25.554 46.214,25.554 47.602,58.715 "/>', 'wifi-2', '[{"datad":"M50,74.446c-3.138,0-5.69-2.553-5.69-5.69c0-3.141,2.552-5.692,5.69-5.692c3.138,0,5.69,2.552,5.69,5.692 C55.69,71.891,53.139,74.446,50,74.446z"},{"datad":"M44.902,52.353c-2.614,0.617-5.125,1.711-7.368,3.294c-1.026,0.73-1.271,2.149-0.547,3.175 c0.727,1.03,2.146,1.276,3.174,0.55c1.517-1.071,3.19-1.856,4.935-2.369L44.902,52.353z"},{"datad":"M62.46,55.646c-2.211-1.561-4.685-2.644-7.259-3.266l-0.194,4.649c1.706,0.513,3.341,1.291,4.828,2.341 c0.4,0.282,0.857,0.418,1.313,0.418c0.714,0,1.419-0.335,1.862-0.968C63.729,57.794,63.486,56.373,62.46,55.646z"},{"datad":"M78.203,37.907c-6.61-4.676-14.137-7.59-22.037-8.591l-0.191,4.57c7.021,0.953,13.709,3.575,19.604,7.739 c0.398,0.282,0.855,0.416,1.31,0.416c0.715,0,1.417-0.335,1.865-0.963C79.474,40.053,79.233,38.631,78.203,37.907z"},{"datad":"M43.938,29.301C36,30.29,28.438,33.212,21.793,37.907c-1.026,0.724-1.269,2.146-0.544,3.171 c0.724,1.026,2.144,1.274,3.172,0.547c5.922-4.186,12.647-6.814,19.708-7.754L43.938,29.301z"},{"datad":"M44.417,40.748c-5.281,0.845-10.302,2.886-14.751,6.032c-1.028,0.725-1.271,2.146-0.544,3.172 c0.724,1.025,2.144,1.271,3.172,0.545c3.725-2.631,7.912-4.374,12.315-5.16L44.417,40.748z"},{"datad":"M70.335,46.78c-4.422-3.125-9.404-5.16-14.647-6.014l-0.192,4.588c4.367,0.794,8.518,2.53,12.213,5.143 c0.401,0.284,0.855,0.418,1.313,0.418c0.715,0,1.417-0.336,1.861-0.963C71.602,48.927,71.361,47.505,70.335,46.78z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (151, 'Hostages and Refugees', '<polygon fill="#FFFFFF" points="30.208,63.573 47.037,63.573 47.037,56.922 30.208,56.922 "/> <path fill="#FFFFFF" d="M52.096,22.496c-9.129,0-17.549,6.857-19.298,14.407c-0.389,1.679-0.828,6.064-0.828,6.064l-5.168,9.149 c-0.084,0.188-0.124,0.407-0.124,0.629c0,0.894,0.721,1.62,1.619,1.62h0.44h3.233h17.08v11.763H32.235 c0.696,4.012,2.882,4.358,7.666,4.358h1.397v6.16h23.226v-17.08c4.684-3.682,7.696-10.527,7.696-16.945 C72.219,31.504,63.213,22.496,52.096,22.496z M37.418,46.973c-1.544,0-2.796-1.251-2.796-2.796s1.251-2.797,2.796-2.797 c1.544,0,2.797,1.252,2.797,2.797S38.963,46.973,37.418,46.973z M61.629,50.281c-0.438,1.528-1.356,2.825-2.585,3.653 c-0.883,0.596-1.853,0.898-2.816,0.898c-0.423,0-0.846-0.058-1.26-0.177c-0.391-0.111-0.617-0.521-0.504-0.909 c0.11-0.393,0.521-0.615,0.908-0.505c0.935,0.265,1.946,0.078,2.851-0.528c0.943-0.636,1.65-1.644,1.993-2.839 c0.319-1.112,0.557-2.689-0.118-3.886c-0.418-0.738-1.155-1.254-2.194-1.534c-1.359-0.364-2.839,0.661-3.307,2.29 c-0.11,0.391-0.518,0.618-0.909,0.505c-0.391-0.112-0.616-0.519-0.504-0.909c0.696-2.439,2.942-3.891,5.103-3.307 c1.435,0.387,2.473,1.139,3.093,2.233C62.111,46.565,62.197,48.298,61.629,50.281z"/>', 'hostage-2', '[{"datad":"M52.096,22.496c-9.129,0-17.549,6.857-19.298,14.407c-0.389,1.679-0.828,6.064-0.828,6.064l-5.168,9.149 c-0.084,0.188-0.124,0.407-0.124,0.629c0,0.894,0.721,1.62,1.619,1.62h0.44h3.233h17.08v11.763H32.235 c0.696,4.012,2.882,4.358,7.666,4.358h1.397v6.16h23.226v-17.08c4.684-3.682,7.696-10.527,7.696-16.945 C72.219,31.504,63.213,22.496,52.096,22.496z M37.418,46.973c-1.544,0-2.796-1.251-2.796-2.796s1.251-2.797,2.796-2.797 c1.544,0,2.797,1.252,2.797,2.797S38.963,46.973,37.418,46.973z M61.629,50.281c-0.438,1.528-1.356,2.825-2.585,3.653 c-0.883,0.596-1.853,0.898-2.816,0.898c-0.423,0-0.846-0.058-1.26-0.177c-0.391-0.111-0.617-0.521-0.504-0.909 c0.11-0.393,0.521-0.615,0.908-0.505c0.935,0.265,1.946,0.078,2.851-0.528c0.943-0.636,1.65-1.644,1.993-2.839 c0.319-1.112,0.557-2.689-0.118-3.886c-0.418-0.738-1.155-1.254-2.194-1.534c-1.359-0.364-2.839,0.661-3.307,2.29 c-0.11,0.391-0.518,0.618-0.909,0.505c-0.391-0.112-0.616-0.519-0.504-0.909c0.696-2.439,2.942-3.891,5.103-3.307 c1.435,0.387,2.473,1.139,3.093,2.233C62.111,46.565,62.197,48.298,61.629,50.281z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (150, 'Rally, Protests, Demos, crowds', '<path fill="#FFFFFF" d="M72.72,41.684c1.11-1.289,1.787-2.962,1.787-4.792c0-0.069-0.014-0.134-0.021-0.202 c0.008-0.068,0.021-0.137,0.021-0.208c0-4.056-3.298-7.356-7.356-7.356c-0.061,0-0.117,0.014-0.179,0.018 c-0.061-0.004-0.116-0.018-0.179-0.018c-4.056,0-7.356,3.3-7.356,7.356c0,0.07,0.015,0.137,0.021,0.204 c-0.008,0.069-0.021,0.134-0.021,0.206c0,1.956,0.771,3.729,2.019,5.047c-0.507,0.242-0.994,0.522-1.461,0.827 c-1.195-4.196-5.053-7.279-9.626-7.279c-0.083,0-0.163,0.018-0.244,0.024c-0.082-0.006-0.159-0.024-0.243-0.024 c-4.557,0-8.405,3.063-9.613,7.236c-0.62-0.399-1.271-0.751-1.957-1.04c1.11-1.289,1.787-2.962,1.787-4.792 c0-0.069-0.015-0.134-0.021-0.203c0.007-0.068,0.021-0.137,0.021-0.208c0-4.056-3.301-7.356-7.356-7.356 c-0.063,0-0.119,0.015-0.179,0.018c-0.061-0.004-0.118-0.018-0.178-0.018c-4.056,0-7.356,3.301-7.356,7.356 c0,0.07,0.015,0.137,0.02,0.204c-0.007,0.069-0.02,0.134-0.02,0.206c0,1.955,0.771,3.729,2.018,5.047 c-4.326,2.072-7.321,6.494-7.321,11.599c0,1.309,1.062,2.37,2.371,2.37c1.309,0,2.37-1.061,2.37-2.37 c0-4.475,3.642-8.116,8.118-8.116c0.129,0,0.253-0.017,0.375-0.038c0.124,0.02,0.265,0.038,0.394,0.038 c4.477,0,8.135,3.641,8.135,8.116c0,0.002,0,0.004,0,0.006c-4.664,3.023-8.821,8.693-8.821,15.179c0,1.781,1.438,3.226,3.218,3.226 c1.782,0,3.223-1.444,3.223-3.226c0-6.093,4.956-11.05,11.049-11.05c0.176,0,0.343-0.025,0.51-0.053 c0.168,0.027,0.338,0.053,0.513,0.053c6.093,0,11.051,4.957,11.051,11.05c0,1.781,1.443,3.226,3.225,3.226 c1.783,0,3.226-1.444,3.226-3.226c0-6.878-3.994-12.837-9.779-15.694c0.266-4.237,3.79-7.606,8.093-7.606 c0.129,0,0.252-0.015,0.375-0.038c0.124,0.021,0.248,0.038,0.376,0.038c4.479,0,8.119,3.643,8.119,8.116 c0,1.31,1.061,2.37,2.37,2.37c1.308,0,2.369-1.061,2.369-2.37C80.604,48.212,77.347,43.631,72.72,41.684z M35.356,36.891 c0,1.443-1.174,2.619-2.616,2.619c-0.062,0-0.119,0.013-0.179,0.016c-0.061-0.003-0.118-0.016-0.178-0.016 c-1.443,0-2.617-1.176-2.617-2.619c0-0.069-0.016-0.135-0.021-0.204c0.006-0.068,0.021-0.136,0.021-0.207 c0-1.443,1.173-2.617,2.617-2.617c0.061,0,0.118-0.014,0.178-0.017c0.06,0.004,0.117,0.017,0.179,0.017 c1.443,0,2.617,1.174,2.617,2.617c0,0.07,0.016,0.136,0.021,0.202C35.373,36.753,35.356,36.819,35.356,36.891z M53.929,46.06 c0,1.965-1.597,3.564-3.562,3.564c-0.083,0-0.162,0.018-0.244,0.023c-0.082-0.005-0.16-0.023-0.245-0.023 c-1.963,0-3.562-1.6-3.562-3.564c0-0.094-0.019-0.183-0.027-0.276c0.008-0.095,0.029-0.186,0.029-0.281 c0-1.964,1.597-3.563,3.562-3.563c0.083,0,0.161-0.017,0.243-0.024c0.082,0.007,0.161,0.024,0.244,0.024 c1.965,0,3.563,1.599,3.563,3.563c0,0.095,0.021,0.181,0.027,0.276C53.95,45.872,53.929,45.962,53.929,46.06z M69.768,36.891 c0,1.443-1.174,2.619-2.617,2.619c-0.061,0-0.118,0.013-0.18,0.016c-0.06-0.003-0.116-0.016-0.178-0.016 c-1.442,0-2.617-1.176-2.617-2.619c0-0.069-0.014-0.135-0.02-0.204c0.006-0.068,0.021-0.136,0.021-0.207 c0-1.443,1.174-2.617,2.616-2.617c0.062,0,0.118-0.014,0.178-0.017c0.062,0.004,0.119,0.017,0.18,0.017 c1.443,0,2.617,1.174,2.617,2.617c0,0.07,0.016,0.136,0.021,0.202C69.782,36.753,69.768,36.819,69.768,36.891z"/>', 'rally-2', '[{"datad":"M72.72,41.684c1.11-1.289,1.787-2.962,1.787-4.792c0-0.069-0.014-0.134-0.021-0.202 c0.008-0.068,0.021-0.137,0.021-0.208c0-4.056-3.298-7.356-7.356-7.356c-0.061,0-0.117,0.014-0.179,0.018 c-0.061-0.004-0.116-0.018-0.179-0.018c-4.056,0-7.356,3.3-7.356,7.356c0,0.07,0.015,0.137,0.021,0.204 c-0.008,0.069-0.021,0.134-0.021,0.206c0,1.956,0.771,3.729,2.019,5.047c-0.507,0.242-0.994,0.522-1.461,0.827 c-1.195-4.196-5.053-7.279-9.626-7.279c-0.083,0-0.163,0.018-0.244,0.024c-0.082-0.006-0.159-0.024-0.243-0.024 c-4.557,0-8.405,3.063-9.613,7.236c-0.62-0.399-1.271-0.751-1.957-1.04c1.11-1.289,1.787-2.962,1.787-4.792 c0-0.069-0.015-0.134-0.021-0.203c0.007-0.068,0.021-0.137,0.021-0.208c0-4.056-3.301-7.356-7.356-7.356 c-0.063,0-0.119,0.015-0.179,0.018c-0.061-0.004-0.118-0.018-0.178-0.018c-4.056,0-7.356,3.301-7.356,7.356 c0,0.07,0.015,0.137,0.02,0.204c-0.007,0.069-0.02,0.134-0.02,0.206c0,1.955,0.771,3.729,2.018,5.047 c-4.326,2.072-7.321,6.494-7.321,11.599c0,1.309,1.062,2.37,2.371,2.37c1.309,0,2.37-1.061,2.37-2.37 c0-4.475,3.642-8.116,8.118-8.116c0.129,0,0.253-0.017,0.375-0.038c0.124,0.02,0.265,0.038,0.394,0.038 c4.477,0,8.135,3.641,8.135,8.116c0,0.002,0,0.004,0,0.006c-4.664,3.023-8.821,8.693-8.821,15.179c0,1.781,1.438,3.226,3.218,3.226 c1.782,0,3.223-1.444,3.223-3.226c0-6.093,4.956-11.05,11.049-11.05c0.176,0,0.343-0.025,0.51-0.053 c0.168,0.027,0.338,0.053,0.513,0.053c6.093,0,11.051,4.957,11.051,11.05c0,1.781,1.443,3.226,3.225,3.226 c1.783,0,3.226-1.444,3.226-3.226c0-6.878-3.994-12.837-9.779-15.694c0.266-4.237,3.79-7.606,8.093-7.606 c0.129,0,0.252-0.015,0.375-0.038c0.124,0.021,0.248,0.038,0.376,0.038c4.479,0,8.119,3.643,8.119,8.116 c0,1.31,1.061,2.37,2.37,2.37c1.308,0,2.369-1.061,2.369-2.37C80.604,48.212,77.347,43.631,72.72,41.684z M35.356,36.891 c0,1.443-1.174,2.619-2.616,2.619c-0.062,0-0.119,0.013-0.179,0.016c-0.061-0.003-0.118-0.016-0.178-0.016 c-1.443,0-2.617-1.176-2.617-2.619c0-0.069-0.016-0.135-0.021-0.204c0.006-0.068,0.021-0.136,0.021-0.207 c0-1.443,1.173-2.617,2.617-2.617c0.061,0,0.118-0.014,0.178-0.017c0.06,0.004,0.117,0.017,0.179,0.017 c1.443,0,2.617,1.174,2.617,2.617c0,0.07,0.016,0.136,0.021,0.202C35.373,36.753,35.356,36.819,35.356,36.891z M53.929,46.06 c0,1.965-1.597,3.564-3.562,3.564c-0.083,0-0.162,0.018-0.244,0.023c-0.082-0.005-0.16-0.023-0.245-0.023 c-1.963,0-3.562-1.6-3.562-3.564c0-0.094-0.019-0.183-0.027-0.276c0.008-0.095,0.029-0.186,0.029-0.281 c0-1.964,1.597-3.563,3.562-3.563c0.083,0,0.161-0.017,0.243-0.024c0.082,0.007,0.161,0.024,0.244,0.024 c1.965,0,3.563,1.599,3.563,3.563c0,0.095,0.021,0.181,0.027,0.276C53.95,45.872,53.929,45.962,53.929,46.06z M69.768,36.891 c0,1.443-1.174,2.619-2.617,2.619c-0.061,0-0.118,0.013-0.18,0.016c-0.06-0.003-0.116-0.016-0.178-0.016 c-1.442,0-2.617-1.176-2.617-2.619c0-0.069-0.014-0.135-0.02-0.204c0.006-0.068,0.021-0.136,0.021-0.207 c0-1.443,1.174-2.617,2.616-2.617c0.062,0,0.118-0.014,0.178-0.017c0.062,0.004,0.119,0.017,0.18,0.017 c1.443,0,2.617,1.174,2.617,2.617c0,0.07,0.016,0.136,0.021,0.202C69.782,36.753,69.768,36.819,69.768,36.891z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (149, 'Drones, robots', '<path fill="#FFFFFF" d="M50.012,28.188c-4.053,0-7.769,2.044-9.942,5.468c-0.55,0.866-0.292,2.015,0.573,2.564 c0.309,0.196,0.653,0.289,0.994,0.289c0.615,0,1.217-0.305,1.57-0.862c1.487-2.344,4.031-3.744,6.805-3.744 c2.762,0,5.3,1.391,6.79,3.72c0.553,0.865,1.7,1.117,2.566,0.564c0.865-0.552,1.117-1.702,0.564-2.567 C57.756,30.22,54.048,28.188,50.012,28.188z"/> <path fill="#FFFFFF" d="M35.006,32.792c0.322,0.205,0.681,0.301,1.036,0.301c0.645,0,1.275-0.32,1.645-0.906 c2.689-4.258,7.3-6.801,12.332-6.801c5.009,0,9.609,2.525,12.304,6.756c0.576,0.905,1.778,1.171,2.684,0.595 c0.905-0.577,1.172-1.777,0.595-2.683c-3.412-5.356-9.237-8.554-15.582-8.554c-6.373,0-12.21,3.22-15.618,8.612 C33.828,31.02,34.099,32.22,35.006,32.792z"/> <path fill="#FFFFFF" d="M57.932,70.393h-5.314V58.5h23.812c0-3.288-2.621-5.946-5.946-5.946H52.618v-5.291 c0-1.982,2.643-3.303,2.671-7.302c0.021-2.92-2.366-5.286-5.286-5.286c-2.919,0-5.261,2.366-5.286,5.286 c-0.028,3.337,2.615,5.32,2.615,7.302v5.291H29.519c-3.325,0-5.946,2.658-5.946,5.946h23.759v11.893h-5.257 c-1.577,0-3.306,1.531-3.306,3.309L50.003,77l11.229-3.299C61.233,71.924,59.508,70.393,57.932,70.393z"/>', 'drone-2', '[{"datad":"M50.012,28.188c-4.053,0-7.769,2.044-9.942,5.468c-0.55,0.866-0.292,2.015,0.573,2.564 c0.309,0.196,0.653,0.289,0.994,0.289c0.615,0,1.217-0.305,1.57-0.862c1.487-2.344,4.031-3.744,6.805-3.744 c2.762,0,5.3,1.391,6.79,3.72c0.553,0.865,1.7,1.117,2.566,0.564c0.865-0.552,1.117-1.702,0.564-2.567 C57.756,30.22,54.048,28.188,50.012,28.188z"},{"datad":"M35.006,32.792c0.322,0.205,0.681,0.301,1.036,0.301c0.645,0,1.275-0.32,1.645-0.906 c2.689-4.258,7.3-6.801,12.332-6.801c5.009,0,9.609,2.525,12.304,6.756c0.576,0.905,1.778,1.171,2.684,0.595 c0.905-0.577,1.172-1.777,0.595-2.683c-3.412-5.356-9.237-8.554-15.582-8.554c-6.373,0-12.21,3.22-15.618,8.612 C33.828,31.02,34.099,32.22,35.006,32.792z"},{"datad":"M57.932,70.393h-5.314V58.5h23.812c0-3.288-2.621-5.946-5.946-5.946H52.618v-5.291 c0-1.982,2.643-3.303,2.671-7.302c0.021-2.92-2.366-5.286-5.286-5.286c-2.919,0-5.261,2.366-5.286,5.286 c-0.028,3.337,2.615,5.32,2.615,7.302v5.291H29.519c-3.325,0-5.946,2.658-5.946,5.946h23.759v11.893h-5.257 c-1.577,0-3.306,1.531-3.306,3.309L50.003,77l11.229-3.299C61.233,71.924,59.508,70.393,57.932,70.393z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (148, 'Gas, stun grenades', '<rect x="22.912" y="51.733" transform="matrix(-0.8953 0.4454 -0.4454 -0.8953 78.6335 102.1719)" fill="#FFFFFF" width="8.798" height="17.185"/> <path fill="#FFFFFF" d="M59.53,26.858c-0.02,0-1.98-0.014-3.13,0l0,0c-6.377,0.823-12.67,4.337-15.21,10.278 c-0.421,0.983-3.649,9.197-4.964,12.544l-6.093,3.082l5.344,10.566l5.572-1.82c2.05,1.825,5.469,3.054,7.744,3.054h0.964v6.337 h19.237V57.673c3.516-2.895,5.442-7.616,5.442-13.39C74.437,35.659,68.152,26.858,59.53,26.858z M47.833,49.484 c-3.367,0-6.095-2.729-6.095-6.097c0-3.367,2.729-6.096,6.095-6.096c3.366,0,6.096,2.729,6.096,6.096 C53.929,46.755,51.199,49.484,47.833,49.484z"/>', 'gas-2', '[{"datad":"M59.53,26.858c-0.02,0-1.98-0.014-3.13,0l0,0c-6.377,0.823-12.67,4.337-15.21,10.278 c-0.421,0.983-3.649,9.197-4.964,12.544l-6.093,3.082l5.344,10.566l5.572-1.82c2.05,1.825,5.469,3.054,7.744,3.054h0.964v6.337 h19.237V57.673c3.516-2.895,5.442-7.616,5.442-13.39C74.437,35.659,68.152,26.858,59.53,26.858z M47.833,49.484 c-3.367,0-6.095-2.729-6.095-6.097c0-3.367,2.729-6.096,6.095-6.096c3.366,0,6.096,2.729,6.096,6.096 C53.929,46.755,51.199,49.484,47.833,49.484z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (147, 'Ship, Warship', '<path fill="#FFFFFF" d="M75.651,66.259H65.561c0.015-2.438-1.138-7.188-0.549-10.713c0.291-1.742,1.431-3.742,2.152-5.382 c0.817-1.862,2.137-3.959,2.153-5.383c0.005-0.732-1.611-1.818-3.901-2.98v-7.76c0-1.821-1.48-3.3-3.301-3.3h-1.129v-2.808 c0-1.82-1.48-3.3-3.301-3.3h-3.321v-4.345h-8.847v4.345h-3.321c-1.82,0-3.3,1.479-3.3,3.3v2.809h-1.128 c-1.821,0-3.302,1.479-3.302,3.3V41.8c-2.288,1.163-3.906,2.249-3.899,2.98c0.015,1.424,1.334,3.521,2.152,5.383 c0.721,1.64,1.861,3.64,2.152,5.382c0.589,3.524-0.563,8.275-0.549,10.713h-9.974c-0.674,0-1.221,0.546-1.221,1.222 c0,0.677,0.547,1.223,1.221,1.223h12.1c3.789,1.698,12.025,4.368,13.493,4.604c1.468-0.235,9.705-2.905,13.494-4.604H75.65 c0.676,0,1.222-0.546,1.222-1.223C76.872,66.805,76.327,66.259,75.651,66.259z M41.339,27.932c0-0.472,0.384-0.856,0.856-0.856 h15.49c0.473,0,0.856,0.384,0.856,0.856v2.809H41.339V27.932z M36.909,34.04c0-0.473,0.384-0.856,0.858-0.856h24.347 c0.474,0,0.858,0.383,0.858,0.856v6.597c-5.354-2.4-12.266-4.748-13.032-4.738c-0.766-0.01-7.678,2.339-13.032,4.739V34.04z"/> <path fill="#FFFFFF" d="M63.338,79.463H36.662c-0.675,0-1.221-0.547-1.221-1.222c0-0.676,0.546-1.224,1.221-1.224h26.676 c0.675,0,1.222,0.548,1.222,1.224C64.56,78.916,64.013,79.463,63.338,79.463z"/>', 'ship-2', '[{"datad":"M75.651,66.259H65.561c0.015-2.438-1.138-7.188-0.549-10.713c0.291-1.742,1.431-3.742,2.152-5.382 c0.817-1.862,2.137-3.959,2.153-5.383c0.005-0.732-1.611-1.818-3.901-2.98v-7.76c0-1.821-1.48-3.3-3.301-3.3h-1.129v-2.808 c0-1.82-1.48-3.3-3.301-3.3h-3.321v-4.345h-8.847v4.345h-3.321c-1.82,0-3.3,1.479-3.3,3.3v2.809h-1.128 c-1.821,0-3.302,1.479-3.302,3.3V41.8c-2.288,1.163-3.906,2.249-3.899,2.98c0.015,1.424,1.334,3.521,2.152,5.383 c0.721,1.64,1.861,3.64,2.152,5.382c0.589,3.524-0.563,8.275-0.549,10.713h-9.974c-0.674,0-1.221,0.546-1.221,1.222 c0,0.677,0.547,1.223,1.221,1.223h12.1c3.789,1.698,12.025,4.368,13.493,4.604c1.468-0.235,9.705-2.905,13.494-4.604H75.65 c0.676,0,1.222-0.546,1.222-1.223C76.872,66.805,76.327,66.259,75.651,66.259z M41.339,27.932c0-0.472,0.384-0.856,0.856-0.856 h15.49c0.473,0,0.856,0.384,0.856,0.856v2.809H41.339V27.932z M36.909,34.04c0-0.473,0.384-0.856,0.858-0.856h24.347 c0.474,0,0.858,0.383,0.858,0.856v6.597c-5.354-2.4-12.266-4.748-13.032-4.738c-0.766-0.01-7.678,2.339-13.032,4.739V34.04z"},{"datad":"M63.338,79.463H36.662c-0.675,0-1.221-0.547-1.221-1.222c0-0.676,0.546-1.224,1.221-1.224h26.676 c0.675,0,1.222,0.548,1.222,1.224C64.56,78.916,64.013,79.463,63.338,79.463z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (146, 'Nuke, radioctive materials', '<path fill="#FFFFFF" d="M41.186,47.966c0.296-2.593,1.704-4.889,3.704-6.296c0.444-0.296,0.592-0.815,0.296-1.259l-9.407-15.703 c-0.296-0.444-0.814-0.593-1.259-0.297c-7.852,4.963-13.185,13.629-13.481,23.556c0,0.518,0.371,0.888,0.889,0.888h18.295 C40.741,48.854,41.111,48.483,41.186,47.966z"/> <path fill="#FFFFFF" d="M54.666,57.447c-0.296-0.445-0.814-0.594-1.333-0.371c-1.037,0.445-2.148,0.666-3.333,0.666 c-1.333,0-2.667-0.295-3.778-0.814c-0.444-0.223-1.037-0.074-1.259,0.371l-9.186,15.703c-0.222,0.443-0.074,1.037,0.371,1.26 c4.148,2.295,8.889,3.555,13.926,3.555c4.963,0,9.629-1.26,13.703-3.48c0.444-0.223,0.593-0.816,0.296-1.26L54.666,57.447z"/> <path fill="#FFFFFF" d="M65.333,24.336c-0.445-0.296-1.037-0.148-1.26,0.297l-9.629,15.555c-0.297,0.444-0.074,0.963,0.297,1.259 c2.222,1.408,3.777,3.778,4.073,6.593c0.074,0.519,0.444,0.889,0.963,0.889h18.296c0.519,0,0.889-0.445,0.889-0.889 C78.666,37.966,73.259,29.299,65.333,24.336z"/> <path fill="#FFFFFF" d="M50,54.188c2.889,0,5.259-2.371,5.259-5.259c0-2.889-2.37-5.26-5.259-5.26s-5.259,2.371-5.259,5.26 C44.741,51.816,47.111,54.188,50,54.188z M50,45.966c1.63,0,2.889,1.259,2.889,2.888c0,1.63-1.259,2.888-2.889,2.888 s-2.889-1.258-2.889-2.888C47.111,47.225,48.37,45.966,50,45.966z"/>', 'nuke-2', '[{"datad":"M41.186,47.966c0.296-2.593,1.704-4.889,3.704-6.296c0.444-0.296,0.592-0.815,0.296-1.259l-9.407-15.703 c-0.296-0.444-0.814-0.593-1.259-0.297c-7.852,4.963-13.185,13.629-13.481,23.556c0,0.518,0.371,0.888,0.889,0.888h18.295 C40.741,48.854,41.111,48.483,41.186,47.966z"},{"datad":"M54.666,57.447c-0.296-0.445-0.814-0.594-1.333-0.371c-1.037,0.445-2.148,0.666-3.333,0.666 c-1.333,0-2.667-0.295-3.778-0.814c-0.444-0.223-1.037-0.074-1.259,0.371l-9.186,15.703c-0.222,0.443-0.074,1.037,0.371,1.26 c4.148,2.295,8.889,3.555,13.926,3.555c4.963,0,9.629-1.26,13.703-3.48c0.444-0.223,0.593-0.816,0.296-1.26L54.666,57.447z"},{"datad":"M65.333,24.336c-0.445-0.296-1.037-0.148-1.26,0.297l-9.629,15.555c-0.297,0.444-0.074,0.963,0.297,1.259 c2.222,1.408,3.777,3.778,4.073,6.593c0.074,0.519,0.444,0.889,0.963,0.889h18.296c0.519,0,0.889-0.445,0.889-0.889 C78.666,37.966,73.259,29.299,65.333,24.336z"},{"datad":"M50,54.188c2.889,0,5.259-2.371,5.259-5.259c0-2.889-2.37-5.26-5.259-5.26s-5.259,2.371-5.259,5.26 C44.741,51.816,47.111,54.188,50,54.188z M50,45.966c1.63,0,2.889,1.259,2.889,2.888c0,1.63-1.259,2.888-2.889,2.888 s-2.889-1.258-2.889-2.888C47.111,47.225,48.37,45.966,50,45.966z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (145, 'Thugs, people in masks', '<path fill="#FFFFFF" d="M65.763,52.317c-0.654-0.559-1.483-0.748-2.274-0.639c0,0,0.025-13.918,0.025-14.011 c0-7.779-6.313-14.084-14.082-14.084c-7.77,0-14.063,6.306-14.063,14.084c0,0.147-0.046,13.979-0.046,13.979 c-0.753-0.069-1.444,0.144-2.066,0.671c-1.202,1.022-1.349,2.807-0.367,4.025l16.68,20.075l16.677-20.291 C67.064,54.92,66.901,53.286,65.763,52.317z M39.663,40.341c0-1.093,0.881-1.974,1.974-1.974h4.44c1.092,0,1.974,0.881,1.974,1.974 c0,1.091-0.882,1.973-1.974,1.973h-4.44C40.544,42.314,39.663,41.432,39.663,40.341z M49.449,54.23 c-1.949,0-3.529-1.581-3.529-3.527c0-1.947,1.581-3.527,3.529-3.527c1.948,0,3.531,1.581,3.531,3.527 C52.979,52.649,51.396,54.23,49.449,54.23z M57.097,42.314h-4.441c-1.092,0-1.972-0.882-1.972-1.973 c0-1.093,0.88-1.974,1.972-1.974h4.441c1.093,0,1.975,0.881,1.975,1.974C59.071,41.432,58.189,42.314,57.097,42.314z"/>', 'thug-2', '[{"datad":"M65.763,52.317c-0.654-0.559-1.483-0.748-2.274-0.639c0,0,0.025-13.918,0.025-14.011 c0-7.779-6.313-14.084-14.082-14.084c-7.77,0-14.063,6.306-14.063,14.084c0,0.147-0.046,13.979-0.046,13.979 c-0.753-0.069-1.444,0.144-2.066,0.671c-1.202,1.022-1.349,2.807-0.367,4.025l16.68,20.075l16.677-20.291 C67.064,54.92,66.901,53.286,65.763,52.317z M39.663,40.341c0-1.093,0.881-1.974,1.974-1.974h4.44c1.092,0,1.974,0.881,1.974,1.974 c0,1.091-0.882,1.973-1.974,1.973h-4.44C40.544,42.314,39.663,41.432,39.663,40.341z M49.449,54.23 c-1.949,0-3.529-1.581-3.529-3.527c0-1.947,1.581-3.527,3.529-3.527c1.948,0,3.531,1.581,3.531,3.527 C52.979,52.649,51.396,54.23,49.449,54.23z M57.097,42.314h-4.441c-1.092,0-1.972-0.882-1.972-1.973 c0-1.093,0.88-1.974,1.972-1.974h4.441c1.093,0,1.975,0.881,1.975,1.974C59.071,41.432,58.189,42.314,57.097,42.314z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (144, 'Tanks, APCs, armored vehicles', '<path fill="#FFFFFF" d="M61.082,47.179H31.729c-4.722,0-8.562,3.841-8.562,8.563c0,4.719,3.84,8.56,8.562,8.56h29.354 c4.723,0,8.563-3.841,8.563-8.56C69.645,51.02,65.805,47.179,61.082,47.179z M31.729,59.409c-2.028,0-3.669-1.642-3.669-3.667 c0-2.028,1.641-3.671,3.669-3.671s3.669,1.643,3.669,3.671C35.397,57.768,33.757,59.409,31.729,59.409z M41.513,59.409 c-2.028,0-3.669-1.642-3.669-3.667c0-2.028,1.641-3.671,3.669-3.671s3.669,1.643,3.669,3.671 C45.183,57.768,43.542,59.409,41.513,59.409z M51.298,59.409c-2.028,0-3.669-1.642-3.669-3.667c0-2.028,1.641-3.671,3.669-3.671 s3.671,1.643,3.671,3.671C54.969,57.768,53.326,59.409,51.298,59.409z M61.082,59.409c-2.028,0-3.667-1.642-3.667-3.667 c0-2.028,1.639-3.671,3.667-3.671s3.671,1.643,3.671,3.671C64.753,57.768,63.11,59.409,61.082,59.409z"/> <rect x="59.86" y="37.393" fill="#FFFFFF" width="17.123" height="4.893"/> <polygon fill="#FFFFFF" points="54.969,44.732 57.415,42.286 57.415,37.393 54.969,34.947 37.844,34.947 35.397,37.393 35.397,42.286 37.844,44.732 "/>', 'heavy-2', '[{"datad":"M61.082,47.179H31.729c-4.722,0-8.562,3.841-8.562,8.563c0,4.719,3.84,8.56,8.562,8.56h29.354 c4.723,0,8.563-3.841,8.563-8.56C69.645,51.02,65.805,47.179,61.082,47.179z M31.729,59.409c-2.028,0-3.669-1.642-3.669-3.667 c0-2.028,1.641-3.671,3.669-3.671s3.669,1.643,3.669,3.671C35.397,57.768,33.757,59.409,31.729,59.409z M41.513,59.409 c-2.028,0-3.669-1.642-3.669-3.667c0-2.028,1.641-3.671,3.669-3.671s3.669,1.643,3.669,3.671 C45.183,57.768,43.542,59.409,41.513,59.409z M51.298,59.409c-2.028,0-3.669-1.642-3.669-3.667c0-2.028,1.641-3.671,3.669-3.671 s3.671,1.643,3.671,3.671C54.969,57.768,53.326,59.409,51.298,59.409z M61.082,59.409c-2.028,0-3.667-1.642-3.667-3.667 c0-2.028,1.639-3.671,3.667-3.671s3.671,1.643,3.671,3.671C64.753,57.768,63.11,59.409,61.082,59.409z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (143, 'Stop, road block', '<path fill="#FFFFFF" d="M50,22.199c-15.286,0-27.676,12.39-27.676,27.676c0,15.285,12.391,27.677,27.676,27.677 c15.285,0,27.677-12.392,27.677-27.677C77.677,34.588,65.285,22.199,50,22.199z M68.182,53.154c0,1.055-0.855,1.91-1.91,1.91 H33.458c-1.055,0-1.909-0.855-1.909-1.91v-7.136c0-1.056,0.854-1.91,1.909-1.91h32.814c1.055,0,1.91,0.854,1.91,1.91V53.154z"/>', 'stop-2', '[{"datad":"M50,22.199c-15.286,0-27.676,12.39-27.676,27.676c0,15.285,12.391,27.677,27.676,27.677 c15.285,0,27.677-12.392,27.677-27.677C77.677,34.588,65.285,22.199,50,22.199z M68.182,53.154c0,1.055-0.855,1.91-1.91,1.91 H33.458c-1.055,0-1.909-0.855-1.909-1.91v-7.136c0-1.056,0.854-1.91,1.909-1.91h32.814c1.055,0,1.91,0.854,1.91,1.91V53.154z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (142, 'Speech, statement', '<path fill="none" d="M63.215,36.777c-1.128,0.185-1.364,4.603-0.538,9.848c0.844,5.26,2.429,9.393,3.558,9.208 c1.13-0.186,1.366-4.587,0.541-9.848C65.947,40.723,64.346,36.608,63.215,36.777z"/> <path fill="#FFFFFF" d="M75.226,44.4c-1.722-10.877-6.545-19.173-10.741-18.498c-1.857,0.285-3.29,2.258-4.168,5.294 c-1.82,1.618-4.603,3.304-7.771,4.906l1.734,3.541l5.396-2.647c-0.152,0.743-0.27,1.585-0.337,2.498 c0.017,2.309,0.219,4.771,0.623,7.317c0.439,2.799,1.08,5.43,1.873,7.757c0.271,0.556,0.558,1.046,0.859,1.467l-7.282-0.69 l-0.304,3.236c3.658,0.423,7.046,1.18,9.661,2.429c1.854,2.968,3.964,4.64,5.95,4.317C74.922,64.669,76.944,55.293,75.226,44.4z M66.234,55.832c-1.129,0.186-2.714-3.947-3.558-9.208c-0.826-5.245-0.59-9.663,0.538-9.848c1.131-0.168,2.732,3.946,3.561,9.208 C67.601,51.245,67.364,55.646,66.234,55.832z"/> <path fill="#FFFFFF" d="M51.16,36.778l-1.552,0.742c-7.874,3.642-16.913,6.661-19.426,7.504c-0.033,0.017-0.05,0.017-0.084,0.017 c-3.473,0.54-5.936,3.524-5.936,6.93c0,0.354,0.034,0.726,0.084,1.097c0.608,3.777,4.131,6.39,7.925,5.851l1.603,9.26 c0.286,0.808,1.028,1.938,2.714,1.651l3.339-0.287c1.686-0.304,1.939-1.029,1.653-2.716l-1.4-8.582 c3.996-0.188,8.853-0.256,13.457,0.167c0.521,0.051,1.063,0.103,1.567,0.169l0.304-3.236l-1.567-0.152L43.47,54.213 c0,0,0,0-0.018,0.001c-1.096,0.085-2.176-0.54-2.9-1.584c-0.438-0.625-0.758-1.4-0.894-2.261c-0.05-0.303-0.066-0.588-0.066-0.875 c0-0.456,0.05-0.894,0.167-1.299c0.355-1.399,1.282-2.445,2.497-2.648l9.106-4.47l2.917-1.433l-1.736-3.541 C52.088,36.323,51.631,36.56,51.16,36.778z"/>', 'speech-2', '[{"datad":"M63.215,36.777c-1.128,0.185-1.364,4.603-0.538,9.848c0.844,5.26,2.429,9.393,3.558,9.208 c1.13-0.186,1.366-4.587,0.541-9.848C65.947,40.723,64.346,36.608,63.215,36.777z"},{"datad":"M75.226,44.4c-1.722-10.877-6.545-19.173-10.741-18.498c-1.857,0.285-3.29,2.258-4.168,5.294 c-1.82,1.618-4.603,3.304-7.771,4.906l1.734,3.541l5.396-2.647c-0.152,0.743-0.27,1.585-0.337,2.498 c0.017,2.309,0.219,4.771,0.623,7.317c0.439,2.799,1.08,5.43,1.873,7.757c0.271,0.556,0.558,1.046,0.859,1.467l-7.282-0.69 l-0.304,3.236c3.658,0.423,7.046,1.18,9.661,2.429c1.854,2.968,3.964,4.64,5.95,4.317C74.922,64.669,76.944,55.293,75.226,44.4z M66.234,55.832c-1.129,0.186-2.714-3.947-3.558-9.208c-0.826-5.245-0.59-9.663,0.538-9.848c1.131-0.168,2.732,3.946,3.561,9.208 C67.601,51.245,67.364,55.646,66.234,55.832z"},{"datad":"M51.16,36.778l-1.552,0.742c-7.874,3.642-16.913,6.661-19.426,7.504c-0.033,0.017-0.05,0.017-0.084,0.017 c-3.473,0.54-5.936,3.524-5.936,6.93c0,0.354,0.034,0.726,0.084,1.097c0.608,3.777,4.131,6.39,7.925,5.851l1.603,9.26 c0.286,0.808,1.028,1.938,2.714,1.651l3.339-0.287c1.686-0.304,1.939-1.029,1.653-2.716l-1.4-8.582 c3.996-0.188,8.853-0.256,13.457,0.167c0.521,0.051,1.063,0.103,1.567,0.169l0.304-3.236l-1.567-0.152L43.47,54.213 c0,0,0,0-0.018,0.001c-1.096,0.085-2.176-0.54-2.9-1.584c-0.438-0.625-0.758-1.4-0.894-2.261c-0.05-0.303-0.066-0.588-0.066-0.875 c0-0.456,0.05-0.894,0.167-1.299c0.355-1.399,1.282-2.445,2.497-2.648l9.106-4.47l2.917-1.433l-1.736-3.541 C52.088,36.323,51.631,36.56,51.16,36.778z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (141, 'Gun shooting', '<path fill="#FFFFFF" d="M74.667,41.78v-4.892c0-0.605-0.495-1.097-1.1-1.097H32.033c-0.605,0-1.099,0.492-1.099,1.097v4.892 c0,0.487,0.32,0.904,0.761,1.045h-0.103c-0.604,0-1.097,0.493-1.097,1.1v1.428c0,0.604,0.494,1.096,1.097,1.096h0.084 c0.355,0.245,3.887,2.784,3.103,6.925c-0.825,4.354-9.889,14.835-3.625,14.835h10.053c0,0,2.424-8.032,4.761-14.331v0.037h10.96 c0.067,0.006,0.214,0.012,0.411,0.012c0.392,0,0.978-0.026,1.573-0.22c0.296-0.094,0.601-0.235,0.876-0.464 c0.274-0.229,0.51-0.563,0.608-0.969c0.066-0.269,0.256-0.831,0.494-1.478c0.358-0.977,0.831-2.179,1.216-3.131 c0.196-0.492,0.37-0.916,0.491-1.217h10.969c0.604,0,1.098-0.492,1.098-1.096v-1.428c0-0.524-0.369-0.964-0.859-1.071 C74.295,42.742,74.667,42.302,74.667,41.78z M60.371,48.158c-0.281,0.72-0.575,1.48-0.82,2.146 c-0.246,0.669-0.441,1.233-0.538,1.624c-0.026,0.093-0.06,0.146-0.145,0.217c-0.124,0.108-0.384,0.22-0.683,0.279 c-0.296,0.058-11.69,0.061-11.69,0.061c1.292-3.346,2.496-5.92,3.173-6.015c0.05-0.006,0.086-0.016,0.108-0.022h1.207 c-0.141,0.674-0.502,3.159,1.578,4.391c2.419,1.429,0.606-0.826,0.164-1.485c-0.425-0.642-0.802-2.118-0.11-2.906h8.441 C60.863,46.923,60.622,47.524,60.371,48.158z"/>', 'gun-2', '[{"datad":"M74.667,41.78v-4.892c0-0.605-0.495-1.097-1.1-1.097H32.033c-0.605,0-1.099,0.492-1.099,1.097v4.892 c0,0.487,0.32,0.904,0.761,1.045h-0.103c-0.604,0-1.097,0.493-1.097,1.1v1.428c0,0.604,0.494,1.096,1.097,1.096h0.084 c0.355,0.245,3.887,2.784,3.103,6.925c-0.825,4.354-9.889,14.835-3.625,14.835h10.053c0,0,2.424-8.032,4.761-14.331v0.037h10.96 c0.067,0.006,0.214,0.012,0.411,0.012c0.392,0,0.978-0.026,1.573-0.22c0.296-0.094,0.601-0.235,0.876-0.464 c0.274-0.229,0.51-0.563,0.608-0.969c0.066-0.269,0.256-0.831,0.494-1.478c0.358-0.977,0.831-2.179,1.216-3.131 c0.196-0.492,0.37-0.916,0.491-1.217h10.969c0.604,0,1.098-0.492,1.098-1.096v-1.428c0-0.524-0.369-0.964-0.859-1.071 C74.295,42.742,74.667,42.302,74.667,41.78z M60.371,48.158c-0.281,0.72-0.575,1.48-0.82,2.146 c-0.246,0.669-0.441,1.233-0.538,1.624c-0.026,0.093-0.06,0.146-0.145,0.217c-0.124,0.108-0.384,0.22-0.683,0.279 c-0.296,0.058-11.69,0.061-11.69,0.061c1.292-3.346,2.496-5.92,3.173-6.015c0.05-0.006,0.086-0.016,0.108-0.022h1.207 c-0.141,0.674-0.502,3.159,1.578,4.391c2.419,1.429,0.606-0.826,0.164-1.485c-0.425-0.642-0.802-2.118-0.11-2.906h8.441 C60.863,46.923,60.622,47.524,60.371,48.158z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (140, 'Police', '<path fill="#FFFFFF" d="M74.628,39.639c-1.424,0-2.589,1.054-2.788,2.423l-13.538-1.967l-6.018-12.194 c0.979-0.445,1.665-1.427,1.665-2.572c0-1.563-1.267-2.829-2.829-2.829c-1.563,0-2.83,1.267-2.83,2.829 c0,1.07,0.601,1.99,1.477,2.47l-6.068,12.296l-13.539,1.967c-0.198-1.369-1.364-2.423-2.789-2.423c-1.562,0-2.828,1.266-2.828,2.828 c0,1.563,1.266,2.829,2.828,2.829c0.794,0,1.51-0.329,2.023-0.856l9.791,9.543l-2.321,13.533c-0.192-0.042-0.391-0.065-0.596-0.065 c-1.563,0-2.828,1.267-2.828,2.829c0,1.562,1.266,2.828,2.828,2.828s2.828-1.267,2.828-2.828c0-0.467-0.123-0.899-0.323-1.286 L51,62.566l12.11,6.366c-0.207,0.394-0.336,0.835-0.336,1.311c0,1.563,1.266,2.828,2.829,2.828c1.563,0,2.828-1.266,2.828-2.828 s-1.266-2.828-2.828-2.828c-0.164,0-0.322,0.021-0.478,0.048l-2.312-13.479l9.789-9.543c0.514,0.527,1.229,0.856,2.023,0.856 c1.563,0,2.829-1.267,2.829-2.829C77.458,40.905,76.191,39.639,74.628,39.639z"/>', 'police-2', '[{"datad":"M74.628,39.639c-1.424,0-2.589,1.054-2.788,2.423l-13.538-1.967l-6.018-12.194 c0.979-0.445,1.665-1.427,1.665-2.572c0-1.563-1.267-2.829-2.829-2.829c-1.563,0-2.83,1.267-2.83,2.829 c0,1.07,0.601,1.99,1.477,2.47l-6.068,12.296l-13.539,1.967c-0.198-1.369-1.364-2.423-2.789-2.423c-1.562,0-2.828,1.266-2.828,2.828 c0,1.563,1.266,2.829,2.828,2.829c0.794,0,1.51-0.329,2.023-0.856l9.791,9.543l-2.321,13.533c-0.192-0.042-0.391-0.065-0.596-0.065 c-1.563,0-2.828,1.267-2.828,2.829c0,1.562,1.266,2.828,2.828,2.828s2.828-1.267,2.828-2.828c0-0.467-0.123-0.899-0.323-1.286 L51,62.566l12.11,6.366c-0.207,0.394-0.336,0.835-0.336,1.311c0,1.563,1.266,2.828,2.829,2.828c1.563,0,2.828-1.266,2.828-2.828 s-1.266-2.828-2.828-2.828c-0.164,0-0.322,0.021-0.478,0.048l-2.312-13.479l9.789-9.543c0.514,0.527,1.229,0.856,2.023,0.856 c1.563,0,2.829-1.267,2.829-2.829C77.458,40.905,76.191,39.639,74.628,39.639z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (139, 'Firebombs', '<path fill="#FFFFFF" d="M74.544,36.852c-0.391-4.3-0.957-9.115-3.829-11.034c-2.872-1.919-5.631-0.113-6.701,0.959 c-0.309,0.308-0.534,0.616-0.71,0.917c-0.245-0.129-0.446-0.206-0.564-0.196c-0.812,0.062-1.056,1.28-1.056,1.28 s-0.505-0.312-0.879,0.135c-0.375,0.446,0.197,0.672-0.303,1.269c-0.499,0.595-2.686,2.299-4.185,4.086 c-0.749,0.893-1.076,1.284-2.576,3.071c-1.499,1.788-2.627,1.93-4.816,2.12c-2.188,0.189-3.795,1.809-4.295,2.404 c-0.5,0.596-15.113,18.033-16.12,19.219c-1.401,1.649-1.276,2.731-0.088,3.732c0.307,0.259,8.586,7.236,8.893,7.495 c1.189,1.002,2.024,1.057,3.654-0.727c1.049-1.148,15.62-18.622,16.12-19.219c0.501-0.596,1.815-2.461,1.626-4.655 c-0.189-2.194-0.243-3.333,1.257-5.12c1.499-1.788,1.827-2.179,2.576-3.071c1.498-1.787,2.797-4.239,3.297-4.834 s0.82-0.07,1.194-0.516c0.375-0.446-0.019-0.891-0.019-0.891s1.155-0.45,1.078-1.262c-0.016-0.153-0.181-0.407-0.444-0.718 c0.401-0.365,0.773-0.575,1.146-0.202c0.958,0.959-1.435,6.237-1.914,12.954c-0.367,5.154-2.394,11.036-0.479,11.036 c1.974,0,1.914,0.479,1.914,1.438s2.394,1.919,3.829,0.959c1.437-0.959,3.351-0.881,3.351-2.398 C75.5,52.205,75.021,42.129,74.544,36.852z"/>', 'molotov-2', '[{"datad":"M74.544,36.852c-0.391-4.3-0.957-9.115-3.829-11.034c-2.872-1.919-5.631-0.113-6.701,0.959 c-0.309,0.308-0.534,0.616-0.71,0.917c-0.245-0.129-0.446-0.206-0.564-0.196c-0.812,0.062-1.056,1.28-1.056,1.28 s-0.505-0.312-0.879,0.135c-0.375,0.446,0.197,0.672-0.303,1.269c-0.499,0.595-2.686,2.299-4.185,4.086 c-0.749,0.893-1.076,1.284-2.576,3.071c-1.499,1.788-2.627,1.93-4.816,2.12c-2.188,0.189-3.795,1.809-4.295,2.404 c-0.5,0.596-15.113,18.033-16.12,19.219c-1.401,1.649-1.276,2.731-0.088,3.732c0.307,0.259,8.586,7.236,8.893,7.495 c1.189,1.002,2.024,1.057,3.654-0.727c1.049-1.148,15.62-18.622,16.12-19.219c0.501-0.596,1.815-2.461,1.626-4.655 c-0.189-2.194-0.243-3.333,1.257-5.12c1.499-1.788,1.827-2.179,2.576-3.071c1.498-1.787,2.797-4.239,3.297-4.834 s0.82-0.07,1.194-0.516c0.375-0.446-0.019-0.891-0.019-0.891s1.155-0.45,1.078-1.262c-0.016-0.153-0.181-0.407-0.444-0.718 c0.401-0.365,0.773-0.575,1.146-0.202c0.958,0.959-1.435,6.237-1.914,12.954c-0.367,5.154-2.394,11.036-0.479,11.036 c1.974,0,1.914,0.479,1.914,1.438s2.394,1.919,3.829,0.959c1.437-0.959,3.351-0.881,3.351-2.398 C75.5,52.205,75.021,42.129,74.544,36.852z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (138, 'Injures/medicine', '<polygon fill="#010101" points="50.201,44.588 50.193,44.595 50.193,44.588 "/> <polygon fill="#010101" points="50.201,44.588 50.193,44.595 50.193,44.588 "/> <path fill="#FFFFFF" d="M67.261,39.531h-6.965v-6.937c0-1.787-1.449-3.236-3.237-3.236h-14.19c-0.106-0.005-0.212-0.005-0.318,0 c-1.662,0.164-2.927,1.566-2.919,3.236v6.937h-6.936c-1.788,0-3.236,1.449-3.237,3.237v14.189c0,1.787,1.449,3.236,3.237,3.236 h6.936v6.967c0,1.787,1.449,3.236,3.237,3.236h14.19c1.788,0,3.237-1.449,3.237-3.236v-6.967h6.965c1.787,0,3.236-1.449,3.236-3.236 V42.768C70.497,40.98,69.048,39.531,67.261,39.531z M64.024,53.721h-6.966c-1.787,0-3.236,1.449-3.237,3.236v6.965h-7.716v-6.965 c0-1.787-1.449-3.236-3.237-3.236h-6.936v-7.716h6.936c1.788,0,3.237-1.449,3.237-3.237v-6.936h7.716v6.936 c0.001,1.788,1.45,3.237,3.237,3.237h6.966V53.721z"/>', 'medicine-2', '[{"datad":"M67.261,39.531h-6.965v-6.937c0-1.787-1.449-3.236-3.237-3.236h-14.19c-0.106-0.005-0.212-0.005-0.318,0 c-1.662,0.164-2.927,1.566-2.919,3.236v6.937h-6.936c-1.788,0-3.236,1.449-3.237,3.237v14.189c0,1.787,1.449,3.236,3.237,3.236 h6.936v6.967c0,1.787,1.449,3.236,3.237,3.236h14.19c1.788,0,3.237-1.449,3.237-3.236v-6.967h6.965c1.787,0,3.236-1.449,3.236-3.236 V42.768C70.497,40.98,69.048,39.531,67.261,39.531z M64.024,53.721h-6.966c-1.787,0-3.236,1.449-3.237,3.236v6.965h-7.716v-6.965 c0-1.787-1.449-3.236-3.237-3.236h-6.936v-7.716h6.936c1.788,0,3.237-1.449,3.237-3.237v-6.936h7.716v6.936 c0.001,1.788,1.45,3.237,3.237,3.237h6.966V53.721z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (137, 'Fires', '<path fill="#FFFFFF" d="M68.621,43.951c-8.432-1.103-11.11,3.58-11.11,4.96s-3.042-13.893,5.991-17.506 c-8.732,1.204-14.353,6.223-14.353,11.342c0-9.636,1.205-18.268,7.728-22.483c-16.159,1.205-40.651,49.384-12.948,53.6 c29.967,4.561,20.778-21.254,20.778-21.254S63.603,46.586,68.621,43.951z M46.614,70.354c-15.453-2.381-14.03-19.188-1.792-24.416 c-1.494,4.425-2.849,9.382,1.969,13.106c0-2.854,3.259-7.456,8.13-8.129c-2.408,4.817-1.443,10.047-0.729,10.335 c1.634,0.655,3.439,1.559,8.209-4.581C64.255,66.572,57.581,72.043,46.614,70.354z"/>', 'fires-2', '[{"datad":"M68.621,43.951c-8.432-1.103-11.11,3.58-11.11,4.96s-3.042-13.893,5.991-17.506 c-8.732,1.204-14.353,6.223-14.353,11.342c0-9.636,1.205-18.268,7.728-22.483c-16.159,1.205-40.651,49.384-12.948,53.6 c29.967,4.561,20.778-21.254,20.778-21.254S63.603,46.586,68.621,43.951z M46.614,70.354c-15.453-2.381-14.03-19.188-1.792-24.416 c-1.494,4.425-2.849,9.382,1.969,13.106c0-2.854,3.259-7.456,8.13-8.129c-2.408,4.817-1.443,10.047-0.729,10.335 c1.634,0.655,3.439,1.559,8.209-4.581C64.255,66.572,57.581,72.043,46.614,70.354z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (136, 'Stabbing attacks', '<polygon id="Sword_1_" fill="#FFFFFF" points="32.545,72.965 40.656,64.854 46.221,69.663 48.768,67.117 44.335,61.741 70.006,40.63 74.13,26.488 59.988,30.612 38.877,56.282 33.501,51.852 30.955,54.396 35.765,59.962 27.653,68.073 29.054,71.564 "/>', 'op-2', '[{"datad":"M32.545,72.965 40.656,64.854 46.221,69.663 48.768,67.117 44.335,61.741 70.006,40.63 74.13,26.488 59.988,30.612 38.877,56.282 33.501,51.852 30.955,54.396 35.765,59.962 27.653,68.073 29.054,71.564z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (135, 'Dead', '<path fill="#FFFFFF" d="M50.002,28.258c-14.21,0-23.209,8.519-23.209,18.962c0.134,4.257,1.249,8.429,3.259,12.184h5.828c0,0,4.135,0,5.173,12.346h4.271v-1.667c0-0.681,0.553-1.234,1.235-1.234c0.682,0,1.234,0.554,1.234,1.234v1.667h4.581v-1.667c0-0.681,0.553-1.234,1.234-1.234c0.681,0,1.235,0.554,1.235,1.234v1.667h4.097c1.038-12.346,5.174-12.346,5.174-12.346h5.827c2.01-3.755,3.125-7.927,3.259-12.184C73.212,36.764,64.212,28.258,50.002,28.258z M39.941,51.713c-2.769,0-5.012-2.242-5.012-5.011s2.244-5.012,5.012-5.012c2.768,0,5.012,2.244,5.012,5.012S42.709,51.713,39.941,51.713L39.941,51.713z M53.015,59.775c-0.63,1.001-5.309,0.938-6.049,0c-0.742-0.938,2-6.963,3.024-6.963C51.016,52.813,53.707,58.787,53.015,59.775z M60.25,51.713c-2.771,0-5.013-2.242-5.013-5.011s2.242-5.012,5.013-5.012c2.767,0,5.012,2.244,5.012,5.012S63.017,51.713,60.25,51.713L60.25,51.713z"/>', 'dead-2', '[{"datad":"M50.002,28.258c-14.21,0-23.209,8.519-23.209,18.962c0.134,4.257,1.249,8.429,3.259,12.184h5.828c0,0,4.135,0,5.173,12.346h4.271v-1.667c0-0.681,0.553-1.234,1.235-1.234c0.682,0,1.234,0.554,1.234,1.234v1.667h4.581v-1.667c0-0.681,0.553-1.234,1.234-1.234c0.681,0,1.235,0.554,1.235,1.234v1.667h4.097c1.038-12.346,5.174-12.346,5.174-12.346h5.827c2.01-3.755,3.125-7.927,3.259-12.184C73.212,36.764,64.212,28.258,50.002,28.258z M39.941,51.713c-2.769,0-5.012-2.242-5.012-5.011s2.244-5.012,5.012-5.012c2.768,0,5.012,2.244,5.012,5.012S42.709,51.713,39.941,51.713L39.941,51.713z M53.015,59.775c-0.63,1.001-5.309,0.938-6.049,0c-0.742-0.938,2-6.963,3.024-6.963C51.016,52.813,53.707,58.787,53.015,59.775z M60.25,51.713c-2.771,0-5.013-2.242-5.013-5.011s2.242-5.012,5.013-5.012c2.767,0,5.012,2.244,5.012,5.012S63.017,51.713,60.25,51.713L60.25,51.713z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (133, 'Camps, tents', '<path fill="#FFFFFF" d="M72.44,70.019L52.26,33.593c-0.438-0.789-1.271-1.281-2.178-1.283c-0.902,0-1.735,0.489-2.173,1.276 L27.563,70.011c-0.429,0.768-0.418,1.716,0.026,2.473c0.444,0.759,1.267,1.23,2.147,1.23h8.248c1.373,0,2.489-1.117,2.489-2.49 c0-1.372-1.117-2.489-2.489-2.489H33.98l16.094-28.813l15.965,28.813h-3.831c-1.372,0-2.489,1.117-2.489,2.49 s1.117,2.489,2.489,2.489h8.057c0.875,0,1.697-0.469,2.146-1.227C72.854,71.729,72.866,70.784,72.44,70.019z"/> <path fill="#FFFFFF" d="M45.205,30.775c0.438,0.797,1.274,1.292,2.184,1.292c0.416,0,0.831-0.106,1.198-0.308 c1.203-0.661,1.645-2.177,0.984-3.381l-1.92-3.494c-0.438-0.796-1.274-1.291-2.183-1.291c-0.416,0-0.83,0.106-1.198,0.308 c-0.583,0.321-1.007,0.849-1.192,1.487c-0.186,0.639-0.112,1.311,0.208,1.894L45.205,30.775z"/> <path fill="#FFFFFF" d="M50.001,62.523c-4.742,0-8.601,3.858-8.601,8.601c0,1.373,1.117,2.489,2.49,2.489 c1.373,0,2.489-1.116,2.489-2.489c0-1.997,1.624-3.621,3.621-3.621c1.996,0,3.621,1.624,3.621,3.621 c0,1.373,1.116,2.489,2.489,2.489c1.372,0,2.489-1.116,2.49-2.489C58.602,66.382,54.743,62.523,50.001,62.523z"/> <path fill="#FFFFFF" d="M52.298,31.759c0.366,0.202,0.78,0.309,1.197,0.309c0.908,0,1.745-0.495,2.185-1.292l1.919-3.493 c0.661-1.204,0.219-2.72-0.983-3.381c-0.366-0.202-0.781-0.31-1.199-0.31c-0.908,0-1.744,0.496-2.183,1.292l-1.919,3.493 c-0.32,0.583-0.394,1.256-0.208,1.895C51.292,30.911,51.716,31.439,52.298,31.759z"/>', 'camp-2', '[{"datad":"M72.44,70.019L52.26,33.593c-0.438-0.789-1.271-1.281-2.178-1.283c-0.902,0-1.735,0.489-2.173,1.276 L27.563,70.011c-0.429,0.768-0.418,1.716,0.026,2.473c0.444,0.759,1.267,1.23,2.147,1.23h8.248c1.373,0,2.489-1.117,2.489-2.49 c0-1.372-1.117-2.489-2.489-2.489H33.98l16.094-28.813l15.965,28.813h-3.831c-1.372,0-2.489,1.117-2.489,2.49 s1.117,2.489,2.489,2.489h8.057c0.875,0,1.697-0.469,2.146-1.227C72.854,71.729,72.866,70.784,72.44,70.019z"},{"datad":"M45.205,30.775c0.438,0.797,1.274,1.292,2.184,1.292c0.416,0,0.831-0.106,1.198-0.308 c1.203-0.661,1.645-2.177,0.984-3.381l-1.92-3.494c-0.438-0.796-1.274-1.291-2.183-1.291c-0.416,0-0.83,0.106-1.198,0.308 c-0.583,0.321-1.007,0.849-1.192,1.487c-0.186,0.639-0.112,1.311,0.208,1.894L45.205,30.775z"},{"datad":"M50.001,62.523c-4.742,0-8.601,3.858-8.601,8.601c0,1.373,1.117,2.489,2.49,2.489 c1.373,0,2.489-1.116,2.489-2.489c0-1.997,1.624-3.621,3.621-3.621c1.996,0,3.621,1.624,3.621,3.621 c0,1.373,1.116,2.489,2.489,2.489c1.372,0,2.489-1.116,2.49-2.489C58.602,66.382,54.743,62.523,50.001,62.523z"},{"datad":"M52.298,31.759c0.366,0.202,0.78,0.309,1.197,0.309c0.908,0,1.745-0.495,2.185-1.292l1.919-3.493 c0.661-1.204,0.219-2.72-0.983-3.381c-0.366-0.202-0.781-0.31-1.199-0.31c-0.908,0-1.744,0.496-2.183,1.292l-1.919,3.493 c-0.32,0.583-0.394,1.256-0.208,1.895C51.292,30.911,51.716,31.439,52.298,31.759z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (132, 'Helicopters', '<path fill="#FFFFFF" d="M51.797,37.341v-7.414h-3.38v7.414c-5.5,0.454-10.07,3.207-11.95,9.403 c-0.598,1.967-0.924,4.097-0.924,6.322c0,1.271,0.107,2.513,0.31,3.709c0.857,5.057,3.434,8.907,6.937,11.107v5.381H38.2 c-0.834,0-1.511,0.678-1.511,1.511c0,0.835,0.677,1.511,1.511,1.511h23.816c0.835,0,1.512-0.676,1.512-1.511 c0-0.833-0.677-1.511-1.512-1.511h-3.928v-5.836c3.179-2.271,5.498-5.969,6.287-10.735c0.193-1.171,0.296-2.385,0.296-3.626 c0-2.225-0.326-4.355-0.923-6.322C61.867,40.547,57.298,37.795,51.797,37.341z M38.839,55.4c-4.122-3.571,0-11.403,0-11.403h9.942 V55.4H38.839z M56.401,73.264H44.473v-4.492c1.735,0.772,3.634,1.193,5.633,1.193c2.256,0,4.389-0.526,6.295-1.504V73.264z M61.373,55.4H51.43V43.997h9.943C61.373,43.997,65.496,51.829,61.373,55.4z"/> <rect x="15.625" y="29.927" fill="#FFFFFF" width="32.025" height="2.983"/> <rect x="52.563" y="29.927" fill="#FFFFFF" width="31.937" height="2.983"/>', 'helicopter-2', '[{"datad":"M51.797,37.341v-7.414h-3.38v7.414c-5.5,0.454-10.07,3.207-11.95,9.403 c-0.598,1.967-0.924,4.097-0.924,6.322c0,1.271,0.107,2.513,0.31,3.709c0.857,5.057,3.434,8.907,6.937,11.107v5.381H38.2 c-0.834,0-1.511,0.678-1.511,1.511c0,0.835,0.677,1.511,1.511,1.511h23.816c0.835,0,1.512-0.676,1.512-1.511 c0-0.833-0.677-1.511-1.512-1.511h-3.928v-5.836c3.179-2.271,5.498-5.969,6.287-10.735c0.193-1.171,0.296-2.385,0.296-3.626 c0-2.225-0.326-4.355-0.923-6.322C61.867,40.547,57.298,37.795,51.797,37.341z M38.839,55.4c-4.122-3.571,0-11.403,0-11.403h9.942 V55.4H38.839z M56.401,73.264H44.473v-4.492c1.735,0.772,3.634,1.193,5.633,1.193c2.256,0,4.389-0.526,6.295-1.504V73.264z M61.373,55.4H51.43V43.997h9.943C61.373,43.997,65.496,51.829,61.373,55.4z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (131, 'Airplanes, jets', '<rect x="-59.3" y="49.005" fill="#010101" width="0.244" height="0.221"/> <polygon fill="#010101" points="-53.738,52.108 -53.746,52.115 -53.746,52.108 "/> <polygon fill="#010101" points="-53.738,52.108 -53.746,52.115 -53.746,52.108 "/> <path fill="#FFFFFF" d="M50.97,18.948h0.048v0.112h-0.001c0.578,1.421,1.604,4.576,1.604,7.408c0,2.83,0.174,3.277,0.174,3.896 s2.058,12.398,2.058,12.398l1.548,3.982v0.669l14.297,13.089h0.015c-0.003-1.48,0.37,5.29,0.37,5.29 c-0.097,0.064-0.275,0.011-0.366-0.02l-0.019,0.011c0,0.005,0,0.01,0,0.01l-0.019-0.015l-13.897-3.557v2.111l7.509,7.568 c0,0-0.63,1.126-0.965,1.552c-0.334,0.426-0.74,1.116-0.892,1.127c-0.152,0.01-22.963,0.011-23.116,0s-0.559-0.699-0.892-1.126 c-0.335-0.426-0.964-1.552-0.964-1.552l7.507-7.569v-2.11L31.07,65.78l-0.017,0.014c0,0,0-0.004,0-0.009l-0.019-0.01 c-0.091,0.028-0.269,0.083-0.366,0.019c0,0,0.372-6.771,0.37-5.29h0.015L45.35,47.415v-0.67l1.547-3.982c0,0,2.06-11.78,2.06-12.398 s0.172-1.066,0.172-3.896c0-2.832,1.025-5.987,1.604-7.408v-0.113h0.049"/>', 'airplane-2', '[{"datad":"M50.97,18.948h0.048v0.112h-0.001c0.578,1.421,1.604,4.576,1.604,7.408c0,2.83,0.174,3.277,0.174,3.896 s2.058,12.398,2.058,12.398l1.548,3.982v0.669l14.297,13.089h0.015c-0.003-1.48,0.37,5.29,0.37,5.29 c-0.097,0.064-0.275,0.011-0.366-0.02l-0.019,0.011c0,0.005,0,0.01,0,0.01l-0.019-0.015l-13.897-3.557v2.111l7.509,7.568 c0,0-0.63,1.126-0.965,1.552c-0.334,0.426-0.74,1.116-0.892,1.127c-0.152,0.01-22.963,0.011-23.116,0s-0.559-0.699-0.892-1.126 c-0.335-0.426-0.964-1.552-0.964-1.552l7.507-7.569v-2.11L31.07,65.78l-0.017,0.014c0,0,0-0.004,0-0.009l-0.019-0.01 c-0.091,0.028-0.269,0.083-0.366,0.019c0,0,0.372-6.771,0.37-5.29h0.015L45.35,47.415v-0.67l1.547-3.982c0,0,2.06-11.78,2.06-12.398 s0.172-1.066,0.172-3.896c0-2.832,1.025-5.987,1.604-7.408v-0.113h0.049"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (130, 'Artillery, MLRS', '<path fill="#FFFFFF" d="M77,28.616c0,0-3.494-0.602-8.312,4.216l2.047,2.048l2.047,2.048C77.602,32.109,77,28.616,77,28.616z"/> <rect x="66.852" y="35.294" transform="matrix(-0.7071 -0.7071 0.7071 -0.7071 93.6993 110.5516)" fill="#FFFFFF" width="5.791" height="1.149"/> <rect x="55.555" y="32.517" transform="matrix(-0.7071 -0.7071 0.7071 -0.7071 66.4361 121.8455)" fill="#FFFFFF" width="5.793" height="29.296"/> <polygon fill="#FFFFFF" points="50.699,49.857 44.354,50.82 41.85,53.324 44.541,56.016 "/> <polygon fill="#FFFFFF" points="52.252,63.726 54.756,61.221 55.719,54.878 49.561,61.034 "/> <path fill="#FFFFFF" d="M43.111,74.338c-0.506,0-1,0.053-1.477,0.147c-0.398-1.239-1.313-2.243-2.492-2.763 c0.081-0.126,0.155-0.26,0.237-0.385c4.217-6.469,10.181-11.057,10.181-11.057l-2.029-2.029l-0.207-0.207l-2.029-2.029 c0,0-4.18,5.434-10.186,9.596c-2.187,1.516-4.615,2.863-7.173,3.718c0,0-5.405-3.964-9.29,1.838c0,0-1.195-0.986-2.696-1.287 c6.409,11.898,18.974,19.991,33.432,20.009c0.779-0.4,1.293-1.074,1.293-1.846c0-0.771-0.51-1.451-1.29-1.854 c0.827-1.214,1.312-2.683,1.312-4.263C50.699,77.735,47.301,74.338,43.111,74.338z"/>', 'artillery-2', '[{"datad":"M77,28.616c0,0-3.494-0.602-8.312,4.216l2.047,2.048l2.047,2.048C77.602,32.109,77,28.616,77,28.616z"},{"datad":"M43.111,74.338c-0.506,0-1,0.053-1.477,0.147c-0.398-1.239-1.313-2.243-2.492-2.763 c0.081-0.126,0.155-0.26,0.237-0.385c4.217-6.469,10.181-11.057,10.181-11.057l-2.029-2.029l-0.207-0.207l-2.029-2.029 c0,0-4.18,5.434-10.186,9.596c-2.187,1.516-4.615,2.863-7.173,3.718c0,0-5.405-3.964-9.29,1.838c0,0-1.195-0.986-2.696-1.287 c6.409,11.898,18.974,19.991,33.432,20.009c0.779-0.4,1.293-1.074,1.293-1.846c0-0.771-0.51-1.451-1.29-1.854 c0.827-1.214,1.312-2.683,1.312-4.263C50.699,77.735,47.301,74.338,43.111,74.338z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (129, 'Road accidents, cars', '<path fill="#FFFFFF" d="M73.716,49.418c-0.204-1.99-0.204-1.703-0.954-3.857c-0.765-2.183-2.379-6.455-3.533-8.981 c-1.183-2.555-2.435-4.737-3.348-6.083c-0.906-1.344-1.147-1.459-2.003-1.831c-0.886-0.38-2.019-0.315-3.151-0.387 c-1.124-0.101-2.301-0.15-3.554-0.15h-14.27c-1.281,0-2.485,0.049-3.611,0.15c-1.139,0.071-2.3,0.007-3.157,0.386 c-0.87,0.371-1.118,0.487-2.003,1.832c-0.935,1.346-2.166,3.528-3.342,6.083c-1.168,2.527-2.782,6.799-3.532,8.981 c-0.757,2.155-0.785,1.869-0.962,3.858c-0.191,2.003-0.553,5.804-0.185,7.922c0.348,2.047,1.211,3.436,2.287,4.437 c1.068,0.995,2.428,1.511,4.105,1.547c-0.092,2.111-0.092,3.742,0,4.922c0.049,1.168-0.036,1.684,0.481,2.126 c0.523,0.424,1.862,0.444,2.676,0.479c0.8,0.051,1.664,0.028,2.102-0.286c0.411-0.357,0.291-0.63,0.375-1.84 c0.071-1.245,0.113-3.048,0.099-5.495h23.395c0.058,2.447,0.129,4.25,0.242,5.495c0.092,1.21-0.036,1.482,0.381,1.84 c0.433,0.314,1.27,0.337,2.096,0.286c0.793-0.035,2.131-0.056,2.676-0.479c0.496-0.442,0.411-0.958,0.481-2.126 c0.078-1.18,0.078-2.813,0-4.922c1.671-0.036,3.03-0.552,4.106-1.547c1.068-1.001,1.926-2.39,2.294-4.437 C74.234,55.222,73.915,51.421,73.716,49.418z M33.042,58.107c-2.478,0-4.488-2.032-4.488-4.536c0-2.506,2.01-4.538,4.488-4.538 c2.477,0,4.488,2.032,4.488,4.538C37.53,56.075,35.519,58.107,33.042,58.107z M56.384,57.238H43.635 c-1.332,0-2.411-0.735-2.411-1.642s1.08-1.642,2.411-1.642h12.749c1.332,0,2.411,0.735,2.411,1.642S57.716,57.238,56.384,57.238z M56.384,52.795H43.635c-1.332,0-2.411-0.734-2.411-1.639c0-0.905,1.08-1.638,2.411-1.638h12.749c1.332,0,2.411,0.733,2.411,1.638 C58.795,52.061,57.716,52.795,56.384,52.795z M58.426,44.208c-3.717,0.014-13.322,0.014-16.996,0 c-3.646-0.029-3.327,0.029-4.778-0.1c-1.466-0.13-3.178-0.251-3.915-0.673c-0.736-0.458-0.652-0.974-0.481-1.933 c0.177-0.981,0.792-2.433,1.437-3.864c0.623-1.46,1.536-3.607,2.195-4.731c0.623-1.13,0.927-1.517,1.62-1.932 c0.687-0.421,1.544-0.485,2.486-0.58c0.92-0.121,1.939-0.143,3.058-0.094h13.911c1.111-0.049,2.131-0.027,3.058,0.094 c0.92,0.093,1.776,0.157,2.484,0.58c0.688,0.416,0.963,0.802,1.622,1.932c0.658,1.124,1.557,3.271,2.2,4.731 c0.603,1.432,1.261,2.883,1.43,3.864c0.156,0.958,0.241,1.474-0.48,1.933c-0.757,0.422-2.436,0.544-3.914,0.673 C61.874,44.237,62.086,44.179,58.426,44.208z M66.973,58.107c-2.477,0-4.488-2.032-4.488-4.536c0-2.506,2.012-4.538,4.488-4.538 s4.488,2.032,4.488,4.538C71.461,56.075,69.45,58.107,66.973,58.107z"/>', 'car-2', '[{"datad":"M73.716,49.418c-0.204-1.99-0.204-1.703-0.954-3.857c-0.765-2.183-2.379-6.455-3.533-8.981 c-1.183-2.555-2.435-4.737-3.348-6.083c-0.906-1.344-1.147-1.459-2.003-1.831c-0.886-0.38-2.019-0.315-3.151-0.387 c-1.124-0.101-2.301-0.15-3.554-0.15h-14.27c-1.281,0-2.485,0.049-3.611,0.15c-1.139,0.071-2.3,0.007-3.157,0.386 c-0.87,0.371-1.118,0.487-2.003,1.832c-0.935,1.346-2.166,3.528-3.342,6.083c-1.168,2.527-2.782,6.799-3.532,8.981 c-0.757,2.155-0.785,1.869-0.962,3.858c-0.191,2.003-0.553,5.804-0.185,7.922c0.348,2.047,1.211,3.436,2.287,4.437 c1.068,0.995,2.428,1.511,4.105,1.547c-0.092,2.111-0.092,3.742,0,4.922c0.049,1.168-0.036,1.684,0.481,2.126 c0.523,0.424,1.862,0.444,2.676,0.479c0.8,0.051,1.664,0.028,2.102-0.286c0.411-0.357,0.291-0.63,0.375-1.84 c0.071-1.245,0.113-3.048,0.099-5.495h23.395c0.058,2.447,0.129,4.25,0.242,5.495c0.092,1.21-0.036,1.482,0.381,1.84 c0.433,0.314,1.27,0.337,2.096,0.286c0.793-0.035,2.131-0.056,2.676-0.479c0.496-0.442,0.411-0.958,0.481-2.126 c0.078-1.18,0.078-2.813,0-4.922c1.671-0.036,3.03-0.552,4.106-1.547c1.068-1.001,1.926-2.39,2.294-4.437 C74.234,55.222,73.915,51.421,73.716,49.418z M33.042,58.107c-2.478,0-4.488-2.032-4.488-4.536c0-2.506,2.01-4.538,4.488-4.538 c2.477,0,4.488,2.032,4.488,4.538C37.53,56.075,35.519,58.107,33.042,58.107z M56.384,57.238H43.635 c-1.332,0-2.411-0.735-2.411-1.642s1.08-1.642,2.411-1.642h12.749c1.332,0,2.411,0.735,2.411,1.642S57.716,57.238,56.384,57.238z M56.384,52.795H43.635c-1.332,0-2.411-0.734-2.411-1.639c0-0.905,1.08-1.638,2.411-1.638h12.749c1.332,0,2.411,0.733,2.411,1.638 C58.795,52.061,57.716,52.795,56.384,52.795z M58.426,44.208c-3.717,0.014-13.322,0.014-16.996,0 c-3.646-0.029-3.327,0.029-4.778-0.1c-1.466-0.13-3.178-0.251-3.915-0.673c-0.736-0.458-0.652-0.974-0.481-1.933 c0.177-0.981,0.792-2.433,1.437-3.864c0.623-1.46,1.536-3.607,2.195-4.731c0.623-1.13,0.927-1.517,1.62-1.932 c0.687-0.421,1.544-0.485,2.486-0.58c0.92-0.121,1.939-0.143,3.058-0.094h13.911c1.111-0.049,2.131-0.027,3.058,0.094 c0.92,0.093,1.776,0.157,2.484,0.58c0.688,0.416,0.963,0.802,1.622,1.932c0.658,1.124,1.557,3.271,2.2,4.731 c0.603,1.432,1.261,2.883,1.43,3.864c0.156,0.958,0.241,1.474-0.48,1.933c-0.757,0.422-2.436,0.544-3.914,0.673 C61.874,44.237,62.086,44.179,58.426,44.208z M66.973,58.107c-2.477,0-4.488-2.032-4.488-4.536c0-2.506,2.012-4.538,4.488-4.538 s4.488,2.032,4.488,4.538C71.461,56.075,69.45,58.107,66.973,58.107z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (3, 'Transport or civil plane', '<path fill="#FFFFFF" d="M34.822,24.58l-4.044,1.931L48.753,44.6L35.1,51.991l-7.81-4.254L24.5,49.092l6.529,7.142l1.596,9.601 l2.79-1.355l1.491-8.767l14.33-5.996l3.013,25.158l4.044-1.931l4.783-28.523c2.699-1.248,6.598-3.173,9.271-4.472 c3.34-1.62,4.177-3.635,3.367-5.304c-0.811-1.669-2.962-2.362-6.301-0.741c-2.673,1.297-6.549,3.272-9.198,4.621L35.429,24.253"/>', 'civil_airplane-2', '[{"datad":"M34.822,24.58l-4.044,1.931L48.753,44.6L35.1,51.991l-7.81-4.254L24.5,49.092l6.529,7.142l1.596,9.601 l2.79-1.355l1.491-8.767l14.33-5.996l3.013,25.158l4.044-1.931l4.783-28.523c2.699-1.248,6.598-3.173,9.271-4.472 c3.34-1.62,4.177-3.635,3.367-5.304c-0.811-1.669-2.962-2.362-6.301-0.741c-2.673,1.297-6.549,3.272-9.198,4.621L35.429,24.253"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (2, 'Floods', '<polygon fill="#010101" points="50.201,44.588 50.193,44.595 50.193,44.588 "/> <polygon fill="#010101" points="50.201,44.588 50.193,44.595 50.193,44.588 "/> <path fill="#FFFFFF" d="M69.789,64.084c-0.467,0.31-1.076,0.465-1.688,0.465c-0.612,0-1.224-0.155-1.688-0.465l-2.709-1.806 c-0.466-0.311-1.075-0.465-1.688-0.465c-0.611,0-1.224,0.154-1.688,0.465l-2.709,1.806c-0.466,0.31-1.074,0.465-1.688,0.465 c-0.609,0-1.222-0.155-1.688-0.465l-2.709-1.806c-0.465-0.311-1.075-0.465-1.687-0.465c-0.61,0-1.222,0.154-1.687,0.465 l-2.709,1.806c-0.465,0.31-1.076,0.465-1.687,0.465c-0.611,0-1.223-0.155-1.688-0.465l-2.709-1.806 c-0.465-0.311-1.075-0.465-1.687-0.465c-0.61,0-1.222,0.154-1.687,0.465l-2.709,1.806c-0.465,0.31-1.076,0.465-1.687,0.465 c-0.611,0-1.223-0.155-1.688-0.465l-2.709-1.806c-0.465-0.311-1.075-0.465-1.687-0.465c-0.001,0-0.004,0-0.007,0v2.028 c0.003,0,0.006,0,0.007,0c0.298,0,0.498,0.08,0.563,0.124l2.709,1.806c0.778,0.518,1.776,0.803,2.811,0.803 s2.033-0.285,2.812-0.803l2.709-1.806c0.065-0.044,0.264-0.124,0.563-0.124c0.298,0,0.497,0.08,0.563,0.124l2.709,1.806 c0.778,0.518,1.776,0.803,2.811,0.803s2.033-0.285,2.812-0.803l2.709-1.806c0.065-0.044,0.264-0.124,0.563-0.124 c0.298,0,0.497,0.08,0.563,0.124l2.709,1.806c0.778,0.518,1.776,0.803,2.812,0.803c1.033,0,2.032-0.285,2.81-0.803l2.709-1.806 c0.066-0.044,0.264-0.124,0.564-0.124c0.298,0,0.495,0.08,0.562,0.124l2.708,1.806c0.779,0.518,1.777,0.803,2.813,0.803 c1.034,0,2.032-0.285,2.811-0.803l2.709-1.806c0.065-0.044,0.261-0.123,0.555-0.124v-2.028c-0.608,0.002-1.218,0.157-1.677,0.465 L69.789,64.084z"/> <path fill="#FFFFFF" d="M72.498,54.521l-2.709,1.807c-0.467,0.309-1.076,0.463-1.688,0.463c-0.612,0-1.224-0.154-1.688-0.463 l-2.709-1.807c-0.208-0.14-0.448-0.247-0.701-0.323l0.035-12.886l1.86,1.859l0,0l0,0c0.396,0.396,1.036,0.396,1.432,0 c0.397-0.396,0.397-1.038,0-1.434l0,0l0,0L50.559,25.964l0,0c-0.396-0.396-1.037-0.396-1.433,0l0,0l0,0L33.355,41.736l0,0h-0.002 c-0.396,0.396-0.396,1.038,0,1.434c0.397,0.396,1.039,0.396,1.434,0c0,0,0,0,0.001,0l1.867-1.869l-0.036,12.92 c-0.224,0.074-0.435,0.175-0.622,0.299l-2.709,1.807c-0.465,0.309-1.076,0.463-1.687,0.463c-0.611,0-1.223-0.154-1.688-0.463 l-2.709-1.807c-0.465-0.311-1.075-0.465-1.687-0.465c-0.001,0-0.004,0-0.007,0v2.027c0.003,0,0.006,0,0.007,0 c0.298,0,0.498,0.08,0.563,0.125l2.709,1.806c0.778,0.519,1.776,0.804,2.812,0.804c1.035,0,2.033-0.285,2.812-0.804l2.709-1.806 c0.065-0.045,0.264-0.125,0.563-0.125c0.298,0,0.497,0.08,0.563,0.125l2.709,1.806c0.778,0.519,1.776,0.804,2.811,0.804 s2.033-0.285,2.812-0.804l2.709-1.806c0.065-0.045,0.264-0.125,0.563-0.125c0.298,0,0.497,0.08,0.563,0.125l2.71,1.806 c0.777,0.519,1.775,0.804,2.812,0.804c1.033,0,2.03-0.285,2.81-0.804l2.709-1.806c0.065-0.045,0.264-0.125,0.563-0.125 c0.298,0,0.496,0.08,0.563,0.125l2.708,1.806c0.779,0.519,1.777,0.804,2.813,0.804c1.034,0,2.032-0.285,2.811-0.804l2.709-1.806 c0.065-0.044,0.261-0.122,0.555-0.125v-2.027C73.566,54.058,72.958,54.212,72.498,54.521z M49.851,54.056 c-0.61,0-1.222,0.154-1.687,0.465l-0.349,0.232v-6.78h4.056v6.768l-0.333-0.22C51.072,54.21,50.462,54.056,49.851,54.056z M57.621,56.327c-0.466,0.309-1.074,0.463-1.688,0.463c-0.609,0-1.222-0.154-1.688-0.463l-0.348-0.233v-9.135 c0-0.56-0.455-1.014-1.013-1.014h-6.084c-0.562,0-1.015,0.455-1.015,1.014c0,0.024,0.001,0.049,0.004,0.073h-0.004v9.071 l-0.333,0.224c-0.465,0.309-1.076,0.463-1.687,0.463c-0.611,0-1.223-0.154-1.688-0.463l-2.709-1.807 c-0.215-0.144-0.46-0.252-0.723-0.33l0.042-14.922l11.153-11.153l11.174,11.173l-0.042,14.925 c-0.234,0.075-0.452,0.177-0.646,0.307L57.621,56.327z"/> <path fill="#FFFFFF" d="M69.789,71.841c-0.467,0.311-1.076,0.465-1.688,0.465c-0.612,0-1.224-0.154-1.688-0.465l-2.709-1.806 c-0.466-0.31-1.075-0.463-1.688-0.463c-0.611,0-1.224,0.153-1.688,0.463l-2.709,1.806c-0.466,0.311-1.074,0.465-1.688,0.465 c-0.612,0-1.222-0.154-1.688-0.465l-2.709-1.806c-0.465-0.31-1.075-0.463-1.687-0.463s-1.222,0.153-1.687,0.463l-2.709,1.806 c-0.465,0.311-1.076,0.465-1.687,0.465c-0.611,0-1.223-0.154-1.688-0.465l-2.709-1.806c-0.465-0.31-1.075-0.463-1.687-0.463 c-0.61,0-1.222,0.153-1.687,0.463l-2.709,1.806c-0.465,0.311-1.076,0.465-1.687,0.465c-0.611,0-1.223-0.154-1.688-0.465 l-2.709-1.806c-0.465-0.31-1.075-0.463-1.687-0.463c-0.001,0-0.004,0-0.007,0V71.6c0.003,0,0.004,0,0.007,0 c0.298,0,0.498,0.081,0.563,0.122l2.709,1.807c0.778,0.52,1.776,0.805,2.811,0.805s2.033-0.285,2.812-0.805l2.709-1.807 c0.065-0.041,0.264-0.122,0.563-0.122c0.298,0,0.497,0.081,0.563,0.122l2.709,1.807c0.778,0.52,1.776,0.805,2.811,0.805 s2.033-0.285,2.812-0.805l2.709-1.807c0.065-0.041,0.264-0.122,0.563-0.122c0.298,0,0.497,0.081,0.563,0.122l2.709,1.807 c0.778,0.52,1.776,0.805,2.812,0.805c1.033,0,2.032-0.285,2.81-0.805l2.709-1.807c0.066-0.041,0.264-0.122,0.564-0.122 c0.298,0,0.495,0.081,0.562,0.122l2.708,1.807c0.779,0.52,1.777,0.805,2.813,0.805c1.034,0,2.032-0.285,2.811-0.805l2.709-1.807 c0.065-0.041,0.261-0.122,0.555-0.122v-2.027c-0.608,0.001-1.218,0.155-1.677,0.463L69.789,71.841z"/>', 'floods-2', '[{"datad":"M69.789,64.084c-0.467,0.31-1.076,0.465-1.688,0.465c-0.612,0-1.224-0.155-1.688-0.465l-2.709-1.806 c-0.466-0.311-1.075-0.465-1.688-0.465c-0.611,0-1.224,0.154-1.688,0.465l-2.709,1.806c-0.466,0.31-1.074,0.465-1.688,0.465 c-0.609,0-1.222-0.155-1.688-0.465l-2.709-1.806c-0.465-0.311-1.075-0.465-1.687-0.465c-0.61,0-1.222,0.154-1.687,0.465 l-2.709,1.806c-0.465,0.31-1.076,0.465-1.687,0.465c-0.611,0-1.223-0.155-1.688-0.465l-2.709-1.806 c-0.465-0.311-1.075-0.465-1.687-0.465c-0.61,0-1.222,0.154-1.687,0.465l-2.709,1.806c-0.465,0.31-1.076,0.465-1.687,0.465 c-0.611,0-1.223-0.155-1.688-0.465l-2.709-1.806c-0.465-0.311-1.075-0.465-1.687-0.465c-0.001,0-0.004,0-0.007,0v2.028 c0.003,0,0.006,0,0.007,0c0.298,0,0.498,0.08,0.563,0.124l2.709,1.806c0.778,0.518,1.776,0.803,2.811,0.803 s2.033-0.285,2.812-0.803l2.709-1.806c0.065-0.044,0.264-0.124,0.563-0.124c0.298,0,0.497,0.08,0.563,0.124l2.709,1.806 c0.778,0.518,1.776,0.803,2.811,0.803s2.033-0.285,2.812-0.803l2.709-1.806c0.065-0.044,0.264-0.124,0.563-0.124 c0.298,0,0.497,0.08,0.563,0.124l2.709,1.806c0.778,0.518,1.776,0.803,2.812,0.803c1.033,0,2.032-0.285,2.81-0.803l2.709-1.806 c0.066-0.044,0.264-0.124,0.564-0.124c0.298,0,0.495,0.08,0.562,0.124l2.708,1.806c0.779,0.518,1.777,0.803,2.813,0.803 c1.034,0,2.032-0.285,2.811-0.803l2.709-1.806c0.065-0.044,0.261-0.123,0.555-0.124v-2.028c-0.608,0.002-1.218,0.157-1.677,0.465 L69.789,64.084z"},{"datad":"M72.498,54.521l-2.709,1.807c-0.467,0.309-1.076,0.463-1.688,0.463c-0.612,0-1.224-0.154-1.688-0.463 l-2.709-1.807c-0.208-0.14-0.448-0.247-0.701-0.323l0.035-12.886l1.86,1.859l0,0l0,0c0.396,0.396,1.036,0.396,1.432,0 c0.397-0.396,0.397-1.038,0-1.434l0,0l0,0L50.559,25.964l0,0c-0.396-0.396-1.037-0.396-1.433,0l0,0l0,0L33.355,41.736l0,0h-0.002 c-0.396,0.396-0.396,1.038,0,1.434c0.397,0.396,1.039,0.396,1.434,0c0,0,0,0,0.001,0l1.867-1.869l-0.036,12.92 c-0.224,0.074-0.435,0.175-0.622,0.299l-2.709,1.807c-0.465,0.309-1.076,0.463-1.687,0.463c-0.611,0-1.223-0.154-1.688-0.463 l-2.709-1.807c-0.465-0.311-1.075-0.465-1.687-0.465c-0.001,0-0.004,0-0.007,0v2.027c0.003,0,0.006,0,0.007,0 c0.298,0,0.498,0.08,0.563,0.125l2.709,1.806c0.778,0.519,1.776,0.804,2.812,0.804c1.035,0,2.033-0.285,2.812-0.804l2.709-1.806 c0.065-0.045,0.264-0.125,0.563-0.125c0.298,0,0.497,0.08,0.563,0.125l2.709,1.806c0.778,0.519,1.776,0.804,2.811,0.804 s2.033-0.285,2.812-0.804l2.709-1.806c0.065-0.045,0.264-0.125,0.563-0.125c0.298,0,0.497,0.08,0.563,0.125l2.71,1.806 c0.777,0.519,1.775,0.804,2.812,0.804c1.033,0,2.03-0.285,2.81-0.804l2.709-1.806c0.065-0.045,0.264-0.125,0.563-0.125 c0.298,0,0.496,0.08,0.563,0.125l2.708,1.806c0.779,0.519,1.777,0.804,2.813,0.804c1.034,0,2.032-0.285,2.811-0.804l2.709-1.806 c0.065-0.044,0.261-0.122,0.555-0.125v-2.027C73.566,54.058,72.958,54.212,72.498,54.521z M49.851,54.056 c-0.61,0-1.222,0.154-1.687,0.465l-0.349,0.232v-6.78h4.056v6.768l-0.333-0.22C51.072,54.21,50.462,54.056,49.851,54.056z M57.621,56.327c-0.466,0.309-1.074,0.463-1.688,0.463c-0.609,0-1.222-0.154-1.688-0.463l-0.348-0.233v-9.135 c0-0.56-0.455-1.014-1.013-1.014h-6.084c-0.562,0-1.015,0.455-1.015,1.014c0,0.024,0.001,0.049,0.004,0.073h-0.004v9.071 l-0.333,0.224c-0.465,0.309-1.076,0.463-1.687,0.463c-0.611,0-1.223-0.154-1.688-0.463l-2.709-1.807 c-0.215-0.144-0.46-0.252-0.723-0.33l0.042-14.922l11.153-11.153l11.174,11.173l-0.042,14.925 c-0.234,0.075-0.452,0.177-0.646,0.307L57.621,56.327z"},{"datad":"M69.789,71.841c-0.467,0.311-1.076,0.465-1.688,0.465c-0.612,0-1.224-0.154-1.688-0.465l-2.709-1.806 c-0.466-0.31-1.075-0.463-1.688-0.463c-0.611,0-1.224,0.153-1.688,0.463l-2.709,1.806c-0.466,0.311-1.074,0.465-1.688,0.465 c-0.612,0-1.222-0.154-1.688-0.465l-2.709-1.806c-0.465-0.31-1.075-0.463-1.687-0.463s-1.222,0.153-1.687,0.463l-2.709,1.806 c-0.465,0.311-1.076,0.465-1.687,0.465c-0.611,0-1.223-0.154-1.688-0.465l-2.709-1.806c-0.465-0.31-1.075-0.463-1.687-0.463 c-0.61,0-1.222,0.153-1.687,0.463l-2.709,1.806c-0.465,0.311-1.076,0.465-1.687,0.465c-0.611,0-1.223-0.154-1.688-0.465 l-2.709-1.806c-0.465-0.31-1.075-0.463-1.687-0.463c-0.001,0-0.004,0-0.007,0V71.6c0.003,0,0.004,0,0.007,0 c0.298,0,0.498,0.081,0.563,0.122l2.709,1.807c0.778,0.52,1.776,0.805,2.811,0.805s2.033-0.285,2.812-0.805l2.709-1.807 c0.065-0.041,0.264-0.122,0.563-0.122c0.298,0,0.497,0.081,0.563,0.122l2.709,1.807c0.778,0.52,1.776,0.805,2.811,0.805 s2.033-0.285,2.812-0.805l2.709-1.807c0.065-0.041,0.264-0.122,0.563-0.122c0.298,0,0.497,0.081,0.563,0.122l2.709,1.807 c0.778,0.52,1.776,0.805,2.812,0.805c1.033,0,2.032-0.285,2.81-0.805l2.709-1.807c0.066-0.041,0.264-0.122,0.564-0.122 c0.298,0,0.495,0.081,0.562,0.122l2.708,1.807c0.779,0.52,1.777,0.805,2.813,0.805c1.034,0,2.032-0.285,2.811-0.805l2.709-1.807 c0.065-0.041,0.261-0.122,0.555-0.122v-2.027c-0.608,0.001-1.218,0.155-1.677,0.463L69.789,71.841z"}]');
INSERT INTO public.event_type (id, name, image, code, path_data) VALUES (1, 'Earthquake', '<polygon fill="#010101" points="50.201,44.588 50.193,44.595 50.193,44.588 "/> <polygon fill="#010101" points="50.201,44.588 50.193,44.595 50.193,44.588 "/> <path fill="#FFFFFF" d="M47.504,70.496l-10.995,2.391l-3.251-3.729c-0.479-0.573-1.338-0.669-2.008-0.191l-7.936,5.642 c-0.669,0.574-0.86,1.529-0.382,2.199c0.286,0.479,0.765,0.67,1.243,0.67c0.287,0,0.669-0.096,0.765-0.384l6.884-4.875l2.964,3.347 c0.382,0.383,0.956,0.572,1.53,0.478l11.855-2.581c0.765-0.189,1.338-0.956,1.147-1.816C49.13,70.878,48.365,70.305,47.504,70.496z "/> <path fill="#FFFFFF" d="M76.953,74.605l-10.614-5.832c-0.573-0.287-1.146-0.19-1.624,0.097l-5.738,3.92l-4.875-2.199 c-0.765-0.382-1.625,0-2.008,0.765c-0.383,0.766,0,1.627,0.766,2.009l5.641,2.581c0.479,0.285,1.053,0.191,1.531-0.096l5.639-3.824 l9.753,5.26c0.287,0.096,0.479,0.191,0.767,0.191c0.574,0,1.053-0.287,1.434-0.768C78.004,75.945,77.717,74.989,76.953,74.605z"/> <path fill="#FFFFFF" d="M49.894,50.131c-0.668-0.479-1.625-0.289-2.103,0.477l-3.06,5.068c-0.382,0.574-0.287,1.338,0.191,1.816 l2.199,2.39l-0.686,3.739l-8.684,1.52L35.266,50.8l26.198-4.589l2.485,14.247l-8.986,1.528c-0.861,0.189-1.438,0.957-1.245,1.816 c0.192,0.86,0.958,1.436,1.819,1.243l10.516-1.817c0.383-0.095,0.766-0.285,0.956-0.668c0.288-0.287,0.384-0.767,0.288-1.147 l-3.061-17.305c-0.084-0.375-0.279-0.691-0.543-0.917c-0.143-0.25-0.33-0.475-0.605-0.612l-16.54-8.893 c-0.669-0.382-1.434-0.287-1.912,0.287L32.207,47.835c-0.402,0.469-0.503,1.074-0.31,1.594c-0.012,0.142-0.01,0.283,0.023,0.413 l3.059,17.307c0.191,0.766,0.765,1.242,1.53,1.242c0.096,0,0.191,0,0.286,0.096L47.6,66.574c0.222-0.051,0.423-0.139,0.6-0.254 c0.577-0.141,1.026-0.557,1.026-1.18l0.956-5.451c0.096-0.478-0.096-0.953-0.383-1.336l-1.912-2.104l2.486-4.019 C50.852,51.564,50.659,50.607,49.894,50.131z M46.166,37.031l12.193,6.549l-21.548,3.802L46.166,37.031z"/>', 'earthquake-2', '[{"datad":"M47.504,70.496l-10.995,2.391l-3.251-3.729c-0.479-0.573-1.338-0.669-2.008-0.191l-7.936,5.642 c-0.669,0.574-0.86,1.529-0.382,2.199c0.286,0.479,0.765,0.67,1.243,0.67c0.287,0,0.669-0.096,0.765-0.384l6.884-4.875l2.964,3.347 c0.382,0.383,0.956,0.572,1.53,0.478l11.855-2.581c0.765-0.189,1.338-0.956,1.147-1.816C49.13,70.878,48.365,70.305,47.504,70.496z "},{"datad":"M76.953,74.605l-10.614-5.832c-0.573-0.287-1.146-0.19-1.624,0.097l-5.738,3.92l-4.875-2.199 c-0.765-0.382-1.625,0-2.008,0.765c-0.383,0.766,0,1.627,0.766,2.009l5.641,2.581c0.479,0.285,1.053,0.191,1.531-0.096l5.639-3.824 l9.753,5.26c0.287,0.096,0.479,0.191,0.767,0.191c0.574,0,1.053-0.287,1.434-0.768C78.004,75.945,77.717,74.989,76.953,74.605z"},{"datad":"M49.894,50.131c-0.668-0.479-1.625-0.289-2.103,0.477l-3.06,5.068c-0.382,0.574-0.287,1.338,0.191,1.816 l2.199,2.39l-0.686,3.739l-8.684,1.52L35.266,50.8l26.198-4.589l2.485,14.247l-8.986,1.528c-0.861,0.189-1.438,0.957-1.245,1.816 c0.192,0.86,0.958,1.436,1.819,1.243l10.516-1.817c0.383-0.095,0.766-0.285,0.956-0.668c0.288-0.287,0.384-0.767,0.288-1.147 l-3.061-17.305c-0.084-0.375-0.279-0.691-0.543-0.917c-0.143-0.25-0.33-0.475-0.605-0.612l-16.54-8.893 c-0.669-0.382-1.434-0.287-1.912,0.287L32.207,47.835c-0.402,0.469-0.503,1.074-0.31,1.594c-0.012,0.142-0.01,0.283,0.023,0.413 l3.059,17.307c0.191,0.766,0.765,1.242,1.53,1.242c0.096,0,0.191,0,0.286,0.096L47.6,66.574c0.222-0.051,0.423-0.139,0.6-0.254 c0.577-0.141,1.026-0.557,1.026-1.18l0.956-5.451c0.096-0.478-0.096-0.953-0.383-1.336l-1.912-2.104l2.486-4.019 C50.852,51.564,50.659,50.607,49.894,50.131z M46.166,37.031l12.193,6.549l-21.548,3.802L46.166,37.031z"}]');


--
-- Data for Name: events_time_count; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: fake_layer_id; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: geo_layer; Type: TABLE DATA; Schema: public; Owner: imst_admin
--



--
-- Data for Name: layer; Type: TABLE DATA; Schema: public; Owner: imst
--

INSERT INTO public.layer (id, name, create_date, update_date, state, is_temp, guid) VALUES (5, 'Libya', '2020-05-21 20:22:49.707', '2020-09-14 19:44:20.237', true, false, 'ab18a6a0-d2ad-4d43-b2b9-4e6ff82c8123');


--
-- Data for Name: layer_export; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: log; Type: TABLE DATA; Schema: public; Owner: imst_admin
--



--
-- Data for Name: log_type; Type: TABLE DATA; Schema: public; Owner: imst_admin
--

INSERT INTO public.log_type (id, name, related_table) VALUES (18, 'Layer Add', 'Layer');
INSERT INTO public.log_type (id, name, related_table) VALUES (19, 'Layer Edit', 'Layer');
INSERT INTO public.log_type (id, name, related_table) VALUES (20, 'Layer Delete', 'Layer');
INSERT INTO public.log_type (id, name, related_table) VALUES (22, 'Map Area Edit', 'Map Area');
INSERT INTO public.log_type (id, name, related_table) VALUES (23, 'Map Area Delete', 'Map Area');
INSERT INTO public.log_type (id, name, related_table) VALUES (21, 'Map Area Add', 'Map Area');
INSERT INTO public.log_type (id, name, related_table) VALUES (14, 'Event Tag Delete', 'Event Tag');
INSERT INTO public.log_type (id, name, related_table) VALUES (12, 'Event Tag Add', 'Event Tag');
INSERT INTO public.log_type (id, name, related_table) VALUES (13, 'Event Tag Edit', 'Event Tag');
INSERT INTO public.log_type (id, name, related_table) VALUES (24, 'Settings Edit', 'Settings');
INSERT INTO public.log_type (id, name, related_table) VALUES (25, 'Tile Server Add', 'Tile Server');
INSERT INTO public.log_type (id, name, related_table) VALUES (26, 'Tile Server Edit', 'Tile Server');
INSERT INTO public.log_type (id, name, related_table) VALUES (27, 'Tile Server Delete', 'Tile Server');
INSERT INTO public.log_type (id, name, related_table) VALUES (1, 'Login', 'User');
INSERT INTO public.log_type (id, name, related_table) VALUES (7, 'Profile edit', 'Profile');
INSERT INTO public.log_type (id, name, related_table) VALUES (2, 'Logout', 'User');
INSERT INTO public.log_type (id, name, related_table) VALUES (3, 'User add', 'User');
INSERT INTO public.log_type (id, name, related_table) VALUES (4, 'User edit', 'User');
INSERT INTO public.log_type (id, name, related_table) VALUES (8, 'Profile delete', 'Profile');
INSERT INTO public.log_type (id, name, related_table) VALUES (6, 'Profile add', 'Profile');
INSERT INTO public.log_type (id, name, related_table) VALUES (5, 'User delete', 'User');
INSERT INTO public.log_type (id, name, related_table) VALUES (9, 'Event Add', 'Event');
INSERT INTO public.log_type (id, name, related_table) VALUES (10, 'Event Edit', 'Event');
INSERT INTO public.log_type (id, name, related_table) VALUES (11, 'Event Delete', 'Event');
INSERT INTO public.log_type (id, name, related_table) VALUES (15, 'Event Type Add', 'Event Type');
INSERT INTO public.log_type (id, name, related_table) VALUES (16, 'Event Type Edit', 'Event Type');
INSERT INTO public.log_type (id, name, related_table) VALUES (17, 'Event Type Delete', 'Event Type');
INSERT INTO public.log_type (id, name, related_table) VALUES (29, 'Geo Layer Edit', 'Geo Layer');
INSERT INTO public.log_type (id, name, related_table) VALUES (30, 'Geo Layer Delete', 'Geo Layer');
INSERT INTO public.log_type (id, name, related_table) VALUES (28, 'Geo Layer Add', 'Geo Layer');
INSERT INTO public.log_type (id, name, related_table) VALUES (32, 'Map Area Edit', 'Map Area');
INSERT INTO public.log_type (id, name, related_table) VALUES (31, 'Map Area Add', 'Map Area');
INSERT INTO public.log_type (id, name, related_table) VALUES (33, 'Map Area Delete', 'Map Area');
INSERT INTO public.log_type (id, name, related_table) VALUES (35, 'User Event Permission Edit', 'User Event Permission');
INSERT INTO public.log_type (id, name, related_table) VALUES (34, 'User Event Permission Add', 'User Event Permission');
INSERT INTO public.log_type (id, name, related_table) VALUES (36, 'User Event Permission Delete', 'User Event Permission');
INSERT INTO public.log_type (id, name, related_table) VALUES (38, 'Event Group Edit', 'Event Group');
INSERT INTO public.log_type (id, name, related_table) VALUES (37, 'Event Group Add', 'Event Group');
INSERT INTO public.log_type (id, name, related_table) VALUES (39, 'Event Group Delete', 'Event Group');
INSERT INTO public.log_type (id, name, related_table) VALUES (40, 'Tag Add', 'Tag');
INSERT INTO public.log_type (id, name, related_table) VALUES (41, 'Tag Edit', 'Tag');
INSERT INTO public.log_type (id, name, related_table) VALUES (42, 'Tag Delete', 'Tag');
INSERT INTO public.log_type (id, name, related_table) VALUES (43, 'User Group Id Add', 'User Group Id');
INSERT INTO public.log_type (id, name, related_table) VALUES (44, 'User Group Id Edit', 'User Group Id');
INSERT INTO public.log_type (id, name, related_table) VALUES (45, 'User Group Id Delete', 'User Group Id');
INSERT INTO public.log_type (id, name, related_table) VALUES (46, 'User Layer Permission Add', 'User Layer Permission');
INSERT INTO public.log_type (id, name, related_table) VALUES (47, 'User Layer Permission Edit', 'User Layer Permission');
INSERT INTO public.log_type (id, name, related_table) VALUES (48, 'User Layer Permission Delete', 'User Layer Permission');
INSERT INTO public.log_type (id, name, related_table) VALUES (49, 'User User Id Add', 'User User Id');
INSERT INTO public.log_type (id, name, related_table) VALUES (50, 'User User Id Edit', 'User User Id');
INSERT INTO public.log_type (id, name, related_table) VALUES (51, 'User User Id Delete', 'User User Id');
INSERT INTO public.log_type (id, name, related_table) VALUES (52, 'Event Media Add', 'Event Media');
INSERT INTO public.log_type (id, name, related_table) VALUES (53, 'Event Media Edit', 'Event Media');
INSERT INTO public.log_type (id, name, related_table) VALUES (54, 'Event Media Delete', 'EventMedia');
INSERT INTO public.log_type (id, name, related_table) VALUES (55, 'Alert Add', 'Alert');
INSERT INTO public.log_type (id, name, related_table) VALUES (56, 'Alert Edit', 'Alert');
INSERT INTO public.log_type (id, name, related_table) VALUES (57, 'Alert Delete', 'Alert');
INSERT INTO public.log_type (id, name, related_table) VALUES (58, 'USER_EVENT_GROUP_PERMISSION_ADD', 'User Event Group Permission');
INSERT INTO public.log_type (id, name, related_table) VALUES (59, 'USER_EVENT_GROUP_PERMISSION_EDIT', 'User Event Group Permission');
INSERT INTO public.log_type (id, name, related_table) VALUES (60, 'USER_EVENT_GROUP_PERMISSION_DELETE', 'User Event Group Permission');
INSERT INTO public.log_type (id, name, related_table) VALUES (61, 'User Settings Add', 'User Settings');
INSERT INTO public.log_type (id, name, related_table) VALUES (62, 'User Settings Edit', 'User Settings');
INSERT INTO public.log_type (id, name, related_table) VALUES (63, 'User Settings Delete', 'User Settings');
INSERT INTO public.log_type (id, name, related_table) VALUES (64, 'Blacklist Add', 'Blacklist');
INSERT INTO public.log_type (id, name, related_table) VALUES (65, 'Blacklist Edit', 'Blacklist');
INSERT INTO public.log_type (id, name, related_table) VALUES (66, 'Blacklist Delete', 'Blacklist');
INSERT INTO public.log_type (id, name, related_table) VALUES (67, 'Event Link Add', 'Event Link');
INSERT INTO public.log_type (id, name, related_table) VALUES (68, 'Event Link Edit', 'Event Link');
INSERT INTO public.log_type (id, name, related_table) VALUES (69, 'Event Link Delete', 'Event Link');
INSERT INTO public.log_type (id, name, related_table) VALUES (70, 'Fake Layer Id Add', 'Fake Layer Id');
INSERT INTO public.log_type (id, name, related_table) VALUES (71, 'Fake Layer Id Delete', 'Fake Layer Id');


--
-- Data for Name: map_area; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: map_area_group; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: permission; Type: TABLE DATA; Schema: public; Owner: imst_admin
--

INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (16, 'ROLE_MAP_AREA_MANAGE', 'Harita Alanı yönetimi yetkisi', true, 'Katmanİşlemleri', 210);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (14, 'ROLE_MANAGE_SETTINGS', 'Sistem ayarları yönetimi yetkisi', true, 'Ayarlar', 800);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (17, 'ROLE_MAP_AREA_LIST', 'Harita Alanı listeleme yetkisi', true, 'Katmanİşlemleri', 215);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (18, 'ROLE_TILE_SERVER_MANAGE', 'Harita Altlığı yönetimi yetkisi', true, 'Katmanİşlemleri', 220);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (19, 'ROLE_TILE_SERVER_LIST', 'Harita Altlığı listeleme yetkisi', true, 'Katmanİşlemleri', 225);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (20, 'ROLE_USER_GROUP_ID_LIST', 'Kullanıcı Grup Id listeleme yetkisi', true, 'KullanıcıGrupIdişlemleri', 220);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (21, 'ROLE_USER_GROUP_ID_MANAGE', 'Kullanıcı Grup Id yönetimi yetkisi', true, 'KullanıcıGrupIdişlemleri', 220);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (1, 'ROLE_USER_MANAGE', 'Kullanıcı yönetimi yetkisi', true, 'Kullanıcıİşlemleri', 5);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (2, 'ROLE_USER_LIST', 'Kullanıcı listeleme yetkisi', true, 'Kullanıcıİşlemleri', 10);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (3, 'ROLE_PROFILE_MANAGE', 'Profil yönetimi yetkisi', true, 'Kullanıcıİşlemleri', 15);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (4, 'ROLE_PROFILE_LIST', 'Profil listeleme yetkisi', true, 'Kullanıcıİşlemleri', 20);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (22, 'ROLE_USER_USER_ID_LIST', 'Kullanıcı User Id listeleme yetkisi', true, 'KullanıcıKullanıcıIdişlemleri', 220);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (23, 'ROLE_USER_USER_ID_MANAGE', 'Kullanıcı User Id yönetim yetkisi', true, 'KullanıcıKullanıcıIdişlemleri', 220);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (5, 'ROLE_EVENT_MANAGE', 'Olay yönetimi yetkisi', true, 'Olayİşlemleri', 100);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (6, 'ROLE_EVENT_LIST', 'Olay listeleme yetkisi', true, 'Olayİşlemleri', 105);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (7, 'ROLE_EVENT_TAG_MANAGE', 'Olay tag yönetimi yetkisi', true, 'Olayİşlemleri', 110);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (8, 'ROLE_EVENT_TAG_LIST', 'Olay tag listeleme yetkisi', true, 'Olayİşlemleri', 115);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (9, 'ROLE_EVENT_TYPE_MANAGE', 'Olay Türü yönetimi yetkisi', true, 'Olayİşlemleri', 120);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (10, 'ROLE_EVENT_TYPE_LIST', 'Olay Türü listeleme yetkisi', true, 'Olayİşlemleri', 125);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (11, 'ROLE_LAYER_MANAGE', 'Layer Katman yönetimi yetkisi', true, 'Katmanİşlemleri', 200);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (12, 'ROLE_LAYER_LIST', 'Layer Katman listeleme yetkisi', true, 'Katmanİşlemleri', 205);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (24, 'ROLE_USER_LAYER_PERMISSION_LIST', 'Kullanıcı Katman İzinileri listeleme yetkisi', true, 'KullanıcıKatmanİzinleriİşlemleri', 225);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (25, 'ROLE_USER_LAYER_PERMISSION_MANAGE', 'Kullanıcı Katman İzinleri yönetimi yetkisi', true, 'KullanıcıKatmanİzinleriİşlemleri', 225);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (26, 'ROLE_GEO_LAYER_LIST', 'Coğrafi Katman listeleme yetkisi', true, 'CoğrafiKatmanİşlemleri', 220);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (27, 'ROLE_GEO_LAYER_MANAGE', 'Coğrafi Katman yönetimi yetkisi', true, 'CoğrafiKatmanİşlemleri', 220);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (28, 'ROLE_MAP_AREA_LIST', 'Harita Alanı listeleme yetkisi', true, 'HaritaAlanıİşlemleri', 220);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (29, 'ROLE_MAP_AREA_MANAGE', 'Harita Alanı yönetim yetkisi', true, 'HaritaAlanıİşlemleri', 220);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (30, 'ROLE_MAP_AREA_GROUP_LIST', 'Harita Alanı Grubu listeleme yetkisi', true, 'HaritaAlanıGrubuİşlemleri', 220);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (37, 'ROLE_LOG_LIST', 'Log listeleme yetkisi', true, 'Logİşlemleri', 220);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (31, 'ROLE_MAP_AREA_GROUP_MANAGE', 'Harita Alanı Grubu yönetim yetkisi', true, 'HaritaAlanıGrubuİşlemleri', 220);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (34, 'ROLE_EVENT_GROUP_LIST', 'Olay Grubu listeleme yetkisi', true, 'OlayGrubuİşlemleri', 220);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (38, 'ROLE_USER_LOGIN_WEB_PERMISSION', 'Web Uygulamasına Giriş Yapabilme Yetkisi', true, 'UygulamayaGirişİzinleriİşlemleri', 2);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (39, 'ROLE_USER_LOGIN_ADMIN_PANEL_PERMISSION', 'Admin Paneline Giriş Yapabilme Yetkisi', true, 'UygulamayaGirişİzinleriİşlemleri', 1);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (40, 'ROLE_ENCRYPT_MANAGE', 'Şifreleme yetkisi', true, 'Şifreİşlemleri', 900);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (41, 'ROLE_USER_EVENT_GROUP_PERMISSION_LIST', 'Kullanıcı Olay Grubu İzinleri listeleme yetkisi', true, 'KullanıcıOlayGrubuİzinleriİşlemleri', 230);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (42, 'ROLE_USER_EVENT_GROUP_PERMISSION_MANAGE', 'Kullanıcı Olay Grubu İzinleri yönetimi yetkisi', true, 'KullanıcıOlayGrubuİzinleriİşlemleri', 230);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (43, 'ROLE_BLACK_LIST_LIST', 'Black List listeleme yetkisi', true, 'BlackListİşlemleri', 221);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (44, 'ROLE_BLACK_LIST_MANAGE', 'Black List yönetimi yetkisi', true, 'BlackListİşlemleri', 221);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (45, 'ROLE_LAYER_EXPORT_LIST', 'Katman dışa aktarım listeleme yetkisi', true, 'LayerExportManagement', 300);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (46, 'ROLE_LAYER_EXPORT_MANAGE', 'Katman dışa aktarım yönetim yetkisi', true, 'LayerExportManagement', 301);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (47, 'ROLE_TILE_EXPORT_LIST', 'Harita Altlığı dışa aktarım listeleme yetkisi', true, 'TileExportManagement', 310);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (48, 'ROLE_TILE_EXPORT_MANAGE', 'Harita Altlığı dışa aktarım yönetim yetkisi', true, 'TileExportManagement', 312);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (36, 'ROLE_EVENT_MEDIA_MANAGE', 'Olay medya yönetim işlemleri', false, 'OlayMedyaİşlemleri', 220);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (35, 'ROLE_EVENT_GROUP_MANAGE', 'Olay Grubu yönetim yetkisi', true, 'OlayGrubuİşlemleri', 220);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (33, 'ROLE_USER_EVENT_PERMISSION_MANAGE', 'Kullanıcı Olay İzinleri yönetim yetkisi', false, 'KullanıcıOlayİzinleriİşlemleri', 220);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (32, 'ROLE_USER_EVENT_PERMISSION_LIST', 'Kullanıcı Olay İzinleri listeleme yetkisi', false, 'KullanıcıOlayİzinleriİşlemleri', 220);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (15, 'ROLE_MANAGE_USER_LOG', 'Kullanıcı log yetkisi', false, 'Rapor', 710);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (13, 'ROLE_MANAGE_REPORT', 'Rapor yönetimi yetkisi', false, 'Rapor', 700);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (49, 'ROLE_EVENT_BATCH_OPERATIONS', 'Harita Projesi tablo görünümü sayfasında toplu ve tekli durum değiştirme yetkisi', true, 'Olayİşlemleri', 901);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (50, 'ROLE_EVENT_STATE_VIEW', 'Pasif olayları görme yetkisi', true, 'Olayİşlemleri', 902);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (51, 'ROLE_EVENT_LINK_LIST', 'Olay linklerini listeleme yetkisi', true, 'OlayLinkİşlemleri', 1000);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (52, 'ROLE_EVENT_LINK_MANAGE', 'Olay linkleri yönetim yetkisi', true, 'OlayLinkİşlemleri', 1001);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (53, 'ROLE_FULL_LAYER_PERMISSION', 'Tüm katmanlara erişim yetkisi', true, 'AllLayerPermissionsOperations', 1100);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (54, 'ROLE_ALERT_LIST', 'Alarm listeleme yetkisi', true, 'Alarmİşlemleri', 1200);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (55, 'ROLE_FAKE_LAYER_ID_LIST', 'Katman rol id listleme yetkisi', true, 'KatmanRolIdİzinleriİşlemleri', 1201);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (56, 'ROLE_FAKE_LAYER_ID_MANAGE', 'Katman rol id yönetimi yetkisi', true, 'KatmanRolIdİzinleriİşlemleri', 1202);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (57, 'ROLE_DATATABASE_DUMP_MANAGE', 'Veritabanı yedeklerini yönetim yetkisi', true, 'DatabaseDumpManagement', 1300);
INSERT INTO public.permission (id, name, description, state, group_name, display_order) VALUES (58, 'ROLE_USER_LAST_PAGE_LIST',    'Kullanıcı Son Sayfa Bilgisi Listeleme Yetkisi',true,    'Kullanıcıİşlemleri',    1203);

--
-- Data for Name: profile; Type: TABLE DATA; Schema: public; Owner: imst_admin
--

INSERT INTO public.profile (id, name, description, create_date, update_date, is_default) VALUES (3, 'Default', 'Default yetkilerin olduğu profil', '2024-01-01 13:34:28.881', '2024-01-01 13:34:28.881', true);
INSERT INTO public.profile (id, name, description, create_date, update_date, is_default) VALUES (1, 'Admin', 'Admin yetkilerin olduğu profil', '2020-03-27 10:01:25.498', '2024-01-01 13:34:44.2', false);


--
-- Data for Name: profile_permission; Type: TABLE DATA; Schema: public; Owner: imst_admin
--

INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1354, 3, 25);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1355, 3, 24);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1356, 1, 8);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1357, 1, 6);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1358, 1, 20);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1359, 1, 25);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1360, 1, 21);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1361, 1, 30);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1362, 1, 48);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1363, 1, 9);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1364, 1, 57);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1365, 1, 7);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1366, 1, 51);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1367, 1, 35);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1368, 1, 23);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1369, 1, 56);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1370, 1, 43);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1371, 1, 42);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1372, 1, 19);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1373, 1, 14);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1374, 1, 31);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1375, 1, 2);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1376, 1, 40);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1377, 1, 53);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1378, 1, 34);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1379, 1, 18);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1380, 1, 10);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1381, 1, 17);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1382, 1, 11);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1383, 1, 52);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1384, 1, 50);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1385, 1, 28);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1386, 1, 5);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1387, 1, 12);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1388, 1, 47);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1389, 1, 3);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1390, 1, 39);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1391, 1, 44);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1392, 1, 55);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1393, 1, 37);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1394, 1, 24);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1395, 1, 54);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1396, 1, 45);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1397, 1, 27);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1398, 1, 38);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1399, 1, 41);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1400, 1, 26);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1401, 1, 29);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1402, 1, 4);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1403, 1, 22);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1404, 1, 16);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1405, 1, 1);
INSERT INTO public.profile_permission (id, fk_profile_id, fk_permission_id) VALUES (1406, 1, 46);


--
-- Data for Name: settings; Type: TABLE DATA; Schema: public; Owner: imst_admin
--

INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (11, 'WebTextForLogin', 'Web Modülü', 'Web uygulaması için giriş sayfasında gösterilecek olan başlık yazısıdır.', 'Webloginyönetim', 'text');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (12, 'WebLogoImage', '/web-logo.ico', 'Web projesinde kullanılan logo resmidir', 'ResimYönetimi', 'text');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (14, 'StaticImageRootPath', '/event-map/images/static', 'Proje içerisinde kullanılan resimlerin kaydedildiği yer', 'ResimYönetimi', 'text');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (15, 'WebLoginImage', '/web_login_image.jpg', 'Web projesinde login sayfasında kullanılan resimdir', 'ResimYönetimi', 'text');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (16, 'FaviconImage', '/favicon.ico', 'Favicon resmidir', 'ResimYönetimi', 'text');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (17, 'AdminLogoImage', '/admin-logo.ico', 'Yönetim Paneli projesinde kullanılan logo resmidir', 'ResimYönetimi', 'text');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (10, 'WebTitleForLogin', 'Olay İzleme Uygulaması', 'Web uygulaması için giriş sayfasında gösterilecek olan başlık yazısıdır.', 'Webloginyönetim', 'text');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (18, 'AdminLoginImage', '/admin_login_image.jpg', 'Yönetim Paneli projesinde login sayfasında kullanılan resimdir', 'ResimYönetimi', 'text');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (19, 'AdminTextForLogin', 'Yönetim Modülü', 'Yönetim Paneli uygulaması için giriş sayfasında gösterilecek olan yazıdır.', 'YönetimPaneliloginyönetim', 'text');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (20, 'AdminTitleForLogin', 'Olay İzleme Uygulaması', 'Yönetim Paneli uygulaması için giriş sayfasında gösterilecek olan başlık yazısıdır.', 'YönetimPaneliloginyönetim', 'text');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (21, 'LayerTileRootPath', '/home/tiles/', 'Layer export için tile klasör yolu', 'KatmanExport', 'text');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (4, 'mediaPath', '/event-map/images/media/', 'medyaların kaydedildiği yer', 'media', 'text');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (1, 'APPLICATION_TITLE', 'Olay Haritası', 'Uygulama Başlığı olarak kullanılır', 'settings', 'text');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (5, 'mediaLinkPrefix', '/api/media/', 'medyaların okunduğu konum', 'media', 'text');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (22, 'LayerExportFilePath', '/home/tiledata/', 'Layer export için tiledata klasör yolu', 'KatmanExport', 'text');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (23, 'TileExportFilePath', '/home/mapTiledata/', 'Tile export için tiledata klasör yolu', 'TileExport', 'text');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (24, 'LayerExportEventLoadLimit', '1000', 'Katman Export için çekilecek veri sayısı', 'KatmanExport', 'text');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (6, 'WebPageRefreshTimeInSec', '3000', 'Sayfanın tamamının yenilenmesi için geçmesi gereken süre', 'Web', 'int');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (7, 'WebPageEventCountPerLoad', '20', 'Web projesinde sayfaya bir kerede yüklenecek olay sayısıdır', 'Web', 'int');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (27, 'WebTableRefreshTimeInSec', '10', 'Tablo sayfasının tamamının yenilenmesi için geçmesi gereken süre', 'Web', 'int');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (26, 'WebLoadAllEvents', '5000', 'Web projesinde tek seferse maksimum yüklenebilecek olay sayısı', 'Web', 'int');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (58, 'WebPageBlackListCountPerLoad', '20', 'Web projesinde sayfaya bir kerede yüklenecek black list sayısıdır', 'Web', 'int');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (28, 'MaxCountEventsExcel', '5000', 'Excel ile indirilebilecek maksimum olay sayısı', 'ExcelDownload', 'int');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (29, 'OptionalButtonValue', 'true', 'Opsiyonel Çıkış Butonu Değeri', 'Webloginyönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (30, 'ExcelEventTitle', 'true', 'Excel tablosunda olay başlığı', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (46, 'ExcelEventCreateUser', 'true', 'Excel tablosunda olayı oluşturan kullanıcı', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (55, 'ExcelEventReserved3', 'true', 'Excel tablosunda reserved 3 alanı', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (36, 'ExcelEventGroupName', 'false', 'Excel tablosunda olay grubu', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (33, 'ExcelEventDate', 'false', 'Excel tablosunda olay tarihi', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (35, 'ExcelEventLayerName', 'false', 'Excel tablosunda olay katmanı', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (32, 'ExcelEventDescription', 'false', 'Excel tablosunda olay açıklaması', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (41, 'ExcelEventReservedKey', 'false', 'Excel tablosunda ayrılmış anahtar', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (45, 'ExcelEventBlackListTag', 'true', 'Excel tablosunda olay kara liste etiketi', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (51, 'ExcelEventGroupId', 'true', 'Excel tablosunda grup Id', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (54, 'ExcelEventReserved2', 'true', 'Excel tablosunda reserved 2 alanı', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (52, 'ExcelEventState', 'true', 'Excel tablosunda olay durumu', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (56, 'ExcelEventReserved4', 'true', 'Excel tablosunda reserved 4 alanı', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (44, 'ExcelEventLongitude', 'true', 'Excel tablosunda olay boylamı', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (38, 'ExcelEventCity', 'true', 'Excel tablosunda olay şehri', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (39, 'ExcelEventReservedLink', 'false', 'Excel tablosunda ayrılmış bağlantı', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (48, 'ExcelEventUpdateDate', 'false', 'Excel tablosunda olayın güncellendiği tarih', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (43, 'ExcelEventLatitude', 'false', 'Excel tablosunda olay enlemi', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (40, 'ExcelEventReservedType', 'false', 'Excel tablosunda ayrılmış tür', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (49, 'ExcelEventGroupColor', 'false', 'Excel tablosunda olay grubu rengi', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (31, 'ExcelEventSpot', 'false', 'Excel tablosunda olay kısa açıklama', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (50, 'ExcelEventUserId', 'false', 'Excel tablosunda kullanıcı Id', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (53, 'ExcelEventReserved1', 'false', 'Excel tablosunda reserved 1 alanı', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (47, 'ExcelEventCreateDate', 'false', 'Excel tablosunda olayın oluşturulduğu tarih', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (57, 'ExcelEventReserved5', 'true', 'Excel tablosunda reserved 5 alanı', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (42, 'ExcelEventReservedId', 'true', 'Excel tablosunda ayrılmış Id', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (37, 'ExcelEventCountry', 'true', 'Excel tablosunda olay ülkesi', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (34, 'ExcelEventType', 'true', 'Excel tablosunda olay türü', 'ExcelYönetim', 'checkbox');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (59, 'DatabaseBackupCount', '3', 'S3 de tutulacak veritabanı yedeklerinin sayısı', 'DatabaseBackupYönetim', 'int');
INSERT INTO public.settings (id, settings_key, settings_value, description, group_name, type) VALUES (60, 'DatabaseBackupInterval', '1', 'Veritabanı yedeğinin ne kadar süre aralıkla alınacağı (Saat)', 'DatabaseBackupYönetim', 'int');


--
-- Data for Name: spatial_ref_sys; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: spatial_test; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: spring_session; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: spring_session_attributes; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: state; Type: TABLE DATA; Schema: public; Owner: imst
--

INSERT INTO public.state (id, state_type, description) VALUES (1, 'false', NULL);
INSERT INTO public.state (id, state_type, description) VALUES (2, 'true', NULL);
INSERT INTO public.state (id, state_type, description) VALUES (3, 'blackList', NULL);
INSERT INTO public.state (id, state_type, description) VALUES (4, 'deleted', NULL);


--
-- Data for Name: tag; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: tile_export; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: tile_server; Type: TABLE DATA; Schema: public; Owner: imst
--

INSERT INTO public.tile_server (id, name, url, create_date, update_date, sort_order, state) VALUES (4, 'Humanitarian', 'http://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png', '2020-05-14 15:13:13.053', '2020-05-14 15:13:13.053', 4, true);
INSERT INTO public.tile_server (id, name, url, create_date, update_date, sort_order, state) VALUES (5, 'Map Box', 'https://api.mapbox.com/styles/v1/mapbox/streets-v11/tiles/{z}/{x}/{y}?access_token=pk.eyJ1IjoibXVzdGFmb3piZWsiLCJhIjoiY2thNDJwMnRyMHFicTNkcDB0aDNpdHBsdCJ9.HP6N01TpxzNDPt-DLCt12A', '2020-06-01 10:16:43.799', NULL, 2, true);
INSERT INTO public.tile_server (id, name, url, create_date, update_date, sort_order, state) VALUES (2, 'Open Real', 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', NULL, '2020-06-15 13:41:52.645', 1, true);
INSERT INTO public.tile_server (id, name, url, create_date, update_date, sort_order, state) VALUES (3, 'Wikimediaa', 'https://maps.wikimedia.org/osm-intl/{z}/{x}/{y}.png', '2020-05-14 15:10:06.052', '2020-06-17 13:30:57.126', 3, true);


--
-- Data for Name: user; Type: TABLE DATA; Schema: public; Owner: imst_admin
--

INSERT INTO public."user" (id, username, name, create_date, fk_profile_id, state, password, update_date, is_db_user, provider_user_id) VALUES (1, 'admin', 'admin', '2022-08-26 15:49:58.282', 1, true, '$2a$10$5cf3F570KhO9cXrObF8G9etGlXemxernb5H870RPj9p/tTdja/raa', '2022-08-26 15:49:58.282', true, NULL);


--
-- Data for Name: user_event_group_permission; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: user_group_id; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: user_layer_permission; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: user_settings; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Data for Name: user_settings_type; Type: TABLE DATA; Schema: public; Owner: imst
--

INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (2, 'TitleAndDescription', NULL, 'Başlık, Kısa Açıklama ve Açıklama', 'Katman', 'text', true, 1);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (3, 'City', NULL, 'Şehir', 'Katman', 'text', true, 2);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (4, 'Country', NULL, 'Ülke', 'Katman', 'text', true, 3);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (11, 'ExcelEventTitle', NULL, 'Excel tablosunda olay başlığı', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (12, 'ExcelEventSpot', NULL, 'Excel tablosunda olay kısa açıklama', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (13, 'ExcelEventDescription', NULL, 'Excel tablosunda olay açıklaması', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (14, 'ExcelEventDate', NULL, 'Excel tablosunda olay tarihi', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (15, 'ExcelEventType', NULL, 'Excel tablosunda olay türü', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (16, 'ExcelEventLayerName', NULL, 'Excel tablosunda olay katmanı', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (17, 'ExcelEventGroupName', NULL, 'Excel tablosunda olay grubu', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (18, 'ExcelEventCountry', NULL, 'Excel tablosunda olay ülkesi', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (19, 'ExcelEventCity', NULL, 'Excel tablosunda olay şehri', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (20, 'ExcelEventReservedLink', NULL, 'Excel tablosunda ayrılmış bağlantı', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (21, 'ExcelEventReservedType', NULL, 'Excel tablosunda ayrılmış tür', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (22, 'ExcelEventReservedKey', NULL, 'Excel tablosunda ayrılmış anahtar', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (23, 'ExcelEventReservedId', NULL, 'Excel tablosunda ayrılmış Id', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (24, 'ExcelEventLatitude', NULL, 'Excel tablosunda olay enlemi', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (25, 'ExcelEventLongitude', NULL, 'Excel tablosunda olay boylamı', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (26, 'ExcelEventBlackListTag', NULL, 'Excel tablosunda olay kara liste etiketi', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (27, 'ExcelEventCreateUser', NULL, 'Excel tablosunda olayı oluşturan kullanıcı', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (28, 'ExcelEventCreateDate', NULL, 'Excel tablosunda olayın oluşturulduğu tarih', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (29, 'ExcelEventUpdateDate', NULL, 'Excel tablosunda olayın güncellendiği tarih', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (30, 'ExcelEventGroupColor', NULL, 'Excel tablosunda olay grubu rengi', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (31, 'ExcelEventUserId', NULL, 'Excel tablosunda kullanıcı Id', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (32, 'ExcelEventGroupId', NULL, 'Excel tablosunda grup Id', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (33, 'ExcelEventState', NULL, 'Excel tablosunda olay durumu', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (34, 'ExcelEventReserved1', NULL, 'Excel tablosunda reserved 1 alanı', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (35, 'ExcelEventReserved2', NULL, 'Excel tablosunda reserved 2 alanı', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (36, 'ExcelEventReserved3', NULL, 'Excel tablosunda reserved 3 alanı', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (37, 'ExcelEventReserved4', NULL, 'Excel tablosunda reserved 4 alanı', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (38, 'ExcelEventReserved5', NULL, 'Excel tablosunda reserved 5 alanı', 'ExcelYönetim', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (39, 'EventTypes', NULL, 'Olay Türleri', 'Katman', 'multiselect', true, 4);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (40, 'EventGroups', NULL, 'Olay Grupları', 'Katman', 'label', true, 10);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (41, 'EventsWithAlarm', NULL, 'Alarmlı Olaylar', 'Katman', 'checkbox', true, 9);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (42, 'StartingDate', NULL, 'Başlangıç Tarihi', 'Katman', 'dateTimePicker', true, 5);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (43, 'EndDate', NULL, 'Bitiş Tarihi', 'Katman', 'dateTimePicker', true, 6);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (45, 'EventTableViewTitle', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (46, 'EventTableViewSpot', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (47, 'EventTableViewDescription', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (48, 'EventTableViewEventDate', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (49, 'EventTableViewEventGroup', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (50, 'EventTableViewEventType', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (51, 'EventTableViewCity', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (52, 'EventTableViewCountry', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (53, 'EventTableViewLatitudeAndLongitude', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (55, 'EventTableViewState', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (56, 'EventTableViewReservedKey', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (57, 'EventTableViewReservedType', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (58, 'EventTableViewReservedId', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (59, 'EventTableViewReservedLink', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (60, 'EventTableViewBlackListTag', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (61, 'EventTableViewReserved1', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (62, 'EventTableViewReserved2', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (63, 'EventTableViewReserved3', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (64, 'EventTableViewReserved4', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (65, 'EventTableViewReserved5', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (66, 'EventTableViewTag', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (67, 'EventTableViewMedia', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (68, 'EventTableViewAlertEvent', NULL, NULL, 'TabloGörünümüSayfasıYönetimi', 'checkbox', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (69, 'MaxCountEventsExcel', NULL, 'Excel ile indirilebilecek maksimum olay sayısı', 'ExcelDownload', 'int', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (70, 'FirstPageOpened', NULL, 'Açılan İlk Sayfa', 'FirstPageOpened', 'openPageSelect', false, NULL);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (71, 'MapCoordinates', '', 'Koordinat Bilgileri', 'Katman', 'double pricision', true, 7);
INSERT INTO public.user_settings_type (id, settings_key, settings_value, description, group_name, type, is_layer, "order") VALUES (72, 'MapZoom', '', 'Haritanın Zoom Seviyesi', 'Katman', 'int', true, 8);


--
-- Data for Name: user_user_id; Type: TABLE DATA; Schema: public; Owner: imst
--



--
-- Name: alert_event_cron_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.alert_event_cron_id_seq', 1, false);


--
-- Name: alert_event_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.alert_event_id_seq', 57593, true);


--
-- Name: alert_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.alert_id_seq', 265, true);


--
-- Name: alert_state_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.alert_state_id_seq', 56, true);


--
-- Name: black_list_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.black_list_id_seq', 121, true);


--
-- Name: comment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.comment_id_seq', 1, false);


--
-- Name: count_events_time_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.count_events_time_id_seq', 1, false);


--
-- Name: database_dump_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.database_dump_id_seq', 1, false);


--
-- Name: event_black_list_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.event_black_list_id_seq', 1, false);


--
-- Name: event_group_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.event_group_id_seq', 399, true);


--
-- Name: event_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.event_id_seq', 252022, true);


--
-- Name: event_link_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.event_link_id_seq', 1, false);


--
-- Name: event_media_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.event_media_id_seq', 97201, true);


--
-- Name: event_tag_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.event_tag_id_seq', 834, true);


--
-- Name: event_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.event_type_id_seq', 225, true);


--
-- Name: fake_layer_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.fake_layer_id_seq', 1, false);


--
-- Name: geo_layer_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst_admin
--

SELECT pg_catalog.setval('public.geo_layer_id_seq', 13, true);


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.hibernate_sequence', 7, true);


--
-- Name: layer_export_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.layer_export_id_seq', 321, true);


--
-- Name: layer_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.layer_id_seq', 284, true);


--
-- Name: log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.log_id_seq', 8793, true);


--
-- Name: map_area_group_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.map_area_group_id_seq', 14, true);


--
-- Name: map_area_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.map_area_id_seq', 16, true);


--
-- Name: profile_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst_admin
--

SELECT pg_catalog.setval('public.profile_id_seq', 3, true);


--
-- Name: profile_permission_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst_admin
--

SELECT pg_catalog.setval('public.profile_permission_id_seq', 1406, true);


--
-- Name: settings_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst_admin
--

SELECT pg_catalog.setval('public.settings_id_seq', 52, true);


--
-- Name: spatial_test_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.spatial_test_id_seq', 22, true);


--
-- Name: tag_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.tag_id_seq', 19, true);


--
-- Name: tile_export_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.tile_export_id_seq', 36, true);


--
-- Name: tile_server_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.tile_server_id_seq', 6, true);


--
-- Name: user_event_group_permission_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.user_event_group_permission_id_seq', 613, true);


--
-- Name: user_group_id_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.user_group_id_id_seq', 22, true);


--
-- Name: user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst_admin
--

SELECT pg_catalog.setval('public.user_id_seq', 92, true);


--
-- Name: user_layer_permission_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.user_layer_permission_id_seq', 143, true);


--
-- Name: user_settings_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.user_settings_id_seq', 1, false);


--
-- Name: user_settings_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.user_settings_type_id_seq', 1, false);


--
-- Name: user_user_id_id_seq; Type: SEQUENCE SET; Schema: public; Owner: imst
--

SELECT pg_catalog.setval('public.user_user_id_id_seq', 79, true);


--
-- Name: user_group_id UserGroupId_pkey; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.user_group_id
    ADD CONSTRAINT "UserGroupId_pkey" PRIMARY KEY (id);


--
-- Name: user_user_id UserUserId_pkey; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.user_user_id
    ADD CONSTRAINT "UserUserId_pkey" PRIMARY KEY (id);


--
-- Name: action_state action_state_pkey; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.action_state
    ADD CONSTRAINT action_state_pkey PRIMARY KEY (id);


--
-- Name: alert_event_cron alert_event_cron_pkey; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.alert_event_cron
    ADD CONSTRAINT alert_event_cron_pkey PRIMARY KEY (id);


--
-- Name: alert_event alert_event_pk; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.alert_event
    ADD CONSTRAINT alert_event_pk PRIMARY KEY (id);


--
-- Name: alert alert_pk; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.alert
    ADD CONSTRAINT alert_pk PRIMARY KEY (id);


--
-- Name: alert_state alert_state_pk; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.alert_state
    ADD CONSTRAINT alert_state_pk PRIMARY KEY (id);


--
-- Name: black_list black_list_pkey; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.black_list
    ADD CONSTRAINT black_list_pkey PRIMARY KEY (id);


--
-- Name: events_time_count count_events_time_pkey; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.events_time_count
    ADD CONSTRAINT count_events_time_pkey PRIMARY KEY (id);


--
-- Name: database_dump database_dump_pkey; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.database_dump
    ADD CONSTRAINT database_dump_pkey PRIMARY KEY (id);


--
-- Name: event_black_list event_black_list_pkey; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.event_black_list
    ADD CONSTRAINT event_black_list_pkey PRIMARY KEY (id);


--
-- Name: event_column event_column_pkey; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.event_column
    ADD CONSTRAINT event_column_pkey PRIMARY KEY (id);


--
-- Name: event_link event_link_pkey; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.event_link
    ADD CONSTRAINT event_link_pkey PRIMARY KEY (id);


--
-- Name: event_media event_media_pk; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.event_media
    ADD CONSTRAINT event_media_pk PRIMARY KEY (id);


--
-- Name: event event_pk; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.event
    ADD CONSTRAINT event_pk PRIMARY KEY (id);


--
-- Name: event_group event_side_pk; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.event_group
    ADD CONSTRAINT event_side_pk PRIMARY KEY (id);


--
-- Name: event_tag event_tag_pk; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.event_tag
    ADD CONSTRAINT event_tag_pk PRIMARY KEY (id);


--
-- Name: event_type event_type_pk; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.event_type
    ADD CONSTRAINT event_type_pk PRIMARY KEY (id);


--
-- Name: fake_layer_id fake_layer_id_pkey; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.fake_layer_id
    ADD CONSTRAINT fake_layer_id_pkey PRIMARY KEY (id);


--
-- Name: geo_layer geo_layer_pk; Type: CONSTRAINT; Schema: public; Owner: imst_admin
--

ALTER TABLE ONLY public.geo_layer
    ADD CONSTRAINT geo_layer_pk PRIMARY KEY (id);


--
-- Name: layer_export layer_export_pk; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.layer_export
    ADD CONSTRAINT layer_export_pk PRIMARY KEY (id);


--
-- Name: user_layer_permission layer_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.user_layer_permission
    ADD CONSTRAINT layer_permission_pkey PRIMARY KEY (id);


--
-- Name: layer layer_pk; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.layer
    ADD CONSTRAINT layer_pk PRIMARY KEY (id);


--
-- Name: log_type log_type_pk; Type: CONSTRAINT; Schema: public; Owner: imst_admin
--

ALTER TABLE ONLY public.log_type
    ADD CONSTRAINT log_type_pk PRIMARY KEY (id);


--
-- Name: map_area_group map_area_group_pk; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.map_area_group
    ADD CONSTRAINT map_area_group_pk PRIMARY KEY (id);


--
-- Name: map_area map_area_pk; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.map_area
    ADD CONSTRAINT map_area_pk PRIMARY KEY (id);


--
-- Name: permission permission_pk; Type: CONSTRAINT; Schema: public; Owner: imst_admin
--

ALTER TABLE ONLY public.permission
    ADD CONSTRAINT permission_pk PRIMARY KEY (id);


--
-- Name: profile_permission profile_permission_pk; Type: CONSTRAINT; Schema: public; Owner: imst_admin
--

ALTER TABLE ONLY public.profile_permission
    ADD CONSTRAINT profile_permission_pk PRIMARY KEY (id);


--
-- Name: profile profile_pk; Type: CONSTRAINT; Schema: public; Owner: imst_admin
--

ALTER TABLE ONLY public.profile
    ADD CONSTRAINT profile_pk PRIMARY KEY (id);


--
-- Name: settings settings_pk; Type: CONSTRAINT; Schema: public; Owner: imst_admin
--

ALTER TABLE ONLY public.settings
    ADD CONSTRAINT settings_pk PRIMARY KEY (id);


--
-- Name: spatial_test spatial_test_pk; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.spatial_test
    ADD CONSTRAINT spatial_test_pk PRIMARY KEY (id);


--
-- Name: spring_session_attributes spring_session_attributes_pk; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.spring_session_attributes
    ADD CONSTRAINT spring_session_attributes_pk PRIMARY KEY (session_primary_id, attribute_name);


--
-- Name: spring_session spring_session_pk; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.spring_session
    ADD CONSTRAINT spring_session_pk PRIMARY KEY (primary_id);


--
-- Name: state state_pkey; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.state
    ADD CONSTRAINT state_pkey PRIMARY KEY (id);


--
-- Name: tag tag_pk; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.tag
    ADD CONSTRAINT tag_pk PRIMARY KEY (id);


--
-- Name: tile_export tile_export_pk; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.tile_export
    ADD CONSTRAINT tile_export_pk PRIMARY KEY (id);


--
-- Name: tile_server tile_server_pk; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.tile_server
    ADD CONSTRAINT tile_server_pk PRIMARY KEY (id);


--
-- Name: user_settings_type uniq_settings_key_settings_key; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.user_settings_type
    ADD CONSTRAINT uniq_settings_key_settings_key UNIQUE (settings_key);


--
-- Name: alert_event_cron unique_event_id; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.alert_event_cron
    ADD CONSTRAINT unique_event_id UNIQUE (fk_event_id);


--
-- Name: database_dump unique_key; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.database_dump
    ADD CONSTRAINT unique_key UNIQUE (key);


--
-- Name: user_event_group_permission user_event_group_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.user_event_group_permission
    ADD CONSTRAINT user_event_group_permission_pkey PRIMARY KEY (id);


--
-- Name: log user_log_pk; Type: CONSTRAINT; Schema: public; Owner: imst_admin
--

ALTER TABLE ONLY public.log
    ADD CONSTRAINT user_log_pk PRIMARY KEY (id);


--
-- Name: user user_pk; Type: CONSTRAINT; Schema: public; Owner: imst_admin
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_pk PRIMARY KEY (id);


--
-- Name: user_settings user_settings_pkey; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.user_settings
    ADD CONSTRAINT user_settings_pkey PRIMARY KEY (id);


--
-- Name: user_settings_type user_settings_type_pkey; Type: CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.user_settings_type
    ADD CONSTRAINT user_settings_type_pkey PRIMARY KEY (id);


--
-- Name: alert_event_event_id_db_name_index; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX alert_event_event_id_db_name_index ON public.alert_event USING btree (event_id_db_name);


--
-- Name: alert_state_db_name_uindex; Type: INDEX; Schema: public; Owner: imst
--

CREATE UNIQUE INDEX alert_state_db_name_uindex ON public.alert_state USING btree (db_name);


--
-- Name: event_fk_event_group_id_uindex; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX event_fk_event_group_id_uindex ON public.event USING btree (fk_event_group_id);


--
-- Name: event_group_fk_layer_id_name_uindex; Type: INDEX; Schema: public; Owner: imst
--

CREATE UNIQUE INDEX event_group_fk_layer_id_name_uindex ON public.event_group USING btree (fk_layer_id, name);


--
-- Name: event_id_uindex; Type: INDEX; Schema: public; Owner: imst
--

CREATE UNIQUE INDEX event_id_uindex ON public.event USING btree (id);


--
-- Name: event_media_id_uindex; Type: INDEX; Schema: public; Owner: imst
--

CREATE UNIQUE INDEX event_media_id_uindex ON public.event_media USING btree (id);


--
-- Name: event_side_id_uindex; Type: INDEX; Schema: public; Owner: imst
--

CREATE UNIQUE INDEX event_side_id_uindex ON public.event_group USING btree (id);


--
-- Name: event_tag_id_uindex; Type: INDEX; Schema: public; Owner: imst
--

CREATE UNIQUE INDEX event_tag_id_uindex ON public.event_tag USING btree (id);


--
-- Name: event_type_id_uindex; Type: INDEX; Schema: public; Owner: imst
--

CREATE UNIQUE INDEX event_type_id_uindex ON public.event_type USING btree (id);


--
-- Name: fki_fk_black_list_action_state_id; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX fki_fk_black_list_action_state_id ON public.black_list USING btree (fk_action_state_id);


--
-- Name: fki_fk_black_list_event_group_id; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX fki_fk_black_list_event_group_id ON public.black_list USING btree (fk_event_group_id);


--
-- Name: fki_fk_black_list_event_type_id; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX fki_fk_black_list_event_type_id ON public.black_list USING btree (fk_event_type_id);


--
-- Name: fki_fk_black_list_state_id; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX fki_fk_black_list_state_id ON public.black_list USING btree (fk_state_id);


--
-- Name: fki_fk_count_events_time_event_group_id; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX fki_fk_count_events_time_event_group_id ON public.events_time_count USING btree (fk_event_group_id);


--
-- Name: fki_fk_count_events_time_layer_id; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX fki_fk_count_events_time_layer_id ON public.events_time_count USING btree (fk_layer_id);


--
-- Name: fki_fk_event_group_id; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX fki_fk_event_group_id ON public.user_event_group_permission USING btree (fk_event_group_id);


--
-- Name: fki_fk_event_id; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX fki_fk_event_id ON public.alert_event_cron USING btree (fk_event_id);


--
-- Name: fki_fk_event_link_event_column; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX fki_fk_event_link_event_column ON public.event_link USING btree (fk_event_column_id);


--
-- Name: fki_fk_event_state_id; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX fki_fk_event_state_id ON public.event USING btree (fk_state_id);


--
-- Name: fki_fk_fake_layer_id_; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX fki_fk_fake_layer_id_ ON public.fake_layer_id USING btree (fk_layer_id);


--
-- Name: fki_fk_layer_export_tile_server_id; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX fki_fk_layer_export_tile_server_id ON public.layer_export USING btree (fk_tile_server_id);


--
-- Name: fki_fk_layer_id; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX fki_fk_layer_id ON public.user_layer_permission USING btree (fk_layer_id);


--
-- Name: fki_fk_tile_export_tile_server_id; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX fki_fk_tile_export_tile_server_id ON public.tile_export USING btree (fk_tile_server_id);


--
-- Name: fki_fk_user_id; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX fki_fk_user_id ON public.user_event_group_permission USING btree (fk_user_id);


--
-- Name: fki_fk_user_settings_type_id; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX fki_fk_user_settings_type_id ON public.user_settings USING btree (fk_user_settings_type_id);


--
-- Name: fki_fk_user_settings_user_id; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX fki_fk_user_settings_user_id ON public.user_settings USING btree (fk_user_id);


--
-- Name: fki_fkk_user_id; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX fki_fkk_user_id ON public.user_layer_permission USING btree (fk_user_id);


--
-- Name: fki_user_id; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX fki_user_id ON public.event USING btree (user_id);


--
-- Name: geo_layer_id_uindex; Type: INDEX; Schema: public; Owner: imst_admin
--

CREATE UNIQUE INDEX geo_layer_id_uindex ON public.geo_layer USING btree (id);


--
-- Name: layer_id_uindex; Type: INDEX; Schema: public; Owner: imst
--

CREATE UNIQUE INDEX layer_id_uindex ON public.layer USING btree (id);


--
-- Name: layer_name_uindex; Type: INDEX; Schema: public; Owner: imst
--

CREATE UNIQUE INDEX layer_name_uindex ON public.layer USING btree (name);


--
-- Name: log_type_id_uindex; Type: INDEX; Schema: public; Owner: imst_admin
--

CREATE UNIQUE INDEX log_type_id_uindex ON public.log_type USING btree (id);


--
-- Name: map_area_group_id_uindex; Type: INDEX; Schema: public; Owner: imst
--

CREATE UNIQUE INDEX map_area_group_id_uindex ON public.map_area_group USING btree (id);


--
-- Name: map_area_id_uindex; Type: INDEX; Schema: public; Owner: imst
--

CREATE UNIQUE INDEX map_area_id_uindex ON public.map_area USING btree (id);


--
-- Name: permission_id_uindex; Type: INDEX; Schema: public; Owner: imst_admin
--

CREATE UNIQUE INDEX permission_id_uindex ON public.permission USING btree (id);


--
-- Name: profile_id_uindex; Type: INDEX; Schema: public; Owner: imst_admin
--

CREATE UNIQUE INDEX profile_id_uindex ON public.profile USING btree (id);


--
-- Name: profile_permission_id_uindex; Type: INDEX; Schema: public; Owner: imst_admin
--

CREATE UNIQUE INDEX profile_permission_id_uindex ON public.profile_permission USING btree (id);


--
-- Name: settings_id_uindex; Type: INDEX; Schema: public; Owner: imst_admin
--

CREATE UNIQUE INDEX settings_id_uindex ON public.settings USING btree (id);


--
-- Name: spring_session_ix1; Type: INDEX; Schema: public; Owner: imst
--

CREATE UNIQUE INDEX spring_session_ix1 ON public.spring_session USING btree (session_id);


--
-- Name: spring_session_ix2; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX spring_session_ix2 ON public.spring_session USING btree (expiry_time);


--
-- Name: spring_session_ix3; Type: INDEX; Schema: public; Owner: imst
--

CREATE INDEX spring_session_ix3 ON public.spring_session USING btree (principal_name);


--
-- Name: tag_id_uindex; Type: INDEX; Schema: public; Owner: imst
--

CREATE UNIQUE INDEX tag_id_uindex ON public.tag USING btree (id);


--
-- Name: tile_server_id_uindex; Type: INDEX; Schema: public; Owner: imst
--

CREATE UNIQUE INDEX tile_server_id_uindex ON public.tile_server USING btree (id);


--
-- Name: user_id_uindex; Type: INDEX; Schema: public; Owner: imst_admin
--

CREATE UNIQUE INDEX user_id_uindex ON public."user" USING btree (id);


--
-- Name: user_log_id_uindex; Type: INDEX; Schema: public; Owner: imst_admin
--

CREATE UNIQUE INDEX user_log_id_uindex ON public.log USING btree (id);


--
-- Name: user_username_uindex; Type: INDEX; Schema: public; Owner: imst_admin
--

CREATE UNIQUE INDEX user_username_uindex ON public."user" USING btree (username);


--
-- Name: alert_event fk_alert_event_alert_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.alert_event
    ADD CONSTRAINT fk_alert_event_alert_id FOREIGN KEY (fk_alert_id) REFERENCES public.alert(id);


--
-- Name: alert fk_alert_event_group_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.alert
    ADD CONSTRAINT fk_alert_event_group_id FOREIGN KEY (fk_event_group_id) REFERENCES public.event_group(id);


--
-- Name: alert fk_alert_event_type_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.alert
    ADD CONSTRAINT fk_alert_event_type_id FOREIGN KEY (fk_event_type_id) REFERENCES public.event_type(id);


--
-- Name: alert_event fk_alert_event_user_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.alert_event
    ADD CONSTRAINT fk_alert_event_user_id FOREIGN KEY (fk_user_id) REFERENCES public."user"(id);


--
-- Name: alert fk_alert_layer_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.alert
    ADD CONSTRAINT fk_alert_layer_id FOREIGN KEY (fk_layer_id) REFERENCES public.layer(id);


--
-- Name: alert fk_alert_user_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.alert
    ADD CONSTRAINT fk_alert_user_id FOREIGN KEY (fk_user_id) REFERENCES public."user"(id);


--
-- Name: black_list fk_black_list_action_state_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.black_list
    ADD CONSTRAINT fk_black_list_action_state_id FOREIGN KEY (fk_action_state_id) REFERENCES public.action_state(id) NOT VALID;


--
-- Name: black_list fk_black_list_event_group_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.black_list
    ADD CONSTRAINT fk_black_list_event_group_id FOREIGN KEY (fk_event_group_id) REFERENCES public.event_group(id);


--
-- Name: black_list fk_black_list_event_type_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.black_list
    ADD CONSTRAINT fk_black_list_event_type_id FOREIGN KEY (fk_event_type_id) REFERENCES public.event_type(id);


--
-- Name: black_list fk_black_list_state_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.black_list
    ADD CONSTRAINT fk_black_list_state_id FOREIGN KEY (fk_state_id) REFERENCES public.state(id) NOT VALID;


--
-- Name: events_time_count fk_count_events_time_event_group_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.events_time_count
    ADD CONSTRAINT fk_count_events_time_event_group_id FOREIGN KEY (fk_event_group_id) REFERENCES public.event_group(id);


--
-- Name: events_time_count fk_count_events_time_layer_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.events_time_count
    ADD CONSTRAINT fk_count_events_time_layer_id FOREIGN KEY (fk_layer_id) REFERENCES public.layer(id);


--
-- Name: event fk_event_event_group_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.event
    ADD CONSTRAINT fk_event_event_group_id FOREIGN KEY (fk_event_group_id) REFERENCES public.event_group(id);


--
-- Name: event fk_event_event_type_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.event
    ADD CONSTRAINT fk_event_event_type_id FOREIGN KEY (fk_event_type_id) REFERENCES public.event_type(id);


--
-- Name: user_event_group_permission fk_event_group_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.user_event_group_permission
    ADD CONSTRAINT fk_event_group_id FOREIGN KEY (fk_event_group_id) REFERENCES public.event_group(id) NOT VALID;


--
-- Name: event_group fk_event_group_layer_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.event_group
    ADD CONSTRAINT fk_event_group_layer_id FOREIGN KEY (fk_layer_id) REFERENCES public.layer(id);


--
-- Name: alert_event_cron fk_event_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.alert_event_cron
    ADD CONSTRAINT fk_event_id FOREIGN KEY (fk_event_id) REFERENCES public.event(id);


--
-- Name: event_link fk_event_link_event_column; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.event_link
    ADD CONSTRAINT fk_event_link_event_column FOREIGN KEY (fk_event_column_id) REFERENCES public.event_column(id);


--
-- Name: event_media fk_event_media_event_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.event_media
    ADD CONSTRAINT fk_event_media_event_id FOREIGN KEY (fk_event_id) REFERENCES public.event(id);


--
-- Name: event fk_event_state_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.event
    ADD CONSTRAINT fk_event_state_id FOREIGN KEY (fk_state_id) REFERENCES public.state(id) NOT VALID;


--
-- Name: event_tag fk_event_tag_event_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.event_tag
    ADD CONSTRAINT fk_event_tag_event_id FOREIGN KEY (fk_event_id) REFERENCES public.event(id);


--
-- Name: event_tag fk_event_tag_tag_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.event_tag
    ADD CONSTRAINT fk_event_tag_tag_id FOREIGN KEY (fk_tag_id) REFERENCES public.tag(id);


--
-- Name: fake_layer_id fk_fake_layer_id_; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.fake_layer_id
    ADD CONSTRAINT fk_fake_layer_id_ FOREIGN KEY (fk_layer_id) REFERENCES public.layer(id);


--
-- Name: geo_layer fk_geo_layer_layer_id; Type: FK CONSTRAINT; Schema: public; Owner: imst_admin
--

ALTER TABLE ONLY public.geo_layer
    ADD CONSTRAINT fk_geo_layer_layer_id FOREIGN KEY (fk_layer_id) REFERENCES public.layer(id);


--
-- Name: layer_export fk_layer_export_layer_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.layer_export
    ADD CONSTRAINT fk_layer_export_layer_id FOREIGN KEY (fk_layer_id) REFERENCES public.layer(id);


--
-- Name: layer_export fk_layer_export_tile_server_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.layer_export
    ADD CONSTRAINT fk_layer_export_tile_server_id FOREIGN KEY (fk_tile_server_id) REFERENCES public.tile_server(id) NOT VALID;


--
-- Name: user_layer_permission fk_layer_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.user_layer_permission
    ADD CONSTRAINT fk_layer_id FOREIGN KEY (fk_layer_id) REFERENCES public.layer(id) NOT VALID;


--
-- Name: map_area_group fk_map_area_group_layer_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.map_area_group
    ADD CONSTRAINT fk_map_area_group_layer_id FOREIGN KEY (fk_layer_id) REFERENCES public.layer(id);


--
-- Name: map_area fk_map_area_map_area_group_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.map_area
    ADD CONSTRAINT fk_map_area_map_area_group_id FOREIGN KEY (fk_map_area_group_id) REFERENCES public.map_area_group(id);


--
-- Name: profile_permission fk_profile_permission_permission_id; Type: FK CONSTRAINT; Schema: public; Owner: imst_admin
--

ALTER TABLE ONLY public.profile_permission
    ADD CONSTRAINT fk_profile_permission_permission_id FOREIGN KEY (fk_permission_id) REFERENCES public.permission(id);


--
-- Name: profile_permission fk_profile_permission_profile_id; Type: FK CONSTRAINT; Schema: public; Owner: imst_admin
--

ALTER TABLE ONLY public.profile_permission
    ADD CONSTRAINT fk_profile_permission_profile_id FOREIGN KEY (fk_profile_id) REFERENCES public.profile(id);


--
-- Name: tile_export fk_tile_export_tile_server_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.tile_export
    ADD CONSTRAINT fk_tile_export_tile_server_id FOREIGN KEY (fk_tile_server_id) REFERENCES public.tile_server(id) NOT VALID;


--
-- Name: user_layer_permission fk_user_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.user_layer_permission
    ADD CONSTRAINT fk_user_id FOREIGN KEY (fk_user_id) REFERENCES public."user"(id) NOT VALID;


--
-- Name: user_event_group_permission fk_user_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.user_event_group_permission
    ADD CONSTRAINT fk_user_id FOREIGN KEY (fk_user_id) REFERENCES public."user"(id) NOT VALID;


--
-- Name: user fk_user_profile_id; Type: FK CONSTRAINT; Schema: public; Owner: imst_admin
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT fk_user_profile_id FOREIGN KEY (fk_profile_id) REFERENCES public.profile(id);


--
-- Name: user_settings fk_user_settings_layer_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.user_settings
    ADD CONSTRAINT fk_user_settings_layer_id FOREIGN KEY (fk_layer_id) REFERENCES public.layer(id);


--
-- Name: user_settings fk_user_settings_type_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.user_settings
    ADD CONSTRAINT fk_user_settings_type_id FOREIGN KEY (fk_user_settings_type_id) REFERENCES public.user_settings_type(id);


--
-- Name: user_settings fk_user_settings_user_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.user_settings
    ADD CONSTRAINT fk_user_settings_user_id FOREIGN KEY (fk_user_id) REFERENCES public."user"(id);


--
-- Name: user_group_id fk_user_user_group_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.user_group_id
    ADD CONSTRAINT fk_user_user_group_id FOREIGN KEY (fk_user_id) REFERENCES public."user"(id);


--
-- Name: user_user_id fk_user_user_user_id; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.user_user_id
    ADD CONSTRAINT fk_user_user_user_id FOREIGN KEY (fk_user_id) REFERENCES public."user"(id);


--
-- Name: spring_session_attributes spring_session_attributes_fk; Type: FK CONSTRAINT; Schema: public; Owner: imst
--

ALTER TABLE ONLY public.spring_session_attributes
    ADD CONSTRAINT spring_session_attributes_fk FOREIGN KEY (session_primary_id) REFERENCES public.spring_session(primary_id) ON DELETE CASCADE;


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: imst
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--


SELECT setval('action_state_id_seq', (SELECT MAX(id) FROM action_state));
SELECT setval('alert_id_seq', (SELECT MAX(id) FROM alert));
SELECT setval('alert_event_id_seq', (SELECT MAX(id) FROM alert_event));
SELECT setval('alert_event_cron_id_seq', (SELECT MAX(id) FROM alert_event_cron));
SELECT setval('alert_state_id_seq', (SELECT MAX(id) FROM alert_state));
SELECT setval('black_list_id_seq', (SELECT MAX(id) FROM black_list));
SELECT setval('database_dump_id_seq', (SELECT MAX(id) FROM database_dump));
SELECT setval('event_id_seq', (SELECT MAX(id) FROM event));
SELECT setval('event_black_list_id_seq', (SELECT MAX(id) FROM event_black_list));
SELECT setval('event_column_id_seq', (SELECT MAX(id) FROM event_column));
SELECT setval('event_group_id_seq', (SELECT MAX(id) FROM event_group));
SELECT setval('event_link_id_seq', (SELECT MAX(id) FROM event_link));
SELECT setval('event_media_id_seq', (SELECT MAX(id) FROM event_media));
SELECT setval('event_tag_id_seq', (SELECT MAX(id) FROM event_tag));
SELECT setval('event_type_id_seq', (SELECT MAX(id) FROM event_type));
SELECT setval('events_time_count_id_seq', (SELECT MAX(id) FROM events_time_count));
SELECT setval('fake_layer_id_id_seq', (SELECT MAX(id) FROM fake_layer_id));
SELECT setval('geo_layer_id_seq', (SELECT MAX(id) FROM geo_layer));
SELECT setval('layer_id_seq', (SELECT MAX(id) FROM layer));
SELECT setval('layer_export_id_seq', (SELECT MAX(id) FROM layer_export));
SELECT setval('log_id_seq', (SELECT MAX(id) FROM log));
SELECT setval('map_area_id_seq', (SELECT MAX(id) FROM map_area));
SELECT setval('map_area_group_id_seq', (SELECT MAX(id) FROM map_area_group));
SELECT setval('profile_id_seq', (SELECT MAX(id) FROM profile));
SELECT setval('profile_permission_id_seq', (SELECT MAX(id) FROM profile_permission));
SELECT setval('settings_id_seq', (SELECT MAX(id) FROM settings));
SELECT setval('spatial_test_id_seq', (SELECT MAX(id) FROM spatial_test));
SELECT setval('state_id_seq', (SELECT MAX(id) FROM state));
SELECT setval('tag_id_seq', (SELECT MAX(id) FROM tag));
SELECT setval('tile_export_id_seq', (SELECT MAX(id) FROM tile_export));
SELECT setval('tile_server_id_seq', (SELECT MAX(id) FROM tile_server));
SELECT setval('user_id_seq', (SELECT MAX(id) FROM "user"));
SELECT setval('user_event_group_permission_id_seq', (SELECT MAX(id) FROM user_event_group_permission));
SELECT setval('user_group_id_id_seq', (SELECT MAX(id) FROM user_group_id));
SELECT setval('user_layer_permission_id_seq', (SELECT MAX(id) FROM user_layer_permission));
SELECT setval('user_settings_id_seq', (SELECT MAX(id) FROM user_settings));
SELECT setval('user_settings_type_id_seq', (SELECT MAX(id) FROM user_settings_type));
SELECT setval('user_user_id_id_seq', (SELECT MAX(id) FROM user_user_id));