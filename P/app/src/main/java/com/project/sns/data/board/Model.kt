package com.project.sns.data.board


data class Genre(var genre : String?= null, var isVisible : Boolean ?= false)

data class Comment(var commentContent : String?="", var commentDateTime : Long?=0, var user : User?= null)
data class User(var userName: String?= "", var userEmail: String?= "", var key: String?= "", var userProfile : String?= "no", var deviceToken : String?= "", var likeGenre : String?= "")


data class PostData(val title : String?= "", val content : String?= "", var image_url : String?= "",var file_url : String?= "", val dateTime : Long = 0,
                    val genre : String?= "", var viewType : Int = 0, var UserName : String?= "", var commentCount : Int = 0,
                    var commentList : HashMap<String, Comment>?= null, var key : String?= "")