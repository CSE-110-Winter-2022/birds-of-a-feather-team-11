package com.example.birdsoffeather;

import static org.junit.Assert.*;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.Person;
import com.example.birdsoffeather.model.db.PersonWithCourses;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class NearbyMockTest {

    @Test
    public void generatePersonTest() {
        StringBuilder testStringBuilder = new StringBuilder();
        testStringBuilder.append("Bill,,,");
        testStringBuilder.append('\n');
        testStringBuilder.append("https://lh3.googleusercontent.com/pw/AM-JKLXQ2ix4dg-PzLrPOSMOOy6M3PSUrijov9jCLXs4IGSTwN73B4kr-F6Nti_4KsiUU8LzDSGPSWNKnFdKIPqCQ2dFTRbARsW76pevHPBzc51nceZDZrMPmDfAYyI4XNOnPrZarGlLLUZW9wal6j-z9uA6WQ=w854-h924-no?authuser=0,,,");
        testStringBuilder.append('\n');
        testStringBuilder.append("2021,FA,CSE,210");
        testStringBuilder.append('\n');
        testStringBuilder.append("2022,WI,CSE,110");
        testStringBuilder.append('\n');
        testStringBuilder.append("2022,SP,CSE,110");
        testStringBuilder.append('\n');

        PersonWithCourses generatedPerson = NearbyMock.generatePerson(testStringBuilder.toString());

        PersonWithCourses personWithCourses = new PersonWithCourses();
        Person person = new Person(0,"Bill","https://lh3.googleusercontent.com/pw/AM-JKLXQ2ix4dg-PzLrPOSMOOy6M3PSUrijov9jCLXs4IGSTwN73B4kr-F6Nti_4KsiUU8LzDSGPSWNKnFdKIPqCQ2dFTRbARsW76pevHPBzc51nceZDZrMPmDfAYyI4XNOnPrZarGlLLUZW9wal6j-z9uA6WQ=w854-h924-no?authuser=0");
        List<Course> courses = new ArrayList<>();
        courses.add(new Course(0,0,"2021","FA","CSE","210"));
        courses.add(new Course(0,0,"2022","WI","CSE","110"));
        courses.add(new Course(0,0,"2022","SP","CSE","110"));

        personWithCourses.person = person;
        personWithCourses.courses = courses;

        assertEquals(generatedPerson, personWithCourses);
    }

    @Test
    public void invalidStringGeneratePersonTest() {
        StringBuilder testStringBuilder = new StringBuilder();
        testStringBuilder.append("Bill,,,");
        testStringBuilder.append('\n');
        testStringBuilder.append("https://lh3.googleusercontent.com/pw/AM-JKLXQ2ix4dg-PzLrPOSMOOy6M3PSUrijov9jCLXs4IGSTwN73B4kr-F6Nti_4KsiUU8LzDSGPSWNKnFdKIPqCQ2dFTRbARsW76pevHPBzc51nceZDZrMPmDfAYyI4XNOnPrZarGlLLUZW9wal6j-z9uA6WQ=w854-h924-no?authuser=0,,,");

        PersonWithCourses generatedPerson = NearbyMock.generatePerson(testStringBuilder.toString());
        assertNull(generatedPerson);

        testStringBuilder = new StringBuilder();
        testStringBuilder.append("Bill,,");
        testStringBuilder.append('\n');
        testStringBuilder.append("https://lh3.googleusercontent.com/pw/AM-JKLXQ2ix4dg-PzLrPOSMOOy6M3PSUrijov9jCLXs4IGSTwN73B4kr-F6Nti_4KsiUU8LzDSGPSWNKnFdKIPqCQ2dFTRbARsW76pevHPBzc51nceZDZrMPmDfAYyI4XNOnPrZarGlLLUZW9wal6j-z9uA6WQ=w854-h924-no?authuser=0,,");
        testStringBuilder.append('\n');
        testStringBuilder.append("2021,FA,210");
        testStringBuilder.append('\n');

        generatedPerson = NearbyMock.generatePerson(testStringBuilder.toString());

        assertNull(generatedPerson);

    }

    @Test
    public void generateDifferentPersonTest() {
        StringBuilder testStringBuilder = new StringBuilder();
        testStringBuilder.append("Bill,,,");
        testStringBuilder.append('\n');
        testStringBuilder.append("https://lh3.googleusercontent.com/pw/AM-JKLXQ2ix4dg-PzLrPOSMOOy6M3PSUrijov9jCLXs4IGSTwN73B4kr-F6Nti_4KsiUU8LzDSGPSWNKnFdKIPqCQ2dFTRbARsW76pevHPBzc51nceZDZrMPmDfAYyI4XNOnPrZarGlLLUZW9wal6j-z9uA6WQ=w854-h924-no?authuser=0,,,");
        testStringBuilder.append('\n');
        testStringBuilder.append("2021,FA,CSE,110");
        testStringBuilder.append('\n');
        testStringBuilder.append("2022,WI,CSE,210");
        testStringBuilder.append('\n');
        testStringBuilder.append("2022,SP,CSE,110");
        testStringBuilder.append('\n');

        PersonWithCourses generatedPerson = NearbyMock.generatePerson(testStringBuilder.toString());

        PersonWithCourses personWithCourses = new PersonWithCourses();
        Person person = new Person(0,"Bill","https://lh3.googleusercontent.com/pw/AM-JKLXQ2ix4dg-PzLrPOSMOOy6M3PSUrijov9jCLXs4IGSTwN73B4kr-F6Nti_4KsiUU8LzDSGPSWNKnFdKIPqCQ2dFTRbARsW76pevHPBzc51nceZDZrMPmDfAYyI4XNOnPrZarGlLLUZW9wal6j-z9uA6WQ=w854-h924-no?authuser=0");
        List<Course> courses = new ArrayList<>();
        courses.add(new Course(0,0,"2021","FA","CSE","210"));
        courses.add(new Course(0,0,"2022","WI","CSE","110"));
        courses.add(new Course(0,0,"2022","SP","CSE","110"));

        personWithCourses.person = person;
        personWithCourses.courses = courses;

        assertNotEquals(generatedPerson, personWithCourses);
    }

}
