package com.example.donogear.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donogear.R;
import com.example.donogear.interfaces.ItemClickListener;
import com.example.donogear.interfaces.TickTime;
import com.example.donogear.models.ItemDetails;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.donogear.utils.Constants.DROP_IDENTIFIER;
import static com.example.donogear.utils.Constants.TIME_UP;

/**
 * Adapter for setting Trending Items
 */
public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.ViewHolder> {

    private Context mContext;
    private List<ItemDetails> trendingItemsList;
    private ItemClickListener itemClickListener;
    // Get the type for onItemClickListener
    private String type;

    public TrendingAdapter(){
        //default constructor
    }
    public TrendingAdapter(Context context, List<ItemDetails> list) {
        mContext = context;
        trendingItemsList = list;
    }

    /**
     * Set trending items list
     * @param newList - if list is updated, set new list of all trending items
     */
    public void setItemList(List<ItemDetails> newList) {
        trendingItemsList = newList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView titleText, endTimeText, timeHolder, proceedTitle;
        CountDownTimer timer;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image);
            titleText = itemView.findViewById(R.id.title);
            endTimeText = itemView.findViewById(R.id.time_left);
            timeHolder = itemView.findViewById(R.id.time_holder);
            proceedTitle = itemView.findViewById(R.id.proceed_item_title);

            itemView.setOnClickListener(view ->
                    itemClickListener.onItemClick(view, getAdapterPosition(), type)
            );
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
        ItemDetails curItem = trendingItemsList.get(i);
        int textColor = Color.BLACK;
        int timeColor = Color.BLACK;
        if (curItem.listOfImages != null && curItem.listOfImages.size() > 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(curItem.listOfImages.get(0).toString());
            textColor = getFavourableTextColor(bitmap);
            timeColor = getFavourableTimeColor(bitmap);
            itemHolder.imageView.setImageBitmap(bitmap);
        }
        String title = curItem.itemName;
        if(title.length() > 30) {
            title = title.substring(0, 30);
            title = title.substring(0, title.lastIndexOf(" ")) + "...";
        }
        itemHolder.titleText.setText(title);
        String proceedTitle = "And support " + curItem.proceedTitle;
        itemHolder.proceedTitle.setText(proceedTitle);
        if (curItem.proceedTitle == null) {
            itemHolder.proceedTitle.setVisibility(View.GONE);
        }
        Date endTime = curItem.endDate;
        if (itemHolder.timer != null) {
            itemHolder.timer.cancel();
        }
        itemHolder.titleText.setTextColor(textColor);
        itemHolder.proceedTitle.setTextColor(textColor);
        itemHolder.timeHolder.setTextColor(timeColor);
        itemHolder.endTimeText.setTextColor(timeColor);
        if (!curItem.category.equals(DROP_IDENTIFIER)) {
            tickTime(endTime, itemHolder);
        }
        if (curItem.category.equals(DROP_IDENTIFIER)) {
            itemHolder.timeHolder.setText("Price  ");
            itemHolder.endTimeText.setText("$" + curItem.buyNowPrice + " ");
            itemHolder.endTimeText.setTextSize(20);
            itemHolder.endTimeText.setTextColor(timeColor);
        }
    }

    /**
     * Checks the color of the image
     * @param bitmap - Decoding image into string
     * @return  - favourable text color required
     */
    public int getFavourableTextColor(Bitmap bitmap) {
        int pixel = bitmap.getPixel(0, bitmap.getHeight() - 1);
        Color bgImageColor =  Color.valueOf(Color.rgb(Color.red(pixel), Color.green(pixel), Color.blue(pixel)));
        return bgImageColor.luminance() > 0.5 ? Color.BLACK : Color.WHITE;
    }

    /**
     * Checks the color of the image
     * @param bitmap - Decoding image into string
     * @return  - favourable text color required
     */
    private int getFavourableTimeColor(Bitmap bitmap) {
        int pixel = bitmap.getPixel(bitmap.getWidth() - 1, 0);
        Color bgImageColor =  Color.valueOf(Color.rgb(Color.red(pixel), Color.green(pixel), Color.blue(pixel)));
        return bgImageColor.luminance() > 0.5 ? Color.BLACK : Color.WHITE;
    }


    /**
     * Get time at every second
     * @param endTime - endTime of the auction
     * @param itemHolder - hold the item in the view
     */
    private void tickTime(final Date endTime, final ViewHolder itemHolder) {
        final long timeInMilliSec = endTime.getTime() - Calendar.getInstance().getTimeInMillis();
        itemHolder.timer = new CountDownTimer(timeInMilliSec, 1000) {
            @Override
            public void onTick(long l) {
                String newTime = TickTime.displayTime(l);
                itemHolder.endTimeText.setText(newTime);
                notifyItemChanged(itemHolder.getAdapterPosition());
            }

            @Override
            public void onFinish() {
                itemHolder.endTimeText.setText(TIME_UP);
                itemHolder.endTimeText.setTextColor(Color.RED);
            }
        }.start();
    }


    @Override
    public int getItemCount() {
        return trendingItemsList.size();
    }

    public void setClickListener(ItemClickListener itemClickListener, String type) {
        this.type = type;
        this.itemClickListener = itemClickListener;
    }
}
