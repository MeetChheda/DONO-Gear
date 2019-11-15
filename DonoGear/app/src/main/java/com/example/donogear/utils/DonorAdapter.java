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
import com.example.donogear.models.DonorDetails;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


/**
 * TODO: Set on click listener for all items
 */
public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.ViewHolder>{

    private Context mContext;
    private List<DonorDetails> donorDetailsList;
//    private ItemClickListener itemClickListener;

    /**
     * Default constructor for donor adapter
     * @param context - Reference of Main activity
     * @param list - list of all donors
     */
    public DonorAdapter(Context context, List<DonorDetails> list) {
        mContext = context;
        donorDetailsList = list;
    }

    /**
     * Set cause list
     * @param newList - if list is updated, set new list of all causes
     */
    public void setDonorList(List<DonorDetails> newList) {
        donorDetailsList = newList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView donorImage;
        private TextView donorName, donorCategory;


        public ViewHolder(View itemView) {
            super(itemView);
            donorImage = itemView.findViewById(R.id.donor_image);
            donorName = itemView.findViewById(R.id.donor_name);
            donorCategory = itemView.findViewById(R.id.donor_category);

//            itemView.setOnClickListener(view ->
//                    itemClickListener.onItemClick(view, getAdapterPosition())
//            );
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_donor, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonorAdapter.ViewHolder donorHolder, final int i) {
        DonorDetails curDonor = donorDetailsList.get(i);
        int textColor = Color.BLACK;

        if (curDonor.images != null && curDonor.images.size() > 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(curDonor.images.get(0).toString());
            textColor = getFavourableTextColor(bitmap);
            donorHolder.donorImage.setImageBitmap(bitmap);
        }

        String donorName = curDonor.donorName;
        if(donorName.length() > 30) {
            donorName = donorName.substring(0, 30);
            donorName = donorName.substring(0, donorName.lastIndexOf(" ")) + "...";
        }
        donorHolder.donorName.setText(donorName);

        String donorCategory = curDonor.category;
        donorHolder.donorCategory.setText(donorCategory);

        donorHolder.donorName.setTextColor(textColor);
        donorHolder.donorCategory.setTextColor(textColor);
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
        return donorDetailsList.size();
    }

}
