package example.fixture;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FixtureYamlTest {
    @Test
    void loadsTheConfigBean() {
        FixtureConfig config = FixtureYaml.load("name: fixture\nretries: 3\n");

        assertThat(config.getName()).isEqualTo("fixture");
        assertThat(config.getRetries()).isEqualTo(3);
    }
}
