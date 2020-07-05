package cn.xunyard.idea.coding.i18n;

import cn.xunyard.idea.coding.i18n.logic.I18nInspectionConfiguration;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-11
 */
@State(name = "I18nInspectionSettings", storages = {@Storage(StoragePathMacros.WORKSPACE_FILE)})
public class I18nInspectionSettings implements PersistentStateComponent<I18nInspectionConfiguration> {

    private I18nInspectionConfiguration i18nInspectionConfiguration;

    @NotNull
    public static I18nInspectionSettings getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, I18nInspectionSettings.class);
    }

    @Nullable
    @Override
    public I18nInspectionConfiguration getState() {
        return this.i18nInspectionConfiguration;
    }

    @Override
    public void loadState(@NotNull I18nInspectionConfiguration state) {
        this.i18nInspectionConfiguration = state;
    }
}
