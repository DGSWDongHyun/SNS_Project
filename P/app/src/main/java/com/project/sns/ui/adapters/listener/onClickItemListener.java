package com.project.sns.ui.adapters.listener;

import com.project.sns.data.write.PostData;

import java.util.List;

public interface onClickItemListener {
    public void onClickItem(int position, List<PostData> postData);
}
interface onClickItemAdapter {
    public void onClickAdapter(int position, List<PostData> postData);
}
