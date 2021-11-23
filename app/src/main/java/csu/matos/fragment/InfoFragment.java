package csu.matos.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.photoapp.InfomationActivity;
import com.example.photoapp.R;

public class InfoFragment extends Fragment {
    InfomationActivity activity;
    //private String textVisible;

//    FragmentInfo(String textVisible) {
//        this.textVisible = textVisible;
//    }

    public static InfoFragment newInstance(int integerArgs) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putInt("arg1", integerArgs);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            activity = (InfomationActivity) getActivity();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Main activity must implement callbacks");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout frag_layout = (RelativeLayout) inflater.inflate(R.layout.fragment_info, null);
        TextView txtView = (TextView) frag_layout.findViewById(R.id.txtView);

        Bundle argument = getArguments();
        txtView.setText(argument.getInt("arg1"));

        return frag_layout;
    }
}
