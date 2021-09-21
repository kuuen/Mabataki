package jp.nakaara.mabataki

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.ApplicationInfo

import android.util.Log
import android.graphics.drawable.Drawable

import android.content.Context

import android.view.ViewGroup

import android.view.LayoutInflater
import android.view.View
import android.widget.*

import android.widget.AdapterView.OnItemClickListener

import android.widget.ArrayAdapter

class AppListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_app_list)

        // 端末にインストール済のアプリケーション一覧情報を取得
        val packageManager = getPackageManager()
        val installedAppList: List<ApplicationInfo> = packageManager.getInstalledApplications(0)

        // リストに一覧データを格納する
        val dataList: MutableList<AppData> = ArrayList<AppData>()

        // 値を保存するクラスを取得
        val utilCommon = UtilCommon.getInstance(this)

        // 指定されたのアプリ一覧を格納するクラスを取得
        val appList = utilCommon.appList

        // インストールされたアプリ一覧を
        for (app in installedAppList) {
            if (app.flags and ApplicationInfo.FLAG_SYSTEM === ApplicationInfo.FLAG_SYSTEM) {
                continue
            }

            val data = AppData()
            data.label = app.loadLabel(packageManager).toString()
            data.icon = app.loadIcon(packageManager)
            data.pname = app.packageName
            data.chkApp = appList?.find { it == app.packageName } != null

            dataList.add(data)
        }

        // チェックされたアプリを優先にソート
        dataList.sortBy { !it.chkApp }

        // リストビューにアプリケーションの一覧を表示する
        val listView : ListView = findViewById<ListView>(R.id.listView)

        listView.setAdapter(AppListAdapter(this, dataList, utilCommon))

        //クリック処理
        listView.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            val item : AppData = dataList[position]
            val pManager = getPackageManager()
            val intent = pManager.getLaunchIntentForPackage(item.pname.toString())
            startActivity(intent)
        })
    }

    override fun onPause() {
        super.onPause()

        val utilCommon = UtilCommon.getInstance(this)
        Log.d("AppList", utilCommon.appList.count().toString())

        utilCommon.saveInstance(this)
    }

    /**
     * アプリケーションデータ格納クラス
     * ListViewに設定する値を格納
     */
    private class AppData {
        var label: String? = null
        var icon: Drawable? = null
        var pname: String? = null
        var chkApp : Boolean = false
    }

    /**
     * ビューホルダー
     * ListViewに表示されるオブジェクトを保持する
     */
    private class ViewHolder {
        var textLabel: TextView? = null
        var imageIcon: ImageView? = null
        var chkApp : CheckBox? = null
    }

    /**
     * アプリケーションのラベルとアイコンを表示するためのアダプタークラス
     */
    private class AppListAdapter(context: Context, dataList: List<AppData?>?, uc : UtilCommon ) : ArrayAdapter<AppData?>(context, R.layout.activity_main) {

        val utilCommon : UtilCommon

        private val mInflater: LayoutInflater

        /**
         * コンストラクタ
         */
        init {
            mInflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            addAll(dataList!!)
            utilCommon = uc
        }

        /**
         * ListViewに表示される前に呼び出される
         * 行単位で呼び出されているよう　ここでオブジェクトに値を設定している
         */
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            var holder : ViewHolder = ViewHolder()

            var convertView = convertView

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_items, parent, false)
                holder.textLabel = convertView!!.findViewById<View>(R.id.label) as TextView
                holder.imageIcon = convertView.findViewById<View>(R.id.icon) as ImageView
                holder.chkApp = convertView.findViewById<View>(R.id.chkApp) as CheckBox
                convertView.setTag(holder)
            } else {
                // ここが実行されるケースがわからないいらない記述か？
                holder = convertView.getTag() as ViewHolder
            }

            // 表示データを取得　positionは今から設定する行No
            val data = getItem(position)
            // ラベルとアイコンをリストビューに設定
            holder.textLabel!!.setText(data!!.label)
            holder.imageIcon!!.setImageDrawable( data!!.icon)
            holder.chkApp!!.setChecked(data!!.chkApp)

            var chk = convertView.findViewById<View>(R.id.chkApp) as CheckBox

            // チェックした時のイベント
            chk.setOnClickListener{
                data.chkApp = chk.isChecked

                // 値格納オブジェクトに設定して保存を行う
                if (chk.isChecked()) {
                    if (utilCommon.appList.find {it == data.pname} == null) {
                        utilCommon.appList.add(data.pname.toString())
                    }
                } else {
                    utilCommon.appList.remove(data.pname)
                }

                utilCommon.saveInstance(context)
                Log.d("AppList", utilCommon.appList.count().toString())
            }

            return convertView!!
        }
    }
}
