package io.github.gshockv.instamaterialsample.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.gshockv.instamaterialsample.ui.FeedAdapter;
import io.github.gshockv.instamaterialsample.R;
import io.github.gshockv.instamaterialsample.Utils;
import io.github.gshockv.instamaterialsample.ui.OnFeedItemClickListener;

public class FeedActivity extends AppCompatActivity implements OnFeedItemClickListener {

    private static final int TOOLBAR_ANIMATION_DURATION = 300;

    @Bind(R.id.imageViewLogo) ImageView imageViewLogo;
    @Bind(R.id.appToolbar) Toolbar appToolbar;
    @Bind(R.id.recycler_feed) RecyclerView recyclerFeed;
    @Bind(R.id.fabCreate) ImageView buttonCreate;

    private MenuItem menuItemInbox;
    private FeedAdapter feedAdapter;
    private boolean pendingIntroAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        }

        setupToolbar();
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
        recyclerFeed.setAdapter(feedAdapter);
        feedAdapter.setOnFeedItemClickListener(this);
    }

    private void setupToolbar() {
        setSupportActionBar(appToolbar);
        appToolbar.setNavigationIcon(R.drawable.ic_menu_white);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_menu, menu);
        menuItemInbox = menu.findItem(R.id.action_inbox);
        menuItemInbox.setActionView(R.layout.menu_item_inbox);

        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }

        return true;
    }

    private void startIntroAnimation() {
        buttonCreate.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btnFabSize));

        int actionBarSize = Utils.dpToPx(56);
        appToolbar.setTranslationY(-actionBarSize);
        imageViewLogo.setTranslationY(-actionBarSize);
        menuItemInbox.getActionView().setTranslationY(-actionBarSize);

        appToolbar.animate()
                .translationY(0)
                .setDuration(TOOLBAR_ANIMATION_DURATION)
                .setStartDelay(300);
        imageViewLogo.animate()
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
}

