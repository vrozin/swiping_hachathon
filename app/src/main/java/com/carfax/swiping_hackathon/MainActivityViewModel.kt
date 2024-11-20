package com.carfax.swiping_hackathon

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class Vehicle(val ymm: String)

class MainActivityViewModel() : ViewModel() {
	companion object {
		internal val VEHICLES = listOf(
			Vehicle("2019 Toyota Camry"),
			Vehicle("2019 Honda Accord"),
			Vehicle("2019 Ford F-150"),
			Vehicle("2019 Chevrolet Silverado"),
			Vehicle("2019 Toyota RAV4"),
			Vehicle("2019 Honda CR-V"),
			Vehicle("2019 Ford Escape"),
			Vehicle("2019 Chevrolet Equinox"),
			Vehicle("2019 Toyota Corolla"),
			Vehicle("2019 Honda Civic"),
			Vehicle("2019 Ford Explorer"),
			Vehicle("2019 Chevrolet Malibu"),
			Vehicle("2019 Toyota Highlander"),
			Vehicle("2019 Honda Pilot"),
			Vehicle("2019 Ford Edge"),
		)
	}

	internal val vehicleList = mutableStateOf(VEHICLES)

}