package dev.yaky.usbcamviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ParadeView extends View {
    private Bitmap scopeBitmap;
    private int[] pixels;
    private final int SCOPE_W = 360;
    private final int SCOPE_H = 128;
    private final Rect srcRect = new Rect(0, 0, SCOPE_W, SCOPE_H);
    private final Rect dstRect = new Rect();
    private final Paint paint = new Paint();

    public ParadeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        scopeBitmap = Bitmap.createBitmap(SCOPE_W, SCOPE_H, Bitmap.Config.ARGB_8888);
        pixels = new int[SCOPE_W * SCOPE_H];
    }

    public void updateData(ByteBuffer frame, int imgW, int imgH) {
        if (getVisibility() != View.VISIBLE || frame == null) return;

        // 1. Czyścimy PRZEZROCZYSTOŚCIĄ, a nie czernią!
        // Dzięki temu widać tło z zaokrąglonymi rogami z XML
        Arrays.fill(pixels, 0x00000000);

        byte[] data = new byte[frame.capacity()];
        frame.get(data);
        frame.position(0);

        int stepX = 16;
        int stepY = 16;
        int offsetR = 0;
        int offsetG = SCOPE_W / 3;
        int offsetB = (SCOPE_W / 3) * 2;
        int colWidth = SCOPE_W / 3;

        for (int y = 0; y < imgH; y += stepY) {
            for (int x = 0; x < imgW; x += stepX) {
                int yIndex = y * imgW + x;
                int uvIndex = imgW * imgH + (y / 2) * imgW + (x & ~1);

                if (yIndex >= data.length || uvIndex + 1 >= data.length) continue;

                int Y = data[yIndex] & 0xff;
                int V = data[uvIndex] & 0xff;
                int U = data[uvIndex + 1] & 0xff;

                int c = Y - 16;
                int d = U - 128;
                int e = V - 128;

                int r = (298 * c + 409 * e + 128) >> 8;
                int g = (298 * c - 100 * d - 208 * e + 128) >> 8;
                int b = (298 * c + 516 * d + 128) >> 8;

                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);

                int plotX_local = (x * colWidth) / imgW;

                int plotY_R = SCOPE_H - 1 - (r * SCOPE_H / 256);
                int plotY_G = SCOPE_H - 1 - (g * SCOPE_H / 256);
                int plotY_B = SCOPE_H - 1 - (b * SCOPE_H / 256);

                if (plotY_R < 0) plotY_R = 0; if (plotY_R >= SCOPE_H) plotY_R = SCOPE_H - 1;
                if (plotY_G < 0) plotY_G = 0; if (plotY_G >= SCOPE_H) plotY_G = SCOPE_H - 1;
                if (plotY_B < 0) plotY_B = 0; if (plotY_B >= SCOPE_H) plotY_B = SCOPE_H - 1;

                // Tutaj nadal używamy pełnych kolorów, one będą "wisiały" w powietrzu
                int idxR = plotY_R * SCOPE_W + (offsetR + plotX_local);
                pixels[idxR] = 0xFFFF0000;

                int idxG = plotY_G * SCOPE_W + (offsetG + plotX_local);
                pixels[idxG] = 0xFF00FF00;

                int idxB = plotY_B * SCOPE_W + (offsetB + plotX_local);
                pixels[idxB] = 0xFF0000FF;
            }
        }

        scopeBitmap.setPixels(pixels, 0, SCOPE_W, 0, 0, SCOPE_W, SCOPE_H);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getVisibility() != View.VISIBLE) return;

        // 2. Skalowanie z uwzględnieniem Paddingu!
        // Dzięki temu bitmapa nie wyjdzie poza obszar ramki i nie zasłoni rogów.
        dstRect.set(
                getPaddingLeft(),
                getPaddingTop(),
                getWidth() - getPaddingRight(),
                getHeight() - getPaddingBottom()
        );

        canvas.drawBitmap(scopeBitmap, srcRect, dstRect, paint);

        // Linie pomocnicze (trójpodział)
        paint.setColor(Color.LTGRAY); // Jasnoszary zamiast białego, mniej bije po oczach
        paint.setStrokeWidth(2);
        paint.setAlpha(100); // Lekko przezroczyste

        // Obliczamy szerokość roboczą
        float contentW = getWidth() - getPaddingLeft() - getPaddingRight();
        float startX = getPaddingLeft();
        float third = contentW / 3.0f;
        float h = getHeight() - getPaddingBottom();

        // Rysujemy linie wewnątrz paddingu
        canvas.drawLine(startX + third, getPaddingTop(), startX + third, h, paint);
        canvas.drawLine(startX + third * 2, getPaddingTop(), startX + third * 2, h, paint);

        // Reset pędzla
        paint.setAlpha(255);
    }
}