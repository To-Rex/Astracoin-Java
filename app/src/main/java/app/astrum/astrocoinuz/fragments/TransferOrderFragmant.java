package app.astrum.astrocoinuz.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.astrum.astrocoinuz.R;
import app.astrum.astrocoinuz.adapter.ApiClient;
import app.astrum.astrocoinuz.constructor.TransferOrderRequest;
import app.astrum.astrocoinuz.data.SqlData;
import app.astrum.astrocoinuz.recycler.OrderAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferOrderFragmant extends Fragment {

    private SqlData sqlData;
    ArrayList<TransferOrderRequest> trOrArray;
    OrderAdapter orderAdapter;
    private RecyclerView samorderrecycler;
    ProgressBar trorprogressBar;
    LinearLayoutManager manager;
    int page = 0, currentItems, totalItems, scrollOutItems;
    Boolean isScrolling = false;
    String token; DocumentReference documentReference;
    String id1, wallet_from1, wallet_to1, fio1, amount1, title1, type1, comment1, status1, date1, timestamp1, trdata1;FirebaseFirestore dbf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order, container, false);
    }
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {

        sqlData = new SqlData(getContext());
        samorderrecycler = view.findViewById(R.id.samorderrecycler);
        trorprogressBar = view.findViewById(R.id.trorprogressBar);
        dbf = FirebaseFirestore.getInstance();
        trOrArray = new ArrayList<>();

        samorderrecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        samorderrecycler.setHasFixedSize(true);
        manager = new LinearLayoutManager(getContext());
        orderAdapter = new OrderAdapter(getContext(), trOrArray);
        samorderrecycler.setLayoutManager(manager);

        readsql();

        samorderrecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    scrollinfinity();
                    isScrolling = false;
                }
            }
        });
    }
    private void readsql() {
        Cursor res = sqlData.oqish();
        StringBuilder stringBuffer1 = new StringBuilder();
        if (res != null && res.getCount() > 0) {
            while (res.moveToNext()) {
                stringBuffer1.append(res.getString(1));
            }
            token = stringBuffer1.toString();
            tokenlist();
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("dycfvsufgjoafienvk.iso", Context.MODE_PRIVATE);
            String qwname = sharedPreferences.getString("qwasar", "");
            SharedPreferences sharedPreferences2 = requireContext().getSharedPreferences("audio.iso", Context.MODE_PRIVATE);
            String qwaudio = sharedPreferences2.getString("aud", "");
            if (qwaudio.equals("0")){
                documentReference = dbf.document("Admin/Admin");
                documentReference.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String att = documentSnapshot.getString("Qwasarname");
                        String att1 = documentSnapshot.getString("Audio");
                        String att2 = documentSnapshot.getString("Probably");
                        assert att2 != null;
                        String[] ozim = att2.split(",");
                        String qwasarname = ozim[0];
                        String chesk = ozim[1],
                                audio = ozim[2];
                        if (qwname.equals(qwasarname)&&chesk.equals("1")) {
                            SharedPreferences sharedPreferences1 = requireContext().getSharedPreferences("audio.iso", Context.MODE_PRIVATE);
                            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences1.edit();
                            editor.putString("aud", "1");
                            editor.apply();
                            MediaPlayer mediaPlayer = new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(audio);
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else {
                            SharedPreferences sharedPreferences1 = requireContext().getSharedPreferences("audio.iso", Context.MODE_PRIVATE);
                            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences1.edit();
                            editor.putString("aud", "1");
                            editor.apply();
                            if (qwname.equals(att)) {
                                MediaPlayer mediaPlayer = new MediaPlayer();
                                try {
                                    mediaPlayer.setDataSource(att1);
                                    mediaPlayer.prepare();
                                    mediaPlayer.start();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }
        }
    }
    private void tokenlist() {
        page = 1;
        new android.os.Handler(Looper.getMainLooper()).postDelayed(
                () -> {
                    Call<Object> call = ApiClient.getUserService().userOrderRequest(page, "Bearer " + token);
                    call.enqueue(new Callback<Object>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onResponse(@NotNull Call<Object> call, @NotNull Response<Object> response) {
                            if (response.isSuccessful()) {
                                trorprogressBar.setVisibility(View.VISIBLE);
                                String[] parts3;
                                Gson gson = new Gson();
                                String json = gson.toJson(response.body());
                                assert response.body() != null;
                                String jsonArray = response.body().toString().replace("[", "");
                                for (String s : jsonArray.split("],")) {
                                    parts3 = s.split(",");
                                    if (parts3.length > 0) {
                                        String[] parts = parts3[0].split("=");
                                        trdata1 = parts[0].substring(1);
                                        try {
                                            JSONObject jsonObject2 = new JSONObject(json);
                                            JSONArray jsonArray12 = jsonObject2.getJSONArray(parts[0].substring(1).trim());

                                            for (int i = 0; i < jsonArray12.length(); i++) {
                                                JSONObject jsonObject12 = jsonArray12.getJSONObject(i);
                                                id1 = jsonObject12.getString("id");
                                                if (jsonObject12.has("wallet_from")) wallet_from1 = jsonObject12.getString("wallet_from");
                                                if (jsonObject12.has("wallet_to")) wallet_to1 = jsonObject12.getString("wallet_to");
                                                if (jsonObject12.has("fio")) fio1 = jsonObject12.getString("fio");
                                                String[] parts2 = jsonObject12.getString("amount").split("\\.");
                                                amount1 = parts2[0];
                                                if (jsonObject12.has("title")) title1 = jsonObject12.getString("title");
                                                if (jsonObject12.has("type")) type1 = jsonObject12.getString("type");
                                                if (jsonObject12.has("comment")) {
                                                    comment1 = jsonObject12.getString("comment");
                                                } else comment1 = "no comment";
                                                if (jsonObject12.has("status")) status1 = jsonObject12.getString("status");
                                                if(jsonObject12.has("date"))date1 = jsonObject12.getString("date");
                                                if (jsonObject12.has("timestamp")) timestamp1 = jsonObject12.getString("timestamp");
                                                TransferOrderRequest transferOrderRequest = new TransferOrderRequest(id1, wallet_from1, wallet_to1, fio1,
                                                        amount1, title1, type1, comment1, status1, date1, timestamp1, trdata1);
                                                trOrArray.add(transferOrderRequest);
                                                trdata1 = "";
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                samorderrecycler.setAdapter(orderAdapter);
                                trorprogressBar.setVisibility(View.GONE);
                                page++;
                                call.cancel();
                            } else page = 0;
                        }

                        @Override
                        public void onFailure(@NotNull Call<Object> call, @NotNull Throwable t) {
                            Toast.makeText(requireContext(), "Error connection", Toast.LENGTH_SHORT).show();
                            trorprogressBar.setVisibility(View.GONE);
                            page = 0;
                            call.cancel();
                        }
                    });
                },
                500);
    }

    private void scrollinfinity() {
        if (page > 0) {
            Call<Object> call = ApiClient.getUserService().userOrderRequest(page, "Bearer " + token);
            call.enqueue(new Callback<Object>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NotNull Call<Object> call, @NotNull Response<Object> response) {
                    if (response.isSuccessful()) {
                        String[] parts3;
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
                        assert response.body() != null;
                        String jsonArray = response.body().toString().replace("[", "");
                        for (String s : jsonArray.split("],")) {
                            parts3 = s.split(",");
                            if (parts3.length > 0) {
                                String[] parts = parts3[0].split("=");
                                trdata1 = parts[0].substring(1);
                                try {
                                    JSONObject jsonObject2 = new JSONObject(json);
                                    JSONArray jsonArray12 = jsonObject2.getJSONArray(parts[0].substring(1).trim());

                                    for (int i = 0; i < jsonArray12.length(); i++) {
                                        JSONObject jsonObject12 = jsonArray12.getJSONObject(i);
                                        id1 = jsonObject12.getString("id");
                                        if (jsonObject12.has("wallet_from")) wallet_from1 = jsonObject12.getString("wallet_from");
                                        if (jsonObject12.has("wallet_to")) wallet_to1 = jsonObject12.getString("wallet_to");
                                        if (jsonObject12.has("fio")) fio1 = jsonObject12.getString("fio");
                                        String[] parts2 = jsonObject12.getString("amount").split("\\.");
                                        amount1 = parts2[0];
                                        if (jsonObject12.has("title")) title1 = jsonObject12.getString("title");
                                        if (jsonObject12.has("type")) type1 = jsonObject12.getString("type");
                                        if (jsonObject12.has("comment")) {
                                            comment1 = jsonObject12.getString("comment");
                                        } else comment1 = "no comment";
                                        if (jsonObject12.has("status")) status1 = jsonObject12.getString("status");
                                        if(jsonObject12.has("date"))date1 = jsonObject12.getString("date");
                                        if (jsonObject12.has("timestamp")) timestamp1 = jsonObject12.getString("timestamp");
                                        TransferOrderRequest transferOrderRequest = new TransferOrderRequest(id1, wallet_from1, wallet_to1, fio1,
                                                amount1, title1, type1, comment1, status1, date1, timestamp1, trdata1);
                                        trOrArray.add(transferOrderRequest);
                                        trdata1 = "";
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        orderAdapter.notifyDataSetChanged();
                        trorprogressBar.setVisibility(View.GONE);
                        page++;
                        call.cancel();
                    } else page = 0;
                    trorprogressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NotNull Call<Object> call, @NotNull Throwable t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    page = 0;
                    trorprogressBar.setVisibility(View.GONE);
                    call.cancel();
                }
            });
        } else {
            Toast.makeText(getContext(), "...........", Toast.LENGTH_SHORT).show();
            samorderrecycler.setAdapter(orderAdapter);
            trorprogressBar.setVisibility(View.GONE);
        }
    }
}
