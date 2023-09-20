package com.example.codewithfriends.createamspeck

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.codewithfriends.createamspeck.ui.theme.CodeWithFriendsTheme
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockSelection

class TeamSpeak : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CodeWithFriendsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun clock(){

    val calendarState = rememberSheetState()


    Box(modifier = Modifier
        .fillMaxWidth()
        .height(500.dp)){

    }

}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting() {
    val calendarState = rememberSheetState()


        CalendarDialog(
            state = calendarState,
            config = CalendarConfig (
              monthSelection = true,
                yearSelection = true
            ),


            selection = CalendarSelection.Date {
                date ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Log.d("SelectDate", "$date")
                    }
            }
        )

    val clockState = rememberSheetState()
        ClockDialog(
            state = clockState,
            selection = ClockSelection.HoursMinutes {hours, minutes ->
                Log.d("SelevtedTime ", " $hours")
            }
        )

    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

        ) {
        Button(onClick = {
            calendarState.show()
        }) {
            Text(text = "Date Picker")
        }

        Button(onClick = {
            clockState.show()
        }) {
            Text(text = "Time Picker")
        }



    }
}

