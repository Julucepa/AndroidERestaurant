package fr.isen.repplinger.androiderestaurant.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import fr.isen.repplinger.androiderestaurant.ui.theme.AndroidERestaurantTheme
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import fr.isen.repplinger.androiderestaurant.R
import fr.isen.repplinger.androiderestaurant.data.BasketUser
import fr.isen.repplinger.androiderestaurant.data.Category
import fr.isen.repplinger.androiderestaurant.data.DataJson
import fr.isen.repplinger.androiderestaurant.utils.getInfoInFile


enum class DishType {
    STARTER,
    MAIN,
    DESSERT
}

class MainActivity : ComponentActivity() {
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
                    Body()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        Log.i("Stop", "HomeActivity")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("Destroy", "HomeActivity")
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(title: String, basket: BasketUser?) {
    val context = LocalContext.current

    var number = 0
    basket?.basket?.forEach{
        number += it.quantity
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.LightGray)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(16.dp)
        )

        BadgedBox(
            badge = {
                Badge(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .align(Alignment.CenterStart)
                ) {
                    Text(number.toString())
                }
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 20.dp)
        ) {
            IconButton(
                onClick = {
                    val intent = Intent(context, BasketActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Basket")
            }
        }
    }
}

@Composable
fun Body() {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally)
    {
        MainButton(type = DishType.STARTER)
        MainButton(type = DishType.MAIN)
        MainButton(type = DishType.DESSERT)
    }
}

@Composable
fun titleCategory(type: DishType): String {
    val context = LocalContext.current
    val title: String = when(type) {
        DishType.STARTER -> {
            context.getString(R.string.button_starter)
        }
        DishType.MAIN -> {
            context.getString(R.string.button_main)
        }
        DishType.DESSERT -> {
            context.getString(R.string.button_dessert)
        }
    }

    return title
}

fun mealCategory(type: DishType, json: DataJson): Category {
    val recipes: Category = when(type) {
        DishType.STARTER -> {
            json.data[0]
        }
        DishType.MAIN -> {
            json.data[1]
        }
        DishType.DESSERT -> {
            json.data[2]
        }
    }

    return recipes
}

@Composable
fun MainButton(type: DishType) {
    val context = LocalContext.current
    val title = titleCategory(type = type)

    Button(onClick = {
        Toast.makeText(
            context,
            "Clicked on $title",
            Toast.LENGTH_SHORT
        ).show()
        val intent = Intent(context, CategoryActivity::class.java)
        intent.putExtra("category", type)
        context.startActivity(intent)
                     },
        modifier = Modifier
            .height(56.dp)
            .padding(10.dp)
    ) {
        Text(text = title)
    }
}