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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent on 23.11.2015.
 * @since 0.6
 */
public class Category extends Activity implements IApplyableSettings, View.OnClickListener{

    /**
     * GUI objects.
     */
    private CheckBox checkBox_all;

    /**
     * Amount of created Checkboxes
     */
    private static int viewsCount = 0;

    /**
     * List of all Checkboxes (without the "ALL"-CheckBox)
     */
    private List<CheckBox> checkBoxes;

    /**
     * List of available Categorys in DataBase
     */
    private List<String> avaibleCategorys;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_category);

        //Init
        this.checkBox_all           = (CheckBox)    this.findViewById(R.id.checkBox_cat_all);
        ScrollView scrollView       = (ScrollView)  this.findViewById(R.id.scrollView_cats);

        Button close                = (Button)      this.findViewById(R.id.category_close);
        close.setOnClickListener(this);

        LinearLayout linearLayout   = new           LinearLayout(this);
        this.checkBoxes             = new           ArrayList<>();
        DatabaseManager db          = new           DatabaseManager(this);
        this.avaibleCategorys       = new           ArrayList<>();

        //Adding OnClickListener
        this.checkBox_all.setOnClickListener(this);

        //Additional GUI Settings
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout);
        this.changeBackground();

        //read DISTINCT Categories from DataBase
        this.avaibleCategorys =  db.getCategories();

        //Add one Checkbox for each Button (+ GUI Settings)
        for (int i = 0; i < this.avaibleCategorys.size(); i++)
        {
            CheckBox c = new CheckBox(this);
            c.setId(++viewsCount);
            linearLayout.addView(c);
            c.setText(convertCategoryName(this.avaibleCategorys.get(i)));
            c.setTextColor(Color.WHITE);
            c.setOnClickListener(this);
            this.checkBoxes.add(c);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        //Save categories automatically if the user leaves the window
        List<String> choosenCategorys = new ArrayList<>();

        for (int i = 0; i < checkBoxes.size(); i++)
        {
            if (checkBoxes.get(i).isChecked()){
                choosenCategorys.add(avaibleCategorys.get(i));
            }
        }


        if (choosenCategorys.size() > 0)
        {
            Singleplayer.setCategories(choosenCategorys);
            Logger.write("Categorys saved!", this);
        }
        //if no checkbox is selected, do not save
        else
        {
            Logger.write("Categorys NOT saved!", this);
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
        switch (nameInDatabase){
            case "capitals":
                newName = this.getResources().getString(R.string.categoryName_capitals);
                break;
            case "countries":
                newName = this.getResources().getString(R.string.categoryName_countries);
                break;
            case "test":
                newName = this.getResources().getString(R.string.categoryName_test);
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            //All buttons set checked/unchecked
            case R.id.checkBox_cat_all:
                try
                {
                    if (checkBox_all.isChecked())
                    {
                        for (int i = 0; i < viewsCount; i++){
                            checkBoxes.get(i).setChecked(true);
                        }
                    }
                    else {
                        for (int i = 0; i < viewsCount; i++){
                            checkBoxes.get(i).setChecked(false);
                        }
                    }
                }
                catch (Exception ex)
                {
                    Logger.logOnlyError(ex.getMessage());
                }
                break;

            case R.id.category_close:
                this.finish();
                break;

            default:
                Logger.write("Currently no function", this);
        }
    }
}
