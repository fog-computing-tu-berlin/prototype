CREATE TABLE public.sensor
(
    id SERIAL NOT NULL,
    temperature NUMERIC(5, 3) NOT NULL,
    temperature_sensor_id varchar(128) NOT NULL,
    humidity NUMERIC(5, 3) NOT NULL,
    humidity_sensor_id varchar(128) NOT NULL,
    uv NUMERIC(5, 3) NOT NULL,
    uv_sensor_id varchar(128) NOT NULL,
    started_at timestamp NOT NULL,
    stopped_at timestamp NOT NULL,
    PRIMARY KEY (id)
)
    TABLESPACE pg_default;

ALTER TABLE public.sensor
    OWNER to postgres;

GRANT SELECT, INSERT(sensor_id, temperature, humidity, uv, started_at, stopped_at)
    ON sensor TO anon;

GRANT USAGE ON SEQUENCE sensor_id_seq TO anon;
