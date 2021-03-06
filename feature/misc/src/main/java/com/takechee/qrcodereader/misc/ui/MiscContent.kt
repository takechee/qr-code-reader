package com.takechee.qrcodereader.misc.ui

import androidx.annotation.StringRes
import com.takechee.qrcodereader.misc.R

enum class MiscContent(@StringRes val title: Int) {
    PRIVACY_POLICY(R.string.privacy_policy),
    OPEN_STORE(R.string.open_store),
    LICENSES(R.string.licenses_title),
}