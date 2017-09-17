package mareklangiewicz.pl.uspek

import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import mareklangiewicz.pl.uspek.USpek.o
import mareklangiewicz.pl.uspek.USpek.uspek
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.startsWith
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @JvmField
    @Rule
    val activityRule = ActivityTestRule(MainActivity::class.java, false, false)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = getTargetContext()
        assertEquals("mareklangiewicz.pl.uspek", appContext.packageName)
    }

    @Test
    fun Display_hello_text_view() {
        activityRule.launchActivity(null)
        onView(withId(R.id.helloTextView)).check(matches(isDisplayed()))
        activityRule.finishActivity()
    }

    @Test
    fun Check_different_activity_user_interactions_with_uspek() {

        USpek.log = { Log.w("USpek", it) }

        uspek("check different user interaction scenarios with uspek", rethrow = true) {

            "on main activity" o {

                try {
                    activityRule.finishActivity()
                }
                catch (e: IllegalStateException) {}

                activityRule.launchActivity(null)

                "check if there is some hello message" o {
                    onView(withId(R.id.helloTextView)).check(matches(withText(startsWith("Hello"))))
                }

                "click twice on hello text" o {

                    onView(withId(R.id.helloTextView)).perform(click())
                    onView(withId(R.id.helloTextView)).perform(click())

                    "check for number 105 at the end of hello text" o {
                        onView(withId(R.id.helloTextView)).check(matches(withText(endsWith("105"))))
                    }
                }

                "click fout times on hello text" o {

                    repeat(4) { onView(withId(R.id.helloTextView)).perform(click()) }

                    "check for number 115 at the end of hello text" o {
                        onView(withId(R.id.helloTextView)).check(matches(withText(endsWith("115"))))
                    }
                }
            }
        }
    }
}
