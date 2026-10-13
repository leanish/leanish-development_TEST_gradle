package example.fixture;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Case 12: uses SnakeYAML 1.x's single-argument {@code Constructor(Class)}, which 2.0 removed.
 * The only fix for CVE-2022-1471 is 2.0, so secure-it's upgrade has to adapt this code.
 */
public final class FixtureYaml {
    private FixtureYaml() {}

    public static FixtureConfig load(String yaml) {
        return new Yaml(new Constructor(FixtureConfig.class, new LoaderOptions())).load(yaml);
    }
}
