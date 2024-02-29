package fr.isen.repplinger.androiderestaurant

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.gson.Gson
import fr.isen.repplinger.androiderestaurant.ui.theme.AndroidERestaurantTheme
import java.io.File

class BasketActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        val basket: MutableLiveData<BasketUser> = MutableLiveData(getInfoInFile(this))

        basket.observe(this) { basketUser ->
            setContent {
                AndroidERestaurantTheme {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Header(title = getString(R.string.title_application), basket = basketUser)
                        BodyBasket(basket)
                    }
                }
            }
        }
    }
}

@Composable
fun BodyBasket(basket: MutableLiveData<BasketUser>) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(
            text = "Basket",
            modifier = Modifier.padding(10.dp),
            fontWeight = FontWeight.Bold
        )
    }
    LazyColumn() {
        basket.value?.let {
            items(it.basket) { meal ->
                OutlinedCard {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.width(50.dp)) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(meal.image)
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(R.drawable.ic_launcher_background),
                                contentDescription = "meal description",
                                error = painterResource(R.drawable.ic_launcher_background)
                            )
                        }

                        Text(
                            text = meal.nameFr,
                            modifier = Modifier
                                .padding(10.dp)
                                .width(200.dp),
                            softWrap = true
                        )

                        Text(
                            text = "X" + meal.quantity.toString(),
                            modifier = Modifier.padding(10.dp),
                            softWrap = true
                        )

                        IconButton(
                            onClick = {
                                removeInfoBasket(context, basket, meal)
                            },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Basket",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Button(
            onClick = {
                AlertDialog.Builder(context)
                    .setTitle("Commande envoyé")
                    .setMessage("La commande a été envoyer")
                    .show()
                sendCommandBasket(context, basket)
            },
            modifier = Modifier
                .padding(top = 25.dp)
        ) {
            Text(
                text = "Commander ",
                modifier = Modifier
                    .padding(10.dp)
                    .padding(start = 5.dp),
            )
        }
    }
}

fun sendCommandBasket(context: Context, basket: MutableLiveData<BasketUser>) {
    val filePath = "basketUser.json"

    val file = File(context.filesDir, filePath)

    if(!file.exists()) {
        file.createNewFile()
    }

    basket.postValue(null)

    val json: String = Gson().toJson(basket.value)

    file.writeText(json)
}

fun removeInfoBasket(context: Context, basket: MutableLiveData<BasketUser>, meal: Meal) {
    val filePath = "basketUser.json"

    val file = File(context.filesDir, filePath)

    if(!file.exists()) {
        file.createNewFile()
    }

    val tmp = basket.value

    tmp?.let {
        val newBasket = it.basket.toMutableList()
        newBasket.removeIf { it.nameFr == meal.nameFr }

        basket.value = BasketUser(newBasket)

        basket.postValue(basket.value)

        val json: String = Gson().toJson(basket.value)

        file.writeText(json)
    }
}