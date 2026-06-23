# ═══════════════════════════════════════════════════════════════════
# HookSniff Kotlin SDK — Maven Central Publish (Windows PowerShell)
# ═══════════════════════════════════════════════════════════════════
# Bu scripti Windows'ta PowerShell'de çalıştırın.
# Gereksinimler: JDK 11+, GPG (Gpg4win)
# ═══════════════════════════════════════════════════════════════════

Write-Host ""
Write-Host "HookSniff Kotlin SDK - Maven Central Publish" -ForegroundColor Green
Write-Host "=============================================" -ForegroundColor Green
Write-Host ""

# 1. GPG Key kontrol
Write-Host "1. GPG key kontrol ediliyor..." -ForegroundColor Yellow
$gpgKeys = gpg --list-secret-keys --keyid-format SHORT 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "GPG key bulunamadi! once bir key olusturun: gpg --full-generate-key" -ForegroundColor Red
    exit 1
}
$gpgKeyId = ($gpgKeys | Select-String "sec" | Select-Object -First 1).ToString().Split("/")[1].Split(" ")[0]
Write-Host "GPG key bulundu: $gpgKeyId" -ForegroundColor Green

# 2. GPG key export
Write-Host ""
Write-Host "2. GPG private key export ediliyor..." -ForegroundColor Yellow
$env:GPG_SIGNING_KEY = gpg --armor --export-secret-keys $gpgKeyId 2>$null
if ([string]::IsNullOrEmpty($env:GPG_SIGNING_KEY)) {
    Write-Host "GPG private key export edilemedi" -ForegroundColor Red
    exit 1
}
Write-Host "GPG key export edildi" -ForegroundColor Green

# 3. GPG passphrase
Write-Host ""
Write-Host "3. GPG passphrase..." -ForegroundColor Yellow
$securePass = Read-Host "GPG passphrase girin (bos birakabilirsiniz)" -AsSecureString
$env:GPG_SIGNING_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePass))

# 4. Maven Central credentials
Write-Host ""
Write-Host "4. Maven Central credentials..." -ForegroundColor Yellow
$env:OSSRH_USERNAME = "spwZqL"
$env:OSSRH_PASSWORD = "ledXS3IjH8MmDQX312BWiB2DsRxQM5CiY"
Write-Host "Credentials ayarlandi" -ForegroundColor Green

# 5. Build + Publish
Write-Host ""
Write-Host "5. Build + Publish..." -ForegroundColor Yellow
./gradlew.bat publishMavenJavaPublicationToOSSRHRepository --no-daemon

Write-Host ""
Write-Host "=============================================" -ForegroundColor Green
Write-Host "Maven Central'a upload edildi!" -ForegroundColor Green
Write-Host "Staging repos: https://s01.oss.sonatype.org/#stagingRepositories" -ForegroundColor Green
Write-Host "Release etmek icin: staging'de 'Close' -> 'Release' yapin" -ForegroundColor Green
Write-Host "=============================================" -ForegroundColor Green
Write-Host ""
Write-Host "Paket linki (yayinlandiktan sonra):"
Write-Host "https://central.sonatype.com/artifact/com.hooksniff/hooksniff-kotlin/0.5.0"
