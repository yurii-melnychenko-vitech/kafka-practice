CREATE TABLE IF NOT EXISTS audit_event (
                                           id serial PRIMARY KEY,
                                           resource_type VARCHAR,
                                           event text,
                                           participant TEXT [],
                                           source text,
                                           object TEXT []
);
