package com.example.employeeproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.employeeproject.R;
import com.example.employeeproject.activity.UpdateEmployeeDetails;
import com.example.employeeproject.model.Employee;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private Context mCtx;
    private List<Employee> employeeList;

    public EmployeeAdapter(Context mCtx, List<Employee> employeeList) {
        this.mCtx = mCtx;
        this.employeeList = employeeList;
    }

    @Override
    public EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.employees_recyclerview, parent, false);
        return new EmployeeViewHolder(view);
    }


    @Override
    public void onBindViewHolder(EmployeeViewHolder holder, int position) {
        Employee employee = employeeList.get(position);
//        holder.textViewTask.setText(t.getTask());
//        holder.textViewDesc.setText(t.getDesc());
//        holder.textViewFinishBy.setText(t.getFinishBy());
        holder.tv_rec_firstName.setText(employee.getFirstName());
        holder.tv_rec_lastName.setText(employee.getLastName());
        holder.tv_rec_phoneNumber.setText(employee.getPhoneNumber());
        holder.tv_rec_dob.setText(employee.getDob());
        holder.tv_rec_gender.setText(employee.getGender());

    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    class EmployeeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_rec_firstName, tv_rec_lastName, tv_rec_phoneNumber, tv_rec_dob,tv_rec_gender;

        public EmployeeViewHolder(View itemView) {
            super(itemView);

            tv_rec_firstName = itemView.findViewById(R.id.tv_rec_firstName);
            tv_rec_lastName = itemView.findViewById(R.id.tv_rec_lastName);
            tv_rec_phoneNumber = itemView.findViewById(R.id.tv_rec_phoneNumber);
            tv_rec_dob = itemView.findViewById(R.id.tv_rec_dob);
            tv_rec_gender = itemView.findViewById(R.id.tv_rec_gender);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Employee empDetails = employeeList.get(getAdapterPosition());

            Intent intent = new Intent(mCtx, UpdateEmployeeDetails.class);
            intent.putExtra("employeeDetails", empDetails);

            mCtx.startActivity(intent);
        }
    }

}
