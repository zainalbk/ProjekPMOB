package com.example.proyekinotekai

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.proyekinotekai.ui.landing.LandingPage
import org.hamcrest.Matchers.*
import org.hamcrest.CoreMatchers.`is` as hamcrestIs
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SettingsDetailFlowTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LandingPage::class.java)

    @Test
    fun testSettingsDetailFlow_LanguageTimezoneLogout() {
        // --- 1. LOGIN ---
        onView(withId(R.id.btnLogin)).perform(click())

        onView(withId(R.id.etEmail)).perform(typeText("tester_baru@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText("rahasia123"), closeSoftKeyboard())
        onView(withId(R.id.btnActionLogin)).perform(click())

        // Tunggu masuk MainActivity
        Thread.sleep(5000)

        // --- 2. NAVIGASI KE SETTINGS -> SETTINGS DETAIL ---
        onView(withId(R.id.ivSettings)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.btnSettings)).perform(click())
        Thread.sleep(2000)

        // --- 3. PILIH BAHASA KE ENGLISH ---
        onView(withId(R.id.spinnerLanguage)).perform(click())
        // Perbaikan: Gunakan alias hamcrestIs untuk menghindari konflik kata kunci Kotlin 'is'
        onData(allOf(instanceOf(String::class.java), hamcrestIs("English"))).perform(click())
        
        // Jeda 10 detik sesuai permintaan
        Thread.sleep(10000)

        // --- 4. PILIH ZONA WAKTU JAKARTA ---
        onView(withId(R.id.autoCompleteTimezone)).perform(scrollTo(), replaceText("Jakarta"), closeSoftKeyboard())
        Thread.sleep(2000) // Tunggu filter muncul
        
        // Klik hasil pencarian Jakarta di dropdown (Platform Popup)
        onView(withText(containsString("Jakarta")))
            .inRoot(isPlatformPopup())
            .perform(click())

        // Jeda 10 detik
        Thread.sleep(10000)

        // --- 5. LOGOUT ---
        onView(withId(R.id.btnLogout)).perform(scrollTo(), click())
        Thread.sleep(2000)

        // Klik Konfirmasi Logout di Dialog
        onView(withId(R.id.btnConfirmLogout)).perform(click())

        // Jeda 10 detik terakhir
        Thread.sleep(10000)

        // Verifikasi kembali ke Landing Page
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()))
    }
}
