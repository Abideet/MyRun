package uk.aston.placestest.Database;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import uk.aston.placestest.R;

//caches data and populates the RecyclerView with it
public class JourneyListAdapter extends RecyclerView.Adapter<JourneyListAdapter.JourneyViewHolder> implements Filterable
{
    private final LayoutInflater mInflater;

    // Cached copy of journeys that will be filtered
    private List<Journey> mJourneysList;

    // Copy of unfiltered list
    private List<Journey> mJourneysListFull;

    private static ClickListener clickListener;

    public JourneyListAdapter(Context context)
    {
        mInflater = LayoutInflater.from(context);
    }
    
    public JourneyListAdapter(List<Journey> mJourneys, Context context)
    {
        mInflater = LayoutInflater.from(context);

        this.mJourneysList = mJourneys;

        mJourneysListFull = new ArrayList<>(mJourneys);
    }


    @Override
    public JourneyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        //change the layout later to one that better displays the journey
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new JourneyViewHolder(itemView);
    }

    public void setOnItemClickListener(ClickListener clickListener)
    {
        JourneyListAdapter.clickListener = clickListener;
    }

    public interface ClickListener
    {
        void onItemClick(View v, int position);
    }

    @Override
    public void onBindViewHolder(@NonNull JourneyViewHolder holder, int position)
    {
        if (mJourneysList != null)
        {
            Journey current = mJourneysList.get(position);

            //thought the parameter for setText was a reference to the xml e.g., findViewById(R.id.var)
            //without the "", setText gets a reference as Tony said
            //NOTE: Maybe thats why the recycler view value is 60 and += 4 everytime
            //69
            //73
            //74
            //went up to 76
            //maybe its storing the different runs and this is the list of the different runs

            holder.journeyIDItemView.setText(""+current.getJourneyID());

            double distance = truncate(current.getMdistance());

            holder.journeyDistanceItemView.setText(""+distance+" KM");

            holder.journeyDurationItemView.setText(""+current.getMduration()+ " S");

            //sets the initially populated speed first
            holder.journeySpeedItemView.setText(""+current.getMSpeed()+ " KM/H");

            holder.journeyNameItemView.setText(current.getmName());

        } else
            {
            // Covers the case of data not being ready yet.
            holder.journeyIDItemView.setText("No Journey");
        }

    }

    //Method to shorten distance to 2 decimal places
    static double truncate(double distance)
    {

        distance = distance * Math.pow(10, 2);
        distance = Math.floor(distance);
        distance = distance / Math.pow(10, 2);

        return distance;
    }



    public void setmJourneysList(List<Journey> journeys)
    {
        mJourneysList = journeys;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount()
    {
        if (mJourneysList != null)
            return mJourneysList.size();
        else return 0;
    }

    public Journey getJourneyAtPosition (int position)
    {
        return mJourneysList.get(position);
    }


    class JourneyViewHolder extends RecyclerView.ViewHolder
    {
        private final TableLayout journeyItem;
        private final TextView journeyIDItemView;
        private final TextView journeyNameItemView;
        private final TextView journeyDistanceItemView;
        private final TextView journeySpeedItemView;
        private final TextView journeyDurationItemView;


        private JourneyViewHolder(View itemView)
        {
            super(itemView);
            //wordItemView = itemView.findViewById(R.id.textView);
            journeyItem = itemView.findViewById(R.id.textView);
            journeyIDItemView = itemView.findViewById(R.id.textViewID);
            journeyNameItemView = itemView.findViewById(R.id.textViewName);
            journeyDistanceItemView = itemView.findViewById(R.id.textViewDistance);
            journeySpeedItemView = itemView.findViewById(R.id.textViewSpeed);
            journeyDurationItemView = itemView.findViewById(R.id.textViewTime);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.d("clicked", "itemClicked");
                    clickListener.onItemClick(v, getAdapterPosition());
                }
            });



            Log.i("ADAPTER", journeyIDItemView.toString());
        }
    }

    public void filterList(ArrayList<Journey> filteredList)
    {
        mJourneysList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return journeyFilter;
    }

    private Filter journeyFilter = new Filter()
    {
        @Override
        protected FilterResults performFiltering(CharSequence searchFilter)
        {

            List<Journey> filteredJourneyList = new ArrayList<>();

            //if the input field is empty
            if (searchFilter == null || searchFilter.length() == 0)
            {
                //Show all journeys in the list
                filteredJourneyList.addAll(mJourneysListFull);
            } else
                {
                //format filter so it is not case sensitive and there are no spaces
                String stringFilter = searchFilter.toString().toLowerCase().trim();

                //Loop through each journey in journey list
                for (Journey journey : mJourneysListFull) {
                    //TODO: Change to journey name and allow user to set that
                    if (journey.getmName().toLowerCase().contains(stringFilter)) {
                        filteredJourneyList.add(journey);
                    }
                }
            }


            FilterResults results = new FilterResults();
            results.values = filteredJourneyList;

            //filtered array list is now passed as results to publishResults method
            return results;
        };

        //Publishes results to the UI thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            //Remove all items in this list because we only want items from our filtered list in it
            mJourneysList.clear();
            mJourneysList.addAll((List) results.values);

            //Tells adapter it has to refresh its list
            notifyDataSetChanged();

        }
    };


}
