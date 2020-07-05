package cn.xunyard.idea.coding.doc.logic.render;

import cn.xunyard.idea.coding.doc.logic.describer.ClassDescriber;
import cn.xunyard.idea.coding.doc.logic.describer.FieldDescriber;
import cn.xunyard.idea.coding.doc.logic.describer.ParameterizedClass;
import cn.xunyard.idea.coding.util.ObjectUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2019-12-27
 */
public abstract class AbstractClassRender {

    protected void renderField(BufferedWriter fileWriter, FieldDescriber field) throws IOException {
        fileWriter.write(field.getName()
                + "|" + (field.isRequired() ? "是" : "否")
                + "|" + String.format("``` %s ```", field.toSimpleString())
                + "|" + ObjectUtils.firstNonNull(field.getDescription(), "-")
                + "|" + ObjectUtils.firstNonNull(field.getNote(), "-")
                + "\n");
    }

    private boolean canSkip(ClassDescriber classDescriber, Set<String> rendered) {
        String simpleString = classDescriber.toSimpleString();

        if (rendered.contains(simpleString)) {
            return true;
        }

        rendered.add(simpleString);
        return false;
    }

    protected void renderClassBasic(BufferedWriter fileWriter, ClassDescriber classDescriber) throws IOException {
        fileWriter.write(String.format("\n``` %s ```\n", classDescriber.toSimpleString()));

        if (classDescriber.hasDescription()) {
            fileWriter.write(String.format("\n>%s\n\n", classDescriber.getDescription()));
        }

        if (classDescriber.hasNote()) {
            fileWriter.write(String.format("\n%s\n\n", classDescriber.getNote()));
        }
    }

    protected boolean renderClassBasicCore(BufferedWriter fileWriter, ClassDescriber classDescriber, Set<String> rendered) throws IOException {
        if (canSkip(classDescriber, rendered) ||
                classDescriber.isCycleReference() ||
                classDescriber.isBasicType()) {
            return false;
        }

        if (classDescriber.isParameterized() && ((ParameterizedClass) classDescriber).isCommonCollectionClass()) {
            return false;
        }

        fileWriter.write(String.format("\n``` %s ```\n", classDescriber.toSimpleString()));

        if (classDescriber.hasDescription()) {
            fileWriter.write(String.format("\n>%s\n\n", classDescriber.getDescription()));
        }

        if (classDescriber.hasNote()) {
            fileWriter.write(String.format("\n%s\n\n", classDescriber.getNote()));
        }

        return true;
    }

    /**
     * 是否为简单类型泛型
     */
    protected boolean isCommonParametrizedClass(ClassDescriber classDescriber) {
        if (classDescriber.isParameterized()) {
            ParameterizedClass parameterizedClass = (ParameterizedClass) classDescriber;
            return parameterizedClass.isCommonCollectionClass() && parameterizedClass.isParameterizedBasicType();
        }

        return false;
    }

    protected void renderExtendClass(BufferedWriter fileWriter, ClassDescriber classDescriber, Set<String> rendered) throws IOException {
        if (classDescriber.isCycleReference()) {
            return;
        }

        if (classDescriber.hasExtend()) {
            for (ClassDescriber describer : classDescriber.getExtend()) {
                renderExtendClassCore(fileWriter, describer, rendered);
            }
        }

        if (classDescriber.isParameterized()) {
            for (ClassDescriber describer : classDescriber.getParameterized()) {
                renderExtendClassCore(fileWriter, describer, rendered);
            }
        }
    }

    private void renderExtendClassCore(BufferedWriter fileWriter, ClassDescriber classDescriber, Set<String> rendered) throws IOException {
        if (classDescriber.isCycleReference()) {
            return;
        }

        if (classDescriber.isBasicType()) {
            return;
        }

        if (isCommonParametrizedClass(classDescriber)) {
            return;
        }

        boolean render = renderClassBasicCore(fileWriter, classDescriber, rendered);
        if (render) {
            renderParameterClassFields(fileWriter, classDescriber);
        }
        renderExtendClass(fileWriter, classDescriber, rendered);
    }

    protected abstract void renderParameterClassFields(BufferedWriter fileWriter, ClassDescriber classDescriber) throws IOException;
}
