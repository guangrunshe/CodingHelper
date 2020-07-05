package cn.xunyard.idea.coding.i18n.logic;

import java.util.List;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-18
 */
public interface InspectionConfigurationOperator {

    String PROPERTIES_SUFFIX = ".properties";

    /**
     * 获得配置用的语言项
     *
     * @return 语言配置项
     */
    List<LanguageConfiguration> getLanguages();

    /**
     * 刷新语言配置
     *
     * @param configurations 语言配置
     */
    void refresh(List<LanguageConfiguration> configurations);

    /**
     * 判断是否已有语言项
     *
     * @param language 语言项
     * @return 已有返回true，否则false
     */
    boolean contains(String language);
}
