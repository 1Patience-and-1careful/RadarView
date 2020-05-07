package com.guanyc.somecustomview.view.path

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.wanzhi.radar.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


/**
 * @author : wanzhi  蛛网雷达图
 * @e-mail : 18039298508@163.com
 * @date : 2020-5-6  15:35
 * @desc :
 * 1.获得布局中心[onSizeChanged] centerX，centerY
 * 2.绘制雷达网络[drawPolygon],绘制的起始端点是在底部水平线的右侧端点
 * 3.绘制从中心到末端的直线[drawLines]
 * @version: 1.0
 */
class RadarView : View {
    var linePaint = Paint()// 蛛网线画笔
    var textPaint = Paint()// 文字画笔
    var valuePaint = Paint()// 雷达值画笔
    var data = arrayListOf<RadarData>()
        set(value) {
            data.clear()
            field = value
            if (data.size != count) {
                updateCount(data.size)
            }
            postInvalidate()
        }
    private var radius: Float? = null
    private var centerX: Float? = null
    var centerY: Float? = null
    var count: Int? = null
    var angle: Float? = null //多边形中心点到相邻两个边线的角度
    var startPointAngel: Double? = null //起始点角度 (底部水平,以水平线右边点为起始点)


    var maxValue: Float? = null //雷达图最大值

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private fun init(attrs: AttributeSet?) {

        linePaint.strokeWidth = 1f
        linePaint.isAntiAlias = true
        linePaint.style = Paint.Style.STROKE

        textPaint.color = Color.BLACK
        textPaint.strokeWidth = 1f
        textPaint.textSize = 28f
        textPaint.isAntiAlias = true
        textPaint.style = Paint.Style.FILL_AND_STROKE
        valuePaint.color = Color.BLUE
        valuePaint.style = Paint.Style.FILL_AND_STROKE

        val a = context.obtainStyledAttributes(attrs, R.styleable.RadarView)
        val lineColor = a.getColor(R.styleable.RadarView_r_line_color, Color.BLACK)
        val rCount = a.getInteger(R.styleable.RadarView_r_count, 6)
        val rMaxValue = a.getFloat(R.styleable.RadarView_r_maxValue, 1000f)
        setLineColor(lineColor)
        setCount(rCount)
        setMaxValue(rMaxValue)
        a.recycle()
    }

    private fun setMaxValue(rMaxValue: Float) {
        maxValue = rMaxValue

    }

    fun updateMaxValue(rMaxValue: Float) {
        setMaxValue(rMaxValue)
        postInvalidate()
    }

    private fun updateCount(rCount: Int) {
        setCount(rCount)
    }

    private fun setCount(rCount: Int) {
        count = rCount
        angle = (Math.PI * 2 / count!!).toFloat()
        startPointAngel = Math.PI / 2 - angle!! / 2
    }

    private fun setLineColor(lineColor: Int) {
        linePaint.color = lineColor
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(h, w) / 2 * 0.7f
        centerX = w / 2.toFloat()
        centerY = h / 2.toFloat()
        postInvalidate()
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        if (data.size == 0) return
        drawPolygon(canvas)
        drawLines(canvas)
        drawText(canvas)
        drawRegion(canvas)
    }

    // 画出底部水平的多边形
    private fun drawPolygon(canvas: Canvas?) {
        var path = Path()
        var r = radius?.div((count!! - 1))//蛛网间距

        for (i in 0 until count!!) {
            var curR = r?.times(i)
            for (j in 0 until count!!) {
                if (j == 0) {
                    path.moveTo(
                        centerX!! + (curR!! * cos(startPointAngel!!)).toFloat(),
                        centerY!! + (curR * sin(startPointAngel!!)).toFloat()
                    )
                } else {
                    path.lineTo(
                        getXPoint(j, curR),
                        getYPoint(j, curR)

                    )
                }
            }
            path.close()
            canvas?.drawPath(path, linePaint)
        }
    }

    //获取雷达图端点X坐标
    private fun getXPoint(i: Int, r: Float?): Float {
        return centerX!! + (r!! * cos(startPointAngel!! + angle!! * i)).toFloat()
    }

    //获取雷达图端点Y坐标
    private fun getYPoint(i: Int, r: Float?): Float {
        return centerY!! + (r!! * sin(startPointAngel!! + angle!! * i)).toFloat()
    }

    //绘制从中心到末端的直线
    private fun drawLines(canvas: Canvas?) {
        var path = Path()
        for (i in 0 until count!!) {
            path.reset()
            path.moveTo(centerX!!, centerY!!)
            path.lineTo(
                getXPoint(i, radius),
                getYPoint(i, radius)
            )
            canvas?.drawPath(path, linePaint)
        }
    }

    //绘制文字
    private fun drawText(canvas: Canvas?) {
        var fontMetrics = textPaint.fontMetrics
        //系统推荐的可绘制区域顶部的线[ascent] 系统推荐的可绘制区域顶部的线[descent] ,坐标系是向下的(字体的高度，底部区域减顶部区域)
        var fontH = fontMetrics.descent - fontMetrics.ascent
        for (i in 0 until count!!) {
            //末端加雷达半径的距离
            var x =
                centerX!! + (radius!! + fontH / 2) * cos(startPointAngel!! + angle!! * i).toFloat()
            var y =
                centerY!! + (radius!! + fontH / 2) * sin(startPointAngel!! + angle!! * i).toFloat()
            var pointAngel = angle!! * i + startPointAngel!!   //点的角度
            if ((pointAngel >= 0 && pointAngel <= Math.PI / 2) || pointAngel > Math.PI / 2 * 3) {  //第一象限和第四象限
                canvas?.drawText(data[i].prop, x, y, textPaint)
            } else {//第二  第三象限  即左边的文本需要在断点左侧个文本长度的地方开始绘制
                val dis = textPaint.measureText(data[i].prop)//文本长度
                canvas?.drawText(data[i].prop, x - dis, y, textPaint)
            }
        }
    }

    // 绘制覆盖区域
    private fun drawRegion(canvas: Canvas?) {
        var path = Path()
        valuePaint.alpha = 255
        for (i in 0 until count!!) {
            var percent = data[i].value!! / maxValue!!
            if (percent > 1.2) percent = 1.2f//如果绘制百分比大于1.2 ，按1.2绘制
            if (i == 0) {
                path.moveTo(
                    getXPoint(i, radius?.times(percent)),
                    getYPoint(i, radius?.times(percent))
                )
            } else {
                path.lineTo(
                    getXPoint(i, radius?.times(percent)),
                    getYPoint(i, radius?.times(percent))
                )
            }
            canvas?.drawCircle(
                getXPoint(i, radius?.times(percent)),
                getYPoint(i, radius?.times(percent)), 10f, valuePaint
            )

        }
        valuePaint.style = Paint.Style.STROKE
        path.close()
        canvas?.drawPath(path, valuePaint)
        valuePaint.alpha = 127
        valuePaint.style = Paint.Style.FILL_AND_STROKE
        canvas?.drawPath(path, valuePaint)
    }


}
