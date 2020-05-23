package com.artmybreath

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : Activity(), AdapterView.OnItemSelectedListener,
	AdapterView.OnItemClickListener {

	private var searchRequestCode: Int = -1

	private val searchResultIDs: ArrayList<String> = arrayListOf()
	private val searchResultNames: ArrayList<String> = arrayListOf()
	private lateinit var searchListAdapter: ArrayAdapter<String>

	private lateinit var loadingAlertDialog: AlertDialog

	private val categories = arrayListOf("Sponsor", "Artist", "Teacher")
	private val sponsorSubCategories = arrayListOf("Capillary", "Financial")
	private val artistSubCategories = arrayListOf(
		"Classical dancer",
		"Pop dancer",
		"Painter",
		"Classical singer",
		"Pop singer",
		"Guitarist",
		"Sound engineer",
		"Drummer",
		"Keyboardist",
		"Flutist",
		"Tabla",
		"Harmonium",
		"Stage actor",
		"Screen actor",
		"Lighting engineer",
		"Photographer"
	)
	private val teacherSubCategories = arrayListOf(
		"Classical dance",
		"Pop dance",
		"Painting",
		"Classical singing",
		"Pop singing",
		"Guitar",
		"Sound engineering",
		"Drumming",
		"Keyboard",
		"Flute",
		"Tabla",
		"Harmonium",
		"Stage acting",
		"Screen acting",
		"Light engineering",
		"Photography"
	)

	private lateinit var artistSpinnerAdapter: ArrayAdapter<String>
	private lateinit var teacherSpinnerAdapter: ArrayAdapter<String>
	private lateinit var sponsorSpinnerAdapter: ArrayAdapter<String>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_search)

		setVisibility()

		setListeners()

		setUpSpinnerAdapters()

		createLoadingAlert()
	}

	private fun searchPortfolioByName() {
		val searchFirstName = searchFirstName.text.toString()
		val searchLastName = searchLastName.text.toString()

		showLoadingAlert()

		USER_COLLECTION_REFERENCE.whereEqualTo(FIRST_NAME, searchFirstName)
			.whereEqualTo(LAST_NAME, searchLastName).get().addOnSuccessListener {
				hideLoadingAlert()
				if(it.isEmpty) {
					Toast.makeText(this,"No matching results found",Toast.LENGTH_SHORT).show()
					return@addOnSuccessListener
				}
				for (userFound in it) {
					searchResultIDs.add(userFound.id)
					searchResultNames.add("$searchFirstName $searchLastName")
				}
				searchListAdapter = ArrayAdapter<String>(
					this,
					android.R.layout.simple_list_item_1,
					searchResultNames
				)
				searchResultsList.adapter = searchListAdapter
			}.addOnFailureListener {
				Toast.makeText(this,"Something went wrong. Try later",Toast.LENGTH_SHORT).show()
				hideLoadingAlert()
			}
	}

	private fun searchPortfolioByCategory() {
		val searchCategory = searchCategorySpinner.selectedItem as String
		val searchSubCategory = searchSubCategorySpinner.selectedItem as String

		showLoadingAlert()

		PORTFOLIO_COLLECTION_REFERENCE.whereEqualTo(CATEGORY, searchCategory)
			.whereEqualTo(SUB_CATEGORY, searchSubCategory).get().addOnSuccessListener {
				hideLoadingAlert()
				if(it.isEmpty) {
					Toast.makeText(this,"No matching results found",Toast.LENGTH_SHORT).show()
					return@addOnSuccessListener
				}
				for (userFound in it) {
					val userID = userFound[PORTFOLIO_OF] as String
					searchResultIDs.add(userID)
					USER_COLLECTION_REFERENCE.document(userID).get()
						.addOnSuccessListener { userFound ->
							val firstName = userFound[FIRST_NAME] as String
							val lastName = userFound[LAST_NAME] as String
							searchResultNames.add("$firstName $lastName")
							searchListAdapter = ArrayAdapter<String>(
								this,
								android.R.layout.simple_list_item_1,
								searchResultNames
							)
							searchResultsList.adapter = searchListAdapter
							searchResultsList.onItemClickListener = this
						}
				}
			}.addOnFailureListener {
				Toast.makeText(this,"Something went wrong. Try later",Toast.LENGTH_SHORT).show()
				hideLoadingAlert()
			}
	}

	override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
		searchSubCategorySpinner.adapter = when (position) {
			0 -> sponsorSpinnerAdapter
			2 -> teacherSpinnerAdapter
			else -> artistSpinnerAdapter
		}
	}

	override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
		showLoadingAlert()
		val userID = searchResultIDs[position]
		USER_COLLECTION_REFERENCE.document(userID).get().addOnSuccessListener {
			hideLoadingAlert()
			val firstName = it[FIRST_NAME] as String
			val lastName = it[LAST_NAME] as String
			val email = it[EMAIL] as String
			val phoneNumber = it[PHONE_NUMBER] as String
			val searchResultDialog = AlertDialog.Builder(this)
			searchResultDialog.setTitle("$firstName $lastName")
			searchResultDialog.setMessage("$email\n$phoneNumber")
			searchResultDialog.setNeutralButton("Ok") { _, _ -> }
			searchResultDialog.show()
		}.addOnFailureListener {
			Toast.makeText(this,"Something went wrong. Try later",Toast.LENGTH_SHORT).show()
			hideLoadingAlert()
		}
	}

	private fun setListeners() {
		portfolioRadio.setOnClickListener {
			searchByPortfolio.visibility = View.VISIBLE
			searchButton.isEnabled = true
		}

		artEventsRadio.setOnClickListener {
			searchByPortfolio.visibility = View.GONE
			searchSpinnerConstraint.visibility = View.GONE
			memberNameConstraint.visibility = View.GONE
			searchButton.isEnabled = false
		}

		memberNameRadio.setOnClickListener {
			memberNameConstraint.visibility = View.VISIBLE
			searchSpinnerConstraint.visibility = View.GONE
			searchRequestCode = 1
		}

		categoryRadio.setOnClickListener {
			memberNameConstraint.visibility = View.GONE
			searchSpinnerConstraint.visibility = View.VISIBLE
			searchRequestCode = 2
		}

		searchCategorySpinner.onItemSelectedListener = this

		searchButton.setOnClickListener {
			searchResultIDs.clear()
			searchResultNames.clear()
			when (searchRequestCode) {
				1 -> searchPortfolioByName()
				2 -> searchPortfolioByCategory()
			}
		}
	}

	override fun onNothingSelected(parent: AdapterView<*>?) {
		Toast.makeText(this,"Select category to search",Toast.LENGTH_SHORT).show()
		return
	}

	private fun setVisibility() {
		searchByPortfolio.visibility = View.GONE
		searchSpinnerConstraint.visibility = View.GONE
		memberNameConstraint.visibility = View.GONE
		searchButton.isEnabled = false
	}

	private fun setUpSpinnerAdapters() {
		val categorySpinnerAdapter =
			ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
		categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		searchCategorySpinner.adapter = categorySpinnerAdapter

		artistSpinnerAdapter =
			ArrayAdapter(this, android.R.layout.simple_spinner_item, artistSubCategories)
		artistSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

		teacherSpinnerAdapter =
			ArrayAdapter(this, android.R.layout.simple_spinner_item, teacherSubCategories)
		teacherSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

		sponsorSpinnerAdapter =
			ArrayAdapter(this, android.R.layout.simple_spinner_item, sponsorSubCategories)
		sponsorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
	}

	private fun createLoadingAlert() {
		loadingAlertDialog = AlertDialog.Builder(this).create()
		loadingAlertDialog.setTitle("")
		val alertLayout: View = layoutInflater.inflate(R.layout.layout_loadingalert, null)
		loadingAlertDialog.setView(alertLayout)
		loadingAlertDialog.setCancelable(false)
	}

	private fun showLoadingAlert() {
		loadingAlertDialog.show()
	}

	private fun hideLoadingAlert() {
		loadingAlertDialog.dismiss()
	}
}
