package example.fixture;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

/** Dumps the config bean as block YAML; SnakeYAML 2.x's {@code Representer} takes the {@link DumperOptions}. */
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
