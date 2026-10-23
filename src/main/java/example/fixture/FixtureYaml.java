package example.fixture;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Case 12: SnakeYAML 2.0 removed the single-argument {@code Constructor(Class)}, so this passes {@code LoaderOptions}
 * instead. The only fix for CVE-2022-1471 is 2.0, which is why secure-it's upgrade adapted this code.
 */
public final class FixtureYaml {
    private FixtureYaml() {}

    public static FixtureConfig load(String yaml) {
        return new Yaml(new Constructor(FixtureConfig.class, new LoaderOptions())).load(yaml);
    }
}
