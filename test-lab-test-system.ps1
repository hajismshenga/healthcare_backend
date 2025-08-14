# Lab Test System API Test Script
# Tests the complete lab test workflow from doctor request to laboratory result

Write-Host "Lab Test System API Testing" -ForegroundColor Green
Write-Host "===========================" -ForegroundColor Green

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

# Test 1: Check if lab test controller is working
Write-Host "`n1. Testing Lab Test Controller Status" -ForegroundColor Magenta
Test-Api -Endpoint "api/lab-tests/test" -Description "Lab Test Controller Test"

# Test 2: Request a lab test (Doctor sends test to laboratory)
Write-Host "`n2. Testing Lab Test Request" -ForegroundColor Magenta
$labTestBody = @{
    laboratoryId = "LAB-001"
    patientId = "PID-001"
    testRequirement = "Complete blood count, liver function tests, and kidney function tests"
    notes = "Patient should be fasting for 12 hours before the test"
} | ConvertTo-Json

$labTestResponse = Test-Api -Endpoint "api/lab-tests/request?doctorId=DID-001" -Method "POST" -Body $labTestBody -Description "Request Lab Test"

# Test 3: Get all lab tests
Write-Host "`n3. Testing Get All Lab Tests" -ForegroundColor Magenta
Test-Api -Endpoint "api/lab-tests" -Description "Get All Lab Tests"

# Test 4: Get tests by doctor
Write-Host "`n4. Testing Get Tests by Doctor" -ForegroundColor Magenta
Test-Api -Endpoint "api/lab-tests/doctor/DID-001" -Description "Get Tests by Doctor"

# Test 5: Get tests by laboratory
Write-Host "`n5. Testing Get Tests by Laboratory" -ForegroundColor Magenta
Test-Api -Endpoint "api/lab-tests/laboratory/LAB-001" -Description "Get Tests by Laboratory"

# Test 6: Get tests by patient
Write-Host "`n6. Testing Get Tests by Patient" -ForegroundColor Magenta
Test-Api -Endpoint "api/lab-tests/patient/PID-001" -Description "Get Tests by Patient"

# Test 7: Get tests by status (PENDING)
Write-Host "`n7. Testing Get Tests by Status (PENDING)" -ForegroundColor Magenta
Test-Api -Endpoint "api/lab-tests/status/PENDING" -Description "Get Tests by Status (PENDING)"

# Test 8: Update test status to IN_PROGRESS (Laboratory starts the test)
Write-Host "`n8. Testing Update Test Status to IN_PROGRESS" -ForegroundColor Magenta
Test-Api -Endpoint "api/lab-tests/TEST/20250813/001/status?status=IN_PROGRESS&notes=Sample collected and processing started" -Method "PUT" -Description "Update Test Status to IN_PROGRESS"

# Test 9: Get tests by status (IN_PROGRESS)
Write-Host "`n9. Testing Get Tests by Status (IN_PROGRESS)" -ForegroundColor Magenta
Test-Api -Endpoint "api/lab-tests/status/IN_PROGRESS" -Description "Get Tests by Status (IN_PROGRESS)"

# Test 10: Add test result (Laboratory completes the test)
Write-Host "`n10. Testing Add Test Result" -ForegroundColor Magenta
$testResultBody = @{
    testResult = "Blood Count: Normal (RBC: 4.5M/μL, WBC: 7.2K/μL, Hemoglobin: 14.2g/dL). Liver Function: Normal (ALT: 25U/L, AST: 28U/L). Kidney Function: Normal (Creatinine: 0.9mg/dL, BUN: 15mg/dL)."
} | ConvertTo-Json

Test-Api -Endpoint "api/lab-tests/TEST/20250813/001/result" -Method "PUT" -Body $testResultBody -Description "Add Test Result"

# Test 11: Get tests by status (COMPLETED)
Write-Host "`n11. Testing Get Tests by Status (COMPLETED)" -ForegroundColor Magenta
Test-Api -Endpoint "api/lab-tests/status/COMPLETED" -Description "Get Tests by Status (COMPLETED)"

# Test 12: Get specific test by ID
Write-Host "`n12. Testing Get Test by ID" -ForegroundColor Magenta
Test-Api -Endpoint "api/lab-tests/TEST/20250813/001" -Description "Get Test by ID"

Write-Host "`nLab Test System Testing Complete!" -ForegroundColor Green
Write-Host "===========================" -ForegroundColor Green
