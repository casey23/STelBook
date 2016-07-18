package com.jay.CustomView;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 通讯录索引菜单控件
 * Created by Jay on 2016/7/17.
 */
public class IndexView extends View {
    // 索引项，默认为# + 26个字母
    private String[] mIndex = {"#", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z"};

    // 绘制索引项的画笔
    private Paint mIndexPaint = new Paint();
    // 索引项的默认宽度 30dp
    private int mIndexWidth = dp2px(30);
    // 是否被按下
    private boolean isTouch = false;
    // 是否显示悬浮窗口
    private boolean isShowPopWindow = true;

    // 使用Animator实现定时一秒后隐藏悬浮窗
    private ValueAnimator valueAnimator;

    // 索引文字大小
    private int mIndexTextSize = sp2px(10);
    // 每一个文字占用的高度
    private float mTextHeight;
    // 索引文字的内边距
    private int mTextPadding = dp2px(1);
    // 索引文字默认颜色
    private int TEXT_DEFAULT_COLOR = 0xff898989;
    // 索引文字被按压的颜色
    private int TEXT_TOUCH_COLOR = 0xff3399FF;
    // 悬浮窗口文字的颜色
    private int TEXT_POPWINDOW_COLOR = 0xff3399FF;
    // 索引被按下的背景色
    private int TOUCH_BACKGROUP_COLOR = 0x88cccccc;
    // 被按下的索引位置
    private int mSelPos = -1;

    // 显示的索引悬浮窗
    private TextView mIndexWindow;
    // 悬浮窗参数
    private WindowManager.LayoutParams lp;
    // 窗口管理类
    private WindowManager windowManager;
    // 悬浮窗是否存在
    private boolean Alive = false;

    // 回调接口
    private OnIndexTouchListener onTouchListener;

    public IndexView(Context context) {
        this(context, null);
    }

    public IndexView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPopWindow(context);
    }

    // 初始化悬浮窗口
    private void initPopWindow(Context context) {
        // mIndexWindow是一个TextView private TextView mIndexWindow;
        mIndexWindow = new TextView(context);
        mIndexWindow.setLayoutParams(new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT));
        mIndexWindow.setMinWidth(dp2px(90));
        mIndexWindow.setBackgroundColor(0x00000000);
        mIndexWindow.setGravity(Gravity.CENTER);
        mIndexWindow.setTextSize(sp2px(40));
        mIndexWindow.setTextColor(TEXT_POPWINDOW_COLOR);
        lp = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        // windowManager.addView(mIndexWindow, lp);

        // 这里使用valueAnimator来延时，当结束的时候隐藏悬浮窗
        valueAnimator = ValueAnimator.ofFloat(0, 0);
        valueAnimator.setDuration(1000);
        valueAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //如果悬浮窗显示，则销毁
                if (Alive) {
                    Alive = false;
                    windowManager.removeViewImmediate(mIndexWindow);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
    }

    /**
     * 当拥有此控件的Activity被销毁的时候，如果悬浮窗还存在，则删除悬浮窗
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //如果悬浮窗显示，则销毁
        if (Alive) {
            Alive = false;
            windowManager.removeViewImmediate(mIndexWindow);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 获取父布局传递进来的宽高
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // 如果是wrap_content,选取默认宽度和实际宽度的较小值，否则使用父布局指定的大小
        if (widthMode == MeasureSpec.AT_MOST) {
            mIndexWidth = Math.min(widthSize, mIndexWidth);
        } else {
            mIndexWidth = widthSize;
        }

        // 计算出每一个文字占用的高度,加0.0f的原因是防止精度丢失(小数点后面的位数)
        mTextHeight = (heightSize + 0.0f) / mIndex.length;
        // 计算出文字的Size,不允许超过索引条的宽度大小
        mIndexTextSize = (int) (mTextHeight > mIndexWidth ? mIndexWidth
                : mTextHeight);

        // 设置测量的结果
        setMeasuredDimension(
                MeasureSpec.makeMeasureSpec(mIndexWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 如果手指按下了，则绘制背景
        if (isTouch) {
            canvas.drawColor(TOUCH_BACKGROUP_COLOR);
        }
        // 设置画笔
        mIndexPaint.setTextSize(mIndexTextSize - 2 * mTextPadding);
        mIndexPaint.setAntiAlias(true);
        mIndexPaint.setStyle(Paint.Style.STROKE);
        // 绘制索引项
        for (int i = 0; i < mIndex.length; i++) {
            if (i != mSelPos) {
                mIndexPaint.setColor(TEXT_DEFAULT_COLOR);
            } else {
                mIndexPaint.setColor(TEXT_TOUCH_COLOR);
            }
            // 水平居中
            float left = (mIndexWidth - mIndexPaint.measureText(mIndex[i])) / 2;
            // 垂直居中
            float top = i * mTextHeight + mTextPadding;
            Paint.FontMetrics metrics = mIndexPaint.getFontMetrics();
            // 因为绘制文字是基于基线位置的，所以需要转换一下
            top = top - metrics.ascent;
            canvas.drawText(mIndex[i], left, top, mIndexPaint);
        }
    }

    // 监听Touch事件
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 如果延时1秒还未到，取消掉
                if (valueAnimator.isRunning()) {
                    valueAnimator.cancel();
                }
                // 设置为按下状态，绘制背景
                isTouch = true;
                mSelPos = getTouchPos(y);
                break;
            case MotionEvent.ACTION_MOVE:
                mSelPos = getTouchPos(y);
                break;
            case MotionEvent.ACTION_UP:
                // 按下状态取消，不绘制背景
                isTouch = false;
                mSelPos = -1;
                // 开始延时效果
                valueAnimator.start();
                break;
        }
        postInvalidate();
        // 调用回调接口
        if (onTouchListener != null && mSelPos >= 0 && mSelPos < mIndex.length) {
            onTouchListener.OnIndexTouch(mIndex[mSelPos]);
        }
        // 设置悬浮窗可见并设置悬浮窗文字
        if (mSelPos != -1 && isShowPopWindow && mSelPos >= 0 && mSelPos < mIndex.length) {
            mIndexWindow.setText(mIndex[mSelPos]);
            if (!Alive) {
                Alive = true;
                windowManager.addView(mIndexWindow, lp);
            }
        }
        return true;
    }

    // 点击回调接口
    public interface OnIndexTouchListener {
        // 回调接口，返回当前选中的字母
        public void OnIndexTouch(String sel);
    }

    // 设置回调接口
    public void setOnIndexTouchListener(OnIndexTouchListener listener) {
        this.onTouchListener = listener;
    }

    // 根据点击的Y左边计算点击的项
    private int getTouchPos(float y) {
        return (int) (y / mTextHeight - 0.5f);
    }

    // dp 转 px
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    // sp 转 px
    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }

    // 设置是否显示悬浮窗
    public void showPopWindow(boolean show) {
        isShowPopWindow = show;
    }
}

