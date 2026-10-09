package example.fixture;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

class HexTest {
    @Test
    void encodesBytes() {
        assertThat(Hex.encode(new byte[] {0, 15, -1})).isEqualTo("000fff");
    }

    @Test
    void usesTheConventionDependency() {
        assertThat(IOUtils.toInputStream("fixture", StandardCharsets.UTF_8)).hasContent("fixture");
    }
}
