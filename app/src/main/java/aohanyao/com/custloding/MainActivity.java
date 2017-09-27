package aohanyao.com.custloding;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import aohanyao.com.custloding.ui.SafeView;

public class MainActivity extends AppCompatActivity {

    private SafeView custLoding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        custLoding = (SafeView) findViewById(R.id.cl_loding);
    }

    public void start(View view) {
        custLoding.start(8, 5000);
    }
}
