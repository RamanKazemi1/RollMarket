package com.example.market.ui.feature.mainScreen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.AsyncImage
import com.example.market.R
import com.example.market.model.data.Ads
import com.example.market.model.data.CheckOut
import com.example.market.model.data.Product
import com.example.market.ui.theme.BackgroundMain
import com.example.market.ui.theme.Blue
import com.example.market.ui.theme.CardViewBackground
import com.example.market.ui.theme.MarketTheme
import com.example.market.ui.theme.Shapes
import com.example.market.utils.CATEGORY
import com.example.market.utils.MyScreens
import com.example.market.utils.NetworkChecker
import com.example.market.utils.PAYMENT_PENDING
import com.example.market.utils.PAYMENT_SUCCESS
import com.example.market.utils.TAGS
import com.example.market.utils.stylePrice
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.viewmodel.getViewModel
import org.koin.core.parameter.parametersOf
import kotlin.reflect.typeOf

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {

    MarketTheme {

        MainScreen()

    }

}


@Composable
fun MainScreen() {

    val context = LocalContext.current

    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(Color.White)

    val mainScreenViewModel =
        getViewModel<MainScreenViewModel>(parameters = { parametersOf(NetworkChecker(context).isInternetConnected) })
    val navigation = getNavController()

    if (NetworkChecker(context).isInternetConnected) {
        mainScreenViewModel.loadBadgeNumber()
    }

    if (mainScreenViewModel.getPurchaseResult() == PAYMENT_PENDING) {
        if (NetworkChecker(context).isInternetConnected) {
            mainScreenViewModel.getCheckOutData()
        }
    }

    if (!NetworkChecker(context).isInternetConnected) {
        navigation.navigate(MyScreens.NoInternetScreen.route)
    } else {

        Box {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp)
            ) {

                if (mainScreenViewModel.progressBar.value) {

                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Blue
                    )

                }

                ToolAppBar({
                    navigation.navigate(MyScreens.CartScreen.route)
                }, badgeNumber = mainScreenViewModel.badgeNumber.value, {
                    navigation.navigate(MyScreens.ProfileScreen.route)
                })

                CategoryBar(CATEGORY) {
                    navigation.navigate(MyScreens.CategoryScreen.route + "/" + it)
                }

                val dataProductViewModel = mainScreenViewModel.dataProduct
                val dataAdsViewModel = mainScreenViewModel.dataAds

                ProductLayoutManager(
                    tags = TAGS,
                    dataProduct = dataProductViewModel.value,
                    adsData = dataAdsViewModel.value
                ) {
                    navigation.navigate(MyScreens.ProductScreen.route + "/" + it)
                }

            }

            if (mainScreenViewModel.showResultPaymentDialog.value) {

                PaymentResultDialog(checkoutResult = mainScreenViewModel.checkOutData.value) {
                    mainScreenViewModel.showResultPaymentDialog.value = false
                }

            }

        }


    }


}

