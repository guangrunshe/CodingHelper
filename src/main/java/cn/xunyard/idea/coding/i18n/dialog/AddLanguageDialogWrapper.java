package cn.xunyard.idea.coding.i18n.dialog;

import cn.xunyard.idea.coding.i18n.logic.LanguageConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-10
 */
public class AddLanguageDialogWrapper extends DialogWrapper {

    private final Project project;
    private final AddLanguage addLanguage;

    public AddLanguageDialogWrapper(Project project) {
        super(project, false);
        this.project = project;
        this.addLanguage = new AddLanguage();
        init();
        setTitle("添加翻译语言");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return addLanguage.createPanel(project);
    }

    public LanguageConfiguration getLanguageConfiguration() {
        return addLanguage.getLanguageConfiguration();
    }
}
