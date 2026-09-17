package pl.mareklangiewicz.uspek.sample.compose

import android.util.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.*
import androidx.compose.ui.unit.*
import java.lang.Thread.*
import kotlin.test.*
import org.junit.*
import org.junit.runner.*
import pl.mareklangiewicz.uspek.*

// I use custom runner here and it seems to work, but consider changing default runner as experimental/dangerous.
// See more info in comments in andro tests in DepsKt/template-andro
// On the other hand it's very nice that this USpekJUnit4Runner runs every leaf in uspek tree as separate JUnit test.
// Thanks to that I can have multiple rule.setContent {..} in uspek tree in separate branches.
@Suppress("DEPRECATION")
@RunWith(USpekJUnit4Runner::class)
class SomeComposeUSpek {
  init {
    uspekLog = {
      if (it.failed) Log.e("uspek", it.status)
      else Log.w("uspek", it.status)
    }
  }

  @get:Rule
  val rule = createComposeRule()

  // @Test
  // fun simpleTest() = assertEquals(2, 2)
  //
  // @Test
  // fun simpleFailingTest() = assertEquals(3, 4)

  @USpekTestTree(13)
  fun layoutUSpek() = with(rule) {

    "On simple box content" o {
      setContent {
        Box {
          Text("First simple box")
        }
      }
    }

    "On second nothing test" o {
      setContent {
        Box(Modifier.background(Color.Blue)) {
          Text("Second simple box")
        }
      }
      assertEquals(4, 4)
    }

    "On third nothing test" o {
      assertEquals(5, 5)
      "On inner UI test" o {
        setContent {
          Box(Modifier.background(Color.Cyan)) {
            Text("Third inner box")
          }
        }
        "wait a bit with content and fail" ox {
          sleep(1000)
          fail()
        }
        "wait a bit again with content and finish" ox {
          sleep(1000)
        }
      }
    }

    "On some animated stuff" o {
      rule.mainClock.autoAdvance = false

      var numberTarget by mutableStateOf(100)

      "On content with animated number" o {
        setContent {
          AnimatedStuff(numberTarget)
        }

        "box width is 200 dp" o { onNodeWithTag("mybox").assertWidthIsEqualTo(200.dp) }


        "On setting numberTarget to 200" o {
          numberTarget = 200

          "box width is still 200 dp" o { onNodeWithTag("mybox").assertWidthIsEqualTo(200.dp) }

          "On 40ms in" o {
            mainClock.advanceTimeBy(40)

            "box width has increased a bit" o { onNodeWithTag("mybox").assertWidthIsAtLeast(205.dp) }

            "On more than full second in" o {
              mainClock.advanceTimeBy(1000)

              "box width is 300" o { onNodeWithTag("mybox").assertWidthIsEqualTo(300.dp) }
            }
          }
        }
      }
    }
  }
}
