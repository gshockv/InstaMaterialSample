package io.github.gshockv.instamaterialsample.ui.view;

import android.content.Context;
import android.support.v13.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.gshockv.instamaterialsample.R;
import io.github.gshockv.instamaterialsample.Utils;

public class FeedContextMenu extends LinearLayout {

    private static final int CONTEXT_MENU_WIDTH = Utils.dpToPx(240);

    private int feedItem = -1;
    private ContextMenuClickListener listener;

    public FeedContextMenu(Context context) {
        this(context, null);
    }

    public FeedContextMenu(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public FeedContextMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_context_menu, this, true);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(CONTEXT_MENU_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT));
        setBackgroundResource(R.drawable.bg_context_menu);
        ViewCompat.setElevation(this, Utils.dpToPx(6));
    }

    public void bindToItem(int feedItem) {
        this.feedItem = feedItem;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.bind(this);
    }

    public void dismiss() {
        ((ViewGroup) getParent()).removeView(FeedContextMenu.this);
    }

    public void setContextMenuClickListener(ContextMenuClickListener listener) {
        this.listener = listener;
    }

    @OnClick(R.id.buttonItemReport)
    public void onReportClick() {
        if (listener != null) {
            listener.onItemClick(feedItem);
        }
    }

    @OnClick(R.id.buttonSharePhoto)
    public void onSharePhotoClick() {
        if (listener != null) {
            listener.onItemClick(feedItem);
        }
    }

    @OnClick(R.id.buttonCopyShareUrl)
    public void onCopyShareUrlClick() {
        if (listener != null) {
            listener.onItemClick(feedItem);
        }
    }

    @OnClick(R.id.buttonCancel)
    public void onCancelClick() {
        if (listener != null) {
            listener.onCancelClick(feedItem);
        }
    }


    public interface ContextMenuClickListener {
        void onItemClick(int feedItem);
        void onCancelClick(int feedItem);
    }

}
