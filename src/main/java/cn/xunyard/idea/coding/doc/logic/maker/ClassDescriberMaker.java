package cn.xunyard.idea.coding.doc.logic.maker;

import cn.xunyard.idea.coding.doc.logic.ClassUtils;
import cn.xunyard.idea.coding.doc.logic.ProcessContext;
import cn.xunyard.idea.coding.doc.logic.describer.ClassDescriber;
import cn.xunyard.idea.coding.doc.logic.describer.FieldDescriber;
import cn.xunyard.idea.coding.doc.logic.describer.impl.*;
import cn.xunyard.idea.coding.doc.logic.model.ApiModel;
import cn.xunyard.idea.coding.doc.logic.model.ApiModelProperty;
import cn.xunyard.idea.coding.doc.logic.service.ServiceResolver;
import cn.xunyard.idea.coding.log.Logger;
import cn.xunyard.idea.coding.log.LoggerFactory;
import cn.xunyard.idea.coding.util.AssertUtils;
import cn.xunyard.idea.coding.util.ObjectUtils;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaParameterizedType;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2019-12-24
 */
@RequiredArgsConstructor
public class ClassDescriberMaker {
    private final Logger log = LoggerFactory.getLogger(ProcessContext.IDENTITY);
    private final Map<String, ClassDescriber> classDescriberMap = new HashMap<>();
    private final ProcessContext processContext;
    private final LinkedList<String> recursionClassFullName = new LinkedList<>();

    public void clear() {
        classDescriberMap.clear();
    }

    public ClassDescriber simpleFromClass(@NotNull String filepath) {
        String noSuffix = filepath.substring(0, filepath.lastIndexOf("."));
        String fullClassName = noSuffix.substring(noSuffix.lastIndexOf(ClassUtils.JAVA_SRC_ROOT) + ClassUtils.JAVA_SRC_ROOT.length());
        // 类可能不一定有package，兼容
        String pkg = fullClassName.contains("/") ? fullClassName.substring(0, fullClassName.lastIndexOf("/")) : "";
        return new BasicTypeClassDescriber(pkg, pkg.isEmpty() ? fullClassName :
                fullClassName.substring(fullClassName.lastIndexOf("/") + 1));
    }

    @NotNull
    public ClassDescriber fromClass(@NotNull JavaClass javaClass) {
        // 第一次调用不会为空
        return Objects.requireNonNull(fromClassCore(javaClass));
    }

    @Nullable
    public ClassDescriber fromClassCore(@NotNull JavaClass javaClass) {
        String classFullName = buildClassFullName(javaClass);

        if (recursionClassFullName.contains(classFullName)) {
            return new ReferenceClassDescriber(javaClass);
        } else {
            recursionClassFullName.addFirst(classFullName);
        }

        ClassDescriber classDescriber = null;

        if (ClassUtils.isBasicType(javaClass)) {
            classDescriber = getOrLoadBasicClass(javaClass);
        }

        if (classDescriber == null && isParameterizedClass(javaClass)) {
            classDescriber = getOrLoadParameterizedClass((DefaultJavaParameterizedType) javaClass);
        }

        if (classDescriber == null) {
            classDescriber = getOrLoadGeneralClass(javaClass);
        }

        recursionClassFullName.removeFirst();
        return classDescriber;
    }


    private static boolean isParameterizedClass(JavaType javaType) {
        return javaType instanceof JavaParameterizedType &&
                !AssertUtils.isEmpty(((JavaParameterizedType) javaType).getActualTypeArguments());
    }

    public ClassDescriber getOrLoadVirtualClass(JavaType javaType) {
        String fullClassName = javaType.toString();

        ClassDescriber classDescriber = classDescriberMap.get(fullClassName);
        if (classDescriber == null) {
            classDescriber = new VirtualClassDescriber(javaType);
            classDescriberMap.put(fullClassName, classDescriber);
        }

        return classDescriber;
    }


    private ClassDescriber getOrLoadBasicClass(JavaType javaType) {
        String name = javaType.getValue();

        ClassDescriber classDescriber = classDescriberMap.get(name);

        if (classDescriber == null) {
            String fullName = javaType.toString();
            classDescriber = new BasicTypeClassDescriber(fullName.substring(0, fullName.lastIndexOf(".")),
                    fullName.substring(fullName.lastIndexOf(".") + 1));
            classDescriberMap.put(fullName, classDescriber);
        }

        return classDescriber;
    }

