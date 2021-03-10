package com.project.sns.ui.adapters.listener;

import com.project.sns.data.board.PostData;

 interface onClickItemListener {
    fun onClickItem(position : Int,  postData : ArrayList<PostData>);
}

interface onClickItemAdapter {
    fun onClickAdapter(position : Int,  postData : ArrayList<PostData>);
}
