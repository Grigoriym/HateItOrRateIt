package com.grappim.hateitorrateit.utils.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.ui.graphics.vector.ImageVector

enum class PlatoIconType {
    Hate,
    Rate,
    ArrowBack,
    Edit,
    Delete,
    Camera,
    Image,
    Done,
    Close,
    ImageNotSupported,
    HighlightOff,
    CheckCircleOutline,
    Search,
    Cancel,
    Add,
    Home,
    Settings,
    PrivacyPolicy;

    val imageVector: ImageVector
        get() = when (this) {
            ArrowBack -> Icons.AutoMirrored.Filled.ArrowBack
            Edit -> Icons.Filled.Edit
            Delete -> Icons.Filled.Delete
            Camera -> Icons.Filled.Camera
            Image -> Icons.Filled.Image
            Done -> Icons.Filled.Done
            Close -> Icons.Filled.Close
            Hate -> Icons.Filled.ThumbDown
            Rate -> Icons.Filled.ThumbUp
            ImageNotSupported -> Icons.Filled.ImageNotSupported
            HighlightOff -> Icons.Filled.HighlightOff
            CheckCircleOutline -> Icons.Filled.CheckCircleOutline
            Search -> Icons.Filled.Search
            Cancel -> Icons.Filled.Cancel
            Add -> Icons.Filled.Add
            Home -> Icons.Filled.Home
            Settings -> Icons.Filled.Settings
            PrivacyPolicy -> Icons.Filled.Security
        }

    val testTag: String
        get() = this.imageVector.name
}
