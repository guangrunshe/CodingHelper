package cn.xunyard.idea.coding.doc.logic;

import cn.xunyard.idea.coding.util.AssertUtils;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaType;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2019-12-26
 */
public class ClassUtils {
    public static final String JAVA_SRC_ROOT = "/src/main/java/";

    public static boolean isSrcClass(String fullPath) {
        return !AssertUtils.isEmpty(fullPath) && fullPath.contains(JAVA_SRC_ROOT);
    }

    public static String resolveSrcPackagePath(String fullPath) {
        if (!isSrcClass(fullPath)) {
            return null;
        }

        return fullPath.substring(fullPath.lastIndexOf(JAVA_SRC_ROOT) + JAVA_SRC_ROOT.length(), fullPath.lastIndexOf("/"));
    }

    public static String getPackage(JavaType javaType) {
        if (javaType instanceof JavaClass) {
            return getPackage((JavaClass) javaType);
        }

        throw new RuntimeException("not.support");
    }

    public static String getPackage(JavaClass javaClass) {
        if (javaClass.isPrimitive()) {
            return "";
        }

        JavaPackage pkg = javaClass.getPackage();

        if (pkg == null) {
            String fullClassName = javaClass.toString();

            if (fullClassName.contains(".")) {
                return fullClassName.substring(0, fullClassName.lastIndexOf("."));
            } else {
                return "";
            }
        }

        return pkg.getName();
    }

    public static String getFullName(JavaType javaType) {
        String pkg = getPackage(javaType);

        return AssertUtils.isEmpty(pkg) ? getSimpleName(javaType) : pkg + "." + getSimpleName(javaType);
    }

    public static String getSimpleName(JavaType javaType) {
        if (javaType instanceof JavaClass) {
            return ((JavaClass)javaType).getSimpleName();
        }

        return javaType.getValue();
    }

    public static boolean isBasicType(JavaType type) {
        return isBasicType(type.toString());
    }

    public static boolean isBasicType(String fullClassName) {
        switch (fullClassName) {
            case "java.lang.Object":
            case "java.lang.Boolean":
            case "java.lang.Byte":
            case "java.lang.Integer":
            case "java.lang.Long":
            case "java.lang.Char":
            case "java.lang.String":
            case "java.lang.Float":
            case "java.lang.Double":
            case "java.util.Date":
                return true;
            default:
                return false;
        }
    }

    public static boolean isCommonCollectionClass(JavaType type) {
        String className = type.toString();
        switch (className) {
            case "java.util.List":
            case "java.util.Set":
            case "java.util.Map":
                return true;
            default:
                return false;
        }
    }
}
