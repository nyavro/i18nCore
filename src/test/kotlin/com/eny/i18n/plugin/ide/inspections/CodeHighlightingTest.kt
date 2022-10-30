package com.eny.i18n.plugin.ide.inspections

import com.eny.i18n.plugin.PlatformBaseTest
import com.eny.i18n.plugin.ide.CodeTranslationGenerators
import com.eny.i18n.plugin.ide.JsCodeAndTranslationGeneratorsNs
import com.eny.i18n.plugin.ide.PhpCodeAndTranslationGenerators
import com.eny.i18n.plugin.ide.runWithConfig
import com.eny.i18n.plugin.ide.settings.Config
import com.eny.i18n.plugin.utils.generator.code.CodeGenerator
import com.eny.i18n.plugin.utils.generator.code.TsxCodeGenerator
import com.eny.i18n.plugin.utils.generator.translation.JsonTranslationGenerator
import com.eny.i18n.plugin.utils.generator.translation.TranslationGenerator
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource

private fun CodeInsightTestFixture.customCheck(fileName: String, code: String, translationName: String, translation: String) = this.runWithConfig(Config(defaultNs = "translation")) {
    this.addFileToProject(translationName, translation)
    this.configureByText(fileName, code)
    this.checkHighlighting(true, true, true, true)
}

class JsDialectCodeHighlightingTestBase: PlatformBaseTest() {

    @ParameterizedTest
    @ArgumentsSource(JsCodeAndTranslationGeneratorsNs::class)
    fun testDefNsUnresolved(cg: CodeGenerator, tg: TranslationGenerator) = myFixture.customCheck(
        "defNsUnresolved.${cg.ext()}",
        cg.multiGenerate(
            "\"<warning descr=\"Missing default translation file\">missing.default.translation</warning>\"",
            "`<warning descr=\"Missing default translation file\">missing.default.in.\${template}</warning>`"
        ),
        "assets/test.${tg.ext()}",
        tg.generatePlural("tst2", "plurals", "value", "value1", "value2", "value5")
    )

    @ParameterizedTest
    @ArgumentsSource(JsCodeAndTranslationGeneratorsNs::class)
    fun testUnresolvedKey(cg: CodeGenerator, tg: TranslationGenerator) = myFixture.customCheck(
        "unresolvedKey.${cg.ext()}",
        cg.multiGenerate(
            "\"test:tst1.<warning descr=\"Unresolved key\">unresolved.part.of.key</warning>\"",
            "\"test:<warning descr=\"Unresolved key\">unresolved.whole.key</warning>\"",
            "`test:tst1.<warning descr=\"Unresolved key\">unresolved.part.of.key.\${arg}</warning>`",
            "`test:<warning descr=\"Unresolved key\">unresolved.whole.\${arg}</warning>`",
            "`test:<warning descr=\"Unresolved key\">unresolved.whole.\${arg}</warning>`",
            "`test:<warning descr=\"Unresolved key\">unresolved.whole.\${b ? 'key' : 'key2'}</warning>`",
            "`test:tst1.<warning descr=\"Unresolved key\">unresolved.part.of.\${b ? 'key' : 'key2'}</warning>`"
        ),
        "test.${tg.ext()}",
        tg.generateContent("tst1", "base", "single", "only one value")
    )

    @ParameterizedTest
    @ArgumentsSource(JsCodeAndTranslationGeneratorsNs::class)
    fun testUnresolvedNs(cg: CodeGenerator, tg: TranslationGenerator) = myFixture.customCheck(
        "unresolvdNs.${cg.ext()}",
        cg.multiGenerate(
            "\"<warning descr=\"Unresolved namespace\">unresolved</warning>:tst1.base\"",
            "`<warning descr=\"Unresolved namespace\">unresolved</warning>:tst1.base.\${arg}`"
        ),
        "test.${tg.ext()}",
        tg.generateContent("root", "first", "key", "value")
    )

    @ParameterizedTest
    @ArgumentsSource(JsCodeAndTranslationGeneratorsNs::class)
    fun testResolvedTemplate(cg: CodeGenerator, tg: TranslationGenerator) = myFixture.customCheck(
        "resolvedTemplate.${cg.ext()}",
        cg.generate("`test:tst1.base.\${arg}`"),
        "assets/translation.${tg.ext()}",
        tg.generateContent("tst1", "base", "value", "translation")
    )

    @Test
    fun testResolvedTemplate1() {}
}

class PhpHighlightingTest: PlatformBaseTest() {

    @ParameterizedTest
    @ArgumentsSource(PhpCodeAndTranslationGenerators::class)
    fun testDefNsUnresolved(cg: CodeGenerator, tg: TranslationGenerator) = myFixture.customCheck(
        "defNsUnresolved.${cg.ext()}",
        cg.multiGenerate(
                "\"<warning descr=\"Missing default translation file\">missing.default.translation</warning>\"",
                "'<warning descr=\"Missing default translation file\">missing.default.in.translation</warning>'"
        ),
        "assets/test.${tg.ext()}",
        tg.generatePlural("tst2", "plurals", "value", "value1", "value2", "value5")
    )

    @ParameterizedTest
    @ArgumentsSource(PhpCodeAndTranslationGenerators::class)
    fun testUnresolvedKey(cg: CodeGenerator, tg: TranslationGenerator) = myFixture.customCheck(
        "unresolvedKey.${cg.ext()}",
        cg.multiGenerate(
            "\"test:tst1.<warning descr=\"Unresolved key\">unresolved.part.of.key</warning>\"",
            "\"test:<warning descr=\"Unresolved key\">unresolved.whole.key</warning>\"",
            "'test:tst1.<warning descr=\"Unresolved key\">unresolved.part.of.key</warning>'",
            "'test:<warning descr=\"Unresolved key\">unresolved.whole.key</warning>'"
        ),
        "test.${tg.ext()}",
        tg.generateContent("tst1", "base", "single", "only one value")
    )

    @ParameterizedTest
    @ArgumentsSource(PhpCodeAndTranslationGenerators::class)
    fun testUnresolvedNs(cg: CodeGenerator, tg: TranslationGenerator) = myFixture.customCheck(
        "unresolvdNs.${cg.ext()}",
        cg.multiGenerate(
            "\"<warning descr=\"Unresolved namespace\">unresolved</warning>:tst1.base\"",
            "'<warning descr=\"Unresolved namespace\">unresolved</warning>:tst1.base'"
        ),
        "test.${tg.ext()}",
        tg.generateContent("root", "first", "key", "value")
    )

    @Test
    fun testResolvedTemplate1() {}
}
