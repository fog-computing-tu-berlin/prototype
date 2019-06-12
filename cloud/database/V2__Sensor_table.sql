CREATE TABLE public.sensor
(
    -- TODO correct data values
    id SERIAL NOT NULL,
    sensor_id varchar(128) NOT NULL,
    temperature REAL NOT NULL,
    humidity REAL NOT NULL,
    uv REAL NOT NULL,
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
