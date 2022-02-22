package com.example.startuplogin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TableAdapter extends RecyclerView.Adapter<TableViewHolder> {

    Context context;
    static ArrayList<Table> tableList;
    static Table table;
    static boolean check;
    DatabaseReference tableRef = FirebaseDatabase.getInstance().getReference("Restaurant");
    public static String[] tableStatusOption;

    public TableAdapter(Context context, ArrayList<Table> tableList) {
        this.context = context;
        this.tableList = tableList;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_layout, parent, false);
        TableViewHolder viewHolder = new TableViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, final int position) {
        check = true;
        tableStatusOption = new String[]{"Free", "Reserved", "Unavailable"};
        ArrayAdapter<String> tableStatusAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, tableStatusOption);
        tableStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.tableStatusSp.setAdapter(tableStatusAdapter);

        table = tableList.get(position);

      //  holder.tableStatusSp.setSelection(0, true);
        if (table != null) {
            holder.tableNumTv.setText(table.getTableName());
            holder.tableSeatTv.setText(String.valueOf(table.getTableSeat()));
            String status = table.getTableStatus();
            if (status.equals("Free"))
                TableViewHolder.tableStatusSp.setSelection(0);
            else {
                TableViewHolder.tableStatusSp.setSelection(1);
                holder.tableStatusSp.setSelected(false);
                holder.tableStatusSp.setSelection(1, true);
                holder.tableStatusSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String statusName = tableStatusOption[i];
                        Log.e("status ", statusName);
                        showDialog(i, statusName);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        }



        holder.tableDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(position, "Delete");
            }
        });

        holder.tableOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent orderIntent = new Intent(context, OrderActivity.class);
                orderIntent.putExtra("tableBundle", table);
                context.startActivity(orderIntent);
            }
        });
    }


    private void showDialog(final int position, final String option) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (option.equals("Delete")) {
            builder.setMessage("Are you sure,you want to Delete restaurant");
        } else if (option.equals("Reserved")) {
            builder.setMessage("Are you sure,you want to Free this Table");
        }


        builder.setTitle("Warning");
        builder.setIcon(R.drawable.ic_warning);
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (option.equals("Delete")) {
                    deleteRest(position);
                } else if (option.equals("Reserved")) {
                    updateStatus(position, option);
                }

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void updateStatus(int position, String option) {

        table = tableList.get(position);
        tableRef.child(RestaurantList.restId).child("Table").child(table.getTableId()).child("tableStatus").setValue(option).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Status Updated", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void deleteRest(int position) {

        table = tableList.get(position);
        tableList.remove(position);
        tableRef.child(RestaurantList.restId).child("Table").child(table.getTableId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Table Deleted", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        notifyItemRemoved(position);
        notifyDataSetChanged();
        notifyItemRangeChanged(position, tableList.size());

    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }
}
