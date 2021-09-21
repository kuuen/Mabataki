package jp.nakaara.mabataki

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.gson.Gson

import android.text.TextUtils

/**
 * データ格納クラス
 * PreferenceManagerでアプリを閉じてもデータを保持できる
 */
class UtilCommon  {
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


    companion object {
        private val UTIL_COMMON_PREF_KEY = "UTIL_COMMON"

        /**
         * 唯一のインスタンスを取得
         */
        fun getInstance(context: Context): UtilCommon {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val gson = Gson()
            val userSettingString = prefs.getString(UTIL_COMMON_PREF_KEY, "")
            val instance: UtilCommon
            // 保存したオブジェクトを取得
            if (!TextUtils.isEmpty(userSettingString)) {
                instance = gson.fromJson(userSettingString, UtilCommon::class.java)
//                instance = Gson().fromJson<UtilCommon>(
//                    userSettingString,
//                    UtilCommon::class.java
//                ) as UtilCommon

            } else {
                // 何も保存されてない 初期時点 この時はデフォルト値を入れてあげる
                instance = getDefaultInstance()
            }
            return instance
        }

        // デフォルト値の入ったオブジェクトを返す
        fun getDefaultInstance(): UtilCommon {
            val instance = UtilCommon()
            instance.vibration = false

            return instance
        }
    }

    // 状態保存メソッド
    fun saveInstance(context: Context?) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val gson = Gson()
        // 現在のインスタンスの状態を保存
        prefs.edit().putString(UTIL_COMMON_PREF_KEY, gson.toJson(this)).apply()

//        with (prefs.edit()) {
//            putString(UTIL_COMMON_PREF_KEY, gson.toJson(this))
//            commit()
//        }
    }
}
