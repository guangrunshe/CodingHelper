package cn.xunyard.idea.coding.doc.logic.render;

import cn.xunyard.idea.coding.doc.logic.ProcessContext;
import cn.xunyard.idea.coding.doc.logic.describer.MethodDescriber;
import cn.xunyard.idea.coding.doc.logic.describer.ServiceDescriber;
import cn.xunyard.idea.coding.log.Logger;
import cn.xunyard.idea.coding.log.LoggerFactory;
import cn.xunyard.idea.coding.util.AssertUtils;
import com.google.common.base.Throwables;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2019-12-21
 */
@RequiredArgsConstructor
public class DocumentRender {
    private final Logger log = LoggerFactory.getLogger(ProcessContext.IDENTITY);
    private final ProcessContext processContext;
    private final List<ServiceDescriber> serviceDescriberList;
    private final ParameterRender parameterRender = new ParameterRender();
    private final ResponseRender responseRender = new ResponseRender();

    public void run() throws IOException {
        log.info("开始生成文档...");
        String filepath = processContext.getConfiguration().getOutputDirectory() + "/" +
                processContext.getConfiguration().getOutputFileName();
        try (FileOutputStream fos = new FileOutputStream(filepath, false)) {
            try (OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
                try (BufferedWriter bufferedWriter = new BufferedWriter(osw)) {
                    int serviceIndex = 1;//服务索引
                    for (ServiceDescriber serviceDescriber : serviceDescriberList) {
                        renderService(serviceIndex, bufferedWriter, serviceDescriber);
                        serviceIndex++;
                    }

                    bufferedWriter.flush();
                    log.info(String.format("文档生成完成，文档路径: %s", filepath));
                } catch (Exception e) {
                    log.error("文档生成失败: {}", Throwables.getStackTraceAsString(e));
                }
            } catch (Exception e) {
                log.error("文件准备失败: {}", Throwables.getStackTraceAsString(e));
            }
        } catch (Exception e) {
            log.error("根节点失败: {}", Throwables.getStackTraceAsString(e));
        }
    }

    private void renderService(BufferedWriter fileWriter, ServiceDescriber serviceDescriber) throws IOException {

        fileWriter.write(String.format("## %s\n\n", serviceDescriber.getDescription()));

        if (!AssertUtils.isEmpty(serviceDescriber.getNote())) {
            fileWriter.write(String.format("接口说明\n%s\n", serviceDescriber.getNote()));
        }

        fileWriter.write(String.format("*接口路径*\n\n**%s**\n", serviceDescriber.getFullName()));

        for (MethodDescriber methodDescriber : serviceDescriber.getMethods()) {
            renderMethod(fileWriter, methodDescriber);
        }
    }

    /**
     *  生成接口文档
     * @param serviceIndex
     * @param fileWriter
     * @param serviceDescriber
     * @throws IOException
     */
    private void renderService(int serviceIndex, BufferedWriter fileWriter, ServiceDescriber serviceDescriber)
            throws IOException {

        fileWriter.write(String.format("## " + serviceIndex + ". %s\n\n", serviceDescriber.getDescription()));
        if (!AssertUtils.isEmpty(serviceDescriber.getNote())) {
            fileWriter.write(String.format("接口说明\n%s\n", serviceDescriber.getNote()));
        }

        fileWriter.write(String.format("*接口路径*\n\n**%s**\n", serviceDescriber.getFullName()));
        fileWriter.write("\n\n");
        int serviceMethodIndex = 1;//服务方法索引
        for (MethodDescriber methodDescriber : serviceDescriber.getMethods()) {
            renderMethod(serviceIndex, serviceMethodIndex, fileWriter, methodDescriber);
            serviceMethodIndex++;
        }
    }

    /**
     * 生成接口中方法文档
     * @param serviceIndex
     * @param serviceMethodIndex
     * @param fileWriter
     * @param methodDescriber
     * @throws IOException
     */
    private void renderMethod(int serviceIndex, int serviceMethodIndex, BufferedWriter fileWriter,
                              MethodDescriber methodDescriber)
            throws IOException {

        fileWriter.write(String.format("### " + serviceIndex + "." + serviceMethodIndex + ". %s\n",
                methodDescriber.getDescription()));
        fileWriter.write("\n\n");
        if (!AssertUtils.isEmpty(methodDescriber.getNote())) {
            fileWriter.write(String.format("\n>%s\n\n", methodDescriber.getNote()));
            fileWriter.write("\n\n");
        }

        fileWriter.write(String.format("#### " + serviceIndex + "." + serviceMethodIndex
                + ".1 方法签名\n\n```java\n%s\n```\n", methodDescriber.getSign()));
        fileWriter.write("\n\n");
        if (methodDescriber.hasParameter()) {
            fileWriter.write("#### " + serviceIndex + "." + serviceMethodIndex
                    + ".2 请求参数\n\n");
            parameterRender.renderParameter(fileWriter, methodDescriber.getParameterList().iterator().next());
            fileWriter.write("\n\n");
        }

        if (methodDescriber.hasResponse()) {
            fileWriter.write("#### " + serviceIndex + "." + serviceMethodIndex
                    + ".3 返回参数\n\n");
            responseRender.renderParameter(fileWriter, methodDescriber.getResponse());
            fileWriter.write("\n\n");
        }
    }



    private void renderMethod(BufferedWriter fileWriter, MethodDescriber methodDescriber) throws IOException {
        fileWriter.write(String.format("### %s\n", methodDescriber.getDescription()));

        if (!AssertUtils.isEmpty(methodDescriber.getNote())) {
            fileWriter.write(String.format("\n>%s\n\n", methodDescriber.getNote()));
        }

        fileWriter.write(String.format("#### 方法签名\n\n```java\n%s\n```\n", methodDescriber.getSign()));

        if (methodDescriber.hasParameter()) {
            fileWriter.write("#### 请求参数\n\n");

            parameterRender.renderParameter(fileWriter, methodDescriber.getParameterList().iterator().next());
        }


        if (methodDescriber.hasResponse()) {
            fileWriter.write("#### 返回参数\n\n");
            responseRender.renderParameter(fileWriter, methodDescriber.getResponse());
        }
    }

}
