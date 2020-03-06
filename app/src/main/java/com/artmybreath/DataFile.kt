package com.artmybreath

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class Quiz(val quizTitle: String, val questionCount: Int, val quizBy: String, val quizID: String)

class Question (val questionTitle: String, val answerOne: String, val answerTwo: String, val answerThree: String, val answerFour: String, val correctAnswer: Int)

class User(val firstName: String, val lastName: String)

lateinit var currUser: User

val firebaseAuth: FirebaseAuth=FirebaseAuth.getInstance()

val firebaseFirestore: FirebaseFirestore=FirebaseFirestore.getInstance()

lateinit var selectedQuiz: Quiz

const val FIRST_NAME: String="firstName"

const val LAST_NAME: String="lastName"

const val EMAIL: String="email"

const val PHONE_NUMBER: String="phoneNumber"

const val QUIZ_TITLE: String="quizTitle"

const val QUIZ_BY: String="quizBy"

const val QUESTION: String="question"

const val QUESTION_TITLE: String="questionTitle"

const val ANSWER_ONE: String="answerOne"

const val ANSWER_TWO: String="answerTwo"

const val ANSWER_THREE: String="answerThree"

const val ANSWER_FOUR: String="answerFour"

const val CORRECT_ANSWER: String="correctAns"

const val QUESTION_COUNT: String="questionCount"

const val QUIZ_COLLECTION: String="quiz"

const val QUESTIONS_COLLECTION: String="questions"

const val USER_COLLECTION: String="users"

val QUIZ_COLLECTION_REFERENCE: CollectionReference=firebaseFirestore.collection(QUIZ_COLLECTION)

val USER_COLLECTION_REFERENCE: CollectionReference=firebaseFirestore.collection(USER_COLLECTION)