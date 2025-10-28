# TpInvite Plugin

Un plugin PaperMC moderne pour Minecraft 1.20.4 qui permet aux joueurs de demander des téléportations entre eux avec un système de confirmation intuitif.

## ✨ Fonctionnalités

- **Demandes de téléportation** entre joueurs avec système de confirmation
- **Interface cliquable** dans le chat pour accepter/refuser facilement
- **Délai de sécurité** de 5 secondes avant téléportation (annulé si le joueur bouge)
- **Expiration automatique** des demandes après 60 secondes
- **Système de permissions** granulaire
- **Commandes avec alias** pour une utilisation flexible
- **Messages colorés** et informatifs

## 🎮 Commandes

| Commande | Alias | Description | Permission |
|----------|-------|-------------|------------|
| `/tpr <joueur>` | `/tprequest`, `/tpa` | Demander une téléportation vers un joueur | `tpinvite.request` |
| `/tpaccept [joueur]` | `/tpyes`, `/tpy` | Accepter une demande de téléportation | `tpinvite.accept` |
| `/tpdeny [joueur]` | `/tpno`, `/tpn` | Refuser une demande de téléportation | `tpinvite.accept` |

## 🔐 Permissions

- `tpinvite.request` - Permet de demander des téléportations (défaut: `true`)
- `tpinvite.accept` - Permet d'accepter/refuser des demandes (défaut: `true`)

## 📥 Installation

1. Téléchargez le fichier JAR depuis les [Releases](../../releases)
2. Placez le fichier dans le dossier `plugins/` de votre serveur PaperMC
3. Redémarrez le serveur
4. Le plugin est prêt à l'utilisation !

## 🚀 Utilisation

### Demander une téléportation
```
/tpr PlayerName
```

### Répondre à une demande
Cliquez sur les boutons **[ACCEPTER]** ou **[REFUSER]** dans le chat, ou utilisez :
```
/tpaccept PlayerName    # Accepter
/tpdeny PlayerName      # Refuser
```

### Accepter/refuser la dernière demande
```
/tpaccept    # Accepte automatiquement la dernière demande reçue
/tpdeny      # Refuse automatiquement la dernière demande reçue
```

## ⚙️ Configuration

Le plugin fonctionne immédiatement sans configuration. Les paramètres par défaut sont :
- Délai de téléportation : 5 secondes
- Expiration des demandes : 60 secondes
- Distance maximale de mouvement : 0.5 blocs

## 🛠️ Développement

### Prérequis
- Java 17+
- Maven 3.6+
- Git

### Compilation
```bash
mvn clean package
```

### Versioning automatique
Utilisez le script fourni pour créer automatiquement une nouvelle version :

```bash
# Version patch (1.0.0 -> 1.0.1)
./version.sh patch

# Version minor (1.0.0 -> 1.1.0)  
./version.sh minor

# Version major (1.0.0 -> 2.0.0)
./version.sh major
```

Le script va :
1. Mettre à jour la version dans `pom.xml`
2. Compiler le plugin
3. Créer un commit et un tag Git
4. Pousser vers origin (si configuré)

### Structure du projet
```
src/
├── main/
│   ├── java/com/tpinvite/
│   │   ├── TpInvitePlugin.java          # Classe principale
│   │   ├── commands/                    # Commandes du plugin
│   │   ├── listeners/                   # Event listeners
│   │   └── managers/                    # Gestionnaires de logique
│   └── resources/
│       └── plugin.yml                   # Configuration du plugin
```

## 🤝 Contribuer

1. Forkez le projet
2. Créez une branche feature (`git checkout -b feature/AmazingFeature`)
3. Committez vos changements (`git commit -m 'Add some AmazingFeature'`)
4. Poussez vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrez une Pull Request

## 📝 Changelog

### v1.0.0 (2024-10-28)
- **Initial Release**
- Système de demandes de téléportation entre joueurs
- Interface cliquable dans le chat
- Délai de 5 secondes avec annulation sur mouvement
- Système de permissions
- Expiration automatique des demandes
- Support Minecraft 1.20.4

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

## ✨ Remerciements

- [PaperMC](https://papermc.io/) pour l'excellent API serveur
- [Adventure](https://docs.adventure.kyori.net/) pour le système de messages modernes
- La communauté Minecraft pour l'inspiration

---

**Fait avec ❤️ pour la communauté Minecraft**