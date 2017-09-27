### SafeView
这是我模仿的一个效果，要不断的进步，就要不断的学习。主要实现的效果是一个转动的效果和一个3D触摸效果。请看效果图（录制的图片有点大）：
![效果图](http://upload-images.jianshu.io/upload_images/1760510-71c49427914de0a0.gif?imageMogr2/auto-orient/strip)

### 知识点
    1. Canvas
    2. Paint
    3. Shader
    4. Camera(不是相机)
    5. Animator

### 分析
整个View看上去是有四个圆组成，内中心的圆对边上有一个小圆，圆上面还有小尾巴。其实主要的原理是使用ValueAnimator来发散一个圆周的值，也就是360度，然后每次发散后旋转画布就能得到上面的效果。

### 技术点
1. android.graphics.Camera是Android官方提供的3D效果组件，使用起来十分的简单，关键在于理解其中的x,y,z轴。（有空再写一篇Camera的使用）
2. Sharder 阴影渲染
    1. 本View中主要使用了LinearGradient和RadialGradient。
        1. LinearGradient为线性渐变，在这里主要是用来画小圆的尾巴。
        2. RadialGradient为径向渐变，从圆心向外四周扩散，在这里是用来画中心圆的效果。

3. Animator 自3.0后推出的属性动画，本文主要使用ValueAnimator。
### CODE
#### 1.初始化 && Measure
将一些全局的值在构方法中进行初始化并且测量View的宽高等。

```java

          mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);// 圆角笔触
        mLeftLinearGradient = new LinearGradient(0, 0, mCy - OFFSET, mCy - OFFSET, 0xddffffff, 0x00000000, Shader.TileMode.CLAMP);//左边的尾巴
        mRightLinearGradient = new LinearGradient(mCy - OFFSET, mCy / 2, 0, mCy / 2, 0xddffffff, 0x00000000, Shader.TileMode.CLAMP);//右边的尾巴
```

#### 2.绘制所有的背景圆
圆的颜色是从中心开始不断的淡化的，其实这只要更改透明度就可以完成这样的效果
    
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

效果图：

![绘制完所有的圆](http://upload-images.jianshu.io/upload_images/1760510-87bde443d0f143ad.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 3.绘制中间的文字

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

![绘制中间的文字](http://upload-images.jianshu.io/upload_images/1760510-7d05ed1d3fddba25.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 4.绘制左右两个圆点并旋转
到这里，基本绘制就已经完成了，只要加上角度的计算和动画部分，这个View就能够动起来了。

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
![绘制左右两个圆点](http://upload-images.jianshu.io/upload_images/1760510-112944d00f04ace7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#### 5.动画与角度

    /**
     * 开始
     */
    private void startLoopAnimator() {
        //发散350度
        motionAnimator = ValueAnimator.ofFloat(360f).setDuration(1500);
        //线性差值器
        motionAnimator.setInterpolator(new LinearInterpolator());
        //永远循环
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
![加入动画后完成](http://upload-images.jianshu.io/upload_images/1760510-10cb2bfc922bf8b4.gif?imageMogr2/auto-orient/strip)

#### 最后

[源码](https://github.com/aohanyao/SafeView)