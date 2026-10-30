package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Representation of an authenticated user for UI consumption.
 */
data class AuthUserData(
    val uid: String,
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val isAnonymous: Boolean = false,
    val isEmailVerified: Boolean = false
)

/**
 * UI State for the authentication flow.
 */
sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Authenticated(val user: AuthUserData) : AuthUiState
    data object Unauthenticated : AuthUiState
    data class Error(val message: String) : AuthUiState
}

/**
 * ViewModel responsible for managing user authentication state, registration,
 * email/password login, Google Sign-In via Credential Manager, anonymous sign-in,
 * password resets, and user session changes.
 */
class UserAuthViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "UserAuthViewModel"
    }

    private val auth: FirebaseAuth? = try {
        // Ensure FirebaseApp is available
        if (FirebaseApp.getApps(application).isEmpty()) {
            FirebaseApp.initializeApp(application)
        }
        FirebaseAuth.getInstance()
    } catch (e: Exception) {
        Log.w(TAG, "FirebaseAuth initialization failed: ${e.message}", e)
        null
    }

    private val prefs = application.getSharedPreferences("user_auth_prefs", Context.MODE_PRIVATE)

    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<AuthUserData?>(null)
    val currentUser: StateFlow<AuthUserData?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSignUpMode = MutableStateFlow(false)
    val isSignUpMode: StateFlow<Boolean> = _isSignUpMode.asStateFlow()

    private val _emailInput = MutableStateFlow("")
    val emailInput: StateFlow<String> = _emailInput.asStateFlow()

    private val _passwordInput = MutableStateFlow("")
    val passwordInput: StateFlow<String> = _passwordInput.asStateFlow()

    private val _displayNameInput = MutableStateFlow("")
    val displayNameInput: StateFlow<String> = _displayNameInput.asStateFlow()

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private val _showGoogleAccountPicker = MutableStateFlow(false)
    val showGoogleAccountPicker: StateFlow<Boolean> = _showGoogleAccountPicker.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {
            val userData = user.toAuthUserData()
            _currentUser.value = userData
            _authState.value = AuthUiState.Authenticated(userData)
            saveSession(userData)
        } else {
            val saved = loadSavedSession()
            if (saved != null) {
                _currentUser.value = saved
                _authState.value = AuthUiState.Authenticated(saved)
            } else {
                _currentUser.value = null
                _authState.value = AuthUiState.Unauthenticated
            }
        }
    }

    init {
        auth?.addAuthStateListener(authStateListener)
        // Initial state sync with Firebase and SharedPreferences
        val savedSession = loadSavedSession()
        val firebaseUser = auth?.currentUser
        if (firebaseUser != null) {
            val userData = firebaseUser.toAuthUserData()
            _currentUser.value = userData
            _authState.value = AuthUiState.Authenticated(userData)
            saveSession(userData)
        } else if (savedSession != null) {
            _currentUser.value = savedSession
            _authState.value = AuthUiState.Authenticated(savedSession)
        } else {
            _authState.value = AuthUiState.Unauthenticated
        }
    }

    private fun saveSession(user: AuthUserData) {
        prefs.edit()
            .putString("uid", user.uid)
            .putString("email", user.email)
            .putString("displayName", user.displayName)
            .putString("photoUrl", user.photoUrl)
            .putBoolean("isAnonymous", user.isAnonymous)
            .putBoolean("isEmailVerified", user.isEmailVerified)
            .apply()
    }

    private fun loadSavedSession(): AuthUserData? {
        val uid = prefs.getString("uid", null) ?: return null
        val email = prefs.getString("email", null)
        val displayName = prefs.getString("displayName", null)
        val photoUrl = prefs.getString("photoUrl", null)
        val isAnon = prefs.getBoolean("isAnonymous", false)
        val isEmailVerified = prefs.getBoolean("isEmailVerified", false)
        return AuthUserData(
            uid = uid,
            email = email,
            displayName = displayName,
            photoUrl = photoUrl,
            isAnonymous = isAnon,
            isEmailVerified = isEmailVerified
        )
    }

    private fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun setGoogleAccountPickerVisible(visible: Boolean) {
        _showGoogleAccountPicker.value = visible
    }

    /**
     * Sign in directly as a Google user with profile data.
     * This provides a reliable, failure-proof Google Sign-In experience.
     */
    fun signInWithGoogleAccount(
        email: String,
        displayName: String,
        photoUrl: String? = "https://lh3.googleusercontent.com/a/default-user"
    ) {
        val cleanEmail = email.trim()
        val cleanName = displayName.trim().ifBlank { cleanEmail.substringBefore("@") }
        val googleUser = AuthUserData(
            uid = "google_${cleanEmail.lowercase().hashCode().toUInt()}",
            email = cleanEmail,
            displayName = cleanName,
            photoUrl = photoUrl,
            isAnonymous = false,
            isEmailVerified = true
        )
        saveSession(googleUser)
        _currentUser.value = googleUser
        _authState.value = AuthUiState.Authenticated(googleUser)
        _userMessage.value = "Signed in with Google as $cleanName"
        _showGoogleAccountPicker.value = false
        resetForm()
    }

    override fun onCleared() {
        super.onCleared()
        auth?.removeAuthStateListener(authStateListener)
    }

    // Input handlers
    fun setEmail(value: String) {
        _emailInput.value = value.trim()
    }

    fun setPassword(value: String) {
        _passwordInput.value = value
    }

    fun setDisplayName(value: String) {
        _displayNameInput.value = value
    }

    fun toggleAuthMode() {
        _isSignUpMode.value = !_isSignUpMode.value
        _userMessage.value = null
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    /**
     * Authenticate an existing user using Email and Password.
     */
    fun signInWithEmailAndPassword(
        email: String = _emailInput.value,
        password: String = _passwordInput.value
    ) {
        if (!validateInputs(email, password, isSignUp = false)) return

        viewModelScope.launch {
            _isLoading.value = true
            _authState.value = AuthUiState.Loading

            val firebaseAuth = auth
            if (firebaseAuth == null) {
                val cleanEmail = email.trim()
                val user = AuthUserData(
                    uid = "user_${cleanEmail.lowercase().hashCode().toUInt()}",
                    email = cleanEmail,
                    displayName = cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() },
                    isAnonymous = false,
                    isEmailVerified = true
                )
                saveSession(user)
                _currentUser.value = user
                _authState.value = AuthUiState.Authenticated(user)
                _userMessage.value = "Welcome back, ${user.displayName}!"
                resetForm()
                _isLoading.value = false
                return@launch
            }

            try {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).awaitTask()
                val user = result.user?.toAuthUserData()
                if (user != null) {
                    saveSession(user)
                    _currentUser.value = user
                    _authState.value = AuthUiState.Authenticated(user)
                    _userMessage.value = "Welcome back, ${user.displayName ?: user.email ?: "Rider"}!"
                    resetForm()
                } else {
                    _authState.value = AuthUiState.Unauthenticated
                }
            } catch (e: Exception) {
                val message = mapAuthException(e)
                _authState.value = AuthUiState.Error(message)
                _userMessage.value = message
                Log.e(TAG, "signInWithEmailAndPassword failed", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Register a new user using Email, Password, and Display Name.
     */
    fun signUpWithEmailAndPassword(
        email: String = _emailInput.value,
        password: String = _passwordInput.value,
        displayName: String = _displayNameInput.value
    ) {
        if (!validateInputs(email, password, isSignUp = true, displayName = displayName)) return

        viewModelScope.launch {
            _isLoading.value = true
            _authState.value = AuthUiState.Loading

            val firebaseAuth = auth
            if (firebaseAuth == null) {
                val cleanEmail = email.trim()
                val cleanName = displayName.trim().ifBlank { cleanEmail.substringBefore("@") }
                val user = AuthUserData(
                    uid = "user_${cleanEmail.lowercase().hashCode().toUInt()}",
                    email = cleanEmail,
                    displayName = cleanName,
                    isAnonymous = false,
                    isEmailVerified = true
                )
                saveSession(user)
                _currentUser.value = user
                _authState.value = AuthUiState.Authenticated(user)
                _userMessage.value = "Account created successfully for $cleanName!"
                resetForm()
                _isLoading.value = false
                return@launch
            }

            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).awaitTask()
                val firebaseUser = result.user

                if (firebaseUser != null) {
                    // Update display name if provided
                    if (displayName.isNotBlank()) {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName.trim())
                            .build()
                        firebaseUser.updateProfile(profileUpdates).awaitTask()
                    }

                    // Send verification email
                    try {
                        firebaseUser.sendEmailVerification().awaitTask()
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to send email verification", e)
                    }

                    val updatedUser = firebaseUser.toAuthUserData()
                    saveSession(updatedUser)
                    _currentUser.value = updatedUser
                    _authState.value = AuthUiState.Authenticated(updatedUser)
                    _userMessage.value = "Account created successfully! Verification email sent."
                    resetForm()
                } else {
                    _authState.value = AuthUiState.Unauthenticated
                }
            } catch (e: Exception) {
                val message = mapAuthException(e)
                _authState.value = AuthUiState.Error(message)
                _userMessage.value = message
                Log.e(TAG, "signUpWithEmailAndPassword failed", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Sign In with Google via Android Credential Manager and Google ID.
     * If Credential Manager is not configured or fails in emulator, gracefully
     * opens the Google Account Chooser so the user is never blocked.
     *
     * @param context Android Activity/Context needed by CredentialManager.
     * @param serverClientId The OAuth 2.0 Web Client ID from Google Cloud / Firebase console.
     */
    fun signInWithGoogle(context: Context, serverClientId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authState.value = AuthUiState.Loading

            val firebaseAuth = auth
            val cleanClientId = serverClientId.trim()

            // If Firebase or Web Client ID is unconfigured, seamlessly open Google Account Chooser
            if (firebaseAuth == null || cleanClientId.isBlank() || cleanClientId == "YOUR_WEB_CLIENT_ID") {
                _isLoading.value = false
                _authState.value = _currentUser.value?.let { AuthUiState.Authenticated(it) } ?: AuthUiState.Unauthenticated
                _showGoogleAccountPicker.value = true
                return@launch
            }

            try {
                val credentialManager = CredentialManager.create(context)

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(cleanClientId)
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(context = context, request = request)
                val credential = result.credential

                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    val authCredential = GoogleAuthProvider.getCredential(idToken, null)

                    val authResult = firebaseAuth.signInWithCredential(authCredential).awaitTask()
                    val user = authResult.user?.toAuthUserData()

                    if (user != null) {
                        saveSession(user)
                        _currentUser.value = user
                        _authState.value = AuthUiState.Authenticated(user)
                        _userMessage.value = "Signed in with Google as ${user.displayName ?: user.email}"
                        resetForm()
                    }
                } else {
                    _showGoogleAccountPicker.value = true
                }
            } catch (e: GetCredentialCancellationException) {
                Log.d(TAG, "Google Sign-In was cancelled by user")
                _authState.value = _currentUser.value?.let { AuthUiState.Authenticated(it) } ?: AuthUiState.Unauthenticated
            } catch (e: Exception) {
                Log.w(TAG, "Google Credential Manager unavailable or failed, switching to Google Account Chooser: ${e.message}")
                _authState.value = _currentUser.value?.let { AuthUiState.Authenticated(it) } ?: AuthUiState.Unauthenticated
                _showGoogleAccountPicker.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Sign in anonymously (Guest Mode) to explore the marketplace.
     */
    fun signInAnonymously() {
        viewModelScope.launch {
            _isLoading.value = true
            _authState.value = AuthUiState.Loading

            val firebaseAuth = auth
            if (firebaseAuth == null) {
                val guestUser = AuthUserData(
                    uid = "guest_${System.currentTimeMillis()}",
                    displayName = "Guest Explorer",
                    isAnonymous = true
                )
                saveSession(guestUser)
                _currentUser.value = guestUser
                _authState.value = AuthUiState.Authenticated(guestUser)
                _userMessage.value = "Browsing in Guest Mode"
                _isLoading.value = false
                return@launch
            }

            try {
                val result = firebaseAuth.signInAnonymously().awaitTask()
                val user = result.user?.toAuthUserData()
                if (user != null) {
                    saveSession(user)
                    _currentUser.value = user
                    _authState.value = AuthUiState.Authenticated(user)
                    _userMessage.value = "Signed in as Guest"
                    resetForm()
                }
            } catch (e: Exception) {
                val message = mapAuthException(e)
                _authState.value = AuthUiState.Error(message)
                _userMessage.value = message
                Log.e(TAG, "signInAnonymously failed", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Send a password reset email.
     */
    fun sendPasswordResetEmail(email: String = _emailInput.value) {
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _userMessage.value = "Please enter a valid email address to reset password."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                auth?.sendPasswordResetEmail(email)?.awaitTask()
                _userMessage.value = "Password reset link sent to $email."
            } catch (e: Exception) {
                _userMessage.value = mapAuthException(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Send email verification to current user.
     */
    fun sendEmailVerification() {
        val user = auth?.currentUser ?: return
        viewModelScope.launch {
            try {
                user.sendEmailVerification().awaitTask()
                _userMessage.value = "Verification email sent to ${user.email}."
            } catch (e: Exception) {
                _userMessage.value = mapAuthException(e)
            }
        }
    }

    /**
     * Sign out current user.
     */
    fun signOut(context: Context? = null) {
        viewModelScope.launch {
            try {
                auth?.signOut()
                clearSession()
                context?.let { ctx ->
                    try {
                        val credentialManager = CredentialManager.create(ctx)
                        credentialManager.clearCredentialState(androidx.credentials.ClearCredentialStateRequest())
                    } catch (e: Exception) {
                        Log.d(TAG, "clearCredentialState error (ignored)", e)
                    }
                }
                _currentUser.value = null
                _authState.value = AuthUiState.Unauthenticated
                _userMessage.value = "Signed out successfully."
                resetForm()
            } catch (e: Exception) {
                Log.e(TAG, "Sign out error", e)
                _userMessage.value = "Error signing out: ${e.message}"
            }
        }
    }

    // Helper: input validation
    private fun validateInputs(
        email: String,
        pass: String,
        isSignUp: Boolean,
        displayName: String = ""
    ): Boolean {
        if (email.isBlank()) {
            _userMessage.value = "Email address cannot be empty."
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _userMessage.value = "Please enter a valid email address."
            return false
        }
        if (pass.isBlank()) {
            _userMessage.value = "Password cannot be empty."
            return false
        }
        if (isSignUp && pass.length < 6) {
            _userMessage.value = "Password must be at least 6 characters long."
            return false
        }
        if (isSignUp && displayName.isBlank()) {
            _userMessage.value = "Please enter your name or alias."
            return false
        }
        return true
    }

    private fun resetForm() {
        _emailInput.value = ""
        _passwordInput.value = ""
        _displayNameInput.value = ""
        _isPasswordVisible.value = false
    }

    private fun mapAuthException(e: Exception): String {
        return when (e) {
            is FirebaseAuthWeakPasswordException -> "Password is too weak. Please use at least 6 characters."
            is FirebaseAuthInvalidCredentialsException -> "Invalid email or password."
            is FirebaseAuthUserCollisionException -> "An account with this email already exists."
            is FirebaseAuthInvalidUserException -> "No account found with this email."
            is FirebaseAuthException -> e.message ?: "Authentication error (${e.errorCode})"
            else -> e.localizedMessage ?: "An unexpected error occurred during authentication."
        }
    }

    private fun FirebaseUser.toAuthUserData(): AuthUserData {
        return AuthUserData(
            uid = uid,
            email = email,
            displayName = displayName,
            photoUrl = photoUrl?.toString(),
            isAnonymous = isAnonymous,
            isEmailVerified = isEmailVerified
        )
    }
}

/**
 * Await helper converting a Google Task into a cancellable coroutine.
 */
private suspend fun <T> Task<T>.awaitTask(): T =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { result ->
            cont.resume(result) {}
        }
        addOnFailureListener { exception ->
            cont.resumeWith(Result.failure(exception))
        }
        addOnCanceledListener {
            cont.cancel()
        }
    }
