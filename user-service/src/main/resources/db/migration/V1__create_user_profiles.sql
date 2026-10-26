-- users_db: perfiles de usuario (solo user-service accede a esta base)
-- id = "sub" del JWT emitido por auth-service (no hay FK entre bases: Database per Service)
CREATE TABLE user_profiles (
    id         UUID         PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(120) NOT NULL,
    first_name VARCHAR(80)  NOT NULL,
    last_name  VARCHAR(80)  NOT NULL,
    phone      VARCHAR(20),
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL
);
