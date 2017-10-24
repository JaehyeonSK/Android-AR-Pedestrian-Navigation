package me.blog.cjh7163.tmaptest.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import me.blog.cjh7163.tmaptest.R;


/**
 * Created by david2 on 2017-10-23.
 */

public class PopupListView extends LinearLayout {
    private ListView contentList;
    private ProgressBar pgDistance;
    private TextView tvTitle;
    private TextView tvClose;
    private TextView tvStatus;

    private int currentDistance = 0, maxDistance = 0;

    public PopupListView(Context context) {
        super(context);
        init(context, null);
    }

    public PopupListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PopupListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public void setAdapter(ListAdapter adapter) {
        contentList.setAdapter(adapter);
    }

    public void updateDistance(int distance) {
        currentDistance = distance;
        setDistance();
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.popup_list_view, this, true);

        tvTitle = (TextView)findViewById(R.id.popup_list_title);
        tvClose = (TextView)findViewById(R.id.close_btn);
        tvStatus = (TextView)findViewById(R.id.distance_status);

        contentList = (ListView)findViewById(R.id.popup_list_view);
        pgDistance = (ProgressBar)findViewById(R.id.progress_distance);

        // 뷰에 애트리뷰트 적용
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PopupListView);
            tvTitle.setText(typedArray.getString(R.styleable.PopupListView_listTitle));
            currentDistance = typedArray.getInteger(R.styleable.PopupListView_distance, 0);
            maxDistance = typedArray.getInteger(R.styleable.PopupListView_maxDistance, 0);
            typedArray.recycle();
        }

        setDistance();

        tvClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupListView.this.setVisibility(INVISIBLE);
            }
        });
    }

    private void setDistance() {
        pgDistance.setMax(maxDistance);
        pgDistance.setProgress(currentDistance);

        tvStatus.setText(String.format(getContext().getString(R.string.dist_status), currentDistance, maxDistance));
    }

}
