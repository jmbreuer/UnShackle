package net.jmbreuer.unshackle

// import androidx.activity.enableEdgeToEdge
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
        /*
        setContent {
            Content("<no data>")
        }
        */
    }

    override fun onNewIntent(intent: Intent) {
        Log.i("Unshackle.MainActivity", "onNewIntent")
        super.onNewIntent(intent)
        val url = fromIntent(intent)
        setContent {
            Content(url)
        }
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
                        startActivity(Intent.createChooser(i, "Share unshackled image"))
                    }
                }
            }
        }
    }

    @Composable
    fun Content(input: String) {
        UnShackleTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Greeting(
                    input = input,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}


fun fromIntent(intent: Intent): String {
    return when (intent.action) {
        Intent.ACTION_SEND ->
            if (intent.type == "text/plain") {
                intent.getStringExtra(Intent.EXTRA_TEXT)
            } else {
                "<type ${intent.type} not supported>"
            }

        Intent.ACTION_VIEW -> if (intent.scheme.orEmpty().startsWith("http")) {
            intent.dataString
        } else {
            "<scheme ${intent.scheme} not supported>"
        }

        else -> "<no matching intent>"
    }.toString() // TODO hualp
}

@Composable
fun Greeting(input: String, modifier: Modifier = Modifier) {
    Text(
        text = "Input: $input!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UnShackleTheme {
        Greeting("<preview>")
    }
}