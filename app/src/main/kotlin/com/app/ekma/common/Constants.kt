package com.app.ekma.common

// Constants
const val FRAGMENT_TYPE = "frm_type"
const val SCORE_FRAGMENT = 0
const val SCHEDULE_FRAGMENT = 1
const val NOTE_FRAGMENT = 2
const val INFORMATION_FRAGMENT = 3

const val APP_EXTERNAL_MEDIA_FOLDER = "media"
const val EXTERNAL_AUDIO_FOLDER = "audio"

const val AUTH_MESSAGE_SUCCESS = "Login successfully"
const val AUTH_MESSAGE_FAILED = "Sai ten dang nhap hoac mat khau"

const val PERIOD_TYPE = 0
const val NOTE_TYPE = 1

const val ADD_NOTE_MODE = 101
const val UPDATE_NOTE_MODE = 102

// for Notification
const val EVENTS_NOTIFY_CHANNEL = "Events"
const val EVENTS_NOTIFY_CHANNEL_ID = "events_id"
const val UPDATE_SCHE_CHANNEL = "Update schedule"
const val GET_SCHE_CHANNEL = "Get schedule"
const val UPDATE_SCHE_CHANNEL_ID = "update_sche_id"
const val GET_SCHE_CHANNEL_ID = "get_sche_id"
const val KEY_EVENT = "sent_event_from_AM_to_BR"
const val EVENTS_NOTIFY_ID = 1001
const val GET_SCHEDULE_ID = 1812003
const val INCOMING_CALL_NOTIFY_CHANNEL = "Call"
const val INCOMING_CALL_NOTIFY_CHANNEL_ID = "incoming_call_id"
const val INCOMING_CALL_ID = 872002
const val NEW_MSG_NOTIFY_CHANNEL = "Message"
const val NEW_MSG_NOTIFY_CHANNEL_ID = "new_msg_id"

// for Chatting
const val TEXT_MSG = 1
const val IMAGE_MSG = 2
const val GIF_MSG = 3
const val VIDEO_MSG = 4
const val FROM_POSITION = "from"
const val TO_POSITION = "to"
const val NOTIFY_NEW_MSG_TIME = "notifyMsgTime"

// for Calling
const val CALLING_OPERATION = "calling_operation"
const val MUTE_MIC = "mute_mic"
const val MUTE_MIC_REQUEST_CODE = 2000
const val UNMUTE_MIC = "unmute_mic"
const val UNMUTE_MIC_REQUEST_CODE = 2001
const val MUTE_CAMERA = "mute_camera"
const val MUTE_CAMERA_REQUEST_CODE = 2002
const val UNMUTE_CAMERA = "unmute_camera"
const val UNMUTE_CAMERA_REQUEST_CODE = 2003
const val EARPIECE_AUDIO_ROUTE = "earpiece_audio_route"
const val EARPIECE_AUDIO_ROUTE_REQUEST_CODE = 2004
const val SPEAKER_AUDIO_ROUTE = "speaker_audio_route"
const val SPEAKER_AUDIO_ROUTE_REQUEST_CODE = 2005
const val LEAVE_ROOM = "leave_room"
const val LEAVE_ROOM_REQUEST_CODE = 2006
const val HANG_UP = "hang_up"
const val HANG_UP_REQUEST_CODE = 2007

// for Recording
const val START_RECORDING = 1001
const val PAUSE_RECORDING = 1002
const val RESUME_RECORDING = 1003
const val STOP_RECORDING = 1004

// for Call timer
const val START_CALL_TIMER = 1101
const val PAUSE_CALL_TIMER = 1102
const val RESUME_CALL_TIMER = 1103
const val STOP_CALL_TIMER = 1104

// for Playing
const val START_PLAYING = 1005
const val PAUSE_PLAYING = 1006
const val RESUME_PLAYING = 1007
const val STOP_PLAYING = 1008

// for WorkManager
const val INPUT_DATA_STUDENT_CODE = "student_code"
const val INPUT_DATA_IMAGE_URI = "image_uri"
const val INPUT_DATA_AUDIO_NOTE_NAME = "audio_note_name"
const val INPUT_DATA_INVITER_CODE = "inviterCode"
const val INPUT_DATA_RECEIVER_CODES = "receiverCodes"
const val INPUT_DATA_CALL_TYPE = "callType"
const val INPUT_DATA_NEW_MSG = "newMsg"
const val INPUT_DATA_NEW_MSG_NOTIFICATION = "newMsgNotification"

