package dev.yaky.usbcamviewer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class HistogramView extends View {
    private int[] binsR = new int[256];
    private int[] binsG = new int[256];
    private int[] binsB = new int[256];

    private final Paint paintR = new Paint();
    private final Paint paintG = new Paint();
    private final Paint paintB = new Paint();
    private final Paint paintZone = new Paint();

    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Inicjalizacja pędzli
        paintR.setColor(0xDDFFFFFF);
        paintR.setStrokeWidth(3f);
        paintR.setStyle(Paint.Style.STROKE);
        paintR.setAntiAlias(true);

        paintG.setColor(0xDDFFFFFF);
        paintG.setStrokeWidth(3f);
        paintG.setStyle(Paint.Style.STROKE);
        paintG.setAntiAlias(true);

        paintB.setColor(0xDDFFFFFF);
        paintB.setStrokeWidth(3f);
        paintB.setStyle(Paint.Style.STROKE);
        paintB.setAntiAlias(true);

        paintZone.setStyle(Paint.Style.FILL);
    }

    public void updateData(int[] r, int[] g, int[] b) {
        // Jeśli widok jest ukryty, nie marnuj zasobów na kopiowanie danych
        if (getVisibility() != View.VISIBLE) return;

        this.binsR = r;
        this.binsG = g;
        this.binsB = b;

        // Wymuś przerysowanie na wątku UI
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Jeśli widok jest ukryty, Android i tak nie wywoła onDraw,
        // ale to dodatkowe zabezpieczenie.
        if (getVisibility() != View.VISIBLE) return;

        float contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float contentHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        if (contentWidth <= 0 || contentHeight <= 0) return;

        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());

        // --- STREFY OSTRZEGAWCZE ---
        float stepX = contentWidth / 256.0f;
        int zoneSize = 12;
        paintZone.setColor(0x30CCCCCC);

        canvas.drawRect(0, 0, stepX * zoneSize, contentHeight, paintZone);
        canvas.drawRect(stepX * (256 - zoneSize), 0, contentWidth, contentHeight, paintZone);

        // --- NORMALIZACJA ---
        int max = 1;
        for (int i = 0; i < 256; i++) {
            if (binsR[i] > max) max = binsR[i];
            if (binsG[i] > max) max = binsG[i];
            if (binsB[i] > max) max = binsB[i];
        }

        // --- RYSOWANIE LINII ---
        for (int i = 0; i < 255; i++) {
            float x1 = i * stepX;
            float x2 = (i + 1) * stepX;

            // Rysujemy od dołu do góry
            canvas.drawLine(x1, contentHeight - ((float)binsR[i] / max * contentHeight),
                    x2, contentHeight - ((float)binsR[i+1] / max * contentHeight), paintR);

            canvas.drawLine(x1, contentHeight - ((float)binsG[i] / max * contentHeight),
                    x2, contentHeight - ((float)binsG[i+1] / max * contentHeight), paintG);

            canvas.drawLine(x1, contentHeight - ((float)binsB[i] / max * contentHeight),
                    x2, contentHeight - ((float)binsB[i+1] / max * contentHeight), paintB);
        }

        canvas.restore();
    }
}