    private ClassDescriber getOrLoadGeneralClass(JavaClass javaClass) {
        FieldAssociated fa = buildClassFields(javaClass);
        ApiModel apiModel = ApiModel.fromJavaClass(javaClass);
        GeneralClassDescriber classDescriber = new GeneralClassDescriber(javaClass, fa.getFields(), fa.getExtendSet());

        if (apiModel != null) {
            classDescriber.setDescription(apiModel.getValue());
        }

        return classDescriber;
    }

    private FieldAssociated buildClassFields(JavaClass javaClass) {
        return buildClassFieldsCore(javaClass, null);
    }

    private static String buildClassFullName(JavaClass javaClass) {
        String basicFullClassName = ClassUtils.getPackage(javaClass) + "." + ClassUtils.getSimpleName(javaClass);
        if (!isParameterizedClass(javaClass)) {
            return basicFullClassName;
        }

        return buildParameterizedSign(new StringBuilder(basicFullClassName), (JavaParameterizedType) javaClass);
    }

    private static String buildParameterizedSign(StringBuilder sb, JavaParameterizedType javaClass) {
        sb.append("<");

        List<JavaType> typeArguments = javaClass.getActualTypeArguments();
        for (int i = 0; i < typeArguments.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }

            JavaType argumentType = typeArguments.get(i);

            if (isParameterizedClass(argumentType)) {
                buildParameterizedSign(sb, (JavaParameterizedType) argumentType);
            } else {
                sb.append(ClassUtils.getSimpleName(argumentType));
            }
        }
        sb.append(">");

        return sb.toString();
    }

    private FieldAssociated buildClassFieldsCore(JavaClass javaClass, FieldAssociated fieldAssociated) {
        fieldAssociated = ObjectUtils.firstNonNull(fieldAssociated, FieldAssociated::new);

        if (ClassUtils.isBasicType(javaClass) || javaClass.isEnum()) {
            return fieldAssociated;
        }

        for (JavaField field : javaClass.getFields()) {
            if (field.getModifiers().contains("final") || field.getModifiers().contains("static")) {
                continue;
            }

            ApiModelProperty apiModelProperty = ApiModelProperty.fromJavaField(field);

            if (apiModelProperty == null) {
                ServiceResolver.setResolveFail();
                if (processContext.getConfiguration().isLogUnresolved()) {
                    log.error("[注释缺失] 属性: " + javaClass.getName() + "#" + field.getName() + " 未找到有效注解");
                }
                apiModelProperty = new ApiModelProperty(null, null, false);
            }
            JavaClass fieldType = field.getType();

            ClassDescriber classDescriber = fromClassCore(fieldType);
            FieldDescriber fieldDescriber = new DefaultFieldDescriber(apiModelProperty, field.getName(), classDescriber);
            fieldAssociated.addField(fieldDescriber);

            if (classDescriber != null && !classDescriber.isBasicType()) {
                fieldAssociated.addExtend(classDescriber);
            }
        }

        if (javaClass.isPrimitive()) {
            return fieldAssociated;
        }

        JavaType superClassType = javaClass.getSuperClass();
        if (superClassType == null) {
            return fieldAssociated;
        }

        if (ClassUtils.isBasicType(superClassType)) {
            return fieldAssociated;
        }

        JavaClass superClass = processContext.getSourceClassLoader().find(superClassType);
        if (superClass != null) {
            return buildClassFieldsCore(superClass, fieldAssociated);
        } else {
            getOrLoadVirtualClass(superClassType);
            return fieldAssociated;
        }

    }

    private ClassDescriber getOrLoadParameterizedClass(DefaultJavaParameterizedType javaClass) {
        List<ClassDescriber> parametrizedList = new LinkedList<>();
        for (JavaType typeArgument : javaClass.getActualTypeArguments()) {
            if (ClassUtils.isBasicType(typeArgument)) {
                parametrizedList.add(getOrLoadVirtualClass(typeArgument));
                continue;
            }

            JavaClass typeClass = processContext.getSourceClassLoader().find(typeArgument);

            if (typeClass == null) {
                if (typeArgument instanceof JavaClass) {
                    parametrizedList.add(fromClassCore((JavaClass) typeArgument));
                } else {
                    parametrizedList.add(getOrLoadVirtualClass(typeArgument));
                }
            } else {
                parametrizedList.add(fromClassCore(typeClass));
            }
        }

        FieldAssociated fa = buildClassFields(javaClass);
        ParameterizedClassDescriber describer = new ParameterizedClassDescriber(javaClass, fa.getFields(),
                fa.getExtendSet(), parametrizedList);

        ApiModel apiModel = ApiModel.fromJavaClass(javaClass);
        if (apiModel != null) {
            describer.setDescription(apiModel.getValue());
        }

        return describer;
    }
}
