package net.jmbreuer.unshackle

// import androidx.activity.enableEdgeToEdge
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.jmbreuer.unshackle.ui.theme.UnShackleTheme

class MainActivity : ComponentActivity() {
    internal lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("Unshackle.MainActivity", "onCreate")
        super.onCreate(savedInstanceState)
        router = Router(applicationContext)
        if (intent != null)
            onNewIntent(intent)
        // enableEdgeToEdge()
    }

    override fun onNewIntent(intent: Intent) {
        Log.i("Unshackle.MainActivity", "onNewIntent")
        super.onNewIntent(intent)
        val url = fromIntent(intent)
        setContent { Content(url, null) }
        
        if (url.startsWith("http")) {
            // the one and only sane description on how to share an image:
            // https://stackoverflow.com/a/30172792

            Log.i("Unshackle.MainActivity", "routing $url")
            CoroutineScope(Dispatchers.IO).launch {
                val image = router.handle(url)
                Log.i("Unshackle.MainActivity", "sharing $image")
                if (image != null) {
                    val i = Intent(Intent.ACTION_SEND)
                    i.setType("image/*")
                    i.putExtra(Intent.EXTRA_STREAM, image)
                    withContext(Dispatchers.Main) {
                        setContent { Content(url, image) }
                        startActivity(Intent.createChooser(i, "Share unshackled image"))
                    }
                }
            }
        }
    }

    @Composable
    fun Content(url: String, image: Uri?) {
        UnShackleTheme {
            val padding = 16.dp
            Column(modifier = Modifier.fillMaxSize(1f).padding(padding)) {
                Spacer(modifier = Modifier.padding(padding))
                Greeting(text = url)
                Spacer(modifier = Modifier.padding(padding))
                ImagePreview(image = image,
                    modifier = Modifier.fillMaxWidth(1f))
                Spacer(modifier = Modifier.fillMaxHeight(1f))
            }
        }
    }
}


fun fromIntent(intent: Intent): String {
    return when (intent.action) {
        Intent.ACTION_SEND ->
            if (intent.type == "text/plain") {
                intent.getStringExtra(Intent.EXTRA_TEXT)
            } else "<type ${intent.type} not supported>"

        Intent.ACTION_VIEW -> if (intent.scheme.orEmpty().startsWith("http")) {
            intent.dataString
        } else "<scheme ${intent.scheme} not supported>"

        else -> "<no matching intent>"
    }.toString() // TODO hualp
}

@Composable
fun Greeting(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
    )
}

@Composable
fun ImagePreview(image: Uri?, modifier: Modifier = Modifier) {
    AsyncImage(model = image, contentDescription = image.toString(), modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UnShackleTheme {
        Greeting("<preview>")
    }
}