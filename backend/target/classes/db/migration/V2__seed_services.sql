INSERT INTO services (name, description, category, price, duration_minutes, active) VALUES
('General Consultation', 'One-on-one consultation with a service specialist.', 'Healthcare', 49.99, 30, TRUE),
('Computer Repair', 'Diagnosis and repair for laptops and desktops.', 'Technology', 79.99, 60, TRUE),
('Document Verification', 'Official document review and verification.', 'Government', 29.99, 20, TRUE),
('Technical Support', 'Remote and in-person technical assistance.', 'Technology', 59.99, 45, TRUE),
('Salon Appointment', 'Haircut, styling, and grooming services.', 'Beauty', 39.99, 40, TRUE);

INSERT INTO service_availability (service_id, day_of_week, opening_time, closing_time, slot_interval_minutes, active)
SELECT s.id, d.day_of_week, '09:00:00', '17:00:00', 30, TRUE
FROM services s
CROSS JOIN (
    SELECT 'MONDAY' AS day_of_week UNION ALL
    SELECT 'TUESDAY' UNION ALL
    SELECT 'WEDNESDAY' UNION ALL
    SELECT 'THURSDAY' UNION ALL
    SELECT 'FRIDAY'
) d;
