package com.example.donogear;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private Context mContext;
    private List<ItemDetails> itemDetailsList;
    private ItemClickListener itemClickListener;

    public ItemAdapter(Context context, List<ItemDetails> list) {
        mContext = context;
        itemDetailsList = list;
    }

    public void setItemList(List<ItemDetails> newList) {
        itemDetailsList = newList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView titleText, endTimeText, timeHolder;
        private RelativeLayout container;
        private CardView cardView;
        CountDownTimer timer;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image);
            titleText = itemView.findViewById(R.id.title);
            endTimeText = itemView.findViewById(R.id.time_left);
            timeHolder = itemView.findViewById(R.id.time_holder);
            container = itemView.findViewById(R.id.container);
            cardView = itemView.findViewById(R.id.cardview);
            itemView.setOnClickListener(view -> {
                System.out.println(getAdapterPosition());
                itemClickListener.onItemClick(view, getAdapterPosition());
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder itemHolder, final int i) {
        ItemDetails curItem = itemDetailsList.get(i);
        int textColor = Color.BLACK;
        if (curItem.listOfImages != null && curItem.listOfImages.size() > 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(curItem.listOfImages.get(0).toString());
            textColor = getFavourableTextColor(bitmap);
            itemHolder.imageView.setImageBitmap(bitmap);
        }

        String title = curItem.itemName;
        if(title.length() > 30) {
            title = title.substring(0, 30);
            title = title.substring(0, title.lastIndexOf(" ")) + "...";
        }
        itemHolder.titleText.setText(title);
        Date endTime = curItem.endDate;
        if (itemHolder.timer != null) {
            itemHolder.timer.cancel();
        }
        tickTime(endTime, itemHolder, i);
        itemHolder.titleText.setTextColor(textColor);
    }

    private int getFavourableTextColor(Bitmap bitmap) {
        int pixel = bitmap.getPixel(0, bitmap.getHeight() - 1);
        Color bgImageColor =  Color.valueOf(Color.rgb(Color.red(pixel), Color.green(pixel), Color.blue(pixel)));
        return bgImageColor.luminance() > 0.5 ? Color.BLACK : Color.WHITE;
    }

    private void tickTime(final Date endTime, final ViewHolder itemHolder, final int position) {
        final long timeInMilliSec = endTime.getTime() - Calendar.getInstance().getTimeInMillis();
        itemHolder.timer = new CountDownTimer(timeInMilliSec, 1000) {
            @Override
            public void onTick(long l) {
                List<String> time = getTime(timeInMilliSec);
                String newTime = String.join(" : ", time).trim();
                itemHolder.endTimeText.setText(newTime);
                notifyItemChanged(position);
            }

            @Override
            public void onFinish() {
                itemHolder.endTimeText.setText("TIME UP");
                itemHolder.endTimeText.setTextColor(Color.RED);
                //itemHolder.timeHolder.setVisibility(View.GONE);
            }
        }.start();
    }

    private List<String> getTime(long difference) {
        long millisInDay = 1000 * 60 * 60 * 24;
        long millisInHour = 1000 * 60 * 60;
        long millisInMinute = 1000 * 60;
        long millisInSecond = 1000;

        long days = difference / millisInDay;
        long daysDivisionResidueMillis = difference - days * millisInDay;
        String day = days < 10 ? "0" + days : "" + days;

        long hours = daysDivisionResidueMillis / millisInHour;
        long hoursDivisionResidueMillis = daysDivisionResidueMillis - hours * millisInHour;
        String hour = hours < 10 ? "0" + hours : "" + hours;

        long minutes = hoursDivisionResidueMillis / millisInMinute;
        long minutesDivisionResidueMillis = hoursDivisionResidueMillis - minutes * millisInMinute;
        String min = minutes < 10 ? "0" + minutes : "" + minutes;

        long seconds = minutesDivisionResidueMillis / millisInSecond;
        String sec = seconds < 10 ? "0" + seconds : "" + seconds;
        return Arrays.asList(day, hour, min, sec);
    }

    @Override
    public int getItemCount() {
        return itemDetailsList.size();
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
