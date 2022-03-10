package com.example.birdsoffeather.model.db;

import java.io.Serializable;
import java.util.List;

public class BluetoothMessageComposite implements Serializable {
    public PersonWithCourses person;
    public List<String> wavedToUUID;

    public BluetoothMessageComposite(PersonWithCourses person, List<String> wavedToUUID) {
        this.person = person;
        this.wavedToUUID = wavedToUUID;
    }
}
