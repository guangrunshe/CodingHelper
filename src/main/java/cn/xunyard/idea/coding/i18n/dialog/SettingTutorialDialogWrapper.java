package cn.xunyard.idea.coding.i18n.dialog;

import cn.xunyard.idea.coding.i18n.logic.LanguageConfiguration;
import cn.xunyard.idea.coding.i18n.logic.TranslateProcessContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-12
 */
public class SettingTutorialDialogWrapper extends DialogWrapper {
    private Project project;
    private SettingTutorial settingTutorial;

    public SettingTutorialDialogWrapper(Project project) {
        super(false);
        this.project = project;
        init();
        setTitle("错误码翻译设置");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        settingTutorial = new SettingTutorial();
        return settingTutorial.createPanel(project, TranslateProcessContext.getInstance(project).getConfigurationOperator());
    }

    @Override
    protected void doOKAction() {
        List<LanguageConfiguration> languageConfiguration = settingTutorial.getLanguageConfiguration();
        TranslateProcessContext.getInstance(project).getConfigurationOperator().refresh(languageConfiguration);
        TranslateProcessContext.getInstance(project).getLanguageManager().reloadConfiguration();
        super.doOKAction();
    }
}
