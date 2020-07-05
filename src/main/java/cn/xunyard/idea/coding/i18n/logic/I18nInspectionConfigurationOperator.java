package cn.xunyard.idea.coding.i18n.logic;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-11
 */
@Data
@RequiredArgsConstructor
public class I18nInspectionConfigurationOperator implements InspectionConfigurationOperator {

    private final Map<String, LanguageConfiguration> languageMap;

    /**
     * 获取翻译语言配置集合
     *
     * @return 语言配置集合
     */
    @Override
    public List<LanguageConfiguration> getLanguages() {
        return new ArrayList<>(languageMap.values());
    }

    @Override
    public void refresh(List<LanguageConfiguration> configurations) {
        languageMap.clear();
        for (LanguageConfiguration configuration : configurations) {
            languageMap.put(configuration.getLanguage(), configuration);
        }
    }

    @Override
    public boolean contains(String language) {
        return languageMap.containsKey(language);
    }
}
