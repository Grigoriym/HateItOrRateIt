package com.grappim.hateitorrateit.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.grappim.hateitorrateit.utils.FileData

@Composable
fun FileItemWidget(
    modifier: Modifier = Modifier,
    fileData: FileData,
    onFileClicked: (file: FileData) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onFileClicked(fileData)
            }
    ) {
        Card(
            modifier = Modifier
                .size(100.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize(),
                painter = rememberAsyncImagePainter(fileData.uri),
                contentScale = ContentScale.Crop,
                contentDescription = ""
            )
        }

        Column(
            modifier = Modifier
                .padding(start = 12.dp)
        ) {
            Text(
                text = "Name: ${fileData.name}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Size: ${fileData.sizeToDemonstrate}"
            )
            Text(text = "Extension: ${fileData.mimeTypeToDemonstrate}")
        }
    }
}