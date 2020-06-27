package com.artmybreath

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
		val searchFirstName = searchFirstNameField.text.toString()
		val searchLastName = searchLastNameField.text.toString()

		if (searchFirstName.isEmpty() && searchLastName.isEmpty()) {
			Toast.makeText(this, "Enter details to search", Toast.LENGTH_SHORT).show()
		}

		var searchFullName =
			"${searchFirstName.toLowerCase()} ${searchLastName.toLowerCase()}".trim()

		showLoadingAlert()

		USER_COLLECTION_REFERENCE.get().addOnSuccessListener {
			hideLoadingAlert()
			if (it.isEmpty) {
				Toast.makeText(this, "No matching results found", Toast.LENGTH_SHORT).show()
				searchListAdapter = ArrayAdapter(
					this,
					android.R.layout.simple_list_item_1,
					searchResultNames
				)
				searchResultsList.adapter = searchListAdapter
				searchResultsList.onItemClickListener = this
				return@addOnSuccessListener
			}

			for (userFound in it) {
				val userFirstName = userFound[FIRST_NAME] as String
				val userLastName = userFound[LAST_NAME] as String
				val userFullName =
					"${userFirstName.toLowerCase()} ${userLastName.toLowerCase()}".trim()

				if (searchFirstName.isEmpty() && userFullName.contains(searchLastName, true)) {
					searchResultNames.add("$userFirstName $userLastName")
					searchResultIDs.add(userFound.id)
				} else if (searchLastName.isEmpty() && userFullName.contains(
						searchFirstName,
						true
					)
				) {
					searchResultNames.add("$userFirstName $userLastName")
					searchResultIDs.add(userFound.id)
				} else if (searchFullName == userFullName) {
					searchResultNames.add("$userFirstName $userLastName")
					searchResultIDs.add(userFound.id)
				}
			}

			searchListAdapter = ArrayAdapter(
				this,
				android.R.layout.simple_list_item_1,
				searchResultNames
			)
			searchResultsList.adapter = searchListAdapter
			searchResultsList.onItemClickListener = this
		}.addOnFailureListener {
			Toast.makeText(this, "Something went wrong. Try later", Toast.LENGTH_SHORT).show()
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
				if (it.isEmpty) {
					Toast.makeText(this, "No matching results found", Toast.LENGTH_SHORT).show()
					searchListAdapter = ArrayAdapter(
						this,
						android.R.layout.simple_list_item_1,
						searchResultNames
					)
					searchResultsList.adapter = searchListAdapter
					searchResultsList.onItemClickListener = this
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
							searchListAdapter = ArrayAdapter(
								this,
								android.R.layout.simple_list_item_1,
								searchResultNames
							)
							searchResultsList.adapter = searchListAdapter
							searchResultsList.onItemClickListener = this
						}
				}
			}.addOnFailureListener {
				Toast.makeText(this, "Something went wrong. Try later", Toast.LENGTH_SHORT).show()
				hideLoadingAlert()
			}
	}

	private fun searchEventByTitle() {
		showLoadingAlert()
		val searchEventTitleEntered: String = searchEventTitle.text.toString().toLowerCase()
		EVENT_COLLECTION_REFERENCE.get()
			.addOnSuccessListener {
				hideLoadingAlert()
				if (it.isEmpty) {
					Toast.makeText(this, "No matching results found", Toast.LENGTH_SHORT).show()
					searchListAdapter = ArrayAdapter(
						this,
						android.R.layout.simple_list_item_1,
						searchResultNames
					)
					searchResultsList.adapter = searchListAdapter
					searchResultsList.onItemClickListener = this
					return@addOnSuccessListener
				}
				for (eventFound in it) {
					val eventTitle = eventFound[EVENT_TITLE] as String
					if (eventTitle.contains(searchEventTitleEntered, true)) {
						searchResultNames.add(eventTitle)
						searchResultIDs.add(eventFound.id)
					}
				}
				searchListAdapter = ArrayAdapter(
					this,
					android.R.layout.simple_list_item_1,
					searchResultNames
				)
				searchResultsList.adapter = searchListAdapter
				searchResultsList.onItemClickListener = this
			}.addOnFailureListener {
				hideLoadingAlert()
				Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
			}
	}

	private fun searchEventByDate() {
		showLoadingAlert()
		val searchEventDay = searchEventDatePicker.dayOfMonth
		val searchEventMonth = searchEventDatePicker.month + 1
		val searchEventYear = searchEventDatePicker.year
		Log.d("DATE_FOR_SEARCH", "$searchEventDay - $searchEventMonth - $searchEventYear")
		EVENT_COLLECTION_REFERENCE.whereEqualTo(EVENT_DAY, searchEventDay)
			.whereEqualTo(EVENT_MONTH, searchEventMonth).whereEqualTo(
				EVENT_YEAR, searchEventYear
			).get()
			.addOnSuccessListener {
				hideLoadingAlert()
				if (it.isEmpty) {
					Toast.makeText(this, "No matching results found", Toast.LENGTH_SHORT).show()
					searchListAdapter = ArrayAdapter(
						this,
						android.R.layout.simple_list_item_1,
						searchResultNames
					)
					searchResultsList.adapter = searchListAdapter
					searchResultsList.onItemClickListener = this
					return@addOnSuccessListener
				}
				for (eventFound in it) {
					searchResultIDs.add(eventFound.id)
					searchResultNames.add(eventFound[EVENT_TITLE] as String)
				}
				searchListAdapter = ArrayAdapter(
					this,
					android.R.layout.simple_list_item_1,
					searchResultNames
				)
				searchResultsList.adapter = searchListAdapter
				searchResultsList.onItemClickListener = this
			}.addOnFailureListener {
				hideLoadingAlert()
				Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
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
		if (artEventsRadio.isChecked) artEventDetails(position)
		else if (portfolioRadio.isChecked) portfolioDetails(position)
	}

	private fun artEventDetails(position: Int) {
		val eventID = searchResultIDs[position]

		EVENT_COLLECTION_REFERENCE.document(eventID).get().addOnSuccessListener {
			val isBookable = it[EVENT_BOOKABLE] as Boolean
			val eventDescription = it[EVENT_DESCRIPTION] as String
			val eventCreatorID = it[EVENT_CREATOR] as String
			val eventVenue = it[EVENT_VENUE]
			val eventDay = (it[EVENT_DAY] as Long).toInt()
			val eventMonth = when ((it[EVENT_MONTH] as Long).toInt()) {
				1 -> "Jan"
				2 -> "Feb"
				3 -> "Mar"
				4 -> "Apr"
				5 -> "May"
				6 -> "Jun"
				7 -> "Jul"
				8 -> "Aug"
				9 -> "Sep"
				10 -> "Oct"
				11 -> "Nov"
				12 -> "Dec"
				else -> "ERR"
			}
			val eventYear = (it[EVENT_YEAR] as Long).toInt()
			val eventHour = (it[EVENT_HOUR] as Long).toInt()
			val eventMinute = (it[EVENT_MINUTE] as Long).toInt()
			var eventMessage: String = "$eventDescription\n"
			if (!isBookable) {
				eventMessage += "Venue: $eventVenue\nDate: $eventDay - $eventMonth - $eventYear\nTime: $eventHour - $eventMinute\n"
			}

			USER_COLLECTION_REFERENCE.document(eventCreatorID).get()
				.addOnSuccessListener { userFound ->
					val eventCreatorName =
						"${userFound[FIRST_NAME] as String} ${userFound[LAST_NAME] as String}"

					eventMessage += "Created by: $eventCreatorName"

					hideLoadingAlert()

					val eventDetailsDialog = AlertDialog.Builder(this)
					eventDetailsDialog.setTitle(searchResultNames[position])
					eventDetailsDialog.setMessage(eventMessage)
					eventDetailsDialog.setCancelable(false)
					eventDetailsDialog.setNeutralButton("Ok") { _, _ -> }
					eventDetailsDialog.show()
				}
		}
	}

	private fun portfolioDetails(position: Int) {
		val selectedUserID = searchResultIDs[position]
		hideLoadingAlert()
		Intent(this, ProfileActivity::class.java).also {
			it.putExtra(USER_REQUESTED, selectedUserID)
			startActivity(it)
		}
	}

	private fun setListeners() {
		portfolioRadio.setOnClickListener {
			searchByPortfolio.visibility = View.VISIBLE
			searchByEvent.visibility = View.GONE
			searchByEventConstraint.visibility = View.GONE
			searchButton.isEnabled = true
		}

		artEventsRadio.setOnClickListener {
			searchByEvent.visibility = View.VISIBLE
			searchByPortfolio.visibility = View.GONE
			searchSpinnerConstraint.visibility = View.GONE
			memberNameConstraint.visibility = View.GONE
			searchButton.isEnabled = true
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

		eventTitleRadio.setOnClickListener {
			searchByEventConstraint.visibility = View.VISIBLE
			searchEventTitle.visibility = View.VISIBLE
			searchEventDatePicker.visibility = View.GONE
			searchByEventLabel.text = "Event title"
			searchRequestCode = 3
		}

		eventDateRadio.setOnClickListener {
			searchByEventConstraint.visibility = View.VISIBLE
			searchEventTitle.visibility = View.GONE
			searchEventDatePicker.visibility = View.VISIBLE
			searchByEventLabel.text = "Event date"
			searchRequestCode = 4
		}

		searchCategorySpinner.onItemSelectedListener = this

		searchButton.setOnClickListener {
			searchResultIDs.clear()
			searchResultNames.clear()
			when (searchRequestCode) {
				1 -> searchPortfolioByName()
				2 -> searchPortfolioByCategory()
				3 -> searchEventByTitle()
				4 -> searchEventByDate()
			}
		}
	}

	override fun onNothingSelected(parent: AdapterView<*>?) {
		Toast.makeText(this, "Select category to search", Toast.LENGTH_SHORT).show()
		return
	}

	private fun setVisibility() {
		searchByPortfolio.visibility = View.GONE
		searchSpinnerConstraint.visibility = View.GONE
		memberNameConstraint.visibility = View.GONE
		searchByEventConstraint.visibility = View.GONE
		searchButton.isEnabled = false
		searchByEvent.visibility = View.GONE
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
