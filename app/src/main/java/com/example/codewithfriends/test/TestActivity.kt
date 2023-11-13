package com.example.codewithfriends.test

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData


class TestActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    )
                {
                    val context = LocalContext.current
                    LazyColumn(content = {
                        items(2) { item ->
                            when (item) {
                                0 -> {

                                    Box(
                                        modifier = Modifier
                                            .padding(it)
                                            .fillMaxWidth()
                                    ) {
                                        Spacer(modifier = Modifier.height(20.dp))
                                            SimpleDonutChart(context)
                                    }
                                }
                                1 -> {

                                   // MultipleSmallDonutCharts(context)
                                }
                            }
                        }
                    })
                }

        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun SimpleDonutChart(context: Context) {
        var selectedLabelText  by remember { mutableStateOf("") }
        val accessibilitySheetState =
            rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
        val scope = rememberCoroutineScope()

        // Новые данные с двумя секторами


        val donutChartData = PieChartData(
            slices = listOf(
                PieChartData.Slice("over", 50f, Color(0xFF00FF05)),
                PieChartData.Slice("delete", 50f, Color(0xFFFF1111)),

                ),
            plotType = PlotType.Donut
        )




        val pieChartConfig =
            PieChartConfig(
                labelVisible = true,
                strokeWidth = 120f,
                labelColor = Color.Black,
                activeSliceAlpha = .9f,
                isEllipsizeEnabled = true,
                labelTypeface = Typeface.defaultFromStyle(Typeface.BOLD),
                isAnimationEnable = true,
                chartPadding = 25,
                labelFontSize = 42.sp,
            )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(550.dp)
        ) {
            DonutPieChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),

                donutChartData, // Передаем новые данные
                pieChartConfig
            ) { slice ->
                // Обновляем выбранный текст при выборе сегмента
                selectedLabelText = slice.label

            }
            // Отображаем выбранный текст внизу

            Text(

                text = " $selectedLabelText",
                 fontSize = 24.sp,
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }


}

/**
 * Simple donut chart
 *
 * @param context
 */




/*

@ExperimentalMaterialApi
@Composable
private fun SimpleDonutChart(context: Context) {
    val accessibilitySheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val data = DataUtils.getDonutChartData()
    // Sum of all the values
    val sumOfValues = data.totalLength

    // Calculate each proportion value
    val proportions = data.slices.proportion(sumOfValues)
    val pieChartConfig =
        PieChartConfig(
            labelVisible = true,
            strokeWidth = 120f,
            labelColor = Color.Black,
            activeSliceAlpha = .9f,
            isEllipsizeEnabled = true,
            labelTypeface = Typeface.defaultFromStyle(Typeface.BOLD),
            isAnimationEnable = true,
            chartPadding = 25,
            labelFontSize = 42.sp,
        )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
    ) {

        DonutPieChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            data,
            pieChartConfig
        ) { slice ->
            Toast.makeText(context, slice.label, Toast.LENGTH_SHORT).show()
        }
    }
}
*/





data class PieChartData(
    val slices: List<Slice>,
    val plotType: PlotType
) {
    data class Slice(
        val label: String,
        val value: Float,
        val color: Color,
        val sliceDescription: (Int) -> String = { it.toString() }
    )
}