package com.project.sns.ui.adapters.listener;

import com.project.sns.data.board.Comment
import com.project.sns.data.board.PostData;

 interface onClickItemListener {
    fun onClickItem(position : Int,  postData : ArrayList<PostData>);
}

interface onClickItemComment {
    fun onClickItem(data : Comment)
}