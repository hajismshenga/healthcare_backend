# Test script to fix laboratory registration issue
# First register a hospital, then test laboratory registration

Write-Host "Testing Laboratory Registration Fix" -ForegroundColor Green
Write-Host "===================================" -ForegroundColor Green

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

# Step 1: Register a hospital first
Write-Host "`nStep 1: Registering Hospital" -ForegroundColor Magenta
$hospitalBody = @{
    name = "Test Hospital for Lab"
    ownership = "GOVERNMENT"
    district = "Test District"
    location = "Test Location"
    contactInfo = "+255-123-456-789"
    username = "test_hospital_lab"
    password = "Hospital123"
} | ConvertTo-Json -Depth 3

$hospitalResponse = Test-Api -Endpoint "api/hospitals/register" -Method "POST" -Body $hospitalBody -Description "Register Hospital"

# Step 2: Get all hospitals to see the generated hospital ID
Write-Host "`nStep 2: Getting All Hospitals" -ForegroundColor Magenta
$hospitalsResponse = Test-Api -Endpoint "api/hospitals" -Description "Get All Hospitals"

# Step 3: Try laboratory registration with the correct hospital ID
Write-Host "`nStep 3: Testing Laboratory Registration" -ForegroundColor Magenta
$laboratoryBody = @{
    name = "Main Lab"
    laboratoryId = "LAB-001"
    hospitalId = "HOSP/001"  # This should match the generated hospital ID format
} | ConvertTo-Json

$laboratoryResponse = Test-Api -Endpoint "api/laboratories/register" -Method "POST" -Body $laboratoryBody -Description "Register Laboratory"

Write-Host "`nTest Complete!" -ForegroundColor Green
