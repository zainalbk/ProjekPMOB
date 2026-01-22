package com.example.proyekinotekai

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.proyekinotekai.ui.landing.LandingPage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SettingsNavigationFlowTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LandingPage::class.java)

    @Test
    fun testSettingsNavigationShowcase() {
        // --- 1. LOGIN ---
        onView(withId(R.id.btnLogin)).perform(click())

        onView(withId(R.id.etEmail)).perform(typeText("tester_baru@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText("rahasia123"), closeSoftKeyboard())
        onView(withId(R.id.btnActionLogin)).perform(click())

        // Tunggu transisi ke MainActivity
        Thread.sleep(5000)

        // --- 2. NAVIGASI KE SETTINGS ---
        onView(withId(R.id.ivSettings)).perform(click())
        Thread.sleep(2000)

        // --- 3. SHOWCASE ABOUT ACTIVITY ---
        onView(withId(R.id.btnAboutUs)).perform(click())
        // Beri waktu 5 detik untuk melihat isi AboutActivity
        Thread.sleep(5000)
        onView(withId(R.id.btnBack)).perform(click()) // Kembali ke Settings
        Thread.sleep(1000)

        // --- 4. SHOWCASE SUPPORT ACTIVITY ---
        onView(withId(R.id.btnSupport)).perform(click())
        // Beri waktu 5 detik untuk melihat isi SupportActivity
        Thread.sleep(5000)
        onView(withId(R.id.btnBack)).perform(click()) // Kembali ke Settings
        Thread.sleep(1000)

        // --- 5. SHOWCASE SETTINGS DETAIL ACTIVITY ---
        onView(withId(R.id.btnSettings)).perform(click())
        // Beri waktu 5 detik untuk melihat isi SettingsDetailActivity
        Thread.sleep(5000)
        
        // Verifikasi elemen di SettingsDetailActivity (misal switch notifikasi)
        onView(withId(R.id.switchNotification)).check(matches(isDisplayed()))
        
        onView(withId(R.id.btnBack)).perform(click()) // Kembali ke Settings
        Thread.sleep(1000)
        
        // Verifikasi kembali di SettingsActivity
        onView(withId(R.id.btnEditProfile)).check(matches(isDisplayed()))
    }
}
