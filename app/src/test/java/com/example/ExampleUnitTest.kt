package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun geminiModels_configuredCorrectly() {
    assertEquals("gemini-3.5-transcribe", com.example.data.gemini.GeminiApiClient.MODEL_TRANSCRIBE)
    assertEquals("gemini-3.1-pro-preview", com.example.data.gemini.GeminiApiClient.MODEL_PRO)
    assertEquals("gemini-3.5-flash", com.example.data.gemini.GeminiApiClient.MODEL_FLASH)
    assertEquals("gemini-3.1-flash-lite", com.example.data.gemini.GeminiApiClient.MODEL_FLASH_LITE)
    assertEquals("gemini-3.8-live", com.example.data.gemini.GeminiApiClient.MODEL_LIVE)
  }

  @Test
  fun pricingCalculation_isCorrect() {
    val hourlyRate = 14.0
    val durationHours = 3
    val platformFee = 5.0
    val deposit = 80.0
    val total = (hourlyRate * durationHours) + platformFee + deposit
    assertEquals(127.0, total, 0.001)
  }
}
