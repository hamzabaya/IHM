package fr.msemna.tp_ihm;

import javafx.beans.property.*;

public class Photo {
    private final StringProperty path = new SimpleStringProperty();
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();

    public Photo(String path, double x, double y) {
        this.path.set(path);
        this.x.set(x);
        this.y.set(y);
    }

    public StringProperty pathProperty() { return path; }
    public DoubleProperty xProperty() { return x; }
    public DoubleProperty yProperty() { return y; }
}
