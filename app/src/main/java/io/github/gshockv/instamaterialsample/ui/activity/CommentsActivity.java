package io.github.gshockv.instamaterialsample.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;

import butterknife.Bind;
import io.github.gshockv.instamaterialsample.R;
import io.github.gshockv.instamaterialsample.Utils;
import io.github.gshockv.instamaterialsample.ui.CommentsAdapter;
import io.github.gshockv.instamaterialsample.ui.view.SendCommentButton;

public class CommentsActivity extends BaseActivity implements SendCommentButton.OnSendClickListener {
    static final String ARG_DRAWING_START_LOCATION = "arg.drawing.start.location";

    @Bind(R.id.recycler_comments) RecyclerView recyclerComments;
    @Bind(R.id.contentRoot) LinearLayout contentRoot;
    @Bind(R.id.addCommentBlock) LinearLayout addCommentBlock;
    @Bind(R.id.editComment) EditText editComment;
    @Bind(R.id.buttonSendComment) SendCommentButton buttonSend;

    private CommentsAdapter commentsAdapter;

    private int drawingStartLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        setupCommentsList();

        buttonSend.setOnSendClickListener(this);

        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        if (savedInstanceState == null) {
            contentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    contentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }
    }

    private void startIntroAnimation() {
        ViewCompat.setElevation(toolbar, 0);

        contentRoot.setScaleY(0.1f);
        contentRoot.setPivotY(drawingStartLocation);
        addCommentBlock.setTranslationY(100);

        contentRoot.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewCompat.setElevation(toolbar, Utils.dpToPx(8));
                        animateContent();
                    }
                })
                .start();
    }

    private void animateContent() {
        commentsAdapter.updateItems();

        addCommentBlock.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
    }

    private void setupCommentsList() {
        recyclerComments.setLayoutManager(new LinearLayoutManager(this));
        commentsAdapter = new CommentsAdapter(this);
        recyclerComments.setAdapter(commentsAdapter);

        recyclerComments.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    commentsAdapter.setAnimationLocked(true);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        ViewCompat.setElevation(toolbar, 0);
        contentRoot.animate()
                .translationY(Utils.getScreenHeight(this))
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        CommentsActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }

    @Override
    public void onSendClickListener(View v) {
        if (!validateComment()) {
            return;
        }
        commentsAdapter.addItem();
        commentsAdapter.setAnimationLocked(false);
        commentsAdapter.setDelayEnterAnimation(false);
        recyclerComments.smoothScrollBy(0, recyclerComments.getChildAt(0).getHeight() * commentsAdapter.getItemCount());

        editComment.setText(null);
        buttonSend.setCurrentState(SendCommentButton.STATE_DONE);
    }

    private boolean validateComment() {
        if (TextUtils.isEmpty(editComment.getText())) {
            buttonSend.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }
        return true;
    }
}
