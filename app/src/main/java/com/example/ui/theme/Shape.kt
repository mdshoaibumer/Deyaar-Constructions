package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Deyaar Constructions Shape System
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp), // Checkboxes, tooltips, small badges
    small = RoundedCornerShape(8.dp),      // Chips, small tags
    medium = RoundedCornerShape(12.dp),    // Buttons, text fields, standard cards, dialogs
    large = RoundedCornerShape(16.dp),     // Prominent dashboard cards, bottom sheets
    extraLarge = RoundedCornerShape(28.dp) // Floating Action Buttons (FABs)
)
