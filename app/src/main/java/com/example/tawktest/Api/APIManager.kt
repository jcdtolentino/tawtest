package com.example.tawktest.Api

import android.R
import android.R.attr.data
import android.annotation.SuppressLint
import android.util.Log
import com.example.tawktest.DataModels.UserDetailModel
import com.example.tawktest.DataModels.UserModel
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response


class APIManager {

    companion object factory {
        fun create(): APIManager = APIManager()
    }

    @SuppressLint("CheckResult")
    fun  getUserList(next: (code: Int, userModel: List<UserModel>?, errorBody: ResponseBody?) -> Unit) {
        val result = API.create().getUserList()

        result.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.code() == 200) {
                    if (it.body().isNullOrEmpty()) {
                        next(it.code(), null, it.errorBody())
                    } else {
                        next(it.code(), it.body(), it.errorBody())
                    }
                } else {
                    next(it.code(), null, it.errorBody())
                }
            }, {err ->
               Log.e("errorTag", "error: ${err.message}")
            })

    }

    @SuppressLint("CheckResult")
    fun  getUserDetails(user: String, next: (code: Int, userModel: UserDetailModel?, errorBody: ResponseBody?) -> Unit) {
        val result = API.create().getUserDetail(user)

        result.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.code() == 200) {
                    if (it.body() != null) {
                        next(it.code(), it.body(), it.errorBody())
                    } else {
                        next(it.code(), null, it.errorBody())
                    }
                } else {
                    next(it.code(), null, it.errorBody())
                }
            }, {err ->
                Log.e("errorTag", "error: ${err.message}")
            })

    }

}

