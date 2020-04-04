package project.dheeraj.hackcovid_19.Fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

import project.dheeraj.hackcovid_19.Activity.ReportPatientActivity;
import project.dheeraj.hackcovid_19.R;

public class facilitiesfragment extends Fragment {

    private FirebaseFirestore db;
    private View view;
    private TextInputEditText etName, etMobile, etAadhaar, etHouse;
    private TextInputEditText etAddress, etPincode, etSearchAadhar;
    private ImageView imageQr;
    private Button buttonSubmit, buttonSearch;
    private TextView tvName, tvAadhaar, tvMobile, tvAddress, tvHouse, tvPin;
    private LinearLayout layoutForm;
    private CardView cardDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_facilities, container, false);

        db = FirebaseFirestore.getInstance();
        etName = view.findViewById(R.id.etName);
        etAadhaar = view.findViewById(R.id.etAadhaar);
        etMobile = view.findViewById(R.id.etMobile);
        etAddress = view.findViewById(R.id.etAddress);
        etPincode = view.findViewById(R.id.etPinCode);
        etSearchAadhar = view.findViewById(R.id.etSearchAadhar);
        etHouse = view.findViewById(R.id.etHouse);
        imageQr = view.findViewById(R.id.imageQR);

        layoutForm = view.findViewById(R.id.layoutForm);
        cardDetails = view.findViewById(R.id.cardUserID);

        tvName = view.findViewById(R.id.textName);
        tvMobile = view.findViewById(R.id.textMobile);
        tvAadhaar = view.findViewById(R.id.textAadhaar);
        tvAddress = view.findViewById(R.id.textAddress);
        tvHouse = view.findViewById(R.id.textHouse);
        tvPin = view.findViewById(R.id.textPinCode);

        buttonSearch = view.findViewById(R.id.buttonSearch);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDetails()){
                    checkData();
                }
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etSearchAadhar.getText().toString().isEmpty()){
                    etAadhaar.setError("Enter Aadhaar number");
                    return;
                }
                Toast.makeText(getContext(), "Please wait", Toast.LENGTH_SHORT).show();
                checkAadhaar();

            }
        });

        return view;

    }

    private void checkAadhaar(){

        db.collection("pass")
                .whereEqualTo("aadhaar", etSearchAadhar.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()){


                                    if (documentSnapshot.getBoolean("verified")) {
                                        tvName.setText("Name : "+documentSnapshot.get("name").toString());
                                        tvMobile.setText("Mobile : "+documentSnapshot.get("mobile").toString());
                                        tvAddress.setText("Address : "+documentSnapshot.get("address").toString());
                                        tvHouse.setText("House Number : "+documentSnapshot.get("house").toString());
                                        tvAadhaar.setText("Aadhaar number : "+documentSnapshot.get("aadhaar").toString());
                                        tvPin.setText("Pincode : "+documentSnapshot.get("pin").toString());

                                        QRCodeWriter writer = new QRCodeWriter();
                                        try {
                                            BitMatrix bitMatrix = writer.encode(documentSnapshot.get("name").toString()+" "+documentSnapshot.get("aadhaar").toString(),
                                                    BarcodeFormat.QR_CODE, 100, 100);
                                            int width = bitMatrix.getWidth();
                                            int height = bitMatrix.getHeight();
                                            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                                            for (int x = 0; x < width; x++) {
                                                for (int y = 0; y < height; y++) {
                                                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                                                }
                                            }
                                            imageQr.setImageBitmap(bmp);

                                        } catch (WriterException e) {
                                            e.printStackTrace();
                                        }

                                        layoutForm.setVisibility(View.GONE);
                                        cardDetails.setVisibility(View.VISIBLE);
                                    }
                                    else{
                                        Toast.makeText(getContext(), "Pending for verification", Toast.LENGTH_SHORT).show();
                                    }

                            }

                        }
                        else {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean checkData(){

        final boolean[] result = {true};

        db.collection("pass")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()){

                                if (documentSnapshot.get("aadhaar").toString().equals(etAadhaar.getText().toString())){
                                    result[0] = false;
                                    Toast.makeText(getContext(), "Aadhaar number already registered", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                                if (documentSnapshot.get("mobile").toString().equals(etMobile.getText().toString())){
                                    result[0] = false;
                                    Toast.makeText(getContext(), "Mobile number already registered", Toast.LENGTH_SHORT).show();
                                    break;
                                }


                            }

                            if (result[0] == true){
                                putData();
                            }

                        }
                        else {
                            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        return result[0];
    }

    private void putData(){
        if (!checkDetails()){
            return;
        }
        buttonSubmit.setClickable(false);
        Map<String, Object> details = new HashMap<>();
        details.put("name", etName.getText().toString());
        details.put("mobile", etMobile.getText().toString());
        details.put("address", etAddress.getText().toString());
        details.put("aadhaar", etAadhaar.getText().toString());
        details.put("house", etHouse.getText().toString());
        details.put("pin", etPincode.getText().toString());
        details.put("verified", false);

        db.collection("pass")
                .add(details)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        buttonSubmit.setClickable(true);
                        if (task.isSuccessful()){
                            Toast.makeText(getActivity(), "Data submitted for verification", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean checkDetails(){
        if (etName.getText().toString().isEmpty()){
            etName.setError("Enter Name");
            return false;
        }
        if (etMobile.getText().toString().isEmpty()){
            etMobile.setError("Enter Mobile");
            return false;
        }
        if (etAadhaar.getText().toString().isEmpty()){
            etAadhaar.setError("Enter your Aadhaar");
            return false;
        }
        if (etAddress.getText().toString().isEmpty()){
            etAddress.setError("Enter your Address");
            return false;
        }
        if (etHouse.getText().toString().isEmpty()){
            etHouse.setError("Enter your House Number");
            return false;
        }
        if (etPincode.getText().toString().isEmpty()){
            etPincode.setError("Enter your Postal code");
            return false;
        }
        return true;
    }

}
