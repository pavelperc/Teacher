package com.example.teacher.utils

import android.content.res.Resources


// dp -> px
fun dip(dp: Int) = (dp * Resources.getSystem().displayMetrics.density).toInt()
// px -> dp
fun px(px: Int) = (px / Resources.getSystem().displayMetrics.density).toInt()