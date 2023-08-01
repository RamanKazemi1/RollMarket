package com.example.market.ui.feature.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.market.R
import com.example.market.ui.feature.productScreen.MainTextFieldComment
import com.example.market.ui.theme.BackgroundMain
import com.example.market.ui.theme.Blue
import com.example.market.ui.theme.Shapes
import com.example.market.utils.MyScreens
import com.example.market.utils.styleTime
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.viewmodel.getViewModel

@Composable
fun ProfileScreen() {

    val context = LocalContext.current
    val navigation = getNavController()

    val profileViewModel = getViewModel<ProfileViewModel>()
    profileViewModel.loadProfileUser()

    Box {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ProfileToolBar() { navigation.popBackStack() }
            ProfileAnimation()

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 16.dp, bottom = 8.dp)
            ) {

                SectionProfile(subject = "Email", text = profileViewModel.email.value, null)
                SectionProfile(subject = "Address", text = profileViewModel.address.value) {
                    profileViewModel.showLocationDialog.value = true
                }
                SectionProfile(
                    subject = "Postal Code ",
                    text = profileViewModel.postalCode.value,
                ) {
                    profileViewModel.showLocationDialog.value = true
                }
                SectionProfile(
                    subject = "Login Time",
                    text = styleTime(profileViewModel.loginTime.value.toLong()),
                    null
                )

            }

            Button(
                onClick = {
                    Toast.makeText(
                        context,
                        "Hope to see you again :)",
                        Toast.LENGTH_SHORT
                    ).show()
                    profileViewModel.signOutUser()

                    navigation.navigate(MyScreens.MainScreen.route) {
                        popUpTo(MyScreens.MainScreen.route) {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 30.dp)
                    .clip(Shapes.small)
            ) {

                Text(
                    text = "Sign Out",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )

            }

        }

        if (profileViewModel.showLocationDialog.value) {

            AddLocationDialog(
                false,
                onDismissClicked = { profileViewModel.showLocationDialog.value = false },
                onSubmitClicked = { address, postalCode, _ ->
                    profileViewModel.saveUserLocation(address, postalCode)
                }
            )

        }

    }

}

@Composable
fun AddLocationDialog(
    showSaveLocation: Boolean,
    onDismissClicked: () -> Unit,
    onSubmitClicked: (String, String, Boolean) -> Unit
) {

    val context = LocalContext.current
    val checkBoxState = remember { mutableStateOf(false) }
    val userAddressState = remember { mutableStateOf("") }
    val userPostalCode = remember { mutableStateOf("") }
    val fraction = if (showSaveLocation) 0.695f else 0.6f

    Dialog(onDismissRequest = { onDismissClicked.invoke() }) {

        Card(
            modifier = Modifier.fillMaxHeight(fraction),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            shape = Shapes.medium
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {

                Text(
                    text = "Add Location Data",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(6.dp))

                MainTextFieldComment(
                    edtValue = userAddressState.value,
                    hint = "Your Address",
                    onValueChange = { userAddressState.value = it })

                MainTextFieldComment(
                    edtValue = userPostalCode.value,
                    hint = "Your PostalCode",
                    onValueChange = { userPostalCode.value = it })

                if (showSaveLocation) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, start = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Checkbox(
                            checked = checkBoxState.value,
                            onCheckedChange = { checkBoxState.value = it },
                        )

                        Text(text = "Save To Profile")

                    }

                }

                // Button " cancel ", " Ok "

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {

                    TextButton(
                        onClick = { onDismissClicked.invoke() },
                    ) {
                        Text(text = "Cancel", color = Color.Cyan)
                    }

                    TextButton(
                        onClick = {

                            if (
                                (userAddressState.value.isNotEmpty() || userAddressState.value.isNotBlank()) &&
                                (userPostalCode.value.isNotEmpty() || userPostalCode.value.isNotBlank())
                            ) {

                                onSubmitClicked.invoke(
                                    userAddressState.value,
                                    userPostalCode.value,
                                    checkBoxState.value
                                )
                                onDismissClicked.invoke()

                            } else {
                                Toast.makeText(
                                    context,
                                    "Please Write All Data", Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                        })
                    { Text(text = "Submit", color = Color.Cyan) }

                }

            }

        }

    }

}

@Composable
fun SectionProfile(
    subject: String,
    text: String,
    onCardClicked: (() -> Unit)?
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundMain)
            .clickable { onCardClicked?.invoke() }
            .clip(Shapes.medium)
            .padding(top = 6.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        border = BorderStroke(1.5.dp, Color.LightGray)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                text = subject,
                modifier = Modifier.padding(top = 2.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Blue
            )
            Text(
                text = text,
                modifier = Modifier.padding(top = 2.dp),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )

        }

    }

}

@Composable
fun ProfileAnimation() {

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.profile_anim)
    )

    LottieAnimation(
        modifier = Modifier
            .size(270.dp)
            .padding(top = 36.dp),
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileToolBar(onBackClicked: () -> Unit) {

    TopAppBar(

        navigationIcon = {

            IconButton(onClick = { onBackClicked.invoke() }) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )

            }

        },
        title = {
            Text(
                text = "My Profile",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 58.dp),
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    )

}
