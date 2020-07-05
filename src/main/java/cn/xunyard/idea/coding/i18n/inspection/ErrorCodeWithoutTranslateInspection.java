package cn.xunyard.idea.coding.i18n.inspection;

import cn.xunyard.idea.coding.i18n.logic.TranslateProcessContext;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-08
 */
public class ErrorCodeWithoutTranslateInspection extends AbstractBaseJavaLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new InspectionPsiElementVisitor(holder, TranslateProcessContext.getInstance(holder.getProject()).getLanguageTranslate());
    }
}
