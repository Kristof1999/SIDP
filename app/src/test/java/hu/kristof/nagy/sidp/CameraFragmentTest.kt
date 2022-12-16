package hu.kristof.nagy.sidp

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CameraFragmentTest {
    @Test
    fun checkDisplay() {
        val scenario = launchFragmentInContainer<CameraFragment>()

        onView(withId(R.id.test))
            .check(matches(isDisplayed()))

        scenario.close()
    }
}