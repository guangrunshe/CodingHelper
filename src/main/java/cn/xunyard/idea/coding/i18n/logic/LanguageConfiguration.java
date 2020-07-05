package cn.xunyard.idea.coding.i18n.logic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LanguageConfiguration implements Serializable {

    /**
     * 翻译目标语言
     */
    private String language;

    /**
     * 翻译文件路径
     */
    private String filepath;


    @Override
    public String toString() {
        return language + " : " + filepath;
    }
}