package com.example.donogear.utils;

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
public class ItemAdapterTest {

    private ItemAdapter itemAdapter;

    @Before
    public void initData() {
        itemAdapter = new ItemAdapter();
    }

    /**
     * Test to check if the background image has a dark color (eg BLACK) which will give us a WHITE
     * text as the output. This white-text will be placed on top of the dark image
     */
    @Test
    public void getWhiteTextTest() {
        Bitmap bitmap = createImage(100, 100, Color.BLACK);
        int textColor = itemAdapter.getFavourableTextColor(bitmap);
        Assert.assertEquals(Color.WHITE, textColor);
    }

    /**
     * Test to check if the background image has a light color (eg WHITE) which will give us a BLACK
     * text as the output. This black-text will be placed on top of the light image
     */
    @Test
    public void getBlackTextTest() {
        Bitmap bitmap = createImage(100, 100, Color.WHITE);
        int textColor = itemAdapter.getFavourableTextColor(bitmap);
        Assert.assertEquals(Color.BLACK, textColor);
    }

    /**
     * Helper method to create a sample image of any color
     * @param width - width of the image
     * @param height - height of the image
     * @param color - color of the image
     * @return image as a bitmap
     */
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
