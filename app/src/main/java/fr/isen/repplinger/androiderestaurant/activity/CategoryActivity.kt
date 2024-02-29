package fr.isen.repplinger.androiderestaurant.activity

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
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import fr.isen.repplinger.androiderestaurant.R
import fr.isen.repplinger.androiderestaurant.data.Category
import fr.isen.repplinger.androiderestaurant.data.DataJson
import fr.isen.repplinger.androiderestaurant.data.MenuItem
import fr.isen.repplinger.androiderestaurant.ui.theme.AndroidERestaurantTheme
import fr.isen.repplinger.androiderestaurant.utils.getInfoInFile
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

    CategoryBody(type = type, recipes = recipes)
}

@Composable
fun CategoryBody(type: DishType, recipes: Category?) {
    val context = LocalContext.current
    val title = titleCategory(type = type)

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
            OutlinedCard(
                modifier= Modifier
                    .height(100.dp)
                    .padding(bottom = 10.dp)
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
                    Box(modifier = Modifier.width(75.dp).padding(start=10.dp)) {
                        DisplayImage(meal = meal, 0)
                    }

                    Text(
                        text = meal.nameFr,
                        modifier = Modifier
                            .padding(10.dp)
                            .width(220.dp),
                        softWrap = true
                    )

                    Text(
                        text = meal.prices[0].price + "€",
                        modifier = Modifier.padding(end = 5.dp),
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
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}