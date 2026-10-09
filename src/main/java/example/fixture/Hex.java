package example.fixture;

/** Uses a stable API so routine upgrades need no source adaptations. */
public final class Hex {
    private Hex() {}

    public static String encode(byte[] bytes) {
        return org.apache.commons.codec.binary.Hex.encodeHexString(bytes);
    }
}
