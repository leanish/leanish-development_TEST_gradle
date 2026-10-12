package example.fixture;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FixtureYamlDumpTest {
    @Test
    void dumpsTheConfigBeanAsBlockYaml() {
        FixtureConfig config = new FixtureConfig();
        config.setName("fixture");
        config.setRetries(3);

        assertThat(FixtureYamlDump.dump(config)).isEqualTo("name: fixture\nretries: 3\n");
    }
}
