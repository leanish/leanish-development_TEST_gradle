package example.fixture;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Case 12: SnakeYAML 1.x's single-argument {@code Constructor(Class)} was removed in 2.0, so this uses the
 * {@code Constructor(Class, LoaderOptions)} overload that 1.33 also has. The only fix for CVE-2022-1471 is 2.0,
 * which is why the major was needed.
 */
public final class FixtureYaml {
    private FixtureYaml() {}

    public static FixtureConfig load(String yaml) {
        return new Yaml(new Constructor(FixtureConfig.class, new LoaderOptions())).load(yaml);
    }
}
