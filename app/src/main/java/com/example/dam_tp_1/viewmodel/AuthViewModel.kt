package com.example.dam_tp_1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.regex.Pattern

// Modèle utilisateur pour Firestore
data class UserProfile(
    val id: String = "",
    val nom: String = "",
    val prenom: String = "",
    val age: Int = 0,
    val pays: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// Sealed class pour gérer les états de résultat
sealed class AuthResult {
    object Loading : AuthResult()
    data class Success(val user: FirebaseUser?) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Idle : AuthResult()
}

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // StateFlows pour les états UI
    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _authResult = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authResult: StateFlow<AuthResult> = _authResult

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isEmailVerified = MutableStateFlow(false)
    val isEmailVerified: StateFlow<Boolean> = _isEmailVerified

    // Regex pour validation
    private val emailRegex = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    )
    private val passwordRegex = Pattern.compile(
        "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%^&*()_+=-]).{8,}$"
    )

    init {
        // Vérifier l'état de connexion actuel
        _currentUser.value = auth.currentUser
        _isEmailVerified.value = auth.currentUser?.isEmailVerified ?: false

        // Charger le profil utilisateur si connecté
        auth.currentUser?.let { user ->
            loadUserProfile(user.uid)
        }
    }

    // Fonctions de validation
    fun validateEmail(email: String): Boolean = emailRegex.matcher(email).matches()

    fun validatePassword(password: String, nom: String, prenom: String): Boolean {
        if (!passwordRegex.matcher(password).matches()) return false
        val lower = password.lowercase()
        return !lower.contains(nom.lowercase()) && !lower.contains(prenom.lowercase())
    }

    // Inscription utilisateur
    fun registerUser(
        nom: String,
        prenom: String,
        age: Int,
        pays: String,
        email: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                // Validations
                if (password != confirmPassword) {
                    _errorMessage.value = "Les mots de passe ne correspondent pas."
                    return@launch
                }
                if (!validateEmail(email)) {
                    _errorMessage.value = "Email invalide."
                    return@launch
                }
                if (!validatePassword(password, nom, prenom)) {
                    _errorMessage.value = "Mot de passe trop faible. (Majuscule, chiffre, spécial, pas votre nom/prénom)"
                    return@launch
                }

                _authResult.value = AuthResult.Loading
                _isLoading.value = true

                println("🔍 DEBUG: Début inscription - $email")

                // Créer l'utilisateur Firebase Auth
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user

                if (user != null) {
                    println("🔍 DEBUG: Utilisateur créé avec succès - ${user.uid}")
                    _currentUser.value = user

                    // Envoyer email de vérification
                    try {
                        user.sendEmailVerification().await()
                        println("🔍 DEBUG: Email de vérification envoyé")
                    } catch (e: Exception) {
                        println("🔍 DEBUG: Erreur envoi email: ${e.message}")
                    }

                    // Créer le profil utilisateur dans Firestore
                    val userProfile = UserProfile(
                        id = user.uid,
                        nom = nom,
                        prenom = prenom,
                        age = age,
                        pays = pays,
                        email = email
                    )

                    try {
                        saveUserProfile(userProfile)
                        println("🔍 DEBUG: Profil utilisateur sauvegardé")
                    } catch (e: Exception) {
                        println("🔍 DEBUG: Erreur sauvegarde profil: ${e.message}")
                    }

                    _authResult.value = AuthResult.Success(user)
                    clearError() // Effacer les erreurs précédentes
                    onSuccess()
                } else {
                    _authResult.value = AuthResult.Error("Erreur lors de la création du compte")
                    _errorMessage.value = "Erreur lors de la création du compte"
                }
            } catch (e: Exception) {
                println("🔍 DEBUG: Erreur inscription: ${e.message}")

                val errorMessage = when {
                    e.message?.contains("email-already-in-use") == true -> "Cette adresse email est déjà utilisée"
                    e.message?.contains("invalid-email") == true -> "Adresse email invalide"
                    e.message?.contains("weak-password") == true -> "Mot de passe trop faible"
                    e.message?.contains("network-request-failed") == true -> "Erreur de connexion réseau"
                    else -> e.message ?: "Erreur inconnue lors de l'inscription"
                }

                _authResult.value = AuthResult.Error(errorMessage)
                _errorMessage.value = errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Connexion utilisateur - MISE À JOUR
    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                _authResult.value = AuthResult.Loading
                _isLoading.value = true

                println("🔍 DEBUG: Tentative de connexion - $email")

                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val user = authResult.user

                if (user != null) {
                    println("🔍 DEBUG: Connexion réussie - ${user.uid}")

                    // ✅ Toujours mettre à jour l'utilisateur actuel d'abord
                    _currentUser.value = user

                    // Recharger les informations utilisateur depuis Firebase
                    user.reload().await()

                    if (user.isEmailVerified) {
                        println("🔍 DEBUG: Email vérifié - connexion autorisée")
                        _isEmailVerified.value = true
                        loadUserProfile(user.uid)
                        _authResult.value = AuthResult.Success(user)
                        clearError() // Effacer toute erreur précédente
                        onSuccess()
                    } else {
                        println("🔍 DEBUG: Email non vérifié")
                        _isEmailVerified.value = false
                        _authResult.value = AuthResult.Error("Email non vérifié")
                        _errorMessage.value = "Veuillez vérifier votre adresse e-mail avant de vous connecter. Vérifiez votre boîte de réception ou vos spams."

                        // ✅ Optionnel: Renvoyer automatiquement un email de vérification
                        try {
                            user.sendEmailVerification().await()
                            println("🔍 DEBUG: Nouvel email de vérification envoyé")
                            _errorMessage.value = _errorMessage.value + "\n\nUn nouvel email de vérification a été envoyé."
                        } catch (e: Exception) {
                            println("🔍 DEBUG: Erreur renvoi email: ${e.message}")
                        }
                    }
                } else {
                    _authResult.value = AuthResult.Error("Erreur de connexion")
                    _errorMessage.value = "Erreur de connexion"
                }
            } catch (e: Exception) {
                println("🔍 DEBUG: Erreur de connexion: ${e.message}")

                // ✅ Messages d'erreur plus précis
                val errorMessage = when {
                    e.message?.contains("invalid-email") == true -> "Adresse email invalide"
                    e.message?.contains("user-disabled") == true -> "Ce compte a été désactivé"
                    e.message?.contains("user-not-found") == true -> "Aucun compte trouvé avec cette adresse email"
                    e.message?.contains("wrong-password") == true -> "Mot de passe incorrect"
                    e.message?.contains("invalid-credential") == true -> "Email ou mot de passe incorrect"
                    e.message?.contains("too-many-requests") == true -> "Trop de tentatives. Réessayez plus tard"
                    e.message?.contains("network-request-failed") == true -> "Erreur de connexion réseau"
                    else -> e.message ?: "Erreur de connexion inconnue"
                }

                _authResult.value = AuthResult.Error(errorMessage)
                _errorMessage.value = errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ MÉTHODE POUR RAFRAÎCHIR LE STATUT DE VÉRIFICATION EMAIL
    fun refreshEmailVerificationStatus() {
        viewModelScope.launch {
            try {
                val user = auth.currentUser
                if (user != null) {
                    println("🔍 DEBUG: Rafraîchissement du statut de vérification email")

                    // Recharger les informations utilisateur depuis Firebase
                    user.reload().await()

                    val isVerified = user.isEmailVerified
                    println("🔍 DEBUG: Statut de vérification: $isVerified")

                    _isEmailVerified.value = isVerified
                    _currentUser.value = user

                    if (isVerified) {
                        // Si l'email est maintenant vérifié, charger le profil et marquer comme succès
                        loadUserProfile(user.uid)
                        _authResult.value = AuthResult.Success(user)
                        clearError()
                        println("🔍 DEBUG: Email vérifié avec succès!")
                    } else {
                        println("🔍 DEBUG: Email toujours non vérifié")
                    }
                } else {
                    _errorMessage.value = "Aucun utilisateur connecté"
                }
            } catch (e: Exception) {
                println("🔍 DEBUG: Erreur rafraîchissement vérification: ${e.message}")
                _errorMessage.value = "Erreur lors de la vérification: ${e.message}"
            }
        }
    }

    // ✅ MÉTHODE POUR VÉRIFIER L'ÉTAT DE VÉRIFICATION EMAIL (ALIAS)
    fun checkEmailVerification() {
        refreshEmailVerificationStatus()
    }

    // Déconnexion
    fun signOut() {
        try {
            println("🔍 DEBUG: Déconnexion utilisateur")
            auth.signOut()
            _currentUser.value = null
            _userProfile.value = null
            _isEmailVerified.value = false
            _authResult.value = AuthResult.Idle
            clearError()
            println("🔍 DEBUG: Déconnexion terminée")
        } catch (e: Exception) {
            println("🔍 DEBUG: Erreur lors de la déconnexion: ${e.message}")
            _errorMessage.value = "Erreur lors de la déconnexion: ${e.message}"
        }
    }

    // Charger le profil utilisateur depuis Firestore
    private fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            try {
                println("🔍 DEBUG: Chargement du profil utilisateur: $userId")
                val document = firestore.collection("users").document(userId).get().await()
                if (document.exists()) {
                    val profile = document.toObject(UserProfile::class.java)
                    _userProfile.value = profile
                    println("🔍 DEBUG: Profil chargé avec succès: ${profile?.email}")
                } else {
                    println("🔍 DEBUG: Aucun profil trouvé pour l'utilisateur")
                }
            } catch (e: Exception) {
                println("🔍 DEBUG: Erreur chargement profil: ${e.message}")
                _errorMessage.value = "Erreur lors du chargement du profil: ${e.message}"
            }
        }
    }

    // Sauvegarder le profil utilisateur dans Firestore
    private suspend fun saveUserProfile(userProfile: UserProfile) {
        try {
            println("🔍 DEBUG: Sauvegarde du profil utilisateur")
            firestore.collection("users")
                .document(userProfile.id)
                .set(userProfile)
                .await()
            _userProfile.value = userProfile
            println("🔍 DEBUG: Profil sauvegardé avec succès")
        } catch (e: Exception) {
            println("🔍 DEBUG: Erreur sauvegarde profil: ${e.message}")
            _errorMessage.value = "Erreur lors de la sauvegarde: ${e.message}"
            throw e
        }
    }

    // Mettre à jour le profil utilisateur
    fun updateUserProfile(
        nom: String,
        prenom: String,
        age: Int,
        pays: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val currentUserId = _currentUser.value?.uid
                if (currentUserId != null) {
                    _isLoading.value = true

                    val updatedProfile = _userProfile.value?.copy(
                        nom = nom,
                        prenom = prenom,
                        age = age,
                        pays = pays,
                        updatedAt = System.currentTimeMillis()
                    ) ?: return@launch

                    firestore.collection("users")
                        .document(currentUserId)
                        .set(updatedProfile)
                        .await()

                    _userProfile.value = updatedProfile
                    onSuccess()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erreur lors de la mise à jour: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Réinitialiser le mot de passe
    fun resetPassword(email: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                if (!validateEmail(email)) {
                    _errorMessage.value = "Email invalide."
                    return@launch
                }

                _isLoading.value = true
                println("🔍 DEBUG: Envoi email de réinitialisation: $email")
                auth.sendPasswordResetEmail(email).await()
                println("🔍 DEBUG: Email de réinitialisation envoyé avec succès")
                onSuccess()
            } catch (e: Exception) {
                println("🔍 DEBUG: Erreur envoi email de réinitialisation: ${e.message}")
                _errorMessage.value = "Erreur lors de l'envoi de l'email: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ RENVOYER L'EMAIL DE VÉRIFICATION
    fun resendVerificationEmail(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser
                if (user != null && !user.isEmailVerified) {
                    println("🔍 DEBUG: Renvoi de l'email de vérification")
                    user.sendEmailVerification().await()
                    println("🔍 DEBUG: Email de vérification renvoyé avec succès")
                    onSuccess()
                } else if (user != null && user.isEmailVerified) {
                    _errorMessage.value = "Votre email est déjà vérifié"
                } else {
                    _errorMessage.value = "Aucun utilisateur connecté"
                }
            } catch (e: Exception) {
                println("🔍 DEBUG: Erreur renvoi email de vérification: ${e.message}")
                _errorMessage.value = "Erreur lors de l'envoi: ${e.message}"
            }
        }
    }

    // Supprimer le compte utilisateur
    fun deleteAccount(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser
                val userId = user?.uid

                if (userId != null) {
                    _isLoading.value = true

                    // Supprimer le profil de Firestore
                    firestore.collection("users").document(userId).delete().await()

                    // Supprimer le compte Firebase Auth
                    user.delete().await()

                    // Réinitialiser les états
                    _currentUser.value = null
                    _userProfile.value = null
                    _isEmailVerified.value = false
                    _authResult.value = AuthResult.Idle

                    onSuccess()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erreur lors de la suppression: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Obtenir le profil utilisateur actuel
    fun getCurrentUserProfile(): UserProfile? = _userProfile.value

    // Vérifier si l'utilisateur est connecté
    fun isUserLoggedIn(): Boolean = _currentUser.value != null

    // Vérifier si l'utilisateur est connecté ET vérifié
    fun isUserLoggedInAndVerified(): Boolean = _currentUser.value != null && _isEmailVerified.value

    // Effacer les erreurs
    fun clearError() {
        _errorMessage.value = null
    }

    // Réinitialiser l'état d'authentification
    fun resetAuthState() {
        _authResult.value = AuthResult.Idle
        clearError()
    }

    // Dans AuthViewModel.kt
    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                auth.signOut()

                // Reset states
                _currentUser.value = null
                _isEmailVerified.value = false
                _errorMessage.value = null

                onLogoutComplete()
            } catch (e: Exception) {
                _errorMessage.value = "Erreur lors de la déconnexion: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

}
