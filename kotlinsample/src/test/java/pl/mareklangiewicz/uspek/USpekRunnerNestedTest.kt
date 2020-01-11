package pl.mareklangiewicz.uspek

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@Ignore // TODO: check why the USpekRunner fails - in command line too: ./gradlew clean test --info
@RunWith(USpekRunner::class)
class USpekRunnerNestedTest {

    @Test
    fun uspekNestedTestsFailingWriteXmlOutput() = uspek {
        "On blabla blalba blab al" o {
            "On source asFlow with buffer capacity 1" o {
                "On collect flow" o {
                    "On first source item" o {
                        "On second source item during first emission" o {
                            "dsfd xads f fdf djsal dajkfl afa sjkf" /*if this test name is short: then it works*/ o {
                                "f" o {}
                            }
                        }
                    }
                }
            }
        }
    }
}
