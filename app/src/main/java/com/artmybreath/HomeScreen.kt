package com.artmybreath

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_homescreen.*

class HomeScreen : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_homescreen)

		setListeners()
	}

	private fun setListeners() {
		quizLogoutButton.setOnClickListener{ logout() }
		playQuizButton.setOnClickListener { playQuiz() }
		createQuizButton.setOnClickListener { createQuiz() }
	}

	private fun createQuiz() {
		Intent(this,CreateQuizActivity::class.java).also { startActivity(it) }
	}

	private fun playQuiz() {
		Intent(this,QuizListActivity::class.java).also { startActivity(it) }
	}

	fun logout() {
		firebaseAuth.signOut()
		Intent(this,LoginActivity::class.java).also { startActivity(it) }
		finish()
	}
}
