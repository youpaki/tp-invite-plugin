#!/bin/bash

# Script d'automatisation pour le versioning et les releases
# Usage: ./version.sh [patch|minor|major]

set -e

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Fonction d'aide
print_help() {
    echo -e "${BLUE}Usage: $0 [patch|minor|major]${NC}"
    echo ""
    echo "  patch  - Increment patch version (1.0.0 -> 1.0.1)"
    echo "  minor  - Increment minor version (1.0.0 -> 1.1.0)"
    echo "  major  - Increment major version (1.0.0 -> 2.0.0)"
    echo ""
    echo "Le script va:"
    echo "  1. Mettre à jour la version dans pom.xml"
    echo "  2. Compiler le plugin"
    echo "  3. Créer un commit et un tag Git"
    echo "  4. Pousser vers origin (si configuré)"
}

# Vérifier les arguments
if [ $# -ne 1 ] || [[ ! "$1" =~ ^(patch|minor|major)$ ]]; then
    print_help
    exit 1
fi

VERSION_TYPE=$1

# Vérifier que Maven est installé
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}Erreur: Maven n'est pas installé ou pas dans le PATH${NC}"
    exit 1
fi

# Vérifier que Git est installé
if ! command -v git &> /dev/null; then
    echo -e "${RED}Erreur: Git n'est pas installé ou pas dans le PATH${NC}"
    exit 1
fi

# Vérifier qu'on est dans un repository Git
if [ ! -d .git ]; then
    echo -e "${RED}Erreur: Ce répertoire n'est pas un repository Git${NC}"
    exit 1
fi

# Vérifier qu'il n'y a pas de changements non commités
if ! git diff-index --quiet HEAD --; then
    echo -e "${RED}Erreur: Il y a des changements non commités. Veuillez d'abord les commiter.${NC}"
    exit 1
fi

echo -e "${BLUE}=== Automatisation du versioning TpInvite Plugin ===${NC}"

# Obtenir la version actuelle depuis pom.xml
CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo -e "${YELLOW}Version actuelle: ${CURRENT_VERSION}${NC}"

# Calculer la nouvelle version
IFS='.' read -ra VERSION_PARTS <<< "$CURRENT_VERSION"
MAJOR=${VERSION_PARTS[0]}
MINOR=${VERSION_PARTS[1]}
PATCH=${VERSION_PARTS[2]}

case $VERSION_TYPE in
    "patch")
        PATCH=$((PATCH + 1))
        ;;
    "minor")
        MINOR=$((MINOR + 1))
        PATCH=0
        ;;
    "major")
        MAJOR=$((MAJOR + 1))
        MINOR=0
        PATCH=0
        ;;
esac

NEW_VERSION="${MAJOR}.${MINOR}.${PATCH}"
echo -e "${GREEN}Nouvelle version: ${NEW_VERSION}${NC}"

# Demander confirmation
read -p "Continuer avec la version $NEW_VERSION? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}Opération annulée${NC}"
    exit 0
fi

echo -e "${BLUE}Mise à jour de la version...${NC}"

# Mettre à jour la version dans pom.xml
mvn versions:set -DnewVersion=$NEW_VERSION -DgenerateBackupPoms=false

echo -e "${BLUE}Compilation du plugin...${NC}"

# Nettoyer et compiler
mvn clean package

# Vérifier que la compilation a réussi
if [ $? -ne 0 ]; then
    echo -e "${RED}Erreur: La compilation a échoué${NC}"
    exit 1
fi

echo -e "${BLUE}Création du commit Git...${NC}"

# Ajouter les changements
git add .

# Créer le commit
COMMIT_MSG="Bump version to ${NEW_VERSION}

- Updated plugin version to ${NEW_VERSION}
- Compiled and packaged JAR file
- Ready for release"

git commit -m "$COMMIT_MSG"

# Créer le tag
TAG_MSG="Release v${NEW_VERSION}

TpInvite Plugin version ${NEW_VERSION}
- Teleportation request system
- 5-second countdown with cancellation
- Clickable chat interface
- Permission system
- Minecraft 1.20.4 support

Built on $(date)"

git tag -a "v${NEW_VERSION}" -m "$TAG_MSG"

echo -e "${GREEN}✓ Version mise à jour vers ${NEW_VERSION}${NC}"
echo -e "${GREEN}✓ Plugin compilé avec succès${NC}"
echo -e "${GREEN}✓ Commit et tag créés${NC}"

# Vérifier si origin est configuré
if git remote get-url origin &> /dev/null; then
    echo -e "${BLUE}Push vers origin...${NC}"
    
    # Pousser le commit et les tags
    git push origin main 2>/dev/null || git push origin master 2>/dev/null || echo -e "${YELLOW}Branche par défaut non trouvée, push manuel requis${NC}"
    git push origin "v${NEW_VERSION}"
    
    echo -e "${GREEN}✓ Poussé vers origin${NC}"
    
    # Afficher le lien GitHub (si applicable)
    ORIGIN_URL=$(git remote get-url origin)
    if [[ $ORIGIN_URL == *github.com* ]]; then
        # Extraire l'URL du repository GitHub
        REPO_URL=$(echo $ORIGIN_URL | sed 's/\.git$//' | sed 's/git@github\.com:/https:\/\/github.com\//')
        echo -e "${BLUE}🚀 Créer une release sur GitHub:${NC}"
        echo -e "${BLUE}   ${REPO_URL}/releases/new?tag=v${NEW_VERSION}${NC}"
    fi
else
    echo -e "${YELLOW}Aucun remote 'origin' configuré. Ajoutez votre repository distant avec:${NC}"
    echo -e "${YELLOW}   git remote add origin <URL_DU_REPOSITORY>${NC}"
    echo -e "${YELLOW}Puis poussez avec:${NC}"
    echo -e "${YELLOW}   git push origin main${NC}"
    echo -e "${YELLOW}   git push origin v${NEW_VERSION}${NC}"
fi

echo -e "${GREEN}=== Versioning terminé avec succès! ===${NC}"
echo -e "${BLUE}Fichier JAR: target/tp-invite-plugin-${NEW_VERSION}.jar${NC}"