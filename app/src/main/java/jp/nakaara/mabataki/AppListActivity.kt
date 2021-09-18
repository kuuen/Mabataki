package jp.nakaara.mabataki

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo

import android.content.pm.PackageManager
import android.util.Log
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView.ViewHolder


import android.content.Context

import android.view.ViewGroup

import android.view.LayoutInflater
import android.view.View

import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.widget.*

import android.widget.AdapterView.OnItemClickListener
import android.R.array

import android.widget.ArrayAdapter





class AppListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_app_list)

        // 端末にインストール済のアプリケーション一覧情報を取得
        val packageManager = getPackageManager()
//        val packages: List<PackageInfo> = packageManager.getInstalledPackages(0)
//        for (info: PackageInfo in packages) {
//            Log.d("Test", "packageName: ${info.packageName}"
//                    + ", versionName: ${info.versionName}"
//                    + ", lastUpdateTime: ${info.lastUpdateTime}"
//                    + ", targetSdk: ${info.applicationInfo.targetSdkVersion}"
//                    + ", minSdk: ${info.applicationInfo.minSdkVersion}"
//                    + ", sourceDir: ${info.applicationInfo.sourceDir}"
//                    + ", uid: ${info.applicationInfo.uid}"
//                    + ", label: ${info.applicationInfo.loadLabel(packageManager)}"
//            )
//        }

        val installedAppList: List<ApplicationInfo> = packageManager.getInstalledApplications(0)
        // リストに一覧データを格納する

//        val dataListEx: MutableList<AppData> = ArrayList<AppData>()
//        for (app in installedAppList) {
//            val data = AppData()
//            data.label = app.loadLabel(packageManager).toString()
//            data.icon = app.loadIcon(packageManager)
//            data.pname = app.packageName
//            dataListEx.add(data)
//        }

        for (appInfo in installedAppList) {
            if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM === ApplicationInfo.FLAG_SYSTEM) {
                continue
            }

            Log.i("MainActivity", "ラベル = " + packageManager.getApplicationLabel(appInfo))
            Log.i("MainActivity", "パッケージ名 = " + appInfo.packageName)
        }

        // リストに一覧データを格納する
        val dataList: MutableList<AppData> = ArrayList<AppData>()
        val utilCommon =  (this.application as UtilCommon)
        val appList = utilCommon.appList

        for (app in installedAppList) {

            if (app.flags and ApplicationInfo.FLAG_SYSTEM === ApplicationInfo.FLAG_SYSTEM) {
                continue
            }

            val data = AppData()
            data.label = app.loadLabel(packageManager).toString()
            data.icon = app.loadIcon(packageManager)
            data.pname = app.packageName
            data.chkApp = appList.find { it == app.packageName } != null

            dataList.add(data)
        }

        // リストビューにアプリケーションの一覧を表示する
//        val listView = ListView(this)
        val listView : ListView = findViewById<ListView>(R.id.listView)

        val appListAdapter : AppListAdapter =  AppListAdapter(this, dataList)
        listView.adapter = appListAdapter

        listView.setAdapter(AppListAdapter(this, dataList))

        //クリック処理
        listView.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
//            val item = installedAppList[position]
            val item : AppData = dataList[position]
            val pManager = getPackageManager()
            //val intent = pManager.getLaunchIntentForPackage(item.packageName)
            val intent = pManager.getLaunchIntentForPackage(item.pname.toString())
            startActivity(intent)
        })

//        val listView2 : ListView = findViewById<ListView>(R.id.listView)
//
//        // Adapterに渡す配列を作成します
//        val data = arrayOf("パンダ", "ダチョウ", "ウミガメ", "メダカ")
//
//        // adapterを作成します
//        val adapter = ArrayAdapter(
//            this,
//            android.R.layout.simple_list_item_1,
//            data
//        )
//
//        // adapterをlistViewに紐付けます。
//        listView2.adapter = adapter
    }

    // アプリケーションデータ格納クラス
    private class AppData {
        var label: String? = null
        var icon: Drawable? = null
        var pname: String? = null
        var chkApp : Boolean = false
    }

    // ビューホルダー
    private class ViewHolder {
        var textLabel: TextView? = null
        var imageIcon: ImageView? = null
        var packageName: TextView? = null
        var chkApp : CheckBox? = null
    }

    // アプリケーションのラベルとアイコンを表示するためのアダプタークラス
    private class AppListAdapter(context: Context, dataList: List<AppData?>?) :
        ArrayAdapter<AppData?>(context, R.layout.activity_main) {

        private lateinit var utilCommon: UtilCommon

        private val mInflater: LayoutInflater

        init {
            mInflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            addAll(dataList!!)
        }

        public fun setUtilCommon(uc : UtilCommon)  {
            utilCommon = uc
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            var holder : ViewHolder = ViewHolder()

            var convertView = convertView

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_items, parent, false)
                holder.textLabel = convertView!!.findViewById<View>(R.id.label) as TextView
                holder.imageIcon = convertView.findViewById<View>(R.id.icon) as ImageView
//                holder.packageName = convertView.findViewById<View>(R.id.pname) as TextView
                holder.chkApp = convertView.findViewById<View>(R.id.chkApp) as CheckBox
                convertView.setTag(holder)
            } else {
                holder = convertView.getTag() as ViewHolder
            }

            // 表示データを取得
            val data = getItem(position)
            // ラベルとアイコンをリストビューに設定
            holder.textLabel!!.setText(data!!.label)
            holder.imageIcon!!.setImageDrawable( data!!.icon)
//            holder.packageName!!.setText(data!!.pname)
            holder.chkApp!!.setChecked(data!!.chkApp)

            var chk = convertView.findViewById<View>(R.id.chkApp) as CheckBox
            chk.setOnClickListener{
                data.chkApp = chk.isChecked

                if (chk.isChecked()) {
                    if (utilCommon.appList.find {it == data.pname} != null) {
                        utilCommon.appList.add(data.pname.toString())
                    }
                } else {
                    utilCommon.appList.remove(data.pname)
                }
            }

            return convertView!!
        }
    }
}


