package com.example.market.ui.feature.productScreen

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.market.R
import com.example.market.model.data.Comment
import com.example.market.model.data.Product
import com.example.market.ui.theme.Blue
import com.example.market.ui.theme.PriceBackground
import com.example.market.ui.theme.Shapes
import com.example.market.utils.MyScreens
import com.example.market.utils.NetworkChecker
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.viewmodel.getViewModel

@Composable
fun ProductScreen(productId: String) {

    val navigation = getNavController()
    val context = LocalContext.current
    val viewModel = getViewModel<ProductViewModel>()
    viewModel.loadDataFromLocalDatabaseAndServer(
        productId,
        NetworkChecker(context).isInternetConnected
    )

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 58.dp)
                .verticalScroll(rememberScrollState())
        ) {

            ProductScreenToolBar(
                "Detail"
                , viewModel.badgeNumber.value, {
                navigation.popBackStack()
            }, {

                if (NetworkChecker(context).isInternetConnected) {
                    navigation.navigate(MyScreens.CartScreen.route)
                } else {
                    Toast.makeText(
                        context,
                        "Please connect to the internet first",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })

            ProductBar(
                viewModel.dataProductById.value,
                viewModel.dataComments.value.reversed(),
                onCategoryClicked = { navigation.navigate(MyScreens.CategoryScreen.route + "/" + it) },
                onAddNewCommentClicked = {
                    viewModel.addNewComment(productId, it) { notification ->
                        Toast.makeText(context, notification, Toast.LENGTH_LONG).show()
                    }
                }
            )

        }

        AddToCartBar(
            viewModel.dataProductById.value.price,
            viewModel.showAnimationAddToCard.value
        ) {
            viewModel.addProductToCard(productId) {
                Toast.makeText(
                    context, it, Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

}

@Composable
fun ProductBar(
    data: Product,
    comments: List<Comment>,
    onCategoryClicked: (String) -> Unit,
    onAddNewCommentClicked: (String) -> Unit
) {

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        ProductDesign(data = data, onCategoryClicked = onCategoryClicked)

        Divider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier.padding(top = 14.dp, bottom = 14.dp)
        )

        ProductDetail(data, comments.size.toString())

        Divider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)
        )

        CommentsBar(data = comments) {
            onAddNewCommentClicked.invoke(it)
        }

    }

}

@Composable
fun CommentBody(data: Comment) {

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp)
            .background(Color.White),
        border = BorderStroke(2.dp, color = Color.LightGray),
        shape = Shapes.large
    ) {

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Text(
                    text = data.userEmail,
                    modifier = Modifier.padding(top = 6.dp),
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )

                Text(
                    text = data.text,
                    modifier = Modifier.padding(top = 10.dp, bottom = 6.dp),
                    style = TextStyle(fontSize = 14.sp)
                )

            }

        }
    }
}

