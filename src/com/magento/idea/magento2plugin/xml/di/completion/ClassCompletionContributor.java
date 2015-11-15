package com.magento.idea.magento2plugin.xml.di.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.magento.idea.magento2plugin.xml.di.XmlHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Created by dkvashnin on 10/15/15.
 */
public class ClassCompletionContributor extends CompletionContributor {
    public ClassCompletionContributor() {
        extend(CompletionType.BASIC,
            XmlPatterns.or(
                XmlHelper.getArgumentValuePatternForType("object"),
                XmlHelper.getItemValuePatternForType("object"),
                XmlHelper.getTagAttributePattern(XmlHelper.TYPE_TAG, XmlHelper.NAME_ATTRIBUTE),
                XmlHelper.getTagAttributePattern(XmlHelper.PREFERENCE_TAG, XmlHelper.TYPE_ATTRIBUTE),
                XmlHelper.getTagAttributePattern(XmlHelper.VIRTUAL_TYPE_TAG, XmlHelper.TYPE_ATTRIBUTE),
                XmlHelper.getTagAttributePattern(XmlHelper.PLUGIN_TAG, XmlHelper.TYPE_ATTRIBUTE)
            ),
            new CompletionProvider<CompletionParameters>() {
                public void addCompletions(@NotNull CompletionParameters parameters,
                                           ProcessingContext context,
                                           @NotNull CompletionResultSet resultSet) {
                    PsiElement psiElement = parameters.getOriginalPosition();
                    PhpIndex phpIndex = PhpIndex.getInstance(psiElement.getProject());
                    String prefix = StringUtil.unquoteString(psiElement.getText());

                    Collection<String> classNames = phpIndex.getAllClassNames(new CamelHumpMatcher(prefix));

                    for (String className: classNames) {
                        Collection<PhpClass> classesByName = phpIndex.getClassesByName(className);
                        for (PhpClass phpClass: classesByName) {
                            resultSet.addElement(
                                LookupElementBuilder
                                    .create(phpClass.getPresentableFQN())
                                    .withIcon(PhpIcons.CLASS_ICON)
                            );
                        }
                    }
                }
            }
        );
    }
}