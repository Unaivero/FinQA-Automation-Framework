package com.finqa.bdd.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"com.finqa.bdd.steps"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/cucumber-html-report.html",
        "json:target/cucumber-results/cucumber.json",
        "junit:target/cucumber-results/cucumber-results.xml",
        "tech.grasshopper.reporter.ExtentCucumberAdapter:",
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
    },
    tags = "not @wip", // Ejecutar todas excepto las marcadas como work-in-progress
    monochrome = true
)
public class CucumberRunner extends AbstractTestNGCucumberTests {
    
    // Para ejecutar pruebas en paralelo
    @Override
    @DataProvider(parallel = false) // Cambia a true para ejecución paralela cuando esté todo estable
    public Object[][] scenarios() {
        return super.scenarios();
    }
}