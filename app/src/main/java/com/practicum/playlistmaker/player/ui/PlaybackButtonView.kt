package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.practicum.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var state: Int = STATE_PLAY
    private val playImageBitmap: Bitmap?
    private val pauseImageBitmap: Bitmap?
    private var imageRect = RectF(0f, 0f, 0f, 0f)
    private var onClickListener: OnClickListener? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                playImageBitmap =
                    getDrawable(R.styleable.PlaybackButtonView_playImageResId)?.toBitmap()
                pauseImageBitmap =
                    getDrawable(R.styleable.PlaybackButtonView_pauseImageResId)?.toBitmap()

            } finally {
                recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        when (state) {
            STATE_PLAY -> {
                if (playImageBitmap != null) {
                    canvas.drawBitmap(playImageBitmap, null, imageRect, null)
                }
            }

            STATE_PAUSE -> {
                if (pauseImageBitmap != null) {
                    canvas.drawBitmap(pauseImageBitmap, null, imageRect, null)
                }
            }
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        onClickListener = l
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }

            MotionEvent.ACTION_UP -> {
                onClickListener?.onClick(this)
                changeButtonState()

                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun changeButtonState() {
        setButtonState(
            if (state == STATE_PLAY) STATE_PAUSE else STATE_PLAY
        )
    }

    fun setStatePlay() {
        setButtonState(STATE_PLAY)
    }

    private fun setButtonState(buttonState: Int) {
        if (state != buttonState) {
            state = buttonState
            invalidate()
        }
    }

    private companion object {
        const val STATE_PLAY = 0
        const val STATE_PAUSE = 1
    }
}