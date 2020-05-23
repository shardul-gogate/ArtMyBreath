package com.artmybreath

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_events.*

class EventsActivity : Activity(), AdapterView.OnItemClickListener {

	private lateinit var eventAdapter: EventListAdapter
	private lateinit var eventList: ListView
	private val events = arrayListOf<Event>()

	private lateinit var loadingAlertDialog: AlertDialog

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_events)

		createLoadingAlert()

		eventList = findViewById(R.id.eventList)

		getAllEvents()

		addEventActionButton.setOnClickListener {
			Intent(this,AddEventActivity::class.java).also { startActivity(it) }
			finish()
		}
	}

	override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}

	private fun getAllEvents() {
		showLoadingAlert()

		EVENT_COLLECTION_REFERENCE.get().addOnSuccessListener {
			for (eventReference in it) {
				val eventTitle = eventReference[EVENT_TITLE] as String
				val isBookable = eventReference[EVENT_BOOKABLE] as Boolean
				val eventVenue = eventReference[EVENT_VENUE] as String
				val eventCreatorID = eventReference[EVENT_CREATOR] as String
				var eventCreatorName = "ArtMyBreathUser"
				val eventDescription = eventReference[EVENT_DESCRIPTION] as String
				val eventDay = (eventReference[EVENT_DAY] as Long).toInt()
				val eventMonthInInt = (eventReference[EVENT_MONTH] as Long).toInt()
				val eventYear = (eventReference[EVENT_YEAR] as Long).toInt()
				val eventHour = (eventReference[EVENT_HOUR] as Long).toInt()
				val eventMinute = (eventReference[EVENT_MINUTE] as Long).toInt()
				val eventMonth = when (eventMonthInInt) {
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

				USER_COLLECTION_REFERENCE.document(eventCreatorID).get()
					.addOnSuccessListener { userFound ->
						eventCreatorName = (userFound[FIRST_NAME] as String) + (userFound[LAST_NAME] as String)

						val newEvent = Event(
							eventTitle,
							eventVenue,
							isBookable,
							eventCreatorID,
							eventCreatorName,
							eventDay,
							eventMonth,
							eventYear,
							eventHour,
							eventMinute,
							eventDescription
						)

						events.add(newEvent)
						eventAdapter = EventListAdapter(this, events)
						eventList.adapter = eventAdapter
						eventList.isClickable = true
						eventList.onItemClickListener = this
					}.addOnFailureListener {
						hideLoadingAlert()
						Toast.makeText(this, "Error loading events", Toast.LENGTH_SHORT).show()
						finish()
					}
			}
			hideLoadingAlert()
		}.addOnFailureListener {
			hideLoadingAlert()
			Toast.makeText(this, "Error loading events", Toast.LENGTH_SHORT).show()
			finish()
		}
	}

	private fun createLoadingAlert() {
		loadingAlertDialog = AlertDialog.Builder(this).create()
		val alertLayout: View = layoutInflater.inflate(R.layout.layout_loadingalert, null)
		loadingAlertDialog.setTitle("")
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
