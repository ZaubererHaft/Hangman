package teamfmg.hangman;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;


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
     * List of avaible Categorys in DataBase
     */
    private List<String> avaibleCategorys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_category);

        //Init
        checkBox_all                = (CheckBox)    findViewById(R.id.checkBox_cat_all);
        ScrollView scrollView       = (ScrollView)  findViewById(R.id.scrollView_cats);
        LinearLayout linearLayout   = new           LinearLayout(this);
        checkBoxes                  = new           ArrayList<>();
        DatabaseManager db          = new           DatabaseManager(this);
        avaibleCategorys            = new           ArrayList<>();

        //Adding OnClickListener
        checkBox_all.setOnClickListener(this);

        //Additional GUI Settings
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout);
        this.changeBackground();

        //read DISTINCT Categorys from DataBase
        avaibleCategorys =  db.getCategorys();

        //Add one Checkbox for each Button (+ GUI Settings)
        for (int i = 0; i < avaibleCategorys.size(); i++)
        {
            CheckBox c = new CheckBox(this);
            c.setId(++viewsCount);
            linearLayout.addView(c);
            c.setText(convertCategoryName(avaibleCategorys.get(i)));
            c.setTextColor(Color.WHITE);
            c.setOnClickListener(this);
            checkBoxes.add(c);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        List<String> choosenCategorys = new ArrayList<>();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isChecked()){
                choosenCategorys.add(avaibleCategorys.get(i));
            }
        }
        if (choosenCategorys.size() > 0){
            Singleplayer s = new Singleplayer();
            s.setCategorys(choosenCategorys);
            Logger.write("Categorys saved!", this);
        }
        else{
            Logger.write("Categorys NOT saved!", this);
        }
    }

    /**
     * Converting the tag in the Database to an Correct Description
     * @param nameInDatabase attribute in column category
     * @return a nice looking Description (String)
     */
    public String convertCategoryName(String nameInDatabase){
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
    public void changeBackground() {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_categorys);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            //All buttons set checked/unchecked
            case R.id.checkBox_cat_all:
                try{
                    if (checkBox_all.isChecked()){
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
                catch (Exception ex){
                    Logger.logOnlyError(ex.getMessage());
                }
                break;
            default:
                Logger.write("Currently no function", this);
        }
    }
}
