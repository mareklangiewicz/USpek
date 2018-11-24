package pl.mareklangiewicz.uspek

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.startsWith
import org.junit.runner.RunWith
import pl.mareklangiewicz.uspek.USpek.o
import pl.mareklangiewicz.uspek.USpek.uspek

@RunWith(USpekJUnitRunner::class)
class AndroCalcInstrumentedTest {

    init {
        val activityRule = ActivityTestRule<MainActivity>(MainActivity::class.java, false, false)
        uspek("andro tests") {

            "on main activity" o {

                try {
                    activityRule.finishActivity()
                } catch (e: IllegalStateException) { }

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
