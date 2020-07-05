package cn.xunyard.idea.coding.dialog;

import com.intellij.icons.AllIcons;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-12
 */
public class AddAndRemoveWrap {
    private JBLabel addLabel;
    private JBLabel removeLabel;
    private JPanel wholePanel;
    private JButton button1;
    private JButton button2;
    private LabelMouseListener removeLabelMouseListener;

    public void createUIComponents() {
        addLabel = new JBLabel(AllIcons.General.Add);
        removeLabel = new JBLabel(AllIcons.General.Remove);
    }

    public void setRemoveEnable(boolean enable) {
        removeLabelMouseListener.setAllowRender(enable);
    }

    public JComponent createPanel(@Nullable ActionListener addActionListener, @Nullable ActionListener removeActionListener) {
        addLabel.addMouseListener(new LabelMouseListener(addLabel, addActionListener));
        removeLabelMouseListener = new LabelMouseListener(removeLabel, removeActionListener);
        setRemoveEnable(false);
        removeLabel.addMouseListener(removeLabelMouseListener);
        return this.wholePanel;
    }

    private static final class LabelMouseListener implements MouseListener {
        private final JLabel label;
        @Nullable
        private final ActionListener actionListener;

        private boolean allowRender = true;

        public void setAllowRender(boolean allowRender) {
            this.allowRender = allowRender;

            if (!allowRender) {
                label.setOpaque(false);
                label.repaint();
            }
        }

        private LabelMouseListener(JLabel label, @Nullable ActionListener actionListener) {
            this.label = label;
            this.actionListener = actionListener;
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
}
