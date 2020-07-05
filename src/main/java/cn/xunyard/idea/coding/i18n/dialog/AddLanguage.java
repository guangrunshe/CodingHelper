package cn.xunyard.idea.coding.i18n.dialog;

import cn.xunyard.idea.coding.i18n.logic.InspectionConfigurationOperator;
import cn.xunyard.idea.coding.i18n.logic.LanguageConfiguration;
import com.intellij.ide.util.TreeFileChooser;
import com.intellij.ide.util.TreeFileChooserFactory;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

import javax.swing.*;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-10
 */
public class AddLanguage extends JPanel {
    private JTextField languageTextField;
    private TextFieldWithBrowseButton filepathBrowseButton;
    private JPanel wholePanel;

    public JComponent createPanel(Project project) {
        filepathBrowseButton.addActionListener(e -> {
            TreeFileChooser fileChooser = TreeFileChooserFactory.getInstance(project)
                    .createFileChooser("选择翻译文件", null, null, null);
            fileChooser.showDialog();
            PsiFile selectedFile = fileChooser.getSelectedFile();

            if (selectedFile != null && selectedFile.getVirtualFile() != null) {
                filepathBrowseButton.setText(selectedFile.getVirtualFile().getPath());
            }
        });

        return wholePanel;
    }

    public LanguageConfiguration getLanguageConfiguration() {
        LanguageConfiguration languageConfiguration = new LanguageConfiguration();
        languageConfiguration.setLanguage(languageTextField.getText());
        languageConfiguration.setFilepath(filepathBrowseButton.getText());
        return languageConfiguration;
    }
}
