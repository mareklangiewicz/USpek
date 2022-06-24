import playground.playground
import react.RBuilder
import react.RComponent
import react.Props
import react.State
import react.dom.div
import react.dom.h2

@JsExport
class App : RComponent<Props, State>() {

    override fun RBuilder.render() {
        div("App-header") {
            h2 { +"Welcome to μSpek Playground" }
        }
        playground()
    }
}

fun RBuilder.app() = child(App::class) {}
