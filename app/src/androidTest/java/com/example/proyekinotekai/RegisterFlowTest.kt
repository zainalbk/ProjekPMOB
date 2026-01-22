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
class RegisterFlowTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LandingPage::class.java)

    @Test
    fun testRegister_InvalidEmailFormat() {
        // Navigasi ke halaman Register dari Landing Page
        onView(withId(R.id.btnGetStarted)).perform(click())

        // Masukkan format email yang salah
        onView(withId(R.id.etRegEmail)).perform(typeText("testing_bukan_email"), closeSoftKeyboard())
        onView(withId(R.id.etRegPass)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.etRegConfirm)).perform(typeText("password123"), closeSoftKeyboard())

        onView(withId(R.id.btnNext)).perform(click())

    }

    @Test
    fun testRegister_PasswordMismatch() {
        onView(withId(R.id.btnGetStarted)).perform(click())

        // Masukkan password yang tidak cocok
        onView(withId(R.id.etRegEmail)).perform(typeText("user@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.etRegPass)).perform(typeText("securePass123"), closeSoftKeyboard())
        onView(withId(R.id.etRegConfirm)).perform(typeText("bedaPass456"), closeSoftKeyboard())

        onView(withId(R.id.btnNext)).perform(click())
    }

    @Test
    fun testRegister_EmptyFields() {
        onView(withId(R.id.btnGetStarted)).perform(click())

        // Langsung klik Next tanpa mengisi apapun
        onView(withId(R.id.btnNext)).perform(click())

        // Verifikasi deteksi field kosong (berdasarkan logika RegisterActivity)
        onView(withId(R.id.tilRegPass)).check(matches(hasDescendant(withText("Email tidak boleh kosong"))))
    }

    @Test
    fun testFullRegisterAndBioFlow() {
        onView(withId(R.id.btnGetStarted)).perform(click())

        // 1. Isi Data Akun Valid
        onView(withId(R.id.etRegEmail)).perform(typeText("tester_baru@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.etRegPass)).perform(typeText("rahasia123"), closeSoftKeyboard())
        onView(withId(R.id.etRegConfirm)).perform(typeText("rahasia123"), closeSoftKeyboard())

        onView(withId(R.id.btnNext)).perform(click())

        // 2. Verifikasi Berpindah ke BioFragment
        onView(withId(R.id.tvTitleBio)).check(matches(isDisplayed()))

        // 3. Isi Data Biodata
        onView(withId(R.id.etNama)).perform(typeText("Robot Penguji"), closeSoftKeyboard())
        onView(withId(R.id.etTelp)).perform(typeText("081299887766"), closeSoftKeyboard())
        onView(withId(R.id.etAlamat)).perform(typeText("Jl. Teknologi No. 404"), closeSoftKeyboard())

        // 4. Klik Finish/Register
        onView(withId(R.id.btnRegisterFinal)).perform(click())
        
        // Catatan: Tes ini berhenti di sini karena langkah selanjutnya melibatkan Firebase asli.
    }
}
