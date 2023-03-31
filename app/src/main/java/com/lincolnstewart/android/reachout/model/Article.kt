package com.lincolnstewart.android.reachout.model

import java.util.UUID

data class Article(
    val id: UUID,
    val title: String,
    val link: String,
//    val image: ImageBitmap
)
