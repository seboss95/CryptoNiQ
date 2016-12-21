package org.purple.smoke;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Button button1 = (Button) findViewById(R.id.reset_neighbor_fields);

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final RadioButton radioButton1 = (RadioButton) findViewById(R.id.neighbor_ipv4);
                final TextView textView1 = (TextView) findViewById(R.id.neighbor_ip_address);
                final TextView textView2 = (TextView) findViewById(R.id.neighbor_port);
                final TextView textView3 = (TextView) findViewById(R.id.neighbor_scope_id);

                radioButton1.setChecked(true);
                textView1.setText("");
                textView2.setText("4710");
                textView3.setText("");
                textView1.requestFocus();
            }
        });

        RadioGroup radioGroup1 = (RadioGroup) findViewById(R.id.neighbors_ipv_radio_group);

        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final TextView textView1 = (TextView) findViewById(R.id.neighbor_scope_id);

                if(checkedId == R.id.neighbor_ipv4) {
                    textView1.setText("");
                    textView1.setVisibility(View.GONE);
                }
                else
                    textView1.setVisibility(View.VISIBLE);
            }
        });

        TextView textView1 = (TextView) findViewById(R.id.neighbor_scope_id);

        textView1.setVisibility(View.GONE);
        textView1 = (TextView) findViewById(R.id.neighbor_port);
        textView1.setText("4710");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_chat) {
            final Intent intent = new Intent(Settings.this, Main.class);

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
