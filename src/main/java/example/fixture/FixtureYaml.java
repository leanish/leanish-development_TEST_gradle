package example.fixture;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Case 12: SnakeYAML 2.0 removed the single-argument {@code Constructor(Class)}. The overload
 * with {@code LoaderOptions} keeps the same root type while supporting the security upgrade.
 */
public final class FixtureYaml {
    private FixtureYaml() {}

    public static FixtureConfig load(String yaml) {
        return new Yaml(new Constructor(FixtureConfig.class, new LoaderOptions())).load(yaml);
    }
}
