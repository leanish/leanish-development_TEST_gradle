package example.fixture;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Case 12: the only fix for CVE-2022-1471 (GHSA-mjmj-j48q-9wg2) is SnakeYAML 2.0, which removed the
 * single-argument {@code Constructor(Class)}; it is now built with {@code Constructor(Class, LoaderOptions)}.
 */
public final class FixtureYaml {
    private FixtureYaml() {}

    public static FixtureConfig load(String yaml) {
        return new Yaml(new Constructor(FixtureConfig.class, new LoaderOptions())).load(yaml);
    }
}
