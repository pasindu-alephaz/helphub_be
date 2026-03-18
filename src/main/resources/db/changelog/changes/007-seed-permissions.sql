-- liquibase formatted sql

-- changeset helphub:007-seed-profile-permissions
INSERT INTO permissions (slug) VALUES ('profile_read') ON CONFLICT (slug) DO NOTHING;
INSERT INTO permissions (slug) VALUES ('profile_update') ON CONFLICT (slug) DO NOTHING;
INSERT INTO permissions (slug) VALUES ('profile_delete') ON CONFLICT (slug) DO NOTHING;

-- changeset helphub:007-create-user-role
INSERT INTO roles (name) VALUES ('USER') ON CONFLICT (name) DO NOTHING;

-- changeset helphub:007-assign-profile-perms-to-user
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'USER' AND p.slug IN ('profile_read', 'profile_update', 'profile_delete')
ON CONFLICT DO NOTHING;

-- changeset helphub:007-assign-profile-perms-to-admin
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ADMIN' AND p.slug IN ('profile_read', 'profile_update', 'profile_delete')
ON CONFLICT DO NOTHING;

-- changeset helphub:007-assign-user-role-to-all-users
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE r.name = 'USER'
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id)
ON CONFLICT DO NOTHING;
