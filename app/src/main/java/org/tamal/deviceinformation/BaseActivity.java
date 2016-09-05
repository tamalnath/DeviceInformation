package org.tamal.deviceinformation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem item = null;
        if (this instanceof MainActivity) {
            item = menu.findItem(R.id.action_home);
        }
        if (this instanceof SensorActivity) {
            item = menu.findItem(R.id.action_about);
        }
        if (item != null) {
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.action_about:
                startActivity(new Intent(this, SensorActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        finish();
        return true;
    }
}
