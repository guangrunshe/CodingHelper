package cn.xunyard.idea.coding.i18n.logic;

import cn.xunyard.idea.coding.i18n.I18nInspectionSettings;
import com.intellij.openapi.project.Project;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-11
 */
@Getter
public class TranslateProcessContext {
    private static Map<Project, TranslateProcessContext> contextMap = new WeakHashMap<>();

    private final InspectionConfigurationOperator configurationOperator;
    private final LanguageTranslate languageTranslate;
    private final LanguageManager languageManager;

    public TranslateProcessContext(InspectionConfigurationOperator configurationOperator) {
        this.configurationOperator = configurationOperator;
        LanguageTranslateWrapper languageTranslateWrapper = new LanguageTranslateWrapper(configurationOperator);
        this.languageTranslate = languageTranslateWrapper;
        this.languageManager = languageTranslateWrapper;
    }

    @NotNull
    public static TranslateProcessContext getInstance(@NotNull Project project) {
        if (contextMap.containsKey(project)) {
            return contextMap.get(project);
        } else {
            I18nInspectionConfiguration configuration = I18nInspectionSettings.getInstance(project).getState();
            if (configuration == null) {

                configuration = new I18nInspectionConfiguration();
                I18nInspectionSettings.getInstance(project).loadState(configuration);
            }

            I18nInspectionConfigurationOperator operator = new I18nInspectionConfigurationOperator(configuration.getLanguageMap());
            TranslateProcessContext context = new TranslateProcessContext(operator);
            contextMap.put(project, context);

            return context;
        }
    }
}
