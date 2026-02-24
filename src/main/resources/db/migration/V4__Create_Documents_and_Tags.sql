CREATE TABLE document_tags (
                               document_id UUID NOT NULL,
                               tags VARCHAR(255),
                               CONSTRAINT fk_document_tags_document FOREIGN KEY (document_id) REFERENCES documents (id)
);