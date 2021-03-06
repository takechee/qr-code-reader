package com.takechee.qrcodereader.misc.ui

interface MiscUserActionEventListener {
    fun toggleUseBrowserApp(checked: Boolean)
    fun toggleAutoLoadNickname(checked: Boolean)
    fun onContentClicked(content: MiscContent)
}