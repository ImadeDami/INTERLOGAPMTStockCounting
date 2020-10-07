package com.interlog.interlogapmtstockcounting.editor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.interlog.interlogapmtstockcounting.R;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.RecyclerViewAdapter> {

    private Context context;
    private List<Note> notes;
    private ItemClickListener itemClickListener;


    public MainAdapter(Context context, List<Note> notes, ItemClickListener itemClickListener) {
        this.context = context;
        this.notes = notes;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note,
                parent, false);
        return new RecyclerViewAdapter(view, itemClickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter holder, int position) {
        Note note = notes.get(position);
        holder.tv_itemnm.setText(note.getItemName());
        holder.tv_quantt.setText(note.getQuantity());
        holder.tv_raclo.setText(note.getRackLocation());
        //holder.card_item.setCardBackgroundColor(note.getColor());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class RecyclerViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_itemnm, tv_quantt, tv_raclo;
        CardView card_item;
        ItemClickListener itemClickListener;

        RecyclerViewAdapter(View itemView, ItemClickListener itemClickListener) {
            super(itemView);

           /** TextView textViewId = itemView.findViewById(R.id.textViewId);
            TextView textViewUsername = itemView.findViewById(R.id.textViewUsername);
            TextView textViewEmail = itemView.findViewById(R.id.textViewEmail);
            TextView textViewGender =  itemView.findViewById(R.id.textViewGender);
            //getting the current user
            User user = SharedPrefManager.getInstance(MainAdapter.RecyclerViewAdapter, context).getUser();

            //setting the values to the textviews
            textViewId.setText(String.valueOf(user.getId()));
            textViewUsername.setText(user.getUsername());
            textViewEmail.setText(user.getEmail());
            textViewGender.setText(user.getGender()); **/

            tv_itemnm = itemView.findViewById(R.id.itemNm);
            tv_quantt = itemView.findViewById(R.id.quantt);
            tv_raclo = itemView.findViewById(R.id.racLoc);
            card_item = itemView.findViewById(R.id.card_item);

            this.itemClickListener = itemClickListener;
            card_item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}