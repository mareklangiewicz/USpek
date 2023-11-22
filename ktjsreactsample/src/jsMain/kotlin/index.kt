package pl.mareklangiewicz.ktsample

import App
import react.create
import react.dom.client.createRoot
import web.dom.document
import web.window.window

fun main() {
    window.onload = {
        val container = document.getElementById("root") ?: error("Couldn't find root container!")
        createRoot(container).render(App.create())
    }
}
