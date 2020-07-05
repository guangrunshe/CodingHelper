package cn.xunyard.idea.coding.doc.dialog;

import cn.xunyard.idea.coding.doc.logic.DocumentBuilderConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2019-12-15
 */
public class BuilderDialogWrapper extends DialogWrapper {

    private final Project project;
    private final DocumentBuilderConfiguration configuration;

    public BuilderDialogWrapper(Project project, DocumentBuilderConfiguration configuration) {
        super(true);
        this.project = project;
        this.configuration = configuration;
        init();
        setTitle("文档生成");
        this.setResizable(false);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return new BuilderTutorial(project, configuration);
    }
}
