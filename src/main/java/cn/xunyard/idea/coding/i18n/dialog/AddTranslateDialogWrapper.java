package cn.xunyard.idea.coding.i18n.dialog;

import cn.xunyard.idea.coding.i18n.logic.LanguageTranslate;
import cn.xunyard.idea.coding.util.AssertUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-11
 */
public class AddTranslateDialogWrapper extends DialogWrapper {
    private String errorCode;
    private LanguageTranslate languageTranslate;
    private AddTranslate addTranslate;

    public AddTranslateDialogWrapper(@Nullable Project project, String errorCode, LanguageTranslate languageTranslate) {
        super(project);
        this.errorCode = errorCode;
        this.languageTranslate = languageTranslate;
        init();
        setTitle("设置错误码翻译");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        addTranslate = new AddTranslate(errorCode, languageTranslate);
        return addTranslate;
    }

    @Override
    protected void doOKAction() {
        // 检查配置操作，并执行
        for (JTextField textField : addTranslate.getAllTextFields()) {
            if (AssertUtils.isEmpty(textField.getText())) {
                Messages.showMessageDialog("部分翻译尚未填写", "错误", Messages.getErrorIcon());
                return;
            }
        }

        super.doOKAction();
    }

    @Override
    public void doCancelAction() {
        // 什么都不做，禁止关闭对话框
    }

    /**
     * 覆写，不返回取消按钮
     *
     * @return 无取消按钮的action集合
     */
    @NotNull
    @Override
    protected Action[] createActions() {
        Action helpAction = getHelpAction();
        return helpAction == myHelpAction && getHelpId() == null ?
                new Action[]{getOKAction()} :
                new Action[]{getOKAction(), helpAction};
    }
}
