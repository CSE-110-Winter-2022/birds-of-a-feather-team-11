package com.example.birdsoffeather;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class InvalidClassesInputTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void invalidClassesInputTest() {
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
        appCompatEditText.perform(replaceText("name"), closeSoftKeyboard());

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

        ViewInteraction materialButton4 = onView(
                allOf(withId(R.id.done_btn), withText("Done"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        materialButton4.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(android.R.id.message), withText("You must enter in at least one class!"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class))),
                        isDisplayed()));
        textView.check(matches(withText("You must enter in at least one class!")));

        ViewInteraction materialButton5 = onView(
                allOf(withId(android.R.id.button1), withText("Ok"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton5.perform(scrollTo(), click());

        ViewInteraction materialButton6 = onView(
                allOf(withId(R.id.enter_btn), withText("Enter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                11),
                        isDisplayed()));
        materialButton6.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withId(android.R.id.message), withText("Subject has not been entered!"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class))),
                        isDisplayed()));
        textView2.check(matches(withText("Subject has not been entered!")));

        ViewInteraction materialButton7 = onView(
                allOf(withId(android.R.id.button1), withText("Ok"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton7.perform(scrollTo(), click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.subject_input),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("CSE"), closeSoftKeyboard());

        ViewInteraction materialButton8 = onView(
                allOf(withId(R.id.enter_btn), withText("Enter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                11),
                        isDisplayed()));
        materialButton8.perform(click());

        ViewInteraction textView3 = onView(
                allOf(withId(android.R.id.message), withText("Course number has not been entered!"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class))),
                        isDisplayed()));
        textView3.check(matches(withText("Course number has not been entered!")));

        ViewInteraction materialButton9 = onView(
                allOf(withId(android.R.id.button1), withText("Ok"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton9.perform(scrollTo(), click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.course_nbr_input),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("110"), closeSoftKeyboard());

        ViewInteraction materialButton10 = onView(
                allOf(withId(R.id.enter_btn), withText("Enter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                11),
                        isDisplayed()));
        materialButton10.perform(click());

        ViewInteraction materialButton11 = onView(
                allOf(withId(R.id.enter_btn), withText("Enter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                11),
                        isDisplayed()));
        materialButton11.perform(click());

        ViewInteraction textView4 = onView(
                allOf(withId(android.R.id.message), withText("Duplicate Entry"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class))),
                        isDisplayed()));
        textView4.check(matches(withText("Duplicate Entry")));

        ViewInteraction materialButton12 = onView(
                allOf(withId(android.R.id.button1), withText("Ok"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton12.perform(scrollTo(), click());

        ViewInteraction textView5 = onView(
                allOf(withId(android.R.id.text1), withText("2022"),
                        withParent(allOf(withId(R.id.year_input),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        textView5.check(matches(withText("2022")));

        ViewInteraction textView6 = onView(
                allOf(withId(android.R.id.text1), withText("Fall"),
                        withParent(allOf(withId(R.id.quarter_input),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        textView6.check(matches(withText("Fall")));

        ViewInteraction editText = onView(
                allOf(withId(R.id.subject_input), withText("CSE"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        editText.check(matches(withText("CSE")));

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.course_nbr_input), withText("110"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        editText2.check(matches(withText("110")));

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.course_nbr_input), withText("110"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText4.perform(click());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.course_nbr_input), withText("110"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("100"));

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.course_nbr_input), withText("100"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText6.perform(closeSoftKeyboard());

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.quarter_input),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatSpinner.perform(click());

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(2);
        appCompatCheckedTextView.perform(click());

        ViewInteraction materialButton13 = onView(
                allOf(withId(R.id.enter_btn), withText("Enter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                11),
                        isDisplayed()));
        materialButton13.perform(click());

        ViewInteraction materialButton14 = onView(
                allOf(withId(R.id.done_btn), withText("Done"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        materialButton14.perform(click());

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.bof_title), withText("BOF"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView7.check(matches(withText("BOF")));
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
