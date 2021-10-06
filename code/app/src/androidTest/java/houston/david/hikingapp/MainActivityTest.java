package houston.david.hikingapp;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION");

    @Test
    public void testPickRouteButtonExsists() {
        ViewInteraction button = onView(
                allOf(withId(R.id.btn_finishRouteBtn), withText("PICK A ROUTE"),
                        withParent(withParent(withId(R.id.materialCardView))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));
    }

    @Test
    public void testCreateRouteButtonExists() {
        ViewInteraction button = onView(
                allOf(withId(R.id.btn_createRouteBtn), withText("CREATE A ROUTE"),
                        withParent(withParent(withId(R.id.materialCardView))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));
    }
}
