package cn.xunyard.idea.coding.i18n.dialog;

import cn.xunyard.idea.coding.dialog.BindingTextField;
import cn.xunyard.idea.coding.i18n.logic.LanguageTranslate;
import cn.xunyard.idea.coding.util.AssertUtils;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-11
 */
public class AddTranslate extends JPanel {

    private java.util.List<JTextField> jTextFieldList;
    private boolean focused = false;

    public AddTranslate(String errorCode, LanguageTranslate languageTranslate) {
        this.setLayout(new VerticalFlowLayout());
        addTitle(errorCode);
        jTextFieldList = new ArrayList<>();
        addTranslate(errorCode, languageTranslate);
    }

    private void addTitle(String errorCode) {
        Panel container = new Panel(new FlowLayout());
        Label label = new Label(String.format("错误码: %s", errorCode));
        container.add(label);
        this.add(container);
    }

    private void addTranslate(String errorCode, LanguageTranslate languageTranslate) {
        GridBagLayout layout = new GridBagLayout();
        layout.columnWidths = new int[]{50, 300};

        Panel panel = new Panel(layout);
        int i = 0;
        for (String language : languageTranslate.getLanguages()) {
            addSingleTranslate(panel, i++, language, translate -> languageTranslate.setTranslate(language, errorCode, translate),
                    languageTranslate.getTranslate(language, errorCode));
        }
        panel.setPreferredSize(new Dimension(400, 35 * i));
        this.add(panel);
    }

    public java.util.List<JTextField> getAllTextFields() {
        return jTextFieldList;
    }

    private void addSingleTranslate(Panel container, int index, @NotNull String language, @NotNull Consumer<String> setter, @Nullable String translate) {
        GridBagConstraints languageConstraints = new GridBagConstraints(0, index, 1, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, JBUI.insets(0), 0, 0);
        container.add(new JLabel(String.format("%s:", language)), languageConstraints);

        GridBagConstraints translateConstraints = new GridBagConstraints(1, index, 1, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, JBUI.insets(0), 0, 0);
        BindingTextField textField = BindingTextField.BindingTextFieldBuilder.from(setter)
                .text(translate)
                .build();

        if (!AssertUtils.isEmpty(translate)) {
            textField.setEnabled(false);
            textField.setToolTipText("已设置的翻译，禁止在此编辑");
        } else if (!focused){
            textField.requestFocus(true);
            focused = true;
        }
        textField.setPreferredSize(new Dimension(290, 35));
        jTextFieldList.add(textField);
        container.add(textField, translateConstraints);
    }
}
