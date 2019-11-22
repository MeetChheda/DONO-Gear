package com.example.donogear.actionpages.adapters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.donogear.utils.ItemAdapter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.LooperMode;

import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

@RunWith(RobolectricTestRunner.class)
@LooperMode(PAUSED)
public class GetFavourableColorTest {

    private ItemAdapter itemAdapter;

    @Before
    public void initData() {
        itemAdapter = new ItemAdapter();
    }

    @Test
    public void getWhiteTextTest() {
        Bitmap bitmap = createImage(100, 100, Color.BLACK);
        int textColor = itemAdapter.getFavourableTextColor(bitmap);
        Assert.assertEquals(Color.WHITE, textColor);
    }

    @Test
    public void getBlackTextTest() {
        Bitmap bitmap = createImage(100, 100, Color.WHITE);
        int textColor = itemAdapter.getFavourableTextColor(bitmap);
        Assert.assertEquals(Color.BLACK, textColor);
    }

    private static Bitmap createImage(int width, int height, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(color);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint);

        return bitmap;
    }
}
