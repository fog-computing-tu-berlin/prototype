CREATE TABLE public.edge_id
(
    id BIGSERIAL PRIMARY KEY NOT NULL,
    plant TEXT NOT NULL
);

ALTER TABLE public.edge_id
    OWNER to postgres;

GRANT SELECT, INSERT(plant)
    ON edge_id TO anon;

GRANT USAGE ON SEQUENCE edge_id_id_seq TO anon;
