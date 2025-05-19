# Test script for Healthcare APIs

# Function to make API requests
function Test-Api {
    param (
        [string]$Endpoint,
        [string]$Method = "GET",
        [string]$Body = $null
    )
    
    try {
        $headers = @{
            'Content-Type' = 'application/json'
        }
        
        if ($Body) {
            $response = Invoke-WebRequest -Uri "http://localhost:8080/$Endpoint" -Method $Method -Headers $headers -Body $Body
        } else {
            $response = Invoke-WebRequest -Uri "http://localhost:8080/$Endpoint" -Method $Method -Headers $headers
        }
        
        Write-Host "Testing $Endpoint ($Method)"
        Write-Host "Status: $($response.StatusCode)"
        Write-Host "Response: $($response.Content)"
        Write-Host "-" * 50
    }
    catch {
        Write-Host "Error testing $Endpoint ($Method)"
        Write-Host $_.Exception.Message
        Write-Host "-" * 50
    }
}

# Test all APIs
Write-Host "Testing Healthcare APIs..."
Write-Host "-" * 50

# Test Patient APIs
Test-Api -Endpoint "api/patients"
Test-Api -Endpoint "api/patients/1"
Test-Api -Endpoint "api/patients/patientId/1"

# Test Doctor APIs
Test-Api -Endpoint "api/doctors"
Test-Api -Endpoint "api/doctors/1"

# Test Hospital APIs
Test-Api -Endpoint "api/hospitals"
Test-Api -Endpoint "api/hospitals/1"

# Test Laboratory APIs
Test-Api -Endpoint "api/laboratories"
Test-Api -Endpoint "api/laboratories/1"

# Test Clinical Record APIs
Test-Api -Endpoint "api/clinical-records"
Test-Api -Endpoint "api/clinical-records/1"

# Test Second Opinion APIs
Test-Api -Endpoint "api/second-opinions"
Test-Api -Endpoint "api/second-opinions/1"

# Test Lab Test APIs
Test-Api -Endpoint "api/lab-tests"
Test-Api -Endpoint "api/lab-tests/1"

# Test Lab Result APIs
Test-Api -Endpoint "api/lab-results"
Test-Api -Endpoint "api/lab-results/1"

Write-Host "API Testing Complete"
