# 📱 Ma Collection - Gestionnaire de Produits Personnel

> Application Android moderne de gestion de collection de produits avec authentification Firebase

![Android](https://img.shields.io/badge/Android-Kotlin-blue?logo=android)
![Firebase](https://img.shields.io/badge/Firebase-Cloud-orange?logo=firebase)
![Jetpack Compose](https://img.shields.io/badge/Jetpack-Compose-green?logo=jetpack-compose)

---

## 📋 Table des matières

- [À propos](#-à-propos)
- [Fonctionnalités](#-fonctionnalités)
- [Technologies](#-technologies)
- [Architecture](#-architecture)
- [Installation](#-installation)
- [Configuration Firebase](#-configuration-firebase)
- [Utilisation](#-utilisation)
- [Structure du projet](#-structure-du-projet)
- [Captures d'écran](#-captures-décran)
- [Auteur](#-auteur)

---

## 🎯 À propos

**Ma Collection** est une application Android native permettant aux utilisateurs de gérer leur collection personnelle de produits (consommables, durables, etc.). Chaque utilisateur possède sa propre collection sécurisée dans Firebase Firestore.

### Cas d'usage
- Gestion d'inventaire personnel
- Suivi des achats et garanties
- Organisation de collections (électronique, vêtements, etc.)
- Notes et évaluations personnelles

---

## ✨ Fonctionnalités

### 🔐 Authentification
- ✅ Inscription avec validation (email, mot de passe sécurisé)
- ✅ Vérification d'email obligatoire
- ✅ Connexion sécurisée
- ✅ Gestion de profil utilisateur

### 📦 Gestion de produits
- ✅ Ajout multi-étapes (3 steps avec formulaire guidé)
- ✅ Catégorisation (Consommable, Durable, Autre)
- ✅ Informations détaillées (marque, prix, date d'achat, pays d'origine)
- ✅ Sélecteur de couleur personnalisé
- ✅ Upload d'images (optionnel)
- ✅ Notes et évaluations (système 5 étoiles)
- ✅ Gestion des garanties
- ✅ Favoris

### 🔍 Recherche et filtres
- ✅ Recherche par nom
- ✅ Filtres par type, pays, favoris
- ✅ Tri personnalisé (date, prix, nom, note)
- ✅ Pagination

### 🎨 Interface moderne
- ✅ Material Design 3
- ✅ Dark/Light mode (automatique)
- ✅ Animations fluides
- ✅ UI/UX optimisée
- ✅ États vides élégants

---

## 🛠 Technologies

### Frontend
- **Kotlin** - Langage principal
- **Jetpack Compose** - UI moderne et déclarative
- **Material Design 3** - Design system
- **Coil** - Chargement d'images

### Backend
- **Firebase Authentication** - Gestion des utilisateurs
- **Firebase Firestore** - Base de données NoSQL
- **Firebase Security Rules** - Sécurisation des données

### Architecture
- **MVVM** (Model-View-ViewModel)
- **StateFlow** pour la gestion d'état
- **Coroutines** pour l'asynchrone
- **Navigation Compose** pour la navigation

### APIs externes
- **REST Countries API** - Liste des pays avec drapeaux

---

