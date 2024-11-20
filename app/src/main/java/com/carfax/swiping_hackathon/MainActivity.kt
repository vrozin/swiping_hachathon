package com.carfax.swiping_hackathon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.carfax.swiping_hackathon.MainActivityViewModel.Companion.VEHICLES
import com.carfax.swiping_hackathon.ui.theme.SwipingHackathonTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val viewModel = MainActivityViewModel()

		enableEdgeToEdge()
		setContent {
			SwipingHackathonTheme {
				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					ScreenContent(innerPadding, viewModel)
				}
			}
		}
	}
}

@Composable
fun ScreenContent(
	paddingValues: PaddingValues = PaddingValues(16.dp),
	viewModel: MainActivityViewModel
) {
	val columnState = rememberLazyListState()

	Box(
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp),
		contentAlignment = Alignment.TopCenter
	) {
		LazyColumn(
			modifier = Modifier
				.padding(paddingValues)
				.fillMaxSize(),
			state = columnState,
			verticalArrangement = Arrangement.Top,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			items(viewModel.vehicleList.value) { vehicleToRender ->
				var shouldAnimateVisibility by remember { mutableStateOf(true) }

				AnimatedVisibility(
					visible = shouldAnimateVisibility,
					exit = slideOutVertically() + fadeOut()
				) {
					Column() {
						VehicleItem(
							vehicleName = vehicleToRender.ymm,
							onVehicleRemove = { extraTaskToRun ->
								viewModel.vehicleList.value = viewModel.vehicleList.value
									.filter { it.ymm != vehicleToRender.ymm }
								extraTaskToRun.invoke()
								shouldAnimateVisibility = false
							}
						)
						HorizontalDivider(thickness = 8.dp, color = Color.Transparent)
					}
				}
			}
		}
	}
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun VehicleItem(
	modifier: Modifier = Modifier,
	vehicleName: String = "Vehicle Name",
	onVehicleRemove: (extraTaskToRun: () -> Unit) -> Unit = {}
) {
	val cardHeight = 90.dp
	val cardHeightPx = with(LocalDensity.current) { cardHeight.toPx() }
	val swipeableState = rememberSwipeableState(SwipeState.NOT_SWIPED)
	val coroutineScope = rememberCoroutineScope()

	Box(
		modifier = Modifier
			.requiredHeight(cardHeight)
			.swipeable(
				state = swipeableState,
				anchors = mapOf(0f to SwipeState.NOT_SWIPED, -cardHeightPx to SwipeState.SWIPED),
				orientation = Orientation.Horizontal,
				thresholds = { _, _ -> FractionalThreshold(0.3f) }
			),
		contentAlignment = Alignment.Center
	) {
		// Delete button layout
		DeleteButtonLayout(
			modifier = Modifier
				.align(Alignment.CenterEnd),
			size = cardHeight,
			onDeleteClick = {
				onVehicleRemove {
					coroutineScope.launch {
						swipeableState.snapTo(SwipeState.NOT_SWIPED)
					}
				}
			}
		)

		// Vehicle info inside the card
		Card(
			modifier = modifier
				.fillMaxWidth()
				.heightIn(min = cardHeight)
				.offset {
					IntOffset(x = swipeableState.offset.value.toInt(), y = 0)
				},
			colors = CardDefaults.cardColors(containerColor = Color(0xFFADD8E6)),
			elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 8.dp),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				// Image
				Box() {
					Image(
						modifier = Modifier
							.widthIn(min = 50.dp)
							.clip(CircleShape),
						contentDescription = "Vehicle Image",
						contentScale = ContentScale.Fit,
						painter = painterResource(id = R.drawable.baseline_directions_car_24)
					)
				}

				// vehicle info
				Column(modifier = Modifier.weight(1f)) {
					Text(vehicleName)
				}
			}
		}


	}
}

@Preview
@Composable
fun DeleteButtonLayout(
	modifier: Modifier = Modifier,
	size: Dp = 90.dp,
	onDeleteClick: () -> Unit = {}
) {
	Surface(
		modifier = modifier.requiredSize(width = size + 20.dp, height = size),
		shape = RoundedCornerShape(15.dp),
		color = Color.Red.copy(alpha = 0.7f),
	) {
		Box(contentAlignment = Alignment.Center) {
			IconButton(
				onClick = onDeleteClick,
				modifier = Modifier.sizeIn(minWidth = 48.dp),
				content = {
					Image(
						painter = painterResource(id = R.drawable.baseline_delete_24),
						contentDescription = "Delete",
						modifier = Modifier.sizeIn(minWidth = 24.dp)
					)
				}
			)
		}
	}


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
	SwipingHackathonTheme {
		ScreenContent(viewModel = MainActivityViewModel())
	}
}

enum class SwipeState { NOT_SWIPED, SWIPED }


