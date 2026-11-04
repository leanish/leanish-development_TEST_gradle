package example.fixture;

/** A bean SnakeYAML fills from YAML; case 12 (secure-it major, adapted for SnakeYAML 2.x). */
public final class FixtureConfig {
    private String name;
    private int retries;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }
}
