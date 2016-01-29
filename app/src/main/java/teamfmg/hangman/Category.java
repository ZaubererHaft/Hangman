package teamfmg.hangman;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Menu to choose the categories.
 * Created by Vincent on 23.11.2015.
 * @since 0.6
 */
public class Category extends Activity implements IApplyableSettings, View.OnClickListener
{

    /**
     * Amount of created Checkboxes
     */
    private int viewsCount = 0;
    /**
     * List of all Checkboxes (without the "ALL"-CheckBox)
     */
    private List<CheckBox> checkBoxes;

    /**
     * List of available Categories in DataBase
     */
    private HashMap<String, String> availableCategories;
    /**
     * The displayed categories.
     */
    private ArrayList<String> displayedCategories = new ArrayList<>();
    /**
     * Is the checkbox checked.
     */
    private boolean checked;
    /**
     * This handles the database connection.
     */
    private DatabaseManager db;
    /**
     * The checkbox to select all elements.
     */
    private CheckBox checkBox_all;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_category);

        this.changeBackground();

        //Init
        checkBox_all           = (CheckBox)    this.findViewById(R.id.checkBox_cat_all);
        ScrollView scrollView           = (ScrollView)  this.findViewById(R.id.scrollView_cats);
        Button close                    = (Button)      this.findViewById(R.id.category_close);
        close.setOnClickListener(this);

        LinearLayout linearLayout   = new           LinearLayout(this);
        this.checkBoxes             = new           ArrayList<>();

        this.availableCategories    = new           HashMap<>(100);
        db = DatabaseManager.getInstance();

        //Adding OnClickListener
        checkBox_all.setOnClickListener(this);

        //Additional GUI Settings
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout);

        //read DISTINCT Categories from DataBase
        this.availableCategories =  this.readCategories();

        int cats = 0;

        //Add one Checkbox for each Button (+ GUI Settings)
        for (int i = 0; i < this.availableCategories.size(); i++)
        {
            CheckBox c = new CheckBox(this);
            c.setId(this.viewsCount);
            this.viewsCount++;
            linearLayout.addView(c);
            c.setText(this.displayedCategories.get(i));
            c.setTextColor(Color.WHITE);
            c.setOnClickListener(this);
            this.checkBoxes.add(c);

            if (Settings.getCategories().contains
            (
                this.availableCategories.get(this.displayedCategories.get(i)))
            )
            {
                c.setChecked(true);
                cats += 1;
            }
        }

        //if no category was found, select them all
        if(cats == 0 || cats == this.availableCategories.size())
        {
            this.selectAllBoxes(true);
            this.checked = true;
        }

    }

    /**
     * This reads the categories out of the databases and adds a name which is displayed in the
     * settings.
     * @return A hashmap with the displayed name as key and the database name as value
     */
    private HashMap<String,String> readCategories()
    {
        ArrayList<String> dbNames = this.db.getCategories();
        HashMap<String,String> hm = new HashMap<>();

        //for all categories
        for (int i = 0; i < dbNames.size(); i++)
        {
            //get the displayed name and add it
            String newString = convertCategoryName(dbNames.get(i),this.getResources());
            hm.put(newString,dbNames.get(i));
            this.displayedCategories.add(newString);
        }

        //sorting array
        this.displayedCategories = formatCategories(this.displayedCategories);
        return hm;
    }

    /**
     * This formats the available categories.<br />
     * Changes the visible name in a good format and sorts the list.
     * @since 0.8
     */
    private ArrayList<String>  formatCategories(ArrayList<String> newList)
    {
        Collections.sort(newList);
        return newList;

    }


    @Override
    protected void onPause()
    {
        super.onPause();

        //Save categories automatically if the user leaves the window
        ArrayList<String> chosenCategories = new ArrayList<>();

        for (int i = 0; i < this.checkBoxes.size(); i++)
        {
            if (this.checkBoxes.get(i).isChecked())
            {
                String key = this.checkBoxes.get(i).getText().toString();
                chosenCategories.add(this.availableCategories.get(key));
            }
        }


        if (chosenCategories.size() > 0)
        {
            Settings.setCategories(chosenCategories);
            Settings.save(this);
        }
        //if no checkbox is selected, do not save
        else
        {
            Logger.write(this.getResources().getString(R.string.category_hint_not_saved), this);
        }
    }

    /**
     * Converting the tag in the Database to an Correct Description
     * @param nameInDatabase attribute in column category
     * @return a nice looking Description (String)
     * @since 0.6
     */
    public static String convertCategoryName(String nameInDatabase, Resources res)
    {
        String newName;
        switch (nameInDatabase)
        {
            case "capitals":
                newName = res.getString(R.string.categoryName_capitals);
                break;
            case "countries":
                newName = res.getString(R.string.categoryName_countries);
                break;
            case "gaming":
                newName = res.getString(R.string.categoryName_gaming);
                break;
            case "instruments":
                newName = res.getString(R.string.categoryName_instruments);
                break;
            case "car":
                newName = res.getString(R.string.categoryName_cars);
                break;
            case "river":
                newName = res.getString(R.string.categoryName_rivers);
                break;
            case "tree":
                newName = res.getString(R.string.categoryName_trees);
                break;
            case "animal":
                newName = res.getString(R.string.categoryName_animals);
                break;
            case "test":
                newName = res.getString(R.string.categoryName_test);
                break;
            case "ownWord":
                newName = res.getString(R.string.categoryName_ownWord);
                break;
            default:
                newName = nameInDatabase;
                break;
        }
        return newName;
    }

    @Override
    public void changeBackground()
    {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_categorys);
        Settings.setColor(rl);
    }

    private void selectAllBoxes(Boolean b)
    {
        for (int i = 0; i < this.viewsCount; i++)
        {
            this.checkBoxes.get(i).setChecked(b);
        }

        this.checkBox_all.setChecked(b);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            //All buttons set checked/unchecked
            case R.id.checkBox_cat_all:

                this.checked = !this.checked;
                this.selectAllBoxes(checked);
            break;

            case R.id.category_close:
                this.finish();
                break;
        }
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        System.gc();
    }
}
