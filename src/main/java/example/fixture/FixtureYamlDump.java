package example.fixture;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Case 17: SnakeYAML 2.0 removed 1.x's no-argument {@code Representer()}, so this passes {@code DumperOptions}
 * instead. It landed after secure-it's SnakeYAML 2.0 PR (case 12) was opened, and that PR's apply adapted it.
 */
public final class FixtureYamlDump {
    private FixtureYamlDump() {}

    public static String dump(FixtureConfig config) {
        Representer representer = new Representer(new DumperOptions());
        representer.addClassTag(FixtureConfig.class, Tag.MAP);
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(representer, options).dump(config);
    }
}
