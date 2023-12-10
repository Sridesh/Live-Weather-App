package com.liveweather.live_weather

import java.sql.Date

data class ForecastEntry(val time: String, val temperature: Double, val humidity: Int, val dates: String, val icon: String, val mainn: String)