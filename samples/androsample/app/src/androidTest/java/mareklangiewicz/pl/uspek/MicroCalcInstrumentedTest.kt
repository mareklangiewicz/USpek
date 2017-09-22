package mareklangiewicz.pl.uspek

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import mareklangiewicz.pl.uspek.USpek.o
import mareklangiewicz.pl.uspek.USpek.uspek
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.startsWith
import org.junit.runner.RunWith

@RunWith(USpekJUnitRunner::class)
class MicroCalcInstrumentedTest {

    init {
        Check_different_activity_user_interactions_with_uspek()
    }

    fun Check_different_activity_user_interactions_with_uspek() {

        val activityRule = ActivityTestRule(MainActivity::class.java, false, false)

        uspek("check different user interaction scenarios with uspek") {


            "on main activity" o {

                try {
                    activityRule.finishActivity()
                } catch (e: IllegalStateException) {
                }

                activityRule.launchActivity(null)

                "it should display some hello message" o {
                    onView(withId(R.id.helloTextView)).check(matches(withText(startsWith("Hello"))))
                }

                "on click on hello text two times" o {

                    onView(withId(R.id.helloTextView)).perform(click())
                    onView(withId(R.id.helloTextView)).perform(click())

                    "it should display 9 at the end of hello text" o {
                        onView(withId(R.id.helloTextView)).check(matches(withText(endsWith("9"))))
                    }
                }

                "on click on hello text four times" o {

                    repeat(4) { onView(withId(R.id.helloTextView)).perform(click()) }

                    "it should display 81 at the end of hello text" o {
                        onView(withId(R.id.helloTextView)).check(matches(withText(endsWith("81"))))
                    }
                }
            }
        }
    }
}
