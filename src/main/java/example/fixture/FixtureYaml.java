package example.fixture;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Case 12: uses SnakeYAML 2.x's {@code Constructor(Class, LoaderOptions)}. SnakeYAML 1.x's single-argument
 * {@code Constructor(Class)} was removed in 2.0, the release that fixes CVE-2022-1471.
 */
public final class FixtureYaml {
    private FixtureYaml() {}

    public static FixtureConfig load(String yaml) {
        return new Yaml(new Constructor(FixtureConfig.class, new LoaderOptions())).load(yaml);
    }
}
