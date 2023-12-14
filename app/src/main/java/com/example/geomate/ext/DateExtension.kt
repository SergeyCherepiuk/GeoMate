package com.example.geomate.ext

import java.util.Date

val moment = Date(Date().time - 1000)
val oneMinute = Date(Date().time - (60 * 1000))
val fifteenMinutes = Date(Date().time - (15 * 60 * 1000))
val oneHour = Date(Date().time - (60 * 60 * 1000))
val eightHours = Date(Date().time - (8 * 60 * 60 * 1000))
val yesterday = Date(Date().time - (24 * 60 * 60 * 1000))
val oneWeek = Date(Date().time - (7 * 24 * 60 * 60 * 1000))
val twoWeeks = Date(Date().time - (14 * 24 * 60 * 60 * 1000))
val oneMonth = Date(Date().time - (30L * 24 * 60 * 60 * 1000))
val threeMonths = Date(Date().time - (90L * 24 * 60 * 60 * 1000))
val oneYear = Date(Date().time - (365L * 24 * 60 * 60 * 1000))

// TODO: Move to string resources
fun Date.description(): String = when {
    this.after(moment) -> "A moment ago"
    this.after(oneMinute) -> "A minute ago"
    this.after(fifteenMinutes) -> "15 minutes ago"
    this.after(oneHour) -> "An hour ago"
    this.after(eightHours) -> "8 hours ago"
    this.after(yesterday) -> "A day ago"
    this.after(oneWeek) -> "A week ago"
    this.after(twoWeeks) -> "Two weeks ago"
    this.after(oneMonth) -> "A month ago"
    this.after(threeMonths) -> "3 months ago"
    this.after(oneYear) -> "A year ago"
    else -> "Long time ago"
}