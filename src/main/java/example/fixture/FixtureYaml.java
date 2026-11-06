package example.fixture;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Case 12: uses explicit loader options with the bean constructor for SnakeYAML 2.x.
 * The upgrade addresses CVE-2022-1471.
 */
public final class FixtureYaml {
    private FixtureYaml() {}

    public static FixtureConfig load(String yaml) {
        return new Yaml(new Constructor(FixtureConfig.class, new LoaderOptions())).load(yaml);
    }
}
