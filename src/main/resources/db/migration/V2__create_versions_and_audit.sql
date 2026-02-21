CREATE TABLE document_versions (
                                   id UUID PRIMARY KEY,
                                   document_id UUID REFERENCES documents(id) ON DELETE CASCADE,
                                   version_number INT NOT NULL,
                                   file_key VARCHAR(255) NOT NULL,
                                   checksum VARCHAR(64) NOT NULL,
                                   file_size BIGINT NOT NULL,
                                   file_type VARCHAR(50) NOT NULL,
                                   uploaded_at TIMESTAMP NOT NULL,
                                   uploaded_by UUID REFERENCES accounts(id)
);

CREATE TABLE audit_logs (
                            id UUID PRIMARY KEY,
                            timestamp TIMESTAMP NOT NULL,
                            account_id UUID REFERENCES accounts(id),
                            action VARCHAR(50) NOT NULL,
                            document_id UUID,
                            metadata JSONB
);