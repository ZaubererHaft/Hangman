package teamfmg.hangman;

import android.app.Activity;
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
    private List<String> availableCategories;

    /**
     * Is the checkbox checked.
     */
    private boolean checked;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_category);

        this.changeBackground();

        //Init
        CheckBox checkBox_all           = (CheckBox)    this.findViewById(R.id.checkBox_cat_all);
        ScrollView scrollView           = (ScrollView)  this.findViewById(R.id.scrollView_cats);
        Button close                    = (Button)      this.findViewById(R.id.category_close);
        close.setOnClickListener(this);

        LinearLayout linearLayout   = new           LinearLayout(this);
        this.checkBoxes             = new           ArrayList<>();
        DatabaseManager db          = new           DatabaseManager(this);
        this.availableCategories    = new           ArrayList<>();

        //Adding OnClickListener
        checkBox_all.setOnClickListener(this);
        this.checked = checkBox_all.isChecked();

        //Additional GUI Settings
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout);


        //read DISTINCT Categories from DataBase
        this.availableCategories =  db.getCategories();

        int cats = 0;

        //Add one Checkbox for each Button (+ GUI Settings)
        for (int i = 0; i < this.availableCategories.size(); i++)
        {
            CheckBox c = new CheckBox(this);
            c.setId(this.viewsCount);
            this.viewsCount++;
            linearLayout.addView(c);
            c.setText(convertCategoryName(this.availableCategories.get(i)));
            c.setTextColor(Color.WHITE);
            c.setOnClickListener(this);
            this.checkBoxes.add(c);

            if (Settings.getCategories().contains(this.availableCategories.get(i)))
            {
                c.setChecked(true);
                cats += 1;
            }
        }

        //if no category was found, select them all
        if(cats == 0)
        {
            this.selectAllBoxes(true);
        }

        Collections.sort(this.availableCategories);

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
                chosenCategories.add(this.availableCategories.get(i));
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
    public String convertCategoryName(String nameInDatabase)
    {
        String newName;
        switch (nameInDatabase)
        {
            case "capitals":
                newName = this.getResources().getString(R.string.categoryName_capitals);
                break;
            case "countries":
                newName = this.getResources().getString(R.string.categoryName_countries);
                break;
            case "gaming":
                newName = this.getResources().getString(R.string.categoryName_gaming);
                break;
            case "instruments":
                newName = this.getResources().getString(R.string.categoryName_instruments);
                break;
            case "car":
                newName = this.getResources().getString(R.string.categoryName_cars);
                break;
            case "river":
                newName = this.getResources().getString(R.string.categoryName_rivers);
                break;
            case "tree":
                newName = this.getResources().getString(R.string.categoryName_trees);
                break;
            case "animal":
                newName = this.getResources().getString(R.string.categoryName_animals);
                break;
            case "test":
                newName = this.getResources().getString(R.string.categoryName_test);
                break;
            case "ownWord":
                newName = this.getResources().getString(R.string.categoryName_ownWord);
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
