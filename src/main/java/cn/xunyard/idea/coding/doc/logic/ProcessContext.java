package cn.xunyard.idea.coding.doc.logic;

import cn.xunyard.idea.coding.doc.logic.maker.ClassDescriberMaker;
import cn.xunyard.idea.coding.doc.logic.maker.MethodDescriberMaker;
import cn.xunyard.idea.coding.doc.logic.maker.ServiceDescriberMaker;
import com.thoughtworks.qdox.JavaProjectBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2019-12-24
 */
@Getter
@RequiredArgsConstructor
public class ProcessContext {
    public static final String IDENTITY = "生成服务";
    private final DocumentBuilderConfiguration configuration;
    private SourceClassLoader sourceClassLoader;
    private ClassDescriberMaker classDescriberMaker;
    private MethodDescriberMaker methodDescriberMaker;
    private ServiceDescriberMaker serviceDescriberMaker;

    public void init() {
        sourceClassLoader = new SourceClassLoader(new JavaProjectBuilder());
        classDescriberMaker = new ClassDescriberMaker(this);
        methodDescriberMaker = new MethodDescriberMaker(this);
        serviceDescriberMaker = new ServiceDescriberMaker(this);
    }

    public void clear() {
        sourceClassLoader.clear();
        classDescriberMaker.clear();
        sourceClassLoader = null;
        classDescriberMaker = null;
        methodDescriberMaker = null;
        serviceDescriberMaker = null;
    }
}
