package x.com.nubextalk.Module.FaFont;

import android.content.Context;
import android.util.AttributeSet;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.internal.HasOnViewAttachListener;

import androidx.appcompat.widget.AppCompatRadioButton;

public class IconRadioButton extends AppCompatRadioButton {
    private HasOnViewAttachListener.HasOnViewAttachListenerDelegate delegate;

    public IconRadioButton(Context context) {
        super(context);
        init();
    }

    public IconRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IconRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setTransformationMethod(null);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(Iconify.compute(getContext(), text, this), type);
    }

    @Override
    public void addOnAttachStateChangeListener(OnAttachStateChangeListener listener) {
        super.addOnAttachStateChangeListener(listener);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
