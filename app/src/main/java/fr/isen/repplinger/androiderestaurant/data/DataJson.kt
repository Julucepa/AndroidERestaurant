package fr.isen.repplinger.androiderestaurant.data

import com.google.gson.annotations.SerializedName

data class MenuItem(
    @SerializedName("id") val id: String,
    @SerializedName("name_fr") val nameFr: String,
    @SerializedName("name_en") val nameEn: String,
    @SerializedName("id_category") val idCategory: String,
    @SerializedName("categ_name_fr") val categNameFr: String,
    @SerializedName("categ_name_en") val categNameEn: String,
    @SerializedName("images") val images: List<String>,
    @SerializedName("ingredients") val ingredients: List<Ingredient>,
    @SerializedName("prices") val prices: List<Price>
)

data class Ingredient(
    @SerializedName("id") val id: String,
    @SerializedName("id_shop") val idShop: String,
    @SerializedName("name_fr") val nameFr: String,
    @SerializedName("name_en") val nameEn: String,
    @SerializedName("create_date") val createDate: String,
    @SerializedName("update_date") val updateDate: String,
    @SerializedName("id_pizza") val idPizza: String
)

data class Price(
    @SerializedName("id") val id: String,
    @SerializedName("id_pizza") val idPizza: String,
    @SerializedName("id_size") val idSize: String,
    @SerializedName("price") val price: String,
    @SerializedName("create_date") val createDate: String,
    @SerializedName("update_data") val updateDate: String,
    @SerializedName("size")val size: String
)

data class Category(
    @SerializedName("name_fr") val nameFr: String,
    @SerializedName("name_en") val nameEn: String,
    @SerializedName("items") val items: List<MenuItem>
)

data class DataJson(
    @SerializedName("data") val data: List<Category>
)