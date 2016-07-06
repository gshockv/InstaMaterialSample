package io.github.gshockv.instamaterialsample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.gshockv.instamaterialsample.ui.view.SquareImageView;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.CellFeedViewHolder> {

    private static final int DEFAULT_ITEMS_COUNT = 25;
    private static final int ANIMATED_ITEMS_COUNT = 2;

    private final Context context;
    private int lastAnimatedPosition = -1;
    private int itemsCount = 0;

    public FeedAdapter(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public CellFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);
        return new CellFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CellFeedViewHolder holder, int position) {
        runEnterAnimation(holder.itemView, position);
        if (position % 2 == 0) {
            holder.imageViewFeedCenter.setImageResource(R.drawable.img_feed_center_1);
            holder.imageViewFeedBottom.setImageResource(R.drawable.img_feed_bottom_1);
        } else {
            holder.imageViewFeedCenter.setImageResource(R.drawable.img_feed_center_2);
            holder.imageViewFeedBottom.setImageResource(R.drawable.img_feed_bottom_2);
        }
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

    @Override
    public int getItemCount() {
        return itemsCount;
    }

    public void updateItems() {
        itemsCount = DEFAULT_ITEMS_COUNT;
        notifyDataSetChanged();
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.imageViewFeedCenter) SquareImageView imageViewFeedCenter;
        @Bind(R.id.imageViewFeedBottom) ImageView imageViewFeedBottom;

        public CellFeedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}