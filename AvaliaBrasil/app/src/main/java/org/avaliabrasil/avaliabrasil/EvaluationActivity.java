package org.avaliabrasil.avaliabrasil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

public class EvaluationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        // Definindo a Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //TODO: Alterar com o título dos dados reais
        getSupportActionBar().setTitle("UPA Zona Norte");
        getAnswers();
    }

    private int getNumberOfQuestions () {
        return 100;
    }

    private String getQuestion (int questionNumber) {
        String question = "As instalações internas e externas da Instituições são visualmente atrativas.";
        return question;
    }

    private void getAnswers () {
        // Definindo os elementos da Activity
        // As respostas seriam mais ou menos carregadas assim. Porém é necessário fazer direito com fragments
        final TextView question_textview = (TextView) findViewById(R.id.question);
        final Button submit_button = (Button) findViewById(R.id.submit_answer);

        int numberOfQuestions = getNumberOfQuestions();
        int questionNumber = 1;
        String question;
        String buttonText;

        for (int i = 1; i <= numberOfQuestions; i++) {
            questionNumber = i;
            question = getQuestion(questionNumber);
            question = questionNumber + ". " + question;
            buttonText = getString(R.string.submit_answer) + "(" + questionNumber + "/" + numberOfQuestions + ")";
            question_textview.setText(question);
            submit_button.setText(buttonText);
        }
    }

}
