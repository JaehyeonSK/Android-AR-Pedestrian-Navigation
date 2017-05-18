package me.blog.cjh7163.tmaptest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    ArrayList<SearchActivity.POI> arrayPOI;
    SearchListAdapter adapter;

    ListView lvHistory;
    Button btnErase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        arrayPOI = new ArrayList<>();
        adapter = new SearchListAdapter();

        btnErase = (Button)findViewById(R.id.btnErase);
        btnErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File file = new File(getFilesDir(), "history.txt");
                    if (file.exists()) {
                        file.delete();
                    }
                } catch(Exception ex) {
                }
            }
        });

        lvHistory = (ListView)findViewById(R.id.lvHistory);
        lvHistory.setAdapter(adapter);

        File file;
        BufferedReader br;
        try {
            file = new File(getFilesDir(), "history.txt");
            br = new BufferedReader(new FileReader(file));

            String line = null;
            while((line = br.readLine()) != null) {
                String[] vals = line.split(" ");

                String name = vals[0];
                double longitude = Double.valueOf(vals[1]);
                double latitude = Double.valueOf(vals[2]);

                SearchActivity.POI poi = new SearchActivity.POI();
                poi.name = name;
                poi.longitude = longitude;
                poi.latitude = latitude;
                arrayPOI.add(poi);

                adapter.addItem(name, "위도:"+longitude+"/경도:"+latitude);
            }
            adapter.notifyDataSetChanged();

            br.close();

        } catch(Exception ex) {
        }


        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int index, long l) {
                try {
                    if (index >= arrayPOI.size()) {
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                    builder.setTitle("안내")
                            .setMessage(String.format("%s를 도착지로 설정하시겠습니까?", arrayPOI.get(index).name))
                            .setNegativeButton("아니오", null)
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent();
                                    intent.putExtra("POI", arrayPOI.get(index).name);
                                    intent.putExtra("LON", arrayPOI.get(index).longitude);
                                    intent.putExtra("LAT", arrayPOI.get(index).latitude);

                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            }).show();
                } catch(Exception ex){
                    Log.d("Exception:", ex.getMessage());
                }
            }
        });

    }
}
