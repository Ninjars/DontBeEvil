package com.projects.jez.dontbeevil.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projects.jez.dontbeevil.R;
import com.projects.jez.dontbeevil.engine.LoopTaskManager;
import com.projects.jez.dontbeevil.managers.Environment;
import com.projects.jez.dontbeevil.managers.GameManager;
import com.projects.jez.dontbeevil.state.GameState;
import com.projects.jez.dontbeevil.ui.IncrementerComparator;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private Handler mHandler;
    private Environment mEnvironment;
    private IncrementerRecyclerViewAdapter adapter;

    public MainActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mEnvironment = Environment.getInstance(getContext().getApplicationContext());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        View view = getView();
        View playButton = view.findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameManager gameManager = mEnvironment.getGameManager();
                boolean appliedChange = gameManager.getPlaysIncrementer().modifyValue(1);
                // TODO: perform action if change didn't apply
            }
        });
        GameManager gameManager = mEnvironment.getGameManager();
        mHandler = new Handler(Looper.getMainLooper());
        bindReadouts(view, gameManager, mEnvironment.getTaskManager());
    }

    @Override
    public void onPause() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onPause();
    }

    private void bindReadouts(View view, GameManager gameManager, final LoopTaskManager taskManager) {
        GameState gameState = gameManager.getGameState();

        adapter = new IncrementerRecyclerViewAdapter(taskManager, gameState.getReadouts(), new IncrementerComparator());

        RecyclerView listView = (RecyclerView) view.findViewById(R.id.readout_list);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(adapter);
    }
}
