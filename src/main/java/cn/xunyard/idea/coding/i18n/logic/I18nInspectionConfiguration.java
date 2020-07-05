package cn.xunyard.idea.coding.i18n.logic;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-18
 */
@Data
public class I18nInspectionConfiguration implements Serializable {

    private Map<String, LanguageConfiguration> languageMap = new HashMap<>();
}
