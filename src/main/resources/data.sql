INSERT INTO hospitals (id, name, location, email, subscription_plan)
VALUES (1, 'BDMS General Hospital', 'Hyderabad', 'hospital@bdms.com', 'FREE')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO donors (id, name, email, password, blood_group, location, phone_number, availability_status, role, total_donations, hospital_id)
VALUES
(1, 'System Admin', 'admin@bdms.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOePaWxn96p36f5f7T6nQWGWpZJYG1Y5a', 'O+', 'Hyderabad', '9999999999', true, 'ROLE_ADMIN', 0, 1),
(2, 'Main Hospital User', 'hospital.user@bdms.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOePaWxn96p36f5f7T6nQWGWpZJYG1Y5a', 'A+', 'Hyderabad', '9888888888', true, 'ROLE_HOSPITAL', 0, 1),
(3, 'Demo Donor', 'donor@bdms.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOePaWxn96p36f5f7T6nQWGWpZJYG1Y5a', 'B+', 'Hyderabad', '9777777777', true, 'ROLE_DONOR', 3, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);
