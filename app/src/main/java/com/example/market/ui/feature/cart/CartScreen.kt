package com.example.market.ui.feature.cart

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.market.R
import com.example.market.model.data.ProductX
import com.example.market.ui.feature.profile.AddLocationDialog
import com.example.market.ui.theme.Blue
import com.example.market.ui.theme.PriceBackground
import com.example.market.ui.theme.Shapes
import com.example.market.utils.MyScreens
import com.example.market.utils.NetworkChecker
import com.example.market.utils.PAYMENT_PENDING
import com.example.market.utils.stylePrice
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.viewmodel.getViewModel

@Composable
fun CartScreen() {

    val context = LocalContext.current
    val navigation = getNavController()
    val getDialogState = remember { mutableStateOf(false) }

    val cartViewModel = getViewModel<CartViewModel>()
    cartViewModel.loadUserCart()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 74.dp)
        ) {

            CartToolBar(
                onProfileClicked = { navigation.navigate(MyScreens.ProfileScreen.route) },
                onBackClicked = { navigation.popBackStack() }
            )

            if (cartViewModel.productList.value.isNotEmpty()) {

                CartList(
                    data = cartViewModel.productList.value,
                    isChangingNumber = cartViewModel.isChangingNumber.value,
                    onAddItemClicked = { cartViewModel.addItemToCart(it) },
                    onRemoveItemClicked = { cartViewModel.removeItemInCart(it) },
                    onItemClicked = { navigation.navigate(MyScreens.ProductScreen.route + "/" + it) }
                )

            } else {
                NoDataAnimation()
            }


        }

        PurchaseAll(cartViewModel.totalPrice.value.toString()) {

            if (cartViewModel.productList.value.isNotEmpty()) {

                val locationData = cartViewModel.getUserLocation()
                if (locationData.second == "click to add" || locationData.first == "click to add") {
                    getDialogState.value = true
                } else {

                    // payment ->
                    cartViewModel.purchaseCart(
                        locationData.first,
                        locationData.second
                    ) { success, link ->

                        if (success) {

                            Toast.makeText(
                                context,
                                "Payment using zarinPal", Toast.LENGTH_SHORT
                            )
                                .show()

                            cartViewModel.setPurchaseStatus(PAYMENT_PENDING)

                            val paymentLink = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                            context.startActivity(paymentLink)

                        } else {
                            Toast.makeText(
                                context,
                                "Payment hit an Error", Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    }

                }

            } else {
                Toast.makeText(context, "Please choose an item first", Toast.LENGTH_SHORT).show()
            }

        }

        if (getDialogState.value) {

            AddLocationDialog(
                showSaveLocation = true,
                onDismissClicked = { getDialogState.value = false },
                onSubmitClicked = { address , postalCode , isChecked ->

                    if (NetworkChecker(context).isInternetConnected) {

                        if (isChecked) {
                            cartViewModel.setUserLocation(address, postalCode)
                        }

                        // payment ->
                        cartViewModel.purchaseCart(
                            address,
                            postalCode
                        ) { success, link ->

                            if (success) {

                                Toast.makeText(
                                    context,
                                    "Payment using zarinPal", Toast.LENGTH_SHORT
                                )
                                    .show()

                                cartViewModel.setPurchaseStatus(PAYMENT_PENDING)

                                val paymentLink = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                                context.startActivity(paymentLink)

                            } else {
                                Toast.makeText(
                                    context,
                                    "Payment hit an Error", Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                        }

                    } else {
                        Toast.makeText(
                            context,
                            "Please connect to the internet",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            )

        }

    }
}

@Composable
fun PurchaseAll(
    totalPrice: String,
    onPurchaseClicked: () -> Unit
) {

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val fraction =
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 0.15f else 0.07f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Button(
                onClick = {

                    if (NetworkChecker(context).isInternetConnected) {
                        onPurchaseClicked.invoke()
                    } else {
                        Toast.makeText(context, "Please connect to internet", Toast.LENGTH_SHORT)
                            .show()
                    }

                },
                modifier = Modifier
                    .size(182.dp, 40.dp)
                    .clip(Shapes.large)
                    .padding(start = 16.dp)
                    .background(Blue)
            ) {

                Text(
                    text = "Let's Purchase",
                    modifier = Modifier.padding(2.dp),
                    color = Color.White,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium)
                )

            }

            Surface(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clip(Shapes.large),
                color = PriceBackground

            ) {

                Text(
                    text = "total : ${stylePrice(totalPrice)}",
                    modifier = Modifier.padding(2.dp),
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                    color = Color.Black
                )

            }

        }

    }

}

@Composable
fun NoDataAnimation() {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_data))
    LottieAnimation(composition = composition, iterations = LottieConstants.IterateForever)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartToolBar(
    onProfileClicked: () -> Unit,
    onBackClicked: () -> Unit
) {

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { onBackClicked.invoke() }) {

                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)

            }
        },
        title = {
            Text(
                text = "My Cart",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 23.dp),
                textAlign = TextAlign.Center
            )
        },
        actions = {

            IconButton(
                onClick = { onProfileClicked.invoke() }
            ) {

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )

            }


        }
    )

}

