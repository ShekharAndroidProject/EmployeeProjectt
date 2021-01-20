package com.example.employeeproject.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.employeeproject.R;
import com.example.employeeproject.adapter.EmployeeAdapter;
import com.example.employeeproject.data.DatabaseClient;
import com.example.employeeproject.data.EmployeeDao;
import com.example.employeeproject.data.EmployeeDatabase;
import com.example.employeeproject.model.Employee;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private int birthYear, birthMonth, birthDay, age;
    EditText et_firstName, et_lastName, et_contactNumber, et_dateOfBirth;
    Button btn_register;
    RadioButton rb_male, rb_female;
    RecyclerView recyclerview_employeeList;
    int count = 0;
    EmployeeDao employeeDao;
    EmployeeDatabase db;
    String gender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        employeeDao = Room.databaseBuilder(this, EmployeeDatabase.class, "employee_database.db")
                .allowMainThreadQueries()
                .build().getEmployeeDao();

        et_dateOfBirth = (EditText) findViewById(R.id.et_dateOfBirth);
        et_firstName = (EditText) findViewById(R.id.et_firstName);
        et_lastName = (EditText) findViewById(R.id.et_lastName);
        et_contactNumber = (EditText) findViewById(R.id.et_contactNumber);
        btn_register = (Button) findViewById(R.id.btn_register);
        rb_male = (RadioButton) findViewById(R.id.rb_male);
        rb_female = (RadioButton) findViewById(R.id.rb_female);
        recyclerview_employeeList=(RecyclerView)findViewById(R.id.recyclerview_employeeList);
        recyclerview_employeeList.setLayoutManager(new LinearLayoutManager(this));

        rb_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "Male";
            }
        });
        rb_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "Female";
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEmployee();
            }
        });
        getEmployee();
        et_dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                birthYear = c.get(Calendar.YEAR);
                birthMonth = c.get(Calendar.MONTH);
                birthDay = c.get(Calendar.DAY_OF_MONTH);
                final DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                age = birthYear - year;
                                et_dateOfBirth.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, birthYear, birthMonth, birthDay);
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 315569260);
            }
        });
    }


private void getEmployee() {
    class GetEmployee extends AsyncTask<Void, Void, List<Employee>> {

        @Override
        protected List<Employee> doInBackground(Void... voids) {
            List<Employee> employeeList = DatabaseClient
                    .getInstance(getApplicationContext())
                    .getAppDatabase()
                    .getEmployeeDao()
                    .getAll();
            return employeeList;
        }

        @Override
        protected void onPostExecute(List<Employee> employees) {
            super.onPostExecute(employees);
            EmployeeAdapter adapter = new EmployeeAdapter(MainActivity.this, employees);
            recyclerview_employeeList.setAdapter(adapter);
        }
    }
    GetEmployee gt = new GetEmployee();
    gt.execute();
}

    private void saveEmployee() {
        final String val_firstName = et_firstName.getText().toString();
        final String val_lastName = et_lastName.getText().toString();
        final String val_contactNumber = et_contactNumber.getText().toString();
        final String val_dateOfBirth = et_dateOfBirth.getText().toString();

        if (val_firstName.isEmpty()) {
            et_firstName.setError("Field required");
            et_firstName.requestFocus();
            return;
        }

        if (val_lastName.isEmpty()) {
            et_lastName.setError("Field cannot be empty");
            et_lastName.requestFocus();
            return;
        }
        if (val_contactNumber.isEmpty()) {
            et_contactNumber.setError("Field cannot be empty");
            et_contactNumber.requestFocus();
            return;
        }
        if (val_dateOfBirth.isEmpty()) {
            et_dateOfBirth.setError("Field cannot be empty");
            et_dateOfBirth.requestFocus();
            return;
        }
        if (gender.isEmpty()) {
            rb_female.setError("Select Gender");
            rb_female.requestFocus();
            return;
        }
        if (val_contactNumber.length()<10) {
            et_contactNumber.setError("Contain 10 digits");
            et_contactNumber.requestFocus();
            return;
        }
        if (age<18) {
            et_dateOfBirth.setError("Age should be 18");
            et_dateOfBirth.requestFocus();
            return;
        }

        class SaveEmployee extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                Employee employee = new Employee(val_firstName, val_lastName, val_contactNumber, val_dateOfBirth, gender);
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .getEmployeeDao()
                        .insert(employee);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                finish();
                startActivity(new Intent(getApplicationContext(), EmployeeHome.class));
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
            }
        }
        SaveEmployee saveEmployee = new SaveEmployee();
        saveEmployee.execute();
    }
}
