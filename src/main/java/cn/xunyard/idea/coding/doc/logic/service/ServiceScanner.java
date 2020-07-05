package cn.xunyard.idea.coding.doc.logic.service;

import cn.xunyard.idea.coding.doc.logic.ClassUtils;
import cn.xunyard.idea.coding.doc.logic.ProcessContext;
import cn.xunyard.idea.coding.doc.logic.describer.ClassDescriber;
import cn.xunyard.idea.coding.log.Logger;
import cn.xunyard.idea.coding.log.LoggerFactory;
import cn.xunyard.idea.coding.util.AssertUtils;
import com.google.common.base.Joiner;
import com.thoughtworks.qdox.model.JavaClass;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2019-12-15
 */
public class ServiceScanner {
    private final Logger log = LoggerFactory.getLogger(ProcessContext.IDENTITY);
    private final ProcessContext processContext;
    private final String serviceSuffix;
    private final String packagePrefix;

    private final List<JavaClass> scannedServices;

    public ServiceScanner(ProcessContext processContext) {
        this.processContext = processContext;
        this.serviceSuffix = wrapService(processContext.getConfiguration().getServiceSuffix());
        this.packagePrefix = wrapPackage(processContext.getConfiguration().getPackagePrefix());

        this.scannedServices = new LinkedList<>();
    }

    private String wrapPackage(String prefix) {
        if (prefix == null || prefix.length() == 0) {
            return null;
        }

        if (!prefix.contains(".")) {
            return prefix;
        }

        return Joiner.on("/").skipNulls().join(prefix.split("\\."));
    }

    private String wrapService(String suffix) {
        if (suffix == null || suffix.length() == 0) {
            return null;
        }

        return suffix;
    }

    public List<JavaClass> scan(String basePath) {
        if (!AssertUtils.isEmpty(processContext.getConfiguration().getSourceInclude())) {
            log.info("开始读取源文件工程...");
            for (String path : processContext.getConfiguration().getSourceInclude()) {
                scanForClassLoader(path);
            }
        }

        log.info("开始扫描服务类...");
        scanSearchService(basePath);
        log.info(String.format("扫描完成!共发现%d个服务", scannedServices.size()));
        return scannedServices;
    }

    private void scanForClassLoader(String path) {
        File file = new File(path);

        if (file.isDirectory()) {
            String[] children = file.list();

            if (children == null) {
                return;
            }

            for (String child : children) {
                scanForClassLoader(path + "/" + child);
            }
        } else {
            if (!file.exists()) {
                log.error("检测到无效文件地址:" + path);
                throw new IllegalArgumentException("invalid.file.path");
            }

            if (!path.endsWith(processContext.getConfiguration().getFileSuffix()) || !ClassUtils.isSrcClass(path)) {
                return;
            }

            processContext.getSourceClassLoader().loadClass(path);
        }
    }

    private void scanSearchService(String path) {
        File file = new File(path);

        if (file.isDirectory()) {
            String[] children = file.list();

            if (children == null) {
                return;
            }

            for (String child : children) {
                // 一些特殊路径的处理
                switch (child) {
                    case ".git":
                        continue;
                }
                scanSearchService(path + "/" + child);
            }
        } else {
            if (!file.exists()) {
                log.error("检测到无效文件地址:" + path);
                throw new IllegalArgumentException("invalid.file.path");
            }

            if (!path.endsWith(processContext.getConfiguration().getFileSuffix()) || !ClassUtils.isSrcClass(path)) {
                return;
            }

            List<JavaClass> javaClasses = processContext.getSourceClassLoader().loadClass(path);
            if (AssertUtils.isEmpty(javaClasses)) {
                return;
            }
            ClassDescriber classDescriber = processContext.getClassDescriberMaker().simpleFromClass(path);

            if (!AssertUtils.isEmpty(packagePrefix) && !Objects.requireNonNull(classDescriber.getPackage()).startsWith(packagePrefix)) {
                return;
            }

            if (!AssertUtils.isEmpty(serviceSuffix) && !classDescriber.getSimpleName().endsWith(serviceSuffix)) {
                return;
            }

            scannedServices.addAll(javaClasses);

            if (processContext.getConfiguration().isLogServiceDetail()) {
                log.info("发现服务:" + classDescriber.getPackage() + "/" + classDescriber.getSimpleName());
            }
        }
    }
}
