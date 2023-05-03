import playground.Playground
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h2
import web.cssom.ClassName

val App = FC<Props> {

        div {
            className = ClassName("App-header")
            h2 { +"Welcome to μSpek Playground" }
        }
        Playground()
}

