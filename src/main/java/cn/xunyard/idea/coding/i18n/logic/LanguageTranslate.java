package cn.xunyard.idea.coding.i18n.logic;

import java.util.Set;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-11
 */
public interface LanguageTranslate {

    /**
     * 判断错误码是否缺少翻译
     *
     * @param errorCode 错误码
     * @return 缺失翻译返回true，否则false
     */
    boolean missing(String errorCode);


    /**
     * 获取设置的语言集合
     *
     * @return 语言集合
     */
    Set<String> getLanguages();


    /**
     * 获取指定语言的错误码翻译
     *
     * @param language  语言
     * @param errorCode 错误码
     * @return 翻译
     */
    String getTranslate(String language, String errorCode);

    /**
     * 设置翻译值
     *
     * @param language  语言
     * @param errorCode 错误码
     * @param translate 翻译
     */
    void setTranslate(String language, String errorCode, String translate);
}
