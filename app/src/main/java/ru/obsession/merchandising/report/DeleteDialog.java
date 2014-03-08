package ru.obsession.merchandising.report;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.main.MainActivity;

public class DeleteDialog extends DialogFragment {
    public final static String QUANTITY_TEXT = "quantity_text";
    public final static String NUM_DELETE = "num_delete";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        int numDelete = bundle.getInt(NUM_DELETE);
        String quantityText = bundle.getString(QUANTITY_TEXT);
        AlertDialog.Builder builder;
        String plural = getResources().getQuantityString(R.plurals.num_for_deleting, numDelete, numDelete);
        builder = new AlertDialog.Builder(getActivity());
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PhotoReportFragment fragment = (PhotoReportFragment) getFragmentManager().findFragmentByTag(MainActivity.REPORT_FRAGMENT);
                fragment.delSelected();
            }
        }).setTitle(R.string.confirmation);
        if (quantityText != null) {
            builder.setMessage(quantityText);
        } else {
            builder.setMessage(plural);
        }
        return builder.create();
    }
}
