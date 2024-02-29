package fr.isen.repplinger.androiderestaurant.utils

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import fr.isen.repplinger.androiderestaurant.data.BasketUser
import fr.isen.repplinger.androiderestaurant.data.Meal
import fr.isen.repplinger.androiderestaurant.data.MenuItem
import java.io.File

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
        basket.value = BasketUser(newBasket)

        basket.postValue(basket.value)

        val json: String = Gson().toJson(basket.value)

        file.writeText(json)
    }
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