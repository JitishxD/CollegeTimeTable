package me.jitish.collegetimetable.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitScreen
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.material.icons.outlined.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import me.jitish.collegetimetable.data.ClassInfo
import me.jitish.collegetimetable.data.TimetableData
import me.jitish.collegetimetable.ui.theme.AppTextStrongLight
import me.jitish.collegetimetable.ui.theme.AppSurfaceLight
import me.jitish.collegetimetable.ui.theme.AppBlue
import me.jitish.collegetimetable.ui.theme.AppBlueContainerDark
import me.jitish.collegetimetable.ui.theme.AppOutlineDark
import me.jitish.collegetimetable.ui.theme.AppSurfaceDark
import me.jitish.collegetimetable.ui.theme.AppTextStrongDark
import me.jitish.collegetimetable.ui.theme.AppOutlineLight
import me.jitish.collegetimetable.ui.theme.AppGreenContainer
import me.jitish.collegetimetable.ui.theme.AppGreenContainerDark
import me.jitish.collegetimetable.utils.COLLEGE_SLOT_COLUMNS
import me.jitish.collegetimetable.utils.COLLEGE_SLOT_GRID

private val DayColumnWidth = 72.dp
private val SlotColumnWidth = 148.dp
private val HeaderHeight = 54.dp
private val RowHeight = 128.dp
private val GridContentWidth = DayColumnWidth + SlotColumnWidth * COLLEGE_SLOT_COLUMNS.size
private val GridContentHeight = HeaderHeight + RowHeight * COLLEGE_SLOT_GRID.size
private const val DefaultGridScale = 0.8f
private const val MaxGridScale = 1.8f
private const val MinGridTextScale = 0.72f
private const val MaxGridTextScale = 1.12f
private const val GridZoomStep = 1.18f

@Composable
fun CollegeTimetableGrid(
    timetable: TimetableData,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    onSlotClick: (String, ClassInfo?) -> Unit = { _, _ -> }
) {
    val classesBySlot = timetable.timetable.values
        .flatten()
        .associateBy { it.slot.uppercase() }
    val headerColor = if (isDarkMode) AppBlueContainerDark else AppBlue
    val gridLineColor = if (isDarkMode) AppOutlineDark else AppOutlineLight
    val emptyCellColor = if (isDarkMode) AppSurfaceDark else AppSurfaceLight
    val emptyTextColor = if (isDarkMode) AppTextStrongDark.copy(alpha = 0.72f) else AppTextStrongLight
    val filledCellColor = if (isDarkMode) AppGreenContainerDark else AppGreenContainer
    val filledTextColor = if (isDarkMode) AppTextStrongDark else AppTextStrongLight

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val fitScale = if (maxWidth.value > 0f) {
            (maxWidth.value / GridContentWidth.value).coerceAtMost(1f)
        } else {
            1f
        }
        var scale by remember(fitScale) {
            mutableStateOf(DefaultGridScale.coerceIn(fitScale, MaxGridScale))
        }
        val horizontalScrollState = rememberScrollState()
        val baseDensity = LocalDensity.current
        val scaledDensity = remember(baseDensity, scale) {
            val textScale = scale.coerceIn(MinGridTextScale, MaxGridTextScale)
            Density(
                density = baseDensity.density * scale,
                fontScale = baseDensity.fontScale * (textScale / scale)
            )
        }

        LaunchedEffect(fitScale) {
            scale = scale.coerceIn(fitScale, MaxGridScale)
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            GridZoomControls(
                scale = scale,
                minScale = fitScale,
                onZoomOut = {
                    scale = (scale / GridZoomStep).coerceAtLeast(fitScale)
                },
                onFit = {
                    scale = fitScale
                },
                onZoomIn = {
                    scale = (scale * GridZoomStep).coerceAtMost(MaxGridScale)
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(GridContentHeight * scale)
                    .horizontalScroll(horizontalScrollState)
                    .pointerInput(fitScale) {
                        awaitEachGesture {
                            do {
                                val event = awaitPointerEvent()
                                val pressedPointers = event.changes.count { it.pressed }
                                if (pressedPointers > 1) {
                                    scale = (scale * event.calculateZoom()).coerceIn(fitScale, MaxGridScale)
                                    event.changes.forEach { it.consume() }
                                }
                            } while (event.changes.any { it.pressed })
                        }
                    }
            ) {
                CompositionLocalProvider(LocalDensity provides scaledDensity) {
                    CollegeTimetableGridContent(
                        classesBySlot = classesBySlot,
                        headerColor = headerColor,
                        gridLineColor = gridLineColor,
                        emptyCellColor = emptyCellColor,
                        emptyTextColor = emptyTextColor,
                        filledCellColor = filledCellColor,
                        filledTextColor = filledTextColor,
                        onSlotClick = onSlotClick
                    )
                }
            }
        }
    }
}

