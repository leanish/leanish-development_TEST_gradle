package example.fixture;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Case 17: SnakeYAML 2.x's {@code Representer} takes {@code DumperOptions}; the no-argument {@code Representer()}
 * of 1.x was removed in 2.0. It lands after secure-it's SnakeYAML 2.0 PR (case 12) was opened, so that PR's next
 * review had to adapt it.
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
