CREATE TABLE accounts (
                          id UUID PRIMARY KEY,
                          username VARCHAR(50) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL,
                          role VARCHAR(20) NOT NULL,
                          tenant_id VARCHAR(50) NOT NULL
);

CREATE TABLE documents (
                           id UUID PRIMARY KEY,
                           title VARCHAR(255) NOT NULL,
                           description TEXT,
                           tags TEXT[],
                           status VARCHAR(20) NOT NULL,
                           owner_id UUID REFERENCES accounts(id),
                           tenant_id VARCHAR(50) NOT NULL,
                           created_at TIMESTAMP NOT NULL,
                           updated_at TIMESTAMP NOT NULL
);