package cn.xunyard.idea.coding.doc.logic.service;

import cn.xunyard.idea.coding.doc.logic.DocumentBuilderConfiguration;
import cn.xunyard.idea.coding.doc.logic.ProcessContext;
import cn.xunyard.idea.coding.doc.logic.render.DocumentRender;
import cn.xunyard.idea.coding.log.Logger;
import cn.xunyard.idea.coding.log.LoggerFactory;
import cn.xunyard.idea.coding.util.AssertUtils;
import com.intellij.openapi.project.Project;
import com.thoughtworks.qdox.model.JavaClass;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2019-12-29
 */
@RequiredArgsConstructor
public class ServiceAsyncProcessor implements Runnable {
    private final Logger log = LoggerFactory.getLogger(ProcessContext.IDENTITY);
    private final DocumentBuilderConfiguration configuration;
    private final Project project;
    private final Consumer<DocumentBuilderConfiguration> doneCallBack;

    @Override
    public void run() {
        run(project.getBasePath());
        doneCallBack.accept(configuration);
    }

    public void run(String basePath) {
        if (!checkOutput()) {
            return;
        }

        ProcessContext processContext = new ProcessContext(configuration);
        try {
            processContext.init();

            ServiceScanner serviceScanner = new ServiceScanner(processContext);
            List<JavaClass> serviceJavaClassList = serviceScanner.scan(basePath);
            if (AssertUtils.isEmpty(serviceJavaClassList)) {
                log.error("过程终止，未发现有效源文件路径!");
                return;
            }

            ServiceResolver serviceResolver = new ServiceResolver(serviceJavaClassList, processContext);
            if (!serviceResolver.run() && !configuration.isAllowInfoMissing()) {
                log.error("过程终止，修复注释缺失问题或者打开忽略开关以继续!");
                return;
            }

            DocumentRender documentRender = new DocumentRender(processContext, serviceResolver.getServiceDescriberList());
            documentRender.run();

        } catch (Throwable e) {
            log.error("fail to run, cause: " + formatException(e));
        } finally {
            processContext.clear();
        }
    }

    private String formatException(Throwable e) {
        String message = e.toString();
        String str = Arrays.stream(e.getStackTrace()).map(Objects::toString).collect(Collectors.joining("\n"));
        return message + "\n" + str;
    }

    private boolean checkOutput() {
        String outputDirectory = configuration.getOutputDirectory();
        File file = new File(outputDirectory);
        try {
            if (!file.exists() || !file.isDirectory()) {
                log.error("错误: " + outputDirectory + " 不是有效的目录");
                return false;
            }

            String fullPath = outputDirectory + "/" + configuration.getOutputFileName();
            File outputFile = new File(fullPath);
            if (outputFile.exists()) {
                log.warn("检测到输出文件已存在，删除!");
                boolean isOk = outputFile.delete();

                if (!isOk) {
                    log.error("错误: " + fullPath + " 无法删除");
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            log.error("检测到错误: " + formatException(e));
            return false;
        }
    }
}
