package cn.xunyard.idea.coding.i18n.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-11
 */
public class LanguageTranslateWrapper implements LanguageTranslate, LanguageManager {

    private final InspectionConfigurationOperator inspectionConfigurationOperator;
    private final Map<String, LanguageTranslateHolder> translateHolderMap;

    public LanguageTranslateWrapper(InspectionConfigurationOperator inspectionConfigurationOperator) {
        this.inspectionConfigurationOperator = inspectionConfigurationOperator;
        this.translateHolderMap = new HashMap<>();
        loadConfiguration();
    }

    private synchronized void loadConfiguration() {
        translateHolderMap.clear();
        for (LanguageConfiguration configuration : inspectionConfigurationOperator.getLanguages()) {
            String language = configuration.getLanguage();
            LanguageTranslateHolder holder = new LanguageTranslateHolder(configuration);
            translateHolderMap.put(language, holder);
        }
    }

    @Override
    public synchronized void reloadConfiguration() {
        Map<String, LanguageTranslateHolder> toReplace = new HashMap<>();

        for (LanguageConfiguration configuration : inspectionConfigurationOperator.getLanguages()) {
            String language = configuration.getLanguage();

            if (translateHolderMap.containsKey(language)) {
                LanguageTranslateHolder holder = translateHolderMap.get(language);
                holder.reloadTranslate(configuration.getFilepath());

                toReplace.put(language, holder);
            } else {
                LanguageTranslateHolder holder = new LanguageTranslateHolder(configuration);
                toReplace.put(language, holder);
            }
        }

        translateHolderMap.clear();
        translateHolderMap.putAll(toReplace);
    }

    @Override
    public boolean missing(String errorCode) {
        for (LanguageTranslateHolder holder : translateHolderMap.values()) {
            if (!holder.exist(errorCode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<String> getLanguages() {
        return translateHolderMap.keySet();
    }

    @Override
    public String getTranslate(String language, String errorCode) {
        LanguageTranslateHolder holder = translateHolderMap.get(language);
        if (holder == null) {
            throw new IllegalArgumentException("指定语言未设置");
        }

        return holder.getTranslate(errorCode);
    }

    @Override
    public void setTranslate(String language, String errorCode, String translate) {
        LanguageTranslateHolder holder = translateHolderMap.get(language);
        if (holder == null) {
            throw new IllegalArgumentException("指定语言未设置");
        }

        holder.setTranslate(errorCode, translate);
    }
}
