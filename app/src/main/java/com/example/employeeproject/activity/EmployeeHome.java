package com.example.employeeproject.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.employeeproject.R;
import com.example.employeeproject.adapter.EmployeeAdapter;
import com.example.employeeproject.data.DatabaseClient;
import com.example.employeeproject.data.EmployeeDao;
import com.example.employeeproject.data.EmployeeDatabase;
import com.example.employeeproject.model.Employee;

import java.util.List;


public class EmployeeHome extends AppCompatActivity {
    private Employee employee;
    private RecyclerView recyclerview_employeeDetails;
    Button btn_addEmployee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home);
        recyclerview_employeeDetails = findViewById(R.id.recyclerview_employeeDetails);
        recyclerview_employeeDetails.setLayoutManager(new LinearLayoutManager(this));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btn_addEmployee=(Button)findViewById(R.id.btn_addEmployee);
        btn_addEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployeeHome.this, MainActivity.class);
                startActivity(intent);
            }
        });

        getEmployee();

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
                EmployeeAdapter adapter = new EmployeeAdapter(EmployeeHome.this, employees);
                recyclerview_employeeDetails.setAdapter(adapter);
            }
        }
        GetEmployee gt = new GetEmployee();
        gt.execute();
    }
}