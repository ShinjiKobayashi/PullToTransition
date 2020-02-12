package kobashin.com.library.ptt.ext

import android.content.res.Resources

val Int.dp get() = (this * Resources.getSystem().displayMetrics.density).toInt()
