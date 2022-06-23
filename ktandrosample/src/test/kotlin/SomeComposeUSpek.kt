package pl.mareklangiewicz.uspek.sample.compose

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

    @Test
    fun simpleTest() = assertEquals(2, 2)
    @Test
    fun simpleFailingTest() = assertEquals(3, 4)

    @USpekTestTree(6)
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
            sleep(1800)
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
                "wait a bit with content and fail" o {
                    sleep(1000)
                    fail()
                }
                "wait a bit again with content and finish" o {
                    sleep(1000)
                }
            }
        }
    }
}
