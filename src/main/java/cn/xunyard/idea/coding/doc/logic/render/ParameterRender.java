package cn.xunyard.idea.coding.doc.logic.render;

import cn.xunyard.idea.coding.doc.logic.describer.ClassDescriber;
import cn.xunyard.idea.coding.doc.logic.describer.FieldDescriber;
import cn.xunyard.idea.coding.doc.logic.describer.ParameterDescriber;
import lombok.RequiredArgsConstructor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2019-12-27
 */
@RequiredArgsConstructor
public class ParameterRender extends AbstractClassRender {

    public void renderParameter(BufferedWriter fileWriter, ParameterDescriber parameterDescriber) throws IOException {
        ClassDescriber classDescriber = parameterDescriber.getClassDescriber();
        renderClassBasic(fileWriter, classDescriber);
        renderParameterClassFields(fileWriter, classDescriber);
        renderExtendClass(fileWriter, classDescriber, new HashSet<>());
    }

    protected void renderParameterClassFields(BufferedWriter fileWriter, ClassDescriber classDescriber) throws IOException {
        if (classDescriber.hasFields()) {
            fileWriter.write("\n参数名|必填|类型|描述|说明\n---|---|---|---|---\n");

            for (FieldDescriber field : classDescriber.getFields()) {
                renderField(fileWriter, field);
            }
        }
    }

}
