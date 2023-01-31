package Rest;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aadapplication.barcode_scanner;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import JsontoJava.BarcodeObject;

public class GetProduct extends AsyncTask<String, Void, String> {

    public String results;

    private Context context;
    private EditText editText;

    public GetProduct(Context context, EditText editText) {
        this.context = context;
        this.editText = editText;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());
            results=result.toString();
            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            // do something with the result
            updateUI(result);
        } else {
            Toast.makeText(context, "Unable to connect to server", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(String result) {
        // access and update the UI with the result
        if (result !=null){

            Gson gson = new Gson();
            BarcodeObject obj = gson.fromJson(result, BarcodeObject.class);
            System.out.println(obj.getProduct().getProductName());
            editText.setText(obj.getProduct().getProductName());

        }

    }




}