@Composable
private fun CollegeTimetableGridContent(
    classesBySlot: Map<String, ClassInfo>,
    headerColor: Color,
    gridLineColor: Color,
    emptyCellColor: Color,
    emptyTextColor: Color,
    filledCellColor: Color,
    filledTextColor: Color,
    onSlotClick: (String, ClassInfo?) -> Unit
) {
    Column {
        Row {
            GridCell(
                width = DayColumnWidth,
                height = HeaderHeight,
                backgroundColor = headerColor,
                borderColor = gridLineColor
            )

            COLLEGE_SLOT_COLUMNS.forEach { column ->
                GridCell(
                    width = SlotColumnWidth,
                    height = HeaderHeight,
                    backgroundColor = headerColor,
                    borderColor = gridLineColor
                ) {
                    Text(
                        text = column.label,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        COLLEGE_SLOT_GRID.forEach { row ->
            Row {
                GridCell(
                    width = DayColumnWidth,
                    height = RowHeight,
                    backgroundColor = headerColor,
                    borderColor = gridLineColor
                ) {
                    Text(
                        text = row.label,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                row.slotCodes.forEach { slotCode ->
                    val classInfo = classesBySlot[slotCode]
                    GridCell(
                        width = SlotColumnWidth,
                        height = RowHeight,
                        backgroundColor = if (classInfo == null) emptyCellColor else filledCellColor,
                        borderColor = gridLineColor,
                        modifier = Modifier.clickable {
                            onSlotClick(slotCode, classInfo)
                        }
                    ) {
                        if (classInfo == null) {
                            Text(
                                text = slotCode,
                                color = emptyTextColor,
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            FilledSlotCell(
                                classInfo = classInfo,
                                textColor = filledTextColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GridZoomControls(
    scale: Float,
    minScale: Float,
    onZoomOut: () -> Unit,
    onFit: () -> Unit,
    onZoomIn: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${(scale * 100).toInt()}%",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 4.dp)
        )
        IconButton(
            onClick = onZoomOut,
            enabled = scale > minScale + 0.01f,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ZoomOut,
                contentDescription = "Zoom out"
            )
        }
        IconButton(
            onClick = onFit,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.FitScreen,
                contentDescription = "Fit grid"
            )
        }
        IconButton(
            onClick = onZoomIn,
            enabled = scale < MaxGridScale - 0.01f,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ZoomIn,
                contentDescription = "Zoom in"
            )
        }
    }
}

@Composable
private fun GridCell(
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
    backgroundColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .width(width)
            .height(height),
        color = backgroundColor,
        border = BorderStroke(0.5.dp, borderColor)
    ) {
        Box(
            modifier = Modifier.padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            content?.invoke()
        }
    }
}

@Composable
private fun FilledSlotCell(
    classInfo: ClassInfo,
    textColor: Color
) {
    val courseCode = classInfo.courseCode
    val facultyName = classInfo.facultyName

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = classInfo.slot,
            color = textColor,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (courseCode.isNotBlank()) {
            Text(
                text = courseCode,
                color = textColor,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = classInfo.courseTitle,
            color = textColor.copy(alpha = 0.82f),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (facultyName.isNotBlank()) {
            Text(
                text = facultyName,
                color = textColor.copy(alpha = 0.72f),
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = classInfo.venue,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
