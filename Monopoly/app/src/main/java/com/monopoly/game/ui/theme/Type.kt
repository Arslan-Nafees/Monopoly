package com.monopoly.game.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    h1 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        letterSpacing = (-1.5).sp
    ),
    h2 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    h3 = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        letterSpacing = 0.sp
    ),
    h4 = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        letterSpacing = 0.sp
    ),
    h5 = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        letterSpacing = 0.sp
    ),
    h6 = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        letterSpacing = 0.sp
    ),
    subtitle1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        letterSpacing = 0.15.sp
    ),
    subtitle2 = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.1.sp
    ),
    body1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    body2 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    ),
    button = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 1.25.sp
    ),
    caption = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp
    ),
    overline = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        letterSpacing = 1.5.sp
    )
) 