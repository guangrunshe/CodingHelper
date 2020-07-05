package cn.xunyard.idea.coding.i18n.inspection;

import cn.xunyard.idea.coding.i18n.logic.InspectionUtils;
import cn.xunyard.idea.coding.i18n.logic.LanguageTranslate;
import cn.xunyard.idea.coding.util.AssertUtils;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.*;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-09
 */
@RequiredArgsConstructor
public final class InspectionPsiElementVisitor extends JavaElementVisitor {
    private static final Logger log = Logger.getInstance(InspectionPsiElementVisitor.class);
    private final ProblemsHolder problemHolder;
    private final LanguageTranslate languageTranslate;

    @Override
    public void visitLiteralExpression(PsiLiteralExpression expression) {
        super.visitLiteralExpression(expression);

        // 错误码应该包含符号'.'
        String text = expression.getText();
        if (AssertUtils.isEmpty(text) || !InspectionUtils.isErrorCode(text)) {
            return;
        }

        // 仅处理方法中出现的字符串
        if (!(expression.getContext() instanceof PsiExpressionList) ||
                !(expression.getContext().getContext() instanceof PsiMethodCallExpression)) {
            return;
        }

        PsiExpressionList psiExpressionList = (PsiExpressionList) expression.getContext();

        // 左括号
        if (!(psiExpressionList.getFirstChild() instanceof PsiJavaToken) ||
                !(((PsiJavaToken) psiExpressionList.getFirstChild()).getTokenType() == JavaTokenType.LPARENTH)) {
            return;
        }

        // 右括号
        if (!(psiExpressionList.getLastChild() instanceof PsiJavaToken) ||
                !(((PsiJavaToken) psiExpressionList.getLastChild()).getTokenType() == JavaTokenType.RPARENTH)) {
            return;
        }

        tryRegisterProblem(expression);
    }

    @Override
    public void visitThrowStatement(PsiThrowStatement statement) {
        super.visitThrowStatement(statement);
        PsiExpression exception = statement.getException();
        if (exception == null || exception.getFirstChild() == null) {
            return;
        }
        // 确保是throw new表达
        if (exception.getFirstChild() instanceof PsiKeyword && PsiKeyword.NEW.equals(exception.getFirstChild().getText())) {
            PsiElement lastChild = exception.getLastChild();

            if (!(lastChild instanceof PsiExpressionList)) {
                return;
            }

            PsiElement context = lastChild.getChildren()[1];
            // 只处理直接丢出内容
            if (!(context instanceof PsiLiteralExpression)) {
                return;
            }

            String text = context.getText();
            if (InspectionUtils.isErrorCode(text)) {
                tryRegisterProblem(context);
            }
        }
    }

    private void tryRegisterProblem(@NotNull PsiElement psiElement) {
        String errorCode = InspectionUtils.getErrorCode(psiElement);
        if (errorCode == null) {
            return;
        }

        if (languageTranslate.missing(errorCode)) {
            problemHolder.registerProblem(psiElement, String.format("%s missing translate", errorCode),
                    InspectionQuickFix.getInstance());
        }
    }
}
