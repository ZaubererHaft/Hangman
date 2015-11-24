package teamfmg.hangman;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TableLayout;


public class Category extends Activity implements IApplyableSettings{

    private CheckBox checkBox_all;
    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private static int viewsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_category);

        checkBox_all = (CheckBox)findViewById(R.id.checkBox_cat_all);
        linearLayout = (LinearLayout)findViewById(R.id.linearLayout_cats);

        for (int i = 0; i < 5; i++)
        {
            CheckBox c = new CheckBox(this);
            c.setId(++viewsCount);
            this.linearLayout.addView(c);
        }
        this.changeBackground();

    }

    @Override
    public void changeBackground() {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_categorys);
        Settings.setColor(rl);
    }
}
