package com.sscl.blesample.wideget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * @author alm
 * Created by ALM on 2016/8/10.
 * 自定义圆形控件
 */
public class CustomTextCircleView extends View {
    private Paint paint;
    private int color;
    private int screenWidth;

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public CustomTextCircleView(Context context) {
        super(context);
    }

    /**
     * 构造方法
     *
     * @param context 上下文
     * @param attrs   参数
     */
    public CustomTextCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return;
        }
        Display defaultDisplay = windowManager.getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        screenWidth = point.x;
        color = Color.RED;
        paint = new Paint();
    }

    /**
     * 构造方法
     *
     * @param context      上下文
     * @param attrs        参数
     * @param defStyleAttr 风格
     */
    public CustomTextCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 在onDraw方法中画一个圆
     *
     * @param canvas 画内容的画布
     */
    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(color);

        //画一个圆心坐标为(40,40)，半径为屏幕宽度的1/30的圆
        canvas.drawCircle(40, 40, screenWidth / 30f, paint);
    }

    /**
     * 改变圆的颜色
     *
     * @param color 要改变的颜色值
     */
    public void setColor(int color) {
        this.color = color;
        postInvalidate();
    }

    /**
     * 计算圆形在屏幕中的大小(仅处理在match_parent时候的宽高)
     *
     * @param widthMeasureSpec  宽度的MeasureSpec
     * @param heightMeasureSpec 高度的MeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                width = screenWidth / 7;
                break;
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
            default:
                break;
        }

        switch (heightMode) {
            case MeasureSpec.AT_MOST:
                height = screenWidth / 7;
                break;
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
            default:
                break;
        }

        setMeasuredDimension(width, height);
    }
}
