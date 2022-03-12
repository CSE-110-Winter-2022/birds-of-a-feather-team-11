package com.example.birdsoffeather.model.db;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class BluetoothMessageComposite implements Serializable {
    public PersonWithCourses person;
    public List<String> wavedToUUID;

    public BluetoothMessageComposite(PersonWithCourses person, List<String> wavedToUUID) {
        this.person = person;
        this.wavedToUUID = wavedToUUID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BluetoothMessageComposite person1 = (BluetoothMessageComposite) o;
        return this.person.equals(person1.person) && this.wavedToUUID.equals(person1.wavedToUUID);
    }
}
