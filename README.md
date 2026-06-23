# Spring Ride Connect

Plateforme de covoiturage type BlaBlaCar.

## Prérequis

- Java 21
- Maven
- MySQL / H2

## Lancement du projet

Pour compiler et lancer le projet en local :

```bash
./mvnw spring-boot:run
```

## Pousser le projet sur GitHub

1. Créez un nouveau dépôt sur GitHub (sans initialiser de README ou de .gitignore).
2. Ouvrez un terminal à la racine de ce projet (`c:\Users\REDA\Desktop\My Projects\springride_VF`).
3. Exécutez les commandes suivantes :

```bash
# Initialiser le dépôt Git local (s'il n'est pas déjà initialisé)
git init

# Ajouter tous les fichiers au suivi (le .gitignore exclura les dossiers comme target/ et uploads/)
git add .

# Créer un premier commit
git commit -m "Initial commit"

# Renommer la branche principale en 'main'
git branch -M main

# Lier votre dépôt local au dépôt distant (remplacez l'URL par celle de votre dépôt)
git remote add origin https://github.com/<votre-nom-utilisateur>/<nom-du-repo>.git

# Pousser le code vers GitHub
git push -u origin main
```
