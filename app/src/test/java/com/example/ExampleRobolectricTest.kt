package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("RideNear", appName)
  }

  @Test
  fun `generate valid sample audio with header`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val helper = com.example.data.gemini.AudioRecorderHelper(context)
    val bytes = helper.createSampleVoicePcmAudio()
    org.junit.Assert.assertTrue(bytes.isNotEmpty())
    assertEquals('R'.code.toByte(), bytes[0])
    assertEquals('I'.code.toByte(), bytes[1])
    assertEquals('F'.code.toByte(), bytes[2])
    assertEquals('F'.code.toByte(), bytes[3])
  }

  @Test
  fun `userAuthViewModel initializes state safely`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val authViewModel = com.example.ui.viewmodel.UserAuthViewModel(app)
    authViewModel.setEmail("user@example.com")
    authViewModel.setPassword("securePassword123")
    authViewModel.setDisplayName("Alex Rider")
    assertEquals("user@example.com", authViewModel.emailInput.value)
    assertEquals("securePassword123", authViewModel.passwordInput.value)
    assertEquals("Alex Rider", authViewModel.displayNameInput.value)

    authViewModel.toggleAuthMode()
    assertEquals(true, authViewModel.isSignUpMode.value)

    authViewModel.togglePasswordVisibility()
    assertEquals(true, authViewModel.isPasswordVisible.value)
  }

  @Test
  fun `loginScreen auth state transitions to error when credentials empty`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val authViewModel = com.example.ui.viewmodel.UserAuthViewModel(app)
    authViewModel.signInWithEmailAndPassword("", "")
    assertEquals(true, authViewModel.userMessage.value?.contains("empty", ignoreCase = true))
  }

  @Test
  fun `signInWithGoogleAccount authenticates user and sets verified badge`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val authViewModel = com.example.ui.viewmodel.UserAuthViewModel(app)
    authViewModel.signInWithGoogleAccount("Howshikpeddi@gmail.com", "Howshik Peddi")
    
    val user = authViewModel.currentUser.value
    org.junit.Assert.assertNotNull(user)
    assertEquals("Howshikpeddi@gmail.com", user?.email)
    assertEquals("Howshik Peddi", user?.displayName)
    assertEquals(true, user?.isEmailVerified)
    assertEquals(false, user?.isAnonymous)
    assertEquals(true, authViewModel.authState.value is com.example.ui.viewmodel.AuthUiState.Authenticated)
  }
}
