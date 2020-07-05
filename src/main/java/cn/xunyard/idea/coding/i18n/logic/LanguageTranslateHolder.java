package cn.xunyard.idea.coding.i18n.logic;

import cn.xunyard.idea.coding.util.AssertUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-11
 */
public class LanguageTranslateHolder {
    private final LanguageConfiguration configuration;
    private Map<String, String> translateMap;

    public boolean exist(String errorCode) {
        return translateMap.containsKey(errorCode);
    }

    public String getTranslate(String errorCode) {
        return translateMap.get(errorCode);
    }

    public void setTranslate(String errorCode, String translate) {
        if (AssertUtils.isEmpty(translate)) {
            return;
        }

        if (translateMap.containsKey(errorCode) && translate.equals(translateMap.get(errorCode))) {
            return;
        }

        translateMap.put(errorCode, translate);
        TranslateAsyncPersistent.submit(configuration, errorCode, translate);
    }

    public LanguageTranslateHolder(LanguageConfiguration configuration) {
        this.configuration = configuration;
        translateMap = new HashMap<>();
        loadTranslate(this.translateMap);
    }

    private void loadTranslate(Map<String, String> translateMap) {
        translateMap.clear();
        String filepath = configuration.getFilepath();
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                return;
            }

            try (FileReader fileReader = new FileReader(file)) {
                try (BufferedReader reader = new BufferedReader(fileReader)) {
                    String str;

                    while ((str = reader.readLine()) != null) {
                        String[] property = str.split("=");
                        translateMap.put(property[0], property[1]);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(String.format("语言[%s]的翻译文件处理失败", configuration.getLanguage()));
        }
    }

    public void reloadTranslate(String filepath) {
        if (configuration.getFilepath().equals(filepath)) {
            return;
        }

        loadTranslate(translateMap);
    }
}
