package com.example.geosnapper

import android.content.Context
import android.content.res.Resources.getSystem
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.card.MaterialCardView
import java.util.*

class CompoundMessageView @JvmOverloads constructor(
    context: Context, attr: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attr, defStyleAttr) {

    private val parentConstraint: ConstraintLayout by lazy { ConstraintLayout(context) }
    private val constraintSet: ConstraintSet by lazy { ConstraintSet() }
    private val linearLayout: LinearLayout by lazy { LinearLayout(context) }
    private val messageTextView: AppCompatTextView by lazy { AppCompatTextView(context) }
    private val messageTextView2: AppCompatTextView by lazy { AppCompatTextView(context) }
    private val messageTextView3: AppCompatTextView by lazy { AppCompatTextView(context) }

    var message: String = ""
    var created: String = ""
    var geoData: String = ""

    init {
        createViews()
    }

    private fun createViews() {
        updateCardView()
        addChildViews()
    }

    private fun updateCardView() {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        radius = 12f
        setPadding(32.dp, 32.dp, 32.dp, 32.dp)
        setCardBackgroundColor(Color.YELLOW)
    }

    private fun addChildViews() {
        //constraintSet.clone(parentConstraint)
        addParentConstraintView()
        addMessageTextViewToParent(messageTextView, message)
        addMessageTextViewToParent(messageTextView2, created)
        addMessageTextViewToParent(messageTextView3, geoData)
        constraintSet.applyTo(parentConstraint)
    }

    private fun addParentConstraintView() {
        /*parentConstraint.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )*/

        parentConstraint.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        addView(parentConstraint)
    }

    private fun addMessageTextViewToParent(msgView: AppCompatTextView, textValue: String) {
        msgView.apply {
            id = generateViewId()
            text = textValue
            textSize = 32.sp
        }
        parentConstraint.addView(msgView)
        setMessageConstraints()
    }

    private fun setMessageConstraints() {

        constraintSet.constrainWidth(messageTextView.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(messageTextView.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(
            messageTextView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constraintSet.connect(
            messageTextView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)

        constraintSet.constrainWidth(messageTextView2.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(messageTextView2.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(
            messageTextView2.id, ConstraintSet.TOP, messageTextView.id, ConstraintSet.TOP)
        constraintSet.connect(
            messageTextView2.id, ConstraintSet.START, messageTextView.id, ConstraintSet.END)
        constraintSet.connect(
            messageTextView2.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

        constraintSet.constrainWidth(messageTextView3.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(messageTextView3.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(
            messageTextView3.id, ConstraintSet.TOP, messageTextView.id, ConstraintSet.BOTTOM)
        constraintSet.connect(
            messageTextView3.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        constraintSet.connect(
            messageTextView3.id, ConstraintSet.START, messageTextView.id, ConstraintSet.START)
    }
}

val Int.dp: Int
    get() = this * getSystem().displayMetrics.density.toInt()

val Int.sp: Float
    get() = this / getSystem().displayMetrics.scaledDensity