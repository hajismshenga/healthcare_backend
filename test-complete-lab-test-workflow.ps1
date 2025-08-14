# Complete Lab Test Workflow Script
# Sets up all required data and tests the complete lab test workflow

Write-Host "Complete Lab Test Workflow Testing" -ForegroundColor Green
Write-Host "=================================" -ForegroundColor Green

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
Start-Sleep -Seconds 5

# Step 1: Register Hospital
Write-Host "`nStep 1: Registering Hospital" -ForegroundColor Magenta
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

# Step 2: Register Doctor
Write-Host "`nStep 2: Registering Doctor" -ForegroundColor Magenta
$doctorBody = @{
    name = "Dr. John Smith"
    profession = "Cardiologist"
    doctorId = "DID-001"
    hospitalId = "HOSP/001"  # This should match the generated hospital ID format
} | ConvertTo-Json

$doctorResponse = Test-Api -Endpoint "api/doctors/register" -Method "POST" -Body $doctorBody -Description "Register Doctor"

# Step 3: Register Laboratory
Write-Host "`nStep 3: Registering Laboratory" -ForegroundColor Magenta
$laboratoryBody = @{
    name = "City General Laboratory"
    laboratoryId = "LAB-001"
    hospitalId = "HOSP/001"  # This should match the generated hospital ID format
} | ConvertTo-Json

$laboratoryResponse = Test-Api -Endpoint "api/laboratories/register" -Method "POST" -Body $laboratoryBody -Description "Register Laboratory"

# Step 4: Register Patient
Write-Host "`nStep 4: Registering Patient" -ForegroundColor Magenta
$patientBody = @{
    name = "John Doe"
    patientId = "PID-001"
    doctorId = "DID-001"  # This should match the registered doctor ID
} | ConvertTo-Json

$patientResponse = Test-Api -Endpoint "api/patients/register" -Method "POST" -Body $patientBody -Description "Register Patient"

# Step 5: Request Lab Test
Write-Host "`nStep 5: Requesting Lab Test" -ForegroundColor Magenta
$labTestBody = @{
    laboratoryId = "LAB-001"
    patientId = "PID-001"
    testRequirement = "Complete blood count and liver function tests"
    notes = "Patient fasting for 12 hours"
} | ConvertTo-Json

$labTestResponse = Test-Api -Endpoint "api/lab-tests/request?doctorId=DID-001" -Method "POST" -Body $labTestBody -Description "Request Lab Test"

# Step 6: Get all lab tests
Write-Host "`nStep 6: Getting All Lab Tests" -ForegroundColor Magenta
Test-Api -Endpoint "api/lab-tests" -Description "Get All Lab Tests"

Write-Host "`nComplete Lab Test Workflow Testing Complete!" -ForegroundColor Green
Write-Host "=================================" -ForegroundColor Green
