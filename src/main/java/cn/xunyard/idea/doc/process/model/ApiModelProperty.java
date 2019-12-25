package cn.xunyard.idea.doc.process.model;

import cn.xunyard.idea.util.AssertUtils;
import cn.xunyard.idea.util.ObjectUtils;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import lombok.Getter;

import java.util.Objects;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2019-12-22
 * @see io.swagger.annotations.ApiModelProperty
 */
@Getter
public class ApiModelProperty {
    private static final String NAME = "io.swagger.annotations.ApiModelProperty";

    private final String value;
    private final String note;
    private final Boolean required;

    private ApiModelProperty(String value, String note, Boolean required) {
        this.value = value;
        this.note = note;
        this.required = required;
    }

    public static ApiModelProperty fromJavaField(JavaField javaField) {
        ApiModelProperty apiModelProperty = null;

        for (JavaAnnotation annotation : javaField.getAnnotations()) {
            apiModelProperty = fromAnnotation(annotation);

            if (apiModelProperty != null) {
                return apiModelProperty;
            }
        }

        String comment = javaField.getComment();

        if (!AssertUtils.isEmpty(comment)) {
            return new ApiModelProperty(ObjectUtils.smoothStr(comment), null, false);
        }

        return new ApiModelProperty(null, null, false);
    }

    private static ApiModelProperty fromAnnotation(JavaAnnotation annotation) {
        if (!NAME.equals(annotation.getType().getFullyQualifiedName())) {
            return null;
        }

        AnnotationValue annotationValue = annotation.getProperty("value");
        if (annotationValue == null) {
            return null;
        }

        String value = annotationValue.toString();
        if (value.startsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }

        return new ApiModelProperty(value,
                ObjectUtils.takeIfNonNull(annotation.getProperty("note"), Objects::toString),
                ObjectUtils.takeBoolean(annotation.getProperty("required")));
    }
}