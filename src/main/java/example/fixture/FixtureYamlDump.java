package example.fixture;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Case 17: SnakeYAML 2.x removed the no-argument {@code Representer()}; the representer now takes the
 * {@code DumperOptions} that are also passed to {@code Yaml}, so both use the same options.
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
