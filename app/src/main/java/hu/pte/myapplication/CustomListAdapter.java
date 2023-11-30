package hu.pte.myapplication;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomListAdapter extends ArrayAdapter<String> {

    public CustomListAdapter(Context context, int resource, List<String> notesList) {
        super(context, resource, notesList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        TextView textView = view.findViewById(android.R.id.text1);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END); // Végénél "..." helyett

        return view;
    }
}
