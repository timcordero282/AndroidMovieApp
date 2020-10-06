package com.example.lab12

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spMovie = findViewById<Spinner>(R.id.spMovie)
        val tvRank = findViewById<TextView>(R.id.tvRank)
        val tvYear = findViewById<TextView>(R.id.tvYear)
        val tvDirector = findViewById<TextView>(R.id.tvDirector)
        val tvDescr = findViewById<TextView>(R.id.tvDescr)
        val ivMovie = findViewById<ImageView>(R.id.ivMovie)
        val tvStars = findViewById<TextView>(R.id.tvStars)
        val btnVideo = findViewById<Button>(R.id.btnVideo)

        val url: String = "http://www1.lasalle.edu/~blum/c349wks/IMDB/IMDB.json"
        val baseURL: String= "http://www1.lasalle.edu/~blum/c349wks/IMDB/"

        val movieTitles: MutableList<String> = ArrayList()
        var myJSONarray: JSONArray

        val requestQueue = Volley.newRequestQueue(this)

        // Initialize a new JsonObjectRequest instance
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            object : Response.Listener<JSONObject> {
                override fun onResponse(response: JSONObject) {

                    // Do something with response

                    // Process the JSON
                    try {
                        // Get the JSON array
                        myJSONarray = response.getJSONArray("IMDBTop")

                        // Loop through the array elements
                        for (i in 0 until myJSONarray.length()) {
                            // Get current json object
                            val movie = myJSONarray.getJSONObject(i)

                            movieTitles.add(movie.getString("movieTitle"))

                        }

                        //use array of string to populate a spinner
                        val aaTitles = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, movieTitles)
                        aaTitles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // The drop down view
                        spMovie.adapter = aaTitles

                        // when item chosen in spinner display same thing in textview
                        spMovie.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                                val tvUserItem = view as TextView
                                Toast.makeText(applicationContext, "You chose " + tvUserItem.text.toString(), Toast.LENGTH_SHORT).show()

                                tvRank.text = "IMDB Rank: " + myJSONarray.getJSONObject(i).getString("IMDBRank")
                                tvYear.text = "Movie Year: " + myJSONarray.getJSONObject(i).getString("movieYear")
                                tvDirector.text ="Movie Director: " + myJSONarray.getJSONObject(i).getString("movieDirector")
                                tvDescr.text = "Movie Description: " + myJSONarray.getJSONObject(i).getString("movieDesc")
                                Picasso.get().load( baseURL+myJSONarray.getJSONObject(i).getString("moviePoster")).into(ivMovie);
                                tvStars.text = "Movie Stars: " + myJSONarray.getJSONObject(i).getString("movieStars")

                            }

                            override fun onNothingSelected(adapterView: AdapterView<*>) {}
                        }//end onItemSelectedListener

                        btnVideo.setOnClickListener {
                            val id = spMovie.selectedItemPosition

                            try {
                                val ytc = myJSONarray.getJSONObject(id).getString("youtubeCode")
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$ytc")))
                            } catch (e: JSONException) {

                            }
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }//end onResponse
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    // Do something when error occurred
                    Toast.makeText(this@MainActivity,error.toString(),Toast.LENGTH_LONG).show()
                }//end onErrorResponse
            }
        )

        // Add JsonObjectRequest to the RequestQueue
        requestQueue.add(jsonObjectRequest)

    }
}
