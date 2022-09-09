package app.astrum.astrocoinuz.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.*;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import app.astrum.astrocoinuz.MainActivity;
import app.astrum.astrocoinuz.R;
import app.astrum.astrocoinuz.adapter.ApiClient;
import app.astrum.astrocoinuz.constructor.ImgUpload;
import app.astrum.astrocoinuz.constructor.SetPassword;
import app.astrum.astrocoinuz.constructor.TokenRequest;
import app.astrum.astrocoinuz.data.SqlData;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentSettings extends Fragment {
    private SqlData sqlData;
    String token, id, text, keys, password, photo, qwasar, email, last_name, name, status;
    TextView txtsetfullname, txtsetemail, txtsetlogout, txtsettshop, txtsettperinfo,
            txtsettpassword, txtsoftware, txtwalleet, txtapppass, txtsettrank;
    ShapeableImageView setimageuser;
    ProgressBar setprogressBar;
    SharedPreferences sharedPreferences;
    ImageView imagesetting, imgsample, imgsearch;
    Uri imageUri;
    int n = 0, chesk = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @SuppressLint({"InflateParams", "SetTextI18n", "SetJavaScriptEnabled"})
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        sqlData = new SqlData(getContext());

        setprogressBar = view.findViewById(R.id.setprogressBar);
        setimageuser = view.findViewById(R.id.usimage);

        txtsetfullname = view.findViewById(R.id.txtsetfullname);
        txtsetemail = view.findViewById(R.id.txtsetemail);
        txtsettshop = view.findViewById(R.id.txtsettshop);
        txtsettperinfo = view.findViewById(R.id.txtsettperinfo);
        txtsettpassword = view.findViewById(R.id.txtsettpassword);
        txtsetlogout = view.findViewById(R.id.txtsetlogout);
        txtsoftware = view.findViewById(R.id.txtsettperinfo2);
        txtwalleet = view.findViewById(R.id.txtsettperinfo3);
        txtapppass = view.findViewById(R.id.txtsettpassword2);
        txtsettrank = view.findViewById(R.id.txtsettrank);
        ImageView imgsetphoto = view.findViewById(R.id.imageView26);
        ImageView imgsetphoto1 = view.findViewById(R.id.imageView25);

        imagesetting = view.findViewById(R.id.imageView10);
        imgsample = view.findViewById(R.id.imageView8);
        imgsearch = view.findViewById(R.id.imageView9);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        //int width = displayMetrics.widthPixels;

        if (height < 1300) {
            int joyi = (int) setimageuser.getY();
            imgsetphoto.setY(joyi - 55);
            imgsetphoto1.setY(joyi - 55);
            setimageuser.setY(joyi - 55);
            txtsetfullname.setY(joyi - 55);
            txtsetemail.setY(joyi - 55);
        }
        imgsetphoto.setOnClickListener(v -> CropImage.activity()
                .start(requireContext(), FragmentSettings.this));
        txtwalleet.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(txtwalleet.getText().toString(), txtwalleet.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(requireActivity(), "copy wallet id", Toast.LENGTH_SHORT).show();
        });
        imagesetting.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_500),
                PorterDuff.Mode.SRC_ATOP);
        readsql();
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.custombottomsheet);

        imagesetting.setOnClickListener(v ->
                replaceFragment(new FragmentSettings()));
        imgsample.setOnClickListener(v ->
                replaceFragment1(new FragmentSample()));
        imgsearch.setOnClickListener(v ->
                replaceFragment2(new FragmentSearch()));
        setimageuser.setOnClickListener(view13 -> {
            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.popupsettings);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);

            ShapeableImageView setshapimguser = dialog.findViewById(R.id.setshapimguser);
            if (photo.equals("")) {
                setshapimguser.setImageResource(R.drawable.ic_profile_circle);
            } else {
                Glide.with(requireContext()).load("https://api.astrocoin.uz" + photo).into(setshapimguser);
            }
            setshapimguser.setOnLongClickListener(view1 -> {
                downloadImageNew("temp", "https://api.astrocoin.uz" + photo);
                return true;
            });
            dialog.show();
        });

        //final BottomSheetDialog dialog01 = new BottomSheetDialog(requireContext(), R.style.custombottomsheet);

        txtsettshop.setOnClickListener(v -> {
           /* v = getLayoutInflater().inflate(R.layout.borromsheetweb, null);
            dialog01.setContentView(v);
            dialog01.setCancelable(false);
            WebView mWebView = dialog01.findViewById(R.id.webView);
            View onclossed1 = v.findViewById(R.id.onclossed1);
            View onclossed = v.findViewById(R.id.onclossed);
            onclossed.setOnClickListener(view1 -> dialog01.dismiss());
            onclossed1.setOnClickListener(view1 -> dialog01.dismiss());
            assert mWebView != null;
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            mWebView.loadUrl("https://store.astrocoin.uz/");
            dialog01.show();*/
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://store.astrocoin.uz/"));
            startActivity(browserIntent);
        });
        txtsettrank.setOnClickListener(v -> {
            /*v = getLayoutInflater().inflate(R.layout.borromsheetweb, null);
            dialog01.setContentView(v);
            dialog01.setCancelable(false);
            WebView mWebView = v.findViewById(R.id.webView);
            View onclossed1 = v.findViewById(R.id.onclossed1);
            View onclossed = v.findViewById(R.id.onclossed);
            onclossed.setOnClickListener(view1 -> dialog01.dismiss());
            onclossed1.setOnClickListener(view1 -> dialog01.dismiss());
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            mWebView.loadUrl("https://astrocoin.uz/ranks");
            dialog01.show();*/

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://astrocoin.uz/ranks"));
            startActivity(browserIntent);
        });

        txtapppass.setOnClickListener(view1 -> {
            bottomSheetDialog.setContentView(R.layout.bottomperinfo);
            bottomSheetDialog.setDismissWithAnimation(true);
            View viev1 = bottomSheetDialog.findViewById(R.id.viev1);
            View viev2 = bottomSheetDialog.findViewById(R.id.viev2);
            View viev3 = bottomSheetDialog.findViewById(R.id.viev3);
            View viev4 = bottomSheetDialog.findViewById(R.id.viev4);

            TextView ttt1 = bottomSheetDialog.findViewById(R.id.ttt1);
            TextView ttt2 = bottomSheetDialog.findViewById(R.id.ttt2);
            TextView ttt3 = bottomSheetDialog.findViewById(R.id.ttt3);
            TextView ttt4 = bottomSheetDialog.findViewById(R.id.ttt4);
            TextView ttt5 = bottomSheetDialog.findViewById(R.id.ttt5);
            TextView ttt6 = bottomSheetDialog.findViewById(R.id.ttt6);
            TextView ttt7 = bottomSheetDialog.findViewById(R.id.ttt7);
            TextView ttt8 = bottomSheetDialog.findViewById(R.id.ttt8);
            TextView ttt9 = bottomSheetDialog.findViewById(R.id.ttt9);
            TextView ttt0 = bottomSheetDialog.findViewById(R.id.ttt0);
            EditText edinumber = bottomSheetDialog.findViewById(R.id.edinumber);
            TextView txtpassinfo = bottomSheetDialog.findViewById(R.id.txtpassinfo);
            ImageView imgbackk = bottomSheetDialog.findViewById(R.id.imgbackk);

            InputFilter filter = (source, start, end, dest, dstart, dend) -> {
                for (int v = start; v < end; v++) {
                    if (!Character.toString(source.charAt(v)).matches("[a-zA-Z\\d-_.]+")) {
                        return "";
                    }
                }
                return null;
            };
            assert edinumber != null;
            edinumber.setFilters(new InputFilter[]{filter});
            edinumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (n == 1) {
                        assert viev1 != null;
                        viev1.setBackgroundResource(R.drawable.sahdov1);
                        assert imgbackk != null;
                        imgbackk.setVisibility(View.VISIBLE);
                    }
                    if (n == 2) {
                        assert viev2 != null;
                        viev2.setBackgroundResource(R.drawable.sahdov1);
                        assert imgbackk != null;
                        imgbackk.setVisibility(View.VISIBLE);
                    }
                    if (n == 3) {
                        assert viev3 != null;
                        viev3.setBackgroundResource(R.drawable.sahdov1);
                        assert imgbackk != null;
                        imgbackk.setVisibility(View.VISIBLE);
                    }
                    if (n == 4) {
                        assert viev4 != null;
                        viev4.setBackgroundResource(R.drawable.sahdov1);
                        assert imgbackk != null;
                        imgbackk.setVisibility(View.VISIBLE);
                    }
                    if (n > 4) {
                        n = 4;
                        edinumber.setText(edinumber.getText().toString().substring(0, 4));
                    }
                    if (n > 3) {
                        if (chesk == 0) {
                            if (password.equals(edinumber.getText().toString())) {
                                new android.os.Handler(Looper.getMainLooper()).postDelayed(
                                        () -> {
                                            assert txtpassinfo != null;
                                            txtpassinfo.setText("New password");
                                            assert viev1 != null;
                                            viev1.setBackgroundResource(R.drawable.shadov_soucsess);
                                            assert viev2 != null;
                                            viev2.setBackgroundResource(R.drawable.shadov_soucsess);
                                            assert viev3 != null;
                                            viev3.setBackgroundResource(R.drawable.shadov_soucsess);
                                            assert viev4 != null;
                                            viev4.setBackgroundResource(R.drawable.shadov_soucsess);
                                            chesk = 1;
                                            n = 0;
                                            edinumber.setText(null);
                                            viev4.setBackgroundResource(R.drawable.shadov_soucsess);
                                            new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                        assert viev1 != null;
                                                        viev1.setBackgroundResource(R.drawable.shadov);
                                                        assert viev2 != null;
                                                        viev2.setBackgroundResource(R.drawable.shadov);
                                                        assert viev3 != null;
                                                        viev3.setBackgroundResource(R.drawable.shadov);
                                                        assert viev4 != null;
                                                        viev4.setBackgroundResource(R.drawable.shadov);
                                                    },
                                                    500);
                                        },
                                        500);
                            } else {
                                assert txtpassinfo != null;
                                assert viev1 != null;
                                viev1.setBackgroundResource(R.drawable.shadovfaild);
                                assert viev2 != null;
                                viev2.setBackgroundResource(R.drawable.shadovfaild);
                                assert viev3 != null;
                                viev3.setBackgroundResource(R.drawable.shadovfaild);
                                assert viev4 != null;
                                viev4.setBackgroundResource(R.drawable.shadovfaild);
                                n = 0;
                                edinumber.setText(null);
                                new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
                                            assert viev1 != null;
                                            viev1.setBackgroundResource(R.drawable.shadov);
                                            assert viev2 != null;
                                            viev2.setBackgroundResource(R.drawable.shadov);
                                            assert viev3 != null;
                                            viev3.setBackgroundResource(R.drawable.shadov);
                                            assert viev4 != null;
                                            viev4.setBackgroundResource(R.drawable.shadov);
                                        },
                                        500);
                            }
                        }
                        if (chesk == 1) {
                            boolean a = sqlData.ozgartir(id, token, "1", edinumber.getText().toString(), "1");
                            if (a) {
                                Toast.makeText(requireContext(), "soucses", Toast.LENGTH_SHORT).show();
                                new android.os.Handler(Looper.getMainLooper()).postDelayed(
                                        () -> {
                                            assert viev1 != null;
                                            viev1.setBackgroundResource(R.drawable.shadov_soucsess);
                                            assert viev2 != null;
                                            viev2.setBackgroundResource(R.drawable.shadov_soucsess);
                                            assert viev3 != null;
                                            viev3.setBackgroundResource(R.drawable.shadov_soucsess);
                                            assert viev4 != null;
                                            viev4.setBackgroundResource(R.drawable.shadov_soucsess);
                                            chesk = 0;
                                            n = 0;
                                            edinumber.setText(null);
                                            viev4.setBackgroundResource(R.drawable.shadov_soucsess);
                                            readData();
                                            readsql();
                                        },
                                        500);
                                new android.os.Handler(Looper.getMainLooper()).postDelayed(
                                        bottomSheetDialog::dismiss,
                                        500);
                            } else {
                                assert txtpassinfo != null;
                                assert viev1 != null;
                                viev1.setBackgroundResource(R.drawable.shadovfaild);
                                assert viev2 != null;
                                viev2.setBackgroundResource(R.drawable.shadovfaild);
                                assert viev3 != null;
                                viev3.setBackgroundResource(R.drawable.shadovfaild);
                                assert viev4 != null;
                                viev4.setBackgroundResource(R.drawable.shadovfaild);
                                n = 0;
                                edinumber.setText(null);
                                new android.os.Handler(Looper.getMainLooper()).postDelayed(
                                        () -> {
                                            assert viev1 != null;
                                            viev1.setBackgroundResource(R.drawable.shadov);
                                            assert viev2 != null;
                                            viev2.setBackgroundResource(R.drawable.shadov);
                                            assert viev3 != null;
                                            viev3.setBackgroundResource(R.drawable.shadov);
                                            assert viev4 != null;
                                            viev4.setBackgroundResource(R.drawable.shadov);
                                        },
                                        500);
                            }
                        }
                    }
                }
            });

            assert ttt1 != null;
            ttt1.setOnClickListener(view2 -> {
                n++;
                text = edinumber.getText().toString().trim();
                edinumber.setText(text + getString(R.string._1));
            });
            assert ttt2 != null;
            ttt2.setOnClickListener(view2 -> {
                n++;
                text = edinumber.getText().toString().trim();
                edinumber.setText(text + getString(R.string._2));
            });
            assert ttt3 != null;
            ttt3.setOnClickListener(view2 -> {
                n++;
                text = edinumber.getText().toString().trim();
                edinumber.setText(text + getString(R.string._3));

            });
            assert ttt4 != null;
            ttt4.setOnClickListener(view2 -> {
                n++;
                text = edinumber.getText().toString().trim();
                edinumber.setText(text + getString(R.string._4));

            });
            assert ttt5 != null;
            ttt5.setOnClickListener(view2 -> {
                n++;
                text = edinumber.getText().toString().trim();
                edinumber.setText(text + getString(R.string._5));

            });
            assert ttt6 != null;
            ttt6.setOnClickListener(view2 -> {
                n++;
                text = edinumber.getText().toString().trim();
                edinumber.setText(text + getString(R.string._6));

            });
            assert ttt7 != null;
            ttt7.setOnClickListener(view2 -> {
                n++;
                text = edinumber.getText().toString().trim();
                edinumber.setText(text + getString(R.string._7));

            });
            assert ttt8 != null;
            ttt8.setOnClickListener(view2 -> {
                n++;
                text = edinumber.getText().toString().trim();
                edinumber.setText(text + getString(R.string._8));

            });
            assert ttt9 != null;
            ttt9.setOnClickListener(view2 -> {
                n++;
                text = edinumber.getText().toString().trim();
                edinumber.setText(text + getString(R.string._9));

            });
            assert ttt0 != null;
            ttt0.setOnClickListener(view2 -> {
                n++;
                text = edinumber.getText().toString().trim();
                edinumber.setText(text + getString(R.string._0));

            });
            assert imgbackk != null;
            imgbackk.setOnClickListener(view2 -> {
                text = edinumber.getText().toString();
                n = text.length() - 1;
                if (n >= 0) {
                    String substr = text.substring(0, n);
                    edinumber.setText(substr);
                    if (n == 0) {
                        assert viev1 != null;
                        viev1.setBackgroundResource(R.drawable.shadov);
                    }
                    if (n == 1) {
                        assert viev2 != null;
                        viev2.setBackgroundResource(R.drawable.shadov);
                    }
                    if (n == 2) {
                        assert viev3 != null;
                        viev3.setBackgroundResource(R.drawable.shadov);
                    }
                    if (n == 3) {
                        assert viev4 != null;
                        viev4.setBackgroundResource(R.drawable.shadov);
                    }
                } else {
                    n = 0;
                }
            });


            bottomSheetDialog.show();
        });
        txtsettpassword.setOnClickListener(v -> {
            v = getLayoutInflater().inflate(R.layout.bottompassword, null);
            bottomSheetDialog.setContentView(v);

            EditText ediserpass1, ediserpass2, ediserpass3;
            Button btnsetsubmit;

            btnsetsubmit = v.findViewById(R.id.btnsetsubmit);
            ediserpass1 = v.findViewById(R.id.ediserpass1);
            ediserpass2 = v.findViewById(R.id.ediserpass2);
            ediserpass3 = v.findViewById(R.id.ediserpass3);

            btnsetsubmit.setOnClickListener(view12 -> {
                if (ediserpass1.getText().toString().isEmpty()) {
                    ediserpass1.setError("Password is required");
                    return;
                }
                if (ediserpass2.getText().toString().isEmpty()) {
                    ediserpass2.setError("Password is required");
                    return;
                }
                if (ediserpass3.getText().toString().isEmpty()) {
                    ediserpass3.setError("Password is required");
                    return;
                }
                if (ediserpass1.getText().toString().length() < 7) {
                    ediserpass1.setError("Password must be at least 8 characters");
                    return;
                }
                if (ediserpass2.getText().toString().length() < 7) {
                    ediserpass2.setError("Password must be at least 8 characters");
                    return;
                }
                if (ediserpass3.getText().toString().length() < 7) {
                    ediserpass3.setError("Password must be at least 8 characters");
                    return;
                }
                if (!ediserpass2.getText().toString().equals(ediserpass3.getText().toString())) {
                    ediserpass3.setError("Password not match");
                } else {
                    String password1 = ediserpass1.getText().toString();
                    String password2 = ediserpass2.getText().toString();
                    String password3 = ediserpass3.getText().toString();
                    SetPassword setPassword = new SetPassword(password1, password2, password3);

                    Call<Object> call = ApiClient.getUserService().userChangePassword("Bearer " + token, setPassword);
                    call.enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(@NotNull Call<Object> call, @NotNull Response<Object> response) {
                            String message;
                            if (response.body() != null) {
                                message = response.body().toString();
                                new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
                                            if (message.equals("{error=null}")) {
                                                Toast.makeText(requireContext(), "Password changed", Toast.LENGTH_SHORT).show();
                                                bottomSheetDialog.dismiss();
                                                call.cancel();
                                            } else {
                                                Toast.makeText(requireContext(), "Password is incorrect", Toast.LENGTH_SHORT).show();
                                                call.cancel();
                                            }
                                        },
                                        900);
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<Object> call, @NotNull Throwable t) {
                            bottomSheetDialog.dismiss();
                            Toast.makeText(requireContext(), "Password not changed", Toast.LENGTH_SHORT).show();
                            call.cancel();
                        }
                    });
                }
            });
            bottomSheetDialog.show();
        });


        txtsetlogout.setOnClickListener(v -> {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.CustomDialog);
            builder.setTitle("Logout");
            builder.setIcon(R.drawable.ic_felid);
            builder.setMessage("Are you sure you want to logout?");
            builder.setPositiveButton("Yes", (dialog, which) ->
                    vlvlvlv()
            );
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss()
            );
            androidx.appcompat.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });
    }


    @SuppressLint("LongLogTag")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(requireContext(), this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), resultUri);
                    setimageuser.setImageBitmap(bitmap);
                    File file = new File(Uri.parse(resultUri.toString()).getPath());
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    MultipartBody.Part requestImage = MultipartBody.Part.createFormData("photo", file.getName(), requestBody);
                    Call<ImgUpload> call = ApiClient.getUserService().userSetPhoto("Bearer " + token, requestImage);
                    call.enqueue(new Callback<ImgUpload>() {
                        @Override
                        public void onResponse(@NotNull Call<ImgUpload> call, @NotNull Response<ImgUpload> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show();
                                updatedata();
                                new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> readData(), 1500);
                                call.cancel();
                            } else {
                                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show();
                                call.cancel();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<ImgUpload> call, @NotNull Throwable t) {
                            Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show();
                            call.cancel();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void replaceFragment(FragmentSettings fragmentSettings) {
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentlayout, fragmentSettings);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void replaceFragment1(FragmentSample fragmentSample) {
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentlayout, fragmentSample);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void replaceFragment2(FragmentSearch fragmentSearch) {
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentlayout, fragmentSearch);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void updatedata() {
        Call<TokenRequest> tokenRequestCall = ApiClient.getUserService().userTokenRequest("Bearer " + token);
        tokenRequestCall.enqueue(new Callback<TokenRequest>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<TokenRequest> call, @NotNull Response<TokenRequest> response) {
                if (response.isSuccessful()) {
                    TokenRequest tokenRequest1 = response.body();
                    new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (tokenRequest1 != null) {
                            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("dycfvsufgjoafienvk.iso", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("name", tokenRequest1.getName());
                            editor.putString("last_name", tokenRequest1.getLast_name());
                            editor.putString("qwasar", tokenRequest1.getQwasar());
                            editor.putString("email", tokenRequest1.getEmail());
                            editor.putString("number", tokenRequest1.getNumber());
                            editor.putString("stack", tokenRequest1.getStack());
                            editor.putString("role", tokenRequest1.getRole());
                            editor.putString("status", tokenRequest1.getStatus());
                            editor.putString("photo", tokenRequest1.getPhoto());
                            editor.putString("balance", tokenRequest1.getBalance());
                            editor.putString("wallet", tokenRequest1.getWallet());
                            editor.apply();
                            call.cancel();
                        }
                    }, 500);
                }
            }

            @Override
            public void onFailure(@NotNull Call<TokenRequest> call, @NotNull Throwable t) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }

    private void vlvlvlv() {
        sharedPreferences = requireActivity().getSharedPreferences("dycfvsufgjoafienvk.iso", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", "");
        editor.putString("last_name", "");
        editor.putString("qwasar", "");
        editor.putString("email", "");
        editor.putString("number", "");
        editor.putString("stack", "");
        editor.putString("role", "");
        editor.putString("status", "");
        editor.putString("photo", "");
        editor.putString("balance", "");
        editor.putString("wallet", "");
        editor.apply();
        readsql();
        boolean s = sqlData.ozgartir(id, "0", "0", "0", "1");
        if (s) {
            Intent intent = new Intent(requireContext(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }
    }

    private void readsql() {
        Cursor res = sqlData.oqish();
        StringBuilder stringBuffer = new StringBuilder();
        StringBuilder stringBuffer1 = new StringBuilder();
        StringBuilder stringBuffer2 = new StringBuilder();
        StringBuilder stringBuffer3 = new StringBuilder();
        StringBuilder stringBuffer4 = new StringBuilder();
        if (res != null && res.getCount() > 0) {
            while (res.moveToNext()) {
                stringBuffer.append(res.getString(0));
                stringBuffer1.append(res.getString(1));
                stringBuffer2.append(res.getString(2));
                stringBuffer3.append(res.getString(3));
                stringBuffer4.append(res.getString(4));
            }
            id = stringBuffer.toString();
            token = stringBuffer1.toString();
            keys = stringBuffer2.toString();
            password = stringBuffer3.toString();
            /*if (id.equals("")&&token.equals("")&&keys.equals("")&&password.equals("")){
                startActivity(new Intent(requireContext(), MainActivity.class));
                requireActivity().finish();
            }*/
            readData();
        }
    }

    @SuppressLint("SetTextI18n")
    public void readData() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("dycfvsufgjoafienvk.iso", Context.MODE_PRIVATE);
        name = sharedPreferences.getString("name", "");
        last_name = sharedPreferences.getString("last_name", "");
        email = sharedPreferences.getString("email", "");
        qwasar = sharedPreferences.getString("qwasar", "");
        photo = sharedPreferences.getString("photo", "");
        status = sharedPreferences.getString("status", "");

        txtsettperinfo.setText(sharedPreferences.getString("qwasar", ""));
        txtwalleet.setText(sharedPreferences.getString("wallet", ""));
        txtsoftware.setText(sharedPreferences.getString("stack", ""));

        if (!status.equals("verified")) {
            txtsettrank.setText("Your account is blocked");
            txtsettrank.setTextColor(Color.RED);
        }

        if (Objects.equals(photo, "")) {
            setimageuser.setImageResource(R.drawable.ic_profile_circle);
        } else {
            Glide.with(requireContext()).load("https://api.astrocoin.uz" + photo).into(setimageuser);
        }
        setprogressBar.setVisibility(View.GONE);
        txtsetfullname.setText(name + " " + last_name);
        txtsetemail.setText(email);
    }

    private void downloadImageNew(String filename, String downloadUrlOfImage) {
        try {
            DownloadManager dm = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(downloadUrlOfImage);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + filename + ".jpg");
            dm.enqueue(request);
            Toast.makeText(requireContext(), "Image download started.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Image download failed.", Toast.LENGTH_SHORT).show();
        }
    }
}