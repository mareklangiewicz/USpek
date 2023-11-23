package pl.mareklangiewicz.ktsample

import react.create
import react.dom.client.createRoot
import web.dom.document
import web.window.window

// https://github.com/JetBrains/kotlin-wrappers/blob/master/docs/guide/react.md

fun main() {
    window.onload = {
        val id = "rootOfKtJsReactSample"
        val container = document.getElementById(id) ?: error("Not found: $id")
        createRoot(container).render(App.create())
    }
}
