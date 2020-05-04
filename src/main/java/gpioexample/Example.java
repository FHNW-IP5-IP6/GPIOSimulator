package gpioexample;

public abstract class Example {
    private int key;
    private String title;

    public Example(int key, String title) {
        this.key = key;
        this.title = title;
    }

    public int getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public abstract void execute() throws Exception;
}
