package com.artmybreath

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_homescreen.*

class HomeScreen : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_homescreen)

		setSupportActionBar(homeScreenToolbar)
		val drawerToggle = ActionBarDrawerToggle(
			this,
			homeScreenDrawerLayout,
			homeScreenToolbar,
			R.string.navigationDrawerOpen,
			R.string.navigationDrawerClose
		)
		homeScreenDrawerLayout.addDrawerListener(drawerToggle)
		drawerToggle.isDrawerIndicatorEnabled = true
		drawerToggle.syncState()

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
						val navigationHeader = homeScreenDrawer.getHeaderView(0)
						navigationHeader.findViewById<LinearLayout>(R.id.navigationDrawerProfile)
							.findViewById<TextView>(R.id.navigationHeaderUsername).text =
							"${currUser.getFirstName()} ${currUser.getLastName()}"
					}
				}
			}
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.toolbar_menu, menu)
		return true
	}

	override fun onBackPressed() {
		if (homeScreenDrawerLayout.isDrawerOpen(GravityCompat.START))
			homeScreenDrawerLayout.closeDrawers()
		else
			logout()
	}

	override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
		R.id.toolbarLogout -> {
			logout()
			true
		}

		R.id.toolbarSearch -> {
			search()
			true
		}

		/*
		R.id.toolbarSettings -> {
			settings()
			true
		}
		*/

		else -> super.onOptionsItemSelected(item)
	}

	private fun setListeners() {
		val navigationView = findViewById<NavigationView>(R.id.homeScreenDrawer)
		val navigationHeader = navigationView.getHeaderView(0)
		val headerProfile =
			navigationHeader.findViewById<LinearLayout>(R.id.navigationDrawerProfile)
		headerProfile.setOnClickListener {
			homeScreenDrawerLayout.closeDrawers()
			openProfile()
		}

		navigationView.setNavigationItemSelectedListener {
			homeScreenDrawerLayout.closeDrawers()
			when (it.itemId) {
				R.id.playQuizNavigation -> {
					playQuiz()
					true
				}
				R.id.createQuizNavigation -> {
					createQuiz()
					true
				}
				R.id.artBlogNavigation -> {
					artBlog()
					true
				}
				R.id.artEventsNavigation -> {
					artEvents()
					true
				}
				R.id.artJobsNavigation -> {
					artJobs()
					true
				}
				else -> true
			}
		}
	}

	private fun openProfile() {
		if (firebaseAuth.currentUser!!.isAnonymous) {
			Snackbar.make(
				homeScreenDrawerLayout,
				"Cannot open profile when anonymous",
				Snackbar.LENGTH_LONG
			).show()
			return
		}
		Intent(this, ProfileActivity::class.java).also { startActivity(it) }
	}

	private fun createQuiz() {
		if (firebaseAuth.currentUser!!.isAnonymous) {
			Snackbar.make(
				homeScreenDrawerLayout,
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

	/*
	private fun settings() {
		Snackbar.make(homeScreenDrawerLayout, "Settings section coming soon", Snackbar.LENGTH_LONG)
			.show()
	}
	*/

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

	private fun artBlog() {
		Snackbar.make(homeScreenDrawerLayout, "Blog section coming soon", Snackbar.LENGTH_LONG)
			.show()
	}

	private fun artEvents() {
		//Snackbar.make(homeScreenDrawerLayout, "Events section coming soon", Snackbar.LENGTH_LONG)
		//	.show()
		Intent(this,AddEventActivity::class.java).also { startActivity(it) }
	}

	private fun artJobs() {
		Snackbar.make(homeScreenDrawerLayout, "Jobs section coming soon", Snackbar.LENGTH_LONG)
			.show()
	}

	private fun search() {
		if (firebaseAuth.currentUser!!.isAnonymous) {
			Snackbar.make(
				homeScreenDrawerLayout,
				"Cannot search database when anonymous",
				Snackbar.LENGTH_LONG
			).show()
			return
		}
		Intent(this, SearchActivity::class.java).also { startActivity(it) }
	}
}