@Composable
fun CommentsBar(data: List<Comment>, onAddNewCommentClicked: (String) -> Unit) {

    val showCommentDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (data.isNotEmpty()) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
        ) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Comments",
                    modifier = Modifier.padding(end = 108.dp),
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )



                TextButton(onClick = {
                    if (NetworkChecker(context).isInternetConnected) {

                        showCommentDialog.value = true

                    } else {
                        Toast.makeText(
                            context,
                            "if you want to add comment connect to the internet first",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }) {
                    Text(text = "Add New Comment", fontSize = 16.sp)
                }

            }

            data.forEach {
                CommentBody(data = it)
            }

        }

    } else {

        TextButton(onClick = {

            if (NetworkChecker(context).isInternetConnected) {
                showCommentDialog.value = true
            } else {
                Toast.makeText(
                    context,
                    "if you want to add comment connect to the internet first",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }) {

            Text(text = "Add New Comment", fontSize = 16.sp)

        }

    }

    if (showCommentDialog.value) {

        AddNewCommentDialog(
            onDismissClicked = { showCommentDialog.value = false },
            onOkClicked = { onAddNewCommentClicked.invoke(it) })

    }

}

@Composable
fun AddNewCommentDialog(onDismissClicked: () -> Unit, onOkClicked: (String) -> Unit) {

    val userComment = remember { mutableStateOf("") }
    val context = LocalContext.current

    Dialog(onDismissRequest = { onDismissClicked.invoke() }) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(Shapes.medium)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "Write Your Comment",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            MainTextFieldComment(userComment.value, "Write Something") {
                userComment.value = it
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { onDismissClicked.invoke() }) {
                    Text(text = "Cancel")
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(onClick = {

                    if (userComment.value.isNotEmpty() && userComment.value.isNotBlank()) {

                        if (NetworkChecker(context).isInternetConnected) {

                            onOkClicked.invoke(userComment.value)
                            onDismissClicked.invoke()

                        } else {

                            Toast.makeText(
                                context,
                                "Please Connect to internet first ", Toast.LENGTH_LONG
                            )
                                .show()

                        }

                    } else {
                        Toast.makeText(
                            context,
                            "Please Write Something",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }) {
                    Text(text = "Ok")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTextFieldComment(edtValue: String, hint: String, onValueChange: (String) -> Unit) {

    OutlinedTextField(
        label = { Text(hint) },
        value = edtValue,
        singleLine = true,
        onValueChange = onValueChange,
        placeholder = { Text(hint) },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 12.dp),
        shape = Shapes.medium,
        maxLines = 2
    )
}
// ----------------------------------------------------------------------

@Composable
fun ProductDetail(data: Product, commentNumber: String) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Image(
                    painter = painterResource(id = R.drawable.ic_details_comment),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )

                Text(
                    text = "$commentNumber Comments",
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(start = 6.dp)
                )

            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 10.dp)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.ic_details_material),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )

                Text(
                    text = data.material,
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(start = 6.dp)
                )

            }
            Row(verticalAlignment = Alignment.CenterVertically) {

                Image(
                    painter = painterResource(id = R.drawable.ic_details_sold),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )

                Text(
                    text = data.soldItem + " Sold",
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(start = 6.dp)
                )

            }

        }

        Surface(
            modifier = Modifier
                .clip(Shapes.large)
                .align(Alignment.Bottom),
            color = Blue
        ) {

            Text(
                text = data.tags,
                modifier = Modifier.padding(6.dp),
                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium)
            )

        }

    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreenToolBar(
    productName: String,
    badgeNumber: Int,
    onBackClicked: () -> Unit,
    onCartClicked: () -> Unit
) {

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { onBackClicked.invoke() }) {

                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)

            }
        },
        title = {
            Text(
                text = productName,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 25.dp),
                textAlign = TextAlign.Center
            )
        },
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


        }
    )

}

@Composable
fun AddToCartBar(
    price: String,
    isShowingAnimation: Boolean,
    onCardClick: () -> Unit
) {

    val configuration = LocalConfiguration.current
    val fraction =
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 0.15f else 0.1f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction),
        color = Color.White
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                onClick = { onCardClick.invoke() },
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clip(Shapes.small)
                    .size(182.dp, 45.dp)
            ) {

                if (isShowingAnimation) {
                    DotsTyping()
                } else {
                    Text(
                        text = "Add To Cart",
                        modifier = Modifier.padding(4.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

            }

            Surface(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clip(Shapes.small),
                color = PriceBackground
            ) {

                Text(
                    text = "$price Toman",
                    modifier = Modifier.padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = 6.dp,
                        bottom = 6.dp
                    ),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

            }

        }

    }

}

@Composable
fun ProductDesign(data: Product, onCategoryClicked: (String) -> Unit) {

    AsyncImage(
        model = data.imgUrl,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(Shapes.medium),
        contentScale = ContentScale.Crop
    )

    Text(
        modifier = Modifier.padding(top = 16.dp),
        text = data.name,
        style = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            textAlign = TextAlign.Start
        )
    )

    Text(
        modifier = Modifier.padding(top = 4.dp),
        text = data.detailText,
        style = TextStyle(
            fontSize = 16.sp,
            textAlign = TextAlign.Justify
        )
    )

    TextButton(onClick =
    { onCategoryClicked.invoke(data.category) }
    ) {

        Text(
            text = "#" + data.category,
            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium)
        )

    }

}

@Composable
fun DotsTyping() {

    val dotSize = 10.dp
    val delayUnit = 350
    val maxOffset = 10f

    @Composable
    fun Dot(
        offset: Float
    ) = Spacer(
        Modifier
            .size(dotSize)
            .offset(y = -offset.dp)
            .background(
                color = Color.White,
                shape = CircleShape
            )
            .padding(start = 8.dp, end = 8.dp)
    )

    val infiniteTransition = rememberInfiniteTransition()

    @Composable
    fun animateOffsetWithDelay(delay: Int) = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = delayUnit * 4
                0f at delay with LinearEasing
                maxOffset at delay + delayUnit with LinearEasing
                0f at delay + delayUnit * 2
            }
        )
    )

    val offset1 by animateOffsetWithDelay(0)
    val offset2 by animateOffsetWithDelay(delayUnit)
    val offset3 by animateOffsetWithDelay(delayUnit * 2)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = maxOffset.dp)
    ) {
        val spaceSize = 2.dp

        Dot(offset1)
        Spacer(Modifier.width(spaceSize))
        Dot(offset2)
        Spacer(Modifier.width(spaceSize))
        Dot(offset3)
    }
}