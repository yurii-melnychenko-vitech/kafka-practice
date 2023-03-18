CREATE TABLE IF NOT EXISTS event (
                                           id serial PRIMARY KEY,
                                           resource_type VARCHAR,
                                           resource text
);
