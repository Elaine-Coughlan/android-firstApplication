package com.elaine.minerecipies.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.elaine.minerecipies.R

object ImageLoader {
    fun loadBlockImage(context: Context, imagePath: String, imageView: ImageView) {
        try {
            Glide.with(context)
                .load("file:///android_asset/$imagePath")
                .override(128, 128) // Reasonable size for block images
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.default_block_placeholder) // Create a placeholder drawable
                .into(imageView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadItemImage(context: Context, imagePath: String, imageView: ImageView) {
        try {
            Glide.with(context)
                .load("file:///android_asset/$imagePath")
                .override(64, 64) // Smaller size for item images
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.default_item_placeholder) // Create a placeholder drawable
                .into(imageView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkBitMapSize(context: Context, assetPath: String) : Boolean{
        try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

            context.assets.open(assetPath).use {
                BitmapFactory.decodeStream(it, null, options)
            }

            val width = options.outWidth
            val height = options.outHeight
            val size = width * height * 4 // Approximate size in bytes (ARGB_8888)

            Log.d("BitmapCheck", "Image at $assetPath has dimensions $width x $height, approx size: ${size/1024/1024}MB")

            return size < 50 * 1024 * 1024
        } catch (e: Exception) {
            Log.e("BitmapCheck", "Error checking bitmap size for $assetPath", e)
            return false
        }
    }

    @Composable
    fun BlockImage(imagePath: String, modifier: Modifier = Modifier) {
        val context = LocalContext.current

        // Use Coil for Compose image loading (more Compose-friendly than Glide)
        val painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context)
                .data("file:///android_asset/$imagePath")
                .size(128, 128) // Set a reasonable size limit
                .scale(Scale.FIT)
                .crossfade(true)
                .placeholder(R.drawable.default_block_placeholder)
                .error(R.drawable.default_block_placeholder)
                .build()
        )

        Image(
            painter = painter,
            contentDescription = "Block Image",
            modifier = modifier.size(64.dp),
            contentScale = ContentScale.Fit
        )
    }

    // Update in ImageLoader.kt
    @Composable
    fun ItemImage(imagePath: String, modifier: Modifier = Modifier) {
        val context = LocalContext.current

        // Check if asset exists, use a default if not
        val finalPath = try {
            context.assets.open(imagePath).close()
            imagePath
        } catch (e: Exception) {
            "items/unknown_item.png"  // Default fallback image
        }

        val painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context)
                .data("file:///android_asset/$finalPath")
                .size(64, 64)
                .scale(Scale.FIT)
                .crossfade(true)
                .placeholder(R.drawable.default_item_placeholder)
                .error(R.drawable.default_item_placeholder)
                .build()
        )

        Image(
            painter = painter,
            contentDescription = "Item Image",
            modifier = modifier.size(48.dp),
            contentScale = ContentScale.Fit
        )
    }


}

