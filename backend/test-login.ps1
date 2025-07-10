$body = @{
    email = "superadmin@bika.com"
    password = "superadmin123"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $body
    Write-Host "Login successful!"
    Write-Host "Token: $($response.token)"
} catch {
    Write-Host "Login failed: $($_.Exception.Message)"
    Write-Host "Response: $($_.Exception.Response)"
} 