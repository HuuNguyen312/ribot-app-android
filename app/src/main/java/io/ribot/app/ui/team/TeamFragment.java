package io.ribot.app.ui.team;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.ribot.app.R;
import io.ribot.app.data.model.Ribot;
import io.ribot.app.ui.adapter.RibotAdapter;
import io.ribot.app.ui.base.BaseActivity;
import io.ribot.app.util.DialogFactory;

public class TeamFragment extends Fragment implements TeamMvpView {

    @Inject
    protected TeamPresenter mTeamPresenter;
    private RibotAdapter mRibotAdapter;

    @Bind(R.id.recycler_view_team)
    RecyclerView mTeamRecycler;

    @Bind(R.id.swipe_refresh_container)
    SwipeRefreshLayout mSwipeRefreshContainer;

    @Bind(R.id.text_no_ribots)
    TextView mNoRibotsText;

    @Bind(R.id.progress)
    ProgressBar mProgress;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_team, container, false);
        ButterKnife.bind(this, fragmentView);
        ((BaseActivity) getActivity()).activityComponent().inject(this);
        mTeamPresenter.attachView(this);
        mRibotAdapter = new RibotAdapter(getActivity());
        mTeamRecycler.setHasFixedSize(true);
        mTeamRecycler.setAdapter(mRibotAdapter);
        mSwipeRefreshContainer.setColorSchemeResources(R.color.primary);
        mSwipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mTeamPresenter.loadRibots();
            }
        });
        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mTeamPresenter.loadRibots();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTeamPresenter.detachView();
    }

    @Override
    public void showRibots(List<Ribot> ribots) {
        mRibotAdapter.setTeamMembers(ribots);
        mRibotAdapter.notifyDataSetChanged();
        mNoRibotsText.setVisibility(View.GONE);
    }

    @Override
    public void showRibotProgress(boolean show) {
        mSwipeRefreshContainer.setRefreshing(show);
        if (show && mRibotAdapter.getItemCount() == 0) {
            mProgress.setVisibility(View.VISIBLE);
        } else {
            mProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void showEmptyMessage() {
        mNoRibotsText.setVisibility(View.VISIBLE);
    }

    @Override
    public void showRibotsError(String errorMessage) {
        DialogFactory.createSimpleOkErrorDialog(getActivity(), errorMessage).show();
    }

    @Override
    public Context getViewContext() {
        return getActivity();
    }
}