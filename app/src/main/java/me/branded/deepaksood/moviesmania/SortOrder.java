package me.branded.deepaksood.moviesmania;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import java.util.List;

/**
 * Created by deepak on 17/2/16.
 */
public class SortOrder extends DialogFragment{
    public static final String DATA = "items";
    public static final String SELECTED = "selected";
    private SelectionListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.listener = (SelectionListener)activity;
        }
        catch ( ClassCastException oops ) {
            oops.printStackTrace();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Please Select the Movie Order");
        dialog.setPositiveButton("Cancel", new PositiveButtonClickListener());
        List<String> list = (List<String>)bundle.get(DATA);
        int position = bundle.getInt(SELECTED);
        CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
        dialog.setSingleChoiceItems(cs, position, selectItemListener);
        return dialog.create();
    }

    class PositiveButtonClickListener implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    DialogInterface.OnClickListener selectItemListener = new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            if ( listener != null )
            {
                listener.selectItem(which);
            }
            dialog.dismiss();
        }

    };

    public interface SelectionListener
    {
        void selectItem ( int position );
    }
}
