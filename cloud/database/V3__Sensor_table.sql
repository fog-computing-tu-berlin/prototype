CREATE TABLE public.sensor
(
    id bigserial PRIMARY KEY NOT NULL,
    edge_id bigint REFERENCES edge_id(id) NOT NULL,
    temperature NUMERIC(5, 3) NOT NULL,
    temperature_sensor_id varchar(128) NOT NULL,
    humidity NUMERIC(5, 3) NOT NULL,
    humidity_sensor_id varchar(128) NOT NULL,
    uv NUMERIC(5, 3) NOT NULL,
    uv_sensor_id varchar(128) NOT NULL,
    started_at timestamp NOT NULL,
    stopped_at timestamp NOT NULL
);

ALTER TABLE public.sensor
    OWNER to postgres;

GRANT SELECT, INSERT(edge_id, temperature, temperature_sensor_id, humidity, humidity_sensor_id, uv, uv_sensor_id, started_at, stopped_at)
    ON sensor TO anon;

GRANT USAGE ON SEQUENCE sensor_id_seq TO anon;
