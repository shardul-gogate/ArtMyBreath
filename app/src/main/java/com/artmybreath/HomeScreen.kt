package com.artmybreath

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_homescreen.*

class HomeScreen : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_homescreen)

		setSupportActionBar(homeScreenToolbar)
		val drawerToggle = ActionBarDrawerToggle(this,homeScreenDrawerLayout,homeScreenToolbar,R.string.navigationDrawerOpen,R.string.navigationDrawerClose)
		homeScreenDrawerLayout.addDrawerListener(drawerToggle)
		drawerToggle.isDrawerIndicatorEnabled=true
		drawerToggle.syncState()

		getCurrentUserInfo()

		setListeners()
	}

	private fun getCurrentUserInfo() {
		USER_COLLECTION_REFERENCE.document(firebaseAuth.currentUser!!.uid).get().addOnCompleteListener {
			if(it.isSuccessful) {
				val userDoc=it.result
				if(userDoc!=null) {
					currUser=User(userDoc.get(FIRST_NAME) as String,userDoc.get(LAST_NAME) as String)
					val navigationHeader=homeScreenDrawer.getHeaderView(0)
					navigationHeader.findViewById<LinearLayout>(R.id.navigationDrawerProfile).findViewById<TextView>(R.id.navigationHeaderUsername).text="${currUser.firstName} ${currUser.lastName}"
				}
			}
		}
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.toolbar_menu,menu)
		return true
	}

	override fun onBackPressed() {
		if(homeScreenDrawerLayout.isDrawerOpen(GravityCompat.START))
			homeScreenDrawerLayout.closeDrawers()
		else
			super.onBackPressed()
	}

	override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
		R.id.toolbarLogout -> {
			logout()
			true
		}
		R.id.toolbarSettings -> {
			settings()
			true
		}
		else -> super.onOptionsItemSelected(item)
	}

	private fun setListeners() {
		val navigationView=findViewById<NavigationView>(R.id.homeScreenDrawer)
		val navigationHeader=navigationView.getHeaderView(0)
		val headerProfile=navigationHeader.findViewById<LinearLayout>(R.id.navigationDrawerProfile)
		headerProfile.setOnClickListener {
			homeScreenDrawerLayout.closeDrawers()
			openProfile()
		}

		navigationView.setNavigationItemSelectedListener{
			homeScreenDrawerLayout.closeDrawers()
			when(it.itemId) {
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
		Snackbar.make(homeScreenDrawerLayout,"Profile section coming soon",Snackbar.LENGTH_LONG).show()
	}

	private fun createQuiz() {
		Intent(this,CreateQuizActivity::class.java).also { startActivity(it) }
	}

	private fun playQuiz() {
		Intent(this,QuizListActivity::class.java).also { startActivity(it) }
	}

	private fun settings() {
		Snackbar.make(homeScreenDrawerLayout,"Settings section coming soon",Snackbar.LENGTH_LONG).show()
	}

	private fun logout() {
		firebaseAuth.signOut()
		Intent(this,LoginActivity::class.java).also { startActivity(it) }
		finish()
	}

	private fun artBlog() {
		Snackbar.make(homeScreenDrawerLayout,"Blog section coming soon",Snackbar.LENGTH_LONG).show()
	}

	private fun artEvents() {
		Snackbar.make(homeScreenDrawerLayout,"Events section coming soon",Snackbar.LENGTH_LONG).show()
	}

	private fun artJobs() {
		Snackbar.make(homeScreenDrawerLayout,"Jobs section coming soon",Snackbar.LENGTH_LONG).show()
	}
}
