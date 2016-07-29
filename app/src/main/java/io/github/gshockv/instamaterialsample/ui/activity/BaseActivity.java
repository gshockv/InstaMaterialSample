package io.github.gshockv.instamaterialsample.ui.activity;

import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.gshockv.instamaterialsample.R;

public abstract class BaseActivity extends AppCompatActivity {
    @Bind(R.id.appToolbar) Toolbar toolbar;
    @Bind(R.id.imageViewLogo) ImageView imageLogo;

    protected MenuItem menuItemInbox;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        setupToolbar();
    }

    private void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_menu_white);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_menu, menu);
        menuItemInbox = menu.findItem(R.id.action_inbox);
        menuItemInbox.setActionView(R.layout.menu_item_inbox);

        return super.onCreateOptionsMenu(menu);
    }
}
