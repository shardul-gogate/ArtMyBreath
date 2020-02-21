package com.artmybreath

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_createquiz.*

class CreateQuizActivity : AppCompatActivity() {

	private var whiteColor: Int=0
	private var blackColor: Int=0

	private lateinit var quizTitle: String
	private lateinit var questionTitle: String
	private lateinit var answerOne: String
	private lateinit var answerTwo: String
	private lateinit var answerThree: String
	private lateinit var answerFour: String

	private var correctAnswer: Int=0
	private var questionCount: Int=0
	private lateinit var quizCollection: CollectionReference
	private lateinit var quizReference: DocumentReference
	private lateinit var questionCollection: CollectionReference

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_createquiz)

		whiteColor=ContextCompat.getColor(this,R.color.white)
		blackColor=ContextCompat.getColor(this,R.color.black)

		quizCollection=firebaseFirestore.collection(QUIZ_COLLECTION)

		quizTitleVisibility()

		setListeners()
	}

	private fun saveCurrentQuestion() {
		questionCount+=1
		questionTitle=quizQuestionTitle.text.toString()
		answerOne=choiceOneField.text.toString()
		answerTwo=choiceTwoField.text.toString()
		answerThree=choiceThreeField.text.toString()
		answerFour=choiceFourField.text.toString()

		if(questionTitle.isEmpty() || answerOne.isEmpty() || answerTwo.isEmpty() || answerThree.isEmpty() || answerFour.isEmpty()) {
			Snackbar.make(createQuizLayout,"Some field are empty",Snackbar.LENGTH_LONG).show()
			return
		}

		quizReference.update(QUESTION_COUNT,questionCount)

		val questionMap: HashMap<String,Any> = hashMapOf(
			QUESTION_TITLE to questionTitle,
			ANSWER_ONE to answerOne,
			ANSWER_TWO to answerTwo,
			ANSWER_THREE to answerThree,
			ANSWER_FOUR to answerFour,
			CORRECT_ANSWER to correctAnswer
		)

		questionCollection.document("$QUESTION$questionCount").set(questionMap).addOnFailureListener{
			Log.d("questionFailure",it.toString())
		}
	}

	private fun clearFields() {
		quizQuestionTitle.setText("")
		choiceOneField.setText("")
		choiceTwoField.setText("")
		choiceThreeField.setText("")
		choiceFourField.setText("")
	}

	private fun createQuizInstance() {
		quizTitle=quizTitleField.text.toString()

		if(quizTitle.isEmpty()) {
			Snackbar.make(createQuizLayout,"Quiz title not entered",Snackbar.LENGTH_LONG).show()
			return
		}

		val userIDForQuiz: String= firebaseAuth.currentUser!!.uid

		quizReference=quizCollection.document()
		quizReference.set(
			hashMapOf(
				QUIZ_TITLE to quizTitle,
				QUESTION_COUNT to questionCount,
				QUIZ_BY to userIDForQuiz
			)
		).addOnFailureListener {
			Log.d("quizCreationFailure",it.toString())
		}
		questionCollection=quizReference.collection(QUESTIONS_COLLECTION)
	}

	fun onAnswerSelected(view: View) {
		resetButtonColors()

		operationsButtonLinear.visibility=View.VISIBLE

		val clickedButton=view as Button
		clickedButton.setBackgroundColor(blackColor)
		clickedButton.setTextColor(whiteColor)

		correctAnswer=Integer.valueOf(clickedButton.text.toString())
	}

	private fun setListeners() {
		startButton.setOnClickListener {
			quizFormVisibility()
			createQuizInstance()
		}

		nextButton.setOnClickListener {
			operationsButtonLinear.visibility=View.GONE

			saveCurrentQuestion()
			clearFields()
			resetButtonColors()
		}

		finishButton.setOnClickListener {
			saveCurrentQuestion()
			clearFields()
			resetButtonColors()
			saveAndExitQuiz() }
	}

	private fun quizFormVisibility() {
		quizTitleLinear.visibility= View.GONE
		quizFormLinear.visibility=View.VISIBLE
		choiceButtonLinear.visibility=View.VISIBLE
	}

	private fun quizTitleVisibility() {
		quizTitleLinear.visibility= View.VISIBLE
		quizFormLinear.visibility=View.GONE
		choiceButtonLinear.visibility=View.GONE
		operationsButtonLinear.visibility=View.GONE
	}

	private fun resetButtonColors() {
		correctAnsOne.setBackgroundColor(whiteColor)
		correctAnsOne.setTextColor(blackColor)

		correctAnsTwo.setBackgroundColor(whiteColor)
		correctAnsTwo.setTextColor(blackColor)

		correctAnsThree.setBackgroundColor(whiteColor)
		correctAnsThree.setTextColor(blackColor)

		correctAnsFour.setBackgroundColor(whiteColor)
		correctAnsFour.setTextColor(blackColor)
	}

	private fun saveAndExitQuiz() {
		val exitAlert: AlertDialog.Builder= AlertDialog.Builder(this)
		exitAlert.setTitle("Confirm exit")
		exitAlert.setMessage("Save and exit quiz creation?")
		exitAlert.setPositiveButton("Yes"){ _,_ ->
			Toast.makeText(this,"Quiz saved",Toast.LENGTH_SHORT).show()
			finish()
		}
		exitAlert.setNegativeButton("No") { _,_ ->
			clearFields()
		}
		exitAlert.show()
	}

	override fun onBackPressed() {
		val exitAlert: AlertDialog.Builder= AlertDialog.Builder(this)
		exitAlert.setTitle("Confirm exit")
		exitAlert.setMessage("Do you surely want to quit? This will erase the quiz")
		exitAlert.setPositiveButton("Yes"){ _,_ ->
			finish()
		}
		exitAlert.setNegativeButton("No") { _,_ -> }
		exitAlert.show()
	}
}