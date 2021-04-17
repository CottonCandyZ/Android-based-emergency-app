package com.example.emergency.model

class STATUS {
    enum class SignUp {
        INIT, CAN_ENTER_CODE, SEND_CODE_SUCCESS, AFTER_ENTER_CODE, OLD_UER_LOGIN, NEW_USER, SIGN_UP_ERROR, SAVE_USER_SUCCESS, ERROR,
    }


    enum class Login {
        SUCCESS, ERROR
    }
}