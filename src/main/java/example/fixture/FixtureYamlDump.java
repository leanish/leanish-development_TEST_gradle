package example.fixture;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Case 17: uses SnakeYAML 2.0's {@code Representer(DumperOptions)}, which replaces the no-argument
 * {@code Representer()} that 2.0 removed. Secure-it's SnakeYAML 2.0 move (case 12) adapted it.
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
