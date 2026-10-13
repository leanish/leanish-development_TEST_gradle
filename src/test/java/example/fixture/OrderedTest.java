package example.fixture;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Case 11: JUnit 6 removes {@code MethodOrderer.Alphanumeric}. The asserted order requires an explicit
 * name-based orderer, so dropping the orderer or the test isn't an adaptation.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class OrderedTest {
    private static final List<String> RAN = new ArrayList<>();

    @Test
    void charlie() {
        RAN.add("charlie");
    }

    @Test
    void alpha() {
        RAN.add("alpha");
    }

    @Test
    void beta() {
        RAN.add("beta");
    }

    @AfterAll
    static void ranInNameOrder() {
        assertThat(RAN).containsExactly("alpha", "beta", "charlie");
    }
}