@Composable
fun CartList(
    data: List<ProductX>,
    isChangingNumber: Pair<String, Boolean>,
    onAddItemClicked: (String) -> Unit,
    onRemoveItemClicked: (String) -> Unit,
    onItemClicked: (String) -> Unit
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        items(data.size) {
            CartItem(
                data = data[it],
                isChangingNumber = isChangingNumber,
                onAddItemClicked = onAddItemClicked,
                onRemoveItemClicked = onRemoveItemClicked,
                onItemClicked = onItemClicked
            )
        }

    }

}

@Composable
fun CartItem(
    data: ProductX,
    isChangingNumber: Pair<String, Boolean>,
    onAddItemClicked: (String) -> Unit,
    onRemoveItemClicked: (String) -> Unit,
    onItemClicked: (String) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .background(Color.White)
            .clickable { onItemClicked.invoke(data.productId) },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        shape = Shapes.medium
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
                    modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 4.dp)
                ) {

                    Text(
                        text = data.name,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "From ${data.category} Group",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Text(
                        text = "Product authenticity guarantee",
                        modifier = Modifier.padding(top = 24.dp),
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Text(
                        text = "Available in stock to ship",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Surface(
                        modifier = Modifier
                            .padding(top = 18.dp, bottom = 6.dp)
                            .clip(Shapes.large),
                        color = PriceBackground
                    ) {

                        Text(
                            modifier = Modifier.padding(
                                top = 6.dp,
                                bottom = 6.dp,
                                start = 8.dp,
                                end = 8.dp
                            ),
                            text = stylePrice(
                                (data.price.toInt() * (data.quantity).toInt()).toString()
                            ),
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        )
                    }

                }

                Surface(
                    modifier = Modifier
                        .padding(bottom = 6.dp, end = 8.dp)
                        .align(Alignment.Bottom),
                    color = Color.White
                ) {

                    Card(
                        border = BorderStroke(2.dp, Blue),
                        modifier = Modifier.background(Color.White),
                        shape = Shapes.medium
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.background(Color.White)
                        ) {

                            if (data.quantity.toInt() == 1) {
                                IconButton(onClick = { onRemoveItemClicked.invoke(data.productId) }) {
                                    Icon(
                                        modifier = Modifier.padding(end = 4.dp, start = 4.dp),
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null
                                    )
                                }
                            } else {

                                IconButton(onClick = { onRemoveItemClicked.invoke(data.productId) }) {
                                    Icon(
                                        modifier = Modifier.padding(end = 4.dp, start = 4.dp),
                                        painter = painterResource(id = R.drawable.ic_minus),
                                        contentDescription = null
                                    )
                                }

                            }

                            if (isChangingNumber.first == data.productId && isChangingNumber.second) {

                                Text(
                                    text = "...",
                                    style = TextStyle(fontSize = 18.sp),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                            } else {

                                Text(
                                    text = data.quantity,
                                    style = TextStyle(fontSize = 18.sp),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )

                            }

                            IconButton(onClick = { onAddItemClicked.invoke(data.productId) }) {

                                Icon(
                                    imageVector = Icons.Default.Add,
                                    modifier = Modifier.padding(end = 4.dp, start = 4.dp),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
