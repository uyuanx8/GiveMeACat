package com.example.givemeacat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class ListDialog extends BottomSheetDialogFragment{

    ArrayList<SavedData> dataSet = new ArrayList<>();
    DialogResult dialogResult;

    public ListDialog(DialogResult result){
        this.dialogResult = result;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_savedlist,container,false);
        RecyclerView list = view.findViewById(R.id.saved_recycler);

        SharedPreferences sp = getContext().getSharedPreferences("save", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Map<String, ?> dataMap = sp.getAll();
        for(String str : dataMap.keySet()){
            String jsonData = sp.getString(str, "");;
            if(jsonData != ""){
//                Log.d("meow", jsonData);
                SavedData document = gson.fromJson(jsonData, SavedData.class);
                document.setImgLink(str);
                dataSet.add(document);
            }
        }
        Log.d("meow", "list w/ size " + dataSet.size());
        dataSet.sort(new Comparator<SavedData>() {
                         @Override
                         public int compare(SavedData t0, SavedData t1) {
                             return t0.getSavedDate().compareTo(t1.getSavedDate());
                         }
                     });
        RecyclerAdapter adapter = new RecyclerAdapter(dataSet);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();
        return view;
    }
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

        ArrayList<SavedData> dataList;
        public RecyclerAdapter(ArrayList<SavedData> dataList){
            this.dataList = dataList;
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView text;
            LinearLayout layout;
            public ViewHolder(View view) {
                super(view);
                layout = view.findViewById(R.id.item_layout);
                text = view.findViewById(R.id.item_date);
            }
        }


        public void setData(ArrayList<SavedData> arr){
            this.dataList = arr;
            this.notifyDataSetChanged();
        }

        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_saved, viewGroup, false);
            return new RecyclerAdapter.ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(RecyclerAdapter.ViewHolder viewHolder, final int position) {
            viewHolder.text.setText(dataList.get(position).getSavedDate());

            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogResult.finish(dataList.get(position));
                    getDialog().dismiss();
                }
            });
        }
        @Override
        public int getItemCount() {
            return dataList.size();
        }

    }
    public interface DialogResult{
        void finish(SavedData s);
    }
}
