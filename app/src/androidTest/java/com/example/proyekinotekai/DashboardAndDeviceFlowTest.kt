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
class DashboardAndDeviceFlowTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LandingPage::class.java)

    @Test
    fun testDashboardScrollAndDeviceListShowcase() {
        // --- 1. PROSES LOGIN ---
        onView(withId(R.id.btnLogin)).perform(click())

        onView(withId(R.id.etEmail)).perform(typeText("tester_baru@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText("rahasia123"), closeSoftKeyboard())
        onView(withId(R.id.btnActionLogin)).perform(click())

        // Tunggu transisi ke MainActivity
        Thread.sleep(5000)

        // --- 2. SHOWCASE DASHBOARD (SCROLL 15 DETIK) ---
        // Espresso tidak punya fungsi 'durasi scroll', jadi kita simulasikan scroll beberapa kali dengan jeda
        val startTime = System.currentTimeMillis()
        val duration = 15000 // 15 detik
        
        while (System.currentTimeMillis() - startTime < duration) {
            onView(withId(android.R.id.content)).perform(swipeUp()) // Scroll ke bawah
            Thread.sleep(2000)
            onView(withId(android.R.id.content)).perform(swipeDown()) // Scroll ke atas
            Thread.sleep(2000)
        }

        // --- 3. NAVIGASI KE DEVICE LIST ---
        onView(withId(R.id.btnMachine)).perform(click())

        // --- 4. SHOWCASE DEVICE LIST (10 DETIK) ---
        // Verifikasi elemen di DeviceListActivity (misal card pakan feeder)
        onView(withId(R.id.cardFeeder1)).check(matches(isDisplayed()))
        
        // Diam selama 10 detik agar user bisa melihat isi Device List
        Thread.sleep(10000)
        
        // Kembali ke Home
        onView(withId(R.id.homeContainer)).perform(click())
        
        // Selesai
        Thread.sleep(2000)
    }
}
