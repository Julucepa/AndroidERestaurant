package fr.isen.repplinger.androiderestaurant

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import fr.isen.repplinger.androiderestaurant.ui.theme.AndroidERestaurantTheme
import org.json.JSONObject

class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        val basket = getInfoInFile(this)

        setContent {
            AndroidERestaurantTheme {
                Column(modifier = Modifier.fillMaxSize()) {
                    Header(getString(R.string.title_application), basket)
                    CreateRequest(intent.getSerializableExtra("category") as DishType)
                }
            }
        }
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun CreateRequest(type: DishType) {
    val context = LocalContext.current
    val queue = Volley.newRequestQueue(context)
    val url = "http://test.api.catering.bluecodegames.com/menu"

    val params = JSONObject()
    params.put("id_shop", "1")

    var recipes: Category? by remember { mutableStateOf(null) }


    val jsonObjectRequest = JsonObjectRequest(
        Request.Method.POST, url, params,
        { response ->
            val json: DataJson = Gson().fromJson(response.toString(), DataJson::class.java)
            recipes = mealCategory(type = type, json = json)
        },
        { error ->
            println("Erreur : ${error.message}")
        })
    queue.add(jsonObjectRequest)

    CaterogyBody(type = type, recipes = recipes)
}

@Composable
fun CaterogyBody(type: DishType, recipes: Category?) {
    val context = LocalContext.current
    val title = TitleCategory(type = type)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(
            text = title,
            modifier = Modifier.padding(10.dp),
            fontWeight = FontWeight.Bold
        )
    }

    Column(modifier = Modifier
        .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        recipes?.items?.forEach { meal ->
            Divider(
                color = Color.Black,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier= Modifier
                    .height(100.dp)
                    .clickable {
                        val intent = Intent(context, MealActivity::class.java)
                        val json = Gson().toJson(meal)
                        intent.putExtra("meal", json)
                        context.startActivity(intent)
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.width(50.dp)) {
                        DisplayImage(meal = meal, 0)
                    }

                    Text(
                        text = meal.nameFr,
                        modifier = Modifier
                            .padding(10.dp)
                            .width(200.dp),
                        softWrap = true
                    )

                    Text(
                        text = meal.prices[0].price + "€",
                        modifier = Modifier.padding(10.dp),
                        softWrap = true
                    )
                }
            }
        }
    }
}

@Composable
fun DisplayImage(meal: MenuItem, index: Int) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(meal.images[index])
            .size(Size.ORIGINAL)
            .build()
    )

    if (painter.state is AsyncImagePainter.State.Success) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    AndroidERestaurantTheme {
        Greeting2("Android")
    }
}