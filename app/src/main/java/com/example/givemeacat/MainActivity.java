package com.example.givemeacat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {

    ImageView catImage;
    String imgId = "";
    String tagStr = "";

    String fontColor = "#000000";

    SharedPreferences sp;


    String curLink = "";
    String cataas = "https://cataas.com";
    ImageData result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        catImage = findViewById(R.id.cat_image);
        Button giveMeACat = findViewById(R.id.button);
        Spinner tag = findViewById(R.id.tag_select);
        EditText say = findViewById(R.id.say_something);
        ImageButton linkB = findViewById(R.id.link);
        ImageButton saveB = findViewById(R.id.save);
        SeekBar color = findViewById(R.id.text_color);
        ImageButton listB = findViewById(R.id.saved_list);

        String baseUrl = "https://cataas.com/cat/";
        RetrofitInterface retrofitInterface;
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);

        sp = getSharedPreferences("save", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences.Editor spEdit = sp.edit();
//        spEdit.clear();
//        spEdit.apply();

        giveMeACat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                say.clearFocus();
                tag.clearFocus();
                color.clearFocus();
                saveB.setBackgroundResource(R.drawable.save_btn);
                curLink = "";

                if(tagStr != "") {tagStr = "/" + tagStr;}
                retrofitInterface.getACat(tagStr, "true").enqueue(new Callback<ImageData>() {
                    @Override
                    public void onResponse(Call<ImageData> call, Response<ImageData> response) {
                        result = response.body();
                        if(result != null) {
                            Log.d("meow",result.toString());
                            imgId = result.get_id();

                            String link = baseUrl;
                            link += imgId;

                            String sayStr = say.getText().toString();
                            if (!sayStr.isEmpty()) {
                                link += ("/says/" + sayStr);
                            }
                            link += "?json=false";
                            if (!sayStr.isEmpty()) {
                                link += "&fontColor=%23" + fontColor.substring(1);
                            }

                            Glide.with(getApplicationContext())
                                    .load(link)
                                    .into(catImage);

                            Log.d("meow", link);

                            if(sp.contains(link) && link != ""){
                                saveB.setBackgroundResource(R.drawable.save_filled_btn);
                            }

                            curLink = link;
                        }
                        else{
                            Log.d("meow","result null");

                            catImage.setImageResource(R.drawable.error);
                            curLink = "";
                        }
                    }

                    @Override
                    public void onFailure(Call<ImageData> call, Throwable t) {
                        Log.d("meow", "bad req");
                        catImage.setImageResource(R.drawable.error);
                        curLink = "";
                    }
                });

            }
        });

        String tagList[] = {"None", "#Cute", "#Funny", "#Fluffy", "#Sleepy", "#Orange", "#White", "#Black"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tagList);
        tag.setAdapter(adapter);
        tag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    tagStr = "";
                    Log.d("meow", "tag reset");
                } else {
                    tagStr = tagList[i].substring(1).toLowerCase();
                    Log.d("meow", "tag: " + tagStr);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String colorList[] = {"#000000", "#ff4848", "#ffac48", "#acff48", "#48ff48",
                 "#48ffac", "#48acff", "#4848ff", "#ac48ff", "#ff48ac", "#ffffff"};
        color.setMax(colorList.length-1);
        say.setTextColor(Color.parseColor(fontColor));
        color.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                fontColor = colorList[i];
                say.setTextColor(Color.parseColor(fontColor));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        linkB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(curLink == ""){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(cataas));
                    Log.d("meow", "link_ cataas");
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(curLink));
                    Log.d("meow", "link_ " + curLink);
                    startActivity(intent);
                }
            }
        });

        saveB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curLink != "") {
                    if (sp.contains(curLink)) {
                        saveB.setBackgroundResource(R.drawable.save_btn);
                        spEdit.remove(curLink);
                        Log.d("meow", "remove_ " + curLink);
                    } else {
                        saveB.setBackgroundResource(R.drawable.save_filled_btn);
                        SavedData newData = new SavedData();

                        newData.setTag(tag.getSelectedItemPosition());
                        newData.setSay(say.getText().toString());
                        newData.setFontColor(color.getProgress());
                        newData.setImgId(imgId);
                        newData.setImgLink(curLink);
                        newData.setSavedDate(newData.calToString(Calendar.getInstance()));

                        spEdit.putString(curLink, gson.toJson(newData));
                        Log.d("meow", "add_ " + curLink);
                    }
                    spEdit.apply();
                }
            }
        });

        listB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListDialog dialog = new ListDialog(new ListDialog.DialogResult() {
                    @Override
                    public void finish(SavedData s) {
                        tag.setSelection(s.getTag());
                        say.setText(s.getSay());
                        color.setProgress(s.getFontColor());

                        Glide.with(getApplicationContext())
                                .load(s.getImgLink())
                                .into(catImage);

                        saveB.setBackgroundResource(R.drawable.save_filled_btn);
                        imgId = s.getImgId();
                        curLink = s.getImgLink();
                    }
                });
                FragmentManager manager = getSupportFragmentManager();
                dialog.show(manager, "listdialog");
                manager.executePendingTransactions();
                dialog.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {

                        dialog.dismiss();
                    }
                });
            }
        });
    }
    //<a target="_blank" href="https://icons8.com/icon/hUqP035cA2Bd/external-link">Link</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>
    //<a target="_blank" href="https://icons8.com/icon/qePzjQLJYgjF/bookmark">Save</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>
}