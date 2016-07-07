package io.github.gshockv.instamaterialsample.ui;

import android.view.View;

public interface OnFeedItemClickListener {
    void onCommentsClick(View view, int position);
    void onLikeClick(View view, int position);
    void onMoreClick(View view, int position);
}
