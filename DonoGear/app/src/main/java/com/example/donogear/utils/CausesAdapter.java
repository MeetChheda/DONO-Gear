package com.example.donogear.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.donogear.R;
import com.example.donogear.models.CausesDetails;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CausesAdapter extends RecyclerView.Adapter<CausesAdapter.ViewHolder>{

    private Context mContext;
    private List<CausesDetails> causesDetailsList;
//    private ItemClickListener itemClickListener;

    public CausesAdapter(Context context, List<CausesDetails> list) {
        mContext = context;
        causesDetailsList = list;
    }

    public void setCausesList(List<CausesDetails> newList) {
        causesDetailsList = newList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView causeImage;
        private TextView causeTitle, causeCategory;


        public ViewHolder(View itemView) {
            super(itemView);
            causeImage = itemView.findViewById(R.id.cause_image);
            causeTitle = itemView.findViewById(R.id.cause_title);
            causeCategory = itemView.findViewById(R.id.cause_category);

//            itemView.setOnClickListener(view ->
//                    itemClickListener.onItemClick(view, getAdapterPosition())
//            );
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_cause, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CausesAdapter.ViewHolder causeHolder, final int i) {
        CausesDetails curCause = causesDetailsList.get(i);
        int textColor = Color.BLACK;

        if (curCause.images != null && curCause.images.size() > 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(curCause.images.get(0).toString());
            textColor = getFavourableTextColor(bitmap);
            causeHolder.causeImage.setImageBitmap(bitmap);
        }

        String causeTitle = curCause.causeTitle;
        if(causeTitle.length() > 30) {
            causeTitle = causeTitle.substring(0, 30);
            causeTitle = causeTitle.substring(0, causeTitle.lastIndexOf(" ")) + "...";
        }
        causeHolder.causeTitle.setText(causeTitle);

        String causeCategory = curCause.causeCategory;
        causeHolder.causeCategory.setText(causeCategory);

        causeHolder.causeTitle.setTextColor(textColor);
        causeHolder.causeCategory.setTextColor(textColor);
    }

    private int getFavourableTextColor(Bitmap bitmap) {
        int pixel = bitmap.getPixel(0, bitmap.getHeight() - 1);
        Color bgImageColor =  Color.valueOf(Color.rgb(Color.red(pixel), Color.green(pixel), Color.blue(pixel)));
        return bgImageColor.luminance() > 0.5 ? Color.BLACK : Color.WHITE;
    }


    @Override
    public int getItemCount() {
        return causesDetailsList.size();
    }

}

