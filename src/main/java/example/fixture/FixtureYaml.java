package example.fixture;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Case 12: SnakeYAML 2.0 (the fix for CVE-2022-1471, GHSA-mjmj-j48q-9wg2) removed the single-argument
 * {@code Constructor(Class)}; this passes {@code LoaderOptions} to the two-argument overload, which 1.33 also has.
 */
public final class FixtureYaml {
    private FixtureYaml() {}

    public static FixtureConfig load(String yaml) {
        return new Yaml(new Constructor(FixtureConfig.class, new LoaderOptions())).load(yaml);
    }
}
