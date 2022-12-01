package com.example.geosnapper

import android.content.Context
import android.content.res.Resources.getSystem
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.card.MaterialCardView
import com.google.api.Distribution
import java.util.*

class CompoundMessageView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var textview2 : TextView
    //private var textview3 : TextView
    //private var textview4 : TextView

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.view_compound_message, this, true)
        textview2 = view.findViewById(R.id.textView2)
        //textview3 = view.findViewById(R.id.textView3)
        //textview4 = view.findViewById(R.id.textView4)
    }

    fun setData(text1 : String, text2 : String, text3 : String){
        textview2.text = text1
        //textview3.text = text2
        //textview4.text = text3
    }
}