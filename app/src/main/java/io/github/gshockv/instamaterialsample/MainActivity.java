package io.github.gshockv.instamaterialsample;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        }

        setupToolbar();
        setupFeed();
    }

    private void setupFeed() {
        recyclerFeed.setLayoutManager(new LinearLayoutManager(this));
        feedAdapter = new FeedAdapter(this);
        recyclerFeed.setAdapter(feedAdapter);
        //feedAdapter.updateItems();
    }

    private void setupToolbar() {
        setSupportActionBar(appToolbar);
        appToolbar.setNavigationIcon(R.drawable.ic_menu_white);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
}

