package fr.isen.repplinger.androiderestaurant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import fr.isen.repplinger.androiderestaurant.ui.theme.AndroidERestaurantTheme
import org.json.JSONObject
import org.json.JSONArray
import java.time.format.TextStyle
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import org.greenrobot.eventbus.EventBus
import java.io.File


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
    }

    override fun onDestroy() {
        Log.i("Destroy", "HomeActivity")
        super.onDestroy()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(text = "Hello,")
        Text(text = name)
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
    val context = LocalContext.current
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
fun TitleCategory(type: DishType): String {
    val context = LocalContext.current
    var title: String
    when(type) {
        DishType.STARTER -> {
            title = context.getString(R.string.button_starter)
        }
        DishType.MAIN -> {
            title = context.getString(R.string.button_main)
        }
        DishType.DESSERT -> {
            title = context.getString(R.string.button_dessert)
        }
    }

    return title
}

fun MealCategory(type: DishType, json: DataJson): Category {
    val recipes: Category
    when(type) {
        DishType.STARTER -> {
            recipes = json.data[0]
        }
        DishType.MAIN -> {
            recipes = json.data[1]
        }
        DishType.DESSERT -> {
            recipes = json.data[2]
        }
    }

    return recipes
}

@Composable
fun MainButton(type: DishType) {
    val context = LocalContext.current
    var title = TitleCategory(type = type)

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

fun getInfoInFile(context: Context): BasketUser? {
    val filePath = "basketUser.json"

    val file = File(context.filesDir, filePath)

    val basket: BasketUser? = if(file.exists()) {
        Log.d("InfoFile", "Read File")
        val json = file.readText()

        val tmp = Gson().fromJson(json, BasketUser::class.java)
        tmp
    } else {
        Log.d("InfoFile", "Create File")
        file.createNewFile()
        null
    }

    return basket
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidERestaurantTheme {
        Greeting("Android")
    }
}