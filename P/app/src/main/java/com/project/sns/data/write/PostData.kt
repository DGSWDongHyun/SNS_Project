package com.project.sns.data.write


data class PostData(val title : String = "", val content : String = "", var image_url : String?= "", val dateTime : Long = 0, var genre : String ?= null)