package com.example.geosnapper

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.geosnapper.dataHandling.Database
import com.example.geosnapper.marker.PostToMarkerClass
import com.example.geosnapper.post.Post
import com.google.android.gms.maps.model.Marker

class EditMessagePopupRender(private val context: Context) {

    @SuppressLint("ResourceAsColor")
    fun render(popupView: View, marker: Marker, removeMarker: (input: Marker) -> Unit) {
        val wid = LinearLayout.LayoutParams.WRAP_CONTENT
        val high = LinearLayout.LayoutParams.WRAP_CONTENT
        val focus = true
        val popupWindow = PopupWindow(popupView, wid, high, focus)
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
        popupView.findViewById<CardView>(R.id.popup_window_view_with_border).alpha = 0f
        popupView.findViewById<CardView>(R.id.popup_window_view_with_border).animate().alpha(1f)
            .setDuration(600)
            .setInterpolator(DecelerateInterpolator())
            .start()

        val post = marker.tag as Post
        val closeButton = popupView.findViewById<Button>(R.id.btn_close)
        val deleteButton = popupView.findViewById<Button>(R.id.btn_delete)
        val updateButton = popupView.findViewById<Button>(R.id.btn_update)
        val upgradeButton = popupView.findViewById<Button>(R.id.btn_upgrade)
        val popupText = popupView.findViewById<TextView>(R.id.popup_window_text)
        val userAvatar = popupView.findViewById<ImageView>(R.id.userAvatar)
        val userName = popupView.findViewById<TextView>(R.id.userName)

        userAvatar.setImageResource(R.drawable.test_avatar)
        userName.text = "setäSomuli"
        popupText.text = post.message

        deleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
                .setTitle(SpannableString("DELETE POST").apply {
                    setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.red)), 0, this.length, 0)
                })
                .setMessage("Are you sure you want to delete the post?")
                .setPositiveButton(
                    SpannableString("Delete").apply {
                    setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.red)), 0, this.length, 0)
                },
                    DialogInterface.OnClickListener { dialog, id ->
                        if (Database().deleteMessage(post.postId)) {
                            removeMarker(marker)
                            popupWindow.dismiss()
                            Toast.makeText(context, "Post deteted successfully", Toast.LENGTH_LONG).show()
                        }
                        else {
                            Toast.makeText(context, "Post deletion failed", Toast.LENGTH_LONG).show()
                        }
                    }
                )
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                    }
                )
            builder.create().show()
        }

        closeButton.setOnClickListener {
            popupWindow.dismiss()
        }

        updateButton.setOnClickListener {
            val newMessage = popupText.text.toString()
            if (post.message != newMessage) {
                if (Database().updatePostsOneValue(post.postId, "message", newMessage)) {
                    post.message = newMessage
                    Toast.makeText(context, "Post updated successfully", Toast.LENGTH_LONG).show()
                    popupWindow.dismiss()
                } else {
                    Toast.makeText(context, "Post update failed", Toast.LENGTH_LONG).show()
                }
            }
        }

        if (post.tier == 3) upgradeButton.visibility = View.VISIBLE
        upgradeButton.setOnClickListener {
            val message: String = "Upgrade your post to TIER 1 only at 1€"
            val builder = AlertDialog.Builder(context)
                .setTitle(SpannableString("UPGRADE POST").apply {
                    setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.green)), 0, this.length, 0)
                })
                .setMessage(SpannableString(message).apply {
                    setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.gold)), 21, 27, 0)
                    setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.red)), 28, message.length, 0)
                })
                .setPositiveButton(
                    SpannableString("Buy").apply {
                    setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.green)), 0, this.length, 0)
                },
                    DialogInterface.OnClickListener { dialog, id ->
                        if (Database().updatePostsOneValue(post.postId, "tier", 1)) {
                            marker.setIcon(PostToMarkerClass().iconSelector(1, post.type))
                            post.tier = 1
                            Toast.makeText(context, "Post upgraded successfully", Toast.LENGTH_LONG).show()
                        }
                        else {
                            Toast.makeText(context, "Post upgrade failed", Toast.LENGTH_LONG).show()
                        }
                    }
                )
                .setNegativeButton(
                    SpannableString("Cancel").apply {
                    setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.red)), 0, this.length, 0)
                },
                    DialogInterface.OnClickListener { dialog, id ->
                    }
                )
            builder.create().show()
        }
    }
}