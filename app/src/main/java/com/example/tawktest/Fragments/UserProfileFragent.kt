package com.example.tawktest.Fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tawktest.Api.APIManager
import com.example.tawktest.DataModels.UserDetailModel
import com.example.tawktest.DataModels.UserNoteModel
import com.example.tawktest.MainActivity
import com.example.tawktest.R
import kotlinx.android.synthetic.main.fragment_user_list.*
import kotlinx.android.synthetic.main.fragment_user_profile_fragent.*


class UserProfileFragent : Fragment() {

    private var mainActivity: MainActivity? = null
    var login: String? = null
    var id: Int? = null
    var mWifi: NetworkInfo? = null

    lateinit var nDialog: ProgressDialog



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mainActivity?.initializeCustomActionBar()
        return inflater.inflate(R.layout.fragment_user_profile_fragent, container, false)
    }

    @SuppressLint("ServiceCast", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = MainActivity()
        nDialog = ProgressDialog(requireContext())


        val connManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

        login = arguments?.get("login").toString()
        id = arguments?.get("id").toString().toInt()

        backbtn.setOnClickListener {
            findNavController().popBackStack()
        }

        if (mainActivity!!.isConnected) {
            fetchUser()
        } else {
            fetchOfflineUser()
        }

        saveButton.setOnClickListener {
            showSpinner()
            Toast.makeText(requireContext(), "Save note successfully", Toast.LENGTH_SHORT).show()
            //add or removing data local notes
            AsyncTask.execute {
                if (et_notes.text.isNullOrEmpty()) {
                    //removing data if the text is save empty
                    MainActivity.appDb.getDao().deleteUserNote(arguments?.get("login").toString())
                } else {
                    //adding data if the text is not empty

                    MainActivity.appDb.getDao().insertUserNote(
                        UserNoteModel(
                            arguments?.get("login").toString(),
                            arguments?.get("id").toString().toInt(), et_notes.text.toString()
                        )
                    )
                }
                hideSpinner()
            }
        }
    }

    fun fetchUser() {
        showSpinner()
        APIManager.create().getUserDetails(login!!) { code, userData, errorBody ->
            if (code == 200) {
                if (userData == null) {
                    Log.e("ccc", "empty $userData")
                } else {
                    AsyncTask.execute {
                        MainActivity.appDb.getDao().insertUserDetail(userData)
                    }
                    setDetails(userData)
                }
            } else {
                Log.e("ccc", "error $errorBody")
            }
            hideSpinner()
        }
    }

    fun fetchOfflineUser() {
        showSpinner()
        AsyncTask.execute {
            val data = MainActivity.appDb.getDao().getUserDetail(login!!)
            if (data != null) {
                setDetails(data)
            } else {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "No user detail save for this user",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            hideSpinner()
        }
    }


    @SuppressLint("SetTextI18n")
    fun setDetails(user: UserDetailModel) {
        val avatar = arguments?.get("avatar").toString()

        requireActivity().runOnUiThread {
            Glide.with(requireContext())
                .load(avatar)
                .error(R.mipmap.ic_launcher)
                .apply(RequestOptions.circleCropTransform())
                .into(user_pic)


            userName.text = user.name
            tvname.text = "name: ${user.name}"
            tvblog.text = "blog: ${user.blog}"
            tvcompany.text = "company: ${user.company}"
            followers.text = "followers: ${user.followers}"
            following.text = "following: ${user.following}"

            getUserNote()

        }

    }

    fun showSpinner() {
        nDialog.setMessage("Loading..")
        nDialog.setCancelable(true)
        nDialog.show()

    }

    fun hideSpinner() {
        nDialog.dismiss()
    }

    fun getUserNote() {
        AsyncTask.execute {
            val note = MainActivity.appDb.getDao().getUserNote(login!!)
            requireActivity().runOnUiThread {
                if (note != null) {
                    if (!note.note.isNullOrEmpty()) {
                        et_notes.setText(note.note)
                    } else {
                        et_notes.setText("")
                    }
                } else {
                    et_notes.setText("")
                }

            }

        }
    }

}