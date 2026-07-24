package com.example.ui.components.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Dimens

/**
 * A premium empty state layout used across all list screens.
 * Provides visual guidance with icon, title, description, and prominent CTA.
 * Fully Material Design 3 compliant with proper spacing and accessibility.
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    secondaryAction: Pair<String, () -> Unit>? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.spaceLarge),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon with brand color
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(Dimens.spaceLarge))

        // Title with semantic heading role for accessibility
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .semantics { heading() }
        )

        Spacer(modifier = Modifier.height(Dimens.spaceSmall))

        // Description text
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        // Primary action button
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(Dimens.spaceExtraLarge))
            Button(
                onClick = onAction,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(Dimens.buttonHeight)
            ) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        // Secondary action (optional)
        if (secondaryAction != null && actionLabel != null) {
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
            androidx.compose.material3.OutlinedButton(
                onClick = secondaryAction.second,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(Dimens.buttonHeight)
            ) {
                Text(
                    text = secondaryAction.first,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
