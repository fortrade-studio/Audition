package com.atria.myapplication

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    private lateinit var scenario : FragmentScenario<LoginFragment>
    private val errorText = "Field Can't be Empty"

    @Before
    fun setUp(){
        scenario = launchFragmentInContainer(initialState = Lifecycle.State.CREATED,themeResId = R.style.Theme_Audition)
    }

    @Test
    fun checkForEmptyInputs(){
        // we will click the sign up button and then check if error is shown
        onView(withId(R.id.signUpButton)).perform(click())

        onView(withId(R.id.nameEditText)).check(matches(hasErrorText(errorText)))

    }

}