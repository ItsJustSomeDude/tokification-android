package net.itsjustsomedude.tokens.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LabeledButton(
    modifier: Modifier = Modifier,
    label: String,
    btnText: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = label, fontSize = 12.sp)
        Button(onClick = onClick) {
            Text(text = btnText)
        }
    }
}

@Composable
fun LabeledButtonSkeleton(
    modifier: Modifier = Modifier,
    label: String? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        if (label == null)
            Box(
                Modifier
                    .size(60.dp, 14.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .skeletonColors()
            )
        else
            Text(text = label, fontSize = 12.sp)

        Box(
            Modifier
                .padding(vertical = 4.dp)
                .size(100.dp, 40.dp)
                .clip(RoundedCornerShape(50))
                .skeletonColors()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLabeledButton() {
    LabeledButton(btnText = "Button", label = "label", onClick = {})
}

@Preview(showBackground = true)
@Composable
fun PreviewLabeledButtonLabeledSkeleton() {
    LabeledButtonSkeleton(label = "Test Label")
}

@Preview(showBackground = true)
@Composable
fun PreviewLabeledButtonSkeleton() {
    LabeledButtonSkeleton()
}
