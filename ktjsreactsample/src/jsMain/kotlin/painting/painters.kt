package pl.mareklangiewicz.painting

import org.khronos.webgl.get
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.get
import kotlinx.browser.document
import kotlin.math.PI
import kotlin.random.Random
import org.w3c.dom.CanvasRenderingContext2D as Ctx

const val WIDTH = 800.0
const val HEIGHT = 800.0

fun paintSomething() = Kandinsky.paint()

interface Painter {
    val name: String
    fun paintBack()
    fun paintFront()
    fun paint() { paintBack(); paintBack(); paintFront(); paintFront() }
}

object Kandinsky : Painter {
    override val name = "Wassily Kandinsky"
    override fun paintBack() = ctx.run {
        repeat(1 rnd 4) { randomPolyline(1 rnd 3, 200, 300, opacity = 10 rnd 80) }
    }
    override fun paintFront() = ctx.run {
        repeat(1 rnd 4) { randomCurve(6 rnd 10, 20 rnd (20 rnd 100)) }
        repeat(2 rnd 8) { randomCurve(2 rnd 8, 10 rnd (10 rnd 100)) }
        repeat(1 rnd 3) { randomCircle(2 rnd (8 rnd 64)) }
        repeat(1 rnd 4) { randomPolyline(1 rnd 3, 2, 6) }
    }
}

object Pollock : Painter {
    override fun paintBack() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun paintFront() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val name = "Jackson Pollock"

}

object Picasso : Painter {
    override val name = "Pablo Picasso"
    override fun paintBack() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun paintFront() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

private val ctx by lazy {
    val canvas = document.getElementsByTagName("canvas")[0] as HTMLCanvasElement
    canvas.width = WIDTH.toInt()
    canvas.height = HEIGHT.toInt()
    canvas.getContext("2d") as Ctx
}

infix fun Int.rnd(to: Int) = Random.nextInt(this, to + 1)
infix fun Double.rnd(to: Double) = Random.nextDouble(this, to)

val Int.near get() = this - this / 6 rnd this + this / 6
val Double.near get() = this - this / 6 rnd this + this / 6

private val rndx get() = 0.0 rnd WIDTH
private val rndy get() = 0.0 rnd HEIGHT

private fun Ctx.randomPainting() {
    repeat(4) { randomCurve(100, 140) }
    repeat(10) { randomCurve(1, 30) }
    repeat(4) { randomCircle(4, 10) }
    repeat(3) { randomPolyline(4, 4, 10) }
}

fun clearCanvas() = ctx.clearRect(0.0, 0.0, WIDTH, HEIGHT)

private fun Ctx.randomCircle(minWidth: Int, maxWidth: Int = minWidth, minHue: Int = 0, maxHue: Int = 360) = strokePath {
    val radius = 40.0 rnd (100.0 rnd 300.0)
    arc(rndx, rndy, radius, 0.0, 2 * PI)
    lineWidth = (minWidth rnd maxWidth).toDouble()
    strokeStyle = "hsl(${minHue rnd maxHue}, 60%, 50%)"
}

private fun Ctx.randomCurve(minWidth: Int, maxWidth: Int = minWidth, minHue: Int = 0, maxHue: Int = 360) = strokePath {
    moveTo(rndx, rndy)
    bezierCurveTo(rndx, rndy, rndx, rndy, rndx, rndy)
    lineWidth = (minWidth rnd maxWidth).toDouble()
    strokeStyle = "hsl(${minHue rnd maxHue}, 60%, 50%)"
}

private fun Ctx.randomPolyline(
    segments: Int = 1,
    minWidth: Int = 4,
    maxWidth: Int = minWidth,
    minHue: Int = 0,
    maxHue: Int = 360,
    opacity: Int = 100
) = strokePath {
    moveTo(rndx, rndy)
    repeat(segments) { lineTo(rndx, rndy) }
    lineWidth = (minWidth rnd maxWidth).toDouble()
    strokeStyle = "hsl(${minHue rnd maxHue}, 60%, 50%, $opacity%)"
}

private inline fun Ctx.strokePath(block: Ctx.() -> Unit) {
    beginPath()
    block()
    stroke()
}

fun pickColor(x: Int, y: Int): String {
    println(x)
    println(y)
    val imageData = ctx.getImageData(x.toDouble(), y.toDouble(), 1.0, 1.0)
    val data = imageData.data
    return "rgba(${data[0]}, ${data[1]}, ${data[2]}, ${data[3] / 255.0})"
}
