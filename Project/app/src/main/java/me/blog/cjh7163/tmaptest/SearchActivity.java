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
import android.widget.EditText;
import android.widget.ListView;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPOIItem;

import java.util.ArrayList;

import me.blog.cjh7163.tmaptest.Utils.StringUtils;

public class SearchActivity extends AppCompatActivity {

    TMapData mapData;
    EditText editSearch;
    Button btnSearch;
    ListView lvSearch;

    SearchListAdapter adapter;
    ArrayList<POI> arrayPOI;

    public static class POI {
        public String name;
        public double latitude;
        public double longitude;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mapData = new TMapData();

        editSearch = (EditText)findViewById(R.id.editSearch);
        btnSearch = (Button)findViewById(R.id.btnSearch);
        lvSearch = (ListView)findViewById(R.id.lvSearch);

        adapter = new SearchListAdapter();
        arrayPOI = new ArrayList<>();
        lvSearch.setAdapter(adapter);

        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int index, long l) {
                try {
                    if (index >= arrayPOI.size()) {
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
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

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mapData.findAllPOI(editSearch.getText().toString(), 20, new TMapData.FindAllPOIListenerCallback() {
                        @Override
                        public void onFindAllPOI(final ArrayList<TMapPOIItem> arrayList) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.clear();
                                    arrayPOI.clear();

                                    for(int i=0; i<arrayList.size(); i++) {
                                        TMapPOIItem poiItem = arrayList.get(i);

                                        String secondLine = StringUtils.join('/', new String[] {poiItem.upperBizName, poiItem.middleBizName, poiItem.lowerBizName, poiItem.detailBizName});
                                        adapter.addItem(poiItem.getPOIName(), secondLine);

                                        POI poi = new POI();
                                        poi.name = poiItem.getPOIName();
                                        poi.latitude = poiItem.getPOIPoint().getLatitude();
                                        poi.longitude = poiItem.getPOIPoint().getLongitude();

                                        arrayPOI.add(poi);
                                    }

                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                } catch(Exception ex) {
                    Log.d("Exception:", ex.getMessage());
                }
            }
        });
    }
}
