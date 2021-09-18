package jp.nakaara.mabataki

import android.app.Application

class UtilCommon : Application() {
    var vibration : Boolean = false
        get() = field
        set(value) {
            field = value
        }

    /**
     * 動作対象のアプリケーションを格納
     */
    val appList : ArrayList<String> = arrayListOf()
        get() = field
}