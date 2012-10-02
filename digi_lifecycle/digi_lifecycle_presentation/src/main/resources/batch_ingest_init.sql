CREATE TABLE IF NOT EXISTS dlc_batch_ingest_log(
    id serial NOT NULL,
    name character varying NOT NULL,
    step character varying NOT NULL,
    status character varying NOT NULL,
    errorlevel character varying NOT NULL,
    startdate timestamp NOT NULL,
    enddate timestamp,
    user_id character varying NOT NULL,
    context_id character varying NOT NULL,
    finished_items integer,
    total_items integer
);
CREATE TABLE IF NOT EXISTS dlc_batch_ingest_log_item(
    id serial NOT NULL,
    name character varying NOT NULL,
    errorlevel character varying,
    startdate timestamp,
    enddate timestamp,
    log_id integer NOT NULL,
    message character varying,
    item_id character varying,
    content_model character varying,
    images_nr integer,
  	footer boolean,
  	tei boolean
);
CREATE TABLE IF NOT EXISTS dlc_batch_ingest_log_item_volume(
  id serial NOT NULL,
  name character varying NOT NULL,
  errorlevel character varying,
  startdate timestamp without time zone,
  enddate timestamp without time zone,
  log_item_id integer NOT NULL,
  message character varying,
  item_id character varying,
  content_model character varying,
  images_nr integer,
  footer boolean,
  tei boolean
)