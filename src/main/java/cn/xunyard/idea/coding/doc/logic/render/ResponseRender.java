package cn.xunyard.idea.coding.doc.logic.render;

import cn.xunyard.idea.coding.doc.logic.describer.ClassDescriber;
import cn.xunyard.idea.coding.doc.logic.describer.FieldDescriber;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2019-12-27
 */
public class ResponseRender extends AbstractClassRender {

    public void renderParameter(BufferedWriter fileWriter, ClassDescriber classDescriber) throws IOException {
        if (classDescriber == null) {
            fileWriter.write("\n```void```\n");
            return;
        }

        renderClassBasic(fileWriter, classDescriber);
        renderParameterClassFields(fileWriter, classDescriber);
        renderExtendClass(fileWriter, classDescriber, new HashSet<>());
    }

    @Override
    protected void renderParameterClassFields(BufferedWriter fileWriter, ClassDescriber classDescriber) throws IOException {
        if (classDescriber.hasFields()) {
            fileWriter.write("\n参数名|必选|类型|描述|说明\n---|---|---|---|---\n");

            for (FieldDescriber field : classDescriber.getFields()) {
                renderField(fileWriter, field);
            }
        }
    }
}
