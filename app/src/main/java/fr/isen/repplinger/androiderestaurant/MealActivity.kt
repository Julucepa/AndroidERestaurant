package fr.isen.repplinger.androiderestaurant

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import fr.isen.repplinger.androiderestaurant.ui.theme.AndroidERestaurantTheme
import java.io.File


class MealActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        val basket: MutableLiveData<BasketUser> = MutableLiveData(getInfoInFile(this))
        val meal: MenuItem = Gson().fromJson(intent.getSerializableExtra("meal").toString(), MenuItem::class.java)

        basket.observe(this) { basketUser ->
            setContent {
                AndroidERestaurantTheme {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Header(getString(R.string.title_application), basketUser)
                        MealBody(meal = meal, basket)
                    }
                }
            }
        }

        setContent {
            AndroidERestaurantTheme {
                Column(modifier = Modifier.fillMaxSize()) {
                    Header(getString(R.string.title_application), basket.value)
                    MealBody(meal = meal, basket)
                }
            }
        }
    }
}

@Composable
fun MealBody(meal: MenuItem, basket: MutableLiveData<BasketUser>) {
    val context = LocalContext.current
    Column(modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Carousel(meal)
        Text(
            text = meal.nameFr,
            modifier = Modifier.padding(10.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Ingrédients :",
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.Start)
                .padding(start = 5.dp)
        )
        meal.ingredients.forEach { ingredient ->
            Text(
                text = "- " + ingredient.nameFr,
                modifier = Modifier
                    .padding(5.dp)
                    .align(Alignment.Start)
                    .padding(start = 30.dp)
            )
        }
        var currentQuantity by remember { mutableStateOf(1) }
        QuantitySelector(initialQuantity = currentQuantity) { newQuantity ->
            currentQuantity = newQuantity
        }
        Button(
            onClick = {
                AlertDialog.Builder(context)
                    .setTitle("Ajout au Panier")
                    .setMessage("Le plat " + meal.nameFr + " a été ajouté au panier")
                    .show()
                sendInfoBasket(meal, currentQuantity, context, basket)
            },
            modifier = Modifier
                .padding(top = 25.dp)
        ) {
            Text(
                text = "Total " + meal.prices[0].price.toFloat() * currentQuantity + " €",
                modifier = Modifier
                    .padding(10.dp)
                    .padding(start = 5.dp),
            )
        }
    }

}

fun sendInfoBasket(meal: MenuItem, currentQuantity: Int, context: Context, basket: MutableLiveData<BasketUser>) {
    val filePath = "basketUser.json"

    val file = File(context.filesDir, filePath)

    if(!file.exists()) {
        file.createNewFile()
    }

    val tmp = basket.value

    if(tmp != null) {
        Log.d("InfoFile", "Add meal to file")
        val newBasket = tmp.basket.toMutableList()
        val mealBasket = newBasket.find { it.nameFr == meal.nameFr }
        if(mealBasket != null) {
            val id = newBasket.indexOf(mealBasket)
            newBasket[id].quantity = newBasket[id].quantity + currentQuantity
        } else {
            val dish = Meal(meal.images[0], meal.nameFr, meal.prices[0].price.toFloat(), currentQuantity)
            newBasket.add(dish)
        }

        basket.value = BasketUser(newBasket)

        basket.postValue(basket.value)

        val json: String = Gson().toJson(basket.value)

        file.writeText(json)
    } else {
        Log.d("InfoFile", "Add meal to file null")
        val dish = Meal(meal.images[0], meal.nameFr, meal.prices[0].price.toFloat(), currentQuantity)
        val newBasket: MutableList<Meal> = mutableListOf()
        newBasket.add(dish)
        basket.value = newBasket?.let { BasketUser(it) }

        basket.postValue(basket.value)

        val json: String = Gson().toJson(basket.value)

        file.writeText(json)
    }
}

@Composable
fun QuantitySelector(initialQuantity: Int, onQuantityChange: (Int) -> Unit) {
    var quantity by remember { mutableIntStateOf(initialQuantity) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp)
    ) {
        IconButton(
            onClick = { if (quantity > 1) { quantity-- }
            },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "-")
        }

        Text(
            text = quantity.toString(),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        IconButton(
            onClick = { quantity++ },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(imageVector = Icons.Filled.KeyboardArrowUp, contentDescription = "+")
        }
    }
    onQuantityChange(quantity)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Carousel(meal: MenuItem) {
    val pagerState = rememberPagerState(pageCount = {
        meal.images.size
    })

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { index ->
        DisplayImage(meal = meal, index = index)
    }
}