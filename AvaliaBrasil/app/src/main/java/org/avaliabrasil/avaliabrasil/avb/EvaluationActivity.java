package org.avaliabrasil.avaliabrasil.avb;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.CommentFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.LikertFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.NumberFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.TransactionFragment;
import org.avaliabrasil.avaliabrasil.rest.javabeans.Anwser;
import org.avaliabrasil.avaliabrasil.rest.javabeans.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EvaluationActivity extends AppCompatActivity implements View.OnClickListener{
    public final String LOG_TAG = this.getClass().getSimpleName();

    /**
     *
     */
    private Button btnSubmit;

    /**
     *
     */
    private Toolbar toolbar;

    /**
     *
     */
    private TransactionFragment newFragment;

    /**
     *
     */
    private FragmentTransaction ft;

    /**
     *
     */
    private List<Question> questions = new ArrayList<Question>();

    /**
     *
     */
    private List<Anwser> anwsers = new ArrayList<Anwser>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        if(getIntent().getExtras().getString("name") == null){
            finish();
        }

        //questions = (List<Question>) i.getSerializableExtra("questions");

        // Definindo a Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //TODO: Alterar com o título dos dados reais
        getSupportActionBar().setTitle(getIntent().getExtras().getString("name"));

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        if (savedInstanceState == null) {
            newFragment = new LikertFragment();

            ft = getFragmentManager().beginTransaction();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, newFragment).commit();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.btnSubmit:

                if(newFragment.isAnwser()){

                   if(newFragment instanceof LikertFragment){
                       anwsers.add(new Anwser("0",newFragment.getAnwser(),null,null));
                    } else if(newFragment instanceof CommentFragment){
                       anwsers.add(new Anwser("0",null,newFragment.getAnwser(),null));
                    } else if(newFragment instanceof NumberFragment){
                       anwsers.add(new Anwser("0",null,null,newFragment.getAnwser()));
                    }

                    Random rand = new Random();

                    int randint = rand.nextInt(3);

                    switch(randint){

                        case 0:
                            newFragment = new LikertFragment();
                            break;
                        case 1:
                            newFragment = new CommentFragment();
                            break;
                        default:
                            newFragment = new NumberFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, newFragment).commit();
                }else{
                    Toast.makeText(EvaluationActivity.this,"Você não respondeu a questão",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
