package com.example.birdsoffeather;

import static org.junit.Assert.*;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsoffeather.model.db.BluetoothMessageComposite;
import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.Person;
import com.example.birdsoffeather.model.db.PersonWithCourses;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class NearbyMockTest {

    @Test
    public void generatePersonTest() {
        String bofID = UUID.randomUUID().toString();
        StringBuilder testStringBuilder = new StringBuilder();
        testStringBuilder.append(bofID);
        testStringBuilder.append(",,,,");
        testStringBuilder.append('\n');
        testStringBuilder.append("Bill,,,,");
        testStringBuilder.append('\n');
        testStringBuilder.append("https://lh3.googleusercontent.com/pw/AM-JKLXQ2ix4dg-PzLrPOSMOOy6M3PSUrijov9jCLXs4IGSTwN73B4kr-F6Nti_4KsiUU8LzDSGPSWNKnFdKIPqCQ2dFTRbARsW76pevHPBzc51nceZDZrMPmDfAYyI4XNOnPrZarGlLLUZW9wal6j-z9uA6WQ=w854-h924-no?authuser=0,,,,");
        testStringBuilder.append('\n');
        testStringBuilder.append("2021,FA,CSE,210,Large");
        testStringBuilder.append('\n');
        testStringBuilder.append("2022,WI,CSE,110,Huge");
        testStringBuilder.append('\n');
        testStringBuilder.append("2022,SP,CSE,110,Medium");
        testStringBuilder.append('\n');

        BluetoothMessageComposite generatedPerson = NearbyMock.generatePerson(testStringBuilder.toString());
        PersonWithCourses personWithCourses = new PersonWithCourses();
        Person person = new Person(bofID,"Bill","https://lh3.googleusercontent.com/pw/AM-JKLXQ2ix4dg-PzLrPOSMOOy6M3PSUrijov9jCLXs4IGSTwN73B4kr-F6Nti_4KsiUU8LzDSGPSWNKnFdKIPqCQ2dFTRbARsW76pevHPBzc51nceZDZrMPmDfAYyI4XNOnPrZarGlLLUZW9wal6j-z9uA6WQ=w854-h924-no?authuser=0", 0, 0, 0);
        List<Course> courses = new ArrayList<>();
        courses.add(new Course(bofID,"2021","FA","CSE","210", "Large"));
        courses.add(new Course(bofID,"2022","WI","CSE","110", "Huge"));
        courses.add(new Course(bofID,"2022","SP","CSE","110", "Medium"));

        personWithCourses.person = person;
        personWithCourses.courses = courses;

        assertEquals(personWithCourses, generatedPerson.person);
    }

    @Test
    public void invalidStringGeneratePersonTest() {

        String bofID = UUID.randomUUID().toString();
        StringBuilder testStringBuilder = new StringBuilder();
        testStringBuilder.append(bofID);
        testStringBuilder.append(",,,,");

        testStringBuilder.append('\n');

        testStringBuilder.append("Bill,,,");
        testStringBuilder.append('\n');
        testStringBuilder.append("https://lh3.googleusercontent.com/pw/AM-JKLXQ2ix4dg-PzLrPOSMOOy6M3PSUrijov9jCLXs4IGSTwN73B4kr-F6Nti_4KsiUU8LzDSGPSWNKnFdKIPqCQ2dFTRbARsW76pevHPBzc51nceZDZrMPmDfAYyI4XNOnPrZarGlLLUZW9wal6j-z9uA6WQ=w854-h924-no?authuser=0,,,");

        BluetoothMessageComposite generatedPerson = NearbyMock.generatePerson(testStringBuilder.toString());
        assertNull(generatedPerson);

        testStringBuilder = new StringBuilder();
        testStringBuilder.append("Bill,,");
        testStringBuilder.append('\n');
        testStringBuilder.append("https://lh3.googleusercontent.com/pw/AM-JKLXQ2ix4dg-PzLrPOSMOOy6M3PSUrijov9jCLXs4IGSTwN73B4kr-F6Nti_4KsiUU8LzDSGPSWNKnFdKIPqCQ2dFTRbARsW76pevHPBzc51nceZDZrMPmDfAYyI4XNOnPrZarGlLLUZW9wal6j-z9uA6WQ=w854-h924-no?authuser=0,,");
        testStringBuilder.append('\n');
        testStringBuilder.append("2021,FA,210,Large (150-250)");
        testStringBuilder.append('\n');

        generatedPerson = NearbyMock.generatePerson(testStringBuilder.toString());

        assertNull(generatedPerson);

    }

    @Test
    public void generateDifferentPersonTest() {
        String bofID = UUID.randomUUID().toString();
        StringBuilder testStringBuilder = new StringBuilder();
        testStringBuilder.append(bofID);
        testStringBuilder.append(",,,,");
        testStringBuilder.append('\n');

        testStringBuilder.append("Bill,,,,");
        testStringBuilder.append('\n');
        testStringBuilder.append("https://lh3.googleusercontent.com/pw/AM-JKLXQ2ix4dg-PzLrPOSMOOy6M3PSUrijov9jCLXs4IGSTwN73B4kr-F6Nti_4KsiUU8LzDSGPSWNKnFdKIPqCQ2dFTRbARsW76pevHPBzc51nceZDZrMPmDfAYyI4XNOnPrZarGlLLUZW9wal6j-z9uA6WQ=w854-h924-no?authuser=0,,,,");
        testStringBuilder.append('\n');
        testStringBuilder.append("2021,FA,CSE,110,Large");
        testStringBuilder.append('\n');
        testStringBuilder.append("2022,WI,CSE,210,Huge");
        testStringBuilder.append('\n');
        testStringBuilder.append("2022,SP,CSE,110,Medium");
        testStringBuilder.append('\n');

        BluetoothMessageComposite generatedPerson = NearbyMock.generatePerson(testStringBuilder.toString());

        PersonWithCourses personWithCourses = new PersonWithCourses();
        Person person = new Person(bofID,"Bill","https://lh3.googleusercontent.com/pw/AM-JKLXQ2ix4dg-PzLrPOSMOOy6M3PSUrijov9jCLXs4IGSTwN73B4kr-F6Nti_4KsiUU8LzDSGPSWNKnFdKIPqCQ2dFTRbARsW76pevHPBzc51nceZDZrMPmDfAYyI4XNOnPrZarGlLLUZW9wal6j-z9uA6WQ=w854-h924-no?authuser=0", 0, 0, 0);
        List<Course> courses = new ArrayList<>();
        courses.add(new Course(bofID,"2021","FA","CSE","210", "Large"));
        courses.add(new Course(bofID,"2022","WI","CSE","110", "Large"));
        courses.add(new Course(bofID,"2022","SP","CSE","110", "Medium"));

        personWithCourses.person = person;
        personWithCourses.courses = courses;

        assertNotEquals(personWithCourses, generatedPerson.person);
    }

    @Test
    public void testValidMockWithWave() {
        String bofID = UUID.randomUUID().toString();

        List<String> waveIDs = Arrays.asList(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());

        StringBuilder testStringBuilder = new StringBuilder();
        testStringBuilder.append(bofID);
        testStringBuilder.append(",,,,");
        testStringBuilder.append('\n');
        testStringBuilder.append("Bill,,,,");
        testStringBuilder.append('\n');
        testStringBuilder.append("https://lh3.googleusercontent.com/pw/AM-JKLXQ2ix4dg-PzLrPOSMOOy6M3PSUrijov9jCLXs4IGSTwN73B4kr-F6Nti_4KsiUU8LzDSGPSWNKnFdKIPqCQ2dFTRbARsW76pevHPBzc51nceZDZrMPmDfAYyI4XNOnPrZarGlLLUZW9wal6j-z9uA6WQ=w854-h924-no?authuser=0,,,,");
        testStringBuilder.append('\n');
        testStringBuilder.append("2021,FA,CSE,210,Large");
        testStringBuilder.append('\n');
        testStringBuilder.append("2022,WI,CSE,110,Huge");
        testStringBuilder.append('\n');
        testStringBuilder.append("2022,SP,CSE,110,Medium");
        testStringBuilder.append('\n');
        testStringBuilder.append(waveIDs.get(0));
        testStringBuilder.append(",wave,,,");
        testStringBuilder.append('\n');
        testStringBuilder.append(waveIDs.get(1));
        testStringBuilder.append(",wave,,,");
        testStringBuilder.append('\n');
        testStringBuilder.append(waveIDs.get(2));
        testStringBuilder.append(",wave,,,");
        testStringBuilder.append('\n');

        BluetoothMessageComposite generatedPerson = NearbyMock.generatePerson(testStringBuilder.toString());
        PersonWithCourses personWithCourses = new PersonWithCourses();
        Person person = new Person(bofID,"Bill","https://lh3.googleusercontent.com/pw/AM-JKLXQ2ix4dg-PzLrPOSMOOy6M3PSUrijov9jCLXs4IGSTwN73B4kr-F6Nti_4KsiUU8LzDSGPSWNKnFdKIPqCQ2dFTRbARsW76pevHPBzc51nceZDZrMPmDfAYyI4XNOnPrZarGlLLUZW9wal6j-z9uA6WQ=w854-h924-no?authuser=0", 0, 0, 0);
        List<Course> courses = new ArrayList<>();
        courses.add(new Course(bofID,"2021","FA","CSE","210", "Large"));
        courses.add(new Course(bofID,"2022","WI","CSE","110", "Huge"));
        courses.add(new Course(bofID,"2022","SP","CSE","110", "Medium"));

        personWithCourses.person = person;
        personWithCourses.courses = courses;

        assertEquals(personWithCourses, generatedPerson.person);
        assertEquals(waveIDs, generatedPerson.wavedToUUID);
    }

}
