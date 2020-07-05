package cn.xunyard.idea.coding.dialog;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-06
 */
public class BindingCheckBox extends JCheckBox {

    private final Consumer<Boolean> setter;

    public BindingCheckBox(Consumer<Boolean> setter, boolean select) {
        this.setter = setter;
        this.setSelected(select);

        this.addActionListener(e -> setter.accept(isSelected()));
    }

    @Override
    public void setSelected(boolean b) {
        super.setSelected(b);
        setter.accept(b);
    }

    public static final class BindingCheckBoxBuilder {

        private final Consumer<Boolean> setter;
        private boolean selected;
        private String text;

        private BindingCheckBoxBuilder(Consumer<Boolean> setter) {
            this.setter = setter;
        }

        public BindingCheckBoxBuilder selected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public BindingCheckBoxBuilder text(String text) {
            this.text = text;
            return this;
        }

        public BindingCheckBox build() {
            BindingCheckBox bindingCheckBox = new BindingCheckBox(setter, selected);
            bindingCheckBox.setText(text);
            return bindingCheckBox;
        }

        public static BindingCheckBoxBuilder from(Consumer<Boolean> setter) {
            return new BindingCheckBoxBuilder(setter);
        }
    }
}
