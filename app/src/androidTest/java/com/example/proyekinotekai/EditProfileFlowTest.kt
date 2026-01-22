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
class EditProfileFlowTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LandingPage::class.java)

    @Test
    fun testEditProfileFlow_Success() {
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

        // --- 3. MASUK KE EDIT PROFILE ---
        onView(withId(R.id.btnEditProfile)).perform(click())
        Thread.sleep(2000)

        // --- 4. UBAH DATA PROFIL ---
        // Scroll ke atas/bawah jika perlu, lalu isi data baru
        onView(withId(R.id.etName)).perform(scrollTo(), clearText(), typeText("Robot Tester Updated"), closeSoftKeyboard())
        onView(withId(R.id.etPhone)).perform(scrollTo(), clearText(), typeText("089988776655"), closeSoftKeyboard())
        onView(withId(R.id.etFullAddress)).perform(scrollTo(), clearText(), typeText("Jalan Baru No. 123, Kota Robot"), closeSoftKeyboard())

        // --- 5. SUBMIT PERUBAHAN ---
        onView(withId(R.id.btnSubmit)).perform(scrollTo(), click())

        // Tunggu proses update di Firebase dan kembali ke Settings
        Thread.sleep(5000)

        // Verifikasi apakah kembali ke SettingsActivity (cek tombol Edit Profile muncul lagi)
        onView(withId(R.id.btnEditProfile)).check(matches(isDisplayed()))
        
        // Tampilkan hasil akhir selama 5 detik untuk dokumentasi
        Thread.sleep(5000)
    }
}
