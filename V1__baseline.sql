CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS patients (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    mrn VARCHAR(64) NOT NULL UNIQUE,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20) NOT NULL,
    facility_code VARCHAR(40),
    consent_status VARCHAR(20) NOT NULL,
    primary_phone VARCHAR(32),
    primary_email VARCHAR(120),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE IF NOT EXISTS encounters (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients (id),
    external_encounter_id VARCHAR(64),
    encounter_type VARCHAR(40),
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ,
    facility_code VARCHAR(40),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE IF NOT EXISTS observations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients (id),
    encounter_id UUID REFERENCES encounters (id),
    code VARCHAR(60) NOT NULL,
    system VARCHAR(120),
    display VARCHAR(120),
    value NUMERIC,
    unit VARCHAR(30),
    status VARCHAR(30),
    effective_time TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE IF NOT EXISTS raw_messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    source_system VARCHAR(60) NOT NULL,
    status VARCHAR(20) NOT NULL,
    received_at TIMESTAMPTZ NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_encounter_patient ON encounters (patient_id);
CREATE INDEX IF NOT EXISTS idx_encounter_external ON encounters (external_encounter_id);
CREATE INDEX IF NOT EXISTS idx_observation_patient ON observations (patient_id);
CREATE INDEX IF NOT EXISTS idx_observation_encounter ON observations (encounter_id);
CREATE INDEX IF NOT EXISTS idx_observation_code ON observations (code, system);
CREATE INDEX IF NOT EXISTS idx_patients_facility ON patients (facility_code);
CREATE INDEX IF NOT EXISTS idx_raw_message_source ON raw_messages (source_system);

