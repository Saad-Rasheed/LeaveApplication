package com.agp.leaveapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.agp.leaveapplication.Admin.Admin_HomeActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class EmployeeDetailFragment extends Fragment {

    private static final String ROOT_URL = "http://" + LoginActivity.IP + "/LeaveApi/ImagePath.php/Api.php?apicall=";
    public static final String UPLOAD_URL = ROOT_URL + "uploadpic";

    SharedPreferences sharedPreferences;
    private String EmpCode;
    public static final String Pre = "MyPre";
    Button savechanges;
    private KProgressHUD loader;
    private static final int REQUEST_WRITE_PERMISSION = 786;

    ImageView imageView; //ImageView to display image selected

    public EmployeeDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employee_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loader = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setWindowColor(Color.parseColor("#201460"));

        TextView EmployeeCode = view.findViewById(R.id.tvEmployeeNo);
        TextView Name = view.findViewById(R.id.tvName);
        TextView Department = view.findViewById(R.id.tvDepartment);
        TextView Designation = view.findViewById(R.id.tvDesignation);

        imageView = view.findViewById(R.id.imageView);
        savechanges = view.findViewById(R.id.savechanges);

        sharedPreferences = getActivity().getSharedPreferences(Pre, Context.MODE_PRIVATE);
        EmpCode = sharedPreferences.getString("EmpCode", "");

        String emp_code = sharedPreferences.getString("EmpCode", "");
        String emp_name = sharedPreferences.getString("EmployeeName", "");
        if (emp_name.endsWith(".")) {
            Name.setText(" Employee Name: " + emp_name.substring(0, emp_name.length() - 1));
        } else {
            Name.setText(" Employee Name: " + sharedPreferences.getString("EmployeeName", ""));
        }

        EmployeeCode.setText(" EmployeeNo: " + emp_code.substring(1));
        Department.setText(" Reporting to: " + sharedPreferences.getString("MgrName", ""));
        Designation.setText(" Designation: " + sharedPreferences.getString("Designation", ""));

        //adding click listener to button
        view.findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();

            }
        });

        GetEmployeeDetail();

    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {
            //if everything is ok we will open image chooser
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i.createChooser(i, "Select Picture"), 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //if everything is ok we will open image chooser
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i.createChooser(i, "Select Picture"), 100);
        }
    }

    public void GetEmployeeDetail() {

        //loader.show();
        String URL = "http://" + LoginActivity.IP + "/LeaveApi/selectImage.php";
        StringRequest req = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.isEmpty()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String image = "http://agpapp02.agp.com.pk/LeaveApi/uploads/" + jsonObject.getString("EmployeeImage");
                                if (!image.equals("")) {
                                    image = image.replaceAll(" ", "%20");
                                    Picasso.get()
                                            .load(image)
                                            .resize(80, 80)
                                            .centerCrop()
                                            .placeholder(R.drawable.ic_avatar)
                                            .into(imageView);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getContext(), "No Response",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("EmployeeNo", EmpCode);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(req);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            //getting the image Uri
            Uri imageUri = data.getData();
            try {
                //getting bitmap object from uri
                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                //displaying selected image to imageview
                imageView.setImageBitmap(bitmap);
                //calling the method uploadBitmap to upload image
                savechanges.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uploadBitmap(bitmap);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void uploadBitmap(final Bitmap bitmap) {

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /* * If you want to add more parameters with the image * you can do it here * here we have only one
            parameter with the image which is EmployeeNo* */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("EmployeeNo", EmpCode);
                return map;
            }

            /* * Here we are passing image by renaming it with a unique name * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("pic", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(volleyMultipartRequest);
    }
}
