package teamfmg.hangman;

import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Singleplayer extends Activity implements View.OnClickListener, IApplyableSettings{

    private Button button_a;

    /**
     * the current shown picture of Hangman
     */
    private int currentBuildOfHangman;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer);
        this.changeBackground();
        Button button_a         =       (Button) findViewById(R.id.button_A);
        button_a.setOnClickListener(this);

        this.resetHangman();
    }

    /**
     * Builds the next Part of the Hangman
     */
    private void buildHangman(){
        if(currentBuildOfHangman <= 12){
            currentBuildOfHangman++;
            ImageView iv = (ImageView) findViewById(R.id.image_hangman);
            int id = this.getResources().getIdentifier("hm_"+currentBuildOfHangman, "drawable", this.getPackageName());
            iv.setImageResource(id);
        }
    }

    /**
     * resets the Hangman. No Part is build
     */
    private void resetHangman(){
        ImageView iv = (ImageView) findViewById(R.id.image_hangman);
        iv.setImageResource(R.drawable.hm_0);
        currentBuildOfHangman = 0;
    }

    @Override
    public void changeBackground() {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_singleplayer);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v) {
        this.buildHangman();
    }
}
