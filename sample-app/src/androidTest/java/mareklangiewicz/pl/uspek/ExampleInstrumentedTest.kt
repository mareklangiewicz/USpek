package mareklangiewicz.pl.uspek

import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @JvmField
    @Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = getTargetContext()
        assertEquals("mareklangiewicz.pl.uspek", appContext.packageName)
    }

    @Test
    fun Display_hello_text_view() {
        onView(withId(R.id.helloTextView)).check(matches(isDisplayed()))
    }

    @Test
    fun Check_different_activity_user_interactions_with_uspek() {

        uspek("check different user interaction scenarios with uspek") {
            TODO()
        }
    }
}
