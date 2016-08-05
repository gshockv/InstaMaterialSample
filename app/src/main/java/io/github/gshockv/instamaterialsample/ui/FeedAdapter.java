package io.github.gshockv.instamaterialsample.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.gshockv.instamaterialsample.R;
import io.github.gshockv.instamaterialsample.Utils;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.CellFeedViewHolder> {
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator();
    private static final int ANIMATION_DURATION = 300;

    private static final int DEFAULT_ITEMS_COUNT = 25;
    private static final int ANIMATED_ITEMS_COUNT = 2;

    private final Context context;
    private int lastAnimatedPosition = -1;
    private int itemsCount = 0;

    private final Map<Integer, Integer> likesCount = new HashMap<>();
    private final ArrayList<Integer> likedPositions = new ArrayList<>();
    private final HashMap<RecyclerView.ViewHolder, AnimatorSet> likeAnimations = new HashMap<>();

    private OnFeedItemClickListener onFeedItemClickListener;

    public FeedAdapter(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public CellFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);
        return new CellFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CellFeedViewHolder holder, final int position) {
        runEnterAnimation(holder.itemView, position);
        int division = position % 3;
        if (division == 0) {
            holder.imageViewFeedCenter.setImageResource(R.drawable.img_feed_center_1);
            holder.imageViewFeedBottom.setImageResource(R.drawable.img_feed_bottom_1);
        } else if (division == 1) {
            holder.imageViewFeedCenter.setImageResource(R.drawable.img_feed_center_2);
            holder.imageViewFeedBottom.setImageResource(R.drawable.img_feed_bottom_2);
        } else {
            holder.imageViewFeedCenter.setImageResource(R.drawable.img_feed_center_3);
            holder.imageViewFeedBottom.setImageResource(R.drawable.img_feed_bottom_1);
        }

        updateLikesCounter(holder, false);
        updateHeartButton(holder, false);

        holder.imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onFeedItemClickListener != null) {
                    onFeedItemClickListener.onProfileClick(holder.imageViewProfile);
                }
            }
        });

        holder.buttonComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onFeedItemClickListener != null) {
                    onFeedItemClickListener.onCommentsClick(holder.buttonComments, position);
                }
            }
        });
        holder.buttonMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onFeedItemClickListener != null) {
                    onFeedItemClickListener.onMoreClick(holder.buttonMore, position);
                }
            }
        });
        holder.buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onFeedItemClickListener != null) {
                    onFeedItemClickListener.onLikeClick(holder.buttonLike, position);
                }
                if (!likedPositions.contains(holder.getPosition())) {
                    likedPositions.add(holder.getPosition());
                    updateLikesCounter(holder, true);
                    updateHeartButton(holder, true);
                }
            }
        });
        holder.imageViewFeedCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!likedPositions.contains(holder.getPosition())) {
                    likedPositions.add(holder.getPosition());
                    animatePhotoLike(holder);
                    updateLikesCounter(holder, true);
                    updateHeartButton(holder, true);
                    notifyDataSetChanged();
                }
            }
        });
    }

    private void runEnterAnimation(View view, int position) {
        if (position >= ANIMATED_ITEMS_COUNT - 1) {
            return;
        }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(Utils.getScreenHeight(context));
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .start();
        }
    }

    private void fillLikesWithFakes() {
        for (int i = 0; i < getItemCount(); i++) {
            likesCount.put(i, new Random().nextInt(100));
        }
    }

    @Override
    public int getItemCount() {
        return itemsCount;
    }

    public void updateItems() {
        itemsCount = DEFAULT_ITEMS_COUNT;
        fillLikesWithFakes();
        notifyDataSetChanged();
    }

    private void updateLikesCounter(CellFeedViewHolder holder, boolean animated) {
        int currentLikesCount = likesCount.get(holder.getPosition()) + 1;
        String text = context.getResources().getQuantityString(
                R.plurals.likes_count, currentLikesCount, currentLikesCount
        );

        if (animated) {
            holder.textSwitcherCounter.setText(text);
        } else {
            holder.textSwitcherCounter.setCurrentText(text);
        }

        likesCount.put(holder.getPosition(), currentLikesCount);
    }

    private void updateHeartButton(final CellFeedViewHolder holder, boolean animated) {
        if (animated) {
            if (!likeAnimations.containsKey(holder)) {
                AnimatorSet animatorSet = new AnimatorSet();
                likeAnimations.put(holder, animatorSet);

                // rotate
                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.buttonLike, "rotation", 0f, 360f);
                rotationAnim.setDuration(ANIMATION_DURATION);
                rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                // bouncing by X
                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.buttonLike, "scaleX", 0.2f, 1f);
                bounceAnimX.setDuration(ANIMATION_DURATION);
                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                // bouncing by Y
                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.buttonLike, "scaleY", 0.2f, 1f);
                bounceAnimY.setDuration(ANIMATION_DURATION);
                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                bounceAnimY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        holder.buttonLike.setImageResource(R.drawable.ic_heart_red);
                    }
                });

                animatorSet.play(rotationAnim);
                animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        resetLikeAnimationState(holder);
                    }
                });

                animatorSet.start();
            }
        } else {
            if (likedPositions.contains(holder.getPosition())) {
                holder.buttonLike.setImageResource(R.drawable.ic_heart_red);
            } else {
                holder.buttonLike.setImageResource(R.drawable.ic_heart_outline_grey);
            }
        }
    }

    private void resetLikeAnimationState(CellFeedViewHolder holder) {
        likeAnimations.remove(holder);
        holder.backgroundLike.setVisibility(View.GONE);
        holder.imageViewLike.setVisibility(View.GONE);
    }

    private void animatePhotoLike(final CellFeedViewHolder holder) {
        if (!likeAnimations.containsKey(holder)) {
            holder.backgroundLike.setVisibility(View.VISIBLE);
            holder.imageViewLike.setVisibility(View.VISIBLE);

            holder.backgroundLike.setScaleY(0.1f);
            holder.backgroundLike.setScaleX(0.1f);
            holder.backgroundLike.setAlpha(1f);

            holder.imageViewLike.setScaleY(0.1f);
            holder.imageViewLike.setScaleX(0.1f);

            AnimatorSet animatorSet = new AnimatorSet();
            likeAnimations.put(holder, animatorSet);

            ObjectAnimator bgScaleAnimY = ObjectAnimator.ofFloat(holder.backgroundLike, "scaleY", 0.1f, 1f);
            bgScaleAnimY.setDuration(200);
            bgScaleAnimY.setInterpolator(DECELERATE_INTERPOLATOR);
            ObjectAnimator bgScaleAnimX = ObjectAnimator.ofFloat(holder.backgroundLike, "scaleX", 0.1f, 1f);
            bgScaleAnimX.setDuration(200);
            bgScaleAnimX.setInterpolator(DECELERATE_INTERPOLATOR);
            ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(holder.backgroundLike, "alpha", 1f, 0f);
            bgAlphaAnim.setDuration(200);
            bgAlphaAnim.setStartDelay(150);
            bgAlphaAnim.setInterpolator(DECELERATE_INTERPOLATOR);

            ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(holder.imageViewLike, "scaleY", 0.1f, 1f);
            imgScaleUpYAnim.setDuration(300);
            imgScaleUpYAnim.setInterpolator(DECELERATE_INTERPOLATOR);
            ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(holder.imageViewLike, "scaleX", 0.1f, 1f);
            imgScaleUpXAnim.setDuration(300);
            imgScaleUpXAnim.setInterpolator(DECELERATE_INTERPOLATOR);

            ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(holder.imageViewLike, "scaleY", 1f, 0f);
            imgScaleDownYAnim.setDuration(300);
            imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
            ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(holder.imageViewLike, "scaleX", 1f, 0f);
            imgScaleDownXAnim.setDuration(300);
            imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

            animatorSet.playTogether(bgScaleAnimY, bgScaleAnimX, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim);
            animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);

            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    resetLikeAnimationState(holder);
                }
            });
            animatorSet.start();
        }
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener listener) {
        this.onFeedItemClickListener = listener;
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imageViewProfile) ImageView imageViewProfile;
        @Bind(R.id.imageViewFeedCenter) ImageView imageViewFeedCenter;
        @Bind(R.id.imageViewFeedBottom) ImageView imageViewFeedBottom;

        @Bind(R.id.buttonLike) ImageButton buttonLike;
        @Bind(R.id.buttonComments) ImageButton buttonComments;
        @Bind(R.id.buttonMore) ImageButton buttonMore;
        @Bind(R.id.textSwitcherCounter) TextSwitcher textSwitcherCounter;

        @Bind(R.id.view_like_background) View backgroundLike;
        @Bind(R.id.imageViewLike) ImageView imageViewLike;

        public CellFeedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
