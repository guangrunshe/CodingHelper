package cn.xunyard.idea.coding.doc.dialog;

import cn.xunyard.idea.coding.dialog.BindingCheckBox;
import cn.xunyard.idea.coding.dialog.BindingListWithAdd;
import cn.xunyard.idea.coding.dialog.BindingTextField;
import cn.xunyard.idea.coding.doc.logic.DocumentBuilderConfiguration;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.TitledSeparator;
import com.intellij.util.ui.JBUI;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import java.awt.*;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2019-12-21
 */
public class BuilderTutorial extends JPanel {

    private final Project project;
    private final DocumentBuilderConfiguration configuration;

    /**
     * 返回包装
     */
    private JTextArea returnPackTextArea;

    public BuilderTutorial(Project project, DocumentBuilderConfiguration configuration) {
        this.project = project;
        this.configuration = configuration;
        this.setLayout(new VerticalLayout());
        this.setPreferredSize(new Dimension(600, 500));
        initComponents();
    }

    private void initComponents() {
        renderBasic();
        renderService();
        renderReturn();
        renderOutput();
    }

    private JPanel createContainer(String title) {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{25, 575};
        gridBagLayout.rowHeights = new int[]{45, 0};

        TitledSeparator titledSeparator = new TitledSeparator(title);
        titledSeparator.setPreferredSize(new Dimension(600, 45));
        GridBagConstraints gridBagConstraints = buildGeneralConstraints(0, 0, 2, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, JBUI.insets(0));

        JPanel container = new JPanel(gridBagLayout);
        container.add(titledSeparator, gridBagConstraints);

        return container;
    }

    private JPanel createHolder(JPanel container) {
        JPanel holder = new JPanel(new VerticalLayout());
        GridBagConstraints gridBagConstraints = buildGeneralConstraints(1, 1, 1);
        container.add(holder, gridBagConstraints);
        return holder;
    }

    private void addHolderContent(JPanel holder, JComponent content, int rowIndex) {
        GridBagConstraints gridBagConstraints = buildGeneralConstraints(1, rowIndex, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, JBUI.insets(0));
        holder.add(content, gridBagConstraints);
    }

    private void addGroupContent(JComponent content, JPanel groupPanel, int gridX, int gridY) {
        GridBagConstraints constraints = buildGeneralConstraints(gridX, gridY, 1);
        groupPanel.add(content, constraints);
    }

    private GridBagConstraints buildGeneralConstraints(int gridX, int gridY, int gridWidth) {
        return new GridBagConstraints(gridX, gridY, gridWidth, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, JBUI.insetsBottom(5), 0, 0);
    }

    private GridBagConstraints buildGeneralConstraints(int gridX, int gridY, int gridWidth, int gridHeight,
                                                       int anchor, int fill, Insets insets) {
        return new GridBagConstraints(gridX, gridY, gridWidth, gridHeight, 0.0, 0.0, anchor,
                fill, insets, 0, 0);
    }

    private void renderBasic() {
        JPanel container = createContainer("运行基础配置");
        JPanel holder = createHolder(container);

        int index = 0;

        JCheckBox allowInfoMissingCheckBox = BindingCheckBox.BindingCheckBoxBuilder
                .from(configuration::setAllowInfoMissing)
                .selected(configuration.isAllowInfoMissing())
                .text("允许信息缺失(使用名称代替)")
                .build();

        addHolderContent(holder, allowInfoMissingCheckBox, index);
        this.add(container);
    }

    private void addLineContent(String title, JComponent content, AbstractButton clickable, JPanel groupPanel, int rowIndex) {
        JLabel titleLabel = new JLabel(title);
        GridBagConstraints leftConstraints = buildGeneralConstraints(0, rowIndex, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, JBUI.insetsRight(5));
        groupPanel.add(titleLabel, leftConstraints);

        GridBagConstraints middleConstraints = buildGeneralConstraints(1, rowIndex, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, JBUI.insetsRight(5));
        groupPanel.add(content, middleConstraints);

        clickable.addActionListener(e -> content.setEnabled(clickable.isSelected()));
        GridBagConstraints rightConstraints = buildGeneralConstraints(2, rowIndex, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, JBUI.insets(0));
        groupPanel.add(clickable, rightConstraints);
    }

    private JTextField createDisableTextField(String text) {
        JTextField jTextField = new JTextField(text);
        jTextField.setEnabled(false);

        return jTextField;
    }

