CREATE TABLE IF NOT EXISTS houses (
    id BIGSERIAL PRIMARY KEY,
    house_type_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    address VARCHAR
);

CREATE TABLE IF NOT EXISTS devices (
    id BIGSERIAL PRIMARY KEY,
    serial_number BIGINT,
    house_id BIGINT,
    device_type_id BIGINT NOT NULL,
    device_status_id BIGINT NOT NULL,
    monolith_id BIGINT UNIQUE
);

CREATE TABLE IF NOT EXISTS temperature_modules (
    device_id BIGINT PRIMARY KEY,
    target DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS device_statuses (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS device_types (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS house_types (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR NOT NULL
);

INSERT INTO device_statuses (id, code) VALUES (1, 'new'), (2, 'on'), (3, 'off'), (4, 'unplugged') ON CONFLICT DO NOTHING;
INSERT INTO house_types (id, code) VALUES (1, 'flat'), (2, 'townhouse'), (3, 'appartments') ON CONFLICT DO NOTHING;
INSERT INTO device_types (id, code) VALUES (1, 'heating'), (2, 'lighting'), (3, 'camera'), (4, 'gate_control') ON CONFLICT DO NOTHING