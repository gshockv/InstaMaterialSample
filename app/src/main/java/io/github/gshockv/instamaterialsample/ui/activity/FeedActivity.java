package io.github.gshockv.instamaterialsample.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.Bind;
import io.github.gshockv.instamaterialsample.R;
import io.github.gshockv.instamaterialsample.Utils;
import io.github.gshockv.instamaterialsample.ui.FeedAdapter;
import io.github.gshockv.instamaterialsample.ui.OnFeedItemClickListener;
import io.github.gshockv.instamaterialsample.ui.view.FeedContextMenu;
import io.github.gshockv.instamaterialsample.ui.view.FeedContextMenuManager;

public class FeedActivity extends BaseActivity implements OnFeedItemClickListener {

    private static final int TOOLBAR_ANIMATION_DURATION = 300;

    @Bind(R.id.recycler_feed) RecyclerView recyclerFeed;
    @Bind(R.id.fabCreate) ImageView buttonCreate;

    private FeedAdapter feedAdapter;
    private boolean pendingIntroAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        }

        setupFeed();
    }

    private void setupFeed() {
        final LinearLayoutManager lm = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };

        recyclerFeed.setLayoutManager(lm);
        feedAdapter = new FeedAdapter(this);
        feedAdapter.setOnFeedItemClickListener(this);
        recyclerFeed.setAdapter(feedAdapter);
        recyclerFeed.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }

        return true;
    }

    private void startIntroAnimation() {
        buttonCreate.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btnFabSize));

        int actionBarSize = Utils.dpToPx(56);
        toolbar.setTranslationY(-actionBarSize);
        imageLogo.setTranslationY(-actionBarSize);
        menuItemInbox.getActionView().setTranslationY(-actionBarSize);

        toolbar.animate()
                .translationY(0)
                .setDuration(TOOLBAR_ANIMATION_DURATION)
                .setStartDelay(300);
        imageLogo.animate()
                .translationY(0)
                .setDuration(TOOLBAR_ANIMATION_DURATION)
                .setStartDelay(300);
        menuItemInbox.getActionView().animate()
                .translationY(0)
                .setDuration(TOOLBAR_ANIMATION_DURATION)
                .setStartDelay(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }
                })
                .start();
    }

    private void startContentAnimation() {
        buttonCreate.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(TOOLBAR_ANIMATION_DURATION)
                .start();
        feedAdapter.updateItems();
    }

    @Override
    public void onProfileClick(View view) {
        Toast.makeText(this, "Coming Soon...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCommentsClick(View view, int position) {
        final Intent intent = new Intent(FeedActivity.this, CommentsActivity.class);

        int[] startingLocation = new int[2];
        view.getLocationOnScreen(startingLocation);
        intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);

        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onLikeClick(View view, int position) {
        // Coming soon . . .
    }

    @Override
    public void onMoreClick(View view, int position) {
        final FeedContextMenuManager manager = FeedContextMenuManager.getInstance();

        FeedContextMenu.ContextMenuClickListener menuListener = new FeedContextMenu.ContextMenuClickListener() {
            @Override
            public void onItemClick(int feedItem) {
                manager.hideContextMenu();
            }

            @Override
            public void onCancelClick(int feedItem) {
                manager.hideContextMenu();
            }
        };

        manager.toggleContextMenuFromView(view, position, menuListener);
    }
}

