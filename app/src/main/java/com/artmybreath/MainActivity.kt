package com.artmybreath

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import java.lang.Thread.sleep
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		splashScreen()
	}

	private fun splashScreen() {
		val splash=thread(start=false) {
			sleep(2000)
			if(firebaseAuth.currentUser==null)
				Intent(this,LoginActivity::class.java).also { startActivity(it) }
			else
				Intent(this,HomeScreen::class.java).also { startActivity(it) }
			finish()
		}
		splash.start()
	}
}
