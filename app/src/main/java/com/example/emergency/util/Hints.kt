package com.example.emergency.util

import android.content.Context
import com.example.emergency.R


class Hints constructor(
    context: Context
) {
    // hints
    val inputHints = listOf(
        context.getString(R.string.info_add_real_name_hint),
        context.getString(R.string.info_add_sex_hint),
//            context.getString(R.string.info_relationship),
        context.getString(R.string.info_add_birth_hint),
        context.getString(R.string.info_add_phone_hint),
        context.getString(R.string.info_add_weight_hint),
        context.getString(R.string.info_add_blood_type_hint),
        context.getString(R.string.info_add_medical_conditions_hint),
        context.getString(R.string.info_add_medical_notes_hint),
        context.getString(R.string.info_add_allergy_hint),
        context.getString(R.string.info_add_medications_hint),
        context.getString(R.string.info_add_address_hint)
    )

    // spinner selection
    val spinnerList = listOf(
        listOf("男", "女"),
        listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"),
        listOf(
            "家人",
            "母亲",
            "父亲",
            "父母",
            "兄弟",
            "姐妹",
            "儿子",
            "女儿",
            "子女",
            "朋友",
            "配偶",
            "伴侣",
            "助理",
            "上司",
            "医生",
            "紧急联系人",
            "家庭成员",
            "老师",
            "看护",
            "监护人",
            "社会工作者",
            "学校",
            "托儿所"
        )
    )
}