const val UPDATE_SCHEDULE_WORKER_TAG = "update_schedule_tag"
const val GET_SCHEDULE_WORKER_TAG = "get_schedule_tag"
const val UPLOAD_AVATAR_WORKER_TAG = "upload_avatar_tag"
const val UPLOAD_AUDIO_NOTE_WORKER_TAG = "upload_audio_note_tag"
const val DELETE_AUDIO_NOTE_WORKER_TAG = "delete_audio_note_tag"
const val DOWNLOAD_AVATAR_WORKER_TAG = "download_avatar_tag"
const val DOWNLOAD_AUDIO_NOTES_WORKER_TAG = "download_audio_notes_tag"
const val CALL_WORKER_TAG = "call_tag"
const val MESSAGE_WORKER_TAG = "msg_tag"
const val MESSAGE_NOTIFICATION_WORKER_TAG = "msg_notification_tag"
const val INCOMING_CALL_WORKER_TAG = "incoming_call_tag"
const val REJECT_CALL_WORKER_TAG = "reject_call_tag"

const val UNIQUE_UPDATE_SCHEDULE_WORK_NAME = "update_schedule"
const val UNIQUE_GET_SCHEDULE_WORK_NAME = "get_schedule"
const val UNIQUE_UPLOAD_AVATAR_WORK_NAME = "upload_avatar"
const val UNIQUE_UPLOAD_AUDIO_NOTE_WORK_NAME = "upload_audio_note"
const val UNIQUE_DELETE_AUDIO_NOTE_WORK_NAME = "delete_audio_note"
const val UNIQUE_DOWNLOAD_AVATAR_WORK_NAME = "download_avatar"
const val UNIQUE_DOWNLOAD_AUDIO_NOTES_WORK_NAME = "download_audio_notes"
const val INCOMING_CALL_WORKER_NAME = "incoming_call"
const val REJECT_CALL_WORKER_NAME = "reject_call"
const val CANCEL_INVITATION_WORKER_NAME = "cancel_invitation_call"
const val CANCEL_INCOMING_NOTIFICATION_WORKER_NAME = "cancel_incoming_notification_call"
const val NOTIFY_NEW_MSG_WORKER_NAME = "notify_new_msg"
const val SHOW_NEW_MSG_WORKER_NAME = "show_new_msg"

// for data Passing
const val KEY_PASS_IMAGE_URL = "image_url"
const val KEY_PASS_CHAT_ROOM_ID = "chat_room_id"
const val KEY_PASS_MINISTUDENT_ID = "ministudent_id"
const val KEY_PASS_IS_MY_MINISTUDENT_ID = "my_ministudent_id"
const val KEY_PASS_NOTE_OBJ = "note_obj"
const val KEY_PASS_NOTE_MODE = "note_mode"
const val KEY_PASS_VOICE_AUDIO_NAME = "voice_audio_name"
const val KEY_PASS_MY_STUDENT_CODE = "my_student_code"
const val KEY_PASS_STATISTIC_SUBJECT = "statistic_subject"
const val KEY_PASS_IS_IN_PIP = "isInPiP"
const val KEY_PASS_AUTO_JOIN_CALL = "isAutoJoinCall"

const val DATABASE_NAME = "app_database.db"

const val SCALE_LAYOUT_SEARCH_DATA_DIALOG_X = 0.80
const val SCALE_LAYOUT_SEARCH_DATA_DIALOG_Y = 0.60

// for data store
const val NAME_DATASTORE_PREFS = "datastore_prefs"
const val KEY_STUDENT_PROFILE = "student_profile"
const val KEY_IS_LOGIN = "is_login"
const val KEY_IS_NOTIFY_EVENTS = "is_notify_events"
const val KEY_IMG_FILE_PATH = "img_file_path"

// for Agora
const val AGORA_APP_ID = "8d92b5ef9ae8488393dc2021dbfc94b5"
const val RTC_TOKEN_TYPE = "rtc"
const val RTM_TOKEN_TYPE = "rtm"
const val PUBLISHER_ROLE = "publisher"
const val SUBSCRIBER_ROLE = "subscriber"
const val DEFAULT_UID = "0"
const val TOKEN_EXPIRED_TIME = 120
const val CHANNEL_TOKEN = "channelToken"
const val PENDING_RESPONSE_TIME = 35000L
const val PENDING_RESPONSE_TOKEN_TIME = 15000L
const val PENDING_INVITE_TIME = 30000L
