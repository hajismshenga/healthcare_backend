# Healthcare API Test Plan

This document outlines the API endpoints available in the Healthcare system and how to test them. The application runs on port 8090.

## Prerequisites
- Java 17+
- Maven
- Postman (for testing)

## Starting the Application
```
cd c:\PROJECTS\healthcare
mvn spring-boot:run
```

## API Endpoints

### Hospital Management

#### 1. Register Hospital
- **Endpoint**: POST /api/hospitals/register
- **Request Body**:
```json
{
    "name": "General Hospital",
    "address": "123 Main St",
    "phoneNumber": "555-1234",
    "email": "info@generalhospital.com"
}
```
- **Expected Response**: 200 OK with hospital details and ID

#### 2. Get All Hospitals
- **Endpoint**: GET /api/hospitals
- **Expected Response**: 200 OK with array of hospitals

### Doctor Management

#### 1. Register Doctor
- **Endpoint**: POST /api/doctors/register
- **Request Body**:
```json
{
    "name": "Dr. John Smith",
    "profession": "Cardiologist",
    "email": "john.smith@hospital.com",
    "phoneNumber": "555-5678",
    "hospitalId": 1
}
```
- **Expected Response**: 200 OK with doctor details, ID, and default password

#### 2. Get All Doctors
- **Endpoint**: GET /api/doctors
- **Expected Response**: 200 OK with array of doctors

#### 3. Get Doctor By ID
- **Endpoint**: GET /api/doctors/{id}
- **Expected Response**: 200 OK with doctor details

#### 4. Bulk Import Doctors
- **Endpoint**: POST /api/excel-import/doctors
- **Request**: Form data with file (Excel) and hospitalId
- **Expected Response**: 200 OK with count of imported doctors

### Patient Management

#### 1. Register Patient
- **Endpoint**: POST /api/doctors/patients/register
- **Request Body**:
```json
{
    "name": "Jane Doe",
    "age": 35,
    "gender": "Female",
    "phoneNumber": "555-9012",
    "address": "456 Oak St",
    "doctorId": 1
}
```
- **Expected Response**: 200 OK with patient details, ID, and default password

#### 2. Get Patient By ID
- **Endpoint**: GET /api/patients/{id}
- **Expected Response**: 200 OK with patient details

#### 3. Get Patient Dashboard
- **Endpoint**: GET /api/patients/dashboard/{patientId}
- **Expected Response**: 200 OK with patient dashboard data including:
  - Patient details
  - Recent clinical records
  - Active prescriptions
  - Pending lab tests
  - Recent lab results
  - Pending second opinion requests

#### 4. Get Patient Prescriptions
- **Endpoint**: GET /api/patients/prescriptions/{patientId}
- **Expected Response**: 200 OK with array of prescriptions

#### 5. Get Patient Active Prescriptions
- **Endpoint**: GET /api/patients/prescriptions/{patientId}/active
- **Expected Response**: 200 OK with array of active prescriptions

#### 6. Get Patient Lab Tests
- **Endpoint**: GET /api/patients/lab-tests/{patientId}
- **Expected Response**: 200 OK with array of lab tests

#### 7. Get Patient Lab Results
- **Endpoint**: GET /api/patients/lab-results/{patientId}
- **Expected Response**: 200 OK with array of lab results

### Clinical Record Management

#### 1. Create Clinical Record
- **Endpoint**: POST /api/clinical-records
- **Request Body**:
```json
{
    "patientId": 1,
    "doctorId": 1,
    "symptoms": "Chest pain, shortness of breath",
    "diagnosis": "Angina",
    "treatment": "Nitroglycerin as needed",
    "notes": "Patient should follow up in 2 weeks"
}
```
- **Expected Response**: 200 OK with clinical record details

#### 2. Get Patient Clinical Records
- **Endpoint**: GET /api/patients/clinical-records/{patientId}
- **Expected Response**: 200 OK with array of clinical records

### Lab Test Management

#### 1. Request Lab Test
- **Endpoint**: POST /api/lab-tests/request
- **Request Body**:
```json
{
    "patientId": 1,
    "doctorId": 1,
    "laboratoryId": 1,
    "testType": "Blood Test",
    "testDescription": "Complete Blood Count",
    "clinicalRecordId": 1
}
```
- **Expected Response**: 200 OK with lab test ID

#### 2. Get Tests By Patient
- **Endpoint**: GET /api/lab-tests/patient/{patientId}
- **Expected Response**: 200 OK with array of lab tests

#### 3. Add Test Result
- **Endpoint**: POST /api/lab-tests/results
- **Request Body**:
```json
{
    "labTestId": 1,
    "resultDetails": "WBC: 7.5, RBC: 4.8, Hemoglobin: 14.2",
    "interpretation": "Normal blood count",
    "normalRange": "WBC: 4.5-11.0, RBC: 4.5-5.5, Hemoglobin: 13.5-17.5",
    "recommendations": "No further action needed"
}
```
- **Expected Response**: 200 OK with result details

### Second Opinion Management

#### 1. Request Second Opinion
- **Endpoint**: POST /api/patients/request-second-opinion
- **Request Body**:
```json
{
    "patientId": 1,
    "requestingDoctorId": 1,
    "consultingDoctorId": 2,
    "conditionDescription": "Persistent chest pain despite medication",
    "currentTreatment": "Nitroglycerin 0.4mg SL PRN",
    "isUrgent": true,
    "clinicalRecordId": 1
}
```
- **Expected Response**: 200 OK with request ID

#### 2. Get Patient Second Opinion Requests
- **Endpoint**: GET /api/patients/second-opinion-requests/{patientId}
- **Expected Response**: 200 OK with array of second opinion requests

#### 3. Provide Second Opinion
- **Endpoint**: POST /api/second-opinion-requests/{id}/provide-opinion
- **Request Body**:
```json
{
    "secondOpinion": "I recommend cardiac catheterization to rule out coronary artery disease. Current medication regimen is appropriate but may need adjustment based on catheterization results."
}
```
- **Expected Response**: 200 OK with updated request details

## Testing Flow

1. Register a hospital
2. Register doctors (or import via Excel)
3. Register patients
4. Create clinical records
5. Request lab tests
6. Add lab test results
7. Request second opinions
8. Provide second opinions
9. View patient dashboard

## Authentication

All endpoints require authentication. The default password for new users (doctors, patients, laboratories) is "123456".

## Postman Collection

A Postman collection has been provided in the file `postman_collection.json` to facilitate testing.
