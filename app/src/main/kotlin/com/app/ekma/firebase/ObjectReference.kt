package com.app.ekma.firebase

import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

val database by lazy { Firebase.database("https://ekma-c517e-default-rtdb.asia-southeast1.firebasedatabase.app/").reference }

lateinit var connectedRefListener: ValueEventListener

val firestore by lazy { Firebase.firestore }

val storage by lazy { Firebase.storage("gs://ekma-c517e.appspot.com/").reference }

val httpsStorageRef = Firebase.storage