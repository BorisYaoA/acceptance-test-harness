package org.jenkinsci.test.acceptance.recorder;

import org.jenkinsci.test.acceptance.guice.TestName;
import org.jenkinsci.test.acceptance.junit.FailureDiagnostics;
import org.junit.Test;
import org.junit.runner.Description;

import java.io.File;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TestRecorderRuleTest {

    @Test
    public void shouldNotRecordSuccessTestExecutionByDefault() {
        Description desc = description("shouldNotRecordSuccessTestExecutionByDefault");
        TestRecorderRule testRecorderRule = rule(desc);
        testRecorderRule.starting(desc);

        System.out.println("Hello World");

        testRecorderRule.succeeded(desc);
        testRecorderRule.finished(desc);

        File outputFile = outputFile(desc);
        assertThat(outputFile.exists(), is(false));

        //Clean the field
        outputFile.delete();
    }

    @Test
    public void shouldRecordFailingTestExecutionByDefault() {

        Description desc = description("shouldRecordFailingTestExecutionByDefault");
        TestRecorderRule testRecorderRule = rule(desc);
        testRecorderRule.starting(desc);

        System.out.println("Good Bye World");
        //succeeded is not called since a failure is simulated
        //testRecorderRule.succeeded(shouldNotRecordSuccessTestExecutionByDefault);
        testRecorderRule.finished(desc);

        File outputFile = outputFile(desc);
        assertThat(outputFile.exists(), is(true));

        //Clean the field
        outputFile.delete();
    }

    @Test
    public void shouldRecordSuccessTestExecutionWhenSaveAll() {

        //Since configured recorder option is static we need to set it manually in each test.
        TestRecorderRule.RECORDER_OPTION = TestRecorderRule.ALWAYS;

        Description desc = description("shouldRecordSuccessTestExecutionWhenSaveAll");
        TestRecorderRule testRecorderRule = rule(desc);
        testRecorderRule.starting(desc);

        System.out.println("Hello World");

        testRecorderRule.succeeded(desc);
        testRecorderRule.finished(desc);

        File outputFile = outputFile(desc);
        assertThat(outputFile.exists(), is(true));

        TestRecorderRule.RECORDER_OPTION = TestRecorderRule.FAILURES;
        outputFile.delete();
    }

    @Test
    public void shouldNotRecordWhenRecorderIsDisabled() {

        //Since configured recorder option is static we need to set it manually in each test.
        TestRecorderRule.RECORDER_OPTION = TestRecorderRule.OFF;

        Description desc = description("shouldNotRecordWhenRecorderIsDisabled");
        TestRecorderRule testRecorderRule = rule(desc);
        testRecorderRule.starting(desc);

        System.out.println("Hello World");

        //testRecorderRule.succeeded(shouldNotRecordSuccessTestExecutionByDefault);
        testRecorderRule.finished(desc);

        File outputFile = outputFile(desc);
        assertThat(outputFile.exists(), is(false));

        //Clean the field
        TestRecorderRule.RECORDER_OPTION = TestRecorderRule.FAILURES;
        outputFile.delete();
    }

    private Description description(String method) {
        return Description.createTestDescription(getClass(), "shouldNotRecordWhenRecorderIsDisabled");
    }

    private TestRecorderRule rule(Description desc) {
        return new TestRecorderRule(new FailureDiagnostics(new TestName(desc.getDisplayName())));
    }

    private File outputFile(Description desc) {
        return new File("target/diagnostics/" +desc + "/ui-recording.mov");
    }
}
