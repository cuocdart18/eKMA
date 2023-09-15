package com.app.ekma.common

class GetPeriodFailingException(message: String) : RuntimeException(message) {
    override val message: String = ""
        get() = "Get periods failure with semester code = $field"
}

class GetSemesterCodesFailingException(message: String) : RuntimeException(message) {
    override val message: String = ""
        get() = "Get semester codes failure with $field"
}