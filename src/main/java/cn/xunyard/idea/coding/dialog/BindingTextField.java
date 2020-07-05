package cn.xunyard.idea.coding.dialog;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.function.Consumer;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-06
 */
public class BindingTextField extends JTextField {

    private final Consumer<String> setter;

    public BindingTextField(Consumer<String> setter) {
        this.setter = setter;

        this.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // do nothing
            }

            @Override
            public void focusLost(FocusEvent e) {
                setter.accept(getText());
            }
        });
    }

    @Override
    public void setText(String t) {
        super.setText(t);
        setter.accept(t);
    }

    public static final class BindingTextFieldBuilder {

        private final Consumer<String> setter;
        private boolean enable = true;
        private String text;

        private BindingTextFieldBuilder(Consumer<String> setter) {
            this.setter = setter;
        }

        public BindingTextFieldBuilder enable(boolean enable) {
            this.enable = enable;
            return this;
        }

        public BindingTextFieldBuilder text(String text) {
            this.text = text;
            return this;
        }

        public BindingTextField build() {
            BindingTextField bindingCheckBox = new BindingTextField(setter);
            bindingCheckBox.setText(text);
            bindingCheckBox.setEnabled(enable);
            return bindingCheckBox;
        }

        public static BindingTextFieldBuilder from(Consumer<String> setter) {
            return new BindingTextFieldBuilder(setter);
        }
    }
}
