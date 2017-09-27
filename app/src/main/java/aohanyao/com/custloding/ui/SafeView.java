package aohanyao.com.custloding.ui;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

import aohanyao.com.custloding.DensityUtils;

/**
 * <p>作者：江俊超 on 2016/8/17 17:04</p>
 * <p>邮箱：928692385@qq.com</p>
 * <p></p>
 */
public class SafeView extends View {
    private int mHeight;
    private int mWidth;
    private int mCy;
    private Paint mPaint;
    private LinearGradient mLeftLinearGradient, mRightLinearGradient;
    private ValueAnimator motionAnimator, touchAnimator;
    private float aninatopValue;
    private int OFFSET = 100;
    int safeCount = 0;
    private Camera camera = new Camera();
    private Matrix matrixCanvas = new Matrix();

    public SafeView(Context context) {
        this(context, null);
    }

    private float canvasRotateX = 0;
    private float canvasRotateY = 0;

    public SafeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public SafeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);// 圆角笔触
        mLeftLinearGradient = new LinearGradient(0, 0, mCy - OFFSET, mCy - OFFSET, 0xddffffff, 0x00000000, Shader.TileMode.CLAMP);
        mRightLinearGradient = new LinearGradient(mCy - OFFSET, mCy / 2, 0, mCy / 2, 0xddffffff, 0x00000000, Shader.TileMode.CLAMP);
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCy = Math.min(mWidth, mHeight);
        OFFSET = (int) (mCy * 0.25);
    }

    /**
     * 旋转画布
     *
     * @param canvas
     */
    private void rotateCanvas(Canvas canvas) {
        matrixCanvas.reset();
        camera.save();
        camera.rotateX(canvasRotateX);
        camera.rotateY(canvasRotateY);
        camera.getMatrix(matrixCanvas);
        camera.restore();
        int matrixCenterX = mCy / 2;
        int matrixCenterY = mCy / 2;
        matrixCanvas.preTranslate(-matrixCenterX, -matrixCenterY);
        matrixCanvas.postTranslate(matrixCenterX, matrixCenterY);
        canvas.concat(matrixCanvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                stopTouchAnim();
                rotateCanvasWhenMove(x, y);
                invalidate();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                rotateCanvasWhenMove(x, y);
                invalidate();
                return true;
            }
            case MotionEvent.ACTION_UP: {
                startBackAnimator();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 启动动画 回弹效果
     *
     */
    private void startBackAnimator() {
        PropertyValuesHolder xValuesHolder = PropertyValuesHolder.ofFloat("x", canvasRotateX, 0);
        PropertyValuesHolder yValuesHolder = PropertyValuesHolder.ofFloat("y", canvasRotateY, 0);
        touchAnimator = ValueAnimator.ofPropertyValuesHolder(xValuesHolder, yValuesHolder).setDuration(700);
        touchAnimator.setInterpolator(new BounceInterpolator());
        touchAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                canvasRotateY = (Float) animation.getAnimatedValue("y");
                canvasRotateX = (Float) animation.getAnimatedValue("x");
                invalidate();
            }
        });
        touchAnimator.start();
    }

    /**
     * 停止触摸
     */
    private void stopTouchAnim() {
        if (touchAnimator != null && touchAnimator.isRunning()) {
            touchAnimator.cancel();
            touchAnimator = null;
            canvasRotateX = 0;
            canvasRotateY = 0;
            invalidate();
        }
    }

    private void rotateCanvasWhenMove(float x, float y) {
        float dx = x - mWidth / 2;
        float dy = y - mHeight / 2;

        float perX = dx / (mWidth / 2);
        float perY = dy / (mHeight / 2);

        if (perX > 1f) {
            perX = 1f;
        } else if (perX < -1f) {
            perX = -1f;
        }
        if (perY > 1f) {
            perY = 1f;
        } else if (perY < -1f) {
            perY = -1f;
        }
        canvasRotateY = 30 * perX;
        canvasRotateX = -(30 * perY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rotateCanvas(canvas);
        mPaint.setShader(null);
        canvas.drawColor(0xff1782dd);
        int radius = mCy / 2;
        float angle;
        if (aninatopValue < 360f) {
            angle = aninatopValue;
        } else {
            int circleCount = (int) (aninatopValue / 360);
            angle = aninatopValue - (circleCount * 360f);
        }
        //绘制圆
        canvasCircle(canvas, radius, angle);
        //绘制文本
        canvasText(canvas, radius);
        //绘制旋转
        canvasRotate(canvas, radius, angle);

    }

    /**
     * //绘制文字
     *
     * @param canvas
     * @param radius
     */
    private void canvasText(Canvas canvas, int radius) {
        mPaint.setShader(null);//文字颜色
        mPaint.setColor(0x88ffffff);//转换字体
        mPaint.setTextSize(DensityUtils.sp2px(getContext(), 14));
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);
        String safeText = "已防御次数";
        canvas.drawText(safeText, radius - mPaint.measureText(safeText) / 2, (radius - mPaint.measureText(String.valueOf(safeText)) / 2), mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(0xffffffff);
        mPaint.setTextSize(DensityUtils.sp2px(getContext(), 30));
        mPaint.setStrokeWidth(2);
        canvas.drawText(String.valueOf(safeCount), radius - mPaint.measureText(String.valueOf(safeCount)) / 2, radius + radius / 4, mPaint);
    }

    /**
     * 绘制旋转部分
     */
    private void canvasRotate(Canvas canvas, int radius, float angle) {
        //旋转画布
        canvas.rotate(angle, radius, radius);
        //移动坐标到中心
        canvas.translate(radius, radius);
        mPaint.setShader(null);
        //左边的圆
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(5);
        canvas.drawCircle(-radius + OFFSET, 0, 10, mPaint);
        //右边的圆
        canvas.drawCircle(radius - OFFSET, 0, 10, mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8);
        //绘制坐标的圆的尾巴
        RectF mRectF = new RectF(-radius + OFFSET, -radius + OFFSET, radius - OFFSET, radius - OFFSET);
        mPaint.setShader(mRightLinearGradient);
        canvas.drawArc(mRectF, 15, 180 - 15, false, mPaint);
        mPaint.setShader(mLeftLinearGradient);
        canvas.drawArc(mRectF, 180 + 15, 180 - 15, false, mPaint);
        //合并画布
        canvas.restore();
    }

    /**
     * @param canvas
     * @param radius 直径
     * @param angle  当前角度
     */
    private void canvasCircle(Canvas canvas, int radius, float angle) {
        float percentage = angle / 360f;//当前角度百分比
        mPaint.setColor(0xddffffff);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1.5f);
        //绘制中心圆圈
        canvas.drawCircle(radius, radius, radius - OFFSET, mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(0x22ffffff);


        if (aninatopValue >= 90) {
            //绘制第二圆圈
            canvas.drawCircle(radius, radius, radius - OFFSET + 40 * percentage, mPaint);
        }
        mPaint.setColor(0x11ffffff);
        if (aninatopValue >= 180) {
            //绘制第三圆圈
            canvas.drawCircle(radius, radius, radius - OFFSET + 80 * percentage, mPaint);
        }
        if (aninatopValue >= 270) {
            //绘制第四圆圈
            canvas.drawCircle(radius, radius, radius - OFFSET + 120 * percentage, mPaint);
        }
        if (aninatopValue <= 90) {// 初始状态下绘制所有的圆圈
            mPaint.setColor(0x22ffffff);
            canvas.drawCircle(radius, radius, radius - OFFSET + 40, mPaint);
            mPaint.setColor(0x11ffffff);
            canvas.drawCircle(radius, radius, radius - OFFSET + 80, mPaint);
            canvas.drawCircle(radius, radius, radius - OFFSET + 120, mPaint);
        }
        mPaint.setColor(0xff1782dd);//绘制中心圆为背景颜色
        canvas.drawCircle(radius, radius, radius - OFFSET, mPaint);
        mPaint.setColor(0x55ffffff);
        //渲染 得到中心的 凹凸效果
        RadialGradient radialGradient = new RadialGradient(radius, radius, radius - OFFSET, new int[]{0x00000000, 0xffffffff}, null, Shader.TileMode.CLAMP);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(radius, radius, radius - OFFSET, mPaint);
    }

    /**
     * 开始动画
     *
     * @param circleCount
     * @param duration
     */
    public void start(final int circleCount, final int duration) {
        if (motionAnimator != null && motionAnimator.isRunning()) {
            motionAnimator.cancel();
            motionAnimator = null;

        } else {
            motionAnimator = ValueAnimator.ofFloat(circleCount * 360f).setDuration(duration);
            motionAnimator.setInterpolator(new LinearInterpolator());
            motionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    aninatopValue = (float) animation.getAnimatedValue();
                    postInvalidate();
                    safeCount++;
                    if (aninatopValue >= circleCount * 360f) {
                        startLoopAnimator();
                    }
                }
            });
            safeCount = 0;
            motionAnimator.start();
        }
    }

    /**
     * 开始
     */
    private void startLoopAnimator() {
        motionAnimator = ValueAnimator.ofFloat(360f).setDuration(1500);
        motionAnimator.setInterpolator(new LinearInterpolator());
        motionAnimator.setRepeatCount(ValueAnimator.INFINITE);
        motionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                safeCount++;
                aninatopValue = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        motionAnimator.start();
    }
}
