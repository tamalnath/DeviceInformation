package org.tamal.deviceinformation;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
    }

    public void showDetails(View view) {
        Intent intent = new Intent(view.getContext(), ShowDataHelperActivity.class);
        intent.putExtra("ref", view.getId());
        startActivity(intent);
    }

}
