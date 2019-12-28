package cn.xunyard.idea.coding.doc.process;

import cn.xunyard.idea.coding.doc.ClassUtils;
import cn.xunyard.idea.coding.doc.DocConfig;
import cn.xunyard.idea.coding.doc.ServiceResolver;
import cn.xunyard.idea.coding.doc.process.describer.MethodDescriber;
import cn.xunyard.idea.coding.doc.process.describer.ServiceDescriber;
import cn.xunyard.idea.coding.doc.process.describer.impl.DefaultServiceDescriber;
import cn.xunyard.idea.coding.doc.process.model.Api;
import cn.xunyard.idea.coding.log.Logger;
import cn.xunyard.idea.coding.log.LoggerFactory;
import cn.xunyard.idea.coding.util.AssertUtils;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2019-12-25
 */
@RequiredArgsConstructor
public class ServiceDescriberMaker {
    private final Logger log = LoggerFactory.getLogger(DocConfig.IDENTITY);
    private final ProcessContext processContext;

    @Nullable
    public ServiceDescriber fromJavaClass(JavaClass javaClass) {
        if (!javaClass.isInterface()) {
            log.warn(String.format("%s不是有效的服务(接口)", javaClass));
            return null;
        }

        Api api = Api.fromJavaClass(javaClass);
        if (api == null) {
            ServiceResolver.setResolveFail();
            if (processContext.getDocConfig().getLogUnresolved()) {
                log.error("[注释缺失] 服务: " + javaClass.getName() + " 未找到有效注释");
            }
            api = new Api(ClassUtils.getSimpleName(javaClass), null);
        }

        List<MethodDescriber> methods = getMethods(javaClass.getMethods(), javaClass);

        return new DefaultServiceDescriber(api, methods, javaClass);
    }

    private List<MethodDescriber> getMethods(List<JavaMethod> javaMethods, JavaClass javaClass) {
        if (AssertUtils.isEmpty(javaMethods)) {
            return Collections.emptyList();
        }

        List<MethodDescriber> describers = new ArrayList<>(javaMethods.size());
        for (JavaMethod javaMethod : javaMethods) {
            describers.add(processContext.getMethodDescriberMaker().fromJavaMethod(javaMethod, javaClass));
        }

        return describers;
    }

}
