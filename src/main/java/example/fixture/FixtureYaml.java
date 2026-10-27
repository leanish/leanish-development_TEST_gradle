package example.fixture;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Case 12: SnakeYAML 1.x's single-argument {@code Constructor(Class)} was removed in 2.0, the first release that fixes
 * CVE-2022-1471 (GHSA-mjmj-j48q-9wg2), so the constructor takes {@link LoaderOptions} instead.
 */
public final class FixtureYaml {
    private FixtureYaml() {}

    public static FixtureConfig load(String yaml) {
        return new Yaml(new Constructor(FixtureConfig.class, new LoaderOptions())).load(yaml);
    }
}
