package andrade.mateus.mytracking.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import andrade.mateus.mytracking.R;
import andrade.mateus.mytracking.db.dao.JourneyDAO;
import andrade.mateus.mytracking.db.entity.JourneyEntity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mateusandrade on 22/01/2018.
 */

public class JourneyListActivity extends AppCompatActivity {

    public List<JourneyEntity> journeys;
    private JourneyDAO journeyDAO;
    @BindView(R.id.journey_list) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journey_list);
        ButterKnife.bind(this);

        journeyDAO = new JourneyDAO(getApplicationContext());
        journeys = journeyDAO.listAll();

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, journeys, true));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final JourneyListActivity mParentActivity;
        private final List<JourneyEntity> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };

        SimpleItemRecyclerViewAdapter(JourneyListActivity parent,
                                      List<JourneyEntity> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            try {

                String pattern = "dd/MM/yyyy - HH:mm:ss";
                SimpleDateFormat format = new SimpleDateFormat(pattern);
                holder.journey_name.setText(String.valueOf(mValues.get(position).getId()));
                holder.journey_start.setText("Start: "+format.format(mValues.get(position).getStartTime()));
                holder.journey_end.setText("End: "+format.format(mValues.get(position).getEndTime()));

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView journey_name;
            TextView journey_start;
            TextView journey_end;

            ViewHolder(View view) {
                super(view);

                journey_name = view.findViewById(R.id.journey_name);
                journey_start = view.findViewById(R.id.journey_start);
                journey_end = view.findViewById(R.id.journey_end);
            }
        }
    }
}
