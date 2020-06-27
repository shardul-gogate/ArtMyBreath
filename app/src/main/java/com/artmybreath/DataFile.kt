package com.artmybreath

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Quiz(val quizTitle: String, val questionCount: Int, val quizBy: String, val quizID: String)

class Question(
	val questionTitle: String,
	val answerOne: String,
	val answerTwo: String,
	val answerThree: String,
	val answerFour: String,
	val correctAnswer: Int
)

class User(
	private var firstName: String,
	private var lastName: String,
	private var userID: String,
	private var emailID: String,
	private var phoneNumber: String
) {

	fun updateUser(firstName: String, lastName: String, phoneNumber: String) {
		this.firstName = firstName
		this.lastName = lastName
		this.phoneNumber = phoneNumber
	}

	fun getFirstName(): String = firstName

	fun getLastName(): String = lastName

	fun getEmail(): String = emailID

	fun getPhone(): String = phoneNumber

	fun getUserID(): String = userID
}

class Portfolio(
	val category: String,
	val subCategory: String,
	val description: String,
	val portfolioID: String
)

class Event(
	val eventID: String,
	val eventTitle: String,
	val eventVenue: String,
	val isBookable: Boolean,
	val eventCreatorID: String,
	val eventCreatorName: String,
	val eventDay: Int,
	val eventMonth: String,
	val eventYear: Int,
	val eventHour: Int,
	val eventMinute: Int,
	val eventDescription: String
)

lateinit var currUser: User

val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

lateinit var selectedQuiz: Quiz

const val FIRST_NAME: String = "firstName"

const val LAST_NAME: String = "lastName"

const val EMAIL: String = "email"

const val PHONE_NUMBER: String = "phoneNumber"

const val QUIZ_TITLE: String = "quizTitle"

const val QUIZ_BY: String = "quizBy"

const val QUESTION: String = "question"

const val QUESTION_TITLE: String = "questionTitle"

const val ANSWER_ONE: String = "answerOne"

const val ANSWER_TWO: String = "answerTwo"

const val ANSWER_THREE: String = "answerThree"

const val ANSWER_FOUR: String = "answerFour"

const val CORRECT_ANSWER: String = "correctAns"

const val QUESTION_COUNT: String = "questionCount"

const val QUIZ_COLLECTION: String = "quiz"

const val QUESTIONS_COLLECTION: String = "questions"

const val USER_COLLECTION: String = "users"

const val PORTFOLIO_COLLECTION: String = "portfolios"

const val EVENT_COLLECTION: String = "events"

const val CATEGORY: String = "category"

const val SUB_CATEGORY: String = "subCategory"

const val DESCRIPTION: String = "description"

const val PORTFOLIO_OF: String = "portfolioOf"

const val EVENT_TITLE: String = "eventTitle"

const val EVENT_VENUE: String = "eventVenue"

const val EVENT_DAY: String = "eventDay"

const val EVENT_MONTH: String = "eventMonth"

const val EVENT_YEAR: String = "eventYEAR"

const val EVENT_HOUR: String = "eventHour"

const val EVENT_MINUTE: String = "eventMinute"

const val EVENT_BOOKABLE: String = "isBookable"

const val EVENT_CREATOR: String = "eventCreator"

const val EVENT_DESCRIPTION: String = "eventDescription"

const val USER_REQUESTED: String = "userRequested"

val QUIZ_COLLECTION_REFERENCE: CollectionReference = firebaseFirestore.collection(QUIZ_COLLECTION)

val USER_COLLECTION_REFERENCE: CollectionReference = firebaseFirestore.collection(USER_COLLECTION)

val PORTFOLIO_COLLECTION_REFERENCE: CollectionReference =
	firebaseFirestore.collection(PORTFOLIO_COLLECTION)

val EVENT_COLLECTION_REFERENCE: CollectionReference = firebaseFirestore.collection(EVENT_COLLECTION)

val imageStorageReference: StorageReference = FirebaseStorage.getInstance().getReference("images")