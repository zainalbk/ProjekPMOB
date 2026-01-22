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
class LoginFlowTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LandingPage::class.java)

    @Test
    fun testLogin_EmptyFields() {
        // Navigasi ke halaman Login dari Landing Page
        onView(withId(R.id.btnLogin)).perform(click())

        // Klik tombol Login tanpa isi apapun
        onView(withId(R.id.btnActionLogin)).perform(click())

        // Verifikasi error pada TextInputLayout
        onView(withId(R.id.tilEmail)).check(matches(hasDescendant(withText("Harap masukkan email dengan benar"))))
        onView(withId(R.id.tilPassword)).check(matches(hasDescendant(withText("Harap masukkan sandi dengan benar"))))
    }

    @Test
    fun testLogin_InvalidEmailFormat() {
        onView(withId(R.id.btnLogin)).perform(click())

        // Masukkan email format salah
        onView(withId(R.id.etEmail)).perform(typeText("bukan_email"), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText("rahasia123"), closeSoftKeyboard())

        onView(withId(R.id.btnActionLogin)).perform(click())

        // Verifikasi error format email
        onView(withId(R.id.tilEmail)).check(matches(hasDescendant(withText("Format email tidak valid"))))
    }

    @Test
    fun testLogin_WrongCredentials() {
        onView(withId(R.id.btnLogin)).perform(click())

        // Masukkan email/password yang tidak terdaftar/salah
        onView(withId(R.id.etEmail)).perform(typeText("salah@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText("salahpassword"), closeSoftKeyboard())

        onView(withId(R.id.btnActionLogin)).perform(click())

        // Tunggu proses asinkron Firebase
        Thread.sleep(2000)

        // Verifikasi error login gagal
        onView(withId(R.id.tilPassword)).check(matches(hasDescendant(withText("Email atau sandi salah, harap cek kembali"))))
    }

    @Test
    fun testLogin_Success() {
        onView(withId(R.id.btnLogin)).perform(click())

        // Masukkan kredensial valid
        onView(withId(R.id.etEmail)).perform(typeText("tester_baru@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText("rahasia123"), closeSoftKeyboard())

        onView(withId(R.id.btnActionLogin)).perform(click())
        Thread.sleep(4000)
    }
}
