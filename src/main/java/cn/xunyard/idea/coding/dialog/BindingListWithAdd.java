package cn.xunyard.idea.coding.dialog;

import cn.xunyard.idea.coding.util.AssertUtils;
import cn.xunyard.idea.coding.util.ObjectUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jdesktop.swingx.VerticalLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-07
 */
public class BindingListWithAdd<T> extends JPanel {

    private JScrollPane scrollPane;
    private JBList<T> jbList;
    private JPanel buttonPanel;
    private Integer selectedIndex;
    private LabelMouseListener addMouseListener;
    private LabelMouseListener removeMouseListener;
    private List<T> listData;
    @Nullable
    private Consumer<List<T>> setter;

    public BindingListWithAdd(@Nullable Consumer<List<T>> setter) {
        this.listData = new ArrayList<>();
        this.setter = setter;
        initComponent();
    }

    private void addList() {
        jbList = new JBList<>();
        jbList.setModel(new DataModel<>(listData));
        scrollPane = new JBScrollPane(jbList);
        this.add(scrollPane);
    }

    private void addButtons() {
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setHgap(1);
        flowLayout.setVgap(1);
        buttonPanel = new JPanel(flowLayout);

        JLabel addLabel = new JLabel(AllIcons.General.Add);
        addLabel.setPreferredSize(new Dimension(20, 20));

        addMouseListener = new LabelMouseListener(addLabel);
        addLabel.addMouseListener(addMouseListener);
        buttonPanel.add(addLabel);


        JLabel removeLabel = new JLabel(AllIcons.General.Remove);
        removeLabel.setPreferredSize(new Dimension(20, 20));
        removeMouseListener = new LabelMouseListener(removeLabel);
        removeLabel.addMouseListener(removeMouseListener);
        buttonPanel.add(removeLabel);
        this.add(buttonPanel);
    }

    private void initComponent() {
        VerticalLayout verticalLayout = new VerticalLayout();
        this.setLayout(verticalLayout);

        addList();
        addButtons();

        removeMouseListener.setAllowRender(false);
        jbList.addListSelectionListener(l -> {
            selectedIndex = jbList.getSelectedIndex();
            removeMouseListener.setAllowRender(true);
        });

        removeMouseListener.setActionListener(this::deleteData);
    }


    public void setAddActionListener(ActionListener listener) {
        addMouseListener.setActionListener(listener);
    }

    public void setData(Collection<T> data) {
        if (AssertUtils.isEmpty(data)) {
            this.listData = new ArrayList<>();
        } else {
            this.listData = new ArrayList<>(data);
        }
        jbList.setModel(new DataModel<>(listData));
        jbList.ensureIndexIsVisible(listData.size() - 1);
    }

    public void addData(T data) {
        if (listData.contains(data)) {
            Messages.showMessageDialog(String.format("%s 重复", data), "添加数据错误", Messages.getErrorIcon());
            return;
        }


        listData.add(data);
        jbList.setModel(new DataModel<>(listData));
        jbList.ensureIndexIsVisible(listData.size() - 1);
        if (setter != null) {
            setter.accept(listData);
        }
    }

    public List<T> getData() {
        return this.listData;
    }

    private void deleteData(ActionEvent e) {
        if (selectedIndex != null) {
            listData.remove(selectedIndex.intValue());
            jbList.setModel(new DataModel<>(listData));
            selectedIndex = null;
            removeMouseListener.setAllowRender(false);
        }
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);

        this.scrollPane.setPreferredSize(new Dimension(preferredSize.width, preferredSize.height - 25));
        this.buttonPanel.setPreferredSize(new Dimension(preferredSize.width, 25));
    }

    private static final class LabelMouseListener implements MouseListener {
        private final JLabel label;

        private boolean allowRender = true;

        public void setAllowRender(boolean allowRender) {
            this.allowRender = allowRender;

            if (!allowRender) {
                label.setOpaque(false);
                label.repaint();
            }
        }

        @Setter
        @Nullable
        private ActionListener actionListener;

        private LabelMouseListener(JLabel label) {
            this.label = label;
            label.setBackground(JBColor.LIGHT_GRAY);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (actionListener != null) {
                actionListener.actionPerformed(new ActionEvent(label, ActionEvent.ACTION_PERFORMED, "click"));
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (!allowRender) {
                return;
            }

            label.setOpaque(true);
            label.repaint();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (!allowRender) {
                return;
            }

            label.setOpaque(false);
            label.repaint();
        }
    }

    @RequiredArgsConstructor
    private static final class DataModel<T> extends AbstractListModel<T> {

        private final List<T> listData;

        @Override
        public int getSize() {
            return listData.size();
        }

        @Override
        public T getElementAt(int index) {
            return listData.get(index);
        }
    }

    public static final class BindingListWithAddBuilder<T> {
        private Consumer<List<T>> setter;
        private List<T> init;
        private ActionListener listener;

        private BindingListWithAddBuilder(Consumer<List<T>> setter) {
            this.setter = setter;
        }

        private BindingListWithAddBuilder(List<T> init) {
            this.init = init;
        }

        private BindingListWithAddBuilder() {
        }

        public BindingListWithAddBuilder<T> init(List<T> init) {
            this.init = init;
            return this;
        }

        public BindingListWithAddBuilder<T> addListener(ActionListener listener) {
            this.listener = listener;
            return this;
        }

        public BindingListWithAdd<T> build() {
            List<T> listData = ObjectUtils.firstNonNull(init, ArrayList::new);
            BindingListWithAdd<T> bindingListWithAdd = new BindingListWithAdd<>(setter);
            bindingListWithAdd.setData(listData);
            bindingListWithAdd.setAddActionListener(listener);
            return bindingListWithAdd;
        }

        public static <T> BindingListWithAddBuilder<T> from(Consumer<List<T>> setter) {
            return new BindingListWithAddBuilder<>(setter);
        }

        public static <T> BindingListWithAddBuilder<T> from(List<T> init) {
            return new BindingListWithAddBuilder<>(init);
        }

        public static <T> BindingListWithAddBuilder<T> from() {
            return new BindingListWithAddBuilder<>();
        }
    }
}
