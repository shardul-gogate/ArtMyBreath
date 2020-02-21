package com.artmybreath

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class QuizListAdapter(private val cont: Context, private val quizzes: ArrayList<Quiz>): ArrayAdapter<Quiz>(cont,R.layout.layout_quizlist,quizzes) {
	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
		return createItemView(position, parent)
	}

	private fun createItemView(position: Int, parent: ViewGroup): View {
		val inflater: LayoutInflater = cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
		val rowView: View =inflater.inflate(R.layout.layout_quizlist,parent,false)
		val quizBy=quizzes[position].quizBy
		val questionCount=quizzes[position].questionCount
		val listQuizTitle: TextView=rowView.findViewById(R.id.listQuizTitle)
		val listQuizByQuestions: TextView=rowView.findViewById(R.id.listQuizByQuestions)
		listQuizTitle.text=quizzes[position].quizTitle
		listQuizByQuestions.text="Author: $quizBy | Q's: $questionCount"
		return rowView
	}
}