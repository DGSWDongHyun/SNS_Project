package com.project.sns.data.write

import com.project.sns.data.comment.Comment


data class PostData(val title : String = "", val content : String = "", var image_url : String?= "", val dateTime : Long = 0,
                    val genre : String ?= null, var viewType : Int = 0, var UserName : String ?= null, var commentCount : Int = 0,
                    var commentList : HashMap<String, Comment>?=null , var key : String?= null)