package com.project.sns.data.board


data class Genre(var genre : String ?= null, var isVisible : Boolean ?= false)
data class Comment(var commentContent : String?= null, var commentDateTime : Long?= null, var user : User?= null)
data class User(var userName: String? = null, var userEmail: String? = null, var key: String? = null, var userProfile : String?="no")


data class PostData(val title : String = "", val content : String = "", var image_url : String?= "", val dateTime : Long = 0,
                    val genre : String ?= null, var viewType : Int = 0, var UserName : String ?= null, var commentCount : Int = 0,
                    var commentList : HashMap<String, Comment>?=null, var key : String?= null)