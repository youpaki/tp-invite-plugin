#!/bin/bash

# Script pour créer automatiquement une release GitHub
# Nécessite GitHub CLI (gh) installé et configuré

set -e

# Couleurs
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# Vérifier que GitHub CLI est installé
if ! command -v gh &> /dev/null; then
    echo -e "${RED}Erreur: GitHub CLI (gh) n'est pas installé${NC}"
    echo -e "${YELLOW}Installation: https://cli.github.com/${NC}"
    exit 1
fi

# Vérifier l'authentification GitHub
if ! gh auth status &> /dev/null; then
    echo -e "${RED}Erreur: Non authentifié avec GitHub${NC}"
    echo -e "${YELLOW}Lancez: gh auth login${NC}"
    exit 1
fi

# Obtenir la version actuelle
CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
TAG_NAME="v${CURRENT_VERSION}"

# Vérifier que le tag existe
if ! git tag -l | grep -q "^${TAG_NAME}$"; then
    echo -e "${RED}Erreur: Le tag ${TAG_NAME} n'existe pas${NC}"
    echo -e "${YELLOW}Lancez d'abord: ./version.sh [patch|minor|major]${NC}"
    exit 1
fi

# Vérifier que le JAR existe
JAR_FILE="target/tp-invite-plugin-${CURRENT_VERSION}.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo -e "${RED}Erreur: Le fichier JAR n'existe pas: ${JAR_FILE}${NC}"
    echo -e "${YELLOW}Lancez: mvn clean package${NC}"
    exit 1
fi

echo -e "${BLUE}=== Création de la release GitHub ===${NC}"
echo -e "${YELLOW}Version: ${CURRENT_VERSION}${NC}"
echo -e "${YELLOW}Tag: ${TAG_NAME}${NC}"
echo -e "${YELLOW}JAR: ${JAR_FILE}${NC}"

# Générer les notes de release
RELEASE_NOTES="# TpInvite Plugin ${CURRENT_VERSION}

Plugin PaperMC pour demandes de téléportation entre joueurs.

## 🎮 Fonctionnalités

- **Demandes de téléportation** entre joueurs avec confirmation
- **Interface cliquable** dans le chat (boutons accepter/refuser)
- **Délai de sécurité** de 5 secondes (annulé si mouvement)
- **Expiration automatique** des demandes (60 secondes)
- **Système de permissions** granulaire
- **Support Minecraft 1.20.4**

## 📥 Installation

1. Téléchargez le fichier \`tp-invite-plugin-${CURRENT_VERSION}.jar\`
2. Placez-le dans le dossier \`plugins/\` de votre serveur PaperMC 1.20.4
3. Redémarrez le serveur
4. Utilisez \`/tpr <joueur>\` pour demander une téléportation !

## 🎯 Commandes

- \`/tpr <joueur>\` - Demander une téléportation
- \`/tpaccept [joueur]\` - Accepter une demande  
- \`/tpdeny [joueur]\` - Refuser une demande

## 🔧 Prérequis

- **Serveur**: PaperMC 1.20.4 ou plus récent
- **Java**: 17 ou plus récent

---

**Téléchargement**: Voir les assets ci-dessous 👇"

# Créer la release
echo -e "${BLUE}Création de la release...${NC}"

gh release create "$TAG_NAME" \
    "$JAR_FILE" \
    --title "TpInvite Plugin ${CURRENT_VERSION}" \
    --notes "$RELEASE_NOTES" \
    --latest

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Release créée avec succès !${NC}"
    
    # Obtenir l'URL de la release
    REPO_URL=$(gh repo view --json url -q .url)
    echo -e "${BLUE}🚀 Release disponible sur: ${REPO_URL}/releases/tag/${TAG_NAME}${NC}"
else
    echo -e "${RED}❌ Erreur lors de la création de la release${NC}"
    exit 1
fi