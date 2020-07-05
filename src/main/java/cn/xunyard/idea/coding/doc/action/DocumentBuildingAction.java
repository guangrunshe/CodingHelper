package cn.xunyard.idea.coding.doc.action;

import cn.xunyard.idea.coding.doc.DocumentBuilderSettings;
import cn.xunyard.idea.coding.doc.dialog.BuilderDialogWrapper;
import cn.xunyard.idea.coding.doc.logic.DocumentBuilderConfiguration;
import cn.xunyard.idea.coding.doc.logic.DocumentBuildingService;
import cn.xunyard.idea.coding.util.ProjectUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2019-12-15
 */
public class DocumentBuildingAction extends AnAction {
    private DocumentBuilderConfiguration documentBuilderConfiguration = DocumentBuilderSettings.getInstance().getState();

    @Override
    public void actionPerformed(AnActionEvent event) {
        final Project project = event.getProject();
        ProjectUtils.switchProject(project);
        if (documentBuilderConfiguration == null) {
            documentBuilderConfiguration = new DocumentBuilderConfiguration();
            DocumentBuilderSettings.getInstance().loadState(documentBuilderConfiguration);
        }

        BuilderDialogWrapper dialogWrapper = new BuilderDialogWrapper(project, documentBuilderConfiguration);
        if (dialogWrapper.showAndGet()) {
            process(documentBuilderConfiguration);
        }
    }

    private void process(DocumentBuilderConfiguration configuration) {
        DocumentBuildingService service = new DocumentBuildingService(configuration);
        service.run();
    }
}
