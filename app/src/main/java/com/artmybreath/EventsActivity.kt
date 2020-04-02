package com.artmybreath

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AlertDialog

class EventsActivity : Activity(), AdapterView.OnItemClickListener {

	private lateinit var eventAdapter: EventListAdapter
	private lateinit var eventList: ListView
	private val events = arrayListOf<Event>()

	private lateinit var loadingAlertDialog: AlertDialog

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_events)

		eventList = findViewById(R.id.eventList)

		getAllEvents()
	}

	override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}

	private fun getAllEvents() {
		showLoadingAlert()
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
