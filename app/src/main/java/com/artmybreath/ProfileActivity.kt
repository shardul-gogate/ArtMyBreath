package com.artmybreath

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : Activity(), AdapterView.OnItemClickListener {

	private lateinit var portfolioAdapter: PortfolioAdapter
	private lateinit var portfolioList: ListView
	private var portfolios: ArrayList<Portfolio> = arrayListOf()

	private lateinit var loadingAlertDialog: AlertDialog

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_profile)

		createLoadingAlert()

		setUserProfile()
		portfolioList=findViewById(R.id.portfoliosList)
		getUserPortfolios()
	}

	override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}

	private fun getUserPortfolios() {
		showLoadingAlert()
		PORTFOLIO_COLLECTION_REFERENCE.whereEqualTo("portfolioOf", currUser.getUserID()).get().addOnSuccessListener{
			for(userPortfolio in it) {
				val category=userPortfolio[CATEGORY] as String
				val subCategory=userPortfolio[SUB_CATEGORY] as String
				val description=userPortfolio[DESCRIPTION] as String
				val newPortfolio=Portfolio(category,subCategory,description)
				portfolios.add(newPortfolio)
			}
			portfolioAdapter= PortfolioAdapter(this,portfolios)
			portfolioList.adapter=portfolioAdapter
			portfolioList.isClickable=true
			portfolioList.onItemClickListener=this
		}
	}

	private fun setUserProfile() {
		profileNameField.text= "${currUser.getFirstName()} ${currUser.getLastName()}"
		profileEmailField.text= currUser.getEmail()
	}

	private fun createLoadingAlert() {
		loadingAlertDialog = AlertDialog.Builder(this).create()
		loadingAlertDialog.setTitle("")
		val alertLayout: View =layoutInflater.inflate(R.layout.layout_loadingalert,null)
		loadingAlertDialog.setView(alertLayout)
	}

	private fun showLoadingAlert() {
		loadingAlertDialog.show()
	}

	private fun hideLoadingAlert() {
		loadingAlertDialog.dismiss()
	}
}
