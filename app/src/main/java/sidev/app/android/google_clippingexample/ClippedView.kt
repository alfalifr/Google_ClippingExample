package sidev.app.android.google_clippingexample

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View

class ClippedView(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
): View(context, attrs, defStyleAttr) {

  private val paint = Paint().apply {
    isAntiAlias = true
    strokeWidth = resources.getDimension(R.dimen.strokeWidth)
    textSize = resources.getDimension(R.dimen.textSize)
  }
  private val path = Path()

  private val clipRectRight = resources.getDimension(R.dimen.clipRectRight)
  private val clipRectLeft = resources.getDimension(R.dimen.clipRectLeft)
  private val clipRectTop = resources.getDimension(R.dimen.clipRectTop)
  private val clipRectBottom = resources.getDimension(R.dimen.clipRectBottom)

  private val rectInset = resources.getDimension(R.dimen.rectInset)
  private val smallRectOffset = resources.getDimension(R.dimen.smallRectOffset)

  private val circleRadius = resources.getDimension(R.dimen.circleRadius)

  private val textSize = resources.getDimension(R.dimen.textSize)
  private val textOffset = resources.getDimension(R.dimen.textOffset)

  private val column1 = rectInset
  private val column2 = column1 + rectInset + clipRectRight

  private val row1 = rectInset
  private val row2 = row1 + rectInset + clipRectBottom
  private val row3 = row2 + rectInset + clipRectBottom
  private val row4 = row3 + rectInset + clipRectBottom
  private val textRow = row4 + (1.5f * clipRectBottom)
  private val rejectRow = row4 + rectInset + clipRectBottom *2

  private val rectF = RectF(
    rectInset,
    rectInset,
    clipRectRight - rectInset,
    clipRectBottom - rectInset,
  )


  /**
   * Implement this to do your drawing.
   *
   * @param canvas the canvas on which the background will be drawn
   */
  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
    canvas?.also { canvas ->
      drawBackAndUnclippedRectangle(canvas)
      drawDifferenceClippingExample(canvas)
      drawCircularClippingExample(canvas)
      drawIntersectionClippingExample(canvas)
      drawCombinedClippingExample(canvas)
      drawRoundedRectangleClippingExample(canvas)
      drawOutsideClippingExample(canvas)
      drawTranslatedTextExample(canvas)
      drawSkewedTextExample(canvas)
      drawQuickRejectExample(canvas)
    }
  }

  private fun drawClippedRectangle(canvas: Canvas) {
    canvas.clipRect(
      clipRectLeft, clipRectTop,
      clipRectRight, clipRectBottom,
    )
    canvas.drawColor(Color.WHITE)

    paint.color = Color.RED
    canvas.drawLine(
      clipRectLeft, clipRectTop,
      clipRectRight, clipRectBottom,
      paint,
    )

    paint.color = Color.GREEN
    canvas.drawCircle(
      circleRadius, clipRectBottom - circleRadius,
      circleRadius, paint,
    )

    paint.color = Color.BLUE
    paint.textSize = textSize
    paint.textAlign = Paint.Align.RIGHT
    canvas.drawText(
      context.getString(R.string.clipping),
      clipRectRight, textOffset, paint,
    )
  }

  private fun drawBackAndUnclippedRectangle(canvas: Canvas){
    canvas.drawColor(Color.GRAY)
    canvas.save()
    canvas.translate(column1, row1)
    drawClippedRectangle(canvas)
    canvas.restore()
  }
  private fun drawDifferenceClippingExample(canvas: Canvas){
    canvas.save()
    canvas.translate(column1, row2)
    canvas.clipRect(
      rectInset * 2, rectInset * 2,
      clipRectRight - rectInset * 2,
      clipRectRight - rectInset * 2,
    )
    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
      canvas.clipRect(
        rectInset * 4, rectInset * 4,
        clipRectRight - rectInset * 4,
        clipRectRight - rectInset * 4,
        Region.Op.DIFFERENCE,
      )
    } else {
      canvas.clipOutRect(
        rectInset * 4, rectInset * 4,
        clipRectRight - rectInset * 4,
        clipRectRight - rectInset * 4,
      )
    }
    drawClippedRectangle(canvas)
    canvas.restore()
  }
  private fun drawCircularClippingExample(canvas: Canvas){
    canvas.save()
    canvas.translate(column1, row3)
    // Clears any lines and curves from the path but unlike reset(),
    // keeps the internal data structure for faster reuse.
    path.rewind()
    path.addCircle(
      circleRadius, clipRectBottom - circleRadius,
      circleRadius, Path.Direction.CCW,
    )
    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
      canvas.clipPath(path, Region.Op.DIFFERENCE)
    } else {
      canvas.clipOutPath(path)
    }
    drawClippedRectangle(canvas)
    canvas.restore()
  }
  private fun drawIntersectionClippingExample(canvas: Canvas){
    canvas.save()
    canvas.translate(column1, row4)
    canvas.clipRect(
      clipRectLeft, clipRectTop,
      clipRectRight - smallRectOffset,
      clipRectBottom - smallRectOffset,
    )

    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
      canvas.clipRect(
        clipRectLeft + smallRectOffset,
        clipRectTop + smallRectOffset,
        clipRectRight, clipRectBottom,
        Region.Op.INTERSECT
      )
    } else {
      canvas.clipRect(
        clipRectLeft + smallRectOffset,
        clipRectTop + smallRectOffset,
        clipRectRight, clipRectBottom,
      )
    }
    drawClippedRectangle(canvas)
    canvas.restore()
  }
  private fun drawCombinedClippingExample(canvas: Canvas){
    canvas.save()
    canvas.translate(column2, row1)

    path.rewind()
    path.addRect(
      clipRectRight / 2 - circleRadius,
      clipRectTop + circleRadius + rectInset,
      clipRectRight / 2 + circleRadius,
      clipRectBottom - rectInset,Path.Direction.CCW
    )
    path.addCircle(
      clipRectLeft + rectInset + circleRadius,
      clipRectTop + circleRadius + rectInset,
      circleRadius,Path.Direction.CCW
    )
    canvas.clipPath(path)
    drawClippedRectangle(canvas)
    canvas.restore()
  }
  private fun drawRoundedRectangleClippingExample(canvas: Canvas){
    canvas.save()
    canvas.translate(column2, row2)
    path.rewind()
    path.addRoundRect(
      rectF,
      clipRectRight / 4,
      clipRectRight / 4,
      Path.Direction.CCW,
    )
    canvas.clipPath(path)
    drawClippedRectangle(canvas)
    canvas.restore()
  }
  private fun drawOutsideClippingExample(canvas: Canvas){
    canvas.save()
    canvas.translate(column2,row3)
    canvas.clipRect(2 * rectInset,2 * rectInset,
      clipRectRight - 2 * rectInset,
      clipRectBottom - 2 * rectInset)
    drawClippedRectangle(canvas)
    canvas.restore()
  }
  private fun drawTranslatedTextExample(canvas: Canvas){
    canvas.save()
    paint.color = Color.GREEN
    paint.textAlign = Paint.Align.LEFT

    canvas.translate(column2, textRow)
    canvas.drawText(
      context.getString(R.string.translated),
      clipRectLeft,
      clipRectTop,
      paint,
    )

    canvas.restore()
  }
  private fun drawSkewedTextExample(canvas: Canvas){
    canvas.save()
    paint.apply {
      color = Color.YELLOW
      textAlign = Paint.Align.RIGHT
    }
    canvas.translate(column2, textRow)
    canvas.skew(.4f, .3f)
    canvas.drawText(
      context.getString(R.string.skewed),
      clipRectLeft,
      clipRectTop,
      paint,
    )
    canvas.restore()
  }
  private fun drawQuickRejectExample(canvas: Canvas){
    val inClipRectangle = RectF(
      clipRectRight / 2,
      clipRectBottom / 2,
      clipRectRight * 2,
      clipRectBottom * 2
    )
    val notInClipRectangle = RectF(
      //RectF(
        clipRectRight+1,
      clipRectBottom+1,
      clipRectRight * 2,
      clipRectBottom * 2
      //)
    )

    canvas.save()
    canvas.translate(column1, rejectRow)
    canvas.clipRect(
      clipRectLeft,
      clipRectTop,
      clipRectRight,
      clipRectBottom,
    )

    val selectedRect = notInClipRectangle

    //`canvas.quickReject()` returns true if the drawn rect isn't visible at all.
    val isRejected = if(Build.VERSION.SDK_INT >= 30) {
      canvas.quickReject(selectedRect)
    } else {
      canvas.quickReject(
        selectedRect, Canvas.EdgeType.AA,
      )
    }

    if(isRejected) {
      canvas.drawColor(Color.WHITE)
    } else {
      paint.color = Color.YELLOW
      canvas.drawColor(Color.BLACK)
      canvas.drawRect(selectedRect, paint)
    }

    canvas.restore()
  }
}