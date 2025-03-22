package com.insightsfromtech.myfoodapp.Activities
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


import com.insightsfromtech.myfoodapp.Fragments.HistoryFragment
import com.insightsfromtech.myfoodapp.Fragments.FoodFragment
import com.insightsfromtech.myfoodapp.Fragments.HomeFragment
import com.insightsfromtech.myfoodapp.Fragments.PaymentsFragment
import com.insightsfromtech.myfoodapp.R
import com.insightsfromtech.myfoodapp.RoomDatabaseComponents.Meal
import com.insightsfromtech.myfoodapp.RoomDatabaseComponents.MealDao
import com.insightsfromtech.myfoodapp.RoomDatabaseComponents.MealDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(){
    private val mealDao by lazy { MealDatabase.getDatabase(this).taskDao()}
    private lateinit var homeFragment: HomeFragment
    private lateinit var foodFragment: FoodFragment
    private lateinit var historyFragment: HistoryFragment
    private lateinit var paymentsFragment: PaymentsFragment
    private var activeFragment: Fragment? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize Fragments
        homeFragment = HomeFragment()
        foodFragment = FoodFragment()
        historyFragment = HistoryFragment()
        paymentsFragment = PaymentsFragment()
        drawerLayout = findViewById(R.id.main)
        actionBarDrawerToggle = ActionBarDrawerToggle(this,drawerLayout,R.string.nav_open,R.string.nav_close)
        val navView : NavigationView = findViewById(R.id.nav_view)


        // Add fragments once and hide them
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragment_container, homeFragment, "home").hide(homeFragment)
            add(R.id.fragment_container, foodFragment, "food").hide(foodFragment)
            add(R.id.fragment_container, historyFragment, "history").hide(historyFragment)
            add(R.id.fragment_container, paymentsFragment, "payments").hide(paymentsFragment)
            commit()
        }

        // Show homeFragment by default
        activeFragment = homeFragment
        supportFragmentManager.beginTransaction().show(homeFragment).commit()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)
        window.navigationBarDividerColor = ContextCompat.getColor(this, R.color.navigationBar)

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home -> switchFragment(homeFragment)
                R.id.food -> switchFragment(foodFragment)
                R.id.history -> switchFragment(historyFragment)
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true

        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> switchFragment(homeFragment)
                R.id.food -> switchFragment(foodFragment)
                R.id.facilities -> switchFragment(historyFragment)
                R.id.payment -> switchFragment(paymentsFragment)
            }
            true
        }

        prepopulateMeals(mealDao)
    }


    private fun switchFragment(fragment: Fragment) {
        if (fragment != activeFragment) {
            supportFragmentManager.beginTransaction().apply {
                activeFragment?.let { hide(it) } // Hide current fragment
                show(fragment) // Show new fragment
                commit()
            }
            activeFragment = fragment
        }
    }

    fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START)
    }




    private fun prepopulateMeals(mealDao: MealDao) {
        CoroutineScope(Dispatchers.IO).launch { // Assuming you have this method

            val newMeals = listOf(

                Meal(dayOfWeek = "Monday", timeOfDay = "Breakfast", mealType = "Veg", name = "Idli", description = "South Indian breakfast", image = R.drawable.idli, isSelected = true),
                Meal(dayOfWeek = "Monday", timeOfDay = "Breakfast", mealType = "Non-Veg", name = "Eggs", description = "Boiled eggs", image = R.drawable.dall_e_2025_03_04_14_14_37___a_photorealistic_top_down_view_of_a_plate_of_boiled_eggs__sliced_in_half__garnished_with_black_pepper_and_salt__the_plate_is_placed_on_a_simple_wooden),

                Meal(dayOfWeek = "Monday", timeOfDay = "Lunch", mealType = "Veg", name = "Dal Rice", description = "North Indian lunch", image = R.drawable.dall_e_2025_03_04_14_14_39___a_photorealistic_top_down_view_of_a_plate_of_dal_rice__with_steamed_white_rice_topped_with_yellow_lentil_dal__garnished_with_coriander_and_a_drizzle_o, isSelected = true),
                Meal(dayOfWeek = "Monday", timeOfDay = "Lunch", mealType = "Non-Veg", name = "Chicken Curry", description = "Spicy chicken curry", image = R.drawable.dall_e_2025_03_04_14_14_41___a_photorealistic_top_down_view_of_a_bowl_of_chicken_curry__with_tender_chicken_pieces_in_a_rich__red_orange_gravy__garnished_with_fresh_coriander__the),

                Meal(dayOfWeek = "Monday", timeOfDay = "Dinner", mealType = "Veg", name = "Kadhi Chawal", description = "North Indian dinner", image = R.drawable.dall_e_2025_03_04_14_14_42___a_photorealistic_top_down_view_of_a_plate_of_kadhai_chawal__featuring_stir_fried_rice_with_bell_peppers__onions__tomatoes__and_indian_spices__the_dish, isSelected = true),
                Meal(dayOfWeek = "Monday", timeOfDay = "Dinner", mealType = "Non-Veg", name = "Chicken Biryani", description = "North Indian dinner", image = R.drawable.dall_e_2025_03_04_14_14_44___a_photorealistic_top_down_view_of_a_plate_of_chicken_biryani__with_aromatic_basmati_rice__tender_chicken_pieces__and_garnished_with_fried_onions__mint),

                // Tuesday Meals
                Meal(dayOfWeek = "Tuesday", timeOfDay = "Breakfast", mealType = "Veg", name = "Poha", description = "Light and healthy breakfast", image = R.drawable.dall_e_2025_03_04_14_14_48___a_photorealistic_top_down_view_of_a_plate_of_poha__a_traditional_indian_breakfast_dish_made_of_flattened_rice__garnished_with_mustard_seeds__green_chi, isSelected = true),
                Meal(dayOfWeek = "Tuesday", timeOfDay = "Breakfast", mealType = "Non-Veg", name = "Omelette", description = "Protein-rich breakfast", image = R.drawable.dall_e_2025_03_04_14_14_52___a_photorealistic_top_down_view_of_a_plate_with_a_freshly_cooked_omelette__garnished_with_herbs_and_served_with_a_side_of_toast_and_tomato_slices__the_),

                Meal(dayOfWeek = "Tuesday", timeOfDay = "Lunch", mealType = "Veg", name = "Chole Bhature", description = "Popular North Indian meal", image = R.drawable.dall_e_2025_03_04_14_14_55___a_photorealistic_top_down_view_of_a_plate_of_chole_bhature__an_indian_dish_consisting_of_a_fluffy__deep_fried_bhatura_served_with_a_bowl_of_spicy_chic, isSelected = true),
                Meal(dayOfWeek = "Tuesday", timeOfDay = "Lunch", mealType = "Non-Veg", name = "Fish Curry", description = "Delicious South Indian fish curry", image = R.drawable.dall_e_2025_03_04_14_15_01___a_photorealistic_top_down_view_of_a_bowl_of_fish_curry__featuring_tender_fish_pieces_in_a_rich__spicy__and_slightly_tangy_red_orange_curry_sauce__garn),

                Meal(dayOfWeek = "Tuesday", timeOfDay = "Dinner", mealType = "Veg", name = "Paneer Butter Masala", description = "Rich paneer curry with butter", image = R.drawable.dall_e_2025_03_04_14_15_08___a_photorealistic_top_down_view_of_a_bowl_of_paneer_butter_masala__featuring_soft_paneer_cubes_in_a_rich__creamy_tomato_based_gravy_with_a_deep_orange_, isSelected = true),
                Meal(dayOfWeek = "Tuesday", timeOfDay = "Dinner", mealType = "Non-Veg", name = "Mutton Rogan Josh", description = "Spicy Kashmiri mutton curry", image = R.drawable.dall_e_2025_03_04_14_15_10___a_photorealistic_top_down_view_of_a_bowl_of_mutton_rogan_josh__a_rich_and_flavorful_kashmiri_curry_featuring_tender_mutton_pieces_in_a_deep_red__aroma),

                // Wednesday Meals
                Meal(dayOfWeek = "Wednesday", timeOfDay = "Breakfast", mealType = "Veg", name = "Aloo Paratha", description = "Stuffed paratha with potato", image = R.drawable.dall_e_2025_03_04_14_15_14___a_photorealistic_top_down_view_of_a_plate_with_aloo_paratha__an_indian_stuffed_flatbread__served_with_butter__curd__and_pickle_on_the_side__the_parath, isSelected = true),
                Meal(dayOfWeek = "Wednesday", timeOfDay = "Breakfast", mealType = "Non-Veg", name = "Chicken Sandwich", description = "Grilled chicken sandwich", image = R.drawable.dall_e_2025_03_04_14_15_18___a_photorealistic_top_down_view_of_a_plate_with_a_chicken_sandwich__the_sandwich_is_golden_brown__with_visible_grilled_chicken__lettuce__tomato__and_a_),

                Meal(dayOfWeek = "Wednesday", timeOfDay = "Lunch", mealType = "Veg", name = "Veg Pulao", description = "Fragrant rice cooked with vegetables", image = R.drawable.dall_e_2025_03_04_14_15_20___a_photorealistic_top_down_view_of_a_plate_with_veg_pulao__the_rice_is_fluffy_and_mixed_with_colorful_vegetables_like_peas__carrots__and_beans__garnish, isSelected = true),
                Meal(dayOfWeek = "Wednesday", timeOfDay = "Lunch", mealType = "Non-Veg", name = "Prawn Masala", description = "Spicy prawn curry", image = R.drawable.dall_e_2025_03_04_14_15_22___a_photorealistic_top_down_view_of_a_plate_with_prawn_masala__the_prawns_are_cooked_in_a_rich__spicy_red_masala_sauce_with_visible_curry_leaves_and_gar),

                Meal(dayOfWeek = "Wednesday", timeOfDay = "Dinner", mealType = "Veg", name = "Vegetable Kofta", description = "Soft vegetable dumplings in curry", image = R.drawable.dall_e_2025_03_04_14_15_24___a_photorealistic_top_down_view_of_a_plate_with_vegetable_kofta__the_kofta_balls_are_golden_brown_and_served_in_a_rich__creamy_tomato_based_gravy__garn, isSelected = true),
                Meal(dayOfWeek = "Wednesday", timeOfDay = "Dinner", mealType = "Non-Veg", name = "Grilled Chicken", description = "Juicy grilled chicken", image = R.drawable.dall_e_2025_03_04_14_15_26___a_photorealistic_top_down_view_of_a_plate_with_grilled_chicken__the_chicken_pieces_are_perfectly_grilled_with_visible_grill_marks__served_with_a_side_),

                // Thursday Meals
                Meal(dayOfWeek = "Thursday", timeOfDay = "Breakfast", mealType = "Veg", name = "Upma", description = "Soft and fluffy South Indian breakfast", image = R.drawable.dall_e_2025_03_04_14_15_28___a_photorealistic_top_down_view_of_a_plate_with_upma__the_upma_is_soft_and_fluffy__garnished_with_mustard_seeds__curry_leaves__and_chopped_coriander__s, isSelected = true),
                Meal(dayOfWeek = "Thursday", timeOfDay = "Breakfast", mealType = "Non-Veg", name = "Bacon", description = "Crispy bacon strips", image = R.drawable.dall_e_2025_03_04_14_15_30___a_photorealistic_top_down_view_of_a_plate_with_crispy_bacon_strips__perfectly_cooked_and_arranged_neatly_on_a_white_plate__the_bacon_has_a_rich_golden),

                Meal(dayOfWeek = "Thursday", timeOfDay = "Lunch", mealType = "Veg", name = "Mixed Vegetable Curry", description = "Healthy and nutritious meal", image = R.drawable.dall_e_2025_03_04_14_15_33___a_photorealistic_top_down_view_of_a_plate_with_mixed_vegetable_curry__showing_a_rich__colorful_assortment_of_vegetables_like_carrots__peas__potatoes__, isSelected = true),
                Meal(dayOfWeek = "Thursday", timeOfDay = "Lunch", mealType = "Non-Veg", name = "Butter Chicken", description = "Creamy North Indian curry", image = R.drawable.dall_e_2025_03_04_14_15_35___a_photorealistic_top_down_view_of_a_plate_with_rich_and_creamy_butter_chicken__the_chicken_is_coated_in_a_smooth__orange_red_tomato_based_gravy_with_a),

                Meal(dayOfWeek = "Thursday", timeOfDay = "Dinner", mealType = "Veg", name = "Dal Tadka", description = "Flavorful dal tempered with spices", image = R.drawable.dall_e_2025_03_04_14_31_07___a_photorealistic_top_down_view_of_a_bowl_of_dal_tadka__an_indian_lentil_dish_garnished_with_tempered_spices__ghee__and_coriander__the_dish_has_a_rich_, isSelected = true),
                Meal(dayOfWeek = "Thursday", timeOfDay = "Dinner", mealType = "Non-Veg", name = "Fried Fish", description = "Crispy fried fish", image = R.drawable.dall_e_2025_03_04_14_31_58___a_photorealistic_top_down_view_of_a_plate_of_crispy_fried_fish__golden_brown_and_garnished_with_lemon_wedges__fresh_coriander__and_a_side_of_dipping_s),

                // Friday Meals
                Meal(dayOfWeek = "Friday", timeOfDay = "Breakfast", mealType = "Veg", name = "Masala Dosa", description = "Crispy dosa filled with potato", image = R.drawable.dall_e_2025_03_04_14_32_01___a_photorealistic_top_down_view_of_a_masala_dosa__a_crispy_south_indian_crepe_filled_with_spiced_potato_filling__the_dosa_is_golden_brown_and_served_wi, isSelected = true),
                Meal(dayOfWeek = "Friday", timeOfDay = "Breakfast", mealType = "Non-Veg", name = "Chicken Sausage", description = "Grilled chicken sausages", image = R.drawable.dall_e_2025_03_04_14_50_10___a_photorealistic_top_down_view_of_chicken_sausage__grilled_to_a_golden_brown_perfection_with_visible_grill_marks__served_on_a_wooden_plate_with_mustar),

                Meal(dayOfWeek = "Friday", timeOfDay = "Lunch", mealType = "Veg", name = "Rajma Chawal", description = "North Indian comfort food", image = R.drawable.dall_e_2025_03_04_14_32_03___a_photorealistic_top_down_view_of_rajma_chawal__a_north_indian_dish_consisting_of_red_kidney_bean_curry_served_over_steamed_basmati_rice__the_dish_is_, isSelected = true),
                Meal(dayOfWeek = "Friday", timeOfDay = "Lunch", mealType = "Non-Veg", name = "Biryani", description = "Rich and flavorful rice dish", image = R.drawable.dall_e_2025_03_04_14_32_05___a_photorealistic_top_down_view_of_a_bowl_of_biryani__an_aromatic_indian_rice_dish_with_layers_of_saffron_infused_basmati_rice__marinated_spiced_chicke),

                Meal(dayOfWeek = "Friday", timeOfDay = "Dinner", mealType = "Veg", name = "Palak Paneer", description = "Spinach curry with paneer", image = R.drawable.dall_e_2025_03_04_14_32_07___a_photorealistic_top_down_view_of_palak_paneer__an_indian_dish_made_of_soft_paneer_cubes_in_a_creamy_spinach_gravy__the_dish_is_garnished_with_a_drizz, isSelected = true),
                Meal(dayOfWeek = "Friday", timeOfDay = "Dinner", mealType = "Non-Veg", name = "Tandoori Chicken", description = "Spicy roasted chicken", image = R.drawable.dall_e_2025_03_04_14_32_59___a_photorealistic_top_down_view_of_tandoori_chicken__an_indian_dish_featuring_grilled_chicken_marinated_in_yogurt_and_spices__with_a_deep_red_color__th),

                // Saturday Meals
                Meal(dayOfWeek = "Saturday", timeOfDay = "Breakfast", mealType = "Veg", name = "Besan Chilla", description = "Gram flour pancake", image = R.drawable.dall_e_2025_03_04_14_33_01___a_photorealistic_top_down_view_of_besan_chilla__an_indian_savory_gram_flour_pancake_with_finely_chopped_onions__tomatoes__and_green_chilies__the_chill, isSelected = true),
                Meal(dayOfWeek = "Saturday", timeOfDay = "Breakfast", mealType = "Non-Veg", name = "Scrambled Eggs", description = "Light and fluffy scrambled eggs", image = R.drawable.dall_e_2025_03_04_14_33_03___a_photorealistic_top_down_view_of_scrambled_eggs__soft_and_fluffy_with_a_golden_yellow_hue__garnished_with_freshly_chopped_herbs__served_on_a_ceramic_),

                Meal(dayOfWeek = "Saturday", timeOfDay = "Lunch", mealType = "Veg", name = "Kadhi Pakora", description = "Tangy yogurt curry with fritters", image = R.drawable.dall_e_2025_03_04_14_33_11___a_photorealistic_top_down_view_of_kadhi_pakora__a_traditional_indian_dish_made_of_gram_flour_fritters_in_a_thick__tangy_yogurt_based_curry__the_dish_i, isSelected = true),
                Meal(dayOfWeek = "Saturday", timeOfDay = "Lunch", mealType = "Non-Veg", name = "Goan Fish Curry", description = "Coconut-based fish curry", image = R.drawable.dall_e_2025_03_04_14_33_46___a_photorealistic_top_down_view_of_goan_fish_curry__a_traditional_coastal_indian_dish_made_with_fish_cooked_in_a_tangy__spicy_coconut_based_curry__the_),

                Meal(dayOfWeek = "Saturday", timeOfDay = "Dinner", mealType = "Veg", name = "Baingan Bharta", description = "Roasted eggplant curry", image = R.drawable.dall_e_2025_03_04_14_33_55___a_photorealistic_top_down_view_of_baingan_bharta__a_smoky_indian_dish_made_of_mashed_roasted_eggplant_cooked_with_tomatoes__onions__and_spices__the_di, isSelected = true),
                Meal(dayOfWeek = "Saturday", timeOfDay = "Dinner", mealType = "Non-Veg", name = "Mutton Korma", description = "Rich and creamy mutton curry", image = R.drawable.dall_e_2025_03_04_14_45_15___a_photorealistic_top_down_view_of_mutton_korma__a_rich_and_aromatic_indian_dish_made_with_slow_cooked_mutton_in_a_creamy__spiced_yogurt_based_gravy__t),

                // Sunday Meals
                Meal(dayOfWeek = "Sunday", timeOfDay = "Breakfast", mealType = "Veg", name = "Thepla", description = "Gujarati flatbread", image = R.drawable.dall_e_2025_03_04_14_46_03___a_photorealistic_top_down_view_of_thepla__a_traditional_gujarati_flatbread_made_from_whole_wheat_flour__fenugreek_leaves__and_spices__the_theplas_are_, isSelected = true),
                Meal(dayOfWeek = "Sunday", timeOfDay = "Breakfast", mealType = "Non-Veg", name = "Chicken Nuggets", description = "Crispy fried chicken bites", image = R.drawable.dall_e_2025_03_04_14_46_06___a_photorealistic_top_down_view_of_chicken_nuggets__crispy_and_golden_brown__arranged_on_a_wooden_plate_with_a_side_of_ketchup_and_mayonnaise__the_nugg),

                Meal(dayOfWeek = "Sunday", timeOfDay = "Lunch", mealType = "Veg", name = "Vegetable Biryani", description = "Fragrant rice with vegetables", image = R.drawable.dall_e_2025_03_04_14_46_34___a_photorealistic_top_down_view_of_vegetable_biryani__an_aromatic_indian_rice_dish_made_with_basmati_rice__mixed_vegetables__and_fragrant_spices__the_d, isSelected = true),
                Meal(dayOfWeek = "Sunday", timeOfDay = "Lunch", mealType = "Non-Veg", name = "Chicken Biryani", description = "Fragrant rice with chicken", image = R.drawable.dall_e_2025_03_04_14_46_36___a_photorealistic_top_down_view_of_chicken_biryani__a_flavorful_indian_rice_dish_made_with_basmati_rice__marinated_chicken__and_aromatic_spices__the_di),

                Meal(dayOfWeek = "Sunday", timeOfDay = "Dinner", mealType = "Veg", name = "Paneer Tikka Masala", description = "Grilled paneer in spicy curry", image = R.drawable.dall_e_2025_03_04_14_46_38___a_photorealistic_top_down_view_of_paneer_tikka_masala__a_rich_and_creamy_indian_dish_featuring_grilled_paneer_cubes_in_a_spiced_tomato_based_gravy__th, isSelected = true),
                Meal(dayOfWeek = "Sunday", timeOfDay = "Dinner", mealType = "Non-Veg", name = "Prawn Curry", description = "Delicious prawn curry", image = R.drawable.dall_e_2025_03_04_14_46_40___a_photorealistic_top_down_view_of_prawn_curry__a_flavorful_seafood_dish_featuring_prawns_cooked_in_a_rich__spiced_coconut_based_gravy__the_dish_is_gar)
            )

            if (newMeals.isNotEmpty()) {
                mealDao.insertMeals(newMeals)
            }
        }
    }








}
