INSERT INTO accounts (id, username, password, role, tenant_id)
VALUES (
           'e45e82a2-446f-4433-9f49-3ea931e168b6',
           'admin',
           '$2a$10$maLDGN70EkMLBjpntMME5OUtWEBQ00QLuK2XCnY8K4zMdCDVzmQLy',
           'ADMIN',
           'T1'
       ) ON CONFLICT (username) DO NOTHING;