package org.dclm.live.browser



import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.dclm.live.*
import org.dclm.live.presenter.GridPresenter
import org.dclm.live.presenter.MyMediaPresenter
import org.dclm.live.presenter.MyPresenter
import org.dclm.live.presenter.MyPresenter2
import org.dclm.live.detail.DetailActivity
import org.dclm.live.model.*
import org.dclm.live.spinner.SpinnerFragment
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set


class MainFragment : BrowseSupportFragment(), OnItemViewClickedListener,OnItemViewSelectedListener  {

    private val GRID_ITEM_WIDTH = 300
    private val GRID_ITEM_HEIGHT = 200
    private var mQueue: RequestQueue? = null
    var windowAdapter:ArrayObjectAdapter? = null
    private var simpleBackgroundManager: SimpleBackground? = null
    val categories: MutableList<Category> = java.util.ArrayList<Category>()

    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    val PREFRENCES = "org.dclm.radio"
    private var messageData:MutableList<Message> = ArrayList<Message>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = activity?.getSharedPreferences(PREFRENCES, Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()
        val progress = SpinnerFragment()
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_browse_fragment, progress)
            ?.addToBackStack(null)?.commit()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // link = getActivity().getResources().getString(R.string.radio_link);
        windowAdapter = ArrayObjectAdapter(ListRowPresenter())
        mQueue = Volley.newRequestQueue(requireActivity().applicationContext)
        setUI()
        simpleBackgroundManager = SimpleBackground(requireActivity())
        onItemViewClickedListener = this
        onItemViewSelectedListener = this
    }



    private fun setUI() {
        //title = " DCLM App"
        //badgeDrawable =activity?.resources?.getDrawable(R.drawable.nlogo, null)
        badgeDrawable = activity?.let { ContextCompat.getDrawable(it,
            R.drawable.nlogo
        ) }
        headersState = HEADERS_ENABLED
        brandColor = Color.parseColor("#1c1859")

        isHeadersTransitionOnBackEnabled = true
        loadRows()
       // prepareBackgroundManager(R.drawable.main)
        setHeaderPresenterSelector(object : PresenterSelector() {
            override fun getPresenter(o: Any): Presenter {
                return IconHeaderItemPresenter()
            }
        })
        //setupEventListeners()
    }
    fun categoryJson() {

        if (context != null) {
            val request: JsonArrayRequest = object : JsonArrayRequest(Method.GET, "put your url", null, Response.Listener { response: JSONArray ->
                        for (i in 0 until response.length()) {
                            try {
                                val `object` = response.getJSONObject(i)
                                val id = `object`.getInt("categoryId")
                                val category = `object`.getString("categoryName")
                                categories.add(Category(id, category))
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }

                jsonParseMessage()
                    },
                    Response.ErrorListener {

                    }
                ) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers: MutableMap<String, String> =
                            HashMap()
                        headers["dclm header"] =
                        ""
                        headers["dclm header"] = ""
                        return headers
                    }
                }
            mQueue!!.add(request)
        }
    }

    fun jsonParseMessage() {
        val header3 = HeaderItem(3, getString(R.string.media))
        val gridItemPresenterHeader3 =
            IconHeaderItem(
                3,
                getString(R.string.media),
                R.drawable.nlogo11
            )
        val adapter4 = ArrayObjectAdapter(context?.let { MyMediaPresenter(it) })
        val onDemands: MutableList<Message> = ArrayList<Message>()
        val request: JsonObjectRequest = object :
            JsonObjectRequest(
                Method.GET, "put your url ", null, Response.Listener { response: JSONObject ->
                try {
                    val `object` = response.getJSONObject("meta")
                    var count = `object`.getString("count").toInt()

                    for (i in 0..19) {
                        val object2 = response.getJSONObject("result")
                        val object3 = object2.getJSONObject("data")
                        try {
                            val object4 = object3.getJSONObject(count.toString())
                            val sermonTitle = object4.getString("sermonTitle")
                            val date = object4.getString("sermonDate")
                            val sermonLow = object4.getString("sermonLow")
                            val sermonHigh = object4.getString("sermonHigh")
                            val sermonAudio = object4.getString("sermonAudio")
                            val languageId = object4.getInt("languageId")
                            val category = object4.getInt("categoryId")

                            if (languageId == context?.resources?.getString(R.string.languageId)?.toInt()) {
                                onDemands.add(
                                    Message(
                                        title = sermonTitle,
                                        audio = sermonAudio,
                                        video = sermonHigh,
                                        preacher = context?.resources?.getString(R.string.kumuyi)!!,
                                        category = categories[category-1].category,
                                        image = R.drawable.blog_one
                                    )
                                )
                                if (languageId == 1){
                                    adapter4.add(
                                        Message(
                                            title = sermonTitle,
                                            audio = sermonAudio,
                                            videoHigh = sermonHigh,
                                            video = sermonLow,
                                            preacher = context?.resources?.getString(R.string.kumuyi)!!,
                                            category = categories[category-1].category,
                                            image = R.drawable.blog_one
                                        )
                                    )

                                } else{
                                    adapter4.add(
                                        Message(
                                            title = sermonTitle,
                                            audio = sermonHigh,
                                            videoHigh = sermonHigh,
                                            video = sermonHigh,
                                            preacher = context?.resources?.getString(R.string.kumuyi)!!,
                                            category = categories[category-1].category,
                                            image = R.drawable.blog_one
                                        )
                                    )
                                }

                            }
                        } catch (E: JSONException) {
                            E.printStackTrace()
                        }
                        count--
                        //Log.i(" count", date);
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                    windowAdapter?.add(ListRow(gridItemPresenterHeader3, adapter4))
                    adapter = windowAdapter
                    activity?.supportFragmentManager?.beginTransaction()?.remove(SpinnerFragment())?.commit()
                    activity?.supportFragmentManager?.popBackStack()

            },
                Response.ErrorListener {

                }
            ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> =
                    HashMap()
                headers[""] =
                    ""
                headers[""] = ""
                return headers
            }
        }
        mQueue!!.add(request)
    }

    override fun onStart() {
        super.onStart()
        simpleBackgroundManager?.updateBackground(context?.let { ContextCompat.getDrawable(it, R.drawable.radio_banner) })
    }
    private fun loadRows() {
        val gridItemPresenterHeader =
            IconHeaderItem(
                1,
                activity?.resources?.getString(R.string.english),
                R.drawable.nlogo11
            )
        val gridItemPresenterHeader1 =
            IconHeaderItem(
                2,
                activity?.resources?.getString(R.string.french),
                R.drawable.nlogo11
            )
        val gridItemPresenterHeader3 =
            IconHeaderItem(
                4,
                "Doctrines",
                R.drawable.nlogo11
            )

        val header1 = HeaderItem(1, activity?.resources?.getString(R.string.english))
        val header2 = HeaderItem(2, activity?.resources?.getString(R.string.french))
        val header4 = HeaderItem(4, "Doctrines")
        val adapter1 = ArrayObjectAdapter(context?.let { MyPresenter(it) })
        adapter1.add(
            Message(0,
                getString(R.string.stream),
                getString(R.string.radio_link),
                getString(R.string.video_url),
                getString(R.string.video_url),
                getString(R.string.dclm_api),
                getString(R.string.kumuyi) ,
                R.drawable.video_icon,
                0
            )
        )
      /*  adapter1.add(
            RowData(
                R.drawable.video_icon,
                getString(R.string.stream),
                getString(R.string.video_url),
                getString(R.string.dclm_api)
            )
        )*/
        windowAdapter?.add(ListRow(gridItemPresenterHeader, adapter1))


        val adapter2 = ArrayObjectAdapter(context?.let { MyPresenter2(it) })
       /* adapter2.add(
            RowData1(
                R.drawable.radio_icon,
                "Radio",
                getString(R.string.radio_link_two),
                getString(R.string.dclm_api_two)
            )
        )
        adapter2.add(
            RowData1(
                R.drawable.video_icon_1,
                getString(R.string.stream),
                getString(R.string.video_url_two),
                getString(R.string.dclm_api_two)
            )

        )*/
        adapter2.add(
            Message(0,
                getString(R.string.stream),
                getString(R.string.radio_link_two),
                getString(R.string.video_url_two),
                getString(R.string.video_url_two),
                getString(R.string.dclm_api_two),
                getString(R.string.kumuyi) ,
                R.drawable.video_icon_1,
                0
            )
        )
        windowAdapter?.add(ListRow(gridItemPresenterHeader1, adapter2))

        val doctrineTitle: Array<out String>? = activity?.resources?.getStringArray(
            R.array.heading
        )
        val adapter3 = ArrayObjectAdapter(GridPresenter())
        for (i in 0 until doctrineTitle?.size!!) {
            adapter3.add(doctrineTitle[i])
        }

        windowAdapter?.add(ListRow(gridItemPresenterHeader3, adapter3))

        //windowAdapter.add(ListRow(header3, adapter4))

        adapter = windowAdapter
        categoryJson()
    }



    override fun onItemClicked(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        if (item is Message) {
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra("message", item)
            intent.putExtra("id", item.id)
            if (item.title == activity?.resources?.getString(R.string.stream)){
                intent.putExtra("live", true)
            }
            activity?.startActivity(intent)
        } else if (item is RowData){
            if (item.name == activity?.resources?.getString(R.string.stream)){
                with(Intent(activity, DetailActivity::class.java)){
                    this.putExtra("link", item.link)
                    this.putExtra("api", item.api)
                    this.putExtra("live", true)
                   /* intent.putExtra("link", message?.audio)
                    intent.putExtra("api", " ")
                    intent.putExtra("title", message?.title)
                    intent.putExtra("preacher", getString(R.string.kumuyi))
                    intent.putExtra("topic", message?.title)
                    intent.putExtra("live", false)
                    intent.putExtra("radio", true)*/
                    val message = Message(0, getString(R.string.stream), getString(R.string.video_url),getString(R.string.video_url_b), getString(R.string.video_url), getString(R.string.stream), getString(R.string.kumuyi), R.drawable.blog_one, 0)
                    this.putExtra("message", message)
                    startActivity(this)
                }
            } else{
                with(Intent(activity, VideoActivity2::class.java)){
                    this.putExtra("link", item.link)
                    this.putExtra("api", item.api)
                    this.putExtra("topic", " ")
                    this.putExtra("live", true)
                    startActivity(this)
                }
            }

        } else if (item is RowData1){
            if (item.name == activity?.resources?.getString(R.string.stream)){
                with(Intent(activity, DetailActivity::class.java)){
                    this.putExtra("link", item.link)
                    this.putExtra("api", item.api)
                    this.putExtra("live", true)

                    val message = Message(0, getString(R.string.stream), getString(R.string.video_url_two),getString(R.string.video_url_two_b), getString(R.string.video_url_two),getString(R.string.stream), getString(R.string.kumuyi), R.drawable.blog_one, 0)
                    this.putExtra("message", message)
                    startActivity(this)
                }
            } else{
                with(Intent(activity, RadioActivity::class.java)){
                    this.putExtra("link", item.link)
                    this.putExtra("api", item.api)
                    this.putExtra("topic", " ")
                    this.putExtra("live", true)
                    startActivity(this)
                }
            }
        } else{
            with(Intent(activity, DoctrineActivity::class.java)){
                this.putExtra("doctrine", item as String)
                //Toast.makeText(activity, item as String, Toast.LENGTH_LONG).show()
                startActivity(this)
            }
        }
    }

    override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
       if (item is RowData){
           simpleBackgroundManager?.updateBackground(context?.let { ContextCompat.getDrawable(it, R.drawable.main4) })
       } else if (item is Message){
           if (item.title == activity?.resources?.getString(R.string.stream) ){
               simpleBackgroundManager?.updateBackground(context?.let { ContextCompat.getDrawable(it, R.drawable.people) })
           } else{
               simpleBackgroundManager?.updateBackground(context?.let { ContextCompat.getDrawable(it, R.drawable.radio_banner) })
           }
          // prepareBackgroundManager(R.drawable.radio_banner)
       } else if (item is RowData1){
           simpleBackgroundManager?.updateBackground(context?.let { ContextCompat.getDrawable(it, R.drawable.people) })
       } else{
           simpleBackgroundManager?.updateBackground(context?.let { ContextCompat.getDrawable(it, R.drawable.bible) })
           //prepareBackgroundManager(R.drawable.bible)
       }
    }
    suspend fun getdata(){
        withContext(Dispatchers.IO){

        }
    }

   /* override fun messageRecieved(message: MutableList<Message>) {
Toast.makeText(context, "size ${messageData.size}", Toast.LENGTH_SHORT).show()
        if (!sharedPreferences?.getBoolean("initial", false)!!){
            lifecycleScope.launch {
                for (i in 0 until message.size){
                    database?.noteDAO()?.insert(message[i])
                }

            }
            val header3 = HeaderItem(3, getString(R.string.media))
            val gridItemPresenterHeader3 =
                IconHeaderItem(
                    3,
                    getString(R.string.media),
                    R.drawable.nlogo11
                )
            val adapter4 = ArrayObjectAdapter(context?.let { MyMediaPresenter(it) })
            for (i in 0 until message.size){
                adapter4.add(message[i])
            }
            windowAdapter?.add(ListRow(gridItemPresenterHeader3, adapter4))
            adapter = windowAdapter

        } else{
            lifecycleScope.launch {
                for (i in 0 until message.size){
                    val message1 = message[i]
                    message1.id = i + 1
                    for (z in 0 until messageData.size){
                        if (message1.title == messageData[z].title){
                            message1.seekto = messageData[z].seekto
                        }
                    }
                    database?.noteDAO()?.update(message1)

                }

            }
            val header3 = HeaderItem(3, getString(R.string.media))
            val gridItemPresenterHeader3 =
                IconHeaderItem(
                    3,
                    getString(R.string.media),
                    R.drawable.nlogo11
                )
            val adapter4 = ArrayObjectAdapter(context?.let { MyMediaPresenter(it) })
            for (i in 0 until message.size){
                adapter4.add(message[i])
            }
            windowAdapter?.add(ListRow(gridItemPresenterHeader3, adapter4))
            adapter = windowAdapter

        }
         editor?.putBoolean("initial", true)
        editor?.apply()

        activity?.supportFragmentManager?.beginTransaction()?.remove(SpinnerFragment())?.commit()
        activity?.supportFragmentManager?.popBackStack()



    }

    override fun messageError() {
        activity?.supportFragmentManager?.beginTransaction()?.remove(SpinnerFragment())?.commit()
        activity?.supportFragmentManager?.popBackStack()
       *//* lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val message1 = database?.noteDAO()?.getAllNotes()

                withContext(Dispatchers.Main) {
                    val header3 = HeaderItem(3, getString(R.string.media))
                    val gridItemPresenterHeader3 =
                        IconHeaderItem(
                            3,
                            getString(R.string.media),
                            R.drawable.nlogo11
                        )
                    val adapter4 = ArrayObjectAdapter(context?.let { MyMediaPresenter(it) })
                    for (i in 0 until message1.size) {
                        adapter4.add(message1[i])
                    }
                    windowAdapter?.add(ListRow(gridItemPresenterHeader3, adapter4))
                    adapter = windowAdapter

            }*//*
        database?.noteDAO()?.getAllNotes1()?.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val gridItemPresenterHeader3 =
                IconHeaderItem(
                    3,
                    getString(R.string.media),
                    R.drawable.nlogo11
                )
            val adapter4 = ArrayObjectAdapter(context?.let { it1 -> MyMediaPresenter(it1) })
            for (i in 0 until it.size!!) {
                adapter4.add(it[i])
            }
            windowAdapter?.add(ListRow(gridItemPresenterHeader3, adapter4))
            adapter = windowAdapter
        })


        val errorFragment = ErrorFragment()
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_browse_fragment, errorFragment)
            ?.addToBackStack(null)?.commit()
        }
*/
    }

