package pl.mareklangiewicz.uspek

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val calc = AndroCalc(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        helloTextView.append("\n" + calc.result)
        helloTextView.setOnClickListener {
            calc.multiplyBy(3)
            helloTextView.append("\n" + calc.result)
        }
    }
}
