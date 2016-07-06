package io.github.gshockv.instamaterialsample.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.facebook.rebound.ui.Util;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.gshockv.instamaterialsample.R;
import io.github.gshockv.instamaterialsample.Utils;
import io.github.gshockv.instamaterialsample.ui.CommentsAdapter;

public class CommentsActivity extends AppCompatActivity {
    static final String ARG_DRAWING_START_LOCATION = "arg.drawing.start.location";

    @Bind(R.id.appToolbar) Toolbar appToolbar;
    @Bind(R.id.recycler_comments) RecyclerView recyclerComments;
    @Bind(R.id.contentRoot) LinearLayout contentRoot;
    @Bind(R.id.addCommentBlock) LinearLayout addCommentBlock;

    private CommentsAdapter commentsAdapter;

    private int drawingStartLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ButterKnife.bind(this);

        setupToolbar();
        setupCommentsList();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_menu, menu);
        menu.findItem(R.id.action_inbox).setActionView(R.layout.menu_item_inbox);
        return true;
    }

    private void startIntroAnimation() {
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

    private void setupToolbar() {
        setSupportActionBar(appToolbar);
        appToolbar.setNavigationIcon(R.drawable.ic_menu_white);
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

    @OnClick(R.id.buttonSendComment)
    public void onSendCommentClick() {
        commentsAdapter.addItem();
        commentsAdapter.setAnimationLocked(false);
        commentsAdapter.setDelayEnterAnimation(false);
        recyclerComments.smoothScrollBy(0, recyclerComments.getChildAt(0).getHeight() * commentsAdapter.getItemCount());
    }

    @Override
    public void onBackPressed() {
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
}
