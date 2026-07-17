-- Bootstrap account for a new PostgreSQL database.
-- Email: admin@inventory.com
-- Password: Admin@123
INSERT INTO users (name, email, password, role, is_active, is_password_reset_required)
VALUES (
    'Super Admin',
    'admin@inventory.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mr/.wqQ8ZvXbR6jUZqyJwY.vjYo2qE2',
    'SUPER_ADMIN',
    TRUE,
    FALSE
)
ON CONFLICT (email) DO NOTHING;
