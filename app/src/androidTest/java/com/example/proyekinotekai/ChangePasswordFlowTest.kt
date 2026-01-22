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
class ChangePasswordFlowTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LandingPage::class.java)

    @Test
    fun testChangePasswordFlow() {
        val email = "tester_baru@gmail.com"
        val oldPass = "rahasia123"
        val newPass = "passwordBaru123"

        // --- 1. LOGIN AWAL ---
        onView(withId(R.id.btnLogin)).perform(click())
        onView(withId(R.id.etEmail)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText(oldPass), closeSoftKeyboard())
        onView(withId(R.id.btnActionLogin)).perform(click())

        Thread.sleep(5000)

        // --- 2. NAVIGASI KE SETTINGS -> DIALOG GANTI PASSWORD ---
        onView(withId(R.id.ivSettings)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.btnChangePassword)).perform(click())
        Thread.sleep(2000)

        // --- 3. SKENARIO: PASSWORD LAMA SALAH ---
        onView(withId(R.id.etOldPassword)).perform(typeText("salahPasswordLama"), closeSoftKeyboard())
        onView(withId(R.id.etNewPassword)).perform(typeText(newPass), closeSoftKeyboard())
        onView(withId(R.id.etConfirmPassword)).perform(typeText(newPass), closeSoftKeyboard())
        onView(withId(R.id.btnSubmitChangePassword)).perform(click())
        
        Thread.sleep(2000)
        // Verifikasi error pada TextInputLayout Password Lama
        onView(withId(R.id.tilOldPassword)).check(matches(hasDescendant(withText("Password lama salah"))))

        // --- 4. SKENARIO: PASSWORD BARU TIDAK COCOK (MISMATCH) ---
        onView(withId(R.id.etOldPassword)).perform(clearText(), typeText(oldPass), closeSoftKeyboard())
        onView(withId(R.id.etConfirmPassword)).perform(clearText(), typeText("tidakCocok123"), closeSoftKeyboard())
        onView(withId(R.id.btnSubmitChangePassword)).perform(click())

        Thread.sleep(1000)
        // Verifikasi error pada TextInputLayout Konfirmasi Password
        onView(withId(R.id.tilConfirmPassword)).check(matches(hasDescendant(withText("Password tidak cocok"))))

        // --- 5. SKENARIO: BERHASIL GANTI PASSWORD ---
        onView(withId(R.id.etConfirmPassword)).perform(clearText(), typeText(newPass), closeSoftKeyboard())
        onView(withId(R.id.btnSubmitChangePassword)).perform(click())

        // Berhasil ganti password akan otomatis logout (berdasarkan logika di SettingsActivity)
        Thread.sleep(7000)

        // --- 6. LOGIN ULANG DENGAN PASSWORD BARU ---
        // Aplikasi kembali ke LandingPage
        onView(withId(R.id.btnLogin)).perform(click())
        onView(withId(R.id.etEmail)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText(newPass), closeSoftKeyboard())
        onView(withId(R.id.btnActionLogin)).perform(click())

        Thread.sleep(5000)
        // Verifikasi berhasil masuk dashboard
        onView(withId(R.id.tvUsername)).check(matches(isDisplayed()))
        
        Thread.sleep(5000)
    }
}
