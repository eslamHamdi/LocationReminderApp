package com.udacity.project4

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.FrameLayout
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.FragmentActivity

@VisibleForTesting
open class KoinTestActivity : FragmentActivity() {


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        val frameLayout = FrameLayout(this)
        frameLayout.id = R.id.container
        setContentView(frameLayout)
    }


}