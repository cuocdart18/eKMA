package com.example.kmatool.firebase

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

val firestore by lazy { Firebase.firestore }

val storage by lazy { Firebase.storage("gs://kma-tool.appspot.com/").reference }

val httpsStorageRef = Firebase.storage