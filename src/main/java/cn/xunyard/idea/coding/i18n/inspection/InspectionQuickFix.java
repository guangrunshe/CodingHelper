package cn.xunyard.idea.coding.i18n.inspection;

import cn.xunyard.idea.coding.i18n.dialog.AddTranslateDialogWrapper;
import cn.xunyard.idea.coding.i18n.logic.InspectionUtils;
import cn.xunyard.idea.coding.i18n.logic.TranslateProcessContext;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiLiteralExpression;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-09
 */
class InspectionQuickFix implements LocalQuickFix {
    private static final Logger log = Logger.getInstance(InspectionQuickFix.class);
    private static InspectionQuickFix instance;

    private InspectionQuickFix() {
    }

    public static InspectionQuickFix getInstance() {
        if (instance == null) {
            instance = new InspectionQuickFix();
        }
        return instance;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Set error code translate";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        try {
            PsiLiteralExpression expression = (PsiLiteralExpression)descriptor.getPsiElement();
            String errorCode = InspectionUtils.getErrorCode(expression);
            EventQueue.invokeLater(() -> (new AddTranslateDialogWrapper(project, errorCode,
                    TranslateProcessContext.getInstance(project).getLanguageTranslate())).showAndGet());
        } catch (Exception e) {
            log.error(e);
        }
    }
}
