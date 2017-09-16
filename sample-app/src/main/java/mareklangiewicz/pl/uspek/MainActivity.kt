package mareklangiewicz.pl.uspek

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val example = Example(20)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        helloTextView.append("\n" + example.addSome(5))
        helloTextView.setOnClickListener {
            helloTextView.append("\n" + example.multiplySome(5))
            example.some += 1
        }
    }
}
