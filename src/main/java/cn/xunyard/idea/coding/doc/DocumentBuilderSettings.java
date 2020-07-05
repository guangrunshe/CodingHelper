package cn.xunyard.idea.coding.doc;

import cn.xunyard.idea.coding.doc.logic.DocumentBuilderConfiguration;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-07
 */
@State(name = "DocumentBuilderSettings", storages = @Storage("xunyard.document.settings.xml"))
public class DocumentBuilderSettings implements PersistentStateComponent<DocumentBuilderConfiguration> {

    private DocumentBuilderConfiguration documentBuilderConfiguration;

    @NotNull
    public static DocumentBuilderSettings getInstance() {
        return ServiceManager.getService(DocumentBuilderSettings.class);
    }

    @Nullable
    @Override
    public DocumentBuilderConfiguration getState() {
        return documentBuilderConfiguration;
    }

    @Override
    public void loadState(@NotNull DocumentBuilderConfiguration state) {
        documentBuilderConfiguration = state;
    }
}
