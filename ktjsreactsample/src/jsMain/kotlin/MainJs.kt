package pl.mareklangiewicz.ktsample

import react.create
import react.dom.client.createRoot
import web.dom.document
import web.events.EventHandler
import web.window.window

// https://github.com/JetBrains/kotlin-wrappers/blob/master/docs/guide/react.md

fun main() {
  window.onload = EventHandler {
    val id = "rootOfKtJsReactSample"
    val container = document.getElementById(id) ?: error("Not found: $id")
    createRoot(container).render(App.create())
  }
}
