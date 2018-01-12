package com.xyw.platescan.view.ResultActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.xyw.platescan.R;
import com.xyw.platescan.util.ExpandableAdapter;
import com.xyw.platescan.view.BaseActivity;

/**
 * Created by 31429 on 2018/1/11.
 */

public class OffLineRes extends BaseActivity {


    private ExpandableListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offline_activity);
        listView = findViewById(R.id.offline_listview);
        String data = getIntent().getStringExtra("data");
        String[] dataArray = data.split("\\|");
        try {
            String[][] childData = new String[][]{
                    {dataArray[13], dataArray[14], dataArray[15]},
                    {dataArray[0], dataArray[6], dataArray[2], dataArray[1], dataArray[7]},
                    {dataArray[4], dataArray[5], dataArray[3], dataArray[8], dataArray[9], dataArray[11], dataArray[12], dataArray[10].equals("0") ? "不占道" : "占道"}
            };
            ExpandableAdapter expandableAdapter = new ExpandableAdapter(childData, OffLineRes.this);
            listView.setAdapter(expandableAdapter);
            for (int i = 0; i < expandableAdapter.getGroupCount(); i++) {
                listView.expandGroup(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "非法的二维码信息", Toast.LENGTH_LONG).show();
            finish();
        }
    }


}
