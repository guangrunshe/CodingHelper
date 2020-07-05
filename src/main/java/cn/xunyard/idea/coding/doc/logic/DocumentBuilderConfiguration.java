package cn.xunyard.idea.coding.doc.logic;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-06
 */
@Data
public class DocumentBuilderConfiguration implements Serializable {

    /**
     * 文件后缀名
     */
    private String fileSuffix = ".java";

    /**
     * 服务后缀
     */
    private String serviceSuffix = "Facade";

    /**
     * 包路径前缀
     */
    private String packagePrefix;

    /**
     * 文档输出目录
     */
    private String outputDirectory = "/tmp";

    /**
     * 输出文件名
     */
    private String outputFileName = "document.md";

    /**
     * 输出服务详情列表
     */
    private boolean logServiceDetail = false;

    /**
     * 输出无法解析的内容
     */
    private boolean logUnresolved = true;

    /**
     * 允许信息缺失
     */
    private boolean allowInfoMissing = true;

    /**
     * 可被包含的源文件目录(可以是工程)
     */
    private List<String> sourceInclude;
}
