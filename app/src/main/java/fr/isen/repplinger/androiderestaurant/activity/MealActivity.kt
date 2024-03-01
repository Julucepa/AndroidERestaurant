package fr.isen.repplinger.androiderestaurant.activity

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import fr.isen.repplinger.androiderestaurant.R
import fr.isen.repplinger.androiderestaurant.data.BasketUser
import fr.isen.repplinger.androiderestaurant.data.MenuItem
import fr.isen.repplinger.androiderestaurant.ui.theme.AndroidERestaurantTheme
import fr.isen.repplinger.androiderestaurant.utils.getInfoInFile
import fr.isen.repplinger.androiderestaurant.utils.sendInfoBasket


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
                    Scaffold (
                        topBar = {
                            Header(getString(R.string.title_application), basketUser)
                        }
                    ){ padding ->
                        MealBody(meal = meal, basket, padding)
                    }
                }
            }
        }
    }
}

@Composable
fun MealBody(meal: MenuItem, basket: MutableLiveData<BasketUser>, padding: PaddingValues) {
    val context = LocalContext.current
    Column(modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(padding),
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