package cn.xunyard.idea.coding.i18n.dialog;

import cn.xunyard.idea.coding.dialog.BindingListWithAdd;
import cn.xunyard.idea.coding.i18n.logic.InspectionConfigurationOperator;
import cn.xunyard.idea.coding.i18n.logic.LanguageConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import java.util.List;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-12
 */
public class SettingTutorial {

    private JPanel wholePanel;
    private BindingListWithAdd<LanguageConfiguration> selectTool;

    public void createUIComponents() {
        selectTool = new BindingListWithAdd<>(null);
    }

    public JComponent createPanel(Project project, InspectionConfigurationOperator configuration) {
        selectTool.setData(configuration.getLanguages());
        selectTool.setAddActionListener(e -> {
            AddLanguageDialogWrapper addLanguageDialogWrapper = new AddLanguageDialogWrapper(project);
            if (addLanguageDialogWrapper.showAndGet()) {
                LanguageConfiguration languageConfiguration = addLanguageDialogWrapper.getLanguageConfiguration();

                if (configuration.contains(languageConfiguration.getLanguage())) {
                    Messages.showMessageDialog(String.format("已包含同名的语言项:%s", languageConfiguration.getLanguage()),
                            "错误", Messages.getErrorIcon());
                    return;
                }

                selectTool.addData(languageConfiguration);
            }
        });

        return wholePanel;
    }

    public List<LanguageConfiguration> getLanguageConfiguration() {
        return this.selectTool.getData();
    }
}
