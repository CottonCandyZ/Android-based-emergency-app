package com.example.emergency.model

class STATE {
    enum class SignUp {
        INIT, CAN_ENTER_CODE, SEND_CODE_SUCCESS, AFTER_ENTER_CODE, OLD_UER_LOGIN,
        NEW_USER, SIGN_UP_ERROR, SAVE_USER_SUCCESS, ERROR,
    }

    enum class Login {
        SUCCESS, ERROR
    }

    enum class MyPage {
        USER_NOT_FOUND, REFRESH_COMPLETE, REFRESH_ERROR, CHOSEN_ERROR
    }

    enum class Info {
        SHOW, EDIT, NEW, SAVE_SUCCESS, SAVE_ERROR, DELETE_SUCCESS, DELETE_ERROR
    }

    enum class Call {
        INIT, CALLING, CANCEL, COMPLETE, ERROR
    }

    enum class History {
        REFRESH_COMPLETE, REFRESH_ERROR
    }
}