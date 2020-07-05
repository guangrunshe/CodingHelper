package cn.xunyard.idea.coding.doc.logic;

import cn.xunyard.idea.coding.log.Logger;
import cn.xunyard.idea.coding.log.LoggerFactory;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.JavaType;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2019-12-24
 */
public class SourceClassLoader {
    private final Logger log = LoggerFactory.getLogger(ProcessContext.IDENTITY);
    private final Map<String, JavaClass> loadedJavaClassMap;
    private final JavaProjectBuilder javaProjectBuilder;

    public SourceClassLoader(JavaProjectBuilder javaProjectBuilder) {
        this.javaProjectBuilder = javaProjectBuilder;
        this.loadedJavaClassMap = new HashMap<>();
    }

    public void clear() {
        loadedJavaClassMap.clear();
    }

    public List<JavaClass> loadClass(String filepath) {
        try {
            JavaSource javaSource;
            try (FileInputStream fis = new FileInputStream(new File(filepath))) {
                try (InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
                    javaSource = javaProjectBuilder.addSource(isr);

                    if (javaSource == null) {
                        return null;
                    }

                    List<JavaClass> javaClassList = javaSource.getClasses();
                    for (JavaClass javaClass : javaClassList) {
                        String pkg = ClassUtils.getPackage(javaClass);
                        String simpleName = ClassUtils.getSimpleName(javaClass);
                        loadedJavaClassMap.putIfAbsent(pkg + "." + simpleName, javaClass);
                    }
                    return javaClassList;
                }
            }
        } catch (Exception e) {
            log.error("解析java类出现问题，路径: " + filepath);
            return null;
        }
    }

    @Nullable
    public JavaClass find(JavaType javaType) {
        return loadedJavaClassMap.get(ClassUtils.getFullName(javaType));
    }

}
