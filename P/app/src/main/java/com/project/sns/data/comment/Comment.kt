package com.project.sns.data.comment

import com.project.sns.data.user.User

data class Comment(var commentContent : String?= null, var commentDateTime : Long?= null, var user : User?= null)