@Composable
private fun PaymentResultDialog(
    checkoutResult: CheckOut,
    onDismiss: () -> Unit
) {

    Dialog(onDismissRequest = onDismiss) {

        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            ),
            shape = Shapes.medium
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Payment Result",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Main Data
                if (checkoutResult.order?.status?.toInt() == PAYMENT_SUCCESS) {

                    AsyncImage(
                        model = R.drawable.success_anim,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(110.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = "Payment was successful!", style = TextStyle(fontSize = 16.sp))
                    Text(
                        text = "Purchase Amount: " + stylePrice(
                            (checkoutResult.order!!.amount).substring(
                                0,
                                (checkoutResult.order!!.amount).length - 1
                            )
                        )
                    )

                } else {

                    AsyncImage(
                        model = R.drawable.fail_anim,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(110.dp)
                            .padding(top = 6.dp, bottom = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = "Payment was not successful!", style = TextStyle(fontSize = 16.sp))
                    Text(
                        text = "Purchase Amount: " + stylePrice(
                            (checkoutResult.order!!.amount).substring(
                                0,
                                (checkoutResult.order.amount).length - 1
                            )
                        )
                    )

                }

                // Ok Button
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    TextButton(onClick = onDismiss) {
                        Text(text = "ok")
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolAppBar(onCartClicked: () -> Unit, badgeNumber: Int, onProfileClicked: () -> Unit) {

    TopAppBar(
        modifier = Modifier.background(Color.White),
        title = { Text(text = "Roll Market", fontWeight = FontWeight.Bold) },
        actions = {

            IconButton(
                onClick = { onCartClicked.invoke() }
            ) {

                if (badgeNumber == 0) {
                    Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
                } else {

                    BadgedBox(badge = {
                        Badge {
                            Text(text = badgeNumber.toString())
                        }
                    }) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
                    }

                }
            }

            IconButton(onClick = { onProfileClicked.invoke() }) {
                Icon(imageVector = Icons.Default.Person, contentDescription = null)
            }

        }
    )

}

// ----------------------------------------------------------------------------------------

@Composable
fun CategoryBar(categoryData: List<Pair<String, Int>>, onCategoryClicked: (String) -> Unit) {

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(top = 16.dp),
        contentPadding = PaddingValues(end = 16.dp)
    ) {

        items(categoryData.size) {
            CategoryItem(categoryData[it], onCategoryClicked)
        }

    }

}

@Composable
fun CategoryItem(categoryData: Pair<String, Int>, onCategoryClicked: (String) -> Unit) {

    Column(
        modifier = Modifier
            .padding(start = 16.dp)
            .clickable { onCategoryClicked.invoke(categoryData.first) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Surface(
            shape = Shapes.medium,
            color = CardViewBackground
        ) {

            Image(
                modifier = Modifier.padding(16.dp),
                painter = painterResource(id = categoryData.second),
                contentDescription = null
            )

        }

        Text(
            text = categoryData.first,
            color = Color.Gray,
            modifier = Modifier.padding(top = 3.dp)
        )

    }

}

// ----------------------------------------------------------------------------------------

@Composable
fun AdsLayout(ads: Ads, onItemClicked: (String) -> Unit) {

    AsyncImage(
        model = ads.imageURL,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 32.dp)
            .height(260.dp)
            .clickable { onItemClicked.invoke(ads.productId) }
            .clip(Shapes.medium),
        contentScale = ContentScale.Crop
    )

}

// ----------------------------------------------------------------------------------------

@Composable
fun ProductLayoutManager(
    tags: List<String>,
    dataProduct: List<Product>,
    adsData: List<Ads>,
    onItemClicked: (String) -> Unit
) {

    if (dataProduct.isNotEmpty()) {

        Column {

            tags.forEachIndexed { it, _ ->

                val dataWithThisTag = dataProduct.filter { product -> product.tags == tags[it] }
                ProductLayout(
                    tag = tags[it],
                    productData = dataWithThisTag.shuffled(),
                    onItemClicked
                )


                if (adsData.size >= 2)
                    if (it == 1 || it == 2)
                        AdsLayout(adsData[it - 1], onItemClicked)

            }

        }

    }

}

@Composable
fun ProductLayout(tag: String, productData: List<Product>, onItemClicked: (String) -> Unit) {

    Column(
        modifier = Modifier.padding(top = 32.dp)
    ) {

        Text(
            text = tag,
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )

        LazyRow(
            modifier = Modifier.padding(top = 16.dp),
            contentPadding = PaddingValues(end = 16.dp)
        ) {

            items(productData.size) { it ->
                ProductItem(productData[it], onItemClicked)
            }

        }

    }

}

@Composable
fun ProductItem(product: Product, onItemClicked: (String) -> Unit) {

    Card(
        modifier = Modifier
            .padding(start = 16.dp)
            .background(Color.White)
            .clickable { onItemClicked.invoke(product.productId) },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier.background(Color.White)
        ) {

            AsyncImage(
                model = product.imgUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(200.dp)
            )

            Column(
                modifier = Modifier.padding(top = 10.dp, start = 10.dp)
            ) {

                Text(
                    text = product.name,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                )

                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = product.price + " Toman",
                    style = TextStyle(fontSize = 14.sp)
                )

                Text(
                    modifier = Modifier.padding(bottom = 6.dp),
                    text = product.soldItem + " Sold",
                    style = TextStyle(color = Color.Gray, fontSize = 13.sp)
                )

            }

        }

    }

}
