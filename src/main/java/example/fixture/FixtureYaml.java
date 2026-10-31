package example.fixture;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Uses the SnakeYAML 2.0-compatible {@code Constructor(Class, LoaderOptions)} API.
 */
public final class FixtureYaml {
    private FixtureYaml() {}

    public static FixtureConfig load(String yaml) {
        return new Yaml(new Constructor(FixtureConfig.class, new LoaderOptions())).load(yaml);
    }
}
