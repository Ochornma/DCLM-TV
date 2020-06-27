package org.dclm.live.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.dclm.live.R
import org.dclm.live.VideoActivity2
import org.dclm.live.presenter.MyMediaPresenter
import org.dclm.live.model.Category
import org.dclm.live.model.Message
import org.dclm.live.playback.PlaybackActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class VideoDetailsFragment : DetailsSupportFragment(), OnItemViewClickedListener {

  private var mFwdorPresenter: FullWidthDetailsOverviewRowPresenter ? = null

    private var listRowAdapter:ArrayObjectAdapter? = null
    private var message: Message? = null
    private var one:Long = 1
    private var two:Long = 2
    private var three:Long = 3
    private var id1: Int = 0
    private var live = false
    private var live2 = false
    private var mQueue: RequestQueue? = null
    val categories: MutableList<Category> = java.util.ArrayList<Category>()
  //  private val mDetailsRowBuilderTask: DetailsRowBuilderTask? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      mQueue = Volley.newRequestQueue(requireActivity().applicationContext)
      live = activity?.intent?.getBooleanExtra("live", false)!!
      message = activity?.intent?.getParcelableExtra("message")
      id1 = activity?.intent?.getIntExtra("id", 1)!!
      mFwdorPresenter = FullWidthDetailsOverviewRowPresenter (context?.let {
          DetailsDescriptionPresenter(
              it
          )
      })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prepareBackgroundManager()

        presenter()


        onItemViewClickedListener = this
    }



    private fun prepareBackgroundManager() {
        val backgroundManager = BackgroundManager.getInstance(activity).apply {
            attach(activity?.window)
        }
        val defaultBackground = context?.let { ContextCompat.getDrawable(it,
            R.drawable.radio_banner
        ) }
        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        backgroundManager.drawable = defaultBackground
        //backgroundManager.attach(activity?.window)
    }
    fun presenter(){
        val row = DetailsOverviewRow(message)
       row.imageDrawable = context?.let { ContextCompat.getDrawable(it, R.drawable.nlogo) }
        val sparseArrayObjectAdapter = SparseArrayObjectAdapter()

            if (live){
                for (i in 0..1){
                    if (i == 0){
                        sparseArrayObjectAdapter[i] = Action(one, getString(R.string.str), getString(R.string.audio), context?.let { ContextCompat.getDrawable(it, R.drawable.radio1) })
                    } else{
                        sparseArrayObjectAdapter[i] = Action(two, getString(R.string.str), getString(R.string.video), context?.let { ContextCompat.getDrawable(it, R.drawable.nlogo11) })
                    }
                }

                live2 = true
            } else{
                sparseArrayObjectAdapter[0] = Action(one, getString(R.string.play), getString(R.string.audio), context?.let { ContextCompat.getDrawable(it, R.drawable.radio1) })
           /*     for (i in 0..2) {
                    if (i == 0){
                            sparseArrayObjectAdapter[i] = Action(one, getString(R.string.play), getString(R.string.audio), context?.let { ContextCompat.getDrawable(it, R.drawable.radio1) })
                        } else if (i == 1){
                            sparseArrayObjectAdapter[i] = Action(three, getString(R.string.play), getString(R.string.video), context?.let { ContextCompat.getDrawable(it, R.drawable.nlogo11) })
                        } else{
                            sparseArrayObjectAdapter[i] = Action(two, getString(R.string.play), getString(R.string.hd), context?.let { ContextCompat.getDrawable(it, R.drawable.nlogo11) })
                        }
            }*/
                live2 = false
            }



        row.actionsAdapter = sparseArrayObjectAdapter

         listRowAdapter = ArrayObjectAdapter(context?.let { MyMediaPresenter(it) })


        categoryJson()

       val headerItem = if (live){
            HeaderItem(0, getString(R.string.recent))
        } else{
            HeaderItem(0, getString(R.string.related))
        }


        val classPresenterSelector = ClassPresenterSelector()
        mFwdorPresenter?.initialState = FullWidthDetailsOverviewRowPresenter.STATE_SMALL
        mFwdorPresenter!!.onActionClickedListener = OnActionClickedListener { action ->
                if (action.id == one) {
                    val intent = Intent(activity, VideoActivity2::class.java)
                    intent.putExtra("link", message?.audio)
                    intent.putExtra("api", message?.category)
                    intent.putExtra("title", message?.title)
                    intent.putExtra("preacher", getString(R.string.kumuyi))
                    intent.putExtra("topic", message?.title)
                    intent.putExtra("live", live2)
                    intent.putExtra("radio", true)
                    intent.putExtra("message", message)
                    startActivity(intent)
                } else if (action.id == two){
                    val intent = Intent(activity, PlaybackActivity::class.java)
                    intent.putExtra("link", message?.video)
                    intent.putExtra("api", " ")
                    intent.putExtra("title", message?.title)
                    intent.putExtra("preacher", getString(R.string.kumuyi))
                    intent.putExtra("live", live2)
                    intent.putExtra("topic", message?.title)
                    intent.putExtra("radio", false)
                    intent.putExtra("high", true)
                    intent.putExtra("message", message)
                    startActivity(intent)
                } else{
                    val intent = Intent(activity, PlaybackActivity::class.java)
                    intent.putExtra("link", message?.video)
                    intent.putExtra("api", " ")
                    intent.putExtra("title", message?.title)
                    intent.putExtra("preacher", getString(R.string.kumuyi))
                    intent.putExtra("live", live2)
                    intent.putExtra("topic", message?.title)
                    intent.putExtra("radio", false)
                    intent.putExtra("high", false)
                    intent.putExtra("message", message)
                    startActivity(intent)
                }
            }
       /* Log.e(
            TAG,
            "mFwdorPresenter.getInitialState: " + mFwdorPresenter.getInitialState()
        )*/

        classPresenterSelector.addClassPresenter(DetailsOverviewRow::class.java, mFwdorPresenter)
       classPresenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())

        val adapter = ArrayObjectAdapter(classPresenterSelector)
        /* 1st row */
        /* 1st row */adapter.add(row)
        /* 2nd row */
        /* 2nd row */adapter.add(ListRow(headerItem, listRowAdapter))

        setAdapter(adapter)
    }

    private fun categoryJson2() {
       /* var message1:MutableList<Message> = ArrayList<Message>()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                message1 = database?.noteDAO()?.getAllNotes() as MutableList<Message>

            }

        }
        Toast.makeText(context, message1.size.toString(), Toast.LENGTH_LONG).show()
        //val adapter4 = ArrayObjectAdapter(context?.let { MainFragment.MyMediaPresenter(it) })
        for (i in 0 until message1.size) {

            Toast.makeText(context, "checlk", Toast.LENGTH_LONG).show()
            // adapter4.add(message1[i])
            listRowAdapter?.add(message1[i])
        }*/





        /*lifecycleScope.launch {
            withContext(Dispatchers.Main){
                val  message = database?.noteDAO()?.getAllNotes()
                val gridItemPresenterHeader3 =
                    IconHeaderItem(
                        3,
                        getString(R.string.media),
                        R.drawable.nlogo11
                    )
                val adapter4 = ArrayObjectAdapter(context?.let { it1 -> MainFragment.MyMediaPresenter(it1) })
                for (i in 0 until message?.size!!) {
                    listRowAdapter?.add(message[i])
                }

            }
        }*/
    }

    fun jsonParseMessage() {
     //   val onDemands: MutableList<Message> = ArrayList<Message>()
        val request: JsonObjectRequest = object : JsonObjectRequest(Method.GET, "put your url ", null, Response.Listener { response: JSONObject ->
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
                            //onDemands.add(Message(title = sermonTitle, audio = sermonAudio, video = sermonLow, preacher = context!!.resources?.getString(R.string.kumuyi)!!, duration = " ", image = R.drawable.blog_one))
                            listRowAdapter!!.add(
                                Message(
                                    title = sermonTitle,
                                    audio = sermonAudio,
                                    video = sermonHigh,
                                    preacher = context?.resources?.getString(R.string.kumuyi)!!,
                                    category = categories[category - 1].category,
                                    image = R.drawable.blog_one
                                )
                            )
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

    fun categoryJson() {

        if (context != null) {
            val request: JsonArrayRequest = object : JsonArrayRequest(
                Method.GET, " put your url", null, Response.Listener { response: JSONArray ->
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
                    headers[" "] =
                        " "
                    headers[" "] = " "
                    return headers
                }
            }
            mQueue!!.add(request)
        }
    }

    override fun onItemClicked(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        if (item is Message) {
            val  message = item as Message
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra("message", message)
            activity?.startActivity(intent)
        }
    }


}

class DetailsDescriptionPresenter(val context: Context) : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(vh: ViewHolder?, item: Any?) {
        val movie = item as Message?
        if (movie != null) {
            vh?.title?.text = movie.title
            vh?.subtitle?.text = movie.preacher
            vh?.body?.text = context.getString(R.string.earnestly)

        }
    }
}
