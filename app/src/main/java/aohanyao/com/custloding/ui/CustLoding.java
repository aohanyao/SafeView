package aohanyao.com.custloding.ui;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * <p>作者：江俊超 on 2016/8/17 15:02</p>
 * <p>邮箱：928692385@qq.com</p>
 * <p>笑脸的loding</p>
 */
public class CustLoding extends View {
    private int mWidth;
    private int mHeight;
    private Paint mPaint;
    private ValueAnimator valueAnimator;
    private float animatorValue;
    long animatorDuration = 5000;
    private TimeInterpolator timeInterpolator = new DecelerateInterpolator();

    public CustLoding(Context context) {
        this(context, null);
    }

    public CustLoding(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustLoding(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureSize(widthMeasureSpec) + getPaddingLeft() + getPaddingRight();
        int height = measureSize(heightMeasureSpec) + getPaddingBottom() + getPaddingTop();
        setMeasuredDimension(width, height);
    }

    /**
     * 测量宽高 模式
     *
     * @param measureSpec
     * @return
     */
    private int measureSize(int measureSpec) {
        int result;
        int size = MeasureSpec.getSize(measureSpec);
        int mode = MeasureSpec.getMode(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = 200;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }


    /**
     * 初始化操作
     */
    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mWidth / 2, mHeight / 2);

        drawAnimator(canvas, mPaint);
    }

    /**
     * 绘制动画
     *
     * @param canvas
     * @param mPaint
     */
    private void drawAnimator(Canvas canvas, Paint mPaint) {
        mPaint.setStyle(Paint.Style.STROKE);//描边
        mPaint.setStrokeCap(Paint.Cap.ROUND);// 圆角笔触
        mPaint.setColor(Color.rgb(97, 195, 109));
        mPaint.setStrokeWidth(15);

        float point = Math.min(mWidth, mHeight) * 0.5f / 2;
        float r = point * (float) Math.sqrt(2);
        RectF rectF = new RectF(-r, -r, r, r);
        canvas.save();

        if (animatorValue >= 135) {
            canvas.rotate(animatorValue - 135);
        }
        canvas.drawPoints(new float[]{-point, -point,
                point, -point}, mPaint);
        float startAngle, sweepAng;
        if (animatorValue < 135) {
            startAngle = animatorValue + 5;
            sweepAng = 170 + animatorValue / 3;
        } else if (animatorValue < 270) {
            startAngle = 135 + 5;
            sweepAng = 170 + animatorValue / 3;
        } else if (animatorValue < 630) {
            startAngle = 135 + 5;
            sweepAng = 260 - (animatorValue - 270) / 5;
        } else if (animatorValue < 720) {
            startAngle = 135 - (animatorValue - 630) / 2 + 5;
            sweepAng = 260 - (animatorValue - 270) / 5;
        } else {
            startAngle = 135 - (animatorValue - 630) / 2 - (animatorValue - 720) / 6 + 5;
            sweepAng = 170;
        }
       // canvas.drawArc(rectF, startAngle, sweepAng, false, mPaint);

        canvas.restore();
    }

    public void initAnimator(long duration) {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
            valueAnimator.start();
        } else {
            valueAnimator = ValueAnimator.ofFloat(0, 855).setDuration(duration);
            valueAnimator.setInterpolator(timeInterpolator);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    animatorValue = (float) animation.getAnimatedValue();
                   postInvalidate();
                }
            });
            valueAnimator.start();
        }
    }
}
