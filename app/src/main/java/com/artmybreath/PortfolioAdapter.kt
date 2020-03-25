package com.artmybreath

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class PortfolioAdapter(private val cont: Context, private val portfolios: ArrayList<Portfolio>) :
	ArrayAdapter<Portfolio>(cont, R.layout.layout_portfoliolist, portfolios) {
	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
		return createItemView(position, parent)
	}

	private fun createItemView(position: Int, parent: ViewGroup): View {
		val inflater: LayoutInflater =
			cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
		val rowView: View = inflater.inflate(R.layout.layout_portfoliolist, parent, false)
		val portfolioCategory: TextView = rowView.findViewById(R.id.portfolioCategory)
		val portfolioSubCategory: TextView = rowView.findViewById(R.id.portfolioSubcategory)
		portfolioCategory.text = portfolios[position].category
		portfolioSubCategory.text = portfolios[position].subCategory
		return rowView
	}
}