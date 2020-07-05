package cn.xunyard.idea.coding.i18n.action;

import cn.xunyard.idea.coding.i18n.dialog.SettingTutorialDialogWrapper;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-12
 */
public class I18nSettingsAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        SettingTutorialDialogWrapper dialogWrapper = new SettingTutorialDialogWrapper(e.getProject());
        dialogWrapper.showAndGet();
    }
}
