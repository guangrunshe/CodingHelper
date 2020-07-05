package cn.xunyard.idea.coding.i18n.logic;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-11
 */
public class InspectionUtils {

    public static boolean isErrorCode(String str) {
        if (!str.startsWith("\"") || !str.endsWith("\"")) {
            return false;
        }

        boolean containsDot = false;
        char[] chars = str.toCharArray();
        for (int i = 1; i < chars.length - 2; i++) {
            char c = chars[i];

            if (c == '.') {
                containsDot = true;
                continue;
            }

            if (c > 'a' - 1 && c < 'z' + 1) {
                continue;
            }

            if (c > 'A' - 1 && c < 'Z' + 1) {
                continue;
            }

            return false;
        }

        return containsDot;
    }

    public static String getErrorCode(PsiElement psiElement) {
        if (!(psiElement instanceof PsiLiteralExpression)) {
            return null;
        }

        String text = psiElement.getText();
        if (text.length() < 3) {
            return null;
        }

        return text.substring(1, text.length() - 1);
    }

}
