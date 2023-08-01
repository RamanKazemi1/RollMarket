package com.example.market.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.market.ui.feature.signUp.SignUpScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.market.R
import com.example.market.di.myModule
import com.example.market.model.repo.TokenInMemory
import com.example.market.model.repo.user.UserRepository
import com.example.market.ui.feature.IntroScreen
import com.example.market.ui.feature.cart.CartScreen
import com.example.market.ui.feature.categoryScreen.CategoryScreen
import com.example.market.ui.feature.mainScreen.MainScreen
import com.example.market.ui.feature.productScreen.ProductScreen
import com.example.market.ui.feature.profile.ProfileScreen
import com.example.market.ui.feature.signIn.SignInScreen
import com.example.market.ui.theme.BackgroundMain
import com.example.market.ui.theme.Blue
import com.example.market.ui.theme.MarketTheme
import com.example.market.ui.theme.PriceBackground
import com.example.market.ui.theme.Shapes
import com.example.market.utils.KEY_CATEGORY_ARG
import com.example.market.utils.KEY_PRODUCT_ARG
import com.example.market.utils.MyScreens
import com.example.market.utils.NetworkChecker
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get
import dev.burnoo.cokoin.navigation.KoinNavHost
import dev.burnoo.cokoin.navigation.getNavController
import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        setContent {
            Koin(appDeclaration = {
                androidContext(this@MainActivity)
                modules(myModule)
            }) {

                MarketTheme {

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = BackgroundMain
                    ) {

                        val userRepository: UserRepository = get()
                        userRepository.loadToken()

                        MarketUi()
                    }

                }

            }

        }
    }
}

@Composable
fun MarketUi() {

    val navController = rememberNavController()
    KoinNavHost(navController = navController, startDestination = MyScreens.MainScreen.route) {

        composable(MyScreens.MainScreen.route) {

            if (TokenInMemory.token != null) {
                MainScreen()
            } else {
                IntroScreen()
            }

        }

        composable(
            route = MyScreens.ProductScreen.route + "/" + "{$KEY_PRODUCT_ARG}",
            arguments = listOf(navArgument(KEY_PRODUCT_ARG) {
                type = NavType.StringType
            })
        ) {

            ProductScreen(it.arguments!!.getString(KEY_PRODUCT_ARG, "null"))

        }

        composable(
            route = MyScreens.CategoryScreen.route + "/" + "{$KEY_CATEGORY_ARG}",
            arguments = listOf(navArgument(KEY_CATEGORY_ARG) {
                type = NavType.StringType
            })
        ) {
            CategoryScreen(it.arguments!!.getString(KEY_CATEGORY_ARG, "null"))
        }

        composable(MyScreens.ProfileScreen.route) {
            ProfileScreen()
        }

        composable(MyScreens.CartScreen.route) {
            CartScreen()
        }

        composable(MyScreens.SignUpScreen.route) {
            SignUpScreen()
        }

        composable(MyScreens.SignInScreen.route) {
            SignInScreen()
        }

        composable(MyScreens.NoInternetScreen.route) {
            NoInternetScreen()
        }

    }

}

@Composable
fun NoInternetScreen() {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val navigation = getNavController()
        val context = LocalContext.current

        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.no_internet_anim)
        )

        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever
        )

        Surface(
            modifier = Modifier
                .padding(end = 16.dp)
                .clickable {
                    if (NetworkChecker(context).isInternetConnected) {
                        navigation.navigate(MyScreens.MainScreen.route) {
                            popUpTo(MyScreens.NoInternetScreen.route) {
                                inclusive = true
                            }
                        }
                    } else {
                        Toast
                            .makeText(
                                context,
                                "Please connect to the internet", Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                }
                .clip(Shapes.large)
                .background(Blue),
            color = PriceBackground
        ) {

            Text(
                text = "Try again !",
                modifier = Modifier.padding(
                    start = 8.dp,
                    end = 8.dp,
                    top = 6.dp,
                    bottom = 6.dp
                ),
                color = Color.White ,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

        }

    }


}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    MarketTheme {


    }
}

