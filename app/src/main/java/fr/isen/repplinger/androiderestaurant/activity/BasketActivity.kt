package fr.isen.repplinger.androiderestaurant.activity

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Scaffold
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
import fr.isen.repplinger.androiderestaurant.R
import fr.isen.repplinger.androiderestaurant.data.BasketUser
import fr.isen.repplinger.androiderestaurant.ui.theme.AndroidERestaurantTheme
import fr.isen.repplinger.androiderestaurant.utils.getInfoInFile
import fr.isen.repplinger.androiderestaurant.utils.removeInfoBasket
import fr.isen.repplinger.androiderestaurant.utils.sendCommandBasket

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
                    Scaffold (
                        topBar = {
                            Header(title = getString(R.string.title_application), basket = basketUser)
                        },
                        bottomBar = {
                            ButtonCommand(basket = basket)
                        },
                        content = { padding ->
                            BodyBasket(basket, padding)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BodyBasket(basket: MutableLiveData<BasketUser>, paddingValues: PaddingValues) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(
            text = "Panier",
            modifier = Modifier.padding(10.dp),
            fontWeight = FontWeight.Bold
        )

        LazyColumn(
        ) {
            basket.value?.let {
                items(it.basket) { meal ->
                    OutlinedCard (
                        modifier = Modifier
                            .height(100.dp)
                            .padding(bottom = 10.dp)
                    ){
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
                                text = "x" + meal.quantity.toString(),
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
    }
}

@Composable
fun ButtonCommand(basket: MutableLiveData<BasketUser>) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth(),
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