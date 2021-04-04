package com.example.tawktest.Fragments

import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tawktest.Adapter.UserListAdapter
import com.example.tawktest.Api.APIManager
import com.example.tawktest.DataModels.UserModel
import com.example.tawktest.DataModels.UserNoteModel
import com.example.tawktest.MainActivity
import com.example.tawktest.R
import kotlinx.android.synthetic.main.fragment_user_list.*
import kotlinx.android.synthetic.main.fragment_user_profile_fragent.*


@Suppress("UNREACHABLE_CODE")
class UserListFragment : Fragment(), UserListAdapter.EventOnClickInterface {


    private var userListAdapter: UserListAdapter? = null
    private var userList: ArrayList<UserModel> = arrayListOf()
    private var noteList: ArrayList<UserNoteModel> = arrayListOf()
    private var loginList: ArrayList<String> = arrayListOf()

    private var mainActivity: MainActivity? = null

    lateinit var nDialog: ProgressDialog


    private var filteredList: ArrayList<UserModel> = arrayListOf()
    var mWifi: NetworkInfo? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = MainActivity()
        nDialog = ProgressDialog(requireContext())


        val connManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

        userListAdapter = UserListAdapter(requireContext(), filteredList, this)
        user_event_list.layoutManager = LinearLayoutManager(requireContext())
        user_event_list.adapter = userListAdapter


        editTextTextPersonName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().toLowerCase().trim()


                if (!filteredList.isNullOrEmpty()) {
                    filteredList.clear()
                }

                if (query.isNullOrEmpty()) {
                    filteredList.addAll(userList)

                } else {
                    //filtering the enter text to match the users

                    val a = userList.filter {
                        it.login == query
                    }
                    if (a.isNotEmpty()) {
                        filteredList.clear()
                        filteredList.addAll(a)
                    }
                }
                userListAdapter?.notifyDataSetChanged()
            }

        })

        getUserNote()

    }

    fun saveAllUser() {
        showSpinner()
        APIManager.create().getUserList { code, userData, errorBody ->
            if (code == 200) {
                if (userData.isNullOrEmpty()) {
                    Log.e("ccc", "empty $userData")
                } else {
                    if (filteredList.isNotEmpty()) {
                        filteredList.clear()
                    }

                    val userdataList: MutableList<UserModel> = arrayListOf()

                    userData.forEach {
                        if (!noteList.isNullOrEmpty()) {
                            userdataList.add(
                                UserModel(
                                    it.login,
                                    it.id,
                                    it.node_id,
                                    it.avatar_url,
                                    it.gravatar_id,
                                    it.url,
                                    it.html_url,
                                    it.followers_url,
                                    it.following_url,
                                    it.gists_url,
                                    it.starred_url,
                                    it.subscriptions_url,
                                    it.inmorganizations_urlNo,
                                    it.repos_url,
                                    it.events_url,
                                    it.received_events_url,
                                    it.type,
                                    it.site_admin,
                                    hasNote(it.login!!)
                                )
                            )
                        } else {
                            userList.addAll(listOf(it))
                            filteredList.addAll(listOf(it))
                        }
                    }

                    filteredList.addAll(userdataList)

                    userListAdapter?.userList = filteredList
                    userListAdapter?.notifyDataSetChanged()

                    for (login in userData) {
                        fetchUserDetails(login.login!!)
                    }

                    AsyncTask.execute {
                        MainActivity.appDb.getDao().deleteALLUsers()
                        MainActivity.appDb.getDao().insertUsers(userData)
                    }

                }
            } else {
                Log.e("ccc", "error $errorBody")
            }

            hindeSpinner()
        }
    }

    fun fetchUserDetails(login: String) {
        APIManager.create().getUserDetails(login) { code, userData, errorBody ->
            if (code == 200) {
                if (userData == null) {
                    Log.e("ccc", "empty $userData")
                } else {
                    AsyncTask.execute {
                        MainActivity.appDb.getDao().insertUserDetail(userData)
                    }
                }
            } else {
                Log.e("ccc", "error $errorBody")
            }
        }

    }


    fun getAllUsers() {
        showSpinner()
        AsyncTask.execute {
            val userdata: MutableList<UserModel> = arrayListOf()
            filteredList.clear()
            userList.clear()
            MainActivity.appDb.getDao().getAllUsers().forEach {
                if (!noteList.isNullOrEmpty()) {
                    userdata.add(
                        UserModel(
                            it.login,
                            it.id,
                            it.node_id,
                            it.avatar_url,
                            it.gravatar_id,
                            it.url,
                            it.html_url,
                            it.followers_url,
                            it.following_url,
                            it.gists_url,
                            it.starred_url,
                            it.subscriptions_url,
                            it.inmorganizations_urlNo,
                            it.repos_url,
                            it.events_url,
                            it.received_events_url,
                            it.type,
                            it.site_admin,
                            hasNote(it.login!!)
                        )
                    )
                } else {
                    userList.addAll(listOf(it))
                    filteredList.addAll(listOf(it))
                }
            }
            userList.addAll(userdata)
            filteredList.addAll(userdata)

            requireActivity().runOnUiThread {
                hindeSpinner()
                userListAdapter?.notifyDataSetChanged()
            }

        }
    }


    private fun hasNote(login: String): Boolean {
        var hasNote = false
        noteList.forEach {
            if (login == it.login) {
                hasNote = true
            }
        }
        return hasNote
    }

    fun getUserNote() {
        MainActivity.appDb.getDao().getUsersNote().observe(requireActivity(), Observer {
            if (!it.isNullOrEmpty()) {
                noteList.clear()
                noteList.addAll(it)
            } else {
                Log.e("ccc", "empty note")
            }

            hindeSpinner()
            if (mWifi!!.isConnected) {
                saveAllUser()
            } else {
                getAllUsers()
            }

        })

    }


    companion object {

    }

    override fun eventSelected(position: Int) {
        editTextTextPersonName.hideKeyboard()

        val avatar = filteredList[position].avatar_url
        val login = filteredList[position].login
        val id = filteredList[position].id

        val bundle = Bundle()
        bundle.putString("avatar", avatar)
        bundle.putString("login", login)
        bundle.putInt("id", id)

        findNavController().navigate(R.id.action_userListFragment_to_userProfileFragent, bundle)

//        editTextTextPersonName.setText("")

    }

    fun showSpinner() {
        nDialog.setMessage("Loading..")
        nDialog.setCancelable(true)
        nDialog.show()

    }

    fun hindeSpinner() {
        nDialog.dismiss()
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}