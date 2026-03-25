-- liquibase formatted sql

-- changeset helphub:009-seed-seller-role
INSERT INTO roles (name) VALUES ('SELLER') ON CONFLICT (name) DO NOTHING;

-- changeset helphub:009-assign-profile-perms-to-seller
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'SELLER' AND p.slug IN ('profile_read', 'profile_update', 'profile_delete')
ON CONFLICT DO NOTHING;
