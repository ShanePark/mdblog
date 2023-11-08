package io.github.shanepark;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Phone implements Serializable {

    private static final long serialVersionUID = 1L;

    private String model;
    private String number;
    transient String password;

    public Phone() {
    }

    public Phone(String model, String number, String password) {
        this.model = model;
        this.number = number;
        this.password = password;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "model='" + model + '\'' +
                ", number='" + number + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    public String getModel() {
        return model;
    }

    public String getNumber() {
        return number;
    }

}
