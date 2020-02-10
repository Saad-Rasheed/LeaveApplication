package com.agp.leaveapplication.Admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.agp.leaveapplication.Admin.ApprovalSection.Fragment_AdminPanel;
import com.agp.leaveapplication.ChangePasswordFragment;
import com.agp.leaveapplication.EmployeeHomeActivity;
import com.agp.leaveapplication.ShortLeaveRequestFragment;
import com.agp.leaveapplication.EmployeeDetailFragment;
import com.agp.leaveapplication.EmployeeLeave;
import com.agp.leaveapplication.LeaveStatus.LeaveStatusEmployee;
import com.agp.leaveapplication.LoginActivity;
import com.agp.leaveapplication.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Admin_HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    NavigationView navigationView;
    Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        sharedPreferences = getSharedPreferences("MyPre", MODE_PRIVATE);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView drawerEmpCode = (TextView) headerView.findViewById(R.id.tv_drawer_EmpCode);
        TextView drawerUsername = (TextView) headerView.findViewById(R.id.tv_drawer_username);
        //Remove 0 in EmployeeCode at NavigationView
        drawerEmpCode.setText(sharedPreferences.getString("EmpCode", "").substring(1));
        //Remove . in EmployeeName at NavigationView
        String emp_name = sharedPreferences.getString("EmployeeName", "");
        if (emp_name.endsWith(".")) {
            drawerUsername.setText(emp_name.substring(0, emp_name.length() - 1));
        } else {
            drawerUsername.setText(sharedPreferences.getString("EmployeeName", ""));
        }
        //drawerUsername.setText(sharedPreferences.getString("EmployeeName",""));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new Fragment_AdminPanel();
        fragmentTransaction.replace(R.id.screen_area, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin__home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Admin_HomeActivity.this);
            builder.setTitle("Confirmation");
            builder.setMessage("Are you sure you want to logout?")
                    .setCancelable(false);
            builder.setIcon(R.drawable.ic_warning);
            builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editor = sharedPreferences.edit();
                    editor.putBoolean("saveLogin", false);
                    editor.clear();
                    editor.apply();
                    finishAffinity();
                    Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Admin_HomeActivity.this, LoginActivity.class));
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }

    /*Dialog function is used for the About Page*/
    public void Dialog() throws PackageManager.NameNotFoundException {

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage("Version name: " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName + "\r\n" +
                        "For any queries email support at saad.rasheed@agp.com.pk")
                .setIcon(R.drawable.icon)
                .setPositiveButton("OK", null)
                .show();

        Button btn_ok = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationView.getMenu().getItem(0).setChecked(true);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (checkNavigationMenuItem() != 0) {
                navigationView.setCheckedItem(R.id.nav_home);
                fragment = new Fragment_AdminPanel();
                getSupportFragmentManager().beginTransaction().replace(R.id.screen_area, fragment).commit();
            } else
                super.onBackPressed();
        }
    }

    private int checkNavigationMenuItem() {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i).isChecked())
                return i;
        }
        return -1;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            fragment = new Fragment_AdminPanel();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.screen_area, fragment).commit();

        } else if (id == R.id.nav_LeaveRequest) {
            fragment = new EmployeeLeave();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.screen_area, fragment).commit();

        } else if (id == R.id.nav_ShortLeaveRequest) {
            fragment = new ShortLeaveRequestFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.screen_area, fragment).commit();

        } else if (id == R.id.nav_LeaveApproval) {
            fragment = new LeaveStatusEmployee();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.screen_area, fragment).commit();

        } else if (id == R.id.nav_employeedetails) {
            fragment = new EmployeeDetailFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.screen_area, fragment).commit();

        } else if (id == R.id.nav_ChangePassword) {
            fragment = new ChangePasswordFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.screen_area, fragment).commit();

        } else if (id == R.id.nav_about) {
            try {
                Dialog();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        } else if (id == R.id.nav_signout) {

            AlertDialog.Builder builder = new AlertDialog.Builder(Admin_HomeActivity.this);
            builder.setTitle("Confirmation");
            builder.setMessage("Are you sure you want to logout?")
                    .setCancelable(false);
            builder.setIcon(R.drawable.ic_warning);
            builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editor = sharedPreferences.edit();
                    editor.putBoolean("saveLogin", false);
                    editor.clear();
                    editor.apply();
                    finishAffinity();
                    Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Admin_HomeActivity.this, LoginActivity.class));
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
