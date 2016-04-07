package org.avaliabrasil.avaliabrasil.avb.fragments.evaluate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Button;

import org.avaliabrasil.avaliabrasil.rest.javabeans.Question;

/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 * @version 1.0
 * @since 1.0
 */
public abstract class TransactionFragment extends Fragment {

    protected Question question;

    protected Button btnSubmit;

    public abstract boolean isAnwser();

    public abstract Object getAnwser();

    public Question getQuestion() {
        return question;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            question = (Question) getArguments().getSerializable("question");
        }
    }
}
