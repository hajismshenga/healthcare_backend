# Comprehensive API testing script

# Function to make API requests
function Test-Api {
    param (
        [string]$Endpoint,
        [string]$Method = "GET",
        [string]$Body = $null,
        [string]$Description = ""
    )
    
    Write-Host "`nTesting: $Description"
    Write-Host "Endpoint: $Endpoint"
    Write-Host "Method: $Method"
    
    try {
        $headers = @{
            'Content-Type' = 'application/json'
        }
        
        if ($Body) {
            $response = Invoke-WebRequest -Uri "http://localhost:8080/$Endpoint" -Method $Method -Headers $headers -Body $Body
        } else {
            $response = Invoke-WebRequest -Uri "http://localhost:8080/$Endpoint" -Method $Method -Headers $headers
        }
        
        Write-Host "Status: $($response.StatusCode)"
        Write-Host "Response: $($response.Content)"
        Write-Host "-" * 50
    }
    catch {
        Write-Host "Error: $_.Exception.Message"
        Write-Host "-" * 50
    }
}

# Function to create test data
function Create-TestData {
    # Create test hospital
    $hospitalBody = @{
        hospitalId = "HOSP/001"
        name = "Test Hospital"
        ownership = "GOVERNMENT"
        district = "Test District"
        location = "Test Location"
        contactInfo = "1234567890"
        username = "test_hospital"
        password = "test123"
    } | ConvertTo-Json
    Test-Api -Endpoint "api/hospitals/register" -Method "POST" -Body $hospitalBody -Description "Creating test hospital"
    
    # Create test doctor
    $doctorBody = @{
        name = "Dr. Test Doctor"
        profession = "Test Profession"
        email = "test@doctor.com"
        phoneNumber = "1234567890"
        hospitalId = "HOSP/001"
    } | ConvertTo-Json
    Test-Api -Endpoint "api/doctors/register" -Method "POST" -Body $doctorBody -Description "Creating test doctor"
    
    # Create test patient
    $patientBody = @{
        patientId = "PAT001"
        name = "Test Patient"
        age = 30
        gender = "Male"
        phoneNumber = "1234567890"
        address = "Test Address"
        hospitalId = "HOSP/001"
    } | ConvertTo-Json
    Test-Api -Endpoint "api/patients/register" -Method "POST" -Body $patientBody -Description "Creating test patient"
    
    # Create test laboratory
    $laboratoryBody = @{
        name = "Test Laboratory"
        specialization = "General"
        hospitalId = "HOSP/001"
    } | ConvertTo-Json
    Test-Api -Endpoint "api/laboratories/register" -Method "POST" -Body $laboratoryBody -Description "Creating test laboratory"
    
    # Create test clinical record
    $clinicalRecordBody = @{
        patientId = "PAT001"
        doctorId = 1
        diagnosis = "Test Diagnosis"
        treatment = "Test Treatment"
        dateRecorded = "2025-05-18"
    } | ConvertTo-Json
    Test-Api -Endpoint "api/clinical-records/create" -Method "POST" -Body $clinicalRecordBody -Description "Creating test clinical record"
    
    # Request lab test
    $labTestBody = @{
        patientId = "PAT001"
        doctorId = 1
        laboratoryId = 1
        testType = "Blood Test"
        testDescription = "Complete Blood Count"
        clinicalRecordId = 1
    } | ConvertTo-Json
    Test-Api -Endpoint "api/lab-tests/request" -Method "POST" -Body $labTestBody -Description "Requesting lab test"
    
    # Get all lab tests
    Test-Api -Endpoint "api/lab-tests" -Method "GET" -Description "Getting all lab tests"
    
    # Create lab result
    $labResultBody = @{
        patientId = "PAT001"
        labTestId = 1
        result = "Test Result"
        interpretation = "Test Interpretation"
        dateRecorded = "2025-05-18"
    } | ConvertTo-Json
    Test-Api -Endpoint "api/lab-results/create" -Method "POST" -Body $labResultBody -Description "Creating lab result"
}

# Function to test all endpoints
function Test-All-Endpoints {
    # Test GET endpoints
    Test-Api -Endpoint "api/patients" -Description "Get all patients"
    Test-Api -Endpoint "api/doctors" -Description "Get all doctors"
    Test-Api -Endpoint "api/hospitals" -Description "Get all hospitals"
    Test-Api -Endpoint "api/laboratories" -Description "Get all laboratories"
    Test-Api -Endpoint "api/clinical-records" -Description "Get all clinical records"
    Test-Api -Endpoint "api/second-opinions" -Description "Get all second opinions"
    Test-Api -Endpoint "api/lab-tests" -Description "Get all lab tests"
    Test-Api -Endpoint "api/lab-results" -Description "Get all lab results"
    
    # Test specific ID endpoints
    Test-Api -Endpoint "api/patients/1" -Description "Get patient by ID"
    Test-Api -Endpoint "api/doctors/1" -Description "Get doctor by ID"
    Test-Api -Endpoint "api/hospitals/1" -Description "Get hospital by ID"
    Test-Api -Endpoint "api/clinical-records/1" -Description "Get clinical record by ID"
    Test-Api -Endpoint "api/lab-tests/1" -Description "Get lab test by ID"
    Test-Api -Endpoint "api/lab-results/1" -Description "Get lab result by ID"
    
    # Test patient-specific endpoints
    Test-Api -Endpoint "api/patients/patientId/PAT001" -Description "Get patient by patient ID"
    Test-Api -Endpoint "api/patients/clinical-records/1" -Description "Get patient's clinical records"
    Test-Api -Endpoint "api/patients/prescriptions/1" -Description "Get patient's prescriptions"
}

# Main execution
Write-Host "Starting API Tests..."
Write-Host "-" * 50

# Create test data
Create-TestData

# Test all endpoints
Test-All-Endpoints

Write-Host "API Testing Complete"
Write-Host "-" * 50
