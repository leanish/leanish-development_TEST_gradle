package example.fixture;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Case 17's SnakeYAML 1.x no-argument {@code Representer()} was removed in 2.0; the 2.0 adaptation
 * passes the same {@code DumperOptions} used by the dumper.
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
