# Test script for Hospital API

# Function to make API requests
function Test-HospitalApi {
    param (
        [string]$Method,
        [string]$Endpoint,
        [string]$Body = $null
    )

    $url = "http://localhost:8090/api/$Endpoint"
    
    if ($Body) {
        $response = Invoke-RestMethod -Method $Method -Uri $url -Body $Body -ContentType "application/json"
    } else {
        $response = Invoke-RestMethod -Method $Method -Uri $url
    }
    
    Write-Host "Response:"
    Write-Host ($response | ConvertTo-Json -Depth 10)
}

# Test Hospital Registration
Write-Host "Testing Hospital Registration..."
$hospitalBody = @{
    name = "Test Hospital"
    ownership = "Government"
    district = "Test District"
    location = "Test Location"
    contactInfo = "1234567890"
    username = "testuser"
    password = "testpassword"
} | ConvertTo-Json

Test-HospitalApi -Method POST -Endpoint "hospitals/register" -Body $hospitalBody

# Test Get All Hospitals
Write-Host "`nTesting Get All Hospitals..."
Test-HospitalApi -Method GET -Endpoint "hospitals"
