package example.fixture;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Case 17: uses explicit dumper options with the representer for SnakeYAML 2.x.
 */
public final class FixtureYamlDump {
    private FixtureYamlDump() {}

    public static String dump(FixtureConfig config) {
        DumperOptions options = new DumperOptions();
        Representer representer = new Representer(options);
        representer.addClassTag(FixtureConfig.class, Tag.MAP);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(representer, options).dump(config);
    }
}
