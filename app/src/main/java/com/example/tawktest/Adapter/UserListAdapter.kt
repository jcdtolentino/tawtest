package com.example.tawktest.Adapter

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tawktest.DataModels.UserModel
import com.example.tawktest.R


class UserListAdapter(
    val mContext: Context,
    var userList: List<UserModel>,
    private val eventOnClickInterface: EventOnClickInterface
) : RecyclerView.Adapter<UserAdapterViewHolder>() {

    interface EventOnClickInterface {
        fun eventSelected(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapterViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.cardview_user, parent, false)

        return UserAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserAdapterViewHolder, position: Int) {
        val userItem = userList[position]
        holder.userLayout.setOnClickListener { eventOnClickInterface.eventSelected(position) }

        val original = floatArrayOf(
            1f, 0f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ) /// turns color to its original form

        if (position != 0) {
            if (position % 3 == 0) {
                    val colorTransform = floatArrayOf(
                    -1f, 0f, 0f, 0f, 255f,
                    0f, -1f, 0f, 0f, 255f,
                    0f, 0f, -1f, 0f, 255f,
                    0f, 0f, 0f, 1f, 0f
                )/// turns color to inverted
                val filter = ColorMatrixColorFilter(colorTransform)
                holder.userPic.colorFilter = filter
            } else {
                val filter = ColorMatrixColorFilter(original)
                holder.userPic.colorFilter = filter
            }

        } else {

            val filter = ColorMatrixColorFilter(original)
            holder.userPic.colorFilter = filter
        }


        Glide.with(mContext)
            .load(userItem.avatar_url)
            .error(R.mipmap.ic_launcher)
            .apply(RequestOptions.circleCropTransform())
            .into(holder.userPic)



        holder.userDetail.text = userItem.node_id
        holder.userLogin.text = userItem.login

        if (userItem.haveNote) {
            holder.userNote.visibility = View.VISIBLE
        } else {
            holder.userNote.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun getItemId(position: Int): Long {
        return userList[position].id.toLong()
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }
}

class UserAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var userLayout: LinearLayout = itemView.findViewById(R.id.user_layout)
    var userPic: ImageView = itemView.findViewById(R.id.user_pic)
    var userNote: ImageView = itemView.findViewById(R.id.user_note)
    var userDetail: TextView = itemView.findViewById(R.id.user_detail)
    var userLogin: TextView = itemView.findViewById(R.id.user_login)

}