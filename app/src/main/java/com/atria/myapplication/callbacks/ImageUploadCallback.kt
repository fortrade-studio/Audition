package com.atria.myapplication.callbacks

import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile

interface ImageUploadCallback {

    fun onImageSelected(media:MediaFile)
    fun onImageUploaded()

}