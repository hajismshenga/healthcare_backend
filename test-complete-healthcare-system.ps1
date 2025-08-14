# Complete Healthcare System API Test Script
# Tests Hospital, Doctor, Laboratory, and Patient Registration/Login

Write-Host "Complete Healthcare System API Testing" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green

# Function to make API requests and display results
function Test-Api {
    param (
        [string]$Endpoint,
        [string]$Method = "GET",
        [string]$Body = $null,
        [string]$Description = ""
    )
    
    Write-Host "`nTesting: $Description" -ForegroundColor Yellow
    Write-Host "Endpoint: $Endpoint"
    Write-Host "Method: $Method"
    
    try {
        $headers = @{
            'Content-Type' = 'application/json'
        }
        
        if ($Body) {
            $response = Invoke-WebRequest -Uri "http://localhost:8080/$Endpoint" -Method $Method -Headers $headers -Body $Body -TimeoutSec 30
        } else {
            $response = Invoke-WebRequest -Uri "http://localhost:8080/$Endpoint" -Method $Method -Headers $headers -TimeoutSec 30
        }
        
        Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
        Write-Host "Response: $($response.Content)"
        Write-Host "-" * 60
        return $response.Content
    }
    catch {
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response) {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $responseBody = $reader.ReadToEnd()
            Write-Host "Response Body: $responseBody" -ForegroundColor Red
        }
        Write-Host "-" * 60
        return $null
    }
}

# Wait for application to start
Write-Host "Waiting for application to start..." -ForegroundColor Cyan
Start-Sleep -Seconds 10

# Test 1: Check if application is running
Write-Host "`n1. Testing Application Status" -ForegroundColor Magenta
Test-Api -Endpoint "api/hospitals/test" -Description "Hospital Controller Test"
Test-Api -Endpoint "api/doctors/test" -Description "Doctor Controller Test"
Test-Api -Endpoint "api/laboratories/test" -Description "Laboratory Controller Test"
Test-Api -Endpoint "api/patients/test" -Description "Patient Controller Test"

# Test 2: Hospital Registration
Write-Host "`n2. Testing Hospital Registration" -ForegroundColor Magenta
$hospitalBody = @{
    name = "City General Hospital"
    ownership = "GOVERNMENT"
    district = "Central District"
    location = "123 Main Street, City Center"
    contactInfo = "+255-123-456-789"
    username = "city_hospital"
    password = "Hospital123"
} | ConvertTo-Json -Depth 3

$hospitalResponse = Test-Api -Endpoint "api/hospitals/register" -Method "POST" -Body $hospitalBody -Description "Register Hospital"

# Test 3: Hospital Login
Write-Host "`n3. Testing Hospital Login" -ForegroundColor Magenta
$hospitalLoginBody = @{
    username = "city_hospital"
    password = "Hospital123"
} | ConvertTo-Json

$loginResponse = Test-Api -Endpoint "api/hospitals/login" -Method "POST" -Body $hospitalLoginBody -Description "Hospital Login"

# Test 4: Get All Hospitals
Write-Host "`n4. Testing Get All Hospitals" -ForegroundColor Magenta
Test-Api -Endpoint "api/hospitals" -Description "Get All Hospitals"

# Test 5: Doctor Registration
Write-Host "`n5. Testing Doctor Registration" -ForegroundColor Magenta
$doctorBody = @{
    name = "Dr. John Smith"
    profession = "Cardiologist"
    doctorId = "DID-001"
    hospitalId = "HOSP/001"  # This should match the generated hospital ID format
} | ConvertTo-Json

$doctorResponse = Test-Api -Endpoint "api/doctors/register" -Method "POST" -Body $doctorBody -Description "Register Doctor"

# Test 6: Doctor Login
Write-Host "`n6. Testing Doctor Login" -ForegroundColor Magenta
$doctorLoginBody = @{
    doctorId = "DID-001"
    password = "123456"  # Default password
} | ConvertTo-Json

Test-Api -Endpoint "api/doctors/login" -Method "POST" -Body $doctorLoginBody -Description "Doctor Login"

# Test 7: Get Doctors by Hospital
Write-Host "`n7. Testing Get Doctors by Hospital" -ForegroundColor Magenta
Test-Api -Endpoint "api/doctors/hospital/HOSP/001" -Description "Get Doctors by Hospital"

# Test 8: Laboratory Registration
Write-Host "`n8. Testing Laboratory Registration" -ForegroundColor Magenta
$laboratoryBody = @{
    name = "City General Laboratory"
    laboratoryId = "LAB-001"
    hospitalId = "HOSP/001"  # This should match the generated hospital ID format
} | ConvertTo-Json

$laboratoryResponse = Test-Api -Endpoint "api/laboratories/register" -Method "POST" -Body $laboratoryBody -Description "Register Laboratory"

# Test 9: Laboratory Login
Write-Host "`n9. Testing Laboratory Login" -ForegroundColor Magenta
$laboratoryLoginBody = @{
    laboratoryId = "LAB-001"
    password = "123456"  # Default password
} | ConvertTo-Json

Test-Api -Endpoint "api/laboratories/login" -Method "POST" -Body $laboratoryLoginBody -Description "Laboratory Login"

# Test 10: Get Laboratories by Hospital
Write-Host "`n10. Testing Get Laboratories by Hospital" -ForegroundColor Magenta
Test-Api -Endpoint "api/laboratories/hospital/HOSP/001" -Description "Get Laboratories by Hospital"

# Test 11: Get All Laboratories
Write-Host "`n11. Testing Get All Laboratories" -ForegroundColor Magenta
Test-Api -Endpoint "api/laboratories" -Description "Get All Laboratories"

# Test 12: Patient Registration
Write-Host "`n12. Testing Patient Registration" -ForegroundColor Magenta
$patientBody = @{
    name = "John Doe"
    patientId = "PID-001"
    doctorId = "DID-001"  # This should match the registered doctor ID
} | ConvertTo-Json

$patientResponse = Test-Api -Endpoint "api/patients/register" -Method "POST" -Body $patientBody -Description "Register Patient"

# Test 13: Patient Login
Write-Host "`n13. Testing Patient Login" -ForegroundColor Magenta
$patientLoginBody = @{
    patientId = "PID-001"
    password = "123456"  # Default password
} | ConvertTo-Json

Test-Api -Endpoint "api/patients/login" -Method "POST" -Body $patientLoginBody -Description "Patient Login"

# Test 14: Get Patients by Doctor
Write-Host "`n14. Testing Get Patients by Doctor" -ForegroundColor Magenta
Test-Api -Endpoint "api/patients/doctor/DID-001" -Description "Get Patients by Doctor"

# Test 15: Get Patients by Hospital
Write-Host "`n15. Testing Get Patients by Hospital" -ForegroundColor Magenta
Test-Api -Endpoint "api/patients/hospital/HOSP/001" -Description "Get Patients by Hospital"

# Test 16: Get All Patients
Write-Host "`n16. Testing Get All Patients" -ForegroundColor Magenta
Test-Api -Endpoint "api/patients" -Description "Get All Patients"

Write-Host "`nComplete API Testing Finished!" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green
