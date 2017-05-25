package com.athou.slidingcard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/4/24.
 */

@CoordinatorLayout.DefaultBehavior(SlidingCardBehavior.class)
public class SlidingCardLayout extends FrameLayout {

    RecyclerView recyclerView;
    TextView header;

    public SlidingCardLayout(Context context) {
        this(context, null);
    }

    public SlidingCardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingCardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.widget_card, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlidingCardLayout);

        header = (TextView) findViewById(R.id.header);
        header.setBackgroundColor(typedArray.getColor(R.styleable.SlidingCardLayout_android_background, Color.RED));
        header.setText(typedArray.getText(R.styleable.SlidingCardLayout_android_text));

        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new MyAdapter());

        typedArray.recycle();
    }

    private String[] datas = new String[]{"赵丽颖", "AngelaBaby", "范冰冰", "李冰冰", "杨幂",
            "刘亦菲", "朱茵", "高圆圆", "刘涛", "唐嫣", "孙俪"};

    public int getHeaderHeight() {
        return headHeight;
    }

    private int headHeight = 0;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            headHeight = findViewById(R.id.header).getMeasuredHeight();
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_card, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            TextView textView = (TextView) holder.itemView.findViewById(R.id.item_tv);
            textView.setText(datas[position]);
        }

        @Override
        public int getItemCount() {
            return datas.length;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            public MyViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
