package io.github.gshockv.instamaterialsample.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

import butterknife.Bind;
import io.github.gshockv.instamaterialsample.R;
import io.github.gshockv.instamaterialsample.ui.view.RevealBackgroundView;

public class UserProfileActivity extends BaseActivity implements RevealBackgroundView.OnStateChangeListener {
    public static final String ARG_REVEAL_START_LOCATION = "arg.reveal.start.location";

    @Bind(R.id.viewReveal) RevealBackgroundView revealBackground;
    @Bind(R.id.recyclerUserProfile) RecyclerView recyclerUserProfile;

    public static void startUserProfileFromLocation(Context context, int[] startingLocation) {
        final Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);
    }

    private void setupUserProfileGrid() {
        // TODO: Implement me . . .
    }

    public void setupRevealBackground(Bundle savedInstanceState) {
        revealBackground.setOnStateChangeListener(this);

        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            revealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    revealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    revealBackground.startFromLocation(startingLocation);
                    return false;
                }
            });
        } else {
            // TODO: Update user profile adapter
            revealBackground.setToFinishedFrame();
        }
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            recyclerUserProfile.setVisibility(View.VISIBLE);
            // TODO: Setup user profile adapter
        } else {
            recyclerUserProfile.setVisibility(View.INVISIBLE);
        }
    }
}
