import android.util.Log
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

fun Throwable.logIt(message : String = "Exception"){
    Log.e("MindNodeErr", message, this)
}