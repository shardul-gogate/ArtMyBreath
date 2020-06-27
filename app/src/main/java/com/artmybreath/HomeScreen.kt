package com.artmybreath

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_homescreen.*

class HomeScreen : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_homescreen)

		getCurrentUserInfo()

		setListeners()
	}

	private fun getCurrentUserInfo() {
		if (firebaseAuth.currentUser!!.isAnonymous)
			return
		USER_COLLECTION_REFERENCE.document(firebaseAuth.currentUser!!.uid).get()
			.addOnCompleteListener {
				if (it.isSuccessful) {
					val userDoc = it.result
					if (userDoc != null) {
						currUser = User(
							userDoc.get(FIRST_NAME) as String,
							userDoc.get(LAST_NAME) as String,
							firebaseAuth.currentUser!!.uid,
							firebaseAuth.currentUser!!.email.toString(),
							userDoc.get(PHONE_NUMBER) as String
						)
						yourProfileButton.text =
							"${currUser.getFirstName()} ${currUser.getLastName()}"
					}
				}
			}
	}

	override fun onBackPressed() {
		logout()
	}

	private fun setListeners() {
		yourProfileButton.setOnClickListener { openProfile() }

		homeScreenSearchButton.setOnClickListener { search() }

		addEventsButton.setOnClickListener { addEvent() }

		openEventsButton.setOnClickListener { artEvents() }

		playQuizButton.setOnClickListener { playQuiz() }

		createQuizButton.setOnClickListener { createQuiz() }

		logoutButton.setOnClickListener { logout() }
	}

	private fun addEvent() {
		if (firebaseAuth.currentUser!!.isAnonymous) {
			Snackbar.make(
				homeScreenLayout,
				"Cannot open profile when anonymous",
				Snackbar.LENGTH_LONG
			).show()
			return
		}
		Intent(
			this,
			AddEventActivity::class.java
		).also { startActivity(it) }
	}

	private fun openProfile() {
		if (firebaseAuth.currentUser!!.isAnonymous) {
			Snackbar.make(
				homeScreenLayout,
				"Cannot open profile when anonymous",
				Snackbar.LENGTH_LONG
			).show()
			return
		}
		Intent(this, ProfileActivity::class.java).also {
			it.putExtra(USER_REQUESTED, currUser.getUserID())
			startActivity(it)
		}
	}

	private fun createQuiz() {
		if (firebaseAuth.currentUser!!.isAnonymous) {
			Snackbar.make(
				homeScreenLayout,
				"Cannot create quiz when anonymous",
				Snackbar.LENGTH_LONG
			).show()
			return
		}
		Intent(this, CreateQuizActivity::class.java).also { startActivity(it) }
	}

	private fun playQuiz() {
		Intent(this, QuizListActivity::class.java).also { startActivity(it) }
	}

	private fun logout() {
		val logoutAlert: AlertDialog.Builder = AlertDialog.Builder(this)
		logoutAlert.setTitle("Confirm logout")
		logoutAlert.setMessage("Do you want to logout")
		logoutAlert.setPositiveButton("Yes") { _, _ ->
			firebaseAuth.signOut()
			Intent(this, LoginActivity::class.java).also { startActivity(it) }
			finish()
		}
		logoutAlert.setNegativeButton("No") { _, _ -> }
		logoutAlert.show()
	}

	private fun artEvents() {
		Intent(this, EventsActivity::class.java).also { startActivity(it) }
	}

	private fun search() {
		if (firebaseAuth.currentUser!!.isAnonymous) {
			Snackbar.make(
				homeScreenLayout,
				"Cannot search database when anonymous",
				Snackbar.LENGTH_LONG
			).show()
			return
		}
		Intent(this, SearchActivity::class.java).also { startActivity(it) }
	}
}
