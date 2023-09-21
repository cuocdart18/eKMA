package com.app.ekma.common

// Constants
const val AUTH_MESSAGE_SUCCESS = "Login successfully"
const val AUTH_MESSAGE_FAILED = "Sai ten dang nhap hoac mat khau"

const val PERIOD_TYPE = 0
const val NOTE_TYPE = 1

const val ADD_NOTE_MODE = 101
const val UPDATE_NOTE_MODE = 102

const val EVENTS_NOTIFY_CHANNEL = "Events"
const val EVENTS_NOTIFY_CHANNEL_ID = "events_id"
const val UPDATE_SCHE_CHANNEL = "Update schedule"
const val GET_SCHE_CHANNEL = "Get schedule"
const val UPDATE_SCHE_CHANNEL_ID = "update_sche_id"
const val GET_SCHE_CHANNEL_ID = "get_sche_id"
const val KEY_EVENT = "sent_event_from_AM_to_BR"
const val EVENTS_NOTIFY_ID = 1001
const val UPDATE_SCHEDULE_ID = 872002
const val GET_SCHEDULE_ID = 1812003

// Message type
const val TEXT_MSG = 1
const val IMAGE_MSG = 2
const val GIF_MSG = 3
const val VIDEO_MSG = 4

// for Recording
const val START_RECORDING = 1001
const val PAUSE_RECORDING = 1002
const val RESUME_RECORDING = 1003
const val STOP_RECORDING = 1004

// for Playing
const val START_PLAYING = 1005
const val PAUSE_PLAYING = 1006
const val RESUME_PLAYING = 1007
const val STOP_PLAYING = 1008

// for WorkManager
const val UPDATE_SCHEDULE_WORKER_TAG = "update_schedule_tag"
const val GET_SCHEDULE_WORKER_TAG = "get_schedule_tag"
const val UNIQUE_UPDATE_SCHEDULE_WORK_NAME = "update_schedule"
const val UNIQUE_GET_SCHEDULE_WORK_NAME = "get_schedule"

// for data Passing
const val KEY_PASS_IMAGE_URL = "image_url"
const val KEY_PASS_CHAT_ROOM_ID = "chat_room_id"
const val KEY_PASS_MINISTUDENT_ID = "ministudent_id"
const val KEY_PASS_IS_MY_MINISTUDENT_ID = "my_ministudent_id"
const val KEY_PASS_NOTE_OBJ = "note_obj"
const val KEY_PASS_NOTE_MODE = "note_mode"
const val KEY_PASS_VOICE_AUDIO_PATH = "voice_audio_path"
const val KEY_PASS_STATISTIC_SUBJECT = "statistic_subject"

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
const val PENDING_INVITE_TIME = 30000L
