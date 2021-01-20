package com.example.employeeproject.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import com.example.employeeproject.data.DatabaseClient;
import com.example.employeeproject.model.Employee;

import java.util.Calendar;

public class UpdateEmployeeDetails extends AppCompatActivity {
    EditText et_updateFirstName, et_updateLastName, et_updateContactNumber, et_updateDob;
    Button btn_update,btn_delete;
    RadioButton rb_update_male, rb_update_female;
    String gender="";
    private int birthYear, birthMonth, birthDay, age;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_employee_details);
        et_updateDob = (EditText) findViewById(R.id.et_updateDob);
        et_updateFirstName = (EditText) findViewById(R.id.et_updateFirstName);
        et_updateLastName = (EditText) findViewById(R.id.et_updateLastName);
        et_updateContactNumber = (EditText) findViewById(R.id.et_updateContactNumber);
        btn_update = (Button) findViewById(R.id.btn_update);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        rb_update_male = (RadioButton) findViewById(R.id.rb_male);
        rb_update_female = (RadioButton) findViewById(R.id.rb_female);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Employee employee = (Employee) getIntent().getSerializableExtra("employeeDetails");

        loadEmployee(employee);
        rb_update_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "Male";
            }
        });
        rb_update_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "Female";
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_LONG).show();
                updateEmployee(employee);
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateEmployeeDetails.this);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteEmployee(employee);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
        });
                AlertDialog ad = builder.create();
                ad.show();
            }
        });
        et_updateDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                birthYear = c.get(Calendar.YEAR);
                birthMonth = c.get(Calendar.MONTH);
                birthDay = c.get(Calendar.DAY_OF_MONTH);
                final DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateEmployeeDetails.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                age = birthYear - year;
                                et_updateDob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, birthYear, birthMonth, birthDay);
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 315569260);
            }
        });


    }
    private void loadEmployee(Employee employee) {
        et_updateFirstName.setText(employee.getFirstName());
        et_updateLastName.setText(employee.getLastName());
        et_updateContactNumber.setText(employee.getPhoneNumber());
        et_updateDob.setText(employee.getDob());

        if(employee.getGender().equals(("Male"))){
            rb_update_male.setChecked(true);
        }
        if(employee.getGender().equals(("Female"))){
            rb_update_female.setChecked(true);
        }
    }

    private void updateEmployee(final Employee employee) {
        final String update_firstName = et_updateFirstName.getText().toString().trim();
        final String update_lastName = et_updateLastName.getText().toString().trim();
        final String update_contactNumber = et_updateContactNumber.getText().toString().trim();
        final String update_dob = et_updateDob.getText().toString().trim();
        if (employee.getGender().equals("Male")){
            rb_update_male.setChecked(true);
        }
        if (employee.getGender().equals("Female")){
            rb_update_female.setChecked(true);
        }

        if (update_firstName.isEmpty()) {
            et_updateFirstName.setError("Field required");
            et_updateFirstName.requestFocus();
            return;
        }

        if (update_lastName.isEmpty()) {
            et_updateLastName.setError("Field required");
            et_updateLastName.requestFocus();
            return;
        }
        if (update_contactNumber.isEmpty()) {
            et_updateContactNumber.setError("Field required");
            et_updateContactNumber.requestFocus();
            return;
        }
        if (update_dob.isEmpty()) {
            et_updateDob.setError("Task required");
            et_updateDob.requestFocus();
            return;
        }

        if (update_contactNumber.length()<10) {
            et_updateContactNumber.setError("Contain 10 digits");
            et_updateContactNumber.requestFocus();
            return;
        }


        class UpdateEmployee extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                employee.setFirstName(update_firstName);
                employee.setLastName(update_lastName);
                employee.setPhoneNumber(update_contactNumber);
                employee.setDob(update_dob);
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .getEmployeeDao()
                        .update(employee);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(UpdateEmployeeDetails.this, EmployeeHome.class));
            }
        }

        UpdateEmployee updateEmployee = new UpdateEmployee();
        updateEmployee.execute();
    }
    private void deleteEmployee(final Employee employee) {
        class DeleteEmployee extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .getEmployeeDao()
                        .delete(employee);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(UpdateEmployeeDetails.this, EmployeeHome.class));
            }
        }

        DeleteEmployee deleteEmployee = new DeleteEmployee();
        deleteEmployee.execute();

    }

}