    private void renderService() {
        JPanel container = createContainer("服务接口配置");
        JPanel holder = createHolder(container);

        // 输出服务列表
        JCheckBox logServiceCheckBox = BindingCheckBox.BindingCheckBoxBuilder.from(configuration::setLogServiceDetail)
                .selected(configuration.isLogServiceDetail())
                .text("输出服务列表")
                .build();
        addHolderContent(holder, logServiceCheckBox, 0);

        // 输出无法解析的内容
        JCheckBox logUnsolvedCheckBox = BindingCheckBox.BindingCheckBoxBuilder.from(configuration::setLogUnresolved)
                .selected(configuration.isLogUnresolved())
                .text("输出无法解析的内容(建议打开)")
                .build();
        addHolderContent(holder, logUnsolvedCheckBox, 1);

        {
            GridBagLayout gridBagLayout = new GridBagLayout();
            gridBagLayout.columnWidths = new int[]{105, 390, 80};
            JPanel groupPanel = new JPanel(gridBagLayout);

            // 文件后缀
            JTextField fileSuffixTextField = BindingTextField.BindingTextFieldBuilder.from(configuration::setFileSuffix)
                    .enable(false)
                    .text(configuration.getFileSuffix())
                    .build();

            JCheckBox fileSuffixCheckBox = new JCheckBox("复写");
            fileSuffixCheckBox.setEnabled(false);
            addLineContent("文件后缀", fileSuffixTextField, fileSuffixCheckBox, groupPanel, 0);

            // 接口后缀匹配
            JTextField suffixTextField = BindingTextField.BindingTextFieldBuilder.from(configuration::setServiceSuffix)
                    .enable(false)
                    .text(configuration.getServiceSuffix())
                    .build();

            JCheckBox suffixCheckBox = new JCheckBox("复写");
            addLineContent("接口后缀匹配", suffixTextField, suffixCheckBox, groupPanel, 1);

            // 包路径前缀匹配
            JTextField prefixTextField = BindingTextField.BindingTextFieldBuilder.from(configuration::setPackagePrefix)
                    .enable(false)
                    .text(configuration.getPackagePrefix())
                    .build();

            JCheckBox prefixCheckBox = new JCheckBox("复写");
            addLineContent("包路径前缀匹配", prefixTextField, prefixCheckBox,
                    groupPanel, 2);

            addHolderContent(holder, groupPanel, 2);
        }

        this.add(container);
    }

    private void renderReturn() {
        JPanel container = createContainer("源代码路径配置");
        JPanel holder = createHolder(container);

        BindingListWithAdd<String> bindingListWithAdd = BindingListWithAdd.BindingListWithAddBuilder
                .from(configuration::setSourceInclude)
                .init(configuration.getSourceInclude())
                .build();
        bindingListWithAdd.setAddActionListener(e -> {
            VirtualFile virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(),
                    project, null);

            if (virtualFile != null) {
                bindingListWithAdd.addData(virtualFile.getPath());
            }
        });

        bindingListWithAdd.setPreferredSize(new Dimension(560, 86));
        addHolderContent(holder, bindingListWithAdd, 0);
        this.add(container);
    }

    private void renderOutput() {
        JPanel container = createContainer("文件输出配置");
        JPanel holder = createHolder(container);

        {
            GridBagLayout gridBagLayout = new GridBagLayout();
            gridBagLayout.columnWidths = new int[]{105, 390, 80};
            JPanel groupPanel = new JPanel(gridBagLayout);

            // json文件名
            JTextField jsonFileNameTextField = createDisableTextField("此功能暂不可用");

            JCheckBox jsonFileNameCheckBox = new JCheckBox("复写");
            jsonFileNameCheckBox.setEnabled(false);
            addLineContent("json文件名", jsonFileNameTextField, jsonFileNameCheckBox, groupPanel, 0);

            // 文件名
            JTextField outputFileNameTextField = BindingTextField.BindingTextFieldBuilder.from(configuration::setOutputFileName)
                    .text(configuration.getOutputFileName())
                    .enable(false)
                    .build();

            JCheckBox outputFileNameCheckBox = new JCheckBox("复写");
            addLineContent("文档文件名", outputFileNameTextField, outputFileNameCheckBox,
                    groupPanel, 1);

            // 文档输出路径
            TextFieldWithBrowseButton outputDirectoryTextWithBrowseButton = new TextFieldWithBrowseButton();
            outputDirectoryTextWithBrowseButton.setEnabled(false);
            outputDirectoryTextWithBrowseButton.setText(configuration.getOutputDirectory());
            outputDirectoryTextWithBrowseButton.addActionListener(e -> {
                VirtualFile virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(),
                        project, null);

                if (virtualFile != null) {
                    outputDirectoryTextWithBrowseButton.setText(virtualFile.getPath());
                    configuration.setOutputDirectory(virtualFile.getPath());
                }
            });

            JCheckBox outputDirectoryCheckBox = new JCheckBox("复写");

            addLineContent("文档输出路径", outputDirectoryTextWithBrowseButton, outputDirectoryCheckBox,
                    groupPanel, 2);

            addHolderContent(holder, groupPanel, 0);
        }

        this.add(container);
    }
}
