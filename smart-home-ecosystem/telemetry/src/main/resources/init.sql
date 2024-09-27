CREATE TABLE IF NOT EXISTS sensors (
    id BIGSERIAL PRIMARY KEY,
    sensor_type_id BIGINT NOT NULL,
    device_id BIGINT,
    serial_number BIGINT,
    unit_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS temperatures (
    id BIGSERIAL PRIMARY KEY,
    sensor_id BIGINT NOT NULL,
    value DOUBLE PRECISION NOT NULL,
    timestamp TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS sensor_types (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS units (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR NOT NULL
);

INSERT INTO sensor_types (id, code) VALUES (1, 'temperature'), (2, 'light'), (3, 'movement') ON CONFLICT DO NOTHING;
INSERT INTO units (id, code) VALUES (1, 'celsius'), (2, 'fahrenheit') ON CONFLICT DO NOTHING