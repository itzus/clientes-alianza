CREATE TABLE IF NOT EXISTS clientes (
    id SERIAL PRIMARY KEY,
    shared_key VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    fecha_creacion DATE NOT NULL
);

CREATE INDEX idx_clientes_shared_key ON clientes(shared_key);
CREATE INDEX idx_clientes_email ON clientes(email);