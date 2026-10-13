package example.fixture;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Case 17: supplies {@code DumperOptions} to replace SnakeYAML 1.x's no-argument
 * {@code Representer()}, removed in 2.0. This case landed after secure-it's
 * SnakeYAML 2.0 PR (case 12) was opened, requiring adaptation during review.
 */
public final class FixtureYamlDump {
    private FixtureYamlDump() {}

    public static String dump(FixtureConfig config) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Representer representer = new Representer(options);
        representer.addClassTag(FixtureConfig.class, Tag.MAP);
        return new Yaml(representer, options).dump(config);
    }
}
