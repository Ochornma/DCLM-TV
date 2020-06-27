package org.dclm.live.browser

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_doctrine.*
import org.dclm.live.R

class DoctrineActivity : FragmentActivity() {
    private var name:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctrine)
        //number = intent.getIntExtra("position", 0)
        name = intent.getStringExtra("doctrine")
       // Toast.makeText(this, name, Toast.LENGTH_LONG).show()

    }

    override fun onStart() {
        super.onStart()
        val heading1 = applicationContext.resources.getStringArray(R.array.heading)
        val content1 = applicationContext.resources.getStringArray(R.array.doctrine)
        val body1 = applicationContext.resources.getStringArray(R.array.body)
        val number: Int? = name?.let { getNumber(it) }
        heading.text = heading1[number!!]
        content.text = content1[number]
        body.text = body1[number]

    }

    private fun getNumber(name: String): Int {
        val heading1 = applicationContext.resources.getStringArray(R.array.heading)

        for (i in 0 until 23){
            if (name == heading1[i]){
                return i
            }
        }
        return 0
    }
}