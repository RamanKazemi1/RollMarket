package com.example.market.ui.feature.categoryScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.market.model.data.Product
import com.example.market.ui.feature.mainScreen.CategoryItem
import com.example.market.ui.theme.Blue
import com.example.market.ui.theme.Shapes
import com.example.market.utils.MyScreens
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.navigation.getNavViewModel
import dev.burnoo.cokoin.viewmodel.getViewModel

@Composable
fun CategoryScreen(categoryName: String) {

    val categoryViewModel = getViewModel<CategoryViewModel>()
    categoryViewModel.getProductsByCategoryName(categoryName)

    val navigation = getNavController()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TopToolBar(categoryName)

        val dataProductCategory = categoryViewModel.data_product_by_category
        CategoryList(data = dataProductCategory.value) {

            navigation.navigate(MyScreens.ProductScreen.route + "/" + it)

        }

    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopToolBar(categoryName: String) {

    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 135.dp),
        title = {
            Text(
                modifier = Modifier
                    .padding(4.dp),
                text = categoryName,
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                color = Color.Black
            )
        }
    )

}

@Composable
fun CategoryList(data: List<Product>, onItemClicked: (String) -> Unit) {

    LazyColumn() {

        items(data.size) {

            CategoryItemList(data[it], onItemClicked)

        }

    }

}

@Composable
fun CategoryItemList(data: Product, onItemClicked: (String) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .clip(Shapes.large)
            .background(Color.White)
            .clickable { onItemClicked.invoke(data.productId) },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {

        Column(
            modifier = Modifier.background(Color.White)
        ) {

            AsyncImage(
                model = data.imgUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column(
                    modifier = Modifier.padding(6.dp)
                ) {

                    Text(
                        text = data.name,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = data.price + " Tomans",
                        modifier = Modifier.padding(top = 4.dp),
                        style = TextStyle(
                            fontSize = 14.sp
                        )
                    )

                }

                Surface(
                    modifier = Modifier
                        .padding(bottom = 4.dp, end = 4.dp)
                        .clip(Shapes.small)
                        .align(Alignment.Bottom),
                    color = Blue
                ) {

                    Text(
                        text = data.soldItem + " Sold",
                        modifier = Modifier.padding(6.dp),
                        style = TextStyle(fontSize = 14.sp),
                        color = Color.White
                    )

                }

            }

        }

    }

}
