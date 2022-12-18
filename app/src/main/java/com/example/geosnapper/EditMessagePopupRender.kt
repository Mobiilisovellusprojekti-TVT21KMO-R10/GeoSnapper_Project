package com.example.geosnapper

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
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


    fun render(popupView: View, marker: Marker, ownPost: Boolean, removeMarker: (input: Marker) -> Unit) {
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
        val editButtons = popupView.findViewById<LinearLayout>(R.id.linearLayout2)

        userAvatar.setImageResource(R.drawable.test_avatar)
        userName.text = "setäSomuli"
        popupText.text = post.message

        if (ownPost) editButtons.visibility = View.VISIBLE
        deleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
                .setTitle(coloredText(R.string.delete_post, R.color.red))
                .setMessage(context.getString(R.string.are_you_sure))
                .setPositiveButton(coloredText(R.string.delete, R.color.red),
                    DialogInterface.OnClickListener { _, _ ->
                        if (Database().deleteMessage(post.postId)) {
                            removeMarker(marker)
                            popupWindow.dismiss()
                            Toast.makeText(context, R.string.delete_success, Toast.LENGTH_LONG).show()
                        }
                        else {
                            Toast.makeText(context, R.string.delete_failed, Toast.LENGTH_LONG).show()
                        }
                    }
                )
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { _, _ ->
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
                    Toast.makeText(context, R.string.post_update_success, Toast.LENGTH_LONG).show()
                    popupWindow.dismiss()
                } else {
                    Toast.makeText(context, R.string.post_update_failed, Toast.LENGTH_LONG).show()
                }
            }
        }

        if (post.tier == 3 && ownPost) upgradeButton.visibility = View.VISIBLE
        upgradeButton.setOnClickListener {
            val message = context.getString(R.string.upgrade_post)
            val builder = AlertDialog.Builder(context)
                .setTitle(coloredText(R.string.upgrade_post_header,R.color.green))
                .setMessage(SpannableString(message).apply {
                    setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.gold)), 21, 27, 0)
                    setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.red)), 28, message.length, 0)
                })
                .setPositiveButton(coloredText(R.string.buy,R.color.green),
                    DialogInterface.OnClickListener { _, _ ->
                        if (Database().updatePostsOneValue(post.postId, "tier", 1)) {
                            marker.setIcon(PostToMarkerClass().iconSelector(1, post.type))
                            post.tier = 1
                            upgradeButton.visibility = View.INVISIBLE
                            Toast.makeText(context,R.string.post_upgrade_success, Toast.LENGTH_LONG).show()
                        }
                        else {
                            Toast.makeText(context,R.string.post_upgrade_failed, Toast.LENGTH_LONG).show()
                        }
                    }
                )
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { _, _ ->
                    }
                )
            builder.create().show()
        }
    }

    private fun coloredText(text: Int, colorResource: Int): SpannableString {
        return SpannableString(context.getString(text)).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(context, colorResource)), 0, this.length, 0)
        }
    }
}