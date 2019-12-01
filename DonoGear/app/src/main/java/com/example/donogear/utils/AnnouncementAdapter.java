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
import com.example.donogear.models.AnnouncementDetails;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Set announcement on home page fragment
 */
public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>{

    private Context mContext;
    private List<AnnouncementDetails> announcementDetailsList;

    /**
     * Default constructor for announcement adapter
     * @param context - Reference of Main activity
     * @param list - list of all announcements
     */
    public AnnouncementAdapter(Context context, List<AnnouncementDetails> list) {
        mContext = context;
        announcementDetailsList = list;
    }

    /**
     * Set cause list
     * @param newList - if list is updated, set new list of all announcements
     */
    public void setAnnouncementList(List<AnnouncementDetails> newList) {
        announcementDetailsList = newList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView announcementImage;
        private TextView announcementTitle, announcementDescription;


        public ViewHolder(View itemView) {
            super(itemView);
            announcementImage = itemView.findViewById(R.id.announcement_image);
            announcementTitle = itemView.findViewById(R.id.announcement_title);
            announcementDescription = itemView.findViewById(R.id.announcement_description);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_announcement, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementAdapter.ViewHolder announcementHolder, final int i) {
        AnnouncementDetails curAnnouncement = announcementDetailsList.get(i);
        int textColor = Color.BLACK;

        if (curAnnouncement.images != null && curAnnouncement.images.size() > 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(curAnnouncement.images.get(0).toString());
            textColor = getFavourableTextColor(bitmap);
            announcementHolder.announcementImage.setImageBitmap(bitmap);
        }

        String announcementTitle = curAnnouncement.announcementTitle;
        announcementHolder.announcementTitle.setText(announcementTitle);

        String announcementDescription = curAnnouncement.announcementDescription;
        announcementHolder.announcementDescription.setText(announcementDescription);

        announcementHolder.announcementTitle.setTextColor(textColor);
//        announcementHolder.announcementDescription.setTextColor(textColor);
    }

    /**
     * Checks the color of the image
     * @param bitmap - Decoding image into string
     * @return  - favourable text color required
     */
    private int getFavourableTextColor(Bitmap bitmap) {
        int pixel = bitmap.getPixel(0, bitmap.getHeight() - 1);
        Color bgImageColor =  Color.valueOf(Color.rgb(Color.red(pixel), Color.green(pixel), Color.blue(pixel)));
        return bgImageColor.luminance() > 0.5 ? Color.BLACK : Color.WHITE;
    }


    @Override
    public int getItemCount() {
        return announcementDetailsList.size();
    }

}


