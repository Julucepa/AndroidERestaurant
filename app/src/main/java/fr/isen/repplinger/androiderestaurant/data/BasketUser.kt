package fr.isen.repplinger.androiderestaurant.data

data class BasketUser(
    var basket: List<Meal>
)

data class Meal(
    val image: String,
    val nameFr: String,
    val price: Float,
    var quantity: Int
)
