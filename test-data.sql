-- Create test data for Healthcare System

-- Insert test hospital
INSERT INTO hospitals (name, address, phone_number, email) VALUES 
('Zanzibar General Hospital', 'Stone Town, Zanzibar', '+255 24 223 1111', 'zgh@healthcare.gov.zw');

-- Insert test doctor
INSERT INTO doctors (name, profession, email, phone_number, hospital_id) VALUES 
('Dr. John Smith', 'Cardiologist', 'john.smith@example.com', '+255 777 123456', 1);

-- Insert test patient
INSERT INTO patients (patient_id, name, age, gender, phone_number, address) VALUES 
('PAT001', 'John Doe', 35, 'Male', '+255 777 987654', 'Stone Town, Zanzibar');

-- Insert test clinical record
INSERT INTO clinical_records (patient_id, doctor_id, diagnosis, treatment, date_recorded) VALUES 
(1, 1, 'Hypertension', 'Prescribed medication', '2025-05-18');

-- Insert test laboratory
INSERT INTO laboratories (name, address, phone_number, email, hospital_id) VALUES 
('Zanzibar General Hospital Lab', 'Stone Town, Zanzibar', '+255 24 223 1112', 'lab@zgh.gov.zw', 1);

-- Insert test lab test
INSERT INTO lab_tests (name, description, laboratory_id) VALUES 
('Blood Pressure Test', 'Measures blood pressure levels', 1);

-- Insert test lab result
INSERT INTO lab_results (patient_id, lab_test_id, result, interpretation, date_recorded) VALUES 
(1, 1, '140/90 mmHg', 'Elevated blood pressure', '2025-05-18');
