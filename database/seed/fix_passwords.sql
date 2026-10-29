-- Fix password hashes for demo accounts
-- Generate a proper BCrypt hash using Spring Boot's BCryptPasswordEncoder at startup
-- For now, use a known-good hash for 'password123'
-- This hash was generated with: new BCryptPasswordEncoder().encode("password123")
UPDATE users SET password_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' WHERE id IN (1,2,3,4,5);
