INSERT INTO accounts (id, username, password, role, tenant_id)
VALUES (
           'f32a91b3-557f-4544-af50-4fb042f279c7',
           'dhenSouza',
           '$2a$10$IKEWFJ3PchJ1MaV7tbQXE.oO6zs.vwVPuSmQwGsomnES.0UES4ssC',
           'USER',
           'T1'
       ) ON CONFLICT (username) DO NOTHING;