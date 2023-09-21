package com.app.ekma.firebase

// for Firestore
const val KEY_USERS_COLL = "users"
const val KEY_ROOMS_COLL = "rooms"
const val KEY_ROOM_ID = "id"
const val KEY_ROOM_MEMBERS = "members"
const val KEY_ROOM_IS_HIDDEN = "isHidden"
const val KEY_ROOM_MESSAGE_COLL = "messages"
const val KEY_MESSAGE_TIMESTAMP_DOC = "timestamp"
const val KEY_MESSAGE_CONTENT_DOC = "content"
const val KEY_MESSAGE_FROM_DOC = "from"
const val KEY_MESSAGE_TYPE_DOC = "type"
const val KEY_USER_ID = "studentCode"
const val KEY_USER_DOB = "birthday"
const val KEY_USER_NAME = "displayName"
const val KEY_USER_GENDER = "gender"
const val KEY_USER_TOKEN = "token"

// for Storage
const val NOT_EXIST_OBJECT_CODE = -13010
const val USERS_DIR = "users"
const val AVATAR_FILE = "avatar"
const val ROOMS_DIR = "rooms"

const val DOCUMENT_CHANGE_ADDED = 900
const val DOCUMENT_CHANGE_MODIFIED = 901
const val DOCUMENT_CHANGE_REMOVED = 902

// for FCM
const val MSG_INVITER_CODE = "inviterCode"
const val MSG_RECEIVER_CODE = "receiverCode"
const val MSG_INVITER_TOKEN = "inviterToken"

const val MSG_OPERATION = "operation"
const val MSG_INVITE = "invite"
const val MSG_CANCEL = "cancel"
const val MSG_SEND_CHANNEL_TOKEN = "sendChannelToken"
const val MSG_RESPONSE_OPERATION = "responseOperation"
const val MSG_ACCEPT = "accept"
const val MSG_REJECT = "reject"

const val MSG_TYPE = "type"
const val MSG_AUDIO_CALL_TYPE = "audioCallType"
const val MSG_VIDEO_CALL_TYPE = "videoCallType"

const val MSG_DATA = "data"