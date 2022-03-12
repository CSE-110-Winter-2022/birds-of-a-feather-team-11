package com.example.birdsoffeather;


import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import com.example.birdsoffeather.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class FavoriteAndAddingMockTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION");

    @Test
    public void favoriteAndAddingMockTest() {
        ViewInteraction materialButton = onView(
                allOf(withId(R.id.skip_btn), withText("SKIP"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.name_edit_text),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("myName"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.confirm_button), withText("Confirm"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.photo_skip), withText("Skip"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.subject_input),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("CSE"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.course_nbr_input),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("110"), closeSoftKeyboard());

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.class_size_input),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                12),
                        isDisplayed()));
        appCompatSpinner.perform(click());

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(5);
        appCompatCheckedTextView.perform(click());

        ViewInteraction materialButton4 = onView(
                allOf(withId(R.id.enter_btn), withText("Enter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                11),
                        isDisplayed()));
        materialButton4.perform(click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.subject_input), withText("CSE"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("ECE"));

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.subject_input), withText("ECE"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText5.perform(closeSoftKeyboard());

        ViewInteraction appCompatSpinner2 = onView(
                allOf(withId(R.id.class_size_input),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                12),
                        isDisplayed()));
        appCompatSpinner2.perform(click());

        DataInteraction appCompatCheckedTextView2 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(2);
        appCompatCheckedTextView2.perform(click());

        ViewInteraction materialButton5 = onView(
                allOf(withId(R.id.enter_btn), withText("Enter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                11),
                        isDisplayed()));
        materialButton5.perform(click());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.subject_input), withText("ECE"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText6.perform(click());

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.subject_input), withText("ECE"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText7.perform(replaceText("CSE"));

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.subject_input), withText("CSE"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText8.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.course_nbr_input), withText("110"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText9.perform(replaceText("101"));

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.course_nbr_input), withText("101"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText10.perform(closeSoftKeyboard());

        ViewInteraction materialButton6 = onView(
                allOf(withId(R.id.enter_btn), withText("Enter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                11),
                        isDisplayed()));
        materialButton6.perform(click());

        ViewInteraction materialButton7 = onView(
                allOf(withId(R.id.done_btn), withText("Done"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        materialButton7.perform(click());

        ViewInteraction materialButton8 = onView(
                allOf(withId(R.id.start_stop_btn), withText("START"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        materialButton8.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.bof_title), withText("3/11/2022 10:0PM"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView.check(matches(isDisplayed()));

        ViewInteraction materialButton9 = onView(
                allOf(withId(R.id.add_mock), withText("Add mock"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton9.perform(click());

        ViewInteraction appCompatEditText11 = onView(
                allOf(withId(R.id.person_entry_edit_text),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText11.perform(replaceText("UUIDPerson1\nPerson1\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Winter,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"), closeSoftKeyboard());

        ViewInteraction appCompatEditText12 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson1\nPerson1\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Winter,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText12.perform(click());

        ViewInteraction appCompatEditText13 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson1\nPerson1\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Winter,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText13.perform(replaceText("UUIDPerson1\nPerson1\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Fall,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"));

        ViewInteraction appCompatEditText14 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson1\nPerson1\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Fall,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText14.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText15 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson1\nPerson1\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Fall,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText15.perform(click());

        ViewInteraction appCompatEditText16 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson1\nPerson1\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Fall,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText16.perform(replaceText("UUIDPerson1\nPerson1\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Fall,CSE,110,Large\nmyID,wave"));

        ViewInteraction appCompatEditText17 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson1\nPerson1\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Fall,CSE,110,Large\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText17.perform(closeSoftKeyboard());

        ViewInteraction materialButton10 = onView(
                allOf(withId(R.id.add_button), withText("Add"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        materialButton10.perform(click());

        pressBack();

        ViewInteraction materialButton11 = onView(
                allOf(withId(R.id.add_mock), withText("Add mock"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton11.perform(click());

        ViewInteraction appCompatEditText18 = onView(
                allOf(withId(R.id.person_entry_edit_text),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText18.perform(replaceText("UUIDPerson1\nPerson1\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Winter,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"), closeSoftKeyboard());

        ViewInteraction appCompatEditText19 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson1\nPerson1\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Winter,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText19.perform(click());

        ViewInteraction appCompatEditText20 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson1\nPerson1\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Winter,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText20.perform(replaceText("UUIDPerson1\nPerson2\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Winter,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"));

        ViewInteraction appCompatEditText21 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson1\nPerson2\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Winter,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText21.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText22 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson1\nPerson2\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Winter,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText22.perform(click());

        ViewInteraction appCompatEditText23 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson1\nPerson2\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Winter,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText23.perform(replaceText("UUIDPerson2\nPerson2\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Winter,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"));

        ViewInteraction appCompatEditText24 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson2\nPerson2\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Winter,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText24.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText25 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson2\nPerson2\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Winter,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText25.perform(click());

        ViewInteraction appCompatEditText26 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson2\nPerson2\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Winter,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText26.perform(click());

        ViewInteraction appCompatEditText27 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson2\nPerson2\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Winter,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText27.perform(click());

        ViewInteraction appCompatEditText28 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson2\nPerson2\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Winter,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText28.perform(click());

        ViewInteraction appCompatEditText29 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson2\nPerson2\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Winter,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText29.perform(replaceText("UUIDPerson2\nPerson2\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Fall,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"));

        ViewInteraction appCompatEditText30 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson2\nPerson2\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Fall,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText30.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText31 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson2\nPerson2\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Fall,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText31.perform(click());

        ViewInteraction appCompatEditText32 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson2\nPerson2\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Fall,CSE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText32.perform(replaceText("UUIDPerson2\nPerson2\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Fall,ECE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"));

        ViewInteraction appCompatEditText33 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson2\nPerson2\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Fall,ECE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText33.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText34 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson2\nPerson2\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Fall,ECE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText34.perform(click());

        ViewInteraction appCompatEditText35 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson2\nPerson2\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Fall,ECE,110,Large\n2022,Winter,MATH,140B,Huge\n2021,Spring,CSE,30,Gigantic\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText35.perform(replaceText("UUIDPerson2\nPerson2\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Fall,ECE,110,Large\n2022,Fall,CSE,101,Huge\nmyID,wave"));

        ViewInteraction appCompatEditText36 = onView(
                allOf(withId(R.id.person_entry_edit_text), withText("UUIDPerson2\nPerson2\nhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.medicalnewstoday.com%2Farticles%2Fasymmetrical-face&psig=AOvVaw1r0_wZEfeBw98wV8OJBAmM&ust=1647144199802000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJCp3brYv_YCFQAAAAAdAAAAABAD\n2022,Fall,ECE,110,Large\n2022,Fall,CSE,101,Huge\nmyID,wave"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText36.perform(closeSoftKeyboard());

        ViewInteraction materialButton12 = onView(
                allOf(withId(R.id.add_button), withText("Add"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        materialButton12.perform(click());

        pressBack();

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.person_row_name), withText("Person2"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class))),
                        isDisplayed()));
        textView2.check(matches(withText("Person2")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.person_row_name), withText("Person1"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class))),
                        isDisplayed()));
        textView3.check(matches(withText("Person1")));

        ViewInteraction appCompatSpinner3 = onView(
                allOf(withId(R.id.filter_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        appCompatSpinner3.perform(click());

        DataInteraction appCompatCheckedTextView3 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        appCompatCheckedTextView3.perform(click());

        ViewInteraction appCompatSpinner4 = onView(
                allOf(withId(R.id.filter_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        appCompatSpinner4.perform(click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.favorite_view), withContentDescription("favorite person"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.cardview.widget.CardView")),
                                        0),
                                3),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction materialButton13 = onView(
                allOf(withId(R.id.fav_button), withText("FAVORITES"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                6),
                        isDisplayed()));
        materialButton13.perform(click());

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.person_row_name), withText("Person2"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class))),
                        isDisplayed()));
        textView4.check(matches(withText("Person2")));

        ViewInteraction materialButton14 = onView(
                allOf(withId(R.id.GoBackButton2), withText("GO BACK"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        materialButton14.perform(click());

        ViewInteraction materialButton15 = onView(
                allOf(withId(R.id.start_stop_btn), withText("Stop"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        materialButton15.perform(click());

        ViewInteraction appCompatEditText37 = onView(
                allOf(withId(R.id.rename_other_edit_text), withText("Name"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText37.perform(replaceText("Test"));

        ViewInteraction appCompatEditText38 = onView(
                allOf(withId(R.id.rename_other_edit_text), withText("Test"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText38.perform(closeSoftKeyboard());

        ViewInteraction materialButton16 = onView(
                allOf(withId(R.id.rename_confirm_button), withText("CONFIRM"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        materialButton16.perform(click());

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.bof_title), withText("Test"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView5.check(matches(withText("Test")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
