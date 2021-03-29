package com.project.sns.ui.adapters.listener;

import com.google.firebase.storage.StorageReference
import com.project.sns.data.board.PostData;

 interface onClickItemListener {
    fun onClickItem(position : Int,  postData : ArrayList<PostData>);
}

interface onClickImageListener {
    fun onClickImage(data: StorageReference)
}

interface onClickItemAdapter {
    fun onClickAdapter(position : Int,  postData : ArrayList<PostData>);
}
