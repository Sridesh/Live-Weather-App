package com.liveweather.live_weather

data class ForecastDay(val date: String, val entries: MutableList<ForecastEntry>)