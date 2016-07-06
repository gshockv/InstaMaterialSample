package io.github.gshockv.instamaterialsample.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ViewAnimator;

import io.github.gshockv.instamaterialsample.R;

public class SendCommentButton extends ViewAnimator implements View.OnClickListener {

    private static final long RESET_STATE_DELAY_MILLIS = 2000;

    public static final int STATE_SEND = 0;
    public static final int STATE_DONE = 1;

    private int currentState;
    private OnSendClickListener onSendClickListener;

    private Runnable revertStateRunnable = new Runnable() {
        @Override
        public void run() {
            setCurrentState(STATE_SEND);
        }
    };

    public SendCommentButton(Context context) {
        this(context, null);
    }

    public SendCommentButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_send_comment_button, this, true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        currentState = STATE_SEND;
        super.setOnClickListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(revertStateRunnable);
        super.onDetachedFromWindow();
    }

    public void setCurrentState(int state) {
        if (state == STATE_DONE) {
            setEnabled(false);
            postDelayed(revertStateRunnable, RESET_STATE_DELAY_MILLIS);
            setInAnimation(getContext(), R.anim.slide_in_done);
            setOutAnimation(getContext(), R.anim.slide_out_send);
        } else if (state == STATE_SEND) {
            setEnabled(true);
            setInAnimation(getContext(), R.anim.slide_in_send);
            setOutAnimation(getContext(), R.anim.slide_out_done);
        }
        showNext();
    }

    @Override
    public void onClick(View view) {
        if (onSendClickListener != null) {
            onSendClickListener.onSendClickListener(view);
        }
    }

    public void setOnSendClickListener(OnSendClickListener listener) {
        this.onSendClickListener = listener;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        // . . . do nothing with standard click listener
    }

    public interface OnSendClickListener {
        void onSendClickListener(View v);
    }
}
