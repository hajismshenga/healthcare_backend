# Lab Test Fix Script - Tests the lab test functionality properly

Write-Host "Lab Test Fix Testing" -ForegroundColor Green
Write-Host "====================" -ForegroundColor Green

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

# Test 1: Check if lab test controller is working
Write-Host "`n1. Testing Lab Test Controller Status" -ForegroundColor Magenta
Test-Api -Endpoint "api/lab-tests/test" -Description "Lab Test Controller Test"

# Test 2: Request a lab test with proper doctorId parameter
Write-Host "`n2. Testing Lab Test Request with doctorId" -ForegroundColor Magenta
$labTestBody = @{
    laboratoryId = "LAB-001"
    patientId = "PID-001"
    testRequirement = "Complete blood count and liver function tests"
    notes = "Patient fasting for 12 hours"
} | ConvertTo-Json

$labTestResponse = Test-Api -Endpoint "api/lab-tests/request?doctorId=DID-001" -Method "POST" -Body $labTestBody -Description "Request Lab Test with doctorId"

# Test 3: Try without doctorId (should fail)
Write-Host "`n3. Testing Lab Test Request without doctorId (should fail)" -ForegroundColor Magenta
Test-Api -Endpoint "api/lab-tests/request" -Method "POST" -Body $labTestBody -Description "Request Lab Test without doctorId"

# Test 4: Get all lab tests
Write-Host "`n4. Testing Get All Lab Tests" -ForegroundColor Magenta
Test-Api -Endpoint "api/lab-tests" -Description "Get All Lab Tests"

Write-Host "`nLab Test Fix Testing Complete!" -ForegroundColor Green
Write-Host "====================" -ForegroundColor Green
