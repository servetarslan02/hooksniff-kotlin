#!/bin/bash
# ═══════════════════════════════════════════════════════════════════
# HookSniff Kotlin SDK — Maven Central Publish Script
# ═══════════════════════════════════════════════════════════════════
# Bu scripti local bilgisayarınızda çalıştırın.
# Gereksinimler: JDK 11+, GPG key
#
# Kullanım:
#   chmod +x publish-maven-central.sh
#   ./publish-maven-central.sh
# ═══════════════════════════════════════════════════════════════════

set -euo pipefail

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

info()    { echo -e "${GREEN}✅ $1${NC}"; }
warn()    { echo -e "${YELLOW}⚠️  $1${NC}"; }
error()   { echo -e "${RED}❌ $1${NC}"; exit 1; }

echo ""
echo "🪝 HookSniff Kotlin SDK — Maven Central Publish"
echo "═══════════════════════════════════════════════"
echo ""

# ── 1. GPG Key kontrol ──
echo "1. GPG key kontrol ediliyor..."
GPG_KEY_ID=$(gpg --list-secret-keys --keyid-format SHORT 2>/dev/null | grep sec | head -1 | awk '{print $2}' | cut -d'/' -f2)
if [ -z "$GPG_KEY_ID" ]; then
    error "GPG key bulunamadı! Önce bir key oluşturun:
  gpg --full-generate-key"
fi
info "GPG key bulundu: $GPG_KEY_ID"

# ── 2. GPG key'yi export et ──
echo ""
echo "2. GPG private key export ediliyor..."
GPG_SIGNING_KEY=$(gpg --armor --export-secret-keys "$GPG_KEY_ID" 2>/dev/null)
if [ -z "$GPG_SIGNING_KEY" ]; then
    error "GPG private key export edilemedi"
fi
info "GPG key export edildi"

# ── 3. GPG passphrase al ──
echo ""
echo "3. GPG passphrase..."
read -sp "GPG passphrase girin (boş bırakabilirsiniz): " GPG_SIGNING_PASSWORD
echo ""
export GPG_SIGNING_KEY
export GPG_SIGNING_PASSWORD

# ── 4. Maven Central credentials ──
echo ""
echo "4. Maven Central credentials..."
export OSSRH_USERNAME="${OSSRH_USERNAME:-spwZqL}"
export OSSRH_PASSWORD="${OSSRH_PASSWORD:-ledXS3IjH8MmDQX312BWiB2DsRxQM5CiY}"
info "Credentials ayarlandı"

# ── 5. Build + Publish ──
echo ""
echo "5. Build + Publish..."
./gradlew publishMavenJavaPublicationToOSSRHRepository --no-daemon

echo ""
info "═══════════════════════════════════════════════"
info "Maven Central'a upload edildi!"
info "Staging repos: https://s01.oss.sonatype.org/#stagingRepositories"
info "Release etmek için: staging'de 'Close' → 'Release' yapın"
info "═══════════════════════════════════════════════"
echo ""
echo "Paket linki (yayınlandıktan sonra):"
echo "https://central.sonatype.com/artifact/com.hooksniff/hooksniff-kotlin/0.5.0"
echo ""
