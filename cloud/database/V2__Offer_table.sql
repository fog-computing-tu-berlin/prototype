CREATE TABLE public.sensor
(
    id SERIAL NOT NULL,
    humidity REAL NOT NULL,
    light REAL NOT NULL,
    temperature REAL NOT NULL,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
)
TABLESPACE pg_default;

ALTER TABLE public.sensor
    OWNER to postgres;

GRANT SELECT, INSERT(humidity, light, temperature)
    ON sensor TO anon;

GRANT USAGE ON SEQUENCE sensor_id_seq TO anon;
