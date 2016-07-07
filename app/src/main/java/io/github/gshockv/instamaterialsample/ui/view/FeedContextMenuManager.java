package io.github.gshockv.instamaterialsample.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import io.github.gshockv.instamaterialsample.Utils;

public class FeedContextMenuManager extends RecyclerView.OnScrollListener implements View.OnAttachStateChangeListener {

    private static final int MENU_ANIMATION_DURATION = 150;
    private static FeedContextMenuManager instance;
    private FeedContextMenu menu;
    private boolean isContextMenuShowing;
    private boolean isContextMenuDismissing;

    public static FeedContextMenuManager getInstance() {
        if (instance == null) {
            instance = new FeedContextMenuManager();
        }
        return instance;
    }

    @Override
    public void onViewAttachedToWindow(View view) {

    }

    @Override
    public void onViewDetachedFromWindow(View view) {
        menu = null;
    }

    public void toggleContextMenuFromView(View anchor, int feedItem, FeedContextMenu.ContextMenuClickListener menuListener) {
        if (menu == null) {
            showContextMenuFromView(anchor, feedItem, menuListener);
        } else {
            hideContextMenu();
        }
    }

    private void showContextMenuFromView(final View anchor, int feedItem, FeedContextMenu.ContextMenuClickListener menuListener) {
        if (!isContextMenuShowing) {
            isContextMenuShowing = true;
            menu = new FeedContextMenu(anchor.getContext());
            menu.bindToItem(feedItem);
            menu.addOnAttachStateChangeListener(this);
            menu.setContextMenuClickListener(menuListener);

            ((ViewGroup) anchor.getRootView().findViewById(android.R.id.content)).addView(menu);
            menu.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    menu.getViewTreeObserver().removeOnPreDrawListener(this);
                    setupContextMenuInitialPosition(anchor);
                    performShowAnimation();
                    return false;
                }
            });
        }
    }

    private void setupContextMenuInitialPosition(View anchor) {
        final int[] openingViewLocation = new int[2];
        anchor.getLocationOnScreen(openingViewLocation);
        int additionalBottomMargin = Utils.dpToPx(16);
        menu.setTranslationX(openingViewLocation[0] - menu.getWidth() / 3);
        menu.setTranslationY(openingViewLocation[1] - menu.getHeight() - additionalBottomMargin);
    }

    private void performShowAnimation() {
        menu.setPivotX(menu.getWidth() / 2);
        menu.setPivotY(menu.getHeight());
        menu.setScaleX(0.1f);
        menu.setScaleY(0.1f);
        menu.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(MENU_ANIMATION_DURATION)
                .setInterpolator(new OvershootInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isContextMenuShowing = false;
                    }
                });
    }

    public void hideContextMenu() {
        if (!isContextMenuDismissing) {
            isContextMenuDismissing = true;
            performDismissAnimation();
        }
    }

    private void performDismissAnimation() {
        menu.setPivotX(menu.getWidth() / 2);
        menu.setPivotY(menu.getHeight());
        menu.animate()
                .scaleX(0.1f)
                .scaleY(0.1f)
                .setDuration(MENU_ANIMATION_DURATION)
                .setInterpolator(new AccelerateInterpolator())
                .setStartDelay(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (menu != null) {
                            menu.dismiss();
                        }
                        isContextMenuDismissing = false;
                    }
                });
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (menu != null) {
            hideContextMenu();
            menu.setTranslationY(menu.getTranslationY() - dy);
        }
    }
}
